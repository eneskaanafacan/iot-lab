package com.iot.dashboard.config;

import com.iot.dashboard.model.User;
import com.iot.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Value("${app.admin.default-password}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> existingAdminOpt = userRepository.findByUsername("admin");
        
        if (existingAdminOpt.isEmpty()) {
            logger.info("Veritabanında admin kullanıcısı bulunamadı, oluşturuluyor...");
            
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode(defaultAdminPassword))
                    .build();

            userRepository.save(adminUser);
            logger.info("Varsayılan admin kullanıcısı oluşturuldu (admin / app.admin.default-password).");
        } else {
            User existingAdmin = existingAdminOpt.get();
            if (!existingAdmin.getPassword().startsWith("$2a$")) {
                logger.info("Mevcut admin kullanıcısının şifresi düz metin formatında, hash'lenerek güncelleniyor...");
                existingAdmin.setPassword(passwordEncoder.encode(defaultAdminPassword));
                userRepository.save(existingAdmin);
                logger.info("Admin kullanıcısının şifresi başarıyla şifrelendi.");
            }
        }
    }
}
