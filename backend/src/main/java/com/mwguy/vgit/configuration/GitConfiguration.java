package com.mwguy.vgit.configuration;

import com.mwguy.vgit.Git;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.util.UUID;

@Configuration
public class GitConfiguration {
    public static final String gitHookSecretKey = UUID.randomUUID().toString().replace("-", "");
    public static Path gitBaseDirectory = null;
    private final Environment environment;

    public GitConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Scope("singleton")
    public Git gitBean() {
        if (GitConfiguration.gitBaseDirectory == null) {
            String baseDirectory = environment.getProperty("GIT_BASE_DIRECTORY");
            if (baseDirectory == null) {
                throw new RuntimeException("Environment variable 'GIT_BASE_DIRECTORY' not set");
            }

            gitBaseDirectory = Path.of(baseDirectory);
        }

        return new Git();
    }

    public static Path resolveGitPath(String path) {
        return gitBaseDirectory.resolve(path);
    }
}
