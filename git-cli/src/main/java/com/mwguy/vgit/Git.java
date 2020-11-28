package com.mwguy.vgit;

import com.mwguy.vgit.commands.*;

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

    public GitLsTreeCommand.GitLsTreeCommandBuilder lsTree() {
        return GitLsTreeCommand.builder();
    }

    public GitCatFileCommand.GitCatFileCommandBuilder catFile() {
        return GitCatFileCommand.builder();
    }
}
