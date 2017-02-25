package com.skytonia.SkyCore.cheapobjects.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 19/05/2016.
 */
public abstract class CheapPlayer implements Player
{

	/*
	 * Player Retrieval/Storage
	 */

	public Player getPlayer()
	{
		if(isOnline())
		{
			return player.get();
		}
		player = new WeakReference<>(Bukkit.getPlayerExact(playerName));
		return player.get();
	}
	
	public void setPlayer(Player player)
	{
		this.player = new WeakReference<>(player);
	}
	public boolean isOnline()
	{
		return player != null && player.get() != null && player.get().isOnline();
	}
	
	final UUID playerUUID;
	
	final String playerName;
	private WeakReference<Player> player; //I wouldn't normally do this, but this needs easy access to a player's location.
	
	@Deprecated
	public CheapPlayer(String playerName)
	{
		this.playerName = playerName;
		
		this.playerUUID = getPlayer().getUniqueId();
	}
	
	public CheapPlayer(Player player)
	{
		this.playerName = player.getName();
		this.playerUUID = player.getUniqueId();
		
		setPlayer(player);
	}
	
	@Override
	public String getName()
	{
		return playerName;
	}
	
	@Override
	public UUID getUniqueId()
	{
		return playerUUID;
	}

	public abstract Object getPlayerConnection();
	
	//Implementations will need to ensure this is an instance of their versioned packet and not another object
	public abstract void queuePacket(Object packet);

	@Override
	public boolean equals(Object object)
	{
		//Attempt to be a Player object in comparisons
		if(object instanceof Player)
		{
			//BUtil.logInfo("Is Equal? " + (player.get().equals(object)));
			return player.get().equals(object);
		}
		else
		{
			// == allows memory addresses to be compared, which we are completely okay with.
			return object instanceof CheapPlayer && ((CheapPlayer) object).playerName == this.playerName;
		}
	}
	
	/*
	 * Player Methods
	 */
	
	@Override
	public long getFirstPlayed()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public long getLastPlayed()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasPlayedBefore()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isBanned()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setBanned(boolean banned)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isWhitelisted()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setWhitelisted(boolean value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public String getDisplayName()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setDisplayName(String name)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public String getPlayerListName()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setPlayerListName(String name)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setCompassTarget(Location loc)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Location getCompassTarget()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public InetSocketAddress getAddress()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isConversing()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void acceptConversationInput(String input)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean beginConversation(Conversation conversation)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void abandonConversation(Conversation conversation)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendRawMessage(String message)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void kickPlayer(String message)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void chat(String msg)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean performCommand(String command)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isSneaking()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setSneaking(boolean sneak)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isSprinting()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setSprinting(boolean sprinting)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void saveData()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void loadData()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setSleepingIgnored(boolean isSleeping)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isSleepingIgnored()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void playNote(Location loc, byte instrument, byte note)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void playNote(Location loc, Instrument instrument, Note note)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void playSound(Location location, Sound sound, float volume, float pitch)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void playSound(Location location, String sound, float volume, float pitch)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void playEffect(Location loc, Effect effect, int data)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public <T> void playEffect(Location loc, Effect effect, T data)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendBlockChange(Location loc, Material material, byte data)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendBlockChange(Location loc, int material, byte data)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendMap(MapView map)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void updateInventory()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void awardAchievement(Achievement achievement)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void removeAchievement(Achievement achievement)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasAchievement(Achievement achievement)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void incrementStatistic(Statistic statistic) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void decrementStatistic(Statistic statistic) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getStatistic(Statistic statistic) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType, int amount)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setStatistic(Statistic statistic, EntityType entityType, int newValue)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setPlayerTime(long time, boolean relative)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public long getPlayerTime()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public long getPlayerTimeOffset()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isPlayerTimeRelative()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void resetPlayerTime()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setPlayerWeather(WeatherType type)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public WeatherType getPlayerWeather()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void resetPlayerWeather()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void giveExp(int amount)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void giveExpLevels(int amount)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public float getExp()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setExp(float exp)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getLevel()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setLevel(int level)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getTotalExperience()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setTotalExperience(int exp)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public float getExhaustion()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setExhaustion(float value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public float getSaturation()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setSaturation(float value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getFoodLevel()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setFoodLevel(int value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Location getBedSpawnLocation()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setBedSpawnLocation(Location location)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setBedSpawnLocation(Location location, boolean force)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean getAllowFlight()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setAllowFlight(boolean flight)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void hidePlayer(Player player)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void showPlayer(Player player)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean canSee(Player player)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Location getLocation()
	{
		return player.get().getLocation();
	}
	
	@Override
	public Location getLocation(Location loc)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setVelocity(Vector velocity)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Vector getVelocity()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isOnGround()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public World getWorld()
	{
		return getPlayer().getWorld();
	}
	
	@Override
	public boolean teleport(Location location)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean teleport(Entity destination)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getEntityId()
	{
		return getPlayer().getEntityId();
	}
	
	@Override
	public int getFireTicks()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getMaxFireTicks()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setFireTicks(int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void remove()
	{
		getPlayer().remove();
	}
	
	@Override
	public boolean isDead()
	{
		return getPlayer().isDead();
	}
	
	@Override
	public boolean isValid()
	{
		return getPlayer().isValid();
	}
	
	@Override
	public void sendMessage(String message)
	{
		getPlayer().sendMessage(message);
	}
	
	@Override
	public void sendMessage(String[] messages)
	{
		getPlayer().sendMessage(messages);
	}
	
	@Override
	public Server getServer()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Entity getPassenger()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean setPassenger(Entity passenger)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isEmpty()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean eject()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public float getFallDistance()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setFallDistance(float distance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setLastDamageCause(EntityDamageEvent event)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public EntityDamageEvent getLastDamageCause()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getTicksLived()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setTicksLived(int value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void playEffect(EntityEffect type)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public EntityType getType()
	{
		return EntityType.PLAYER;
	}
	
	@Override
	public boolean isInsideVehicle()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean leaveVehicle()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Entity getVehicle()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setCustomName(String name)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public String getCustomName()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setCustomNameVisible(boolean flag)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isCustomNameVisible()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setGlowing(boolean b)
	{
	
	}
	
	@Override
	public boolean isGlowing()
	{
		return false;
	}
	
	@Override
	public void setInvulnerable(boolean b)
	{
	
	}
	
	@Override
	public boolean isInvulnerable()
	{
		return false;
	}
	
	@Override
	public boolean isSilent()
	{
		return false;
	}
	
	@Override
	public void setSilent(boolean b)
	{
	
	}
	
	@Override
	public boolean isFlying()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setFlying(boolean value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setFlySpeed(float value) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setWalkSpeed(float value) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public float getFlySpeed()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public float getWalkSpeed()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setTexturePack(String url)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setResourcePack(String url)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Scoreboard getScoreboard()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException
	{
		getPlayer().setScoreboard(scoreboard);
	}
	
	@Override
	public boolean isHealthScaled()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setHealthScaled(boolean scale)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setHealthScale(double scale) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public double getHealthScale()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Entity getSpectatorTarget()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setSpectatorTarget(Entity entity)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendTitle(String title, String subtitle)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void resetTitle()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void spawnParticle(Particle particle, Location location, int i)
	{
	
	}
	
	@Override
	public void spawnParticle(Particle particle, double v, double v1, double v2, int i)
	{
	
	}
	
	@Override
	public <T> void spawnParticle(Particle particle, Location location, int i, T t)
	{
	
	}
	
	@Override
	public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t)
	{
	
	}
	
	@Override
	public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2)
	{
	
	}
	
	@Override
	public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5)
	{
	
	}
	
	@Override
	public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t)
	{
	
	}
	
	@Override
	public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t)
	{
	
	}
	
	@Override
	public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3)
	{
	
	}
	
	@Override
	public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6)
	{
	
	}
	
	@Override
	public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t)
	{
	
	}
	
	@Override
	public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t)
	{
	
	}
	
	@Override
	public boolean getAffectsSpawning()
	{
		return false;
	}
	
	@Override
	public void setAffectsSpawning(boolean b)
	{
	
	}
	
	@Override
	public int getViewDistance()
	{
		return 0;
	}
	
	@Override
	public void setViewDistance(int i)
	{
	
	}
	
	@Override
	public void setResourcePack(String s, String s1)
	{
	
	}
	
	@Override
	public PlayerResourcePackStatusEvent.Status getResourcePackStatus()
	{
		return null;
	}
	
	@Override
	public String getResourcePackHash()
	{
		return null;
	}
	
	@Override
	public boolean hasResourcePack()
	{
		return false;
	}
	
	@Override
	public Spigot spigot()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Location getOrigin()
	{
		return null;
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public PlayerInventory getInventory()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Inventory getEnderChest()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public MainHand getMainHand()
	{
		return null;
	}
	
	@Override
	public boolean setWindowProperty(InventoryView.Property prop, int value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public InventoryView getOpenInventory()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public InventoryView openInventory(Inventory inventory)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public InventoryView openWorkbench(Location location, boolean force)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public InventoryView openEnchanting(Location location, boolean force)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void openInventory(InventoryView inventory)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public InventoryView openMerchant(Villager villager, boolean b)
	{
		return null;
	}
	
	@Override
	public void closeInventory()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public ItemStack getItemInHand()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setItemInHand(ItemStack item)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public ItemStack getItemOnCursor()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setItemOnCursor(ItemStack item)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isSleeping()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getSleepTicks()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public GameMode getGameMode()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setGameMode(GameMode mode)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isBlocking()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getExpToLevel()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public double getEyeHeight()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public double getEyeHeight(boolean ignoreSneaking)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Location getEyeLocation()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Block getTargetBlock(Set<Material> transparent, int maxDistance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getRemainingAir()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setRemainingAir(int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getMaximumAir()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setMaximumAir(int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int getMaximumNoDamageTicks()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setMaximumNoDamageTicks(int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public double getLastDamage()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int _INVALID_getLastDamage()
	{
		return 0;
	}
	
	@Override
	public void setLastDamage(double damage)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void _INVALID_setLastDamage(int i)
	{
	
	}
	
	@Override
	public int getNoDamageTicks()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setNoDamageTicks(int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Player getKiller()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean addPotionEffect(PotionEffect effect)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean addPotionEffect(PotionEffect effect, boolean force)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean addPotionEffects(Collection<PotionEffect> effects)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Collection<PotionEffect> getActivePotionEffects()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasLineOfSight(Entity other)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean getRemoveWhenFarAway()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setRemoveWhenFarAway(boolean remove)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public EntityEquipment getEquipment()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setCanPickupItems(boolean pickup)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean getCanPickupItems()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isLeashed()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Entity getLeashHolder() throws IllegalStateException
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean setLeashHolder(Entity holder)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isGliding()
	{
		return false;
	}
	
	@Override
	public void setGliding(boolean b)
	{
	
	}
	
	@Override
	public void setAI(boolean b)
	{
	
	}
	
	@Override
	public boolean hasAI()
	{
		return false;
	}
	
	@Override
	public void setCollidable(boolean b)
	{
	
	}
	
	@Override
	public boolean isCollidable()
	{
		return false;
	}
	
	@Override
	public int getArrowsStuck()
	{
		return 0;
	}
	
	@Override
	public void setArrowsStuck(int i)
	{
	
	}
	
	@Override
	public void damage(double amount)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void _INVALID_damage(int i)
	{
	
	}
	
	@Override
	public void damage(double amount, Entity source)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void _INVALID_damage(int i, Entity entity)
	{
	
	}
	
	@Override
	public double getHealth()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int _INVALID_getHealth()
	{
		return 0;
	}
	
	@Override
	public void setHealth(double health)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void _INVALID_setHealth(int i)
	{
	
	}
	
	@Override
	public double getMaxHealth()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public int _INVALID_getMaxHealth()
	{
		return 0;
	}
	
	@Override
	public void setMaxHealth(double health)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void _INVALID_setMaxHealth(int i)
	{
	
	}
	
	@Override
	public void resetMaxHealth()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public List<MetadataValue> getMetadata(String metadataKey)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasMetadata(String metadataKey)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isPermissionSet(String name)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isPermissionSet(Permission perm)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasPermission(String name)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean hasPermission(Permission perm)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void removeAttachment(PermissionAttachment attachment)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void recalculatePermissions()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public boolean isOp()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void setOp(boolean value)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public Set<String> getListeningPluginChannels()
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectile)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity)
	{
		throw new IllegalArgumentException("Operation not Permitted.");
	}
	
	@Override
	public void sendMessage(BaseComponent baseComponent)
	{
	
	}
	
	@Override
	public void sendMessage(BaseComponent... baseComponents)
	{
	
	}
	
	@Override
	public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents)
	{
	
	}
	
	@Override
	public void setPlayerListHeaderFooter(BaseComponent[] baseComponents, BaseComponent[] baseComponents1)
	{
	
	}
	
	@Override
	public void setPlayerListHeaderFooter(BaseComponent baseComponent, BaseComponent baseComponent1)
	{
	
	}
	
	@Override
	public void setTitleTimes(int i, int i1, int i2)
	{
	
	}
	
	@Override
	public void setSubtitle(BaseComponent[] baseComponents)
	{
	
	}
	
	@Override
	public void setSubtitle(BaseComponent baseComponent)
	{
	
	}
	
	@Override
	public void showTitle(BaseComponent[] baseComponents)
	{
	
	}
	
	@Override
	public void showTitle(BaseComponent baseComponent)
	{
	
	}
	
	@Override
	public void showTitle(BaseComponent[] baseComponents, BaseComponent[] baseComponents1, int i, int i1, int i2)
	{
	
	}
	
	@Override
	public void showTitle(BaseComponent baseComponent, BaseComponent baseComponent1, int i, int i1, int i2)
	{
	
	}
	
	@Override
	public void sendTitle(com.destroystokyo.paper.Title title)
	{
	
	}
	
	@Override
	public void updateTitle(com.destroystokyo.paper.Title title)
	{
	
	}

	@Override
	public void hideTitle()
	{
	
	}
	
	@Override
	public AttributeInstance getAttribute(Attribute attribute)
	{
		return null;
	}
}
