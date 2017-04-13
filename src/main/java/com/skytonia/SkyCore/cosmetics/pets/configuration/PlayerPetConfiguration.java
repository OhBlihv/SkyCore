package com.skytonia.SkyCore.cosmetics.pets.configuration;

import com.skytonia.SkyCore.items.construction.ItemContainer;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Chris Brown (OhBlihv) on 4/13/2017.
 */
public class PlayerPetConfiguration
{
	
	//TODO: Set players pet name to the default name in the config
	
	@Getter
	@Setter
	private PetConfiguration defaultConfiguration = null;
	
	@Getter
	@Setter
	private String petName = null;
	
	@Getter
	@Setter
	private boolean baby = false;
	
	public PlayerPetConfiguration(PetConfiguration petConfiguration, String petName, boolean baby)
	{
		this.defaultConfiguration = petConfiguration;
		
		if(petName == null)
		{
			petName = petConfiguration.defaultName;
		}
		
		this.petName = petName;
		
		this.baby = baby;
	}
	
	public boolean hasPet()
	{
		return defaultConfiguration != null;
	}
	
	public String getConfigName()
	{
		return defaultConfiguration.configName;
	}
	
	public String getDefaultName()
	{
		return defaultConfiguration.defaultName;
	}
	
	public ItemContainer getPetSkull()
	{
		return defaultConfiguration.petSkull;
	}
	
	public double getPetSpeed()
	{
		return defaultConfiguration.speed;
	}
	
}
