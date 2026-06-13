# Güney Marmara Kampanya Takip - Android Studio Projesi

Bu paket, sıfırdan açılabilir bağımsız Android Studio projesidir. Mevcut AquaLight projesine ihtiyaç yoktur.

## Proje bilgileri

- Uygulama adı: Güney Marmara Kampanya Takip
- Package: `com.guneymarmara.kampanyatakip`
- Dil: Java
- UI: Native Android programmatic UI
- Harici dependency: Yok
- Backend: Şimdilik yok, mock/local test verisi var

## Test kullanıcıları

Admin:
- E-posta: `admin@gm-kampanya.test`
- Şifre: `123456`

Mağaza yöneticisi:
- E-posta: `mudur@gm-kampanya.test`
- Şifre: `123456`

Çalışan:
- E-posta: `calisan@gm-kampanya.test`
- Şifre: `123456`

## Ekranlar

- Login ekranı
- Kampanya listesi
- Kampanya detay ekranı
- Çalışan görev/checklist onayı
- Yönetici kampanya takip paneli
- Yönetici yeni kampanya oluşturma ekranı

## Android Studio'da açma

1. Zip dosyasını çıkar.
2. Android Studio > File > Open.
3. Çıkardığın klasörü seç: `guney_marmara_campaign_tracker`.
4. Gradle Sync çalıştır.
5. Run ile emülatörde veya telefonda başlat.

Not: Bu pakette Gradle wrapper jar dosyası yoktur. Android Studio kendi Gradle kurulumu ile projeyi açabilir. Gerekirse Android Studio sağ üstten Gradle/AGP dosyalarını otomatik indirecektir.

## İlk test akışı

1. Admin hesabıyla giriş yap.
2. Yönetici Paneli ekranını kontrol et.
3. Çıkış yap.
4. Çalışan hesabıyla giriş yap.
5. Kampanyaya gir, görevleri işaretle.
6. Tüm zorunlu görevler tamamlanınca "Mağazamda Aktif Olarak Onayla" butonuna bas.
7. Çıkış yap, tekrar admin hesabıyla gir.
8. Yönetici panelinde çalışan durumunu kontrol et.

## Sonraki geliştirme adımları

- Firebase Cloud Messaging ile gerçek push bildirim
- Backend API ve gerçek login
- Kampanya görsel yükleme
- Günlük kontrol bildirimleri/takvimi
- Fotoğraf kanıtı yükleme
- Yönetici rapor ekranları
