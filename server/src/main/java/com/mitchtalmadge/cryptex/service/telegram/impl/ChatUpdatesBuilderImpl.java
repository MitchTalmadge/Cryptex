package com.mitchtalmadge.cryptex.service.telegram.impl;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
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

    private TelegramContext telegramContext;
    private IKernelComm iKernelComm;
    private IDifferenceParametersService iDifferenceParametersService;

    public ChatUpdatesBuilderImpl(TelegramContext telegramContext) {
        this.telegramContext = telegramContext;
        telegramContext.setDatabaseManager(new DatabaseManagerImpl());
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
        return telegramContext.getDatabaseManager();
    }

    @Override
    public UpdatesHandlerBase build() {
        return new UpdatesHandlerImpl(telegramContext, iKernelComm, iDifferenceParametersService);
    }
}