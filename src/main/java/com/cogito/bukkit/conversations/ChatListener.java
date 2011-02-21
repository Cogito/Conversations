package com.cogito.bukkit.conversations;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class ChatListener extends PlayerListener {

    private Conversations plugin;

    public ChatListener(Conversations conversations) {
        this.plugin = conversations;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        ConversationManager conversation = plugin.getManager(event.getPlayer().getName());
        if (conversation == null) {
            //System.out.println("no manager could be found :(");
            return;
        }
        // send the reply, and check if the reply was accepted
        if (conversation.newReply(event.getMessage())) {
            // echo the player's chat back to them.
            event.getPlayer().sendMessage("["+event.getPlayer().getDisplayName()+"] "+event.getMessage());
            event.setCancelled(true);
        }
    }

    public void onPlayerJoin(PlayerEvent event) {
        ConversationManager conversation = plugin.getManager(event.getPlayer().getName());
        if (conversation == null) {
            return;
        }
        conversation.plugin.manageThread(conversation);
    }
}
