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

}
