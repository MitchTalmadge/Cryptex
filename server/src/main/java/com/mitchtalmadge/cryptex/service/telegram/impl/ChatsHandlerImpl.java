package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramChat;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.bot.handlers.interfaces.IChatsHandler;

import java.util.List;

public class ChatsHandlerImpl implements IChatsHandler {

    private DatabaseManagerImpl databaseManager;

    public ChatsHandlerImpl(DatabaseManagerImpl databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onChats(List<TLAbsChat> chats) {
        chats.forEach(chat -> databaseManager.storeChat(new TelegramChat(chat)));
    }
}
