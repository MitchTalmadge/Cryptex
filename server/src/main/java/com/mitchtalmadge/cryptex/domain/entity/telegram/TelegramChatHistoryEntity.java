package com.mitchtalmadge.cryptex.domain.entity.telegram;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Represents history in a Telegram chat room.
 */
@Entity(name = "telegram_chat_history")
public class TelegramChatHistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * The phone number of the account associated with this history.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * The ID of the chat associated with this history.
     */
    @Column(name = "chat_id")
    private int chatId;

    /**
     * The ID of the last seen message for this chat.
     */
    @Column(name = "last_message_id")
    private int lastMessageId;

    public TelegramChatHistoryEntity() {
    }

    /**
     * Constructs a Telegram chat history record.
     * @param phoneNumber The phone number of the account associated with this history.
     * @param chatId The ID of the chat associated with this history.
     * @param lastMessageId The ID of the last seen message for this chat.
     */
    public TelegramChatHistoryEntity(String phoneNumber, int chatId, int lastMessageId) {
        this.phoneNumber = phoneNumber;
        this.chatId = chatId;
        this.lastMessageId = lastMessageId;
    }

    public Long getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramChatHistoryEntity that = (TelegramChatHistoryEntity) o;
        return chatId == that.chatId &&
                lastMessageId == that.lastMessageId &&
                Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, chatId, lastMessageId);
    }
}
