package com.skytonia.SkyCore.titles;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public class TagLine
{
	
	@Getter
	private final int tagId;
	
	@Getter
	private final UUID uuid = UUID.randomUUID();
	
	@Getter
	private String text;
	
	@Getter
	private final WrappedDataWatcher metadata;
	
	//Used to mark for deletion or other reason
	@Getter
	@Setter
	private DirtyPlayerType dirtyPlayerType = DirtyPlayerType.CLEAN;
	
	public TagLine(int tagId, String text)
	{
		this.tagId = tagId;
		this.text = text;
		
		metadata = PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.AREA_EFFECT_CLOUD);
		
		//Entity
		metadata.setObject(2, text);
		metadata.setObject(3, true);
		
		//AreaEffectCloud
		metadata.setObject(5, 0F);
		metadata.setObject(8, EnumWrappers.Particle.SUSPENDED.getId());
	}
	
	public void setText(String text)
	{
		metadata.setObject(2, text);
	}
	
}
