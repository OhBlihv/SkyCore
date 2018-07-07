package com.skytonia.SkyCore.view.template;

import com.skytonia.SkyCore.view.View;
import com.skytonia.SkyCore.view.slots.ViewAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *  ViewTemplate with zero slot conversion
 */
public class BlankTemplate implements ViewTemplate
{

	@Override
	public void populateInventory(View view, Inventory inventory, Object... args)
	{
		//No population. Leave blank.
	}

	@Override
	public int getSlot(int slot)
	{
		return slot;
	}

	@Override
	public int getInverseSlot(int slot)
	{
		return slot;
	}

	@Override
	public void setItem(View view, Inventory inventory, int slot, ItemStack itemStack, Object... args)
	{
		inventory.setItem(slot, itemStack);
	}

	@Override
	public ItemStack getItem(View view, Inventory inventory, int slot)
	{
		return inventory.getItem(slot);
	}

	@Override
	public ViewAction getSlotAction(View view, int slot)
	{
		return view.getViewSlot(slot).getAction();
	}

}
