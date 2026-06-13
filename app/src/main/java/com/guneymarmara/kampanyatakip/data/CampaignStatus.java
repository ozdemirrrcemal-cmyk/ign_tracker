package com.guneymarmara.kampanyatakip.data;

public enum CampaignStatus {
    ACTIVE,
    PLANNED,
    ENDING_SOON,
    FINISHED;

    public String displayName() {
        switch (this) {
            case ACTIVE:
                return "Aktif";
            case PLANNED:
                return "Planlandı";
            case ENDING_SOON:
                return "Bitiyor";
            default:
                return "Bitti";
        }
    }
}
