package eu.maxpi.fiverr.questsystem.events;

import eu.maxpi.fiverr.questsystem.QuestSystem;
import eu.maxpi.fiverr.questsystem.quests.QuestProgressStep;
import eu.maxpi.fiverr.questsystem.quests.QuestProgressStepType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class onMobKill implements Listener {

    @EventHandler
    public void entityKill(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player p)) return;
        if(!(event.getEntity() instanceof LivingEntity e)) return;

        //If the entity wasn't killed return
        if(e.getHealth() - event.getFinalDamage() > 0) return;

        //Get all the quests this player currently has as active
        QuestSystem.loadedQuests.values().stream().filter(q -> q.progress.getOrDefault(p.getName(), -1) != -1).forEach(q -> {
            //Get the current quest step the player is on, if it's not of mob kill with this type of mob, return
            QuestProgressStep step = q.steps.get(q.progress.get(p.getName()));
            if(step.type != QuestProgressStepType.MOB_KILL) return;

            if(!((String)step.checkObject).equalsIgnoreCase(event.getEntity().getType().name())) return;

            //Add value
            step.progress.put(p.getName(), step.progress.getOrDefault(p.getName(), 0L) + 1);
        });
    }

}
