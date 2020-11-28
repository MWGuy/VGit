package com.mwguy.vgit.data;

import lombok.*;

@Data
public class GitCommit {
    @Data
    public static class Refs {
        private String commit;
        private String tree;
    }

    @Data
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
