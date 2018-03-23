package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.update.*;
import org.telegram.api.update.encrypted.TLUpdateEncryptedMessagesRead;
import org.telegram.api.update.encrypted.TLUpdateNewEncryptedMessage;
import org.telegram.api.updates.TLUpdateShortChatMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.updates.TLUpdateShortSentMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

import java.util.List;

/**
 * Updates handler implementation. This is where message events, etc. are received.
 */
public class UpdatesHandlerImpl extends DefaultUpdatesHandler {

    private TelegramService telegramService;
    private IUsersHandler usersHandler;
    private final IChatsHandler chatsHandler;

    UpdatesHandlerImpl(TelegramService telegramService,
                       IKernelComm kernelComm,
                       IDifferenceParametersService differenceParametersService,
                       DatabaseManager databaseManager,
                       IUsersHandler usersHandler,
                       IChatsHandler chatsHandler) {
        super(kernelComm, differenceParametersService, databaseManager);
        this.telegramService = telegramService;
        this.usersHandler = usersHandler;
        this.chatsHandler = chatsHandler;
    }

    @Override
    protected void onUsersCustom(List<TLAbsUser> users) {
        usersHandler.onUsers(users);
    }

    @Override
    protected void onChatsCustom(List<TLAbsChat> chats) {
        chatsHandler.onChats(chats);
    }

    @Override
    protected void onTLAbsMessageCustom(TLAbsMessage message) {
        telegramService.messageReceived(message);
    }

    @Override
    protected void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update) {
        telegramService.messageReceived(update.getMessage());
    }

    @Override
    protected void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
        telegramService.messageReceived(update.getMessage());
    }

}