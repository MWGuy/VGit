package com.mwguy.vgit.utils;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class Authorization {
    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";

    public static UserDetails getCurrentUser(boolean allowAnonymousUser) {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new BadCredentialsException(UNAUTHORIZED_MESSAGE);
        }

        if (!allowAnonymousUser && userDetails == "anonymousUser") {
            throw new BadCredentialsException(UNAUTHORIZED_MESSAGE);
        }

        return (UserDetails) userDetails;
    }
}
