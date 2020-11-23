package com.mwguy.vgit.service;

import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.repositories.RepositoriesRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HookRequestBody<T> {
        private RepositoryDao.RepositoryHookType type;
        private String repository;
        private T payload;
    }

    public HooksService(RepositoriesRepository repositoriesRepository) {
        this.repositoriesRepository = repositoriesRepository;
    }

    public Boolean hasHooks(RepositoryDao repositoryDao, RepositoryDao.RepositoryHookType type) {
        if (repositoryDao.getHooks().size() == 0) {
            return false;
        }

        for (RepositoryDao.RepositoryHook hook : repositoryDao.getHooks()) {
            if (hook.getType().equals(type)) {
                return true;
            }
        }

        return false;
    }

    @SneakyThrows
    public <T> void triggerHook(RepositoryDao repositoryDao, RepositoryDao.RepositoryHookType type, T payload) {
        for (RepositoryDao.RepositoryHook hook : repositoryDao.getHooks()) {
            if (hook.getType().equals(type)) {
                HookRequestBody<T> body = new HookRequestBody<>(type, repositoryDao.toRepositoryPath(), payload);
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
