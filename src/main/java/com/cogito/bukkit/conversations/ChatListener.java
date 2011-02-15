package com.cogito.bukkit.conversations;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ChatListener extends PlayerListener {

    private Conversations plugin;

    public ChatListener(Conversations conversations) {
        this.plugin = conversations;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        System.out.println("player chat event");
        ConversationManager conversation = plugin.getManager(event.getPlayer());
        if (conversation == null) {
            System.out.println("no manager could be found :(");
            return;
        }
        // send the reply, and check if the reply was accepted
        System.out.println("sending reply");
        if (conversation.newReply(event.getMessage())) {
            System.out.println("reply received");
            event.setCancelled(true);
        }
    }
}
