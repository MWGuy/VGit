package com.mwguy.vgit.components.git;

import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.exceptions.GitException;
import com.mwguy.vgit.utils.Processes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Git {
    private final Path baseDirectory;
    private String versionString;

    public enum GitPackType {
        RECEIVE_PACK("receive-pack", "001f#", RepositoryDao.PermissionType.GIT_PUSH), // push
        UPLOAD_PACK("upload-pack", "001e#", RepositoryDao.PermissionType.GIT_PULL); // pull

        private final String type;
        private final String magic;
        private final RepositoryDao.PermissionType permissionType;

        GitPackType(String type, String magic, RepositoryDao.PermissionType permissionType) {
            this.type = type;
            this.magic = magic;
            this.permissionType = permissionType;
        }

        public MediaType getMediaType() {
            return new MediaType("application", "x-git-" + type + "-advertisement");
        }

        public String getName() {
            return "git-" + type;
        }

        public String getMagic() {
            return this.magic;
        }

        public RepositoryDao.PermissionType getPermissionType() {
            return permissionType;
        }

        public static GitPackType of(String pack) {
            if ("git-receive-pack".equals(pack)) {
                return GitPackType.RECEIVE_PACK;
            }

            return GitPackType.UPLOAD_PACK;
        }
    }

    public Git(Path baseDirectory) {
        this.baseDirectory = baseDirectory.toAbsolutePath();

        log.info(String.format("Using git %s", this));
    }

    public Path getBaseDirectory() {
        return baseDirectory;
    }

    public StatelessRpcBuilder statelessRpc() {
        return new StatelessRpcBuilder(this);
    }

    @Override
    public String toString() {
        return String.format("version=%s, repositoryBasePath=%s", version(), this.baseDirectory);
    }

    public String version() throws GitException {
        if (versionString != null) {
            return versionString;
        }

        try {
            Process process = Processes.startGitProcess("git", "--version");
            process.waitFor();
            String version = new String(process.getInputStream().readAllBytes());
            return versionString = version.substring(12).trim();
        } catch (InterruptedException | IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    public Process init(String repository) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("init");
        command.add("--bare");

        File directory = this.getBaseDirectory().resolve(repository).toFile();
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        return Processes.startGitProcess(command, directory);
    }
}
