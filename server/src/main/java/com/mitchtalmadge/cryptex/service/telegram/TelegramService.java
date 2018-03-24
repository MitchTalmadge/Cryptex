package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.domain.entity.telegram.TelegramChatHistoryEntity;
import com.mitchtalmadge.cryptex.service.DiscordService;
import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.SpringProfileService;
import com.mitchtalmadge.cryptex.service.entity.telegram.TelegramChatHistoryEntityRepository;
import com.mitchtalmadge.cryptex.service.telegram.impl.BotConfigImpl;
import com.mitchtalmadge.cryptex.service.telegram.impl.ChatUpdatesBuilderImpl;
import com.mitchtalmadge.cryptex.util.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.api.engine.RpcException;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.List;
import java.util.Map;
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

    private LogService logService;

    private SpringProfileService springProfileService;
    private TelegramAuthService telegramAuthService;

    private DiscordService discordService;
    private TelegramChatHistoryEntityRepository telegramChatHistoryEntityRepository;

    @Autowired
    public TelegramService(LogService logService,
                           SpringProfileService springProfileService,
                           TelegramAuthService telegramAuthService,
                           DiscordService discordService,
                           TelegramChatHistoryEntityRepository telegramChatHistoryEntityRepository) {
        this.logService = logService;
        this.springProfileService = springProfileService;
        this.telegramAuthService = telegramAuthService;
        this.discordService = discordService;
        this.telegramChatHistoryEntityRepository = telegramChatHistoryEntityRepository;
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

        // Create Telegram Context
        TelegramContext telegramContext = new TelegramContext();
        telegramContext.setTelegramService(this);
        telegramContext.setPhoneNumber(PHONE_NUMBER);
        telegramContext.setApiId(apiID);
        telegramContext.setApiHash(API_HASH);

        // Create Telegram Bot
        telegramContext.setBot(
                new TelegramBot(
                        new BotConfigImpl(telegramContext),
                        new ChatUpdatesBuilderImpl(telegramContext),
                        telegramContext.getApiId(),
                        telegramContext.getApiHash()
                )
        );

        // Sign into Telegram.
        telegramAuthService.signIn(telegramContext);
    }

    /**
     * Called when a message is received from Telegram.
     * Relays the message to Discord if applicable.
     *
     * @param telegramContext The Telegram Context associated with the message.
     * @param message         The message received.
     */
    public void messageReceived(TelegramContext telegramContext, TLMessage message) {
        // Check chat history to see if we have seen this message.
        TelegramChatHistoryEntity history = telegramChatHistoryEntityRepository.findFirstByPhoneNumberAndChatId(telegramContext.getPhoneNumber(), message.getChatId());
        if (history == null) {
            // Create a new chat history entity.
            history = new TelegramChatHistoryEntity(telegramContext.getPhoneNumber(), message.getChatId(), message.getId());
            telegramChatHistoryEntityRepository.save(history);
        } else {
            // Skip this message if we have already seen it.
            if (history.getLastMessageId() >= message.getId()) {
                logService.logInfo(getClass(), "Skipping message ID " + message.getId() + " in chat ID " + message.getChatId() + " as it is smaller than " + history.getLastMessageId());
                return;
            }

            // Update last seen message id.
            history.setLastMessageId(message.getId());
            telegramChatHistoryEntityRepository.save(history);
        }

        // TODO: check image

        // Ignore empty messages
        if (message.getMessage().isEmpty())
            return;

        logService.logInfo(getClass(), "New Message Received: " + message.getMessage());

        // Send message to discord
        TextChannel discordChannel = discordService.getJDA().getTextChannelById(DISCORD_CHANNEL_ID);
        if (discordChannel != null) {

            // Segment message so that it will fit in Discord message limits. Append @everyone tag for mentions.
            String[] segmentedMessage = StringUtils.segmentString(message.getMessage(), MessageEmbed.TEXT_MAX_LENGTH);

            try {
                // Mention everyone.
                discordChannel.sendMessage("@everyone").queue();

                // Send segments
                for (int i = 0; i < segmentedMessage.length; i++) {
                    // Build rich embed
                    EmbedBuilder builder = new EmbedBuilder();

                    // Title displays "pages" if necessary; "Call Made (1/3):", otherwise "Call Made:"
                    builder.setTitle(segmentedMessage.length > 1 ? ("Call Made (" + (i + 1) + "/" + segmentedMessage.length + "):") : "Call Made:");
                    builder.setDescription(segmentedMessage[i]);
                    builder.setColor(Color.CYAN);

                    // Queue and pin message
                    discordChannel.sendMessage(builder.build()).queue(sentMessage -> discordChannel.pinMessageById(sentMessage.getId()).queue());
                }
            } catch (Exception e) {
                logService.logException(getClass(), e, "Could not send Discord message.");
            }

        }
    }

}
