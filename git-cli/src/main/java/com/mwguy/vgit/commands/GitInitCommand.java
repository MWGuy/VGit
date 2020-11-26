package com.mwguy.vgit.commands;

import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Builder
public class GitInitCommand implements GitCommand<Void> {
    @Builder.Default private final Boolean bare = true;
    private final Path repository;

    @Override
    public Void call() throws GitException {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("init");
        if (bare) {
            command.add("--bare");
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(repository.toAbsolutePath().toFile());
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new GitException(e.getMessage(), e);
        }

        return null;
    }
}
