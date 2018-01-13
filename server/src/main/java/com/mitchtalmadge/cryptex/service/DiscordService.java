package com.mitchtalmadge.cryptex.service;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.security.auth.login.LoginException;

@Service
public class DiscordService {

    @Value("${DISCORD_TOKEN}")
    private String discordToken;

    private final LogService logService;

    /**
     * The JDA (Discord API) instance.
     */
    private JDA jda;

    /**
     * The first guild connected to, which should be the only guild used throughout the application.
     * (This bot is designed to work with only one guild.)
     */
    private Guild guild;

    @Autowired
    public DiscordService(LogService logService) {
        this.logService = logService;
    }

    @PostConstruct
    private void init() throws LoginException {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(discordToken)
                    .buildBlocking();

            this.guild = jda.getGuilds().get(0);
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
     * @return The Guild that this bot is assigned to.
     */
    public Guild getGuild() {
        return guild;
    }

}
