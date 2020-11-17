package com.mwguy.vgit.components.git;

import com.mwguy.vgit.exceptions.GitException;

public interface GitProcessBuilder {
    Process build() throws GitException;
}
