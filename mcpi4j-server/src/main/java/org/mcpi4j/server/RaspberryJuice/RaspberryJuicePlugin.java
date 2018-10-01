package org.mcpi4j.server.RaspberryJuice;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcpi4j.server.*;

import java.util.*;

public class RaspberryJuicePlugin extends JavaPlugin implements Listener {

	public static final Set<Material> blockBreakDetectionTools = EnumSet.of(
			Material.DIAMOND_SWORD,
			Material.GOLD_SWORD,
			Material.IRON_SWORD,
			Material.STONE_SWORD,
			Material.WOOD_SWORD);

	public Player hostPlayer = null;
	private LocationType locationType;
	private HitClickType hitClickType;
	private McpiServer mcpiServer;
    RaspberryJuiceApi api;

	public LocationType getLocationType() {
		return locationType;
	}
	public HitClickType getHitClickType() {
		return hitClickType;
	}

	public void onEnable() {
		//save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        //get port from config.yml
		int port = this.getConfig().getInt("port");
		getLogger().info("Using port " + Integer.toString(port));

		//get location type (ABSOLUTE or RELATIVE) from config.yml
		String location = this.getConfig().getString("location").toUpperCase();
		try {
			locationType = LocationType.valueOf(location);
		} catch(IllegalArgumentException e) {
			getLogger().warning("warning - location value in config.yml should be ABSOLUTE or RELATIVE - '" + location + "' found");
			locationType = LocationType.valueOf("RELATIVE");
		}
		getLogger().info("Using " + locationType.name() + " locations");

		//get hit click type (LEFT, RIGHT or BOTH) from config.yml
		String hitClick = this.getConfig().getString("hitclick").toUpperCase();
		try {
			hitClickType = HitClickType.valueOf(hitClick);
		} catch(IllegalArgumentException e) {
			getLogger().warning("warning - hitclick value in config.yml should be LEFT, RIGHT or BOTH - '" + hitClick + "' found");
			hitClickType = HitClickType.valueOf("RIGHT");
		}
		getLogger().info("Using " + hitClickType.name() + " clicks for hits");

		//start tcp listener
        api = new RaspberryJuiceApi(this);
        mcpiServer = new McpiServer(api, 4711);

		//register the events
		getServer().getPluginManager().registerEvents(this, this);
		//setup the schedule to called the tick handler
//		getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// only react to events which are of the correct type
		switch(hitClickType) {
			case BOTH:
				if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) return;
				break;
			case LEFT:
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
				break;
			case RIGHT:
				if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
				break;
		}
		ItemStack currentTool = event.getItem();
		if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
			return;
		}
        api.queuePlayerInteractEvent(event);
	}

	@EventHandler(ignoreCancelled=true)
	public void onChatPosted(AsyncPlayerChatEvent event) {
        api.queueChatPostedEvent(event);
	}

	public Player getNamedPlayer(String name) {
		if (name == null) return null;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if (name.equals(player.getPlayerListName())) {
				return player;
			}
		}
		return null;
	}

	public Player getHostPlayer() {
		if (hostPlayer != null) return hostPlayer;
		for(Player player : Bukkit.getOnlinePlayers()) {
			return player;
		}
		return null;
	}

	//get entity by id - DONE to be compatible with the pi it should be changed to return an entity not a player...
	public Entity getEntity(int id) {
		for (Player p: getServer().getOnlinePlayers()) {
			if (p.getEntityId() == id) {
				return p;
			}
		}
		//check all entities in host player's world
		Player player = getHostPlayer();
		World w = player.getWorld();
		for (Entity e : w.getEntities()) {
			if (e.getEntityId() == id) {
				return e;
			}
		}
		return null;
	}


	public void onDisable() {
	    mcpiServer.stop();
		getLogger().info("Raspberry Juice Stopped");
	}

}

