package com.skytonia.SkyCore.titles;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import lombok.Getter;
import lombok.Setter;
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
	private WrappedDataWatcher metadata;
	
	@Getter
	private EntityType lineEntity = null;
	
	//Used to mark for deletion or other reason
	@Getter
	@Setter
	private DirtyPlayerType dirtyPlayerType = DirtyPlayerType.CLEAN;
	
	public TagLine(int tagId, String text)
	{
		this.tagId = tagId;
		this.text = text;
		
		setText(text);
	}
	
	public void setText(String text)
	{
		this.text = text;
		
		if(text == null || text.isEmpty())
		{
			if(lineEntity != EntityType.AREA_EFFECT_CLOUD)
			{
				setNewMetadata(EntityType.AREA_EFFECT_CLOUD);
				
				//AreaEffectCloud
				metadata.setObject(5, 0F);
				metadata.setObject(8, EnumWrappers.Particle.SUSPENDED.getId());
			}
		}
		else
		{
			if(lineEntity != EntityType.RABBIT)
			{
				setNewMetadata(EntityType.RABBIT);

				metadata.setObject(12, 99);
			}
		}
		
		metadata.setObject(2, text);
		metadata.setObject(3, text != null && !text.isEmpty());
	}
	
	private void setNewMetadata(EntityType entityType)
	{
		metadata = PetUtil.getDefaultWatcher(null, entityType);
		
		this.lineEntity = entityType;
	}
	
	@Override
	public String toString()
	{
		return "Id=" + tagId + ",Text=" + text;
	}
}
