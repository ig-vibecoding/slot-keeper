package com.slotkeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Single entry point for SlotKeeper.
 * Fails fast at startup when a required secret is missing, empty, or still a placeholder.
 */
@SpringBootApplication
@EnableScheduling
public class SlotKeeperApplication {

    private static final Logger log = LoggerFactory.getLogger(SlotKeeperApplication.class);

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.password-hash:}")
    private String adminPasswordHash;

    @Value("${app.security.encryption-key:}")
    private String encryptionKey;

    @Value("${app.security.phone-hmac-key:}")
    private String phoneHmacKey;

    @Value("${app.base-url:}")
    private String baseUrl;

    @Value("${app.messaging.provider:console}")
    private String messagingProvider;

    public static void main(String[] args) {
        SpringApplication.run(SlotKeeperApplication.class, args);
    }

    @Bean
    ApplicationRunner secretValidationRunner() {
        return args -> {
            requireSecret(adminUsername, "ADMIN_USERNAME");
            requireSecret(adminPasswordHash, "ADMIN_PASSWORD_HASH");
            requireSecret(encryptionKey, "ENCRYPTION_KEY");
            requireSecret(phoneHmacKey, "PHONE_HMAC_KEY");
            requireSecret(baseUrl, "APP_BASE_URL");
            if (!"console".equals(messagingProvider) && !"twilio".equals(messagingProvider)) {
                throw new IllegalStateException("MESSAGING_PROVIDER must be console or twilio");
            }
            log.info("SlotKeeper started");
        };
    }

    private static void requireSecret(String value, String name) {
        boolean missing = value == null || value.isBlank() || "__PLACEHOLDER__".equals(value.trim());
        if (missing) {
            throw new IllegalStateException(
                "Required secret " + name + " is missing, empty, or still a placeholder. Set it in .env and retry.");
        }
    }
}
