package com.guneymarmara.kampanyatakip.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StoreRepository {
    private static final List<Store> STORES = Arrays.asList(
            new Store(
                    1L,
                    "8876",
                    "Aziziye Mahallesi MM Migros",
                    "Güney Marmara",
                    true
            )
    );

    private StoreRepository() {}

    public static Store findByCode(String code) {
        if (code == null) return null;
        String normalized = code.trim();
        for (Store store : STORES) {
            if (store.active && store.code.equals(normalized)) {
                return store;
            }
        }
        return null;
    }

    public static List<Store> getStores() {
        return new ArrayList<>(STORES);
    }
}
