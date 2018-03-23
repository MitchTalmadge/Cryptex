package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.domain.entity.FileEntity;
import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.entity.FileEntityRepository;
import com.mitchtalmadge.cryptex.service.mail.MailListener;
import com.mitchtalmadge.cryptex.service.telegram.impl.BotConfigImpl;
import com.mitchtalmadge.cryptex.service.telegram.impl.ChatUpdatesBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages authentication and de-authentication with Telegram.
 */
@Service
public class TelegramAuthService implements MailListener {

    /**
     * The name of the auth file in the running directory.
     */
    private static final String AUTH_FILE_NAME = "telegram.auth";

    /**
     * The TelegramBot instance.
     */
    private TelegramBot telegramBot;

    /**
     * The current login status.
     */
    private LoginStatus loginStatus;

    private LogService logService;

    private FileEntityRepository fileEntityRepository;

    @Autowired
    public TelegramAuthService(LogService logService, FileEntityRepository fileEntityRepository) {
        this.logService = logService;
        this.fileEntityRepository = fileEntityRepository;
    }

    @PostConstruct
    private void init() {
        restoreAuthFromDatabase();
    }

    /**
     * Writes the contents of the auth file to the database.
     */
    private void writeAuthToDatabase() {
        File authFile = new File(AUTH_FILE_NAME);
        if (!authFile.exists()) {
            logService.logError(getClass(), "Tried to write auth file to database, but it did not exist.");
            return;
        }

        try (FileInputStream inputStream = new FileInputStream(authFile)) {

            // Read contents to array.
            byte[] contents = new byte[inputStream.available()];
            int readBytes = inputStream.read(contents);
            if (readBytes != contents.length) {
                logService.logError(getClass(), "Failed to read entire auth file.");
                return;
            }

            // Store contents in database.
            List<FileEntity> fileEntities = fileEntityRepository.findByName(AUTH_FILE_NAME);
            if (fileEntities.size() > 0) {
                // Update existing record.
                FileEntity fileEntity = fileEntities.get(0);
                fileEntity.setContents(contents);
                fileEntityRepository.save(fileEntity);
            } else {
                // Create new record.
                FileEntity fileEntity = new FileEntity(AUTH_FILE_NAME, contents);
                fileEntityRepository.save(fileEntity);
            }

            logService.logInfo(getClass(), "Wrote auth file to database.");

        } catch (IOException e) {
            logService.logException(getClass(), e, "Could not read auth file contents to database.");
        }
    }

    /**
     * Restores the contents of the auth file from the database.
     * If a file is not stored in the database, nothing is written.
     */
    private void restoreAuthFromDatabase() {
        List<FileEntity> fileEntities = fileEntityRepository.findByName(AUTH_FILE_NAME);
        if (fileEntities.size() > 0) {
            byte[] contents = fileEntities.get(0).getContents();
            File authFile = new File(AUTH_FILE_NAME);

            // Delete existing auth file.
            if (authFile.exists()) {
                boolean deleted = authFile.delete();
                if (!deleted) {
                    logService.logError(getClass(), "Could not delete auth file: returned false.");
                    return;
                }
            }

            // Create new auth file.
            try {
                boolean created = authFile.createNewFile();
                if (!created) {
                    logService.logError(getClass(), "Could not create auth file from database: returned false.");
                    return;
                }
            } catch (IOException e) {
                logService.logException(getClass(), e, "Could not create auth file from database.");
                return;
            }

            // Write contents to file.
            try (FileOutputStream outputStream = new FileOutputStream(authFile)) {
                outputStream.write(contents);
            } catch (IOException e) {
                logService.logException(getClass(), e, "Could not write database contents to auth file.");
            }

            logService.logInfo(getClass(), "Loaded auth file from database.");
        }
    }

    /**
     * Attempts to sign into Telegram.
     *
     * @param apiID       The api_id.
     * @param apiHash     The api_hash.
     * @param phoneNumber The phone number of the user to sign into.
     */
    @Async
    public void signIn(TelegramService telegramService, int apiID, String apiHash, String phoneNumber) {

        // Create bot
        telegramBot = new TelegramBot(new BotConfigImpl(phoneNumber), new ChatUpdatesBuilderImpl(telegramService), apiID, apiHash);
        telegramBot.getConfig().setAuthfile(AUTH_FILE_NAME);

        try {
            // Initialize bot and check status.
            loginStatus = telegramBot.init();

            // Check if we can start the bot right away.
            if (loginStatus == LoginStatus.ALREADYLOGGED) {
                telegramBot.startBot();
                writeAuthToDatabase();
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

                        // Extract body
                        List<String> bodyParts = new ArrayList<>();
                        Object contents = message.getContent();
                        if (contents instanceof String) {
                            bodyParts.add((String) contents);
                        } else if (contents instanceof Multipart) {
                            for (int i = 0; i < ((Multipart) contents).getCount(); i++) {
                                bodyParts.add(((Multipart) contents).getBodyPart(i).toString());
                            }
                        }

                        // Examine each body part
                        for (String bodyPart : bodyParts) {
                            // Determine if this text was from Telegram.
                            int codeIndex = bodyPart.indexOf("Telegram code");
                            if (codeIndex != -1) {
                                // Extract code; "Telegram code" is 14 characters, code itself is another 5.
                                String code = bodyPart.substring(codeIndex + 14, codeIndex + 14 + 5);

                                logService.logInfo(getClass(), "Found Telegram code: " + code);

                                // Apply code.
                                telegramBot.getKernelAuth().setAuthCode(code);
                                loginStatus = LoginStatus.ALREADYLOGGED;

                                // Start bot.
                                telegramBot.startBot();
                                writeAuthToDatabase();
                                return;
                            }
                        }
                    }
                } catch (MessagingException | IOException e) {
                    logService.logException(getClass(), e, "Could not read message.");
                }
            }
        }
    }
}
