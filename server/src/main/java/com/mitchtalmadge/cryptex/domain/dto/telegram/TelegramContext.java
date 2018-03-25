package com.mitchtalmadge.cryptex.domain.dto.telegram;

import com.mitchtalmadge.cryptex.service.telegram.TelegramOutboundRelayService;
import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
import com.mitchtalmadge.cryptex.service.telegram.impl.DatabaseManagerImpl;
import org.telegram.bot.kernel.TelegramBot;

/**
 * Contains useful objects for a Telegram bot/client context.
 */
public class TelegramContext {

    /**
     * The TelegramService instance.
     */
    private TelegramService telegramService;

    /**
     * The TelegramOutboundRelayService instance.
     */
    private TelegramOutboundRelayService telegramOutboundRelayService;

    /**
     * The phone number of the Telegram user.
     */
    private String phoneNumber;

    /**
     * The api_id of the Telegram app.
     */
    private int apiId;

    /**
     * The api_hash of the Telegram app.
     */
    private String apiHash;

    /**
     * The Telegram bot instance.
     */
    private TelegramBot bot;

    /**
     * The database manager instance.
     */
    private DatabaseManagerImpl databaseManager;

    public TelegramService getTelegramService() {
        return telegramService;
    }

    public void setTelegramService(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public TelegramOutboundRelayService getTelegramOutboundRelayService() {
        return telegramOutboundRelayService;
    }

    public void setTelegramOutboundRelayService(TelegramOutboundRelayService telegramOutboundRelayService) {
        this.telegramOutboundRelayService = telegramOutboundRelayService;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public String getApiHash() {
        return apiHash;
    }

    public void setApiHash(String apiHash) {
        this.apiHash = apiHash;
    }

    public TelegramBot getBot() {
        return bot;
    }

    public void setBot(TelegramBot bot) {
        this.bot = bot;
    }

    public DatabaseManagerImpl getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(DatabaseManagerImpl databaseManager) {
        this.databaseManager = databaseManager;
    }
}
