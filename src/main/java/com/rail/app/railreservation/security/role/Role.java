package com.rail.app.railreservation.security.role;

import com.rail.app.railreservation.security.permission.Permission;

import java.util.HashSet;
import java.util.Set;

public enum Role {

    ADMIN(Set.of(Permission.READ,Permission.WRITE,Permission.DELETE)),
    USER(Set.of(Permission.READ));

    public Set<Permission> getPermission() {
        return permission;
    }

    private final Set<Permission> permission = new HashSet<>();

    private Role(Set<Permission> permission) {

        this.permission.addAll(permission);
    }
}
