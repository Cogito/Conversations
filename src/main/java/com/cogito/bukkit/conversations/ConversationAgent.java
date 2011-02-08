package com.cogito.bukkit.conversations;

/**
 * 
 * @author Cogito
 *
 */
public class ConversationAgent {

    private ConversationManager manager;
    private ConversationListener listener;

    public ConversationAgent(ConversationManager manager, ConversationListener listener) {
        this.manager = manager;
        this.listener = listener;
    }

    /**
     * Send an informative message as part of this conversation.
     * 
     * Messages sent in this manner will be queued until this conversation is active.
     * No response is expected to a message, and so none will be looked for. If you require
     * a response, use sendMessage() instead.
     * 
     * @param message the message to be sent as part of this conversation.
     */
    public void sendMessage(String message){
        manager.sendMessage(this, message);
    }

    /**
     * Ask a question as part of this conversation.
     * 
     * A question is queued just as a message, however when a question is asked an answer is expected.
     * Anything that is said by this conversations Player, that is not a command, will be sent to the
     * listener's onReply() function.
     *  
     * @param question the text of the question being asked
     * @return
     */
    public boolean sendQuestion(String question){
        return manager.sendQuestion(this, question);
    }

    boolean sendReply(String reply){
        return listener.onReply(reply);
    }
}
