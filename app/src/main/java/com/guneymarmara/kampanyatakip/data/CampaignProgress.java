package com.guneymarmara.kampanyatakip.data;

public class CampaignProgress {
    public final long campaignId;
    public final String storeCode;
    public final String storeName;
    public CampaignProgressStatus status;
    public String seenAt;
    public String completedAt;

    public CampaignProgress(long campaignId,
                            String storeCode,
                            String storeName,
                            CampaignProgressStatus status) {
        this.campaignId = campaignId;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.status = status;
    }

    public void markSeen() {
        if (status == CampaignProgressStatus.WAITING) {
            status = CampaignProgressStatus.SEEN;
        }
        if (seenAt == null) {
            seenAt = "Bugün";
        }
    }

    public void approve() {
        status = CampaignProgressStatus.APPROVED;
        if (seenAt == null) seenAt = "Bugün";
        completedAt = "Bugün";
    }
}
