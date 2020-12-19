package com.mwguy.vgit.service;

import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.repositories.RepositoriesRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@Service
public class HooksService {
    private final RepositoriesRepository repositoriesRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TaskExecutor taskExecutor;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HookRequestBody<T> {
        private RepositoryDao.RepositoryHookType type;
        private Date date;
        private String repository;
        private T payload;
    }

    public HooksService(RepositoriesRepository repositoriesRepository, TaskExecutor taskExecutor) {
        this.repositoriesRepository = repositoriesRepository;
        this.taskExecutor = taskExecutor;
    }

    public Boolean hasHooks(RepositoryDao repositoryDao, RepositoryDao.RepositoryHookType type) {
        if (repositoryDao.getHooks().size() == 0) {
            return false;
        }

        for (RepositoryDao.RepositoryHook hook : repositoryDao.getHooks()) {
            if (hook.getTypes().contains(type)) {
                return true;
            }
        }

        return false;
    }

    public <T> void asyncTriggerWebHooks(RepositoryDao repositoryDao, RepositoryDao.RepositoryHookType type, T payload) {
        taskExecutor.execute(() -> triggerWebHooks(repositoryDao, type, payload));
    }

    @SneakyThrows
    public <T> void triggerWebHooks(RepositoryDao repositoryDao, RepositoryDao.RepositoryHookType type, T payload) {
        for (RepositoryDao.RepositoryHook hook : repositoryDao.getHooks()) {
            if (hook.getTypes().contains(type)) {
                HookRequestBody<T> body = new HookRequestBody<>(type, new Date(), repositoryDao.toRepositoryPath(), payload);
                try {
                    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(hook.getUrl().toURI(), body, Void.class);
                    hook.getRequestLog().add(new RepositoryDao.RepositoryHookRequestLogEntity(responseEntity.getStatusCode(), new Date()));
                } catch (Throwable throwable) {
                    log.info("Can`t connect to " + hook.getUrl().toString() + ", pushing as 'bad gateway'...");
                    hook.getRequestLog().add(new RepositoryDao.RepositoryHookRequestLogEntity(HttpStatus.BAD_GATEWAY, new Date()));
                }
            }
        }

        repositoriesRepository.save(repositoryDao);
    }
}
