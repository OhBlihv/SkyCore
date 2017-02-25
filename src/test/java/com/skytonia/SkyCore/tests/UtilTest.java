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
	
}
