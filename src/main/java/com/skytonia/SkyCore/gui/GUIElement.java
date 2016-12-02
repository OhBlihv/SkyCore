package com.skytonia.SkyCore.gui;

import com.skytonia.SkyCore.gui.actions.ElementAction;
import com.skytonia.SkyCore.gui.variables.GUIVariable;
import com.skytonia.SkyCore.items.EnchantStatus;
import com.skytonia.SkyCore.items.ItemContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@AllArgsConstructor
public class GUIElement
{
	
	public static final GUIElement DEFAULT_GUI_ELEMENT =
		new GUIElement(new ItemContainer(Material.POTATO_ITEM, 0, 1, null, null, EnchantStatus.NO_CHANGE,
		                                 null, null, null, null),
		               new ArrayDeque<>());
	
	@Getter
	protected final ItemContainer itemContainer;
	
	@Getter
	//Use a queue here as actions are meant to be executed in the order they
	//were added in, and if one fails, the next one should not execute
	protected final Deque<ElementAction> elementActions;
	
	public ItemStack toItemStack(Deque<GUIVariable> guiVariables, Player player)
	{
		ItemStack itemStack = itemContainer.toItemStack(player == null ? "" : player.getName());
		if(itemStack != null && itemStack.getType() != Material.AIR && guiVariables != null && !guiVariables.isEmpty())
		{
			ItemMeta itemMeta = itemStack.getItemMeta();
			
			if(itemMeta.hasDisplayName() && !itemMeta.getDisplayName().isEmpty())
			{
				String displayName = itemMeta.getDisplayName();
				for(GUIVariable guiVariable : guiVariables)
				{
					if(!guiVariable.containsVariable(displayName))
					{
						continue;
					}
					
					displayName = guiVariable.doReplacement(displayName, player);
				}
				itemMeta.setDisplayName(displayName);
			}
			
			if(itemMeta.hasLore() && !itemMeta.getLore().isEmpty())
			{
				List<String> lore = itemMeta.getLore();
				for(GUIVariable guiVariable : guiVariables)
				{
					if(!guiVariable.containsVariable(lore))
					{
						continue;
					}
					
					lore = guiVariable.doReplacement(lore, player);
				}
				itemMeta.setLore(lore);
			}
			itemStack.setItemMeta(itemMeta);
		}
		return itemStack;
	}
	
}
