package com.mwguy.vgit.exceptions;

import graphql.GraphQLException;

public class UsersException extends GraphQLException {
    public UsersException() {
        super();
    }

    public UsersException(String message) {
        super(message);
    }

    public UsersException(String message, Throwable cause) {
        super(message, cause);
    }
}
