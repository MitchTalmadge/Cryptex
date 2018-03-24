package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramUser;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.handlers.interfaces.IUsersHandler;

import java.util.List;

/**
 * Handles new and updated users from the Telegram API.
 */
public class UsersHandlerImpl implements IUsersHandler {

    private TelegramContext telegramContext;

    UsersHandlerImpl(TelegramContext telegramContext) {
        this.telegramContext = telegramContext;
    }

    @Override
    public void onUsers(List<TLAbsUser> list) {
        // Store all users in database.
        list.forEach(user -> {
            if (user instanceof TLUser) {
                telegramContext.getDatabaseManager().storeUser(new TelegramUser((TLUser) user));
            }
        });
    }

}
