package com.guneymarmara.kampanyatakip.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CampaignRepository {
    private static final List<Campaign> campaigns = new ArrayList<>();
    private static final Map<String, CampaignProgress> progressMap = new HashMap<>();
    private static long nextCampaignId = 1003L;
    private static long nextTaskId = 9000L;

    static {
        seedCampaigns();
        seedProgress();
    }

    private CampaignRepository() {}

    private static void seedCampaigns() {
        campaigns.add(new Campaign(
                1001L,
                "Haftasonu Fırsat Alanı Kontrolü",
                "Kampanya alanı, fiyat etiketi ve raf düzeni aynı standartta uygulanacak.",
                "Migros Fırsat Alanı",
                "13.06.2026 09:00",
                "16.06.2026 22:00",
                "Yüksek Öncelik",
                "%25'e varan fırsatlar",
                "Güney Marmara şubeleri",
                CampaignStatus.ACTIVE,
                Arrays.asList(
                        new CampaignTask(1L, "Kampanya alanı hazırlandı", "Afiş, stant ve yönlendirme görselleri kontrol edilir.", true),
                        new CampaignTask(2L, "Fiyat etiketleri güncel", "Kampanyalı ürünlerde eski fiyat etiketi kalmamalı.", true),
                        new CampaignTask(3L, "Raf ve stok kontrolü yapıldı", "Eksik stok varsa mağaza yöneticisine bildirilir.", true),
                        new CampaignTask(4L, "Kasa fiyat kontrolü yapıldı", "Kasa fiyatı ile raf fiyatı eşleşmeli.", true)
                )
        ));

        campaigns.add(new Campaign(
                1002L,
                "Yeni Ürün Lansman Alanı",
                "Yeni ürün raf alanı, görsel materyal ve personel bilgilendirme notları kontrol edilecek.",
                "Yeni Ürün Lansmanı",
                "14.06.2026 10:00",
                "21.06.2026 21:00",
                "Normal",
                "Lansman kontrolü",
                "Seçili Güney Marmara şubeleri",
                CampaignStatus.PLANNED,
                Arrays.asList(
                        new CampaignTask(11L, "Lansman raf alanı ayrıldı", "Ürün teşhiri için ayrılmış alan net olmalı.", true),
                        new CampaignTask(12L, "Personel bilgilendirmesi yapıldı", "Satış argümanları ekip içinde paylaşılır.", true),
                        new CampaignTask(13L, "Görsel materyaller kontrol edildi", "Eksik veya hasarlı afiş varsa not alınır.", true)
                )
        ));
    }

    private static void seedProgress() {
        addProgress(new CampaignProgress(
                1001L,
                8876L,
                "Aziziye Mahallesi Ekibi",
                "8876 • Aziziye Mahallesi MM Migros",
                CampaignProgressStatus.WAITING
        ));

        CampaignProgress mudanya = new CampaignProgress(
                1001L,
                1024L,
                "Mudanya Mağaza Ekibi",
                "1024 • Mudanya MM Migros",
                CampaignProgressStatus.SEEN
        );
        mudanya.seenAt = "Bugün";
        addProgress(mudanya);

        CampaignProgress nilufer = new CampaignProgress(
                1001L,
                3401L,
                "Bursa Nilüfer Ekibi",
                "3401 • Bursa Nilüfer MMM Migros",
                CampaignProgressStatus.APPROVED
        );
        nilufer.seenAt = "Bugün";
        nilufer.completedAt = "Bugün";
        nilufer.completedTaskIds.add(1L);
        nilufer.completedTaskIds.add(2L);
        nilufer.completedTaskIds.add(3L);
        nilufer.completedTaskIds.add(4L);
        addProgress(nilufer);

        addProgress(new CampaignProgress(
                1002L,
                8876L,
                "Aziziye Mahallesi Ekibi",
                "8876 • Aziziye Mahallesi MM Migros",
                CampaignProgressStatus.WAITING
        ));

        addProgress(new CampaignProgress(
                1002L,
                1024L,
                "Mudanya Mağaza Ekibi",
                "1024 • Mudanya MM Migros",
                CampaignProgressStatus.WAITING
        ));

        CampaignProgress seen = new CampaignProgress(
                1002L,
                3401L,
                "Bursa Nilüfer Ekibi",
                "3401 • Bursa Nilüfer MMM Migros",
                CampaignProgressStatus.SEEN
        );
        seen.seenAt = "Bugün";
        addProgress(seen);
    }

    private static void addProgress(CampaignProgress progress) {
        progressMap.put(key(progress.campaignId, progress.userId), progress);
    }

    private static String key(long campaignId, long userId) {
        return campaignId + ":" + userId;
    }

    public static List<Campaign> getCampaigns() {
        return campaigns;
    }

    public static Campaign getCampaign(long id) {
        for (Campaign campaign : campaigns) {
            if (campaign.id == id) return campaign;
        }
        return null;
    }

    public static CampaignProgress getProgressForUser(Campaign campaign, AppUser user) {
        if (campaign == null || user == null) return null;

        String key = key(campaign.id, user.id);
        CampaignProgress progress = progressMap.get(key);

        if (progress == null) {
            String storeName = user.storeCode == null || user.storeCode.length() == 0
                    ? user.storeName
                    : user.storeCode + " • " + user.storeName;
            progress = new CampaignProgress(campaign.id, user.id, user.fullName, storeName, CampaignProgressStatus.WAITING);
            progressMap.put(key, progress);
        }

        return progress;
    }

    public static List<CampaignProgress> getProgressForCampaign(long campaignId) {
        List<CampaignProgress> result = new ArrayList<>();
        for (CampaignProgress progress : progressMap.values()) {
            if (progress.campaignId == campaignId) {
                result.add(progress);
            }
        }
        return result;
    }

    public static List<CampaignProgress> getAllProgress() {
        return new ArrayList<>(progressMap.values());
    }

    public static int getTrackedStoreCount() {
        Set<String> stores = new HashSet<>();
        for (CampaignProgress progress : progressMap.values()) {
            stores.add(progress.storeName);
        }
        return stores.size();
    }

    public static Campaign addCampaign(String title,
                                       String description,
                                       String visualTitle,
                                       String startDate,
                                       String endDate,
                                       String priority,
                                       String discountText,
                                       String targetGroup) {
        List<CampaignTask> tasks = Arrays.asList(
                new CampaignTask(nextTaskId++, "Kampanya alanı hazırlandı", "Afiş, stant ve yönlendirme görselleri kontrol edilir.", true),
                new CampaignTask(nextTaskId++, "Fiyat etiketleri güncel", "Raf ve kasa fiyatları eşleşmeli.", true),
                new CampaignTask(nextTaskId++, "Stok kontrolü yapıldı", "Eksik stok varsa mağaza yöneticisine bildirilir.", true)
        );

        Campaign campaign = new Campaign(
                nextCampaignId++,
                title,
                description,
                visualTitle,
                startDate,
                endDate,
                priority,
                discountText,
                targetGroup,
                CampaignStatus.PLANNED,
                tasks
        );

        campaigns.add(0, campaign);

        addProgress(new CampaignProgress(campaign.id, 8876L, "Aziziye Mahallesi Ekibi", "8876 • Aziziye Mahallesi MM Migros", CampaignProgressStatus.WAITING));
        addProgress(new CampaignProgress(campaign.id, 1024L, "Mudanya Mağaza Ekibi", "1024 • Mudanya MM Migros", CampaignProgressStatus.WAITING));
        addProgress(new CampaignProgress(campaign.id, 3401L, "Bursa Nilüfer Ekibi", "3401 • Bursa Nilüfer MMM Migros", CampaignProgressStatus.WAITING));

        return campaign;
    }
}
