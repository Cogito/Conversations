package com.cogito.bukkit.conversations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.bukkit.entity.Player;

public class ConversationManager {

    private Conversations plugin;
    private Player player;
    private Map<ConversationListener, ConversationAgent> agents;
    private Queue<ConversationAgent> conversations;
    private ConversationAgent currentAgent;

    public ConversationManager(Conversations plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        agents = Collections.synchronizedMap(new HashMap<ConversationListener, ConversationAgent>());
        currentAgent = null;
    }

    public ConversationAgent getAgent(ConversationListener listener) {
        // TODO Auto-generated method stub
        ConversationAgent agent;
        if (agents.containsKey(listener)){
            agent = agents.get(listener);
        } else {
            agent = new ConversationAgent(this, listener);
            agents.put(listener, agent);
        }
        return agent;
    }

    public void sendMessage(ConversationAgent agent, String message) {
        // TODO Auto-generated method stub
    }

    public boolean sendQuestion(ConversationAgent agent, String question) {
        // TODO Auto-generated method stub
        if (!agents.containsValue(agent)) {
            // Don't have this agent
            return false;
        }

        // add this question to the queue, and wait for it to be sent. If the player is not online, return false.
        for (conversations.add(agent); player.isOnline();) {
            synchronized(conversations){
                if (conversations.peek() == agent) {
                    conversations.poll();
                    player.sendMessage(question);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean newReply(String reply) {
        if (currentAgent != null) {
            return currentAgent.sendReply(reply);
        } else {
            return false;
        }
    }

}
