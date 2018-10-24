package com.skytonia.SkyCore.items.heads;

import com.skytonia.SkyCore.items.HeadUtil;
import com.skytonia.SkyCore.items.construction.ItemContainer;
import com.skytonia.SkyCore.items.construction.ItemContainerConstructor;
import com.skytonia.SkyCore.util.StaticNMS;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

/**
 * Created by Chris Brown (OhBlihv) on 12/29/2016.
 */
public class StoredHeads
{
	
	@RequiredArgsConstructor
	public static class StoredHead
	{
		
		private final String skullTexture;
		
		public ItemContainer asItemContainer()
		{
			return new ItemContainerConstructor.ItemContainerBuilder()
				       .material(StaticNMS.getNMSItemUtil().getSkullMaterial())
					   .damage(3)
				       .skullTexture(skullTexture).build();
		}
		
		public ItemStack buildHead()
		{
			return HeadUtil.getHead(skullTexture);
		}
		
		public ItemStack updateHead(ItemStack itemStack)
		{
			return HeadUtil.setHeadTexture(itemStack, skullTexture);
		}
		
	}
	
	/*
	* Head Textures
	*/
	
	public static final StoredHead
	
	PIG         = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="),
	
	CHICKEN     = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="),
	
	OCELOT      = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"),
	
	COW         = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19"),
	
	SHEEP       = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"),
	
	WOLF        = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OGNiZDUyMWNiMTg1YjI3NjcyY2U0MzBmYzQ4NTBkNzU5NWM2ZTg3NzhlMDcyNzFjOTU3OWVkMWY1YWZiNSJ9fX0="),
	
	RABBIT      = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VjMjQyZTY2N2FlZTQ0NDkyNDEzZWY0NjFiODEwY2FjMzU2Yjc0ZDg3MThlNWNlYzFmODkyYTZiNDNlNWUxIn19fQ=="),
	
	ZOMBIE      = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="),
	
	SKELETON    = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1YmU2YTNjMDE1OWQyYzFmM2IxZTRlMWQ4Mzg0YjZmN2ViYWM5OTNkNThiMTBiOWY4OTg5Yzc4YTIzMiJ9fX0="),
	
	SPIDER      = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1NDE1NDFkYWFmZjUwODk2Y2QyNThiZGJkZDRjZjgwYzNiYTgxNjczNTcyNjA3OGJmZTM5MzkyN2U1N2YxIn19fQ=="),
	
	CREEPER     = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ZWY4MzYzODlhZjk5MzE1OGFiYTI3ZmYzN2I2NTY3MTg1ZjdhNzIxY2E5MGZkZmViOTM3YTdjYjU3NDcifX19"),
	
	ENDERMAN    = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="),
	
	SLIME       = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ=="),
	
	BLAZE       = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="),
	
	PIG_ZOMBIE  = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ=="),

	SQUID       = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ"),

	MUSHROOM_COW= new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYzYxYjk3NTdhN2I4M2UwM2NkMjUwN2EyMTU3OTEzYzJjZjAxNmU3YzA5NmE0ZDZjZjFmZTFiOGRiIn19fQ"),

	MAGMA_CUBE  = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0="),

	IRON_GOLEM  = new StoredHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19")

	;
	
	public static StoredHead toMobSkull(EntityType entityType)
	{
		switch(entityType)
		{
			case PIG:           return PIG;
			case CHICKEN:       return CHICKEN;
			case OCELOT:        return OCELOT;
			case COW:           return COW;
			case SHEEP:         return SHEEP;
			case WOLF:          return WOLF;
			case RABBIT:        return RABBIT;
			case ZOMBIE:        return ZOMBIE;
			case SKELETON:      return SKELETON;
			case SPIDER:        return SPIDER;
			case CREEPER:       return CREEPER;
			case ENDERMAN:      return ENDERMAN;
			case SLIME:         return SLIME;
			case BLAZE:         return BLAZE;
			case PIG_ZOMBIE:    return PIG_ZOMBIE;
			case SQUID:         return SQUID;
			case MUSHROOM_COW:  return MUSHROOM_COW;
			case MAGMA_CUBE:    return MAGMA_CUBE;
			case IRON_GOLEM:    return IRON_GOLEM;
			default: throw new IllegalArgumentException("EntityType -> MobSkull not found '" + entityType + "");
		}
	}
	
	public static StoredHead getHeadFromURL(String skinURL)
	{
		return new StoredHead(Base64.getEncoder().encodeToString(("{textures:{SKIN:{url:\"" + skinURL + "\"}}}").getBytes()));
	}
	
}
