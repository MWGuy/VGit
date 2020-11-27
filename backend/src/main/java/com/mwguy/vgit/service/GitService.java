package com.mwguy.vgit.service;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.data.GitPackType;
import com.mwguy.vgit.exceptions.GitException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class GitService {
    private final Git git;

    public GitService(Git git) {
        this.git = git;
    }

    public void infoRefs(OutputStream outputStream, GitPackType packType, RepositoryDao repository)
            throws IOException, GitException {
        outputStream.write((packType.getMagic() + " service=" + packType.getName() + "\n0000").getBytes());

        git.statelessRpc()
                .repository(repository.toGitRepository())
                .advertiseRefs(true)
                .packType(packType)
                .build()
                .call()
                .transferTo(outputStream);
    }

    public void uploadPack(OutputStream outputStream, InputStream inputStream, RepositoryDao repository)
            throws IOException, GitException {
        git.statelessRpc()
                .repository(repository.toGitRepository())
                .packType(GitPackType.UPLOAD_PACK)
                .inputStream(inputStream)
                .build()
                .call()
                .transferTo(outputStream);
    }

    public void receivePack(OutputStream outputStream, InputStream inputStream, RepositoryDao repository)
            throws IOException, GitException {
        git.statelessRpc()
                .repository(repository.toGitRepository())
                .packType(GitPackType.RECEIVE_PACK)
                .inputStream(inputStream)
                .build()
                .call()
                .transferTo(outputStream);
    }

    public Git getGit() {
        return git;
    }
}
