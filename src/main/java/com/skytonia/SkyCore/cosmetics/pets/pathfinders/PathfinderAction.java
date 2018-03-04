package com.skytonia.SkyCore.cosmetics.pets.pathfinders;

public enum PathfinderAction
{

	NONE(false), //Used to select another action
	FOLLOW_PLAYER(false),
	LOOK_AT_PLAYER(true),
	RANDOM_LOOKAROUND(true),
	RANDOM_WALKAROUND(true);

	final boolean isValid;

	PathfinderAction(boolean isValid)
	{
		this.isValid = isValid;
	}

}