"""
Border router gönderim servisi.

Sensör düğümünden seri port üzerinden gelen CSV satırlarını okur,
JSON'a dönüştürür ve ana sunucudaki canlı veri ucuna HTTPS POST ile
iletir. systemd altında `iot-sender.service` olarak çalışır.

Ham seri veri biçimi:

    # snapshot_begin gateway_id=<id> time_s=<t> virtual_nodes=<n>
    12960,341102,24,3289,23.1628,56.28,2.32
    ...
    # snapshot_end

`#` ile başlayan çerçeve satırları atlanır; aradaki her satır
virgülden bölünüp LiveData modelinin alanlarına eşlenir.
"""

import os
import sys
import time

import requests
import serial

API_URL = os.environ.get(
    "IOT_API_URL", "https://<alan-adi>/iot-api/api/v1/live/data"
)
SERIAL_PORT = os.environ.get("IOT_SERIAL_PORT", "/dev/ttyACM0")
BAUD_RATE = int(os.environ.get("IOT_BAUD_RATE", 115200))

RECONNECT_DELAY = 5
POST_RETRY_DELAY = 2

# CSV sütun sırası. LiveData modelinin alan adlarıyla birebir eşleşir.
FIELDS = [
    ("node_id", int),
    ("time_s", int),
    ("onchip_temp_c", int),
    ("battery_mv", int),
    ("env_temp_c", float),
    ("humidity_rh", float),
    ("light_lux", float),
]


def main():
    while True:
        try:
            ser = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)
            print(f"🔌 Bağlantı kuruldu: {SERIAL_PORT} @ {BAUD_RATE} baud")

            while True:
                line = ser.readline().decode("utf-8", errors="ignore").strip()
                if not line or line.startswith("#"):
                    continue

                print(f"📤 Ham Veri: {line}")

                parts = line.split(",")
                if len(parts) < len(FIELDS):
                    print("⚠️ Hata: Gelen veri eksik veya hatalı, atlanıyor...")
                    continue

                try:
                    payload = {
                        name: cast(parts[i])
                        for i, (name, cast) in enumerate(FIELDS)
                    }
                except ValueError:
                    print("⚠️ Hata: Veri dönüştürme başarısız, atlanıyor...")
                    continue

                sent_successfully = False
                while not sent_successfully:
                    try:
                        response = requests.post(
                            API_URL,
                            json=payload,
                            headers={"Content-Type": "application/json"},
                            timeout=5,
                        )
                        if response.status_code == 200:
                            print(
                                f"✅ İletildi! | Node ID: {payload['node_id']} "
                                f"| Temp: {payload['env_temp_c']}°C"
                            )
                            sent_successfully = True
                        else:
                            print(
                                f"❌ Sunucu Hatası ({response.status_code}). "
                                f"{POST_RETRY_DELAY} sn sonra tekrar denenecek..."
                            )
                            time.sleep(POST_RETRY_DELAY)
                    except requests.exceptions.RequestException as e:
                        print(
                            f"🔌 Ağ Hatası: {e}. "
                            f"{POST_RETRY_DELAY} sn sonra tekrar denenecek..."
                        )
                        time.sleep(POST_RETRY_DELAY)

        except serial.SerialException as e:
            print(
                f"⚠️ Seri Port Hatası: {e}. "
                f"{RECONNECT_DELAY} sn sonra yeniden bağlanılıyor..."
            )
            time.sleep(RECONNECT_DELAY)
        except KeyboardInterrupt:
            print("\n👋 Kapatılıyor...")
            sys.exit(0)
        except Exception as e:
            print(
                f"💥 Beklenmeyen Hata: {e}. "
                f"{RECONNECT_DELAY} sn sonra yeniden başlatılacak..."
            )
            time.sleep(RECONNECT_DELAY)


if __name__ == "__main__":
    main()
