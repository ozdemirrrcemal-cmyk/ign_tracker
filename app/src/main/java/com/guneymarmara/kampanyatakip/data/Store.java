package com.guneymarmara.kampanyatakip.data;

public class Store {
    public final long id;
    public final String code;
    public final String name;
    public final String region;
    public final boolean active;

    public Store(long id, String code, String name, String region, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.region = region;
        this.active = active;
    }

    public String displayName() {
        return code + " • " + name;
    }
}
