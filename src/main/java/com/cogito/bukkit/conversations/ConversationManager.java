package com.cogito.bukkit.conversations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.entity.Player;

public class ConversationManager implements Runnable{

    private static final int WAIT_FOR_REPLY = 300;
    private static final int WAIT_PER_CHARACTER = 10;
    
    private Conversations plugin;
    private String playerName;
    private Player player;
    private Map<ConversationListener, ConversationAgent> agents;
    private Queue<ConversationAgent> conversations;
    private ConversationAgent currentAgent;
    private int questions;
    private boolean waitingForReply;

    public ConversationManager(Conversations plugin, String playerName) {
        this.plugin = plugin;
        this.playerName = playerName;
        agents = Collections.synchronizedMap(new HashMap<ConversationListener, ConversationAgent>());
        conversations = new ConcurrentLinkedQueue<ConversationAgent>();
        currentAgent = null;
        this.questions = 0;
    }

    public void run() {
        while(conversing()){
            System.out.println("waiting for a conversation");
            try {
            // pick an agent to be active - the next in the queue sounds good
            currentAgent = conversations.poll();
            if (currentAgent != null) {
                System.out.println("we have a conversation!");
                while (!currentAgent.messages.isEmpty()) {
                    // lets send all messages, ask all questions for this agent.
                    // TODO handle the conversation ending prematurely
                    //  - if plugin or player hangs up
                    //  - if player times out
                    if (waitingForReply) {
                        // if a question is asked, wait for a reply - just wait, as the reply is handled by ChatListener
                            Thread.sleep(WAIT_FOR_REPLY);
                    } else {
                        // send questions and messages, in order they are given
                        Message currentMessage = currentAgent.messages.poll();
                        if (currentMessage != null) {
                            // TODO questions have a player - maybe check it here?
                            //player.sendMessage(currentMessage.getMessage());
                            currentMessage.send(plugin.getServer());
                            if (currentMessage instanceof Question) {
                                // when we send a question, we wait for a reply
                                waitingForReply = true;
                            }
                            // wait some time after sending each message
                            Thread.sleep(currentMessage.getMessage().length() * WAIT_PER_CHARACTER);
                        }
                    }
                }
                // add conversation to the end of the queue
                //conversations.add(currentAgent);
            }
            
                Thread.sleep(500);
                if (!waitingForReply) {
                    currentAgent = null;
                }
            } catch (InterruptedException e) {
                System.out.println("Shutting down conversation");
                Iterator<ConversationAgent> itr = conversations.iterator();
                while (itr.hasNext()) {
                    ConversationAgent agent = itr.next();
                    // TODO check the meaningfulness of this end reason
                    agent.listener.onConversationEnd(ConversationEndReason.CONVERSATION_OVER, agent.messages);
                }
                currentAgent = null;
                return;
            } finally {
                
            }
            
        }
    }

    /**
     * When should this manager die?
     * also, what if the manager is killed prematurely???
     * @return
     */
    private boolean conversing() {
        player = plugin.getServer().getPlayer(playerName);
        return !conversations.isEmpty() && (player != null && player.isOnline());
    }

    /**
     * Associates a ConversationListener with the Player.
     * 
     * If the listener is currently in conversation with the player, it's existing ConversationAgent
     *  will be returned. Otherwise, a new ConversationAgent is started and returned.
     *  If a conversation is ended, the ConversationAgent is not guaranteed to still exist.
     *
     * @param listener The ConversationListener that will listen for replies to questions.
     * 
     * @return a ConversationAgent that can be used to converse with the Player.
     */
    public ConversationAgent getAgent(ConversationListener listener) {
        ConversationAgent agent;
        if (agents.containsKey(listener)){
            agent = agents.get(listener);
        } else {
            agent = new ConversationAgent(this, listener);
            agents.put(listener, agent);
        }
        return agent;
    }

    public boolean newReply(String reply) {
        boolean replied;
        
        replied = (currentAgent == null)?false:currentAgent.sendReply(reply);
        System.out.println("player chat event");
        if (replied) {
            waitingForReply = false;
            this.questions--; // question has been dealt with
        }
        return replied;
    }

    /**
     * Register a question with the conversation manager.
     * 
     * @param conversationAgent the agent registering the question.
     * @param question the question to be registered.
     * 
     * @return the number of questions before this one
     */
    public int newQuestion(ConversationAgent conversationAgent, Message question) {
        conversations.add(conversationAgent);
        plugin.manageThread(this);
        return this.questions++;
    }

    public void newMessage(ConversationAgent conversationAgent, Message message) {
        conversations.add(conversationAgent);
        plugin.manageThread(this);
    }

}
