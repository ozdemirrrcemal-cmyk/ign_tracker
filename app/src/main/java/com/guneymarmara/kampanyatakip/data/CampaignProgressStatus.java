package com.guneymarmara.kampanyatakip.data;

public enum CampaignProgressStatus {
    WAITING,
    SEEN,
    APPROVED,
    OVERDUE;

    public String displayName() {
        switch (this) {
            case WAITING:
                return "Bekliyor";
            case SEEN:
                return "Görüldü";
            case APPROVED:
                return "Tamamlandı";
            default:
                return "Gecikti";
        }
    }
}
