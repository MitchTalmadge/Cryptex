package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramMessage;
import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.engine.RpcException;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.TLMessageService;
import org.telegram.api.message.action.TLMessageActionChannelCreate;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Updates handler implementation. This is where message events, etc. are received.
 */
public class UpdatesHandlerImpl extends DefaultUpdatesHandler {

    private TelegramContext telegramContext;

    private final IUsersHandler usersHandler;
    private final IChatsHandler chatsHandler;

    /**
     * Maps IDs of chats which are currently in overflow protection mode to the time the first message of the channel was received for this session.
     * This facilitates the ability to discard all but the last-sent message in a chat.
     * <p>
     * The intent is to prevent mass message spam for newly joined channels,
     * or channels which have not been visited in a while.
     */
    private final Map<Integer, LocalDateTime> overflowProtectionMap = new HashMap<>();

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
    }

    @Override
    protected void onTLAbsMessageCustom(TLAbsMessage message) {
        // If this is the first message received in this channel for this session...
        if (message instanceof TLMessageService) {
            if (((TLMessageService) message).getAction() instanceof TLMessageActionChannelCreate) {
                // Store current time.
                overflowProtectionMap.put(message.getChatId(), LocalDateTime.now().minusMinutes(1));
            }
        } else if (message instanceof TLMessage) {
            // Check if we are in overflow protection mode
            if (overflowProtectionMap.containsKey(message.getChatId())) {
                // Check if this message was sent after 1 minute before the overflow time.
                if (overflowProtectionMap.get(message.getChatId()).isBefore(LocalDateTime.ofEpochSecond(((TLMessage) message).getDate(), 0, ZoneOffset.UTC))) {
                    // Done with overflow protection.
                    overflowProtectionMap.remove(message.getChatId());
                    handleMessage(new TelegramMessage((TLMessage) message));
                }
            } else {
                handleMessage(new TelegramMessage((TLMessage) message));
            }
        }
    }

    @Override
    protected void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update) {
        if (update.getMessage() instanceof TLMessage)
            handleMessage(new TelegramMessage((TLMessage) update.getMessage()));
    }

    @Override
    protected void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
        if (update.getMessage() instanceof TLMessage)
            handleMessage(new TelegramMessage((TLMessage) update.getMessage()));
    }

    @Override
    protected void onTLUpdateShortMessageCustom(TLUpdateShortMessage update) {
        handleMessage(new TelegramMessage(update));
    }

    /**
     * Relays and reads a message.
     *
     * @param message The message.
     */
    private void handleMessage(TelegramMessage message) {
        // Relay message
        telegramContext.getTelegramOutboundRelayService().messageReceived(telegramContext, message);

        // Read message
        Chat chat = telegramContext.getDatabaseManager().getChatById(message.getChatId());
        if (chat != null) {
            try {
                telegramContext.getBot().getKernelComm().performMarkGroupAsRead(chat, message.getId());
            } catch (RpcException ignored) {
            }
        } else {
            IUser user = telegramContext.getDatabaseManager().getUserById(message.getChatId());
            if (user != null) {
                try {
                    telegramContext.getBot().getKernelComm().performMarkAsRead(user, message.getId());
                } catch (RpcException ignored) {
                }
            }
        }
    }
}