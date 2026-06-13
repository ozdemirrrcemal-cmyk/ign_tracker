package com.guneymarmara.kampanyatakip.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CredentialStore {
    private static final String PREFS = "gm_store_credentials";
    private static final String DEFAULT_STORE_PASSWORD = "123456";
    private static final String ADMIN_LOGIN = "admin@gm-kampanya.test";
    private static final String ADMIN_PASSWORD = "123456";

    private CredentialStore() {}

    public static boolean isAdminLogin(String value) {
        return value != null && ADMIN_LOGIN.equalsIgnoreCase(value.trim());
    }

    public static boolean verifyAdmin(String login, String password) {
        return isAdminLogin(login) && ADMIN_PASSWORD.equals(password == null ? "" : password.trim());
    }

    public static boolean verifyStorePassword(Context context, String storeCode, String password) {
        if (context == null || storeCode == null || password == null) return false;
        String expectedHash = getPasswordHash(context, storeCode);
        return expectedHash.equals(hash(storeCode.trim() + ":" + password.trim()));
    }

    public static boolean changeStorePassword(Context context, String storeCode, String oldPassword, String newPassword) {
        if (context == null || storeCode == null || oldPassword == null || newPassword == null) return false;
        String normalizedCode = storeCode.trim();
        String normalizedNewPassword = newPassword.trim();
        if (normalizedNewPassword.length() < 6) return false;
        if (!verifyStorePassword(context, normalizedCode, oldPassword)) return false;
        prefs(context).edit()
                .putString(passwordKey(normalizedCode), hash(normalizedCode + ":" + normalizedNewPassword))
                .apply();
        return true;
    }

    private static String getPasswordHash(Context context, String storeCode) {
        String normalizedCode = storeCode.trim();
        String saved = prefs(context).getString(passwordKey(normalizedCode), null);
        if (saved != null) return saved;
        return hash(normalizedCode + ":" + DEFAULT_STORE_PASSWORD);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String passwordKey(String storeCode) {
        return "store_password_hash_" + storeCode;
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 desteklenmiyor", e);
        }
    }
}
