package com.skytonia.SkyCore.items.nms;

import org.bukkit.Material;

public abstract class NMSItemUtil_1_13_X implements NMSItemUtil
{

	private Material defaultMaterial = null;

	@Override
	public Material getDefaultMaterial()
	{
		//Cannot hardcode new material names as versioned bukkit artefacts
		//overlap. The 1.8 version is used for all versions.
		if(defaultMaterial == null)
		{
			for(Material material : Material.values())
			{
				if(material.name().equals("POTATO"))
				{
					defaultMaterial = material;
				}
			}

			if(defaultMaterial == null)
			{
				throw new IllegalArgumentException("Cannot find potato material as 'POTATO'! Have the material names changed since 1.13?");
			}
		}

		return defaultMaterial;
	}

}
