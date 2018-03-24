package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.TLMessageService;
import org.telegram.api.message.action.TLMessageActionChannelCreate;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

import java.util.*;

/**
 * Updates handler implementation. This is where message events, etc. are received.
 */
public class UpdatesHandlerImpl extends DefaultUpdatesHandler {

    private TelegramContext telegramContext;

    private final IUsersHandler usersHandler;
    private final IChatsHandler chatsHandler;

    /**
     * Maps IDs of chats which are currently in overflow protection mode to the last received message.
     * This facilitates the ability to discard all but the last-sent message in a chat.
     * <p>
     * The intent is to prevent mass message spam for newly joined channels,
     * or channels which have not been visited in a while.
     */
    private final Map<Integer, TLMessage> overflowProtectionMap = new HashMap<>();

    /**
     * Creates an Updates Handler.
     *
     * @param telegramContext             A Telegram Context containing a Database Manager.
     * @param kernelComm                  The Kernel Comm instance.
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

        // Send any messages in the overflow protection map.
        overflowProtectionMap.values().forEach(message -> {
            if(message != null) {
                telegramContext.getTelegramService().messageReceived(telegramContext, message);
            }
        });

        // Clear overflow protection map.
        overflowProtectionMap.clear();
    }

    @Override
    protected void onTLAbsMessageCustom(TLAbsMessage message) {
        // If this is the first message received in this channel for this session...
        if (message instanceof TLMessageService) {
            if (((TLMessageService) message).getAction() instanceof TLMessageActionChannelCreate) {
                overflowProtectionMap.put(message.getChatId(), null);
            }
        } else if (message instanceof TLMessage) {
            // Check if we are in overflow protection mode
            if (overflowProtectionMap.containsKey(message.getChatId())) {
                overflowProtectionMap.put(message.getChatId(), (TLMessage) message);
            } else {
                telegramContext.getTelegramService().messageReceived(telegramContext, (TLMessage) message);
            }
        }
    }


    @Override
    protected void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update) {
        if (update.getMessage() instanceof TLMessage)
            telegramContext.getTelegramService().messageReceived(telegramContext, (TLMessage) update.getMessage());
    }

    @Override
    protected void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
        if (update.getMessage() instanceof TLMessage)
            telegramContext.getTelegramService().messageReceived(telegramContext, (TLMessage) update.getMessage());
    }

}