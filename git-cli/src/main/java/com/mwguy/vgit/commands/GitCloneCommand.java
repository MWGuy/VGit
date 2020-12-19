package com.mwguy.vgit.commands;

import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Builder
public class GitCloneCommand implements GitCommand<Void> {
    @Builder.Default
    private final Boolean bare = false;
    private final String from;
    private final Path to;

    @Override
    public Void call() throws GitException {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("clone");
        if (bare) {
            command.add("--bare");
        }

        command.add(from);

        try {
            File directory = to.toAbsolutePath().toFile();
            if (!directory.isDirectory()) {
                directory.mkdirs();
            }

            command.add(directory.toString());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new GitException(e.getMessage(), e);
        }

        return null;
    }
}
