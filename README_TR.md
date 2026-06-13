# Migros Güney Marmara Kampanya Takip

Bu sürüm mağaza kodu + şifre girişine göre düzenlenmiştir.

## Giriş

### Mağaza girişi

- Mağaza kodu: `8876`
- İlk şifre: `123456`
- Açılan mağaza: `8876 • Aziziye Mahallesi MM Migros`

Mağaza giriş yaptıktan sonra ana ekrandaki **Mağaza Şifresini Değiştir** butonu ile kendi şifresini değiştirebilir.

### Admin girişi

- Admin: `admin@gm-kampanya.test`
- Şifre: `123456`

Admin hesabı yönetici panelini görür. Yönetici panelinde onay işlemi yoktur; sadece hangi mağazanın kampanyayı tamamladığı izlenir.

## Mağaza listesi nerede?

Mağaza listesi şu dosyada tutulur:

`app/src/main/java/com/guneymarmara/kampanyatakip/data/StoreRepository.java`

Yeni mağaza eklemek için `STORES` listesine şu formatta kayıt eklenir:

```java
new Store(
    2L,
    "1234",
    "Örnek Mahallesi MM Migros",
    "Güney Marmara",
    true
)
```

## Şifre sistemi

Mağaza şifreleri `SharedPreferences` içinde SHA-256 hash olarak saklanır. İlk şifre cihazda kayıtlı değilse varsayılan olarak `123456` kabul edilir. Mağaza şifre değiştirdiğinde yeni hash cihazda tutulur.

Backend bağlandığında bu yapı `CredentialStore` içinden API tabanlı kimlik doğrulamaya taşınabilir.

## Ekran yapısı

Bu sürüm XML layout kullanır:

- `screen_login.xml`
- `screen_home.xml`
- `screen_campaign_list.xml`
- `screen_campaign_detail.xml`
- `screen_manager_dashboard.xml`
- `screen_change_password.xml`
- `screen_create_campaign.xml`
- `item_campaign_card.xml`
- `item_task_info.xml`
- `item_progress_row.xml`

## Çalışan / mağaza akışı

1. Mağaza kodu ve şifre ile giriş yapar.
2. Kampanya listesini görür.
3. Kampanya detayını okur.
4. Mağazada uyguladıktan sonra tek butonla tamamlar.

Çoklu checkbox/onay kaldırılmıştır.

## Yönetici akışı

1. Admin hesabı ile giriş yapar.
2. Yönetici panelini açar.
3. Mağaza bazında kampanya durumunu görür.
4. Yönetici panelinde onay verme işlemi yoktur.
