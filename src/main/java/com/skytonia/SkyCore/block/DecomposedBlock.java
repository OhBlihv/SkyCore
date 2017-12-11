package com.skytonia.SkyCore.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

@RequiredArgsConstructor
public class DecomposedBlock
{

	@Getter
	private final Material material;

	@Getter
	private final BlockState blockState;

	@Getter
	private final Location blockLocation;

}
