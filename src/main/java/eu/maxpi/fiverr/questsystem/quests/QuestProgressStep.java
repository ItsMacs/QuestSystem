package eu.maxpi.fiverr.questsystem.quests;

import eu.maxpi.fiverr.questsystem.utils.ColorTranslator;
import eu.maxpi.fiverr.questsystem.utils.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;

public class QuestProgressStep {

    public QuestProgressStepType type;


    //Same style as the one in Quest.java
    public HashMap<String, Long> progress = new HashMap<>();

    //Could be an ItemStack or a String
    public Object checkObject;

    //Used for dialogue duration or for mob kill count
    public long duration;

    //Create the object managing the different types of progress
    public QuestProgressStep(QuestProgressStepType type, Object obj){
        this.type = type;
        this.checkObject = obj;

        switch (type){
            case DIALOGUE, MOB_KILL -> {
                String s = (String)obj;
                if(!s.contains("/")) {
                    checkObject = s.split("/")[0];
                    duration = PluginLoader.defDuration;
                    return;
                }

                checkObject = s.split("/")[0];
                duration = Long.parseLong(s.split("/")[1]);
            }

            case LOCATION -> {
                String s = (String)obj;
                checkObject = new Location(Bukkit.getWorld(s.split("//")[0]), Double.parseDouble(s.split("//")[1]), Double.parseDouble(s.split("//")[2]), Double.parseDouble(s.split("//")[3]));
            }

            case ITEM_COLLECTION -> {
                String s = (String)obj;
                checkObject = new ItemStack(Material.valueOf(s.split("/")[0]), Integer.parseInt(s.split("/")[1]));
            }
        }
    }

    /**
     * Sets the value in progress
     * @param p Player
     */
    public void track(Player p){
        if (type == QuestProgressStepType.DIALOGUE) {
            p.sendMessage(ColorTranslator.translate((String) checkObject).replace("%player%", p.getName()));
            progress.put(p.getName(), ZonedDateTime.now().toEpochSecond() + duration);
            return;
        }

        progress.put(p.getName(), 0L);
    }

    /**
     * Returns whether this step was completed by this player. This only works if this step is the one the player is working on.
     * @param p Player interested
     * @return Whether this is the active step and is completed
     */
    public boolean hasCompleted(Player p){
        switch (type) {
            default -> {
                return true;
            }
            case DIALOGUE -> {
                return ZonedDateTime.now().toEpochSecond() >= progress.getOrDefault(p.getName(), ZonedDateTime.now().toEpochSecond());
            }
            case LOCATION -> {
                Location l = (Location) checkObject;
                if (l.getWorld() != p.getWorld()) return false;

                return l.distance(p.getLocation()) < 5;
            }
            case MOB_KILL -> {
                return progress.getOrDefault(p.getName(), 0L) >= duration;
            }
            case ITEM_COLLECTION -> {
                return progress.getOrDefault(p.getName(), 0L) >= ((ItemStack) checkObject).getAmount();
            }
        }
    }
}
