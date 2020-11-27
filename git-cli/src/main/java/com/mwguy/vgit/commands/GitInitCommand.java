package com.mwguy.vgit.commands;

import com.mwguy.vgit.data.GitRepository;
import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Builder
public class GitInitCommand implements GitCommand<Void> {
    @Builder.Default
    private final Boolean bare = true;
    private final GitRepository repository;

    @Override
    public Void call() throws GitException {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("init");
        if (bare) {
            command.add("--bare");
        }

        try {
            File processDirectory = repository.getPath().toAbsolutePath().toFile();
            if (!processDirectory.isDirectory()) {
                processDirectory.mkdirs();
            }

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(processDirectory);
            repository.getEnvironmentResolver().resolve().forEach(processBuilder.environment()::put);

            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new GitException(e.getMessage(), e);
        }

        return null;
    }
}
