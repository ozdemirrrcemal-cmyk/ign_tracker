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
        ensureProgressForAllStores();
    }

    private CampaignRepository() {}

    private static void seedCampaigns() {
        campaigns.add(new Campaign(
                1001L,
                "Haftasonu Kampanya Alanı Uygulaması",
                "Mağaza giriş alanı, fiyat etiketleri ve kampanya ürünlerinin merkezi duyuruya uygun şekilde uygulanması gerekiyor.",
                "Haftasonu Fırsatları",
                "13.06.2026 09:00",
                "16.06.2026 22:00",
                "Yüksek Öncelik",
                "Mağaza uygulama kontrolü",
                "Güney Marmara mağazaları",
                CampaignStatus.ACTIVE,
                Arrays.asList(
                        new CampaignTask(1L, "Kampanya alanı", "Afiş, stant ve yönlendirme görselleri kampanya alanında görünür olmalı.", true),
                        new CampaignTask(2L, "Fiyat etiketi", "Raf fiyatı ve kasa fiyatı kampanya bilgisiyle uyumlu olmalı.", true),
                        new CampaignTask(3L, "Raf / stok", "Kampanyalı ürünler kolay görülebilir şekilde düzenlenmeli.", true)
                )
        ));

        campaigns.add(new Campaign(
                1002L,
                "Yeni Ürün Lansman Alanı",
                "Yeni ürün alanı ve personel bilgilendirme notları kontrol edilecek.",
                "Lansman Alanı",
                "14.06.2026 10:00",
                "21.06.2026 21:00",
                "Normal",
                "Lansman vitrini",
                "Seçili mağazalar",
                CampaignStatus.PLANNED,
                Arrays.asList(
                        new CampaignTask(11L, "Lansman raf alanı", "Ürün teşhiri için ayrılan alan düzenli ve erişilebilir olmalı.", true),
                        new CampaignTask(12L, "Personel bilgilendirmesi", "Kampanya bilgisi vardiya ekibiyle paylaşılmalı.", true)
                )
        ));
    }

    private static void ensureProgressForAllStores() {
        for (Campaign campaign : campaigns) {
            for (Store store : StoreRepository.getStores()) {
                ensureProgress(campaign.id, store);
            }
        }
    }

    private static CampaignProgress ensureProgress(long campaignId, Store store) {
        String key = key(campaignId, store.code);
        CampaignProgress progress = progressMap.get(key);
        if (progress == null) {
            progress = new CampaignProgress(campaignId, store.code, store.name, CampaignProgressStatus.WAITING);
            progressMap.put(key, progress);
        }
        return progress;
    }

    private static String key(long campaignId, String storeCode) {
        return campaignId + ":" + storeCode;
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
        if (campaign == null || user == null || !user.isStoreUser()) return null;
        Store store = StoreRepository.findByCode(user.storeCode);
        if (store == null) return null;
        return ensureProgress(campaign.id, store);
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
                                       String startDate,
                                       String endDate,
                                       String targetGroup) {
        List<CampaignTask> tasks = Arrays.asList(
                new CampaignTask(nextTaskId++, "Kampanya alanı", "Afiş ve kampanya görselleri kontrol edilecek.", true),
                new CampaignTask(nextTaskId++, "Fiyat etiketi", "Raf ve kasa fiyatları eşleşmeli.", true),
                new CampaignTask(nextTaskId++, "Raf / stok", "Ürün görünürlüğü ve stok düzeni kontrol edilecek.", true)
        );
        Campaign campaign = new Campaign(
                nextCampaignId++,
                title,
                description,
                title,
                startDate,
                endDate,
                "Normal",
                "Mağaza uygulama kontrolü",
                targetGroup,
                CampaignStatus.PLANNED,
                tasks
        );
        campaigns.add(0, campaign);
        for (Store store : StoreRepository.getStores()) {
            ensureProgress(campaign.id, store);
        }
        return campaign;
    }
}
