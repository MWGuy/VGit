package com.mwguy.vgit.dao;

import lombok.extern.slf4j.Slf4j;
import com.mwguy.vgit.service.UsersDetailsService;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UsersAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UsersDetailsService usersDetailsService;

    public UsersAuthenticationProvider(PasswordEncoder passwordEncoder, UsersDetailsService usersDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.usersDetailsService = usersDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        UserDetails details = usersDetailsService.loadUserByUsername(username);
        if (!details.isAccountNonLocked()) {
            throw new LockedException("Account banned");
        }

        if (passwordEncoder.matches(password, details.getPassword())) {
            return new AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return details;
                }
            };
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
