package com.mwguy.vgit.service;

import com.mwguy.vgit.components.git.Git;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.repositories.RepositoriesRepository;
import com.mwguy.vgit.utils.Authorization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;

@Service
public class RepositoriesService {
    public static final String NAMESPACE_PERMISSION_DENIED = "Permission denied for this namespace";
    public static final String PERMISSION_DENIED = "Permission denied";
    public static final String REPOSITORY_ALREADY_EXISTS = "Repository already exists";
    public static final String REPOSITORY_NOT_FOUND = "Repository not found";

    private final RepositoriesRepository repositoriesRepository;
    private final Git git;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRepositoryInput {
        private RepositoryDao.RepositoryPath path;
        private RepositoryDao.AccessPermission accessPermission;
        private String description;
    }

    public RepositoriesService(RepositoriesRepository repositoriesRepository, Git git) {
        this.repositoriesRepository = repositoriesRepository;
        this.git = git;
    }

    public RepositoryDao createNewRepository(CreateRepositoryInput input)
            throws InterruptedException, IOException {
        UserDao userDao = Authorization.getCurrentUser(false);
        assert userDao != null;
        if (!input.getPath().getNamespace().equals(userDao.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NAMESPACE_PERMISSION_DENIED);
        }

        if (repositoriesRepository.findByPath_NamespaceAndPath_Name(
                input.getPath().getNamespace(),
                input.getPath().getName()
        ) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, REPOSITORY_ALREADY_EXISTS);
        }

        git.init(input.path.getNamespace() + "/" + input.getPath().getName());
        RepositoryDao repositoryDao = new RepositoryDao();
        repositoryDao.setPath(new RepositoryDao.RepositoryPath(
                input.getPath().getName(),
                input.getPath().getNamespace(),
                RepositoryDao.RepositoryPathType.USER));
        repositoryDao.setAccessPermission(input.getAccessPermission());
        repositoryDao.setDescription(input.getDescription());
        repositoryDao.setMembersIds(Collections.singleton(userDao.getId()));
        return repositoriesRepository.save(repositoryDao);
    }

    public RepositoryDao findRepositoryAndCheckPermissions(
            String namespace,
            String name,
            RepositoryDao.PermissionType type
    ) {
        RepositoryDao repositoryDao = repositoriesRepository.findByPath_NamespaceAndPath_Name(namespace, name);
        if (repositoryDao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, REPOSITORY_NOT_FOUND);
        }

        if (type == RepositoryDao.PermissionType.HOOK_TRIGGER) {
            if (Authorization.isGitHookTrigger()) {
                return repositoryDao;
            } else {
                throw new BadCredentialsException(PERMISSION_DENIED);
            }
        }

        if (!repositoryDao.checkPermission(type, Authorization.getCurrentUser(true))) {
            throw new BadCredentialsException(Authorization.UNAUTHORIZED_MESSAGE);
        }

        return repositoryDao;
    }
}
