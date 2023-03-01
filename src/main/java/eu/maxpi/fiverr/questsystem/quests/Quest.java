package eu.maxpi.fiverr.questsystem.quests;

import eu.maxpi.fiverr.questsystem.utils.ColorTranslator;
import eu.maxpi.fiverr.questsystem.utils.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Quest {

    public String internalName; //Name for commands and such
    public String name; //Display name

    //List of steps to this quest
    public List<QuestProgressStep> steps = new ArrayList<>();

    //-1 = completed
    //K: Player name
    //V: Index of the step they currently are on
    public HashMap<String, Integer> progress = new HashMap<>();

    public Quest(String internalName, String name){
        this.internalName = internalName;
        this.name = name;
    }


    /**
     * Checks whether a player should advance with their quest steps or not
     */
    public void checkProgression(){
        progress.keySet().stream().filter(s -> progress.get(s) != -1).forEach(s -> {
            Player p = Bukkit.getPlayer(s);
            if(p == null) return;

            if(!steps.get(progress.get(s)).hasCompleted(p)) return;
            advance(p);
        });
    }

    /**
     * Manages a new step being initiated for a player
     * @param p Player interested
     */
    public void advance(Player p){
        if(!progress.containsKey(p.getName())){
            return;
        }

        if(progress.get(p.getName()) == -1){
            return;
        }

        if(progress.get(p.getName()) + 1 >= steps.size()){
            stop(p);
            return;
        }

        steps.get(progress.get(p.getName())).progress.remove(p.getName());
        progress.put(p.getName(), progress.get(p.getName()) + 1);

        steps.get(progress.get(p.getName())).track(p);
    }

    /**
     * Starts the quest for a new player with progress 0. If the quest was already started by this player, the progress gets reset
     * @param p Player interested
     */
    public void start(Player p){
        progress.put(p.getName(), 0);

        steps.get(progress.get(p.getName())).track(p);

        p.sendTitle(PluginLoader.lang.get("quest-started"), PluginLoader.lang.get("quest-started-subtitle").replace("%name%", name), 15, 80, 15);
    }

    /**
     * Stops the quest and displays the title
     * @param p Player interested
     */
    public void stop(Player p){
        progress.put(p.getName(), -1);

        p.sendTitle(PluginLoader.lang.get("quest-stopped"), PluginLoader.lang.get("quest-stopped-subtitle").replace("%name%", name), 15, 80, 15);

        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        ColorTranslator.spawnFireworks(p.getLocation());
    }
}
