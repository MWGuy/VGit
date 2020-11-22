package com.mwguy.vgit.components.git;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwguy.vgit.utils.Processes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    }

    protected GitLog(Git git, String repository) {
        this.git = git;
        this.repository = repository;
    }

    public List<GitCommit> parse(Integer skip, Integer limit) throws IOException {
        ArrayList<String> command = new ArrayList<>();
        ArrayList<GitCommit> commits = new ArrayList<>();

        command.add("git");
        command.add("log");
        command.add("--skip");
        command.add(skip.toString());
        command.add("-" + limit.toString());
        command.add("--pretty=format:{^^^^abbreviatedCommitHash^^^^: ^^^^%h^^^^, ^^^^abbreviatedTreeHash^^^^: ^^^^%t^^^^, ^^^^author^^^^: {^^^^name^^^^: ^^^^%an^^^^, ^^^^email^^^^: ^^^^%ae^^^^, ^^^^date^^^^: %at}, ^^^^committer^^^^: {^^^^name^^^^: ^^^^%cn^^^^, ^^^^email^^^^: ^^^^%ce^^^^, ^^^^date^^^^: %ct}, ^^^^subject^^^^: ^^^^%s^^^^}");

        Process process = Processes.startGitProcess(command, git.getBaseDirectory().resolve(repository).toFile());
        Scanner scanner = new Scanner(process.getInputStream());

        ObjectMapper objectMapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            commits.add(objectMapper.readValue(scanner.nextLine()
                    .replace("\n", "\\n")
                    .replace("\"", "\\\"")
                    .replace("^^^^", "\""), GitCommit.class));
        }

        return commits;
    }
}
