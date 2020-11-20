package com.mwguy.vgit.service;

import com.mwguy.vgit.components.git.Git;
import org.apache.commons.io.IOUtils;
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
        Process process = git.statelessRpc()
                .advertiseRefs()
                .packType(packType)
                .repository(repository)
                .build();
        IOUtils.copy(process.getInputStream(), outputStream);
    }

    public void uploadPack(OutputStream outputStream, InputStream inputStream, String repository)
            throws IOException {
        Process process = git.statelessRpc()
                .packType(Git.GitPackType.UPLOAD_PACK)
                .repository(repository)
                .inputStream(inputStream)
                .build();
        IOUtils.copy(process.getInputStream(), outputStream);
    }

    public void receivePack(OutputStream outputStream, InputStream inputStream, String repository)
            throws IOException {
        Process process = git.statelessRpc()
                .packType(Git.GitPackType.RECEIVE_PACK)
                .repository(repository)
                .inputStream(inputStream)
                .build();
        IOUtils.copy(process.getInputStream(), outputStream);
    }
}
