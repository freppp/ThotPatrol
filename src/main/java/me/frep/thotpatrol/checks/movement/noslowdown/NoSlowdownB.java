package me.frep.thotpatrol.checks.movement.noslowdown;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class NoSlowdownB extends Check {

    public NoSlowdownB(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("NoSlowdownB", "NoSlowdown (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

}