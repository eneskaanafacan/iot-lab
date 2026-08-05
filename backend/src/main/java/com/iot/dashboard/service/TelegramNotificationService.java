package com.iot.dashboard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate;

    public TelegramNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendMessage(String message) {
        if (botToken == null || botToken.equals("YOUR_TELEGRAM_BOT_TOKEN") || chatId == null || chatId.isEmpty()) {
            logger.warn("Telegram bot token veya chat id ayarlanmadığı için mesaj gönderilemedi.");
            return;
        }

        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            
            Map<String, String> request = new HashMap<>();
            request.put("chat_id", chatId);
            request.put("text", message);

            restTemplate.postForObject(url, request, String.class);
            logger.info("Telegram bildirimi başarıyla gönderildi: {}", message);
            
        } catch (Exception e) {
            logger.error("Telegram bildirimi gönderilirken hata oluştu: {}", e.getMessage());
        }
    }
}
