package com.mwguy.vgit.commands;

import com.google.gson.Gson;
import com.mwguy.vgit.data.GitCommit;
import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
public class GitLogCommand implements GitCommand<List<GitCommit>> {
    private static final Gson gson = new Gson();

    @Builder.Default private final Integer maxCount = 0;
    @Builder.Default private final Integer skip = 0;
    @Builder.Default private final String path = null;
    @Builder.Default private final String branch = null;
    @Builder.Default private final String oldTree = null;
    private final String newTree;
    private final Path repository;

    @Override
    public List<GitCommit> call() throws GitException {
        ArrayList<String> command = new ArrayList<>();

        command.add("git");
        command.add("log");

        if (skip != 0) {
            command.add("--skip");
            command.add(skip.toString());
        }

        if (maxCount != 0) {
            command.add("--max-count=" + maxCount.toString());
        }

        if (oldTree != null && newTree != null) {
            if ("0000000000000000000000000000000000000000".equals(oldTree)) {
                command.add(newTree);
            } else {
                command.add(String.format("%s..%s", oldTree, newTree));
            }
        }

        if (branch != null) {
            command.add("--branches=" + branch);
        }

        command.add("--pretty=format:{^^^^refs^^^^: { ^^^^commit^^^^: ^^^^%h^^^^, ^^^^tree^^^^: ^^^^%t^^^^ }, ^^^^abbreviatedTreeHash^^^^: , ^^^^author^^^^: {^^^^name^^^^: ^^^^%an^^^^, ^^^^email^^^^: ^^^^%ae^^^^, ^^^^date^^^^: %at}, ^^^^committer^^^^: {^^^^name^^^^: ^^^^%cn^^^^, ^^^^email^^^^: ^^^^%ce^^^^, ^^^^date^^^^: %ct}, ^^^^subject^^^^: ^^^^%s^^^^, ^^^^body^^^^: ^^^^%b^^^^},^&^&^&");

        if (path != null) {
            command.add("--");
            command.add(path);
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(repository.toAbsolutePath().toFile());
            Process process = builder.start();

            process.waitFor();

            String output = new String(process.getInputStream().readAllBytes())
                    .replace("\r", "\\\\r")
                    .replace("\n", "\\\\n")
                    .replace("^&^&^&\\\\n", "\n")
                    .replace("\"", "\\\"")
                    .replace("^^^^", "\"");

            if (output.length() != 0) {
                output = output.substring(0, output.length() - 7);
            }

            output = String.format("[%s]", output);
            return Arrays.asList(gson.fromJson(output, GitCommit[].class));
        } catch (IOException | InterruptedException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
