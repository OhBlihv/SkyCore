package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.items.uniqueitems.UniqueItems;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.TimeUtil;
import org.junit.Test;

import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

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
		System.out.println(BUtil.toRomanNumerals(3));
		System.out.println(BUtil.toRomanNumerals(4));
		System.out.println(BUtil.toRomanNumerals(14));
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
		testUsersGUIWithUserCount(75, 2);
	}
	
	public void testUsersGUIWithUserCount(int users, int page)
	{
		System.out.println("=============================");
		System.out.println("Users: " + users + " Page: " + page);
		//Max of 21 players per page
		int slot = 10,
			userNum = 0;
		
		boolean hasPrinted = false;
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
			
			if(!hasPrinted)
			{
				System.out.println("Starting at user " + userNum + " and slot " + slot);
				hasPrinted = true;
			}
			
			//System.out.println("Setting user " + userNum + " at slot " + slot);
			
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
	
	class InstanceofTestClass
	{
		
		boolean isAsync() { return false; }
		
	}
	
	final class InstanceofTestClassExtension extends InstanceofTestClass
	{
		
		boolean isAsync() { return true; }
		
	}
	
	@Test
	public void instanceofTest()
	{
		final int MAX_IN_COLLECTION = 10000000;
		
		ArrayDeque<InstanceofTestClass> testCollection = new ArrayDeque<>();
		for(int i = 0;i < MAX_IN_COLLECTION;i++)
		{
			testCollection.add(new InstanceofTestClassExtension());
		}
		
		long startTime, endTime;
		int value = 0;
		
		startTime = System.nanoTime();
		
		for(InstanceofTestClass testClass : testCollection)
		{
			//22 Required
			if(
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension &&
				testClass instanceof InstanceofTestClassExtension
				)
			{
				value++;
			}
		}
		
		endTime = System.nanoTime();
		
		System.out.println("Total time took for 'instanceof': " + NumberFormat.getIntegerInstance().format(endTime - startTime));
		
		value = 0;
		
		startTime = System.nanoTime();
		
		for(InstanceofTestClass testClass : testCollection)
		{
			if(testClass.isAsync())
			{
				value++;
			}
		}
		
		endTime = System.nanoTime();
		
		System.out.println("Total time took for 'isAsync': " + NumberFormat.getIntegerInstance().format(endTime - startTime));
	}
	
	@Test
	public void timeUtilTest()
	{
		//BUtil.log(TimeUtil.getNeatComparison((1497100847 * 1000L), System.currentTimeMillis(),
		//                           ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.WEEKS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS));
		BUtil.log(TimeUtil.getNeatComparison((1494547200 * 1000L), System.currentTimeMillis(),
		                           ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.WEEKS, ChronoUnit.DAYS));
	}
	
	@Test
	public void fastUtilCollectionTest()
	{
	/*	doCollectionTime(new ObjectLinkedOpenHashSet<>(), "ObjectLinkedOpenHashSet");
		doCollectionTime(new ObjectArrayList<>(), "ObjectArrayList");
		//doCollectionTime(new ObjectArraySet<>(), "ObjectArraySet"); //28 seconds
		doCollectionTime(new ObjectRBTreeSet<>(), "ObjectRBTreeSet");
		doCollectionTime(new ObjectBigArrayBigList<>(), "ObjectBigArrayBigList");
		doCollectionTime(new ObjectOpenHashSet<>(), "ObjectOpenHashSet");*/
	}
	
	private void doCollectionTime(Collection<String> collection, String name)
	{
		long startTime = System.currentTimeMillis();
		
		final int numMax = 100000;
		final Random random = new Random();
		
		for(int i = 0;i < numMax;i++)
		{
			collection.add("" + i);
		}
		
		for(int i = 0;i < 1000;i++)
		{
			collection.contains("" + random.nextInt(numMax));
		}
		
		for(int i = 0;i < 1000;i++)
		{
			collection.remove("" + random.nextInt(numMax));
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total time took for '" + name + "': " + NumberFormat.getIntegerInstance().format(endTime - startTime) + "ms");
	}
	
	@Test
	public void uniqueItemTest()
	{
		UniqueItems uniqueItems = new UniqueItems(Arrays.asList("0008", "0034", "0001"));
		
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
		BUtil.log(uniqueItems.getNextId());
	}
	
}
