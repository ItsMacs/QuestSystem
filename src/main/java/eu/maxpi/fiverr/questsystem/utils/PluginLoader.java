package eu.maxpi.fiverr.questsystem.utils;

import eu.maxpi.fiverr.questsystem.QuestSystem;
import eu.maxpi.fiverr.questsystem.quests.Quest;
import eu.maxpi.fiverr.questsystem.quests.QuestProgressStep;
import eu.maxpi.fiverr.questsystem.quests.QuestProgressStepType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginLoader {

    public static HashMap<String, String> lang = new HashMap<>();

    public static long defDuration;
    public static void load(){
        QuestSystem.getInstance().saveResource("config.yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(QuestSystem.getInstance().getDataFolder() + "/config.yml"));
        config.getConfigurationSection("lang").getKeys(false).forEach(s -> lang.put(s, ColorTranslator.translate(config.getString("lang." + s))));

        defDuration = config.getLong("default-dialogue-duration");

        config.getConfigurationSection("quests").getKeys(false).forEach(s -> {
            Quest q = new Quest(s, config.getString("quests." + s + ".name"));

            config.getConfigurationSection("quests." + s + ".steps").getKeys(false).forEach(st -> {
                QuestProgressStep step = new QuestProgressStep(QuestProgressStepType.valueOf(config.getString("quests." + s + ".steps." + st + ".type")), config.getString("quests." + s + ".steps." + st + ".content"));
                q.steps.add(step);
            });

            QuestSystem.loadedQuests.put(q.internalName, q);
        });

        YamlConfiguration storage = YamlConfiguration.loadConfiguration(new File(QuestSystem.getInstance().getDataFolder() + "/storage.yml"));
        storage.getKeys(false).forEach(s -> {
            Quest q = QuestSystem.loadedQuests.get(s);
            storage.getConfigurationSection(s + ".steps").getKeys(false).forEach(step -> {
                QuestProgressStep stepObj = q.steps.get(Integer.parseInt(step));
                storage.getStringList(s + ".steps." + step).forEach(el -> {
                    stepObj.progress.put(el.split("//")[0], Long.parseLong(el.split("//")[1]));
                });
            });

            storage.getStringList(s + ".progress").forEach(prog -> {
                q.progress.put(prog.split("//")[0], Integer.parseInt(prog.split("//")[1]));
            });
        });
    }

    /**
     * questname:
     *   step1:
     *     p
     */

    public static void save() {
        YamlConfiguration storage = new YamlConfiguration();

        QuestSystem.loadedQuests.values().forEach(q -> {
            List<String> progressQuest = new ArrayList<>();
            q.progress.forEach((s, i) -> progressQuest.add(s + "//" + i));
            storage.set(q.internalName + ".progress", progressQuest);

            q.steps.forEach(step -> {
                List<String> progressSub = new ArrayList<>();
                step.progress.forEach((s, i) -> progressSub.add(s + "//" + i));
                storage.set(q.internalName + ".steps." + q.steps.indexOf(step), progressSub);
            });
        });

        try {
            storage.save(new File(QuestSystem.getInstance().getDataFolder() + "/storage.yml"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
