package com.mwguy.vgit.commands;

import com.mwguy.vgit.data.GitRepository;
import com.mwguy.vgit.data.GitTreeEntry;
import com.mwguy.vgit.exceptions.GitException;
import lombok.Builder;

import java.io.InputStream;

@Builder
public class GitCatFileCommand implements GitCommand<InputStream> {
    @Builder.Default
    private final GitTreeEntry.GitObjectType type = GitTreeEntry.GitObjectType.BLOB;
    private final GitRepository repository;
    private final String object;

    @Override
    public InputStream call() throws GitException {
        try {
            ProcessBuilder builder = new ProcessBuilder("git", "cat-file", type.name().toLowerCase(), object);
            builder.directory(repository.getPath().toAbsolutePath().toFile());
            repository.getEnvironmentResolver().resolve().forEach(builder.environment()::put);
            return builder.start().getInputStream();
        } catch (Exception e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
