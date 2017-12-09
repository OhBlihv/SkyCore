package com.skytonia.SkyCore.util;

public class IntegerTrio
{

	public int x;
	public int y;
	public int z;

	public IntegerTrio(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public IntegerTrio(IntegerTrio node)
	{
		this.x = node.x;
		this.y = node.y;
		this.z = node.z;
	}

	public IntegerTrio()
	{
	}

	public final void set(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final void set(IntegerTrio node)
	{
		this.x = node.x;
		this.y = node.y;
		this.z = node.z;
	}

	public final int hashCode()
	{
		return this.x ^ this.z << 12 ^ this.y << 24;
	}

	public final int getX()
	{
		return this.x;
	}

	public final int getY()
	{
		return this.y;
	}

	public final int getZ()
	{
		return this.z;
	}

	public String toString()
	{
		return this.x + "," + this.y + "," + this.z;
	}

	public boolean equals(Object obj)
	{
		IntegerTrio other = (IntegerTrio) obj;
		return other.x == this.x && other.z == this.z && other.y == this.y;
	}
}