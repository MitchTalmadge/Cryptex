package com.mitchtalmadge.cryptex.service.telegram.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Database manager implementation that simply stores data in memory rather than a database.
 */
public class DatabaseManagerImpl implements DatabaseManager {

    /**
     * Maps chats to their IDs.
     */
    private Map<Integer, Chat> chats = new HashMap<>();

    /**
     * Maps users to their IDs.
     */
    private Map<Integer, IUser> users = new HashMap<>();

    /**
     * Maps botId to pts, date, seq.
     */
    private Map<Integer, int[]> differencesData = new HashMap<>();

    /**
     * Stores a chat for later retrieval.
     * @param chat The chat to store.
     */
    public void storeChat(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    @Override
    public @Nullable Chat getChatById(int id) {
        return chats.get(id);
    }

    /**
     * Stores a user for later retrieveal.
     * @param user The user to store.
     */
    public void storeUser(IUser user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public @Nullable IUser getUserById(int id) {
        return users.get(id);
    }

    @Override
    public @NotNull
    Map<Integer, int[]> getDifferencesData() {
        return differencesData;
    }

    @Override
    public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
        differencesData.put(botId, new int[]{pts, date, seq});
        return true;
    }
}