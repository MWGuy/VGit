package com.mwguy.vgit;

import com.mwguy.vgit.commands.GitStatelessRpcCommand;
import com.mwguy.vgit.commands.GitVersionCommand;

public class Git {
    public GitVersionCommand version() {
        return new GitVersionCommand();
    }

    public GitStatelessRpcCommand.GitStatelessRpcCommandBuilder statelessRpcCommand() {
        return GitStatelessRpcCommand.builder();
    }
}
