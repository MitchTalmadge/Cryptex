package com.mitchtalmadge.cryptex.service.mail;

import javax.mail.Message;

/**
 * Allows services to receive and process mail.
 */
public interface MailListener {

    /**
     * Reads and handles all unread messages as necessary.
     * @param unreadMessages All unread mail messages.
     */
    void readMail(Message[] unreadMessages);

}
