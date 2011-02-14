package com.cogito.bukkit.conversations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.bukkit.entity.Player;

public class ConversationManager implements Runnable{

    private static final int WAIT_FOR_REPLY = 500;
    private static final int WAIT_PER_CHARACTER = 50;
    private Player player;
    private Map<ConversationListener, ConversationAgent> agents;
    private Queue<ConversationAgent> conversations;
    private ConversationAgent currentAgent;
    private int questions;
    private boolean waitingForReply;

    public ConversationManager(Conversations plugin, Player player) {
        this.player = player;
        agents = Collections.synchronizedMap(new HashMap<ConversationListener, ConversationAgent>());
        currentAgent = null;
        this.questions = 0;
    }

    @Override
    public void run() {
        while(conversing()){
            // pick an agent to be active - the next in the queue sounds good
            ConversationAgent currentConvo = conversations.poll();
            if (currentConvo != null) {
                while (!currentConvo.messages.isEmpty()) {
                    // lets send all messages, ask all questions for this agent.
                    // TODO handle the conversation ending prematurely
                    //  - if plugin or player hangs up
                    //  - if player times out
                    if (waitingForReply) {
                        // if a question is asked, wait for a reply - just wait, as the reply is handled by ChatListener
                        try {
                            Thread.sleep(WAIT_FOR_REPLY);
                        } catch (InterruptedException e) {
                            // we are going to loop again anyhow, so lets keep going
                        }
                    } else {
                        // send questions and messages, in order they are given
                        Message currentMessage = currentConvo.messages.poll();
                        if (currentMessage != null) {
                            // wait some time between sending each message
                            try {
                                Thread.sleep(currentMessage.getMessage().length() * WAIT_PER_CHARACTER);
                            } catch (InterruptedException e) {
                                // oh well couldn't wait, just send it anyways.
                            }
                            player.sendMessage(currentMessage.getMessage());
                            if (currentMessage instanceof Question) {
                                // when we send a question, we wait for a reply
                                waitingForReply = true;
                            }
                        }
                    }
                    
                    
                }
                // add conversation to the end of the queue
                conversations.add(currentConvo);
            }
        }
    }

    /**
     * When should this manager die?
     * also, what if the manager is killed prematurely???
     * @return
     */
    private boolean conversing() {
        // TODO Auto-generated method stub
        return true;
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
        if (replied) {
            waitingForReply = false;
        }
        return replied;
    }

    public int newQuestion(ConversationAgent conversationAgent, Message question) {
        return (++this.questions);
    }

}
