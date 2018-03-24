package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramChat;
import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.bot.handlers.interfaces.IChatsHandler;

import java.util.List;

public class ChatsHandlerImpl implements IChatsHandler {

    private TelegramContext telegramContext;

    public ChatsHandlerImpl(TelegramContext telegramContext) {
        this.telegramContext = telegramContext;
    }

    @Override
    public void onChats(List<TLAbsChat> chats) {
        chats.forEach(chat -> telegramContext.getDatabaseManager().storeChat(new TelegramChat(chat)));
    }
}
