package com.guneymarmara.kampanyatakip.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                "Haftasonu Temizlik & Raf Düzeni Kampanyası",
                "Güney Marmara şubelerinde kampanya alanı, fiyat etiketleri ve raf düzeninin aynı standartta uygulanması gerekiyor.",
                "Haftasonu Fırsat Alanı",
                "13.06.2026 09:00",
                "16.06.2026 22:00",
                "Yüksek Öncelik",
                "%25'e varan fırsatlar",
                "Güney Marmara tüm şubeler",
                CampaignStatus.ACTIVE,
                Arrays.asList(
                        new CampaignTask(1L, "Kampanya alanı hazırlandı", "Afiş, stant ve yönlendirme görselleri kontrol edilecek.", true),
                        new CampaignTask(2L, "Fiyat etiketleri güncellendi", "Kampanyalı ürünlerde eski fiyat etiketi kalmayacak.", true),
                        new CampaignTask(3L, "Raf ve stok kontrolü yapıldı", "Eksik stok varsa yöneticiye not düşülecek.", true),
                        new CampaignTask(4L, "Kasa fiyat kontrolü yapıldı", "Kasadaki fiyat ile raf fiyatı eşleşmeli.", true),
                        new CampaignTask(5L, "Uygulama fotoğrafı hazır", "Bir sonraki sürümde fotoğraf kanıtı yüklenecek.", false)
                )
        ));

        campaigns.add(new Campaign(
                1002L,
                "Yeni Ürün Lansman Alanı Kontrolü",
                "Yeni ürün görselleri, raf alanı ve personel bilgilendirme notları kontrol edilecek.",
                "Yeni Ürün Lansmanı",
                "14.06.2026 10:00",
                "21.06.2026 21:00",
                "Normal",
                "Lansman vitrini",
                "Seçili Güney Marmara şubeleri",
                CampaignStatus.PLANNED,
                Arrays.asList(
                        new CampaignTask(11L, "Lansman raf alanı ayrıldı", "Ürün teşhiri için ayrılmış alan net olmalı.", true),
                        new CampaignTask(12L, "Personel bilgilendirmesi yapıldı", "Satış argümanları ekip içinde paylaşılacak.", true),
                        new CampaignTask(13L, "Görsel materyaller kontrol edildi", "Eksik veya hasarlı afiş varsa not düşülecek.", true)
                )
        ));
    }

    private static void seedProgress() {
        addProgress(new CampaignProgress(1001L, 3L, "Mehmet Kaya", "Migros Güney Marmara - Bursa Nilüfer Şube", CampaignProgressStatus.WAITING));
        CampaignProgress zeynep = new CampaignProgress(1001L, 4L, "Zeynep Yılmaz", "Migros Güney Marmara - Balıkesir Merkez Şube", CampaignProgressStatus.IN_PROGRESS);
        zeynep.seenAt = "Bugün";
        zeynep.completedTaskIds.add(1L);
        zeynep.completedTaskIds.add(2L);
        addProgress(zeynep);
        CampaignProgress ali = new CampaignProgress(1001L, 5L, "Ali Koç", "Migros Güney Marmara - Çanakkale Şube", CampaignProgressStatus.APPROVED);
        ali.seenAt = "Bugün";
        ali.completedAt = "Bugün";
        ali.completedTaskIds.add(1L);
        ali.completedTaskIds.add(2L);
        ali.completedTaskIds.add(3L);
        ali.completedTaskIds.add(4L);
        addProgress(ali);

        addProgress(new CampaignProgress(1002L, 3L, "Mehmet Kaya", "Migros Güney Marmara - Bursa Nilüfer Şube", CampaignProgressStatus.WAITING));
        addProgress(new CampaignProgress(1002L, 4L, "Zeynep Yılmaz", "Migros Güney Marmara - Balıkesir Merkez Şube", CampaignProgressStatus.WAITING));
        addProgress(new CampaignProgress(1002L, 5L, "Ali Koç", "Migros Güney Marmara - Çanakkale Şube", CampaignProgressStatus.SEEN));
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
            progress = new CampaignProgress(campaign.id, user.id, user.fullName, user.storeName, CampaignProgressStatus.WAITING);
            progressMap.put(key, progress);
        }
        return progress;
    }

    public static List<CampaignProgress> getProgressForCampaign(long campaignId) {
        List<CampaignProgress> result = new ArrayList<>();
        for (CampaignProgress progress : progressMap.values()) {
            if (progress.campaignId == campaignId) result.add(progress);
        }
        return result;
    }

    public static List<CampaignProgress> getAllProgress() {
        return new ArrayList<>(progressMap.values());
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
                new CampaignTask(nextTaskId++, "Kampanya alanı hazırlandı", "Afiş, stant ve yönlendirme görselleri kontrol edilecek.", true),
                new CampaignTask(nextTaskId++, "Fiyat etiketleri güncellendi", "Raf ve kasa fiyatları eşleşmeli.", true),
                new CampaignTask(nextTaskId++, "Stok kontrolü yapıldı", "Eksik stok varsa yöneticiye not düşülecek.", true)
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
        addProgress(new CampaignProgress(campaign.id, 3L, "Mehmet Kaya", "Migros Güney Marmara - Bursa Nilüfer Şube", CampaignProgressStatus.WAITING));
        addProgress(new CampaignProgress(campaign.id, 4L, "Zeynep Yılmaz", "Migros Güney Marmara - Balıkesir Merkez Şube", CampaignProgressStatus.WAITING));
        addProgress(new CampaignProgress(campaign.id, 5L, "Ali Koç", "Migros Güney Marmara - Çanakkale Şube", CampaignProgressStatus.WAITING));
        return campaign;
    }
}
