package com.skytonia.SkyCore.gui.config;

/**
 * Created by Chris Brown (OhBlihv) on 1/20/2017.
 */
public enum InventorySize
{
	
	//Absolute Sized Inventories
	HOPPER(5),
	DISPENSER(9),
	
	//Regular Chest/Double Chests
	ONE_LINE(0, 9),
	TWO_LINE(10, 18),
	THREE_LINE(19, 27),
	FOUR_LINE(28, 36),
	FIVE_LINE(37, 45),
	SIX_LINE(46, 54);
	
	private final int absoluteSize;
	
	private final int minSize;
	private final int maxSize;
	
	InventorySize(int absoluteSize)
	{
		this.absoluteSize = absoluteSize;
		
		this.minSize = 0;
		this.maxSize = 5;
	}
	
	InventorySize(int minSize, int maxSize)
	{
		this.minSize = minSize;
		this.maxSize = maxSize;
		
		this.absoluteSize = -1;
	}
	
	public int getSize()
	{
		if(absoluteSize != -1)
		{
			return absoluteSize;
		}
		
		return maxSize;
	}
	
	public boolean isWithinRange(int slot)
	{
		return slot >= 0 && slot < maxSize;
	}
	
	public boolean matchesSize(int guiSize)
	{
		return guiSize == maxSize;
	}
	
	public static InventorySize ofSize(int slots)
	{
		return ofSize(slots, true);
	}
	
	public static InventorySize ofSize(int slots, boolean ignoreSpecial)
	{
		for(InventorySize inventorySize : values())
		{
			if(ignoreSpecial && (inventorySize == DISPENSER))
			{
				continue;
			}
			
			if(inventorySize.absoluteSize != -1 && inventorySize.absoluteSize == slots)
			{
				return inventorySize;
			}
			
			if(inventorySize.maxSize >= slots && inventorySize.minSize <= slots)
			{
				return inventorySize;
			}
		}
		
		//Default to the largest inventory for compatibility
		return SIX_LINE;
	}
	
}
