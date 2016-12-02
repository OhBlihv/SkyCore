package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import org.junit.Test;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class PluginLoggingTest
{
	
	@Test
	public void pluginLoggingPrefixTest()
	{
		BUtil.logMessage("Test Message #1");
		BUtil.logMessage("Test Message #2");
	}
	
	@Test
	public void pluginRetrievalTest()
	{
		System.out.println(BUtil.getCallingPlugin());
	}
	
	@Test
	public void skyblockIslandFinderTest()
	{
		//getIslandAt(-251, -148); //Low Z End
		//getIslandAt(-251, -236); //Middle Block
		//getIslandAt(-249,-259);
		//getIslandAt(-249,-347); //High Z End
		getIslandAt(2304, -2816); //Real world test
	}
	
	public void getIslandAt(int x, int z)
	{
		int islandCentreX = ((Math.round(x >> 4) * 16) >> 4),
			islandCentreZ = ((Math.round(z >> 4) * 16) >> 4);
		
		System.out.println("Centre Chunk: " + islandCentreX + "," + islandCentreZ);
		
		//Attempt to snap to the nearest centre point of an island
		//(Values are hardcoded since this is being developed in-house)
		int nearestGridLocationX = round(islandCentreX, 16),
			nearestGridLocationZ = round(islandCentreZ, 16);
		
		System.out.println("Nearest Grid: " + nearestGridLocationX + "," + nearestGridLocationZ);
		
		//Skip 8 chunks to the edge of the island (according to GridManager) and
		//add 8 back on to get the centre of the island from the edge of the chunk
		//(Islands are stored at the minX/minZ)
		islandCentreX = ((nearestGridLocationX - 8) * 16) + 8;
		islandCentreZ = ((nearestGridLocationZ - 8) * 16) + 8;
		
		System.out.println("Should be an island at " + islandCentreX + "," + islandCentreZ);
		
		/*TreeMap<Integer, Island> xCoord = islandGrid.get(islandCentreX);
		if(xCoord != null)
		{
			return xCoord.get(islandCentreZ);
		}
		
		return null;*/
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
	
}
