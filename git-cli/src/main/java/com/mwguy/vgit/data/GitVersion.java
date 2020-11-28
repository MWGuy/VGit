package com.mwguy.vgit.data;

import com.mwguy.vgit.exceptions.GitException;
import lombok.*;

@Data
@AllArgsConstructor
public class GitVersion {
    private Integer major;
    private Integer minor;
    private Integer path;

    @Override
    public String toString() {
        return String.format("%s.%s.%s", major, minor, path);
    }

    public static GitVersion parseVersionFromRawOutput(String output) throws GitException {
        if (output == null) {
            throw new GitException("Version source can`t be null");
        }

        output = output.replace("git version ", "").trim();
        String[] versionParts = output.split("\\.");
        if (versionParts.length != 3) {
            throw new GitException("Provided invalid version source");
        }

        return new GitVersion(
                Integer.parseInt(versionParts[0]),
                Integer.parseInt(versionParts[1]),
                Integer.parseInt(versionParts[2])
        );
    }
}
