package com.mwguy.vgit;

import com.mwguy.vgit.commands.GitVersionCommand;

public class Git {
    public GitVersionCommand version() {
        return new GitVersionCommand();
    }
}
