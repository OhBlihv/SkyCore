package com.skytonia.SkyCore.cosmetics.pets.pathfinders;

import lombok.Getter;

public class ActivePathfinderAction
{

	@Getter
	private final PathfinderAction pathfinderAction;

	@Getter
	private int ticksLeft;

	public ActivePathfinderAction()
	{
		this.pathfinderAction = PathfinderAction.NONE;
		this.ticksLeft = -1;
	}

	public ActivePathfinderAction(PathfinderAction pathfinderAction, int actionLength)
	{
		this.pathfinderAction = pathfinderAction;
		this.ticksLeft = actionLength;
	}

	public boolean tick()
	{
		return pathfinderAction.isValid && --ticksLeft < 0;
	}

}
