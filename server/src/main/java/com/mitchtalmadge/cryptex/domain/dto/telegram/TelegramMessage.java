package com.mitchtalmadge.cryptex.domain.dto.telegram;

import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TelegramMessage {

    /**
     * The ID of the message.
     */
    private int id;

    /**
     * The ID of the chat room.
     */
    private int chatId;

    /**
     * The contents of the message.
     */
    private String contents;

    /**
     * The date the message was created.
     */
    private LocalDateTime creationDate;

    /**
     * The original message object used to construct this instance.
     * May be one of {@link TLMessage} or {@link TLUpdateShortMessage}.
     */
    private Object original;

    /**
     * Creates a TelegramMessage from a {@link TLMessage}.
     * @param message The message.
     */
    public TelegramMessage(TLMessage message) {
        this.id = message.getId();
        this.chatId = message.getChatId();
        this.contents = message.getMessage();
        this.creationDate = LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.UTC);
        this.original = message;
    }

    /**
     * Creates a TelegramMessage from a {@link TLUpdateShortMessage}.
     * @param message The message.
     */
    public TelegramMessage(TLUpdateShortMessage message) {
        this.id = message.getId();
        this.chatId = message.getUserId();
        this.contents = message.getMessage();
        this.creationDate = LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.UTC);
        this.original = message;
    }

    public int getId() {
        return id;
    }

    public int getChatId() {
        return chatId;
    }

    public String getContents() {
        return contents;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Object getOriginal() {
        return original;
    }
}
