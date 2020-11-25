package com.mwguy.vgit.exceptions;

public class GitException extends Exception {
    public GitException() {
        super();
    }

    public GitException(String message) {
        super(message);
    }

    public GitException(String message, Throwable cause) {
        super(message, cause);
    }
}
