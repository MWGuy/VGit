package com.mwguy.vgit.dao;

import com.mwguy.vgit.VGitApplication;
import com.mwguy.vgit.repositories.RepositoriesRepository;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
@Document("users")
@NoArgsConstructor
public class UserDao implements UserDetails {
    @Id
    @NonNull
    private String id;

    @NonNull
    private String email;

    @NonNull
    private String userName;

    @NonNull
    private String realName;

    @Nullable
    private String description;

    @NonNull
    private String password;

    @NonNull
    private Boolean banned;

    @NonNull
    private Set<String> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.NO_AUTHORITIES;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !banned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Set<RepositoryDao> getRepositories() {
        RepositoriesRepository repositoriesRepository = VGitApplication.context.getBean(RepositoriesRepository.class);
        return repositoriesRepository.findByPath_Namespace(getUsername());
    }
}
