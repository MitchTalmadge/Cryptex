package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.domain.entity.telegram.TelegramChatHistoryEntity;
import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.discord.DiscordInboundRelayService;
import com.mitchtalmadge.cryptex.service.entity.telegram.TelegramChatHistoryEntityRepository;
import com.mitchtalmadge.cryptex.util.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.api.message.TLMessage;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * This service allows relaying messages from Telegram to external sources.
 */
@Service
public class TelegramOutboundRelayService {

    /**
     * The ID of the Discord channel to relay calls to.
     * TODO: Store this info in the database, created via web interface
     */
    private static final String DISCORD_CHANNEL_ID = System.getenv("TELEGRAM_DISCORD_CHANNEL_ID");

    private LogService logService;
    private TelegramChatHistoryEntityRepository telegramChatHistoryEntityRepository;
    private DiscordInboundRelayService discordInboundRelayService;

    @Autowired
    public TelegramOutboundRelayService(LogService logService,
                                        TelegramChatHistoryEntityRepository telegramChatHistoryEntityRepository,
                                        DiscordInboundRelayService discordInboundRelayService) {
        this.logService = logService;
        this.telegramChatHistoryEntityRepository = telegramChatHistoryEntityRepository;
        this.discordInboundRelayService = discordInboundRelayService;
    }

    /**
     * Called when a Telegram message is received.
     *
     * @param telegramContext The context related to the message.
     * @param message         The message received.
     */
    public void messageReceived(TelegramContext telegramContext, TLMessage message) {

        // Check chat history to see if we have seen this message.
        TelegramChatHistoryEntity history = telegramChatHistoryEntityRepository.findFirstByPhoneNumberAndChatId(telegramContext.getPhoneNumber(), message.getChatId());
        if (history == null) {
            // Create a new chat history entity.
            history = new TelegramChatHistoryEntity(telegramContext.getPhoneNumber(), message.getChatId(), LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.UTC));
            telegramChatHistoryEntityRepository.save(history);
        } else {
            // Skip this message if we have already seen it.
            if (history.getLastMessageTime().isAfter(LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.UTC))) {
                logService.logInfo(getClass(), "Skipping relay of message ID " + message.getId() + " in chat ID " + message.getChatId() + " as it is too old.");
                return;
            }

            // Update last seen message id.
            history.setLastMessageTime(LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.UTC));
            telegramChatHistoryEntityRepository.save(history);
        }

        // TODO: check image

        // Ignore empty messages
        if (message.getMessage().isEmpty())
            return;

        logService.logInfo(getClass(), "Relaying message from chat ID " + message.getChatId());
        logService.logDebug(getClass(), "Message: " + message.getMessage());

        // Relay to Discord.
        discordInboundRelayService.notifyEveryone(DISCORD_CHANNEL_ID);
        discordInboundRelayService.relayMessage(message.getMessage(), "Call Made", DISCORD_CHANNEL_ID, true, true);
    }

}
