# Gereksinimler

Bu doküman, sistemin karşılaması beklenen gereksinimleri ve bunların
fiilen karşılanma durumunu açıklar.

## 1.Kullanım Bağlamı

Aşağıda sistemin paydaş tanımlamaları yapılmıştır.

- **Son kullanıcı / laboratuvar araştırmacısı/ öğrenci:** Sistemin JWT ile
  korumalı `/dashboard`, `/history`, `/node` sayfalarına giriş yapıp
  sensör verilerini gözlemleyen kişi. Kod tabanında bu rol için tek bir
  seviye var —`admin`/`normal kullanıcı` ayrımı yok (bkz.
  [15-security.md §3](15-security.md#3-erişim-ve-yetkilendirme));

- **Veri kaynağı (insan değil):** Fiziksel sensör düğümü (Border
  Router üzerinden) ve mock veri üreticileri, kimlik doğrulaması
  istemeyen `*/data` uçlarına POST atan otomatik istemcilerdir (bkz.
  [06-data-pipeline.md §5](06-data-pipeline.md#5-veri-alım-uçları)).

- **Sistem yöneticisi / geliştiriciler:** Sunucuya SSH ile erişen, Dokku
  deployment'ını ve Nginx yapılandırmasını yöneten kişi/kişiler.


## 2. Fonksiyonel Gereksinimler

| Kimlik | Gereksinim | Durum | Not |
|---|---|---|---|
| FR-01 | Sensör verisinin toplanması | **Karşılandı** | Sistem çeşitli tiplerde sensör verisini toplayabiliyor.Dökümanda açıklandığı üzere gerçek bir sensör ve mock veriler ile test edilmiştir. |
| FR-02 | Uç cihazdan sunucuya iletim | **Karşılandı** | Border router seri porttan okuduğu ham veriyi ayrıştırıp JSON'a çeviriyor ve HTTPS üzerinden sunucudaki ilgili veri alım ucuna gönderiyor.  |
| FR-03 | Kalıcı saklama | **Karşılandı** | Veriler MongoDB üzerinde beş ayrı koleksiyonda saklanıyor: dört sistem tipi için birer veri koleksiyonu ve kullanıcı hesapları için bir koleksiyon.|
| FR-04 | Çoklu senaryo desteği | **Karşılandı** | Dört sistem tipi birbirinden bağımsız çalışıyor; her birinin kendi controller, service, repository ve model katmanı var. Arayüz tarafında ise tek bir bileşen, seçilen sistem tipine göre farklı ekran üretiyor.|
| FR-05 | Web arayüzünde görüntüleme | **Karşılandı** | Angular tabanlı arayüz, giriş yapmış kullanıcılara üç ayrı ekranda veriyi sunuyor: genel pano, geçmiş kayıtlar, düğüm detayı ve grafikler.|
| FR-06 | Eşik aşımında bildirim | **Karşılandı** | Sıcaklık 40 °C eşiğini aştığında sistem Telegram üzerinden otomatik uyarı gönderiyor.|
| FR-07 | Kullanıcı girişi ve korumalı erişim | **Karşılandı** | Giriş akışı JWT tabanlı çalışıyor; kullanıcı doğrulandıktan sonra üretilen token, hem backend'deki korumalı uçlara erişimde hem de arayüzdeki rota korumasında kullanılıyor. Ancak yetkilendirme burada bitiyor: giriş yapan herkes aynı yetkilere sahip. |
| FR-08 | Veri dışa aktarma | **Kısmen Karşılandı** | Bağ ve canlı veri sistemlerinde CSV dışa aktarma tam olarak çalışıyor ve gerçek kayıtları döndürüyor.|

## 3. Fonksiyonel Olmayan Gereksinimler

| Kimlik | Gereksinim | Durum | Not |
|---|---|---|---|
| NFR-01 | Sürekli çalışma (7/24 işletim) | **Kısmen Karşılandı** | Border router üzerindeki gönderim script'i bir systemd servisine dönüştürüldüğü için makine açıldığında kendiliğinden başlıyor ve çökme durumunda otomatik olarak yeniden ayağa kalkıyor. Sunucu tarafında da uygulamalar Dokku üzerinde konteyner olarak koştuğu için benzer bir süreklilik sağlanıyor. |
| NFR-02 | Veri bütünlüğü | **Kısmen Karşılandı** | Gelen isteklerin JSON yapısı backend tarafından çözümlenirken temel bir format kontrolünden geçiyor, ancak alan bazında içerik doğrulaması hiçbir veri alım ucunda yapılmıyor. En somut sonucu şu: sensör firmware'i geçerli okuma alamadığında ürettiği -999.0 değeri zincirin hiçbir noktasında elenmiyor ve olduğu gibi veritabanına yazılıyor.Bu sorunun donanım kaynaklı olduğu tespit edilmiş olup proje kapsamı dışında bırakılmıştır.Sistem farklı tiplerde sensörlerden gelen verileri karşılayabilecek şekilde tasarlandığı için eleme özelliği eklenmemiştir bu işin veri analizi kısmında elenebilir.|
| NFR-03 | Güvenli taşıma (HTTPS/TLS) | **Karşılandı** | Dışarıya açılan tüm trafik HTTPS üzerinden şifreli olarak taşınıyor ve sertifika geçerli durumda. TLS sonlandırması gateway katmanında yapıldığı için gateway ile arkadaki konteynerler arasındaki trafik şifresiz akıyor; bu iletişim aynı makine üzerinde kapalı bir ağ arayüzünde gerçekleştiğinden bu ölçekte kabul edilebilir bir tercih sayılabilir. |
| NFR-04 | Kimlik doğrulama | **Kısmen Karşılandı** | Sistem kimliği doğrulanmış ve doğrulanmamış kullanıcıyı ayırt edebiliyor, korumalı uçlar token olmadan erişime kapalı. Ancak: rol ayrımı yok, giriş yapan herkes aynı rolde.Dış kullanıcı için tasarlanmış bir sistem olmadığı için farklı rollere ihtiyaç duyulmadı üstüne zaman kıstı.|
| NFR-05 | Genişletilebilirlik | **Karşılandı** | MongoDB'nin şema zorunluluğu olmaması ve arayüzün sistem tipine göre kendini şekillendirmesi sayesinde yeni bir sensör senaryosu eklemek veritabanı göçü gerektirmiyor. Yine de yeni bir sistem eklemek, backend tarafında yeni bir controller-service-repository-model dörtlüsünü eklemekak anlamına geliyor; genişleme kolay ama zahmetli. |


## 4. Kısıtlar

Aşağıdaki kısıtlar, projenin baştan tabi olduğu ve yukarıdaki
gereksinimlerin karşılanma biçimini doğrudan şekillendiren sınırlardır:

- **Tek fiziksel sunucu (yedeksiz).** Üniversitenin tahsis ettiği tek
  bir makine kullanıldı.
- **Kurum ağı / tek alan adı içinde konumlanma.** Sistem
  `iotlab.omu.edu.tr` altında tek bir Nginx gateway üzerinden
  sunuluyor; bu, tüm servislerin path bazlı tek bir gateway'den yönlendirilmesini şekillendirdi.

- **Tek fiziksel sensör düğümü.** Laboratuvarda hazır bulunan tek bir
  sensör düğümü var; bu, FR-01'in mock veri üreticilerine dayanmasının
  doğrudan nedenidir.

- **Kısıtlı uç donanımı.** Border Router'ın Raspberry Pi Zero olması
  (düşük işlem gücü, düşük pil bütçesi),FR-02'nin hafif bir Python
  script + basit HTTPS POST deseniyle çözülmesini, MQTT gibi daha ağır
  bir mesajlaşma altyapısının tercih edilmemesini şekillendirdi.

- **Süre ve ekip kısıtı.** Bir lisans bitirme projesi olarak, çevik/
  kısa-sprint bir geliştirme yöntemi benimsendi.Süre yetersizliği nedeniye bazı kısımlara (özellikle yazılımsal) tam olarak `best practice` diyemeyiz



## İlgili Dokümanlar

- [01-overview.md](01-overview.md) — Genel bakış, problem, kapsam
- [03-system-architecture.md](03-system-architecture.md) — Uçtan uca mimari, kapsam dışı bırakılanlar
- [06-data-pipeline.md](06-data-pipeline.md) — Veri doğrulama boşluğu, mock verinin meşruiyeti
- [07-backend.md](07-backend.md) — Eksik/yarım kalan uçlar, hata yönetimi
- [09-frontend.md](09-frontend.md) — Arayüz kapsamı, bilinen sınırlamalar
