package com.mwguy.vgit.controllers;

import com.mwguy.vgit.components.git.Git;
import com.mwguy.vgit.utils.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

@Slf4j
@RestController
public class GitController {
    private final Git git;

    public GitController(Git git) {
        this.git = git;
    }

    @GetMapping("/{namespace}/{path}.git/info/refs")
    public void infoRefs(
            @RequestParam("service") String service,
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletResponse response
    ) throws IOException, InterruptedException {
        Git.GitPackType packType = Git.GitPackType.of(service);
        OutputStream outputStream = response.getOutputStream();
        outputStream.write((packType.getMagic() + " service=" + packType.getName() + "\n0000").getBytes());

        Process process = git.statelessRpc()
                .advertiseRefs()
                .packType(packType)
                .repository(namespace + "/" + path)
                .build();
        process.waitFor();
        IOUtils.copy(process.getInputStream(), outputStream);
        response.setHeader("Content-Type", packType.getMediaType().toString());
    }

    @PostMapping("/{namespace}/{path}.git/git-upload-pack")
    public void uploadPack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, InterruptedException {
        Process process = git.statelessRpc()
                .packType(Git.GitPackType.UPLOAD_PACK)
                .repository(namespace + "/" + path)
                .inputStream(request.getInputStream())
                .build();
        IOUtils.copy(process.getInputStream(), response.getOutputStream());
        response.setHeader("Content-Type", Git.GitPackType.UPLOAD_PACK.getMediaType().toString());
    }

    @PostMapping("/{namespace}/{path}.git/git-receive-pack")
    public void receivePack(
            @PathVariable("namespace") String namespace,
            @PathVariable("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, InterruptedException {
        Process process = git.statelessRpc()
                .packType(Git.GitPackType.RECEIVE_PACK)
                .repository(namespace + "/" + path)
                .inputStream(request.getInputStream())
                .build();
        process.waitFor();

        InputStreamReader reader = new InputStreamReader(process.getInputStream());
        System.out.println(reader);

        IOUtils.copy(process.getInputStream(), response.getOutputStream());
        response.setHeader("Content-Type", Git.GitPackType.RECEIVE_PACK.getMediaType().toString());
    }
}
