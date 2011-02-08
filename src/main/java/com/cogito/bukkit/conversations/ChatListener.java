package com.cogito.bukkit.conversations;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ChatListener extends PlayerListener {

    private Conversations plugin;

    public ChatListener(Conversations conversations) {
        this.plugin = conversations;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        ConversationManager conversation = plugin.currentConversation();
        if (conversation == null) { return; }
        
        if (conversation.newReply(event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
