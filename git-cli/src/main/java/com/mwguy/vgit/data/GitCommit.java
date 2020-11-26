package com.mwguy.vgit.data;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitCommit {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Refs {
        private String commit;
        private String tree;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GitUserInfo {
        private String name;
        private String email;
        private Date date;
    }

    private Refs refs;
    private GitUserInfo author;
    private GitUserInfo committer;
    private String subject;
    private String body;
}
