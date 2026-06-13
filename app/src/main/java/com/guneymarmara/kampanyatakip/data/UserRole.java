package com.guneymarmara.kampanyatakip.data;

public enum UserRole {
    ADMIN,
    MANAGER,
    EMPLOYEE;

    public String displayName() {
        switch (this) {
            case ADMIN:
                return "Admin";
            case MANAGER:
                return "Mağaza Yöneticisi";
            default:
                return "Çalışan";
        }
    }
}
