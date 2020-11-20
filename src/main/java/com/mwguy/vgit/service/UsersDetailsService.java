package com.mwguy.vgit.service;

import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.repositories.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public UsersDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDao userDao = usersRepository.findByUserName(username);
        if (userDao == null) {
            throw new UsernameNotFoundException(username);
        }

        return userDao;
    }
}
