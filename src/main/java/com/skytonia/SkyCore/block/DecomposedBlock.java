package com.skytonia.SkyCore.block;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

public class DecomposedBlock
{

	@Getter
	private final Material material;

	@Getter
	private final byte damage;

	@Getter
	private final BlockState blockState;

	@Getter
	private final Location blockLocation;

	public DecomposedBlock(Material material, BlockState blockState, Location blockLocation)
	{
		this.material = material;
		this.damage = 0;
		this.blockState = blockState;
		this.blockLocation = blockLocation;
	}

	public DecomposedBlock(Material material, byte damage, BlockState blockState, Location blockLocation)
	{
		this.material = material;
		this.damage = damage;
		this.blockState = blockState;
		this.blockLocation = blockLocation;
	}

}
