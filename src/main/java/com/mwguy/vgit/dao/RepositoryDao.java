package com.mwguy.vgit.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("repositories")
public class RepositoryDao {
    public enum AccessPermission {
        PUBLIC,
        PRIVATE,
        INTERNAL
    }

    public enum PermissionType {
        GIT_PULL,
        GIT_PUSH
    }

    public enum RepositoryPathType {
        USER,
        GROUP
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepositoryPath {
        private String name;
        private String namespace;
        private RepositoryPathType type;
    }

    @NonNull
    private String id;

    @NonNull
    private RepositoryPath path;

    @NonNull
    private AccessPermission accessPermission;

    @Nullable
    private String description;

    @NonNull
    private Set<String> membersIds;

    public Boolean needAuthorization(PermissionType type) {
        if (type == PermissionType.GIT_PULL) {
            return !accessPermission.equals(AccessPermission.PUBLIC);
        } else {
            return true;
        }
    }

    public Boolean checkPermission(PermissionType type, @Nullable UserDao userDao) {
        if (type == PermissionType.GIT_PULL) {
            if (accessPermission.equals(AccessPermission.PUBLIC)) {
                return true;
            } else if (accessPermission.equals(AccessPermission.INTERNAL)) {
                return userDao != null;
            } else {
                return userDao != null && membersIds.contains(userDao.getId());
            }
        } else if (type == PermissionType.GIT_PUSH) {
            return userDao != null && membersIds.contains(userDao.getId());
        }

        return false;
    }

    public String toRepositoryPath() {
        return path.getNamespace() + "/" + path.getName();
    }
}
