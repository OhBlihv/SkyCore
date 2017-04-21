package com.skytonia.SkyCore.cosmetics.pets.entities;

import com.skytonia.SkyCore.cosmetics.pets.configuration.PlayerPetConfiguration;
import com.skytonia.SkyCore.cosmetics.pets.pathfinders.PathfinderPetActions;
import com.skytonia.spigot.entities.OverriddenEntity;
import lombok.Getter;
import net.minecraft.server.v1_9_R2.AxisAlignedBB;
import net.minecraft.server.v1_9_R2.DamageSource;
import net.minecraft.server.v1_9_R2.EntityZombie;
import net.minecraft.server.v1_9_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R2.SoundEffect;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 4/13/2017.
 */
public class PetZombieSource extends EntityZombie implements OverriddenEntity
{
	
	private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false);
	
	@Getter
	private final Player attachedPlayer;
	
	@Getter
	private final PlayerPetConfiguration petConfiguration;
	
	public PetZombieSource(World world, Player attachedPlayer, PlayerPetConfiguration petConfiguration)
	{
		super(world);
		
		this.attachedPlayer = attachedPlayer;
		this.petConfiguration = petConfiguration;
		
		fireProof = true;
		maxFireTicks = 0;
		
		setBaby(petConfiguration.isBaby());
	}
	
	public void initialize()
	{
		setUpGoalSelector(goalSelector);
		setUpGoalSelector(targetSelector);
		
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(1, new PathfinderPetActions(this, ((CraftPlayer) attachedPlayer).getHandle(), petConfiguration.getPetSpeed()));
		
		Zombie bukkitZombie = (Zombie) getBukkitEntity();
		bukkitZombie.getEquipment().setHelmet(petConfiguration.getPetSkull().toItemStack());
		
		bukkitZombie.addPotionEffect(INVISIBILITY);
	}
	
	private void setUpGoalSelector(PathfinderGoalSelector goalSelector)
	{
		try
		{
			Field selectorSet = goalSelector.getClass().getDeclaredField("b");
			selectorSet.setAccessible(true);
			
			((Collection) selectorSet.get(goalSelector)).clear();
			
			selectorSet = goalSelector.getClass().getDeclaredField("c");
			selectorSet.setAccessible(true);
			
			((Collection) selectorSet.get(goalSelector)).clear();
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * NMS Accessors/Helpers
	 */
	
	public void jump()
	{
		this.motY = 0.30F;
		
		this.impulse = true;
	}
	
	public boolean getBd()
	{
		return bd;
	}
	
	/*
	 * NMS Overrides
	 */
	
	public void c(double d0)
	{
		this.getNavigation().a(d0);
		this.moveController.a(this.moveController.d(), this.moveController.e(), this.moveController.f(), d0);
	}
	
	public void recalcPosition()
	{
		AxisAlignedBB boundingBox = this.getBoundingBox();
		
		this.locX = (boundingBox.a + boundingBox.d) / 2.0D;
		this.locY = boundingBox.b - 1.5D;
		this.locZ = (boundingBox.c + boundingBox.f) / 2.0D;
	}
	
	@Override
	public boolean damageEntity(DamageSource damagesource, float f)
	{
		return false;
	}
	
	protected SoundEffect G()
	{
		return null;
	}
	
	protected SoundEffect bS()
	{
		return null;
	}
	
	protected SoundEffect bT()
	{
		return null;
	}
	
	@Override
	public int getEntityTypeId()
	{
		return EntityType.ZOMBIE.getTypeId();
	}
}
