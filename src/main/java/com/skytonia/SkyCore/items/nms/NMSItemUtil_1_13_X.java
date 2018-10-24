package com.skytonia.SkyCore.items.nms;

import org.bukkit.Material;

public abstract class NMSItemUtil_1_13_X implements NMSItemUtil
{

	private Material getMaterialFromString(String materialName)
	{
		Material requestedMaterial = null;
		for(Material material : Material.values())
		{
			if(material.name().equals(materialName))
			{
				requestedMaterial = material;
			}
		}

		if(requestedMaterial == null)
		{
			throw new IllegalArgumentException("Cannot find potato material as '" + materialName + "'! Have the material names changed since 1.13?");
		}

		return requestedMaterial;
	}

	private Material defaultMaterial = null;

	@Override
	public Material getDefaultMaterial()
	{
		//Cannot hardcode new material names as versioned bukkit artefacts
		//overlap. The 1.8 version is used for all versions.
		if(defaultMaterial == null)
		{
			defaultMaterial = getMaterialFromString("POTATO");
		}

		return defaultMaterial;
	}

	private Material skullMaterial = null;

	@Override
	public Material getSkullMaterial()
	{
		if(skullMaterial == null)
		{
			skullMaterial = getMaterialFromString("SKELETON_SKULL");
		}

		return skullMaterial;
	}

}
