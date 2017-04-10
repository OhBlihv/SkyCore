package com.skytonia.SkyCore.cosmetics.pets;

import com.comphenix.packetwrapper.AbstractPacket;
import com.skytonia.SkyCore.cosmetics.objects.ActiveCosmetic;
import com.skytonia.SkyCore.cosmetics.objects.BaseCosmetic;
import com.skytonia.SkyCore.cosmetics.pets.fakeentities.FakeEntity;
import com.skytonia.SkyCore.cosmetics.pets.fakeentities.FakeLivingEntity;
import com.skytonia.SkyCore.cosmetics.pets.fakeentities.FakeZombie;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
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
	
	private final PetConfiguration petConfiguration;
	
	private FakeLivingEntity petEntity;
	private FakeEntity  snowBallOne,
						snowBallTwo;
	
	public MiniPet(Player attachedPlayer, PetConfiguration petConfiguration)
	{
		super(COSMETIC_PREFIX, UPDATE_RATE);
		
		this.attachedPlayer = attachedPlayer;
		this.petConfiguration = petConfiguration;
		
		petEntity = new FakeZombie(EntityType.ZOMBIE, attachedPlayer.getLocation(), false);
		
		snowBallOne = new FakeEntity(EntityType.SNOWBALL, attachedPlayer.getLocation());
		snowBallTwo = new FakeEntity(EntityType.SNOWBALL, attachedPlayer.getLocation());
	}
	
	@Override
	public Location getLocation()
	{
		return petEntity.getLocation();
	}
	
	@Override
	public void onTick(long tick, Location location)
	{
		throw new IllegalArgumentException("Unsupported Operation onTick() - Nearby Players are not tracked, please provide Nearby Players.");
	}
	
	@Override
	public void onTick(long tick, Location location, Collection<Player> nearbyPlayers)
	{
		//Force teleport the pet to the correct location eventually
		if(tick % 500 == 0)
		{
			AbstractPacket teleportPacket = petEntity.getTeleportPacket();
			for(Player player : nearbyPlayers)
			{
				teleportPacket.sendPacket(player);
			}
		}
		else
		{
			AbstractPacket movePacket = petEntity.updateNavigation();
			if(movePacket != null)
			{
				for(Player player : nearbyPlayers)
				{
					movePacket.sendPacket(player);
				}
			}
		}
	}
	
	@Override
	public void removeCosmetic()
	{
	
	}
	
	@Override
	public boolean showToNearbyPlayer(Player player)
	{
		BUtil.logInfo(player.getName() + " is in range. Spawning...");
		
		petEntity.getSpawnPacket().sendPacket(player);
		snowBallOne.getSpawnPacket().sendPacket(player);
		snowBallTwo.getSpawnPacket().sendPacket(player);
		
		petEntity.addPassenger(snowBallOne.getEntityId());
		petEntity.addPassenger(snowBallTwo.getEntityId());
		
		return true;
	}
	
	@Override
	public boolean removeFromNearbyPlayer(Player player)
	{
		BUtil.logInfo(player.getName() + " is out of range. Destroying...");
		
		petEntity.getDestroyPacket().sendPacket(player);
		snowBallOne.getDestroyPacket().sendPacket(player);
		snowBallTwo.getDestroyPacket().sendPacket(player);
		
		return true;
	}
}
