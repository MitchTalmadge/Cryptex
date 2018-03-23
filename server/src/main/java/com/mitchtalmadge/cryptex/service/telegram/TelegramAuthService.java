package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.mail.MailListener;
import com.mitchtalmadge.cryptex.service.telegram.impl.BotConfigImpl;
import com.mitchtalmadge.cryptex.service.telegram.impl.ChatUpdatesBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import javax.annotation.PreDestroy;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Manages authentication and de-authentication with Telegram.
 */
@Service
public class TelegramAuthService implements MailListener {

    /**
     * The TelegramBot instance.
     */
    private TelegramBot telegramBot;

    /**
     * The current login status.
     */
    private LoginStatus loginStatus;

    private LogService logService;

    @Autowired
    public TelegramAuthService(LogService logService) {
        this.logService = logService;
    }

    /**
     * Attempts to sign into Telegram.
     *
     * @param apiID       The api_id.
     * @param apiHash     The api_hash.
     * @param phoneNumber The phone number of the user to sign into.
     */
    @Async
    public void signIn(int apiID, String apiHash, String phoneNumber) {

        // Create bot
        telegramBot = new TelegramBot(new BotConfigImpl(phoneNumber), new ChatUpdatesBuilderImpl(), apiID, apiHash);
        telegramBot.getConfig().setAuthfile("telegram.auth");

        try {
            // Initialize bot and check status.
            loginStatus = telegramBot.init();

            // Check if we can start the bot right away.
            if (loginStatus == LoginStatus.ALREADYLOGGED) {
                telegramBot.startBot();
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logService.logException(getClass(), e, "Failed to initialize Telegram Service.");
        }
    }

    @Override
    public void readMail(Message[] unreadMessages) {
        // Check if we must search for a code.
        if (telegramBot != null && loginStatus == LoginStatus.CODESENT) {

            // Search for code
            for (Message message : unreadMessages) {
                try {
                    // Determine if email is from TextNow.
                    if (message.getSubject().startsWith("TextNow")) {
                        // Determine if this text was from Telegram.
                        int codeIndex = message.getContent().toString().indexOf("Telegram code");
                        if (codeIndex != -1) {
                            // Extract code; "Telegram code" is 14 characters, code itself is another 5.
                            String code = message.getContent().toString().substring(codeIndex + 14, codeIndex + 14 + 5);

                            logService.logInfo(getClass(), "Found Telegram code: " + code);

                            // Apply code.
                            telegramBot.getKernelAuth().setAuthCode(code);
                            loginStatus = LoginStatus.ALREADYLOGGED;

                            // Start bot.
                            telegramBot.startBot();
                            return;
                        }
                    }
                } catch (MessagingException | IOException e) {
                    logService.logException(getClass(), e, "Could not read message.");
                }
            }
        }
    }
}
