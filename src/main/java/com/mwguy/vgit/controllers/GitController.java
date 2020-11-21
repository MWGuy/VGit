package com.mwguy.vgit.controllers;

import com.mwguy.vgit.components.git.Git;
import com.mwguy.vgit.service.GitService;
import com.mwguy.vgit.utils.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class GitController {
    private final GitService gitService;

    public GitController(GitService gitService) {
        this.gitService = gitService;
    }

    @GetMapping("/{namespace}/{path}.git/info/refs")
    public void infoRefs(
            @RequestParam("service") String service,
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletResponse response
    ) throws IOException {
        Authorization.getCurrentUser(false);
        Git.GitPackType packType = Git.GitPackType.of(service);
        response.setHeader("Content-Type", packType.getMediaType().toString());
        gitService.infoRefs(response.getOutputStream(), packType, namespace + "/" + path);
    }

    @PostMapping("/{namespace}/{path}.git/git-upload-pack")
    public void uploadPack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        Authorization.getCurrentUser(false);
        response.setHeader("Content-Type", Git.GitPackType.UPLOAD_PACK.getMediaType().toString());
        gitService.uploadPack(response.getOutputStream(), request.getInputStream(), namespace + "/" + path);
    }

    @PostMapping("/{namespace}/{path}.git/git-receive-pack")
    public void receivePack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        Authorization.getCurrentUser(false);
        response.setHeader("Content-Type", Git.GitPackType.RECEIVE_PACK.getMediaType().toString());
        gitService.receivePack(response.getOutputStream(), request.getInputStream(), namespace + "/" + path);
    }
}
