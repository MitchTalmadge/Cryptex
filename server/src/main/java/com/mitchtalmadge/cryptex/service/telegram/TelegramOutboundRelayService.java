package com.mitchtalmadge.cryptex.service.telegram;

import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramContext;
import com.mitchtalmadge.cryptex.domain.dto.telegram.TelegramMessage;
import com.mitchtalmadge.cryptex.domain.entity.telegram.TelegramChatHistoryEntity;
import com.mitchtalmadge.cryptex.service.LogService;
import com.mitchtalmadge.cryptex.service.discord.DiscordInboundRelayService;
import com.mitchtalmadge.cryptex.service.entity.telegram.TelegramChatHistoryEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.api.file.location.TLFileLocation;
import org.telegram.api.input.filelocation.TLInputFileLocation;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.media.TLMessageMediaPhoto;
import org.telegram.api.photo.TLPhoto;
import org.telegram.api.photo.size.TLPhotoSize;
import org.telegram.api.upload.file.TLAbsFile;
import org.telegram.api.upload.file.TLFile;

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
    public void messageReceived(TelegramContext telegramContext, TelegramMessage message) {

        // Check chat history to see if we have seen this message.
        TelegramChatHistoryEntity history = telegramChatHistoryEntityRepository.findFirstByPhoneNumberAndChatId(telegramContext.getPhoneNumber(), message.getChatId());
        if (history == null) {
            // Create a new chat history entity.
            history = new TelegramChatHistoryEntity(telegramContext.getPhoneNumber(), message.getChatId(), message.getCreationDate());
            telegramChatHistoryEntityRepository.save(history);
        } else {
            // Skip this message if we have already seen it.
            if (history.getLastMessageTime().isEqual(message.getCreationDate()) || history.getLastMessageTime().isAfter(message.getCreationDate())) {
                logService.logInfo(getClass(), "Skipping relay of message ID " + message.getId() + " in chat ID " + message.getChatId() + " as it is too old.");
                return;
            }

            // Update last seen message id.
            history.setLastMessageTime(message.getCreationDate());
            telegramChatHistoryEntityRepository.save(history);
        }

        // Check if this message contains a picture.
        if (message.getOriginal() instanceof TLMessage) {
            if (((TLMessage) message.getOriginal()).getMedia() instanceof TLMessageMediaPhoto) {
                // Get photo instance
                TLPhoto photo = (TLPhoto) ((TLMessageMediaPhoto) ((TLMessage) message.getOriginal()).getMedia()).getPhoto();

                // Get largest size of photo
                TLPhotoSize photoSize = (TLPhotoSize) photo.getSizes().get(photo.getSizes().size() - 1);

                // Get location of largest photo size
                TLFileLocation photoFileLocation = (TLFileLocation) photoSize.getLocation();

                try {
                    // Download photo
                    TLFile photoFile = (TLFile) telegramContext.getBot().getKernelComm().getApi().doGetFile(photoFileLocation.getDcId(), new TLInputFileLocation(photoFileLocation), 0, (int) Math.pow(2, Math.ceil(Math.log((double) photoSize.getSize()) / Math.log(2))));
                    logService.logInfo(getClass(), "Relaying image from message ID " + message.getId() + " in chat ID " + message.getChatId());

                    // Relay to discord
                    discordInboundRelayService.notifyEveryone(DISCORD_CHANNEL_ID);
                    discordInboundRelayService.relayImage(photoFile.getBytes().getData(), ((TLMessageMediaPhoto) ((TLMessage) message.getOriginal()).getMedia()).getCaption(), DISCORD_CHANNEL_ID);
                    return;
                } catch (Exception e) {
                    logService.logException(getClass(), e, "Could not download photo.");
                    return;
                }
            }
        }

        // Ignore empty messages
        if (message.getContents().isEmpty())
            return;

        logService.logInfo(getClass(), "Relaying message ID " + message.getId() + " from chat ID " + message.getChatId());
        logService.logDebug(getClass(), "Message: " + message.getContents());

        // Relay to Discord.
        discordInboundRelayService.notifyEveryone(DISCORD_CHANNEL_ID);
        discordInboundRelayService.relayMessage(message.getContents(), "Call Made", DISCORD_CHANNEL_ID, true, true);
    }

}
