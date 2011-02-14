package com.cogito.bukkit.conversations;

import org.bukkit.entity.Player;

public class Message {
    private final Player player;
    private final String message;
    private boolean sent;

    public Message(Player player, String message) {
        super();
        this.player = player;
        this.message = message;
        this.sent = false;
    }

    final boolean send(){
        synchronized(this) {
            if (player.isOnline() && !sent) {
                player.sendMessage(message);
                sent = true;
                return true;
            } else {
                return false;
            }
        }
    }

    public final Player getPlayer() {
        return player;
    }

    public final String getMessage() {
        return message;
    }

    public final boolean isSent() {
        return sent;
    }


}
