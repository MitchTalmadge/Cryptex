package com.mitchtalmadge.cryptex.domain.entity.telegram;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
     * The creation time of the last seen message for this chat.
     */
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    public TelegramChatHistoryEntity() {
    }

    /**
     * Constructs a Telegram chat history record.
     * @param phoneNumber The phone number of the account associated with this history.
     * @param chatId The ID of the chat associated with this history.
     * @param lastMessageTime The creation time of the last message received.
     */
    public TelegramChatHistoryEntity(String phoneNumber, int chatId, LocalDateTime lastMessageTime) {
        this.phoneNumber = phoneNumber;
        this.chatId = chatId;
        this.lastMessageTime = lastMessageTime;
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

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramChatHistoryEntity that = (TelegramChatHistoryEntity) o;
        return chatId == that.chatId &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(lastMessageTime, that.lastMessageTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(phoneNumber, chatId, lastMessageTime);
    }
}
