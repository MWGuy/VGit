package com.mwguy.vgit.utils;

import com.mwguy.vgit.exceptions.GitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Processes {
    public static Process startGitProcess(String... command) throws GitException {
        return Processes.startGitProcess(Arrays.asList(command), null);
    }

    public static Process startGitProcess(List<String> command, @Nullable File directory) throws GitException {
        log.info(String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (directory != null) {
            processBuilder.directory(directory);
        }

        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new GitException("Unable to start new git process", e);
        }
    }
}
