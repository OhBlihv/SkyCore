package com.skytonia.SkyCore.view;

import com.skytonia.SkyCore.gui.config.InventorySize;
import com.skytonia.SkyCore.view.delegate.OpenRequirementDelegate;
import com.skytonia.SkyCore.view.delegate.TitleDelegate;
import com.skytonia.SkyCore.view.items.ViewItem;
import com.skytonia.SkyCore.view.slots.ViewAction;
import com.skytonia.SkyCore.view.slots.ViewSlot;
import com.skytonia.SkyCore.view.template.BlankTemplate;
import com.skytonia.SkyCore.view.template.ViewTemplate;

public class ViewBuilder
{

	private OpenRequirementDelegate openRequirement = null;

	private TitleDelegate titleDelegate = null;

	private InventorySize guiSize = InventorySize.SIX_LINE;

	private ViewSlot[] viewSlots = new ViewSlot[guiSize.getSize()];

	private ViewTemplate viewTemplate = new BlankTemplate();

	public ViewBuilder()
	{

	}

	public ViewBuilder openReq(OpenRequirementDelegate openRequirement)
	{
		this.openRequirement = openRequirement;

		return this;
	}

	public ViewBuilder title(TitleDelegate titleDelegate)
	{
		this.titleDelegate = titleDelegate;

		return this;
	}

	public ViewBuilder title(String title)
	{
		this.titleDelegate = (player, args) -> title;

		return this;
	}

	public ViewBuilder template(ViewTemplate viewTemplate)
	{
		this.viewTemplate = viewTemplate;

		return this;
	}

	public ViewBuilder size(InventorySize size)
	{
		this.guiSize = size;

		return this;
	}

	public ViewBuilder setSlot(int slot, ViewItem viewItem, ViewAction viewAction)
	{
		viewSlots[slot] = new ViewSlot(viewItem, viewAction);

		return this;
	}

	public View build()
	{
		return new View(guiSize, viewSlots, viewTemplate, openRequirement, titleDelegate);
	}

}
