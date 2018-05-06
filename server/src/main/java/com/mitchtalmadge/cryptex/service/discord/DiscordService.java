package com.mitchtalmadge.cryptex.service.discord;

import com.mitchtalmadge.cryptex.service.LogService;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.security.auth.login.LoginException;

@Service
public class DiscordService {

    /**
     * The token used to sign into Discord as the Cryptex Bot.
     */
    private static final String DISCORD_TOKEN = System.getenv("DISCORD_TOKEN");

    private final LogService logService;

    /**
     * The JDA (Discord API) instance.
     */
    private JDA jda;

    @Autowired
    public DiscordService(LogService logService) {
        this.logService = logService;
    }

    @PostConstruct
    private void init() throws LoginException {
        if(DISCORD_TOKEN == null) {
            logService.logError(getClass(), "One or more environment variables were missing. Disabling Discord bot.");
            return;
        }

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(DISCORD_TOKEN)
                    .buildBlocking();
        } catch (LoginException e) {
            logService.logException(getClass(), e, "Could not sign in to Discord");
            throw e;
        } catch (InterruptedException e) {
            logService.logException(getClass(), e, "JDA was interrupted while logging in");
        }
    }

    @PreDestroy
    private void destroy() {
        if (jda != null)
            jda.shutdown();
    }

    /**
     * @return The JDA instance for this bot.
     */
    public JDA getJDA() {
        return jda;
    }

    /**
     * @return Whether or not the Discord Bot is connected and running.
     */
    public boolean isConnected() {
        return jda != null;
    }

}
