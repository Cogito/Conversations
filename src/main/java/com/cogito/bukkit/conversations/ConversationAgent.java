package com.cogito.bukkit.conversations;

import java.util.Queue;

/**
 * 
 * @author Cogito
 *
 */
public class ConversationAgent {

    private ConversationManager manager;
    private ConversationListener listener;
    Queue<Message> messages;
    private Question currentQuestion;

    public ConversationAgent(ConversationManager manager, ConversationListener listener) {
        this.manager = manager;
        this.listener = listener;
    }

    /**
     * Send an informative message as part of this conversation.
     * 
     * Messages sent in this manner will be queued until this conversation is active.
     * No response is expected to a message, and so none will be looked for. If you require
     * a response, use sendQuestion(Question question) instead.
     * 
     * @param message the message to be sent as part of this conversation.
     */
    public void sendMessage(Message message){
        // TODO sync properly
        this.messages.add(message);
    }

    /**
     * Ask a question as part of this conversation.
     * 
     * A question is queued just as a message, however when a question is asked an answer is expected.
     * Any further messages will in general be sent, until the next question is about to be asked.
     * If a new question is queued, no further responses or messages will be processed until this
     * question is dealt with.
     * Once this question is asked, anything that is said by this conversation's Player, that is not a 
     * command, will be sent to the listener's onReply() function. When a valid reply is received,
     * the conversation will continue until it is ended.
     *  
     * @param question the question to be asked.
     * @return true if there are no questions queued before this one, and no active question.
     */
    public boolean sendQuestion(Question question){
        // TODO sync properly
        this.messages.add(question);
        return (manager.newQuestion(this, question) == 0);
    }

    boolean sendReply(String reply){
        boolean answered = listener.onReply(this.currentQuestion, reply);
        return answered;
    }
}
