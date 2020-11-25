package com.mwguy.vgit;

import java.util.regex.Pattern;

public interface VGitRegex {
    Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
}
