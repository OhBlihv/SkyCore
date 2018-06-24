package com.skytonia.SkyCore.util;

/**
 * Created by Chris Brown (OhBlihv) on 4/18/2017.
 */
@Deprecated
public class Pair<A, B>
{
	
	public final A left;
	
	public final B right;

	public Pair(A left, B right)
	{
		this.left = left;
		this.right = right;

		BUtil.log("SkyCore PAIR is DEPRECATED and will be removed soon.");
	}
	
	@Override
	public int hashCode()
	{
		return left.hashCode() + right.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Pair && left.equals(((Pair) obj).left) && right.equals(((Pair) obj).right);
	}
}
