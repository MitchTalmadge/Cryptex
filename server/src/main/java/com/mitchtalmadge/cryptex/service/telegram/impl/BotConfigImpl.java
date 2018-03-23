package com.mitchtalmadge.cryptex.service.telegram.impl;

import org.telegram.bot.structure.BotConfig;

/**
 * Bot config implementation that acts as a Telegram client, not a bot.
 */
public class BotConfigImpl extends BotConfig {

    /**
     * The phone number of the client.
     */
    private String phoneNumber;

    /**
     * @param phoneNumber The phone number of the client.
     */
    public BotConfigImpl(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
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