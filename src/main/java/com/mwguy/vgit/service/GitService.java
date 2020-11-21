package com.mwguy.vgit.service;

import com.mwguy.vgit.components.git.Git;
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

    public void infoRefs(OutputStream outputStream, Git.GitPackType packType, String repository)
            throws IOException {
        outputStream.write((packType.getMagic() + " service=" + packType.getName() + "\n0000").getBytes());
        git.statelessRpc()
                .advertiseRefs()
                .packType(packType)
                .repository(repository)
                .build()
                .getInputStream()
                .transferTo(outputStream);
    }

    public void uploadPack(OutputStream outputStream, InputStream inputStream, String repository)
            throws IOException {
        git.statelessRpc()
                .packType(Git.GitPackType.UPLOAD_PACK)
                .repository(repository)
                .inputStream(inputStream)
                .build()
                .getInputStream()
                .transferTo(outputStream);
    }

    public void receivePack(OutputStream outputStream, InputStream inputStream, String repository)
            throws IOException {
        git.statelessRpc()
                .packType(Git.GitPackType.RECEIVE_PACK)
                .repository(repository)
                .inputStream(inputStream)
                .build()
                .getInputStream()
                .transferTo(outputStream);
    }
}
