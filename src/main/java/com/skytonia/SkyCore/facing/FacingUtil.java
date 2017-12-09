package com.skytonia.SkyCore.facing;

import org.bukkit.Location;

public class FacingUtil
{

	public static FacingDirection getDirectionFor(Location location)
	{
		return getDirectionFor((int) location.getYaw(), (int) location.getPitch());
	}

	public static FacingDirection getDirectionFor(int yaw, int pitch)
	{
		//Avoid yaws such as -260
		while(yaw < 0)
		{
			yaw += 360;
		}

		while(yaw > 360)
		{
			yaw -= 360;
		}

		if(pitch < -90 || pitch > 90)
		{
			throw new IllegalArgumentException("Pitch Out Of Range (" + pitch + ") - (-90 -> 90)");
		}

		if(pitch > 45)
		{
			return FacingDirection.DOWN;
		}
		else if(pitch < -45)
		{
			return FacingDirection.UP;
		}

		if(yaw >= 315 || yaw <= 45)
		{
			return FacingDirection.SOUTH;
		}
		else if(yaw >= 225 /*&& yaw <= 315*/)
		{
			return FacingDirection.EAST;
		}
		else if(yaw >= 135 /*&& yaw <= 225*/)
		{
			return FacingDirection.NORTH;
		}
		else //if(yaw >= 45 /*&& yaw <= 135*/)
		{
			return FacingDirection.WEST;
		}

		//throw new IllegalArgumentException("Yaw out of range! " + yaw);
	}

}
