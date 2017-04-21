package com.skytonia.SkyCore.cosmetics.pets.entities.controllers;

import com.skytonia.SkyCore.cosmetics.pets.entities.PetZombieSource;
import net.minecraft.server.v1_9_R2.ControllerMove;

/**
 * Created by Chris Brown (OhBlihv) on 4/21/2017.
 */
public class PetMoveController extends ControllerMove
{
	
	private final PetZombieSource i;
	private final PetJumpController controllerJump;
	
	private double j;
	
	public PetMoveController(PetZombieSource entityCreature, PetJumpController controllerJump)
	{
		super(entityCreature);
		
		this.i = entityCreature;
		this.controllerJump = controllerJump;
	}
	
	public void c()
	{
		/*if(this.i.onGround && !this.i.getBd() && !controllerJump.getA())
		{
			//this.i.c(0.0D);
		}
		else */if(this.a())
		{
			this.i.c(this.j);
		}
		
		super.c();
	}
	
	public void a(double d0, double d1, double d2, double d3)
	{
		if(this.i.isInWater())
		{
			d3 = 1.5D;
		}
		
		super.a(d0, d1, d2, d3);
		if(d3 > 0.0D)
		{
			this.j = d3;
		}
	}
	
}
