package com.mwguy.vgit.controllers;

import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.data.GitCommit;
import com.mwguy.vgit.data.GitPackType;
import com.mwguy.vgit.exceptions.GitException;
import com.mwguy.vgit.service.CIService;
import com.mwguy.vgit.service.GitService;
import com.mwguy.vgit.service.HooksService;
import com.mwguy.vgit.service.RepositoriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
public class GitController {
    private final CIService ciService;
    private final GitService gitService;
    private final RepositoriesService repositoriesService;
    private final HooksService hooksService;
    private final TaskExecutor taskExecutor;

    public GitController(CIService ciService, GitService gitService, RepositoriesService repositoriesService, HooksService hooksService, TaskExecutor taskExecutor) {
        this.ciService = ciService;
        this.gitService = gitService;
        this.repositoriesService = repositoriesService;
        this.hooksService = hooksService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping("/{namespace}/{path}.git/info/refs")
    public void infoRefs(
            @RequestParam("service") String service,
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletResponse response
    ) throws IOException, GitException {
        GitPackType packType = GitPackType.of(service);
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.getByPackType(packType));

        response.setHeader("Content-Type", packType.getContentType());
        gitService.infoRefs(response.getOutputStream(), packType, repositoryDao);
    }

    @PostMapping("/{namespace}/{path}.git/git-upload-pack")
    public void uploadPack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, GitException {
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.GIT_PULL);

        response.setHeader("Content-Type", GitPackType.UPLOAD_PACK.getContentType());
        gitService.uploadPack(response.getOutputStream(), request.getInputStream(), repositoryDao);
    }

    @PostMapping("/{namespace}/{path}.git/git-receive-pack")
    public void receivePack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, GitException {
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.GIT_PUSH);

        response.setHeader("Content-Type", GitPackType.RECEIVE_PACK.getContentType());
        gitService.receivePack(response.getOutputStream(), request.getInputStream(), repositoryDao);
    }

    @PostMapping("/{namespace}/{path}.git/hook/{type}")
    public void hookType(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            @PathVariable("type") String type,
            HttpEntity<String> httpEntity
    ) {
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.HOOK_TRIGGER);

        if (!"POST_RECEIVE".equals(type)) {
            return;
        }

        taskExecutor.execute(() -> {
            String[] input = Objects.requireNonNull(httpEntity.getBody()).trim().split(" ");
            String oldTree = input[0];
            String newTree = input[1];
            String branch = input[2];

            if (hooksService.hasHooks(repositoryDao, RepositoryDao.RepositoryHookType.GIT_PUSH)) {
                try {
                    List<GitCommit> commits = gitService.getGit()
                            .log()
                            .repository(repositoryDao.toGitRepository())
                            .branch(branch)
                            .oldTree(oldTree)
                            .newTree(newTree)
                            .build()
                            .call();

                    hooksService.asyncTriggerWebHooks(repositoryDao, RepositoryDao.RepositoryHookType.GIT_PUSH, commits);
                } catch (GitException e) {
                    e.printStackTrace();
                }
            }

            try {
                Optional<InputStream> pipelineInputStream = ciService.findPipelineScript(newTree, repositoryDao);
                if (pipelineInputStream.isEmpty()) {
                    return;
                }

                log.info("Found '.pipeline.groovy' file, executing ci...");
                ciService.runGroovyPipeline(repositoryDao, pipelineInputStream.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
