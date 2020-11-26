package com.mwguy.vgit.commands;

import com.mwguy.vgit.data.GitPackType;
import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Builder
public class GitStatelessRpcCommand implements GitCommand<InputStream> {
    private final Path repository;
    private final GitPackType packType;
    @Builder.Default private final Boolean advertiseRefs = false;
    @Builder.Default private final InputStream inputStream = null;

    @Override
    public InputStream call() throws GitException {
        List<String> command = new ArrayList<>();
        command.add(packType.getName());
        command.add("--stateless-rpc");

        if (advertiseRefs) {
            command.add("--advertise-refs");
        }

        command.add(repository.toAbsolutePath().toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            if (this.inputStream != null) {
                process.getOutputStream().write(this.inputStream.readAllBytes());
                process.getOutputStream().flush();
            }

            return process.getInputStream();
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
