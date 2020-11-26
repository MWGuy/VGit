package com.mwguy.vgit.commands;

import com.mwguy.vgit.data.GitVersion;
import com.mwguy.vgit.exceptions.GitException;

import java.io.IOException;

public class GitVersionCommand implements GitCommand<GitVersion> {
    private static GitVersion cache;

    @Override
    public GitVersion call() throws GitException {
        if (cache != null) {
            return cache;
        }

        ProcessBuilder builder = new ProcessBuilder("git", "version");
        try {
            Process process = builder.start();
            if (process.waitFor() != 0) {
                throw new GitException("Git process exited with non-zero exit code");
            }

            String rawOutput = new String(process.getInputStream().readAllBytes());
            return cache = GitVersion.parseVersionFromRawOutput(rawOutput);
        } catch (IOException | InterruptedException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
