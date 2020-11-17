package com.mwguy.vgit.utils;

import com.mwguy.vgit.exceptions.GitException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Processes {
    public static Process startGitProcess(String ...command) throws GitException {
        return Processes.startGitProcess(Arrays.asList(command));
    }

    public static Process startGitProcess(List<String> command) throws GitException {
        log.info(String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new GitException("Unable to start new git process", e);
        }
    }
}
