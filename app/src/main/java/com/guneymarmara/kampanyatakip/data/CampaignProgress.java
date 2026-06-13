package com.guneymarmara.kampanyatakip.data;

import java.util.HashSet;
import java.util.Set;

public class CampaignProgress {
    public final long campaignId;
    public final long userId;
    public final String userName;
    public final String storeName;
    public CampaignProgressStatus status;
    public String seenAt;
    public String completedAt;
    public final Set<Long> completedTaskIds = new HashSet<>();

    public CampaignProgress(long campaignId,
                            long userId,
                            String userName,
                            String storeName,
                            CampaignProgressStatus status) {
        this.campaignId = campaignId;
        this.userId = userId;
        this.userName = userName;
        this.storeName = storeName;
        this.status = status;
    }

    public boolean isTaskCompleted(long taskId) {
        return completedTaskIds.contains(taskId);
    }

    public void setTaskCompleted(long taskId, boolean completed) {
        if (completed) {
            completedTaskIds.add(taskId);
            if (status == CampaignProgressStatus.WAITING || status == CampaignProgressStatus.SEEN) {
                status = CampaignProgressStatus.IN_PROGRESS;
            }
        } else {
            completedTaskIds.remove(taskId);
            if (status == CampaignProgressStatus.APPROVED) {
                status = CampaignProgressStatus.IN_PROGRESS;
                completedAt = null;
            }
        }
    }

    public void markSeen() {
        if (status == CampaignProgressStatus.WAITING) {
            status = CampaignProgressStatus.SEEN;
        }
        if (seenAt == null) {
            seenAt = "Bugün";
        }
    }

    public boolean allRequiredTasksDone(Campaign campaign) {
        for (CampaignTask task : campaign.tasks) {
            if (task.required && !completedTaskIds.contains(task.id)) {
                return false;
            }
        }
        return true;
    }

    public int completedRequiredCount(Campaign campaign) {
        int count = 0;
        for (CampaignTask task : campaign.tasks) {
            if (task.required && completedTaskIds.contains(task.id)) count++;
        }
        return count;
    }

    public void approve(Campaign campaign) {
        if (allRequiredTasksDone(campaign)) {
            status = CampaignProgressStatus.APPROVED;
            completedAt = "Bugün";
        }
    }
}
