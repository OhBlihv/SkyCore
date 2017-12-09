package com.skytonia.SkyCore.facing;

import com.skytonia.SkyCore.util.IntegerTrio;
import lombok.Getter;
import org.bukkit.Location;

public enum FacingDirection
{

	UP(0),
	DOWN(0),
	NORTH(180),
	EAST(270),
	SOUTH(0),
	WEST(90);


	/**
	 * Indicates the rotation from SOUTH to get to the current position
	 */
	@Getter
	private final int rotation;

	FacingDirection(int rotation)
	{
		this.rotation = rotation;
	}

	public FacingDirection getInverse()
	{
		switch(this)
		{
			case UP:    return DOWN;
			case DOWN:  return UP;
			case NORTH: return SOUTH;
			case EAST:  return WEST;
			case SOUTH: return NORTH;
			case WEST:  return EAST;
		}

		throw new IllegalStateException("Unable to find inverse of " + this);
	}

	public Location transformLocation(Location location, int distance)
	{
		Location transformedLocation = location.clone();

		IntegerTrio transformedCoordinates = transformCoordinates(new IntegerTrio(
				(int) location.getX(), (int) location.getY(), (int) location.getZ()
		), distance);

		transformedLocation.setX(transformedCoordinates.x);
		transformedLocation.setY(transformedCoordinates.y);
		transformedLocation.setZ(transformedCoordinates.z);

		return transformedLocation;
	}

	public IntegerTrio transformCoordinates(IntegerTrio integerTrio, int distance)
	{
		integerTrio = new IntegerTrio(integerTrio); //Clone
		switch(this)
		{
			case UP:    integerTrio.y += distance; break;
			case DOWN:  integerTrio.y -= distance; break;
			case NORTH: integerTrio.z -= distance; break;
			case EAST:  integerTrio.x += distance; break;
			case SOUTH: integerTrio.z += distance; break;
			case WEST:  integerTrio.x -= distance; break;
		}

		return integerTrio;
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

	public CoordinatePlane getFacePlane()
	{
		switch(this)
		{
			case EAST:
			case WEST:
			{
				return CoordinatePlane.X;
			}
			case UP:
			case DOWN:
			{
				return CoordinatePlane.Y;
			}
			case NORTH:
			case SOUTH:
			{
				return CoordinatePlane.Z;
			}
		}

		throw new IllegalArgumentException("Unknown CoordinatePlane for " + this);
	}

	public IntegerTrio translateCoordinates(FacingDirection to, IntegerTrio inCoords)
	{
		if(to == this)
		{
			return inCoords;
		}

		int newX = inCoords.x;
		int newZ = inCoords.z;
		switch(getFacingRotationFor(to))
		{
			case NONE:
			{
				break; //Already correct
			}
			case LEFT:
			{
				newX = inCoords.z;
				newZ = -inCoords.x;
				break;
			}
			case RIGHT:
			{
				newX = -inCoords.z;
				newZ = inCoords.x;
				break;
			}
			case OPPOSITE:
			{
				newX = -inCoords.x;
				newZ = -inCoords.z;
				break;
			}
		}

		return new IntegerTrio(newX, inCoords.y, newZ);
	}

}
