package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.telegram.impl.BotConfigImpl;
import com.mitchtalmadge.cryptex.service.telegram.impl.ChatUpdatesBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class TelegramSignInService {

    private LogService logService;

    @Autowired
    public TelegramSignInService(LogService logService) {
        this.logService = logService;
    }

    /**
     * Attempts to sign into Telegram.
     * @param apiID The api_id.
     * @param apiHash The api_hash.
     * @param phoneNumber The phone number of the user to sign into.
     * @return A future that will contain the bot instance if sign-in was successful, or null if it was not.
     */
    @Async
    public CompletableFuture<TelegramBot> signIn(int apiID, String apiHash, String phoneNumber) {

        // Create bot
        TelegramBot telegramBot = new TelegramBot(new BotConfigImpl(phoneNumber), new ChatUpdatesBuilderImpl(), apiID, apiHash);

        // Credit: https://github.com/rubenlagus/Deepthought/blob/master/Deepthought/src/main/java/org/telegram/Deepthought.java
        try {
            LoginStatus status = telegramBot.init();

            if (status == LoginStatus.CODESENT) {
                Scanner in = new Scanner(System.in);
                boolean success = telegramBot.getKernelAuth().setAuthCode(in.nextLine().trim());
                if (success) {
                    status = LoginStatus.ALREADYLOGGED;
                }
            }
            if (status == LoginStatus.ALREADYLOGGED) {
                telegramBot.startBot();
                return CompletableFuture.completedFuture(telegramBot);
            } else {
                logService.logError(getClass(), "Cannot sign into Telegram; status code: " + status);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logService.logException(getClass(), e, "Failed to initialize Telegram Service.");
        }

        return CompletableFuture.completedFuture(null);
    }

}
