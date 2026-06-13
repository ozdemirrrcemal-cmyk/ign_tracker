package com.guneymarmara.kampanyatakip.data;

public class AppUser {
    public final long id;
    public final String fullName;
    public final String email;
    public final String storeName;
    public final UserRole role;

    public AppUser(long id, String fullName, String email, String storeName, UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.storeName = storeName;
        this.role = role;
    }

    public boolean isManagerOrAdmin() {
        return role == UserRole.ADMIN || role == UserRole.MANAGER;
    }
}
