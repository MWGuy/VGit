package com.mwguy.vgit;

import com.mwguy.vgit.commands.GitInitCommand;
import com.mwguy.vgit.commands.GitLogCommand;
import com.mwguy.vgit.commands.GitStatelessRpcCommand;
import com.mwguy.vgit.commands.GitVersionCommand;

public class Git {
    public GitVersionCommand version() {
        return new GitVersionCommand();
    }

    public GitStatelessRpcCommand.GitStatelessRpcCommandBuilder statelessRpc() {
        return GitStatelessRpcCommand.builder();
    }

    public GitInitCommand.GitInitCommandBuilder init() {
        return GitInitCommand.builder();
    }

    public GitLogCommand.GitLogCommandBuilder log() {
        return GitLogCommand.builder();
    }
}
