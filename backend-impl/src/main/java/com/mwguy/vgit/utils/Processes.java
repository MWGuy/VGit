package com.mwguy.vgit.utils;

import com.mwguy.vgit.VGitApplication;
import com.mwguy.vgit.configuration.GitConfiguration;
import com.mwguy.vgit.exceptions.GitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class Processes {
    public static Process startGitProcess(String... command) throws GitException {
        return Processes.startGitProcess(Arrays.asList(command), null, null);
    }

    public static Process startGitProcess(List<String> command, @Nullable File directory, @Nullable Map<String, String> env) throws GitException {
        log.debug(String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Map<String,String> environment = processBuilder.environment();
        environment.put("VGIT_SECRET", GitConfiguration.gitHookSecretKey);

        if (VGitApplication.context != null) {
            Environment springEnvironment = VGitApplication.context.getBean(Environment.class);
            environment.put("VGIT_PORT", springEnvironment.getProperty("local.server.port"));
        }

        if (env != null) {
            env.forEach(environment::put);
        }

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
