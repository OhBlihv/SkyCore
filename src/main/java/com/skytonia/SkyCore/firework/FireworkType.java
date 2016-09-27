package com.skytonia.SkyCore.firework;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public enum FireworkType
{
	
	CRATE_SPAWN(FireworkEffect.Type.BALL, Color.ORANGE, Color.WHITE, Color.YELLOW),
	OPEN_CRATE(FireworkEffect.Type.BALL, Color.GRAY, Color.WHITE, Color.BLACK),
	PVP_DEATH(FireworkEffect.Type.BALL, Color.RED, Color.ORANGE, Color.RED);
	
	@Getter
	private FireworkEffect.Type type;
	
	@Getter
	private Color colour1, colour2, colour3;
	
	FireworkType(FireworkEffect.Type type, Color colour1, Color colour2, Color colour3)
	{
		this.type = type;
		this.colour1 = colour1;
		this.colour2 = colour2;
		this.colour3 = colour3;
	}
	
}
