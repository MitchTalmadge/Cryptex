package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.service.telegram.TelegramService;
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
    private DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();

    private TelegramService telegramService;

    public ChatUpdatesBuilderImpl(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

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
        return new UpdatesHandlerImpl(telegramService, iKernelComm, iDifferenceParametersService, databaseManager, new UsersHandlerImpl(databaseManager), new ChatsHandlerImpl(databaseManager));
    }
}