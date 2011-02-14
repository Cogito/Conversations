package com.cogito.bukkit.conversations;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ChatListener extends PlayerListener {

    private Conversations plugin;

    public ChatListener(Conversations conversations) {
        this.plugin = conversations;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        ConversationManager conversation = plugin.getManager(event.getPlayer());
        if (conversation == null) {
            return;
        }
        // send the reply, and check if the reply was accepted
        if (conversation.newReply(event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
