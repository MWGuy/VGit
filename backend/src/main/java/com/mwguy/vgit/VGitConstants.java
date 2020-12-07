package com.mwguy.vgit;

import java.util.regex.Pattern;

public interface VGitConstants {
    Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");

    String NAMESPACE_PERMISSION_DENIED = "Permission denied for this namespace";
    String PERMISSION_DENIED = "Permission denied";
    String REPOSITORY_ALREADY_EXISTS = "Repository already exists";
    String REPOSITORY_NOT_FOUND = "Repository not found";
    String USER_ALREADY_EXISTS = "User already exists";
    String USER_NOT_FOUND = "User not found";
    String ACCOUNT_BANNED = "Account banned";
    String PROVIDED_INVALID_EMAIL = "Provided invalid email";
    String PROVIDED_INVALID_PASSWORD = "Provided invalid password";
    String PROVIDED_INVALID_TOKEN = "Provided invalid token";
    String PROVIDED_INVALID_CREDENTIALS = "Invalid username or password";
    String UNAUTHORIZED_MESSAGE = "Unauthorized";
    String GIT_HOOK_TRIGGER = "git-hook-trigger";
}
