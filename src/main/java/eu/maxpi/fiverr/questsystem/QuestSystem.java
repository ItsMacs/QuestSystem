package eu.maxpi.fiverr.questsystem;

import eu.maxpi.fiverr.questsystem.commands.QuestCMD;
import eu.maxpi.fiverr.questsystem.events.onItemPickup;
import eu.maxpi.fiverr.questsystem.events.onMobKill;
import eu.maxpi.fiverr.questsystem.quests.Quest;
import eu.maxpi.fiverr.questsystem.utils.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

public final class QuestSystem extends JavaPlugin {

    private static QuestSystem instance = null;
    public static QuestSystem getInstance() { return QuestSystem.instance; }
    private static void setInstance(QuestSystem in) { QuestSystem.instance = in; }

    public static HashMap<String, Quest> loadedQuests = new HashMap<>();

    @Override
    public void onEnable() {
        setInstance(this);

        PluginLoader.load();

        loadCommands();
        loadEvents();
        loadTasks();

        Bukkit.getLogger().info("QuestSystem by fiverr.com/macslolz was enabled successfully!");
    }

    private void loadCommands(){
        Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCMD());
    }

    private void loadEvents(){
        Bukkit.getPluginManager().registerEvents(new onItemPickup(), this);
        Bukkit.getPluginManager().registerEvents(new onMobKill(), this);
    }

    private void loadTasks(){
        new BukkitRunnable() {
            @Override
            public void run() {
                loadedQuests.values().forEach(Quest::checkProgression);
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        PluginLoader.save();

        Bukkit.getLogger().info("QuestSystem by fiverr.com/macslolz was disabled successfully!");
    }
}
