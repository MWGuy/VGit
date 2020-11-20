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

@Service
public class UsersService {
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String PROVIDED_INVALID_EMAIL = "Provided invalid email";

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

    public UserDao registerUser(UsersService.UserInfoInput info,
                                UsersService.AuthorizationCredentials credentials) throws UsersException {
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
        return usersRepository.save(userDao);
    }
}
