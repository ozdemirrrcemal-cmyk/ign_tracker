package com.guneymarmara.kampanyatakip.data;

public class AppUser {
    public final long id;
    public final String fullName;
    public final String loginName;
    public final String storeCode;
    public final String storeName;
    public final UserRole role;

    public AppUser(long id,
                   String fullName,
                   String loginName,
                   String storeCode,
                   String storeName,
                   UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.loginName = loginName == null ? "" : loginName;
        this.storeCode = storeCode == null ? "" : storeCode;
        this.storeName = storeName == null ? "" : storeName;
        this.role = role;
    }

    public boolean isManagerOrAdmin() {
        return role == UserRole.ADMIN || role == UserRole.MANAGER;
    }

    public boolean isStoreUser() {
        return role == UserRole.EMPLOYEE;
    }

    public String storeDisplayName() {
        if (storeCode == null || storeCode.isEmpty()) return storeName;
        return storeCode + " • " + storeName;
    }
}
