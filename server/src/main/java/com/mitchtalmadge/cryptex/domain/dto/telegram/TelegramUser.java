package com.mitchtalmadge.cryptex.domain.dto.telegram;

import org.telegram.api.user.TLUser;
import org.telegram.bot.structure.IUser;

/**
 * Represents a Telegram user.
 */
public class TelegramUser implements IUser {

    private int userId;

    private Long userHash;

    /**
     * Constructs a telegram user instance from the provided TLUser
     *
     * @param user The user to copy details from.
     */
    public TelegramUser(TLUser user) {
        userId = user.getId();
        userHash = user.getAccessHash();
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public Long getUserHash() {
        return userHash;
    }

}
