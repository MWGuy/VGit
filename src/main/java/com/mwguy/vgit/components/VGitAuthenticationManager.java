package com.mwguy.vgit.components;

import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.repositories.UsersRepository;
import com.mwguy.vgit.utils.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

@Slf4j
public class VGitAuthenticationManager implements AuthenticationManager {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    public VGitAuthenticationManager(PasswordEncoder passwordEncoder, UsersRepository usersRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return this.authenticateHttpBasic((UsernamePasswordAuthenticationToken) authentication);
        } else if (authentication instanceof BearerTokenAuthenticationToken) {
            return this.authenticateBearerBasic((BearerTokenAuthenticationToken) authentication);
        }

        throw new BadCredentialsException("Bad Credentials");
    }

    public Authentication authenticateHttpBasic(UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        UserDao userDao = usersRepository.findByUserName(username);
        if (userDao == null) {
            throw new UsernameNotFoundException(username);
        }

        if (!userDao.isAccountNonLocked()) {
            throw new LockedException("Account banned");
        }

        if (passwordEncoder.matches(password, userDao.getPassword())) {
            return Authorization.createAuthenticationToken(userDao, authentication.getCredentials());
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    public Authentication authenticateBearerBasic(BearerTokenAuthenticationToken authentication)
            throws AuthenticationException {
        UserDao userDao = usersRepository.findByTokensContains(authentication.getToken());
        if (userDao == null) {
            throw new BadCredentialsException("User not found");
        }

        if (!userDao.isAccountNonLocked()) {
            throw new LockedException("Account banned");
        }

        return Authorization.createAuthenticationToken(userDao, authentication.getToken());
    }
}
