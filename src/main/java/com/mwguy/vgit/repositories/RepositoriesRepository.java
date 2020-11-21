package com.mwguy.vgit.repositories;

import com.mwguy.vgit.dao.RepositoryDao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoriesRepository extends MongoRepository<RepositoryDao, String> {
    RepositoryDao findByPath_NamespaceAndPath_Name(String namespace, String name);
}
