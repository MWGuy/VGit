package com.mwguy.vgit.data;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GitCommit {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Refs {
        private String commit;
        private String tree;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GitUserInfo {
        private String name;
        private String email;
        private Integer date;
    }

    private Refs refs;
    private GitUserInfo author;
    private GitUserInfo committer;
    private String subject;
    private String body;
}
