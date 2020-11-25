package com.mwguy.vgit.commands;

import com.mwguy.vgit.exceptions.GitException;

public interface GitCommand<T> {
    T call() throws GitException;
}
