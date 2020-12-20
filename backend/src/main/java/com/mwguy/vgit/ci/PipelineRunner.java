package com.mwguy.vgit.ci;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.ci.dsl.Job;
import com.mwguy.vgit.ci.dsl.Pipeline;
import com.mwguy.vgit.ci.dsl.Stage;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.service.HooksService;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.task.TaskExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Builder
public class PipelineRunner {
    private final TaskExecutor taskExecutor;
    private final HooksService hooksService;
    private final RepositoryDao repository;
    private final Pipeline pipeline;
    private final Git git;
    private Path directory;
    private Long startTimeMillis;

    @SneakyThrows
    public void run() {
        directory = Files.createTempDirectory("vgit-ci");
        startTimeMillis = System.currentTimeMillis();

        git.clone()
                .from(repository.toGitRepository().getPath().toString())
                .to(directory)
                .bare(false)
                .build()
                .call();

        taskExecutor.execute(this::runStages);
    }

    @SneakyThrows
    public void runStages() {
        AtomicInteger passedJobs = new AtomicInteger(0);
        for (Stage stage: pipeline.stages) {
            passedJobs.set(0);
            log.info("Running stage {}", stage.name);

            for (Job job: stage.jobs) {
                taskExecutor.execute(() -> runJob(stage.name, job, (type, info) -> {
                    if (type.equals(StageConsumeType.PASSED)) {
                        passedJobs.incrementAndGet();
                    } else {
                        sendFailedWebHook(info);
                        passedJobs.set(-1);
                    }
                }));
            }

            while (passedJobs.get() < stage.jobs.size() && passedJobs.get() != -1) {
                Thread.sleep(200);
            }

            if (passedJobs.get() == -1) {
                break;
            }
        }

        if (passedJobs.get() != -1) {
            sendPassedWebHook();
        }

        FileUtils.deleteDirectory(directory.toFile());
    }

    @SneakyThrows
    private void runJob(String stage, Job job, StageConsumer<PipelineFailedInfo> consumer) {
        log.info("Running job {}", job.name);

        for (String command: job.commands) {
            log.info("Running command {}", command);

            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.directory(directory.toFile());
            processBuilder.inheritIO();

            pipeline.environment.forEach(processBuilder.environment()::put);

            try {
                int exitCode = processBuilder.start().waitFor();
                if (exitCode != 0) {
                    consumer.consume(StageConsumeType.FAILED,
                            PipelineFailedInfo.builder()
                                    .stage(stage)
                                    .job(job.name)
                                    .command(command)
                                    .exitCode(exitCode)
                                    .build());
                    return;
                }
            } catch (Exception e) {
                consumer.consume(StageConsumeType.FAILED,
                        PipelineFailedInfo.builder()
                                .stage(stage)
                                .job(job.name)
                                .command(command)
                                .exitCode(-1)
                                .build());
            }
        }

        consumer.consume(StageConsumeType.PASSED, null);
    }

    private void sendPassedWebHook() {
        PipelinePassedPayload payload = PipelinePassedPayload.builder()
                .repository(getRepositoryPath())
                .durationMillis(System.currentTimeMillis() - startTimeMillis)
                .build();

        hooksService.asyncTriggerWebHooks(repository,
                RepositoryDao.RepositoryHookType.PIPELINE_PASSED,
                payload);
    }

    public void sendFailedWebHook(PipelineFailedInfo pipelineFailedInfo) {
        PipelineFailedPayload payload = PipelineFailedPayload.builder()
                .repository(getRepositoryPath())
                .stage(pipelineFailedInfo.getStage())
                .job(pipelineFailedInfo.getJob())
                .command(pipelineFailedInfo.getCommand())
                .exitCode(pipelineFailedInfo.getExitCode())
                .durationMillis(System.currentTimeMillis() - startTimeMillis)
                .build();

        hooksService.asyncTriggerWebHooks(repository,
                RepositoryDao.RepositoryHookType.PIPELINE_FAILED,
                payload);
    }

    private interface StageConsumer<T> {
        void consume(StageConsumeType type, T payload);
    }

    private enum StageConsumeType {
        PASSED,
        FAILED
    }

    private String getRepositoryPath() {
        return String.format("%s/%s",
                repository.getPath().getNamespace(),
                repository.getPath().getName()
        );
    }

    @Data
    @Builder
    public static class PipelineFailedInfo {
        private final String stage;
        private final String job;
        private final String command;
        private final Integer exitCode;
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
