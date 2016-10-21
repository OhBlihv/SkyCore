package com.skytonia.SkyCore.cosmetics.objects.particles;

import com.skytonia.SkyCore.cosmetics.objects.BaseCosmetic;
import com.skytonia.SkyCore.cosmetics.objects.options.BlankOptionStorage;
import com.skytonia.SkyCore.cosmetics.objects.options.CosmeticModifier;
import com.skytonia.SkyCore.cosmetics.objects.options.CosmeticOptionStorage;
import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Chris Brown (OhBlihv) on 6/08/2016.
 */
public abstract class BaseParticleEffect extends BaseCosmetic
{
	
	public static class ParticleOptionStorage extends CosmeticOptionStorage
	{
		
		public ParticleOptionStorage(ConfigurationSection configurationSection)
		{
			super(configurationSection);
		}
		
		@Override
		public void applyDefaults(BaseCosmetic baseCosmetic)
		{
			if(baseCosmetic instanceof BaseParticleEffect)
			{
				BaseParticleEffect particleCosmetic = (BaseParticleEffect) baseCosmetic;
				
				particleCosmetic.setHeight(getModifier(CosmeticModifier.HEIGHT));
				particleCosmetic.setLength((int) getModifier(CosmeticModifier.LENGTH));
				particleCosmetic.setSpeed((int) getModifier(CosmeticModifier.SPEED));
			}
		}
		
	}
	
	
	
	ParticleEffect particleEffect;
	
	@Setter(AccessLevel.PACKAGE)
	double  height;
	@Setter(AccessLevel.PACKAGE)
	int     length,
			speed;
	
	/**
	 * Comparison Constructor
	 * Used 
	 *
	 * @param particleEffect
	 */
	public BaseParticleEffect(ParticleEffect particleEffect)
	{
		super("comparison", 40);
		
		this.particleEffect = particleEffect;
	}
	
	public BaseParticleEffect(String displayname, ParticleEffect particleEffect)
	{
		this(displayname, particleEffect, null);
	}
	
	public BaseParticleEffect(String displayName, ParticleEffect particleEffect, CosmeticOptionStorage cosmeticOptions)
	{
		//Default to 40 ticks between player updates
		super(displayName, 40);
		
		this.particleEffect = particleEffect;
		
		if(cosmeticOptions == null)
		{
			//cosmeticOptions = BlivTrails.getInstance().getCosmeticManager().getCosmeticDefaults(this);
			cosmeticOptions = new BlankOptionStorage();
		}
		
		cosmeticOptions.applyDefaults(this);
	}
	
	@Override
	public boolean supportsModifier(CosmeticModifier cosmeticModifier)
	{
		switch(cosmeticModifier)
		{
			case HEIGHT:
			case LENGTH:
			case SPEED:
				return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object object)
	{
		//Only one cosmetic of each particle type is supported
		return object instanceof BaseParticleEffect && particleEffect == ((BaseParticleEffect) object).particleEffect;
	}
	
}
