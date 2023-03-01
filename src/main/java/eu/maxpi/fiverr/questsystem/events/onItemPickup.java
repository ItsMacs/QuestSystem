package eu.maxpi.fiverr.questsystem.events;

import eu.maxpi.fiverr.questsystem.QuestSystem;
import eu.maxpi.fiverr.questsystem.quests.QuestProgressStep;
import eu.maxpi.fiverr.questsystem.quests.QuestProgressStepType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class onItemPickup implements Listener {

    @EventHandler
    public void itemPickup(EntityPickupItemEvent event){
        if(!(event.getEntity() instanceof Player p)) return;

        //Get all the quests this player currently has as active
        QuestSystem.loadedQuests.values().stream().filter(q -> q.progress.getOrDefault(p.getName(), -1) != -1).forEach(q -> {
            //Get the current quest step the player is on, if it's not of item collection with this type of item, return
            QuestProgressStep step = q.steps.get(q.progress.get(p.getName()));
            if(step.type != QuestProgressStepType.ITEM_COLLECTION) return;

            if(((ItemStack)step.checkObject).getType() != event.getItem().getItemStack().getType()) return;

            //Add the progress
            step.progress.put(p.getName(), step.progress.getOrDefault(p.getName(), 0L) + event.getItem().getItemStack().getAmount());
        });
    }

}
