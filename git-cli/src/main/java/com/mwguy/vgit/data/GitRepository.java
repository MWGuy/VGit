package com.mwguy.vgit.data;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@Builder
public class GitRepository {
    @Builder.Default
    private final GitRepositoryEnvironmentResolver environmentResolver = GitRepositoryEnvironmentResolver.EMPTY;
    private final Path path;
}
