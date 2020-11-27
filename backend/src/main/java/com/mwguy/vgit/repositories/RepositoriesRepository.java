package com.mwguy.vgit.repositories;

import com.mwguy.vgit.dao.RepositoryDao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface RepositoriesRepository extends MongoRepository<RepositoryDao, String> {
    RepositoryDao findByPath_NamespaceAndPath_Name(String namespace, String name);

    Set<RepositoryDao> findByPath_Namespace(String namespace);
}
