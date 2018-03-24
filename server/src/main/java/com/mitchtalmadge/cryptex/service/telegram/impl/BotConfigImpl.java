package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import org.telegram.bot.structure.BotConfig;

/**
 * Bot config implementation that acts as a Telegram client, not a bot.
 */
public class BotConfigImpl extends BotConfig {

    private TelegramContext telegramContext;

    /**
     * @param telegramContext A TelegramContext containing a phone number.
     */
    public BotConfigImpl(TelegramContext telegramContext) {
        this.telegramContext = telegramContext;
    }

    @Override
    public String getPhoneNumber() {
        return telegramContext.getPhoneNumber();
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }
}