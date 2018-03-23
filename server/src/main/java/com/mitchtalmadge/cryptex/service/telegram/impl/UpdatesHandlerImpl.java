package com.mitchtalmadge.cryptex.service.telegram.impl;

import org.telegram.api.message.TLAbsMessage;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

/**
 * Updates handler implementation. This is where message events, etc. are received.
 */
public class UpdatesHandlerImpl extends DefaultUpdatesHandler {

    UpdatesHandlerImpl(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, DatabaseManager databaseManager) {
        super(kernelComm, differenceParametersService, databaseManager);
    }

    @Override
    protected void onTLAbsMessageCustom(TLAbsMessage message) {
    }

}