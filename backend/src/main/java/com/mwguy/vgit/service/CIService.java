package com.mwguy.vgit.service;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.VGitApplication;
import com.mwguy.vgit.ci.PipelineRunner;
import com.mwguy.vgit.ci.dsl.CI;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.data.GitTreeEntry;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CIService {
    private final TaskExecutor taskExecutor;
    private final HooksService hooksService;
    private final Git git;

    public CIService(TaskExecutor taskExecutor, HooksService hooksService, Git git) {
        this.taskExecutor = taskExecutor;
        this.hooksService = hooksService;
        this.git = git;
    }

    public Optional<InputStream> findPipelineScript(String tree, RepositoryDao repositoryDao) throws IOException {
        List<GitTreeEntry> treeEntries = repositoryDao.getTree(RepositoryDao.GitTreeInput.builder()
                .object(tree)
                .build());

        for (GitTreeEntry entry : treeEntries) {
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

        PipelineRunner.builder()
                .pipeline(dsl.pipeline)
                .repository(repositoryDao)
                .taskExecutor(taskExecutor)
                .hooksService(hooksService)
                .git(git)
                .build()
                .run();
    }
}
