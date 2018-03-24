package com.mitchtalmadge.cryptex.domain.dto.telegram;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.TLChatForbidden;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelForbidden;
import org.telegram.bot.structure.Chat;

/**
 * Represents a Telegram chat.
 */
public class TelegramChat implements Chat {

    private int id;
    private Long accessHash;
    private boolean isChannel;

    /**
     * Constructs a Telegram chat instance from a TLAbsChat instance.
     * @param chat The chat to copy from.
     */
    public TelegramChat(TLAbsChat chat) {
        this.id = chat.getId();

        // Determine chat type.
        if(chat instanceof TLChannel) {
            if(((TLChannel) chat).hasAccessHash())
                this.accessHash = ((TLChannel) chat).getAccessHash();
            this.isChannel = true;
        } else if(chat instanceof TLChannelForbidden) {
            this.accessHash = ((TLChannelForbidden) chat).getAccessHash();
            this.isChannel = true;
        } else if(chat instanceof TLChat || chat instanceof TLChatForbidden) {
            this.isChannel = false;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Long getAccessHash() {
        return accessHash;
    }

    @Override
    public boolean isChannel() {
        return isChannel;
    }
}
