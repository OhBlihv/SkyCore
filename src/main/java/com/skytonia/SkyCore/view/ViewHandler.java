package com.skytonia.SkyCore.view;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ViewHandler implements Listener
{

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		View.setOpenView((Player) event.getPlayer(), null);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		PlayerView playerView = View.getOpenView((Player) event.getWhoClicked());
		if(playerView != null)
		{
			playerView.getParentView().onClick(event);
		}
	}

}
