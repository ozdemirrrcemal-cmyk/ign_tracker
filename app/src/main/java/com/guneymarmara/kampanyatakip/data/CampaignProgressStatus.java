package com.guneymarmara.kampanyatakip.data;

public enum CampaignProgressStatus {
    WAITING,
    SEEN,
    IN_PROGRESS,
    APPROVED,
    OVERDUE;

    public String displayName() {
        switch (this) {
            case WAITING:
                return "Bekliyor";
            case SEEN:
                return "Görüldü";
            case IN_PROGRESS:
                return "İşlemde";
            case APPROVED:
                return "Onaylandı";
            default:
                return "Gecikti";
        }
    }
}
