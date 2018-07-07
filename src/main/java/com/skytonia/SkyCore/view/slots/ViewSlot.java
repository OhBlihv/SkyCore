package com.skytonia.SkyCore.view.slots;

import com.skytonia.SkyCore.view.items.ViewItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewSlot
{

	@Getter
	private final ViewItem item;

	@Getter
	private final ViewAction action;

}
