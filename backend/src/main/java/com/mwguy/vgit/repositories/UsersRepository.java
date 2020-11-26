package com.mwguy.vgit.repositories;

import com.mwguy.vgit.dao.UserDao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UsersRepository extends MongoRepository<UserDao, String> {
    UserDao findByUserName(String userName);

    UserDao findByUserNameOrEmail(String userName, String email);

    UserDao findByTokensContains(String token);

    List<UserDao> findById(List<String> ids);
}
