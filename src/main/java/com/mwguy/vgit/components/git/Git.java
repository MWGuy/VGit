package com.mwguy.vgit.components.git;

import com.mwguy.vgit.exceptions.GitException;
import com.mwguy.vgit.utils.Processes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class Git {
    private final Path baseDirectory;

    public enum GitPackType {
        RECEIVE_PACK("receive-pack", "001f#"), // push
        UPLOAD_PACK("upload-pack", "001e#"); // pull

        private final String type;
        private final String magic;
        GitPackType(String type, String magic) {
            this.type = type;
            this.magic = magic;
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
        return String.format("version=%s, repositoryBasePath=%s", Git.version(), this.baseDirectory);
    }

    public static String version() throws GitException {
        try {
            Process process = Processes.startGitProcess("git", "--version");
            process.waitFor();
            String version = new String(process.getInputStream().readAllBytes());
            return version.substring(12).trim();
        } catch (InterruptedException | IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
