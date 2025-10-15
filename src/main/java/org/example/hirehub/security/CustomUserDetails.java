package org.example.hirehub.security;

import org.example.hirehub.entity.Permission;
import org.example.hirehub.entity.Role;
import org.example.hirehub.entity.RolePermission;
import org.example.hirehub.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private final User user;

    CustomUserDetails(User user) {
        this.user = user;
    }
    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        List<Permission> permissions =  role.getRolePermission().stream().map(RolePermission::getPermission).toList();

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
        for (Permission permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission.getAction().toUpperCase() + "_" + permission.getResource().toUpperCase()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
