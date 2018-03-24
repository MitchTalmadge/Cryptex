package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
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

    private TelegramContext telegramContext;

    private final IUsersHandler usersHandler;
    private final IChatsHandler chatsHandler;

    /**
     * Creates an Updates Handler.
     * @param telegramContext A Telegram Context containing a Database Manager.
     * @param kernelComm The Kernel Comm instance.
     * @param differenceParametersService The Difference Parameters Service instance.
     */
    UpdatesHandlerImpl(TelegramContext telegramContext, IKernelComm kernelComm, IDifferenceParametersService differenceParametersService) {
        super(kernelComm, differenceParametersService, telegramContext.getDatabaseManager());
        this.telegramContext = telegramContext;

        usersHandler = new UsersHandlerImpl(telegramContext);
        chatsHandler = new ChatsHandlerImpl(telegramContext);
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
        telegramContext.getTelegramService().messageReceived(telegramContext, message);
    }

    @Override
    protected void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update) {
        telegramContext.getTelegramService().messageReceived(telegramContext, update.getMessage());
    }

    @Override
    protected void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
        telegramContext.getTelegramService().messageReceived(telegramContext, update.getMessage());
    }

}