package com.mwguy.vgit.controllers.graphql;

import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.repositories.RepositoriesRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RepositoriesQueryResolver implements GraphQLQueryResolver {
    private final RepositoriesRepository repositoriesRepository;

    public RepositoriesQueryResolver(RepositoriesRepository repositoriesRepository) {
        this.repositoriesRepository = repositoriesRepository;
    }

    public RepositoryDao repositoryByPath(RepositoryDao.RepositoryPath path)
            throws InterruptedException {
        return repositoriesRepository.findByPath_NamespaceAndPath_Name(path.getNamespace(), path.getName());
    }

    public Set<RepositoryDao> repositoriesByNamespace(String namespace) {
        return repositoriesRepository.findByPath_Namespace(namespace);
    }
}
