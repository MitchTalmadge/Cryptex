package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.SpringProfileService;
import com.mitchtalmadge.cryptex.service.telegram.impl.BotConfigImpl;
import com.mitchtalmadge.cryptex.service.telegram.impl.ChatUpdatesBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;

import javax.annotation.PostConstruct;
import java.util.logging.Level;

/**
 * This service signs into and maintains connection with Telegram.
 */
@Service
public class TelegramService {

    /**
     * The App api_id which can be obtained from https://my.telegram.org/apps
     */
    private static final String API_ID = System.getenv("TELEGRAM_API_ID");

    /**
     * The App api_hash which can be obtained from https://my.telegram.org/apps
     */
    private static final String API_HASH = System.getenv("TELEGRAM_API_HASH");

    /**
     * The phone number of the Telegram account, in international format (1XXXXXXXXXX)
     */
    private static final String PHONE_NUMBER = System.getenv("TELEGRAM_PHONE_NUMBER");

    private LogService logService;

    private SpringProfileService springProfileService;
    private TelegramAuthService telegramAuthService;

    private TelegramOutboundRelayService telegramOutboundRelayService;

    @Autowired
    public TelegramService(LogService logService,
                           SpringProfileService springProfileService,
                           TelegramAuthService telegramAuthService,
                           TelegramOutboundRelayService telegramOutboundRelayService) {
        this.logService = logService;
        this.springProfileService = springProfileService;
        this.telegramAuthService = telegramAuthService;
        this.telegramOutboundRelayService = telegramOutboundRelayService;
    }

    @PostConstruct
    public void init() {

        // Check if telegram is enabled
        if(API_ID == null || API_HASH == null || PHONE_NUMBER == null) {
            logService.logError(getClass(), "One or more Telegram environment variables were missing. Disabling Telegram.");
            return;
        }

        // Try to parse API ID as an integer.
        int apiID;
        try {
            apiID = Integer.parseInt(API_ID);
        } catch (NumberFormatException ignored) {
            logService.logError(getClass(), "Cannot parse API_ID as integer: " + API_ID);
            return;
        }

        // Set Logging Level
        if (springProfileService.isProfileActive(SpringProfileService.Profile.DEV))
            BotLogger.setLevel(Level.ALL);

        // Create Telegram Context
        TelegramContext telegramContext = new TelegramContext();
        telegramContext.setTelegramService(this);
        telegramContext.setTelegramOutboundRelayService(telegramOutboundRelayService);
        telegramContext.setPhoneNumber(PHONE_NUMBER);
        telegramContext.setApiId(apiID);
        telegramContext.setApiHash(API_HASH);

        // Create Telegram Bot
        telegramContext.setBot(
                new TelegramBot(
                        new BotConfigImpl(telegramContext),
                        new ChatUpdatesBuilderImpl(telegramContext),
                        telegramContext.getApiId(),
                        telegramContext.getApiHash()
                )
        );

        // Sign into Telegram.
        telegramAuthService.signIn(telegramContext);
    }

}
