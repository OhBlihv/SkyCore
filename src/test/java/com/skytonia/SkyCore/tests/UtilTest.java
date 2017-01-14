package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 11/28/2016.
 */
public class UtilTest
{
	
	@Test
	public void romanNumeralTest()
	{
		System.out.println(BUtil.toRomanNumerals(2016));
		System.out.println(BUtil.toRomanNumerals(1400));
		System.out.println(BUtil.toRomanNumerals(14));
		System.out.println(BUtil.toRomanNumerals(206));
	}
	
	/*
	 * Island Level Estimates: (BALANCE:LEVEL) for base 100 and multiplier 2.0
	 * 0:0
	 * 100:1
	 * 200:2
	 * 399:2
	 * 400:3
	 * 401:3
	 * 550:3
	 * 800:4
	 */
	
	@Test
	public void islandLevelTest()
	{
		System.out.println("Level for 0   (0): " + getIslandLevel(0  ));
		System.out.println("Level for 100 (1): " + getIslandLevel(100));
		System.out.println("Level for 200 (2): " + getIslandLevel(200));
		System.out.println("Level for 399 (2): " + getIslandLevel(399));
		System.out.println("Level for 400 (3): " + getIslandLevel(400));
		System.out.println("Level for 401 (3): " + getIslandLevel(401));
		System.out.println("Level for 550 (3): " + getIslandLevel(550));
		System.out.println("Level for 800 (4): " + getIslandLevel(800));
		System.out.println("Level for 100k (4): " + getIslandLevel(100000));
		System.out.println("Level for 1.5M (4): " + getIslandLevel(1500000));
	}
	
	public int getIslandLevel(int balance)
	{
		final double multiplier = 1.2;
		
		int islandLevel = 0;
		int nextIslandBalance = (int) (100 / multiplier); //Start 1/2 of our base
		while(balance >= (nextIslandBalance *= multiplier))
		{
			islandLevel++;
		}
		
		return islandLevel;
	}
	
	@Test
	public void collectionTest()
	{
		//testCollection(new ArrayDeque<>(), "ArrayDeque");
		//testCollection(new ArrayList<>(),  "ArrayList");
		//testCollection(new HashSet<>(), "HashSet");
	}
	
	public void testCollection(Collection<String> collection, String collectionName)
	{
		long startTime = System.currentTimeMillis();
		
		for(int i = 0;i < 100000;i++)
		{
			collection.add(i + "");
			
			collection.contains(i + "");
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Collection size: " + collection.size());
		
		System.out.println("Total time took for " + collectionName + ": " + (endTime - startTime));
	}
	
}
