package com.mwguy.vgit.components.git;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwguy.vgit.utils.Processes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitLog {
    private final Git git;
    private final String repository;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GitUserInfo {
        private String name;
        private String email;
        private Integer date;
    }

    @Getter
    @Setter
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

    public List<GitCommit> parse(Integer skip, Integer limit) throws IOException, InterruptedException {
        ArrayList<String> command = new ArrayList<>();

        command.add("git");
        command.add("log");
        command.add("--skip");
        command.add(skip.toString());
        command.add("-" + limit.toString());
        command.add("--pretty=format:{^^^^abbreviatedCommitHash^^^^: ^^^^%h^^^^, ^^^^abbreviatedTreeHash^^^^: ^^^^%t^^^^, ^^^^author^^^^: {^^^^name^^^^: ^^^^%an^^^^, ^^^^email^^^^: ^^^^%ae^^^^, ^^^^date^^^^: %at}, ^^^^committer^^^^: {^^^^name^^^^: ^^^^%cn^^^^, ^^^^email^^^^: ^^^^%ce^^^^, ^^^^date^^^^: %ct}, ^^^^subject^^^^: ^^^^%s^^^^, ^^^^body^^^^: ^^^^%b^^^^},^&^&^&");

        Process process = Processes.startGitProcess(command, git.getBaseDirectory().resolve(repository).toFile());
        process.waitFor();

        String output = new String(process.getInputStream().readAllBytes())
                .replace("\r", "\\\\r")
                .replace("\n", "\\\\n")
                .replace("^&^&^&\\\\n", "\n")
                .replace("\"", "\\\"")
                .replace("^^^^", "\"");

        output = output.substring(0, output.length() - 7);
        output = String.format("[%s]", output);

        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(output, GitCommit[].class));
    }
}
