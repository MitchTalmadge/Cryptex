package com.mitchtalmadge.cryptex.service.mail;

import com.mitchtalmadge.cryptex.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

@Service
public class MailReceiveService {

    /**
     * The email server to connect to.
     */
    private static final String IMAP_HOST = System.getenv("IMAP_HOST");

    /**
     * The username of the email account to sign in with.
     */
    private static final String IMAP_USERNAME = System.getenv("IMAP_USERNAME");

    /**
     * The password for the email account.
     */
    private static final String IMAP_PASSWORD = System.getenv("IMAP_PASSWORD");
    private LogService logService;
    private Set<MailListener> mailListeners;

    @Autowired
    public MailReceiveService(LogService logService, Set<MailListener> mailListeners) {
        this.logService = logService;
        this.mailListeners = mailListeners;
    }

    /**
     * Checks for mail every 15 seconds.
     */
    @Async
    @Scheduled(fixedDelay = 15_000)
    protected void checkMail() throws NoSuchProviderException {
        // IMAP Properties
        Properties properties = new Properties();
        properties.put("mail.imap.host", IMAP_HOST);
        properties.put("mail.imap.port", "143");
        properties.put("mail.imap.connectiontimeout", "15000");
        properties.put("mail.imap.timeout", "15000");

        // Mail Session
        Session mailSession = Session.getDefaultInstance(properties);

        // Mail Store
        Store mailStore = mailSession.getStore("imap");

        // Connect
        try {
            mailStore.connect(IMAP_HOST, IMAP_USERNAME, IMAP_PASSWORD);
        } catch (MessagingException e) {
            logService.logError(getClass(), "Cannot sign in: " + e.getMessage());
            return;
        }

        // Get inbox
        Folder inboxFolder;
        try {
            inboxFolder = mailStore.getFolder("Inbox");
            inboxFolder.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            logService.logError(getClass(), "Cannot open inbox: " + e.getMessage());
            return;
        }

        // Get messages from inbox
        try {
            Message[] messages = inboxFolder.getMessages();

            // Filter unread messages
            Message[] unreadMessages = new Message[inboxFolder.getUnreadMessageCount()];
            int count = 0;
            for (Message message : messages)
                if (!message.getFlags().contains(Flags.Flag.SEEN))
                    unreadMessages[count++] = message;

            // Print out new messages
            for (Message message : unreadMessages) {
                logService.logInfo(getClass(), "New Message Received.");
                logService.logInfo(getClass(), "Subject: " + message.getSubject());
                logService.logInfo(getClass(), "From: " + message.getFrom()[0].toString());
            }

            // Notify Mail Listeners
            if (unreadMessages.length > 0) {
                for (MailListener listener : mailListeners) {
                    listener.readMail(unreadMessages);
                }
            }

            // Mark all as read
            inboxFolder.setFlags(unreadMessages, new Flags(Flags.Flag.SEEN), true);
        } catch (MessagingException e) {
            logService.logError(getClass(), "Cannot read inbox messages: " + e.getMessage());
            return;
        }

        // Close mail client
        try {
            inboxFolder.close(false);
            mailStore.close();
        } catch (MessagingException e) {
            logService.logError(getClass(), "Cannot close mail client: " + e.getMessage());
        }
    }

}
