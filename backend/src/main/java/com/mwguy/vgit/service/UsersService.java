package com.mwguy.vgit.service;

import com.mwguy.vgit.VGitRegex;
import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.exceptions.UsersException;
import com.mwguy.vgit.repositories.UsersRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@Service
public class UsersService {
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String PROVIDED_INVALID_EMAIL = "Provided invalid email";
    public static final String PROVIDED_INVALID_PASSWORD = "Provided invalid password";
    public static final String PROVIDED_INVALID_TOKEN = "Provided invalid token";

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoInput {
        @NonNull
        private String email;

        @NonNull
        private String realName;

        @Nullable
        private String description;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationCredentials {
        @NonNull
        private String userName;

        @NonNull
        private String password;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationResponse {
        private UserDao user;
        private String token;
    }

    public AuthorizationResponse registerUser(UserInfoInput info, AuthorizationCredentials credentials) throws UsersException {
        if (usersRepository.findByUserNameOrEmail(credentials.getUserName(), info.getEmail()) != null) {
            throw new UsersException(USER_ALREADY_EXISTS);
        }

        if (!VGitRegex.EMAIL_PATTERN.matcher(info.getEmail()).matches()) {
            throw new UsersException(PROVIDED_INVALID_EMAIL);
        }

        UserDao userDao = new UserDao();
        userDao.setBanned(false);
        userDao.setEmail(info.getEmail());
        userDao.setDescription(info.getDescription());
        userDao.setRealName(info.getRealName());
        userDao.setUserName(credentials.getUserName());
        userDao.setPassword(passwordEncoder.encode(credentials.getPassword()));
        userDao.setTokens(new HashSet<>());
        return authenticateUserImpl(usersRepository.save(userDao));
    }

    public AuthorizationResponse authenticateUser(AuthorizationCredentials credentials) throws UsersException {
        UserDao userDao = usersRepository.findByUserName(credentials.getUserName());
        if (userDao == null) {
            throw new UsersException(USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(credentials.getPassword(), userDao.getPassword())) {
            throw new UsersException(PROVIDED_INVALID_PASSWORD);
        }

        return authenticateUserImpl(userDao);
    }

    private AuthorizationResponse authenticateUserImpl(UserDao userDao) {
        String token = UUID.randomUUID().toString().replace("-", "");
        userDao.getTokens().add(token);
        return new AuthorizationResponse(usersRepository.save(userDao), token);
    }

    public void deleteToken(UserDao userDao, String token) {
        if (!userDao.getTokens().contains(token)) {
            throw new UsersException(PROVIDED_INVALID_TOKEN);
        }

        userDao.getTokens().remove(token);
        usersRepository.save(userDao);
    }

    public void deleteAllTokens(UserDao userDao) {
        userDao.setTokens(new HashSet<>());
        usersRepository.save(userDao);
    }
}
