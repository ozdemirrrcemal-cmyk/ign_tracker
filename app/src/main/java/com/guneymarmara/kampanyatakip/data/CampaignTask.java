package com.guneymarmara.kampanyatakip.data;

public class CampaignTask {
    public final long id;
    public final String title;
    public final String description;
    public final boolean required;

    public CampaignTask(long id, String title, String description, boolean required) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.required = required;
    }
}
