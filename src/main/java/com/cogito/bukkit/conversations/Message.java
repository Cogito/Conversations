package com.cogito.bukkit.conversations;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Message {
    private final String playerName;
    private final String message;
    boolean sent; // default so I can set it in ConversationManager. private if using this.send()

    public Message(String playerName, String message) {
        super();
        this.playerName = playerName;
        this.message = message;
        this.sent = false;
    }

    final boolean send(Server server){
        synchronized(this) {
            Player player = getPlayer(server);
            if (player != null && player.isOnline() && !sent) {
                player.sendMessage(message);
                sent = true;
                return true;
            } else {
                return false;
            }
        }
    }

    public final String getPlayerName() {
        return playerName;
    }

    public final Player getPlayer(Server server) {
        return server.getPlayer(playerName);
    }

    public final String getMessage() {
        return message;
    }

    public final boolean isSent() {
        return sent;
    }


}
