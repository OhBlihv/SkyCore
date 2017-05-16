package com.skytonia.SkyCore.util;

/**
 * Created by Chris Brown (OhBlihv) on 4/24/2017.
 */
public enum FacingDirection
{
	
	NORTH(0),
	EAST(90),
	SOUTH(180),
	WEST(270);
	
	final double directionDegrees;
	
	FacingDirection(double directionDegrees)
	{
		this.directionDegrees = directionDegrees;
	}
	
	public double getRotationFor(FacingDirection facingDirection)
	{
		double rotationDifference = this.directionDegrees - facingDirection.directionDegrees;
		if(rotationDifference < 0)
		{
			rotationDifference += 360D;
		}
		return rotationDifference;
	}
	
	public CardinalRotation getFacingRotationFor(FacingDirection facingDirection)
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
	
	public Pair<Double, Double> translateCoordinates(FacingDirection to, double x, double z)
	{
		double newX = x;
		double newZ = z;
		switch(getFacingRotationFor(to))
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
