package com.mitchtalmadge.cryptex.service.discord;

import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.util.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

/**
 * This service allows for relaying messages to Discord channels from external sources.
 */
@Service
public class DiscordInboundRelayService {

    private final LogService logService;
    private final DiscordService discordService;

    @Autowired
    public DiscordInboundRelayService(LogService logService,
                                      DiscordService discordService) {
        this.logService = logService;
        this.discordService = discordService;
    }

    /**
     * Notifies everyone in a channel.
     *
     * @param channelId The ID of the Discord channel.
     */
    public void notifyEveryone(String channelId) {
        TextChannel channel = discordService.getJDA().getTextChannelById(channelId);
        if (channel != null) {
            logService.logInfo(getClass(), "Notifying everyone in channel '" + channel.getName() + "'");
            channel.sendMessage("@everyone").queue();
        }
    }

    /**
     * Relays a message to a specific channel.
     *
     * @param message   The message body.
     * @param title     The title of the message.
     * @param channelId The ID of the Discord channel.
     * @param embed     Whether to use an embed (true) or plain-text (false).
     * @param pin       Whether to pin the message to the channel.
     */
    public void relayMessage(String message, String title, String channelId, boolean embed, boolean pin) {

        // Find channel
        TextChannel channel = discordService.getJDA().getTextChannelById(channelId);
        if (channel == null) {
            logService.logInfo(getClass(), "Channel not found for id: " + channelId);
            return;
        }

        logService.logInfo(getClass(), "Relaying message to channel '" + channel.getName() + "'");
        logService.logDebug(getClass(), "Message: " + message);

        // Validate title
        if (title != null) {
            if (embed && title.length() > MessageEmbed.TEXT_MAX_LENGTH) {
                // Shorten title if needed
                title = title.substring(0, MessageEmbed.TITLE_MAX_LENGTH - 1);
            } else if (!embed) {
                // Append title to message.
                message = "**" + title + "**\n\n" + message;
            }
        }

        // Segment message so that it will fit in Discord message limits.
        String[] segmentedMessage = StringUtils.segmentString(message, embed ? 750 : Message.MAX_CONTENT_LENGTH);

        // Send segments
        for (int i = 0; i < segmentedMessage.length; i++) {
            String segment = segmentedMessage[i];

            if (embed) {
                // Build embed
                EmbedBuilder builder = new EmbedBuilder();

                // Title displays "pages" if necessary; "Title: (1/3)", otherwise "Title:"
                builder.setTitle(segmentedMessage.length > 1 ? (title + " (" + (i + 1) + "/" + segmentedMessage.length + ")") : title);
                builder.setDescription(segment);
                builder.setColor(Color.CYAN);

                // Queue and pin message
                channel.sendMessage(builder.build()).queue(sentMessage -> {
                    if (pin) {
                        logService.logInfo(getClass(), "Pinning relayed message to Discord channel '" + channel.getName() + "'");
                        channel.pinMessageById(sentMessage.getId()).queue();
                    }
                });
            } else {
                channel.sendMessage(segment).queue(sentMessage -> {
                    if (pin) {
                        logService.logInfo(getClass(), "Pinning relayed message to Discord channel '" + channel.getName() + "'");
                        channel.pinMessageById(sentMessage.getId()).queue();
                    }
                });
            }
        }

    }

    /**
     * Relays an image and caption to a specific Discord channel.
     *
     * @param imageData The data of the image file (PNG, JPG, etc).
     * @param caption   The image caption. Optional.
     * @param channelId The ID of the Discord channel.
     */
    public void relayImage(byte[] imageData, String caption, String channelId) {
        // Find channel
        TextChannel channel = discordService.getJDA().getTextChannelById(channelId);
        if (channel == null) {
            logService.logInfo(getClass(), "Channel not found for id: " + channelId);
            return;
        }

        // Validate image data
        String mediaTypeStr = new Tika().detect(imageData);
        if (mediaTypeStr == null) {
            logService.logError(getClass(), "Could not determine image media type for relay.");
            return;
        }

        MediaType mediaType = MediaType.parseMediaType(mediaTypeStr);
        String fileExtension = mediaType.equals(MediaType.IMAGE_PNG) ? "png" : mediaType.equals(MediaType.IMAGE_JPEG) ? "jpg" : mediaType.equals(MediaType.IMAGE_GIF) ? "gif" : null;
        if (fileExtension == null) {
            logService.logError(getClass(), "Media type invalid for relaying image: " + mediaType);
            return;
        }

        String fileName = "image." + fileExtension;

        logService.logInfo(getClass(), "Relaying image to channel '" + channel.getName() + "'");
        logService.logDebug(getClass(), "Media type: " + mediaType);
        logService.logDebug(getClass(), "File name: " + fileName);

        // Send to Discord.
        if (caption != null && !caption.isEmpty()) {
            Message message = new MessageBuilder().append(caption).build();
            channel.sendFile(imageData, fileName, message).queue();
        } else {
            channel.sendFile(imageData, fileName).queue();
        }
    }
}
