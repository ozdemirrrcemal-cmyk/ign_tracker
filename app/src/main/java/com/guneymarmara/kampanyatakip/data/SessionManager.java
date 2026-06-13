package com.guneymarmara.kampanyatakip.data;

import java.util.Arrays;
import java.util.List;

public final class SessionManager {
    private static AppUser currentUser;
    private static final String ADMIN_EMAIL = "admin@gm-kampanya.test";
    private static final String TEST_PASSWORD = "123456";

    private static final List<AppUser> USERS = Arrays.asList(
            new AppUser(
                    1L,
                    "Cemal Özdemir",
                    ADMIN_EMAIL,
                    "Migros Güney Marmara Bölge Yönetimi",
                    "ADMIN",
                    UserRole.ADMIN
            ),
            new AppUser(
                    8876L,
                    "Aziziye Mahallesi Ekibi",
                    "8876",
                    "Aziziye Mahallesi MM Migros",
                    "8876",
                    UserRole.EMPLOYEE
            )
    );

    private SessionManager() {}

    public static AppUser loginAdmin(String email, String password) {
        if (email == null || password == null) return null;
        String normalizedEmail = email.trim().toLowerCase();

        if (!ADMIN_EMAIL.equalsIgnoreCase(normalizedEmail)) return null;
        if (!TEST_PASSWORD.equals(password.trim())) return null;

        for (AppUser user : USERS) {
            if (user.role == UserRole.ADMIN) {
                currentUser = user;
                return user;
            }
        }

        return null;
    }

    public static AppUser loginWithStoreCode(String storeCode) {
        if (storeCode == null) return null;
        String normalizedCode = storeCode.trim();

        for (AppUser user : USERS) {
            if (user.role == UserRole.EMPLOYEE && user.storeCode.equals(normalizedCode)) {
                currentUser = user;
                return user;
            }
        }

        return null;
    }

    public static AppUser login(String loginValue, String password) {
        if (password == null || password.trim().isEmpty()) {
            return loginWithStoreCode(loginValue);
        }
        return loginAdmin(loginValue, password);
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
