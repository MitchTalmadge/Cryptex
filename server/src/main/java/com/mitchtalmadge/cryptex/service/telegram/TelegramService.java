package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.service.DiscordService;
import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.SpringProfileService;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.bot.services.BotLogger;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * This service signs into and maintains connection with Telegram.
 */
@Service
public class TelegramService {

    /**
     * The App api_id which can be obtained from https://my.telegram.org/apps
     */
    private static final String API_ID = System.getenv("TELEGRAM_API_ID");

    /**
     * The App api_hash which can be obtained from https://my.telegram.org/apps
     */
    private static final String API_HASH = System.getenv("TELEGRAM_API_HASH");

    /**
     * The phone number of the Telegram account, in international format (1XXXXXXXXXX)
     */
    private static final String PHONE_NUMBER = System.getenv("TELEGRAM_PHONE_NUMBER");

    /**
     * The ID of the Discord channel to relay calls to.
     */
    private static final String DISCORD_CHANNEL_ID = System.getenv("TELEGRAM_DISCORD_CHANNEL_ID");

    /**
     * IDs of messages that have already been relayed, to prevent duplication.
     * Maps chat IDs to message IDs.
     */
    private final Map<Integer, Set<Integer>> relayedMessageMap = new HashMap<>();

    private LogService logService;

    private SpringProfileService springProfileService;
    private TelegramAuthService telegramAuthService;

    private DiscordService discordService;

    @Autowired
    public TelegramService(LogService logService,
                           SpringProfileService springProfileService,
                           TelegramAuthService telegramAuthService,
                           DiscordService discordService) {
        this.logService = logService;
        this.springProfileService = springProfileService;
        this.telegramAuthService = telegramAuthService;
        this.discordService = discordService;
    }

    @PostConstruct
    public void init() {
        // Try to parse API ID as an integer.
        int apiID;
        try {
            apiID = Integer.parseInt(API_ID);
        } catch (NumberFormatException ignored) {
            logService.logError(getClass(), "Cannot parse API_ID as integer: " + API_ID);
            return;
        }

        // Set Logging Level
        if (springProfileService.isProfileActive(SpringProfileService.Profile.DEV))
            BotLogger.setLevel(Level.ALL);

        // Sign into Telegram.
        telegramAuthService.signIn(this, apiID, API_HASH, PHONE_NUMBER);
    }

    /**
     * Called when a message is received from Telegram.
     * Relays the message to Discord if applicable.
     *
     * @param message The message received.
     */
    public void messageReceived(TLAbsMessage message) {
        if (message instanceof TLMessage) {

            // Prevention of duplicate message relays
            if (relayedMessageMap.containsKey(message.getChatId())
                    && relayedMessageMap.get(message.getChatId()).contains(((TLMessage) message).getId()))
                return;

            // TODO: check image

            // Ignore empty messages
            if (((TLMessage) message).getMessage().isEmpty())
                return;

            logService.logInfo(getClass(), "New Message Received: " + ((TLMessage) message).getMessage());

            // TODO: split message by length (2k chars)

            // Send message to discord
            TextChannel discordChannel = discordService.getJDA().getTextChannelById(DISCORD_CHANNEL_ID);
            if (discordChannel != null) {
                // Build rich embed
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Call Made:");
                builder.setDescription("@everyone " + ((TLMessage) message).getMessage());
                builder.setColor(Color.CYAN);

                // Queue message
                discordChannel.sendMessage(builder.build()).queue();

                // Record message ID
                relayedMessageMap.putIfAbsent(message.getChatId(), new HashSet<>());
                relayedMessageMap.get(message.getChatId()).add(((TLMessage) message).getId());
            }
        }
    }

}
