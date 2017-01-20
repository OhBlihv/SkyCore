package com.skytonia.SkyCore.cosmetics.objects.options;

import com.skytonia.SkyCore.cosmetics.objects.BaseCosmetic;
import com.skytonia.SkyCore.cosmetics.objects.particles.BaseParticleEffect;
import com.skytonia.SkyCore.cosmetics.objects.particles.SimpleParticleEffect;
import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import com.skytonia.SkyCore.util.file.FlatFile;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 18/08/2016.
 */
public class CosmeticOptions
{
	
	@Getter
	private Map<BaseCosmetic, CosmeticOptionStorage> cosmeticOptionsMap = new HashMap<>();
	
	private final BlankOptionStorage BLANK_OPTIONS = new BlankOptionStorage(null);
	
	//Cosmetic Type Defaults
	private final BaseParticleEffect particleDefaultKey = new SimpleParticleEffect(null, null);
	
	public CosmeticOptions()
	{
		loadOptions();
	}
	
	public void loadOptions()
	{
		cosmeticOptionsMap.clear(); //Remove all old entries in-case of reload situation
		
		FlatFile cfg = FlatFile.getInstance();
		ConfigurationSection defaultSection = cfg.getConfigurationSection("options.defaults");
		
		//Set up generic defaults
		cosmeticOptionsMap.put(particleDefaultKey, new BaseParticleEffect.ParticleOptionStorage(defaultSection.getConfigurationSection("particles.simple")));
		
		//Load other cosmetic-specifics
		
		ConfigurationSection particleTrailSection = cfg.getConfigurationSection("cosmetics.particles");
		for(ParticleEffect particleEffect : ParticleEffect.values())
		{
			ConfigurationSection particleSection = particleTrailSection.getConfigurationSection(particleEffect.name());
			if(particleSection == null || particleSection.getKeys(false).isEmpty())
			{
				continue; //No overriding defaults provided
			}
			
			cosmeticOptionsMap.put(new SimpleParticleEffect(null, particleEffect), new BaseParticleEffect.ParticleOptionStorage(particleSection));
		}
	}
	
	/**
	 * Returns Cosmetic Options/Defaults for the given cosmetic
	 *
	 * @param baseCosmetic Cosmetic to return options/defaults for
	 * @return Option Storage for the requested cosmetic, or the base cosmetic of that type.
	 */
	public CosmeticOptionStorage getOptions(BaseCosmetic baseCosmetic)
	{
		return getOptions(baseCosmetic, null);
	}
	
	/**
	 * Returns Cosmetic Options/Defaults for the given cosmetic that contains a value
	 * for the modifier provided
	 *
	 * @param baseCosmetic Cosmetic to return options/defaults for
	 * @param containsModifier Cosmetic options/defaults should be returned with a value for this modifier
	 * @return Option Storage for the requested cosmetic, or the base cosmetic of that type.
	 */
	public CosmeticOptionStorage getOptions(BaseCosmetic baseCosmetic, CosmeticModifier containsModifier)
	{
		CosmeticOptionStorage cosmeticOptions = null;
		if(baseCosmetic instanceof BaseParticleEffect)
		{
			//Hold a temporary 'best fit' options object when using a CosmeticModifier filter
			//Get most specific options, and override if we find our filter.
			
			//Retrieve the specific defaults for this cosmetic
			if(cosmeticOptionsMap.containsKey(baseCosmetic))
			{
				cosmeticOptions = cosmeticOptionsMap.get(baseCosmetic);
			}
			//Retrieve the most generic defaults
			//If a modifier filter is provided
			if(cosmeticOptions == null || (containsModifier != null && cosmeticOptions.getModifier(containsModifier) == containsModifier.defaultValue))
			{
				//This may still not be defined, but the blank fallback is still provided at this point.
				CosmeticOptionStorage tempCosmeticOptions = cosmeticOptionsMap.get(particleDefaultKey);
				if(cosmeticOptions == null || (tempCosmeticOptions != null && tempCosmeticOptions.getModifier(containsModifier) != containsModifier.defaultValue))
				{
					cosmeticOptions = tempCosmeticOptions;
				}
			}
		}
		
		if(cosmeticOptions == null)
		{
			//If no defaults are given
			//return a defaults class that does not apply any defaults
			cosmeticOptions = BLANK_OPTIONS;
		}
		
		return cosmeticOptions;
	}
	
	public double getModifier(BaseCosmetic baseCosmetic, CosmeticModifier cosmeticModifier)
	{
		return getOptions(baseCosmetic, cosmeticModifier).getModifier(cosmeticModifier);
	}
	
}
