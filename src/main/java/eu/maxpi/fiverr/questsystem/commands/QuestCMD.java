package eu.maxpi.fiverr.questsystem.commands;

import eu.maxpi.fiverr.questsystem.QuestSystem;
import eu.maxpi.fiverr.questsystem.quests.Quest;
import eu.maxpi.fiverr.questsystem.utils.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("quest.admin")){
            sender.sendMessage(PluginLoader.lang.get("no-permission"));
            return true;
        }

        if(args.length == 0){
            sender.sendMessage(PluginLoader.lang.get("quest-usage"));
            return true;
        }

        switch (args[0].toLowerCase()){
            case "start" -> {
                //Error handling
                if(args.length != 3){
                    sender.sendMessage(PluginLoader.lang.get("quest-start-usage"));
                    return true;
                }

                Player p = Bukkit.getPlayer(args[1]);
                if(p == null){
                    sender.sendMessage(PluginLoader.lang.get("no-player"));
                    return true;
                }

                if(!QuestSystem.loadedQuests.containsKey(args[2])){
                    sender.sendMessage(PluginLoader.lang.get("no-quest"));
                    return true;
                }

                //If no errors were found, start the quest
                QuestSystem.loadedQuests.get(args[2]).start(p);
                sender.sendMessage(PluginLoader.lang.get("quest-started-admin"));
                return true;
            }

            case "stop" -> {
                if(args.length != 3){
                    sender.sendMessage(PluginLoader.lang.get("quest-stop-usage"));
                    return true;
                }

                Player p = Bukkit.getPlayer(args[1]);
                if(p == null){
                    sender.sendMessage(PluginLoader.lang.get("no-player"));
                    return true;
                }

                if(!QuestSystem.loadedQuests.containsKey(args[2])){
                    sender.sendMessage(PluginLoader.lang.get("no-quest"));
                    return true;
                }

                QuestSystem.loadedQuests.get(args[2]).stop(p);
                sender.sendMessage(PluginLoader.lang.get("quest-stopped-admin"));
                return true;
            }
        }
        return true;
    }
}
