package com.skytonia.SkyCore.util;

import lombok.RequiredArgsConstructor;

/**
 * Created by Chris Brown (OhBlihv) on 4/18/2017.
 */
@RequiredArgsConstructor
public class Pair<A, B>
{
	
	public final A left;
	
	public final B right;
	
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
