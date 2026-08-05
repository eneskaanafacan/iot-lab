# Veri Modeli (MongoDB)


Bu doküman, backend'in MongoDB'de tuttuğu koleksiyonların alan alan
şemasını anlatır: hangi koleksiyon hangi model sınıfına karşılık
geliyor, alanların tipi/anlamı ne, kimlik ve zaman alanları nasıl
işliyor, indeksleme ve veri kalitesi durumu nedir gibi konuları içerir.


## 1. Veritabanı Seçimi


Projede yalnızca MongoDB kullanılmıştır; hiçbir ilişkisel veritabanı
(RDBMS) yoktur.


MongoDB'nin
şemasız (schemaless) yapısı, her sistemin kendi alan setiyle ayrı bir
koleksiyona yazabilmesini, yeni bir sensör tipi eklenirken bir şema
migrasyonuna gerek duyulmamasını sağlıyor. Bunun getirdiği esneklik sayesinde projede ilişkisel olmayan bir veri tabanı seçilmiştir.


## 2. Veritabanı ve Koleksiyon Envanteri




| Koleksiyon | Sistem | Model Sınıfı | Yazan Controller |
|---|---|---|---|
| `iot_data` | `vineyard` (Tarım & Bağ) | `IotData` | `IotDataController` |
| `live_data` | `live-data` (Raspberry Pi Canlı) | `LiveData` | `LiveDataController` |
| `wind_data` | `wind` (Rüzgar & Liman) | `WindData` | `WindController` |
| `flood_data` | `flood` (Akarsu & Sel) | `FloodData` | `FloodController` |
| `users` | Kimlik doğrulama | `User` | `AuthController` (okuma) ve `DataLoader` (yazma) |




## 3. Doküman Şemaları




### 3.1 `IotData` (`iot_data`)


| Alan | Tip | Birim | Açıklama |
|---|---|---|---|
| `id` | `String` | — | `ObjectId`'nin string karşılığı |
| `is_energest` | `Integer` | — | Energest Contiki enerji tüketimi ölçüm aracı kaynaklı bir bayrak |
| `node_id` | `Long` | — | Node kimliği |
| `light` | `Double` | lux | Işık ölçümü |
| `temperature` | `Double` |  °C | Ortam sıcaklığı |
| `humidity` | `Double` |   %RH | Nem |
| `soil_temperature` | `Double` |  °C | Toprak sıcaklığı |
| `clock_drift` | `Integer` | - | cihaz saatinin sunucu saatinden sapması; |
| `timestamp` | `LocalDateTime` | — | Kayıt anı (bkz. §9) |



### 3.2 `LiveData` (`live_data`)


| Alan | Tip | Birim | Açıklama |
|---|---|---|---|
| `id` | `String` | — | `ObjectId`'nin string karşılığı |
| `node_id` | `Long` | — | Node kimliği |
| `time_s` | `Long` | - | Sensörün kendi zaman/sayaç alanı|
| `onchip_temp_c` | `Integer` | °C | Mikrodenetleyicinin kendi çip sıcaklığı |
| `battery_mv` | `Integer` | mV | Pil voltajı |
| `env_temp_c` | `Double` | °C | Ortam sıcaklığı; Telegram eşik kontrolü bu alan üzerinden yapılıyor (bkz. [07-backend.md](07-backend.md#5)) |
| `humidity_rh` | `Double` | %RH | Bağıl nem |
| `light_lux` | `Double` | lux | Işık ölçümü |
| `timestamp` | `LocalDateTime` | — | Kayıt anı |

### 3.3 `FloodData` (`flood_data`)


| Alan (Java) | Mongo Alan Adı | Tip | Birim | Açıklama |
|---|---|---|---|---|
| `id` | `_id` | `String` | — | `ObjectId`'nin string karşılığı |
| `river_id` | `river_id` | `String` | — | Nehir/istasyon kimliği |
| `nodeId` | `node_id` (`@Field("node_id")` ile eşlenmiş) | `String` | — | Node kimliği; Iot/Live'daki `Long` tipinden farklı olarak `String` |
| `lat` | `lat` | `Double` | derece | Enlem |
| `lon` | `lon` | `Double` | derece | Boylam |
| `river_height_m` | `river_height_m` | `Double` | m | Nehir su seviyesi |
| `river_height_rate_cm_min` | `river_height_rate_cm_min` | `Double` | cm/dk | Su seviyesi değişim hızı |
| `flow_speed_mps` | `flow_speed_mps` | `Double` | m/s | Akış hızı |
| `rainfall_mm_h` | `rainfall_mm_h` | `Double` | mm/saat | Yağış miktarı |
| `soil_moisture_pct` | `soil_moisture_pct` | `Double` | % | Toprak nemi |
| `alert_level` | `alert_level` | `String` | — | Uyarı seviyesi (değer kümesi kaynaklardan çıkarılamıyor) |
| `flood_risk_score` | `flood_risk_score` | `Integer` | — | Sel risk skoru (ölçek/aralık belirtilmemiş) |
| `battery_v` | `battery_v` | `Double` | V | Pil voltajı — Live'daki `battery_mv` (mV) alanından birim olarak farklı |
| `rssi_dbm` | `rssi_dbm` | `Integer` | dBm | Sinyal gücü |
| `timestamp` | `timestamp` | `LocalDateTime` | — | Kayıt anı (bkz. §9) |


### 3.4 `WindData` (`wind_data`)


| Alan (Java) | Mongo Alan Adı | Tip | Birim | Açıklama |
|---|---|---|---|---|
| `id` | `_id` | `String` | — | `ObjectId`'nin string karşılığı |
| `harbour_id` | `harbour_id` | `String` | — | Liman/istasyon kimliği |
| `nodeId` | `node_id` (`@Field("node_id")` ile eşlenmiş) | `String` | — | Node kimliği |
| `lat` | `lat` | `Double` | derece | Enlem |
| `lon` | `lon` | `Double` | derece | Boylam |
| `wind_speed_mps` | `wind_speed_mps` | `Double` | m/s | Rüzgar hızı |
| `wind_direction_deg` | `wind_direction_deg` | `Integer` | derece | Rüzgar yönü |
| `wind_gust_mps` | `wind_gust_mps` | `Double` | m/s | Rüzgar hamlesi (gust) |
| `pressure_hpa` | `pressure_hpa` | `Double` | hPa | Basınç |
| `temperature_c` | `temperature_c` | `Double` | °C | Sıcaklık |
| `humidity_pct` | `humidity_pct` | `Double` | % | Nem |
| `battery_v` | `battery_v` | `Double` | V | Pil voltajı |
| `rssi_dbm` | `rssi_dbm` | `Integer` | dBm | Sinyal gücü |
| `link_quality` | `link_quality` | `Integer` | belirtilmemiş | Bağlantı kalitesi (ölçek belirtilmemiş) |
| `timestamp` | `timestamp` | `LocalDateTime` | — | Kayıt anı |



## 4. İndeksleme


Kod tabanında (`src/main/java` altında) dört sensör-veri model
sınıfının (`IotData`, `LiveData`, `FloodData`, `WindData`) her
birinde `node_id`/`nodeId` ve `timestamp` alanları `@Indexed`
anotasyonuyla işaretli — yani bu iki alan üzerinde tekli
(single-field) MongoDB indeksi tanımlı. `User`
koleksiyonunda ise `@Id` dışında hiçbir indeks yok.


Bunun somut sonucu şu: `findByTimestampAfter`,
`findByNodeIdOrderByTimestampDesc` gibi zaman penceresi ve node
bazlı sorgular artık indekslenmiş alanlar üzerinde çalışıyor; ancak
`node_id`/`nodeId` ile `timestamp`'i birlikte kullanan sorgular
(örn. `findByNodeIdAndTimestampBetweenOrderByTimestampDesc`) için
bir bileşik (`@CompoundIndex`) tanımlı değil.


## İlgili Dokümanlar


- [03-system-architecture.md](03-system-architecture.md) — Uçtan uca mimari, sistem tipleri
- [06-data-pipeline.md](06-data-pipeline.md) — Veri hattı, doğrulama boşluğu, zaman damgası kanıtı
- [07-backend.md](07-backend.md) — Spring Boot iç mimarisi, controller/service/repository katmanları

