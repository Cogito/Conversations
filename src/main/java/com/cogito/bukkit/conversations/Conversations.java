package com.cogito.bukkit.conversations;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.cogito.bukkit.conversations.ConversationAgent;

public class Conversations extends JavaPlugin {

    private Map<String, ConversationManager> managers;
    private final ChatListener chatListener = new ChatListener(this);
    private Map<ConversationManager, Integer> managerTasks;

    public Conversations(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        managers = Collections.synchronizedMap(new HashMap<String, ConversationManager>());
        managerTasks = Collections.synchronizedMap(new HashMap<ConversationManager, Integer>());
    }

    public void onDisable() {
        // TODO Auto-generated method stub
        getServer().getScheduler().cancelTasks(this);
        managerTasks.clear();
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_CHAT, chatListener, Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_COMMAND, chatListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, chatListener, Priority.Normal, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    /**
     * Initiate a conversation with a player.
     * 
     * In order to converse with a player you need to send messages and questions, and receive replies.
     * Messages and questions are sent through the returned ConversationAgent. Replies to questions will
     * be received by the ConversationListener you supply.
     * This listener must be thread safe, and should optimally only listen to one conversation
     * (though it could potentially listen to more).
     * 
     * @param playerName the player with whom you would like to start a conversation.
     * @param listener the ConversationListener that will receive replies to your questions.
     * @return
     */
    public ConversationAgent startConversation(String playerName, ConversationListener listener) {
        ConversationManager manager = getManager(playerName);
        return (manager==null)?null:manager.getAgent(listener);
        
    }

    // TODO add a prefix option for conversations

    /**
     * The conversation manager for a player.
     * 
     * Each player only ever has one active ConversationManager, though the manager may not be the same at all times.
     * If a player does not have an active manager, a new one will be created, otherwise the active one will be returned.
     * 
     * @param playerName the Player whose ConversationManager you would like.
     * 
     * @return a ConversationManager who manages the conversations of the player.
     */
    ConversationManager getManager(String playerName) {
        ConversationManager manager;
        if (managers == null) {
            System.out.println("managers has not been instantiated!!!");
            return null;
        }
        if (playerName == null) {
            System.out.println("player is null!!!");
            return null;
        }
        if (managers.containsKey(playerName)){
            manager = managers.get(playerName);
        } else {
            manager = new ConversationManager(this, playerName);
            System.out.println("New manager: "+manager);
            managers.put(playerName, manager);
        }
        return manager;
    }

    /**
     * Ensure that there is a thread running for this manager.
     * @param manager
     */
    void manageThread(ConversationManager manager) {
        synchronized(managerTasks) {
            if (manager == null) {
                return;
            }
            BukkitScheduler scheduler = getServer().getScheduler();
            Integer task;
            if (managerTasks.containsKey(manager)) {
                task = managerTasks.get(manager);
                if (scheduler.isCurrentlyRunning(task)) {
                    System.out.println(manager+" still running.");
                    return;
                } else {
                    System.out.println("Starting up "+manager);
                    task = scheduler.scheduleAsyncDelayedTask(this, manager);
                }
            } else {
                System.out.println("Starting up "+manager);
                task = scheduler.scheduleAsyncDelayedTask(this, manager);
            }
            managerTasks.put(manager, task);
        }
    }
}
