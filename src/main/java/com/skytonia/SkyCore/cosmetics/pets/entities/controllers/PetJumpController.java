package com.skytonia.SkyCore.cosmetics.pets.entities.controllers;

import net.minecraft.server.v1_9_R2.ControllerJump;
import net.minecraft.server.v1_9_R2.EntityCreature;

/**
 * Created by Chris Brown (OhBlihv) on 4/21/2017.
 */
public class PetJumpController extends ControllerJump
{
	
	private EntityCreature c;
	private boolean d = false;
	
	public PetJumpController(EntityCreature entityCreature)
	{
		super(entityCreature);
		
		this.c = entityCreature;
	}
	
	public boolean c()
	{
		return this.a;
	}
	
	public boolean d()
	{
		return this.d;
	}
	
	public void a(boolean flag)
	{
		this.d = flag;
	}
	
	public void b()
	{
		if(this.a)
		{
			this.a = false;
		}
	}
	
	public boolean getA()
	{
		return a;
	}
	
}
