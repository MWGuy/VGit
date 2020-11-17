package com.mwguy.vgit.configuration;

import com.mwguy.vgit.components.git.Git;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.nio.file.Path;

@Configuration
public class GitConfiguration {
    private final Environment environment;

    public GitConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Scope("singleton")
    public Git gitBean() {
        String gitBaseDirectory = environment.getProperty("GIT_BASE_DIRECTORY");
        if (gitBaseDirectory == null) {
            throw new RuntimeException("Environment variable 'GIT_BASE_DIRECTORY' not set");
        }

        return new Git(Path.of(gitBaseDirectory));
    }
}
