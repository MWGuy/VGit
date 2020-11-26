package com.mwguy.vgit.controllers.graphql;

import com.mwguy.vgit.exceptions.UsersException;
import com.mwguy.vgit.service.UsersService;
import com.mwguy.vgit.utils.Authorization;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UsersMutationResolver implements GraphQLMutationResolver {
    private final UsersService usersService;

    public UsersMutationResolver(UsersService usersService) {
        this.usersService = usersService;
    }

    public UsersService.AuthorizationResponse registerUser(UsersService.UserInfoInput info,
                                                           UsersService.AuthorizationCredentials credentials)
            throws UsersException {
        return usersService.registerUser(info, credentials);
    }

    public UsersService.AuthorizationResponse authenticateUser(UsersService.AuthorizationCredentials credentials)
            throws UsersException {
        return usersService.authenticateUser(credentials);
    }

    public Boolean deleteToken() {
        usersService.deleteToken(
                Objects.requireNonNull(Authorization.getCurrentUser(false)),
                Authorization.getCurrentToken()
        );
        return true;
    }

    public Boolean deleteAllTokens() {
        usersService.deleteAllTokens(Objects.requireNonNull(Authorization.getCurrentUser(false)));
        return true;
    }
}
