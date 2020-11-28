package com.mwguy.vgit.commands;

import com.mwguy.vgit.data.GitRepository;
import com.mwguy.vgit.data.GitTreeEntry;
import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

@Builder
public class GitLsTreeCommand implements GitCommand<List<GitTreeEntry>> {
    @Builder.Default private final String object = null;
    @Builder.Default private final String path = null;
    private final GitRepository repository;

    @Override
    public List<GitTreeEntry> call() throws GitException {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("ls-tree");
        command.add("-l");
        command.add(object != null ? object : "HEAD");
        if (path != null) {
            command.add("--");
            command.add(path);
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(repository.getPath().toAbsolutePath().toFile());
            repository.getEnvironmentResolver().resolve().forEach(builder.environment()::put);
            Process process = builder.start();
            process.waitFor();

            List<GitTreeEntry> trees = new ArrayList<>();
            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                GitTreeEntry tree = new GitTreeEntry();
                String line = scanner.nextLine();
                String[] metaInfoWithName = line.split("\t");

                // 100644 blob c2065bc26202b2d072aca3efc3d1c2efad3afcbf
                String size = metaInfoWithName[0].substring(52).trim();
                if ("-".equals(size)) {
                    tree.setSize(0);
                } else {
                    tree.setSize(Integer.parseInt(size));
                }

                tree.setObject(metaInfoWithName[0].substring(12, 52));
                tree.setMode(Integer.parseInt(metaInfoWithName[0].substring(0, 6)));
                tree.setType(GitTreeEntry.GitObjectType.valueOf(metaInfoWithName[0].substring(7, 11).toUpperCase()));
                tree.setName(metaInfoWithName[1]);

                trees.add(tree);
            }

            trees.sort(Comparator.comparingInt(entry -> entry.getType().ordinal()));
            return trees;
        } catch (InterruptedException | IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
