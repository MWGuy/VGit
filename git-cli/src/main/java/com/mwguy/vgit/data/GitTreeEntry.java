package com.mwguy.vgit.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
