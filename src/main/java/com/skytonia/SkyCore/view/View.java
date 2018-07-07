package com.skytonia.SkyCore.view;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.gui.config.InventorySize;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.view.delegate.OpenRequirementDelegate;
import com.skytonia.SkyCore.view.delegate.TitleDelegate;
import com.skytonia.SkyCore.view.slots.ViewSlot;
import com.skytonia.SkyCore.view.template.ViewTemplate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.HashMap;
import java.util.Map;

public class View
{

	private static final Map<String, PlayerView> playerViewMap = new HashMap<>();

	public static PlayerView getOpenView(Player player)
	{
		return playerViewMap.get(player.getName());
	}

	public static void setOpenView(Player player, PlayerView playerView)
	{
		PlayerView previousView = playerViewMap.put(player.getName(), playerView);
		if(previousView != null)
		{
			previousView.getParentView().closeInventory(player);
		}
	}

	private static boolean listenersRegistered = false;
	private static void initialize()
	{
		if(!listenersRegistered)
		{
			Bukkit.getPluginManager().registerEvents(new ViewHandler(), SkyCore.getPluginInstance());
			listenersRegistered = true;
		}
	}

	//

	private final InventorySize guiSize;

	private final ViewSlot[] viewSlots;

	/*
	 * Delegates
	 */

	@Getter
	private final ViewTemplate viewTemplate;

	private final OpenRequirementDelegate openRequirement;

	private final TitleDelegate titleDelegate;

	/*
	 *
	 */

	public View(InventorySize guiRows, ViewSlot[] viewSlots, ViewTemplate viewTemplate,
	            OpenRequirementDelegate openRequirement, TitleDelegate titleDelegate)
	{
		//Ensure listeners are registered to allow Views to function
		initialize();

		this.guiSize = guiRows;
		this.viewSlots = viewSlots;
		this.viewTemplate = viewTemplate;
		this.openRequirement = openRequirement;
		this.titleDelegate = titleDelegate;
	}

	public void openInventory(Player player, Object... args)
	{
		if(!openRequirement.canOpenInventory(player, args))
		{
			return;
		}

		Inventory inventory = createInventory(guiSize, titleDelegate.getTitle(player, args));

		//Fill template first
		viewTemplate.populateInventory(this, inventory);

		int slot = -1;
		for(ViewSlot viewSlot : viewSlots)
		{
			slot = viewTemplate.getSlot(++slot);

			viewTemplate.setItem(this, inventory, slot, viewSlot.getItem().getItem(player, this, args), args);
		}

		setOpenView(player, new PlayerView(this, inventory));
		player.openInventory(inventory);
	}

	@OverridingMethodsMustInvokeSuper
	public void closeInventory(Player player)
	{
		if(player.getOpenInventory().getTopInventory() != null)
		{
			BUtil.log("Closing top inventory");
			player.closeInventory();
		}
	}

	public void onClick(InventoryClickEvent event)
	{
		viewTemplate.getSlotAction(this, event.getSlot())
			.onClick((Player) event.getWhoClicked(), event.getClick(), event.getSlot(), event);
	}

	protected Inventory createInventory(InventorySize guiSize, String guiTitle)
	{
		switch(guiSize)
		{
			case HOPPER: return Bukkit.createInventory(null, InventoryType.HOPPER, guiTitle);
			case DISPENSER: return Bukkit.createInventory(null, InventoryType.DISPENSER, guiTitle);
			default:
			{
				return Bukkit.createInventory(null, guiSize.getSize(), guiTitle);
			}
		}
	}

	//

	public ViewSlot getViewSlot(int slot)
	{
		return viewSlots[slot];
	}

}
