package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.kernel.TelegramBot;

import javax.annotation.PostConstruct;
import java.util.concurrent.Future;

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

    private TelegramSignInService telegramSignInService;

    @Autowired
    public TelegramService(LogService logService, TelegramSignInService telegramSignInService) {
        this.logService = logService;
        this.telegramSignInService = telegramSignInService;
    }

    @PostConstruct
    public void init() {
        // Try to parse API ID as an integer.
        int apiID;
        try {
            apiID = Integer.parseInt(API_ID);
        } catch (NumberFormatException ignored) {
            logService.logError(getClass(), "Cannot parse API_ID as integer: " + API_ID);
            return;
        }

        // Sign into Telegram.
        Future<TelegramBot> telegramBotFuture = telegramSignInService.signIn(apiID, API_HASH, PHONE_NUMBER);
    }

}
