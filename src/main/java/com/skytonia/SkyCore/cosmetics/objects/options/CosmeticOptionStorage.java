package com.skytonia.SkyCore.cosmetics.objects.options;

import com.skytonia.SkyCore.cosmetics.objects.BaseCosmetic;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 18/08/2016.
 */
public abstract class CosmeticOptionStorage
{
	
	private Map<CosmeticModifier, Double> modifierMap = new EnumMap<>(CosmeticModifier.class);
	
	public CosmeticOptionStorage(CosmeticModifier[] cosmeticModifiers, Double[] values) throws IllegalArgumentException
	{
		if(cosmeticModifiers.length != values.length)
		{
			throw new IllegalArgumentException("Modifier Array Length did not match Value Array Length!");
		}
		
		for(int i = 0;i < cosmeticModifiers.length;i++)
		{
			modifierMap.put(cosmeticModifiers[i], values[i]);
		}
	}
	
	public CosmeticOptionStorage(ConfigurationSection configurationSection)
	{
		//Invalid configuration section, cannot use.
		if(configurationSection == null || configurationSection.getKeys(false).isEmpty())
		{
			return;
		}
		
		for(CosmeticModifier cosmeticModifier : CosmeticModifier.values())
		{
			//If the default is not set in configuration, default to our hardcoded value
			double value = cosmeticModifier.defaultValue;
			if(configurationSection.contains(cosmeticModifier.configurationKey))
			{
				value = configurationSection.getDouble(cosmeticModifier.configurationKey);
			}
			//'Simple' Configuration (No Min/Max Provided)
			//Strip any sub-section keys and use the root key provided
			else if(configurationSection.contains(cosmeticModifier.configurationKey.split("[.]")[0]))
			{
				value = configurationSection.getDouble(cosmeticModifier.configurationKey.split("[.]")[0]);
			}
			//Else, use default value
			
			if(value <= 0 &&
				   (cosmeticModifier != CosmeticModifier.HEIGHT && cosmeticModifier != CosmeticModifier.HEIGHT_MIN && cosmeticModifier != CosmeticModifier.HEIGHT_MAX))
			{
				throw new IllegalArgumentException("Default value provided for '" + cosmeticModifier.name() + "' -> '" + value + "' is invalid." +
					                                   " Please use a value above 0 for this option.");
			}
			
			modifierMap.put(cosmeticModifier, value);
		}
	}
	
	public abstract void applyDefaults(BaseCosmetic baseCosmetic);
	
	public double getModifier(CosmeticModifier cosmeticModifier)
	{
		return modifierMap.get(cosmeticModifier);
	}
	
	@Override
	public String toString()
	{
		return getClass().getName() + ": " + modifierMap.toString();
	}
	
}
