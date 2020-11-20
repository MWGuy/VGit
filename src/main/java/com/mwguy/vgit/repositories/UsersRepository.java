package com.mwguy.vgit.repositories;

import com.mwguy.vgit.dao.UserDao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<UserDao, String> {
    UserDao findByUserName(String userName);
    UserDao findByUserNameOrEmail(String userName, String email);
}
