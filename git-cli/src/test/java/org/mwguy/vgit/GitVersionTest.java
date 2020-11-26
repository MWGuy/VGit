package org.mwguy.vgit;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.data.GitVersion;
import com.mwguy.vgit.exceptions.GitException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GitVersionTest {
    private final Git git = new Git();

    @Test
    public void versionTest() throws GitException {
        GitVersion gitVersion = git.version().call();

        assertNotNull(gitVersion);

        System.out.printf(
                "Parsed git version major: %s, minor: %s, patch: %s - %s%n",
                gitVersion.getMajor(),
                gitVersion.getMinor(),
                gitVersion.getPath(),
                gitVersion.toString()
        );
    }
}
