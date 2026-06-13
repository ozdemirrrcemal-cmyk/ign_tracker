package com.guneymarmara.kampanyatakip.data;

import java.util.ArrayList;
import java.util.List;

public class Campaign {
    public final long id;
    public final String title;
    public final String description;
    public final String visualTitle;
    public final String startDate;
    public final String endDate;
    public final String priority;
    public final String discountText;
    public final String targetGroup;
    public final CampaignStatus status;
    public final List<CampaignTask> tasks;

    public Campaign(long id,
                    String title,
                    String description,
                    String visualTitle,
                    String startDate,
                    String endDate,
                    String priority,
                    String discountText,
                    String targetGroup,
                    CampaignStatus status,
                    List<CampaignTask> tasks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.visualTitle = visualTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.discountText = discountText;
        this.targetGroup = targetGroup;
        this.status = status;
        this.tasks = tasks == null ? new ArrayList<>() : tasks;
    }

    public int requiredTaskCount() {
        int count = 0;
        for (CampaignTask task : tasks) {
            if (task.required) count++;
        }
        return count;
    }
}
