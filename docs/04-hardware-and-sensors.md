# Donanım ve Sensörler

Bu doküman sistemin fiziksel katmanını anlatır: ana sunucunun donanımı,
border router olarak kullanılan Raspberry Pi ve fiziksel sensör düğümü.
burada yalnızca
donanımın kendisi ve ondan çıkan ham veri ele alınır.

## Donanım Envanteri

| Bileşen | Rol | Teknik Özellikler 
|---|---|---|
| Ana sunucu | Tüm Dokku konteynerlerini ve Nginx'i barındıran fiziksel makine | Ubuntu Server 24.04.2 LTS| 
| Border router | Sensör verisini seri porttan okuyup HTTPS ile sunucuya ileten uç cihaz | Raspberry Pi Zero, Raspbian, kernel `6.12.87+rpt-rpi-v6`, mimari `armv6l` |
| Sensör düğümü | Sıcaklık/nem/ışık ölçüp seri porta basan  fiziksel düğüm | Mikrodenetleyici üzerinde çalışıyor; marka/model bilinmiyor | 

## Ana Sunucu

Sunucu, üniversite tarafından tahsis edilen fiziksel bir makinedir;
Ubuntu Server 24.04.2 LTS çalıştırır.

Donanım yaklaşık 4 çekirdek CPU,
16 GB RAM ve 512 GB disktir.

Bu, **tek/yedeksiz sunucu** kararının
donanım tarafındaki karşılığıdır: sistemin tamamı bu tek fiziksel
makineye bağımlıdır, donanım arızasında tek nokta hatası (SPOF) riski
doğar.

## Border Router — Raspberry Pi

Border router olarak **Raspberry Pi
Zero** kullanılmıitır. Cihaz Raspbian işletim sistemini çalıştırır.


## Sensör Düğümü

Sistemde **tek bir fiziksel sensör düğümü** vardır ve bu düğüm yalnızca
`live-data` sistemini besler; diğer üç sistem (`vineyard`, `wind`,
`flood`) mock veri üreticilerinden beslenir (bkz.
[03-system-architecture.md](03-system-architecture.md) §6).

Düğüm bir mikrodenetleyici üzerinde çalışır; sıcaklık, nem ve ışık
okur ve ölçtüğü veriyi seri port (`/dev/ttyACM0`) üzerinden satır
tabanlı olarak Raspberry Pi'ye basar.

## Ham Sensör Verisi

Pi üzerinde `minicom` ile `/dev/ttyACM0` dinlendiğinde, veri
`snapshot_begin`/`snapshot_end` etiketli bloklar hâlinde gelir. Blok
başlığı bir gateway kimliği, zaman damgası ve düğüm sayısını taşır:

```
# snapshot_begin gateway_id=... time_s=... virtual_nodes=8
12960,341102,24,3289,23.1628,2.32
...
# snapshot_end
```

Blok içindeki her satır, CSV benzeri virgülle ayrılmış alanlardan
oluşur (sıra: node_id, zaman, sıcaklık, pil, nem, ışık).

Backend'in `LiveData` modelinde kalıcı hâle gelen alanlar ve tipleri
şöyledir:

| Alan | Tip | Anlamı |
|---|---|---|
| `node_id` | Long | Sanal node kimliği |
| `time_s` | Long | Zaman damgası |
| `onchip_temp_c` | Integer | Mikrodenetleyicinin kendi çip sıcaklığı (°C) |
| `battery_mv` | Integer | Pil gerilimi (mV) |
| `env_temp_c` | Double | Ortam sıcaklığı (°C) |
| `humidity_rh` | Double | Bağıl nem (%RH) |
| `light_lux` | Double | Işık şiddeti (lux) |



## İlgili Dokümanlar

- [03-system-architecture.md](03-system-architecture.md) — Uçtan uca mimari, sistem tipleri, güven sınırları
- [05-border-router.md](05-border-router.md) — Raspberry Pi üzerindeki seri okuma ve gönderim yazılımı
- [06-data-pipeline.md](06-data-pipeline.md) — Veri hattı, mock üreticiler
