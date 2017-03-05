package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

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
	
	private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
	
	@Test
	public void islandLevelTest()
	{
		//getIslandLevel(100000000);
		
		/*System.out.println("Cost for Level 1: "      + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(1))));
		System.out.println("Cost for Level 5: "      + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(5))));
		System.out.println("Cost for Level 10: "     + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(10))));
		System.out.println("Cost for Level 25: "     + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(25))));
		System.out.println("Cost for Level 50: "     + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(50))));
		System.out.println("Cost for Level 100: "    + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(100))));
		System.out.println("Cost for Level 200: "    + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(200))));
		System.out.println("Cost for Level 300: "    + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(300))));
		System.out.println("Cost for Level 400: "    + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(400))));
		System.out.println("Cost for Level 500: "    + numberFormat.format(Integer.valueOf(levelTotalCostMap.get(500))));*/
		
		System.out.println("Total Cost for Level 1: " + numberFormat.format(getRequiredBalanceForLevel(1)));
		System.out.println("Total Cost for Level 2: " + numberFormat.format(getRequiredBalanceForLevel(2)));
		System.out.println("Total Cost for Level 3: " + numberFormat.format(getRequiredBalanceForLevel(3)));
		System.out.println("Total Cost for Level 4: " + numberFormat.format(getRequiredBalanceForLevel(4)));
		System.out.println("Total Cost for Level 5: " + numberFormat.format(getRequiredBalanceForLevel(5)));
		System.out.println("Total Cost for Level 6: " + numberFormat.format(getRequiredBalanceForLevel(6)));
		System.out.println("Total Cost for Level 7: " + numberFormat.format(getRequiredBalanceForLevel(7)));
		System.out.println("Total Cost for Level 8: " + numberFormat.format(getRequiredBalanceForLevel(8)));
		System.out.println("Total Cost for Level 9: " + numberFormat.format(getRequiredBalanceForLevel(9)));
		System.out.println("Total Cost for Level 10: " + numberFormat.format(getRequiredBalanceForLevel(10)));
		System.out.println("Total Cost for Level 25: " + numberFormat.format(getRequiredBalanceForLevel(25)));
		System.out.println("Total Cost for Level 50: " + numberFormat.format(getRequiredBalanceForLevel(50)));
		System.out.println("Total Cost for Level 100: " + numberFormat.format(getRequiredBalanceForLevel(100)));
		System.out.println("Total Cost for Level 200: " + numberFormat.format(getRequiredBalanceForLevel(200)));
		System.out.println("Total Cost for Level 300: " + numberFormat.format(getRequiredBalanceForLevel(300)));
		System.out.println("Total Cost for Level 400: " + numberFormat.format(getRequiredBalanceForLevel(400)));
		System.out.println("Total Cost for Level 500: " + numberFormat.format(getRequiredBalanceForLevel(500)));
		System.out.println("Total Cost for Level 999: " + numberFormat.format(getRequiredBalanceForLevel(999)));
		System.out.println("Total Cost for Level 1000: " + numberFormat.format(getRequiredBalanceForLevel(1000)));
	}
	
	private final Map<Integer, Integer> levelTotalCostMap = new Int2ObjectOpenHashMap<>();
	
	public long getRequiredBalanceForLevel(final int requiredLevel)
	{
		if(1 == 1)
		{
			return 1;
		}
		
		int islandLevel = 0;
		final int base = 50;
		long totalLevelCost = 0;
		int currentLevelCost = 0;
		double prevIslandCost = base;
		while((islandLevel++ < requiredLevel))
		{
			currentLevelCost += prevIslandCost;
			
			prevIslandCost = (base * Math.pow(islandLevel, 1.000001));
			
			totalLevelCost += currentLevelCost;
		}
		
		return totalLevelCost;
	}
	
	private int round(int i, int n)
	{
		boolean negative = i < 0;
		if(negative)
		{
			i = 0 - i;
		}
		
		int result = (((i % n) > n/2) ? i + n - i % n : i - i % n);
		
		return negative ? 0 - result : result;
	}
	
	//@Test
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
	
	@Test
	public void angleTest()
	{
		System.out.println((byte) ((65 * (1D / 256D)) * 100));
	}
	
	@Test
	public void murderNameDisplayTest()
	{
		System.out.println("Displayname of Murder1 Id:" + 1 + " = " + getDisplayGameId("Murder1", "1"));
		System.out.println("Displayname of Murder1 Id:" + 5 + " = " + getDisplayGameId("Murder1", "5"));
		System.out.println("Displayname of Murder2 Id:" + 1 + " = " + getDisplayGameId("Murder2", "1"));
		System.out.println("Displayname of Murder2 Id:" + 5 + " = " + getDisplayGameId("Murder2", "5"));
	}
	
	private static final int maxGamesPerInstance = 5;
	
	public int getDisplayGameId(String serverInstance, String gameId)
	{
		int instanceNumber = Integer.parseInt(serverInstance.substring(serverInstance.length() - 1, serverInstance.length())) - 1;
		
		//Assume 5 games per server
		//Get starting number
		int displayId = (instanceNumber * maxGamesPerInstance) + Integer.parseInt(gameId) + 1;
		/*if(instanceNumber > 0)
		{
			displayId -= 1; //Lazy fix for skipping #7 and #13
		}*/
		
		//BUtil.logInfo("I: " + serverInstance + " G: " + gameId + " = " + displayId);
		
		return displayId;
	}
	
	@Test
	public void userGUISlotTest()
	{
		testUsersGUIWithUserCount(19, 1);
		testUsersGUIWithUserCount(21, 1);
		testUsersGUIWithUserCount(30, 1);
		testUsersGUIWithUserCount(50, 2);
	}
	
	public void testUsersGUIWithUserCount(int users, int page)
	{
		System.out.println("=============================");
		System.out.println("Users: " + users + " Page: " + page);
		//Max of 21 players per page
		int slot = 10,
			userNum = 0;
		
		while(userNum < users)
		{
			//Lower end of range, keep looping til we find the start of this page.
			if(++userNum < (page - 1) * 21)
			{
				continue;
			}
			//Past our rage. Break.
			else if(userNum >= page * 21)
			{
				System.out.println("Breaking. Hit limit for page " + page + " at user " + userNum + " and slot " + slot);
				break;
			}
			
			System.out.println("Setting user " + userNum + " at slot " + slot);
			
			slot = getSafeSlot(++slot);
		}
		
		System.out.println("=============================");
	}
	
	public int getSafeSlot(int slot)
	{
		switch(slot)
		{
			case 8: slot = 10; break;
			case 17: slot = 19; break;
			case 26: slot = 28; break;
			case 35: slot = 37; break;
		}
		
		return slot;
	}
	
}
