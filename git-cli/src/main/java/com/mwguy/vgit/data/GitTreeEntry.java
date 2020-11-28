package com.mwguy.vgit.data;

import lombok.*;

@Data
public class GitTreeEntry {
    public enum GitObjectType {
        TREE,
        BLOB
    }

    private Integer mode;
    private GitObjectType type;
    private String object;
    private Integer size;
    private String name;
}
