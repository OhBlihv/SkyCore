package com.skytonia.SkyCore.util;

import com.skytonia.SkyCosmetics.util.Pair;

/**
 * Created by Chris Brown (OhBlihv) on 4/24/2017.
 */
public enum FacingDirection
{
	
	NORTH,
	EAST,
	SOUTH,
	WEST;
	
	public CardinalRotation getRotationFor(FacingDirection facingDirection)
	{
		switch(this)
		{
			case NORTH:
			{
				switch(facingDirection)
				{
					case NORTH: return CardinalRotation.NONE;
					case EAST: return CardinalRotation.RIGHT;
					case SOUTH: return CardinalRotation.OPPOSITE;
					case WEST: return CardinalRotation.LEFT;
				}
				break;
			}
			case EAST:
			{
				switch(facingDirection)
				{
					case NORTH: return CardinalRotation.LEFT;
					case EAST: return CardinalRotation.NONE;
					case SOUTH: return CardinalRotation.RIGHT;
					case WEST: return CardinalRotation.OPPOSITE;
				}
				break;
			}
			case SOUTH:
			{
				switch(facingDirection)
				{
					case NORTH: return CardinalRotation.OPPOSITE;
					case EAST: return CardinalRotation.LEFT;
					case SOUTH: return CardinalRotation.NONE;
					case WEST: return CardinalRotation.RIGHT;
				}
				break;
			}
			case WEST:
			{
				switch(facingDirection)
				{
					case NORTH: return CardinalRotation.RIGHT;
					case EAST: return CardinalRotation.OPPOSITE;
					case SOUTH: return CardinalRotation.LEFT;
					case WEST: return CardinalRotation.NONE;
				}
				break;
			}
		}
		
		//Not accessible
		return CardinalRotation.NONE;
	}
	
	public Pair<Integer, Integer> translateCoordinates(FacingDirection to, int x, int z)
	{
		int newX = x;
		int newZ = z;
		switch(getRotationFor(to))
		{
			case NONE:
			{
				break; //Already correct
			}
			case LEFT:
			{
				newX = z;
				newZ = -x;
				break;
			}
			case RIGHT:
			{
				newX = -z;
				newZ = x;
				break;
			}
			case OPPOSITE:
			{
				newX = -x;
				newZ = -z;
				break;
			}
		}
		
		return new Pair<>(newX, newZ);
	}
	
}
