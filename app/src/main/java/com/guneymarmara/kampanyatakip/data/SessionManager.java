package com.guneymarmara.kampanyatakip.data;

import java.util.Arrays;
import java.util.List;

public final class SessionManager {
    private static AppUser currentUser;
    private static final String TEST_PASSWORD = "123456";

    private static final List<AppUser> USERS = Arrays.asList(
            new AppUser(
                    1L,
                    "Cemal Özdemir",
                    "admin@gm-kampanya.test",
                    "Migros Güney Marmara Bölge Yönetimi",
                    UserRole.ADMIN
            ),
            new AppUser(
                    2L,
                    "Ayşe Demir",
                    "mudur@gm-kampanya.test",
                    "Migros Güney Marmara - Bursa Nilüfer Şube",
                    UserRole.MANAGER
            ),
            new AppUser(
                    3L,
                    "Mehmet Kaya",
                    "calisan@gm-kampanya.test",
                    "Migros Güney Marmara - Bursa Nilüfer Şube",
                    UserRole.EMPLOYEE
            )
    );

    private SessionManager() {}

    public static AppUser login(String email, String password) {
        if (email == null || password == null) return null;
        String normalizedEmail = email.trim().toLowerCase();
        if (!TEST_PASSWORD.equals(password.trim())) return null;
        for (AppUser user : USERS) {
            if (user.email.equalsIgnoreCase(normalizedEmail)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    public static AppUser getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static List<AppUser> getTestUsers() {
        return USERS;
    }
}
