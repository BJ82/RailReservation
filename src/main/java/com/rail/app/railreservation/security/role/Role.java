package com.rail.app.railreservation.security.role;

import com.rail.app.railreservation.security.permission.Permission;

import java.util.HashSet;
import java.util.Set;

public enum Role {
    ADMIN(Permission.READ),
    USER,

    Role(Permission permission) {
        Set<Permission> permissions = new HashSet();
    }
}
