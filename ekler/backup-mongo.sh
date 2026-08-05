#!/usr/bin/env bash
#
# MongoDB (Dokku) yedekleme script'i.
# Sunucuda (Ubuntu host) çalıştırılır — Dokku CLI'ye erişimi olan
# bir kullanıcı ile.
#
# Kurulum:
#   1. MONGO_SERVICE değerini gerçek Dokku mongo servis adınla değiştir.
#      Gerçek adı öğrenmek için: dokku mongo:list
#   2. BACKUP_DIR'i sunucuda yeterli disk alanı olan bir yola ayarla.
#   3. chmod +x backup-mongo.sh
#   4. mongo-backup.cron dosyasındaki talimatla crontab'a ekle.

set -euo pipefail

MONGO_SERVICE="iot-mongo"                
BACKUP_DIR="/var/backups/mongo"
RETENTION_DAYS=7
TIMESTAMP="$(date +%Y-%m-%d_%H-%M-%S)"
BACKUP_FILE="${BACKUP_DIR}/${MONGO_SERVICE}_${TIMESTAMP}.dump"
LOG_FILE="${BACKUP_DIR}/backup.log"

mkdir -p "${BACKUP_DIR}"

if dokku mongo:export "${MONGO_SERVICE}" > "${BACKUP_FILE}"; then
    echo "$(date '+%Y-%m-%d %H:%M:%S') OK  -> ${BACKUP_FILE}" >> "${LOG_FILE}"
else
    echo "$(date '+%Y-%m-%d %H:%M:%S') HATA -> yedek alinamadi" >> "${LOG_FILE}"
    rm -f "${BACKUP_FILE}"
    exit 1
fi

# RETENTION_DAYS'ten eski yedekleri sil
find "${BACKUP_DIR}" -maxdepth 1 -name "${MONGO_SERVICE}_*.dump" -type f -mtime "+${RETENTION_DAYS}" -delete
