package com.mwguy.vgit.components.git;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwguy.vgit.utils.Processes;
import lombok.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitLog {
    private final Git git;
    private final String repository;

    private String oldTree;
    private String newTree;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GitUserInfo {
        private String name;
        private String email;
        private Integer date;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitCommit {
        private String abbreviatedCommitHash;
        private String abbreviatedTreeHash;
        private GitUserInfo author;
        private GitUserInfo committer;
        private String subject;
        private String body;
    }

    protected GitLog(Git git, String repository) {
        this.git = git;
        this.repository = repository;
    }


    public GitLog oldTree(String oldTree) {
        this.oldTree = oldTree;
        return this;
    }

    public GitLog newTree(String newTree) {
        this.newTree = newTree;
        return this;
    }

    public List<GitCommit> parse(Integer skip, Integer limit) throws IOException, InterruptedException {
        ArrayList<String> command = new ArrayList<>();

        command.add("git");
        command.add("log");

        if (skip != 0) {
            command.add("--skip");
            command.add(skip.toString());
        }

        if (limit != 0) {
            command.add("-" + limit.toString());
        }

        if (oldTree != null && newTree != null) {
            if ("0000000000000000000000000000000000000000".equals(oldTree)) {
                command.add(newTree);
            } else {
                command.add(String.format("%s..%s", oldTree, newTree));
            }
        }

        command.add("--pretty=format:{^^^^abbreviatedCommitHash^^^^: ^^^^%h^^^^, ^^^^abbreviatedTreeHash^^^^: ^^^^%t^^^^, ^^^^author^^^^: {^^^^name^^^^: ^^^^%an^^^^, ^^^^email^^^^: ^^^^%ae^^^^, ^^^^date^^^^: %at}, ^^^^committer^^^^: {^^^^name^^^^: ^^^^%cn^^^^, ^^^^email^^^^: ^^^^%ce^^^^, ^^^^date^^^^: %ct}, ^^^^subject^^^^: ^^^^%s^^^^, ^^^^body^^^^: ^^^^%b^^^^},^&^&^&");

        Process process = Processes.startGitProcess(command, git.getBaseDirectory().resolve(repository).toFile(), null);
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

        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(output, GitCommit[].class));
    }
}
