package com.mwguy.vgit.components.git;

import com.mwguy.vgit.exceptions.GitException;
import com.mwguy.vgit.utils.Processes;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class StatelessRpcBuilder implements GitProcessBuilder {
    private final Git git;
    private boolean advertiseRefs;
    private String repository;
    private InputStream inputStream;
    private Git.GitPackType packType;

    protected StatelessRpcBuilder(Git git) {
        this.git = git;
    }

    public StatelessRpcBuilder advertiseRefs() {
        advertiseRefs = true;
        return this;
    }

    public StatelessRpcBuilder repository(String repository) {
        this.repository = repository;
        return this;
    }

    public StatelessRpcBuilder inputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public StatelessRpcBuilder packType(Git.GitPackType packType) {
        this.packType = packType;
        return this;
    }

    @Override
    public Process build() throws GitException {
        LinkedList<String> args = new LinkedList<>();
        args.add(packType.getName());
        args.add("--stateless-rpc");
        if (advertiseRefs) {
            args.add("--advertise-refs");
        }

        args.add(this.git.getBaseDirectory().resolve(this.repository).toString());
        Process process = Processes.startGitProcess(args);
        if (this.inputStream != null) {
            try {
                process.getOutputStream().write(this.inputStream.readAllBytes());
                process.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new GitException(e.getMessage(), e);
            }
        }

        return process;
    }
}
