package com.skytonia.SkyCore.view.template;

import com.skytonia.SkyCore.view.View;
import com.skytonia.SkyCore.view.slots.ViewAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface ViewTemplate
{

	/**
	 * Fill inventory in with pre-set format
	 *
	 * @param inventory
	 */
	void populateInventory(View view, Inventory inventory, Object... args);

	int getSlot(int slot);

	int getInverseSlot(int slot);

	void setItem(View view, Inventory inventory, int slot, ItemStack itemStack, Object... args);

	ItemStack getItem(View view, Inventory inventory, int slot);

	ViewAction getSlotAction(View view, int slot);

}
