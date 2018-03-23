package com.mitchtalmadge.cryptex.service.telegram.impl;

import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

/**
 * Chat updates builder implementation.
 */
public class ChatUpdatesBuilderImpl implements ChatUpdatesBuilder {

    private IKernelComm iKernelComm;
    private IDifferenceParametersService iDifferenceParametersService;
    private DatabaseManager databaseManager = new DatabaseManagerImpl();

    @Override
    public void setKernelComm(IKernelComm iKernelComm) {
        this.iKernelComm = iKernelComm;
    }

    @Override
    public void setDifferenceParametersService(IDifferenceParametersService iDifferenceParametersService) {
        this.iDifferenceParametersService = iDifferenceParametersService;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public UpdatesHandlerBase build() {
        return new UpdatesHandlerImpl(iKernelComm, iDifferenceParametersService, databaseManager);
    }
}