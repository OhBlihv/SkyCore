package com.skytonia.SkyCore.cosmetics.pets;

import com.skytonia.SkyCore.cosmetics.objects.ActiveCosmetic;
import com.skytonia.SkyCore.cosmetics.objects.BaseCosmetic;
import com.skytonia.SkyCore.cosmetics.pets.configuration.PlayerPetConfiguration;
import com.skytonia.SkyCore.cosmetics.pets.entities.PetZombieSource;
import com.skytonia.SkyCore.titles.TagController;
import com.skytonia.SkyCore.titles.TaggedPlayer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class MiniPet extends BaseCosmetic
{
	
	private static final int UPDATE_RATE = 1;
	private static final String COSMETIC_PREFIX = "PET_";
	
	//
	
	@Setter
	@Getter
	private ActiveCosmetic attachedCosmetic;
	
	private final Player attachedPlayer;
	
	private final PlayerPetConfiguration petConfiguration;
	
	private PetZombieSource petEntity;
	
	private final TaggedPlayer petTags;
	
	public MiniPet(Player attachedPlayer, PlayerPetConfiguration petConfiguration)
	{
		super(COSMETIC_PREFIX, UPDATE_RATE);
		
		this.attachedPlayer = attachedPlayer;
		this.petConfiguration = petConfiguration;
		
		World world = ((CraftWorld) attachedPlayer.getWorld()).getHandle();
		
		petEntity = new PetZombieSource(world, attachedPlayer, petConfiguration);
		
		Location spawnLocation = attachedPlayer.getLocation();
		petEntity.setLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch());
		
		world.addEntity(petEntity);
		
		petEntity.initialize();
		
		petTags = TagController.getInstance().getTagForEntity(petEntity.getBukkitEntity());
		petTags.setLine(0, ""); //'Spacer'
		
		setPetName(petConfiguration.getPetName());
	}
	
	public void setPetName(String name)
	{
		if(name == null)
		{
			petTags.setHideTags(true);
		}
		else
		{
			petTags.setLine(1, name);
			petTags.setHideTags(false);
		}
	}
	
	@Override
	public Location getLocation()
	{
		return petEntity.getBukkitEntity().getLocation();
	}
	
	@Override
	public void onTick(long tick, Location location)
	{
		throw new IllegalArgumentException("Unsupported Operation onTick() - Nearby Players are not tracked, please provide Nearby Players.");
	}
	
	@Override
	public void onTick(long tick, Location location, Collection<Player> nearbyPlayers)
	{
	
	}
	
	@Override
	public void removeCosmetic()
	{
	
	}
	
	@Override
	public boolean showToNearbyPlayer(Player player)
	{
		return true;
	}
	
	@Override
	public boolean removeFromNearbyPlayer(Player player)
	{
		return true;
	}
}
