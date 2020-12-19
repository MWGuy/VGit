package com.mwguy.vgit.service;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.VGitApplication;
import com.mwguy.vgit.ci.dsl.CI;
import com.mwguy.vgit.ci.dsl.Job;
import com.mwguy.vgit.ci.dsl.Pipeline;
import com.mwguy.vgit.ci.dsl.Stage;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.data.GitTreeEntry;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CIService {
    private final HooksService hooksService;
    private final Git git;

    public CIService(HooksService hooksService, Git git) {
        this.hooksService = hooksService;
        this.git = git;
    }

    public Optional<InputStream> findPipelineScript(String tree, RepositoryDao repositoryDao) throws IOException {
        List<GitTreeEntry> treeEntries = repositoryDao.getTree(RepositoryDao.GitTreeInput.builder()
                .object(tree)
                .build());

        for (GitTreeEntry entry: treeEntries) {
            if (".pipeline.groovy".equals(entry.getName())) {
                return Optional.of(repositoryDao.getBlobAsInputStream(entry.getObject()));
            }
        }

        return Optional.empty();
    }

    public void runGroovyPipeline(RepositoryDao repositoryDao, InputStream inputStream) throws IOException, InterruptedException {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass(DelegatingScript.class.getName());
        GroovyShell sh = new GroovyShell(VGitApplication.class.getClassLoader(), new Binding(), compilerConfiguration);
        DelegatingScript script = (DelegatingScript) sh.parse(new InputStreamReader(inputStream), ".pipeline.groovy");

        CI dsl = new CI();
        script.setDelegate(dsl);
        script.run();

        if (dsl.pipeline == null) {
            log.warn("File '.pipeline.groovy' found, but pipeline can`t set");
            return;
        }

        if (dsl.pipeline.stages.isEmpty()) {
            log.warn("File '.pipeline.groovy' parsed, but stages can`t set");
            return;
        }

        runPipeline(repositoryDao, dsl.pipeline);
    }

    public void runPipeline(RepositoryDao repositoryDao, Pipeline pipeline) throws IOException, InterruptedException {
        Path directory = Files.createTempDirectory("vgit-ci");
        String repository = String.format("%s/%s",
                repositoryDao.getPath().getNamespace(),
                repositoryDao.getPath().getName()
        );

        git.clone()
                .from(repositoryDao.toGitRepository().getPath().toString())
                .to(directory)
                .bare(false)
                .build()
                .call();

        long startTimeMillis = System.currentTimeMillis();
        for (Map.Entry<String, Stage> stageEntry: pipeline.stages.entrySet()) {
            log.info("Staring stage '{}' ...", stageEntry.getKey());
            // TODO: make jobs async
            for (Map.Entry<String, Job> jobEntry: stageEntry.getValue().jobs.entrySet()){
                log.info("Staring job '{}' ...", jobEntry.getKey());
                List<String> commands = jobEntry.getValue().commands;
                for (String command: commands) {
                    log.info("Staring process '{}' ...", command);
                    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                    processBuilder.directory(directory.toFile());
                    processBuilder.inheritIO();
                    pipeline.environment.forEach(processBuilder.environment()::put);

                    int exitCode = processBuilder.start().waitFor();
                    if (exitCode != 0) {
                        log.warn("Pipeline failed, sending web hook....");

                        PipelineFailedPayload payload = PipelineFailedPayload.builder()
                                .repository(repository)
                                .stage(stageEntry.getKey())
                                .job(jobEntry.getKey())
                                .command(command)
                                .exitCode(exitCode)
                                .durationMillis(System.currentTimeMillis() - startTimeMillis)
                                .build();

                        hooksService.asyncTriggerWebHooks(repositoryDao,
                                RepositoryDao.RepositoryHookType.PIPELINE_FAILED,
                                payload);
                        return;
                    }
                }
            }
        }

        log.info("Pipeline passed, sending web hook....");
        PipelinePassedPayload payload = PipelinePassedPayload.builder()
                .repository(repository)
                .durationMillis(System.currentTimeMillis() - startTimeMillis)
                .build();

        hooksService.asyncTriggerWebHooks(repositoryDao,
                RepositoryDao.RepositoryHookType.PIPELINE_PASSED,
                payload);

        FileUtils.deleteDirectory(directory.toFile());
    }

    @Data
    @Builder
    public static class PipelineFailedPayload {
        private final String repository;
        private final Long durationMillis;
        private final String stage;
        private final String job;
        private final String command;
        private final Integer exitCode;
    }

    @Data
    @Builder
    public static class PipelinePassedPayload {
        private final String repository;
        private final Long durationMillis;
    }
}
