# Gereksinimler

Bu doküman, sistemin karşılaması beklenen gereksinimleri ve bunların
fiilen karşılanma durumunu açıklar.

## 1. Kullanım Bağlamı

Sistemin paydaşları üç grupta toplanır:

- **Son kullanıcı — laboratuvar araştırmacısı veya öğrenci.** JWT ile
  korumalı pano, geçmiş ve düğüm detay sayfalarına giriş yaparak
  sensör verilerini gözlemler.

- **Veri kaynakları.** Fiziksel sensör düğümü (border router
  üzerinden) ve mock veri üreticileri; kimlik doğrulaması istemeyen
  veri alım uçlarına otomatik olarak POST atan istemcilerdir (bkz.
  [06-data-pipeline.md](06-data-pipeline.md)).

- **Sistem yöneticisi ve geliştiriciler.** Sunucuya SSH ile erişen,
  Dokku dağıtımını ve Nginx yapılandırmasını yöneten kişiler.

## 2. Fonksiyonel Gereksinimler

| Kimlik | Gereksinim | Durum | Not |
|---|---|---|---|
| FR-01 | Sensör verisinin toplanması | **Karşılandı** | Sistem farklı tiplerde sensör verisini toplayabiliyor. Gerçek bir fiziksel sensör düğümü ve mock veri üreticileriyle birlikte test edildi. |
| FR-02 | Uç cihazdan sunucuya iletim | **Karşılandı** | Border router seri porttan okuduğu ham veriyi ayrıştırıp JSON'a çeviriyor ve HTTPS üzerinden sunucudaki veri alım ucuna gönderiyor. Ağ veya sunucu kesintisinde gönderim otomatik olarak yeniden deneniyor. |
| FR-03 | Kalıcı saklama | **Karşılandı** | Veriler MongoDB üzerinde beş ayrı koleksiyonda saklanıyor: dört sistem tipi için birer veri koleksiyonu ve kullanıcı hesapları için bir koleksiyon. |
| FR-04 | Çoklu senaryo desteği | **Karşılandı** | Dört sistem tipi birbirinden bağımsız çalışıyor; her birinin kendi controller, service, repository ve model katmanı var. Arayüz tarafında tek bir bileşen, seçilen sistem tipine göre farklı ekran üretiyor. |
| FR-05 | Web arayüzünde görüntüleme | **Karşılandı** | Angular tabanlı arayüz, giriş yapmış kullanıcılara üç ayrı ekranda veri sunuyor: genel pano, geçmiş kayıtlar ve grafik içeren düğüm detayı. |
| FR-06 | Eşik aşımında bildirim | **Karşılandı** | Sıcaklık 40 °C eşiğini aştığında sistem Telegram üzerinden otomatik uyarı gönderiyor. |
| FR-07 | Kullanıcı girişi ve korumalı erişim | **Karşılandı** | Giriş akışı JWT tabanlı çalışıyor; üretilen token hem backend'deki korumalı uçlara erişimde hem de arayüzdeki rota korumasında kullanılıyor. Oturum kapatıldığında token geçersiz kılınabiliyor. |
| FR-08 | Veri dışa aktarma | **Karşılandı** | Dört sistem tipi için de CSV dışa aktarma ucu bulunuyor ve kayıtları döndürüyor. |

## 3. Fonksiyonel Olmayan Gereksinimler

| Kimlik | Gereksinim | Durum | Not |
|---|---|---|---|
| NFR-01 | Sürekli çalışma (7/24 işletim) | **Karşılandı** | Border router üzerindeki gönderim script'i systemd servisine dönüştürüldüğü için makine açıldığında kendiliğinden başlıyor ve çökme durumunda otomatik olarak yeniden ayağa kalkıyor. Sunucu tarafında uygulamalar Dokku üzerinde konteyner olarak koştuğu için benzer bir süreklilik sağlanıyor. Otomatik izleme ve arıza uyarısı kapsam dışında bırakıldı. |
| NFR-02 | Veri bütünlüğü | **Karşılandı** | Gelen istekler backend tarafında hem yapı hem alan bazında doğrulanıyor; eksik veya hatalı biçimdeki kayıtlar reddediliyor. Sensör firmware'inin geçerli okuma alamadığında ürettiği `-999.0` değeri ise bilinçli olarak elenmiyor: sistem farklı tiplerde sensörü karşılayacak şekilde tasarlandığı için ham veri olduğu gibi saklanıyor ve bu tür ayıklama veri analizi katmanına bırakılıyor. |
| NFR-03 | Güvenli taşıma (HTTPS/TLS) | **Karşılandı** | Dışarıya açılan tüm trafik HTTPS üzerinden şifreli taşınıyor ve sertifika geçerli durumda. TLS sonlandırması gateway katmanında yapıldığı için gateway ile arkadaki konteynerler arasındaki trafik şifresiz akıyor; bu iletişim aynı makine üzerinde kapalı bir ağ arayüzünde gerçekleştiğinden bu ölçekte kabul edilebilir bir tercihtir. |
| NFR-04 | Kimlik doğrulama ve yetkilendirme | **Karşılandı** | Sistem kimliği doğrulanmış ve doğrulanmamış kullanıcıyı ayırt ediyor, korumalı uçlar token olmadan erişime kapalı. Yetkilendirme tek kullanıcı rolü üzerinden tasarlandı; sistem dış kullanıcıya açık olmadığı için ayrıntılı bir rol hiyerarşisine ihtiyaç duyulmadı (bkz. [§4 Kısıtlar](#4-kısıtlar)). |
| NFR-05 | Kötüye kullanıma karşı dayanıklılık | **Karşılandı** | Kimlik doğrulaması istemeyen veri alım uçları, aynı kaynaktan gelen aşırı istekleri sınırlayan bir filtre ile korunuyor. |
| NFR-06 | Genişletilebilirlik | **Karşılandı** | MongoDB'nin şema zorunluluğu olmaması ve arayüzün sistem tipine göre kendini şekillendirmesi sayesinde yeni bir sensör senaryosu eklemek veritabanı göçü gerektirmiyor. Backend tarafında ise yeni bir controller-service-repository-model dörtlüsü yazmak gerekiyor; genişleme kolay ancak tekrar içeren bir işlem. |

## 4. Kısıtlar

Aşağıdaki kısıtlar, projenin baştan tabi olduğu ve yukarıdaki
gereksinimlerin karşılanma biçimini doğrudan şekillendiren sınırlardır:

- **Tek fiziksel sunucu.** Üniversitenin tahsis ettiği tek bir makine
  kullanıldı; yedeklilik ve yüksek erişilebilirlik baştan kapsam
  dışında tutuldu.

- **Kurum ağı ve tek alan adı.** Sistem `iotlab.omu.edu.tr` altında
  tek bir Nginx gateway üzerinden sunuluyor. Bu kısıt, tüm servislerin
  alt alan adı yerine path bazlı yönlendirilmesini şekillendirdi.

- **Tek fiziksel sensör düğümü.** Laboratuvarda hazır bulunan tek bir
  sensör düğümü vardı; FR-01'in mock veri üreticileriyle
  desteklenmesinin doğrudan nedeni budur.

- **Kısıtlı uç donanımı.** Border router'ın Raspberry Pi Zero olması
  (düşük işlem gücü ve enerji bütçesi), FR-02'nin hafif bir Python
  script ve basit HTTPS POST deseniyle çözülmesini, MQTT gibi daha
  ağır bir mesajlaşma altyapısının tercih edilmemesini şekillendirdi.

- **Süre ve ekip kısıtı.** Bir lisans bitirme projesi olarak kısa
  yinelemelerle ilerleyen bir geliştirme yöntemi benimsendi; öncelik
  uçtan uca çalışan bir sistem ortaya koymaya verildi.

## İlgili Dokümanlar

- [01-overview.md](01-overview.md) — Genel bakış, problem, kapsam
- [03-system-architecture.md](03-system-architecture.md) — Uçtan uca mimari, kapsam dışı bırakılanlar
- [06-data-pipeline.md](06-data-pipeline.md) — Veri hattı ve doğrulama noktaları
- [07-backend.md](07-backend.md) — Backend iç mimarisi, uç envanteri
- [09-frontend.md](09-frontend.md) — Arayüz kapsamı
