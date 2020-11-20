package com.mwguy.vgit.controllers.graphql;

import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.exceptions.UsersException;
import com.mwguy.vgit.service.UsersService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import io.leangen.graphql.annotations.GraphQLMutation;
import org.springframework.stereotype.Component;

@Component
public class UsersMutationResolver implements GraphQLMutationResolver {
    private final UsersService usersService;

    public UsersMutationResolver(UsersService usersService) {
        this.usersService = usersService;
    }

    @GraphQLMutation(name = "registerUser")
    public UserDao registerUser(UsersService.UserInfoInput info,
                                UsersService.AuthorizationCredentials credentials) throws UsersException {
        return usersService.registerUser(info, credentials);
    }
}
