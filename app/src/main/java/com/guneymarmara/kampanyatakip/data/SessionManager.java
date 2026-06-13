package com.guneymarmara.kampanyatakip.data;

import android.content.Context;

public final class SessionManager {
    private static AppUser currentUser;

    private SessionManager() {}

    public static AppUser login(Context context, String loginValue, String password) {
        if (context == null || loginValue == null || password == null) return null;
        String normalizedLogin = loginValue.trim();
        String normalizedPassword = password.trim();

        if (CredentialStore.verifyAdmin(normalizedLogin, normalizedPassword)) {
            currentUser = new AppUser(
                    1L,
                    "Bölge Admin",
                    "admin@gm-kampanya.test",
                    "ADMIN",
                    "Migros Güney Marmara Bölge Yönetimi",
                    UserRole.ADMIN
            );
            return currentUser;
        }

        Store store = StoreRepository.findByCode(normalizedLogin);
        if (store == null) return null;
        if (!CredentialStore.verifyStorePassword(context, store.code, normalizedPassword)) return null;

        currentUser = new AppUser(
                store.id,
                store.name,
                store.code,
                store.code,
                store.name,
                UserRole.EMPLOYEE
        );
        return currentUser;
    }

    public static AppUser getCurrentUser() {
        return currentUser;
    }

    public static boolean changeCurrentStorePassword(Context context, String oldPassword, String newPassword) {
        if (currentUser == null || !currentUser.isStoreUser()) return false;
        return CredentialStore.changeStorePassword(context, currentUser.storeCode, oldPassword, newPassword);
    }

    public static void logout() {
        currentUser = null;
    }
}
