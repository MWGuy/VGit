package com.mwguy.vgit.controllers;

import com.mwguy.vgit.components.git.Git;
import com.mwguy.vgit.components.git.GitLog;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.service.GitService;
import com.mwguy.vgit.service.HooksService;
import com.mwguy.vgit.service.RepositoriesService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class GitController {
    private final Git git;
    private final GitService gitService;
    private final RepositoriesService repositoriesService;
    private final HooksService hooksService;
    private final TaskExecutor taskExecutor;

    public GitController(Git git, GitService gitService, RepositoriesService repositoriesService, HooksService hooksService, TaskExecutor taskExecutor) {
        this.git = git;
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
    ) throws IOException {
        Git.GitPackType packType = Git.GitPackType.of(service);
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, packType.getPermissionType());

        response.setHeader("Content-Type", packType.getMediaType().toString());
        gitService.infoRefs(response.getOutputStream(), packType, repositoryDao.toRepositoryPath());
    }

    @PostMapping("/{namespace}/{path}.git/git-upload-pack")
    public void uploadPack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.GIT_PULL);

        response.setHeader("Content-Type", Git.GitPackType.UPLOAD_PACK.getMediaType().toString());
        gitService.uploadPack(response.getOutputStream(), request.getInputStream(), repositoryDao.toRepositoryPath());
    }

    @PostMapping("/{namespace}/{path}.git/git-receive-pack")
    public void receivePack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.GIT_PUSH);

        response.setHeader("Content-Type", Git.GitPackType.RECEIVE_PACK.getMediaType().toString());
        gitService.receivePack(response.getOutputStream(), request.getInputStream(), repositoryDao.toRepositoryPath());
    }

    @PostMapping("/{namespace}/{path}.git/hook/{type}")
    public void hookType(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            @PathVariable("type") String type,
            HttpEntity<String> httpEntity
    ) throws IOException, InterruptedException {
        RepositoryDao repositoryDao = repositoriesService
                .findRepositoryAndCheckPermissions(namespace, path, RepositoryDao.PermissionType.HOOK_TRIGGER);

        if (!hooksService.hasHooks(repositoryDao, RepositoryDao.RepositoryHookType.PUSH)) {
            return;
        }

        taskExecutor.execute(() -> {
            String[] input = Objects.requireNonNull(httpEntity.getBody()).trim().split(" ");
            String oldTree = input[0];
            String newTree = input[1];
            String branch = input[2];

            try {
                List<GitLog.GitCommit> commits = git
                        .log(repositoryDao.toRepositoryPath())
                        .oldTree(oldTree)
                        .newTree(newTree)
                        .parse(0, 0);

                hooksService.triggerHook(repositoryDao, RepositoryDao.RepositoryHookType.PUSH, commits);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
