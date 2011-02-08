package com.cogito.bukkit.conversations;

/**
 * An object that waits for replies to questions.
 * @author Cogito
 *
 */
public interface ConversationListener {

    /**
     * Called when a conversation ends.
     * 
     * A conversation can be ended by either party, or by timing out. If a conversation is over,
     * any remaining questions or messages will not be sent, and no replies will be received.
     * Note that it is possible for a plugin to gracefully end a conversation, where any remaining
     * messages will be flushed and any outstanding replies will be received before ending.
     * 
     * @param reason the cause for the conversation ending.
     * @param messages any messages not sent when the conversation ended.
     * @param questions any questions not sent when the conversation ended.
     */
    public void onConversationEnd(ConversationEndReason reason, String[] messages, String[] questions);

    /**
     * If a question has been asked, anything the user says will be piped to this function.
     * 
     * If true is returned the reply text will not be broadcast to the server. The PlayerChatEvent that
     * started the reply will be cancelled. If false is returned it is assumed the 'reply' was in fact just
     * a chat message, and will be broadcast to the server (perhaps after the conversation is over)
     * 
     * @param reply 
     * @return
     */
    public boolean onReply(String reply);

}
