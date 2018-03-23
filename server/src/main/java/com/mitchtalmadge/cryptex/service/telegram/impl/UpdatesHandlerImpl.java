package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.update.TLUpdateChannel;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

/**
 * Updates handler implementation. This is where message events, etc. are received.
 */
public class UpdatesHandlerImpl extends DefaultUpdatesHandler {

    private TelegramService telegramService;

    UpdatesHandlerImpl(TelegramService telegramService, IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, DatabaseManager databaseManager) {
        super(kernelComm, differenceParametersService, databaseManager);
        this.telegramService = telegramService;
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