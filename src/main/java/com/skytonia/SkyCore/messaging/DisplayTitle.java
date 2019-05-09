package com.skytonia.SkyCore.messaging;

import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.VariableReplacer;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DisplayTitle
{

	public class MessageBuilder
	{

		private Player player;

		private final VariableReplacer title, subtitle;

		public MessageBuilder(Player player, DisplayTitle displayTitle)
		{
			this.player = player;

			this.title = new VariableReplacer(displayTitle.title);
			this.subtitle = new VariableReplacer(displayTitle.subtitle);
		}

		public MessageBuilder replace(String variable, String replacement)
		{
			title.replace(variable, replacement);
			subtitle.replace(variable, replacement);

			return this;
		}

		public void send()
		{
			if(player == null)
			{
				throw new IllegalArgumentException("Player cannot be null!");
			}

			send(player);
		}

		public void send(Player player)
		{
			player.sendTitle(title.getLine(), subtitle.getLine(), fadeIn, stay, fadeOut);
		}

	}

	private final String title;
	private final String subtitle;

	private final int fadeIn, stay, fadeOut;

	public static DisplayTitle load(ConfigurationSection configurationSection)
	{
		if(configurationSection == null)
		{
			return null;
		}

		//Easy defaults
		String title = "",
			subtitle = "";

		int fadeIn = 5, fadeOut = 5,
			stay = 20;

		if(configurationSection.contains("title"))
		{
			title = BUtil.translateColours(configurationSection.getString("title"));
		}

		if(configurationSection.contains("subtitle"))
		{
			subtitle = BUtil.translateColours(configurationSection.getString("subtitle"));
		}

		if(configurationSection.contains("fade"))
		{
			fadeIn = configurationSection.getInt("in", fadeIn);
			fadeOut = configurationSection.getInt("out", fadeOut);
			stay = configurationSection.getInt("stay", stay);
		}

		return new DisplayTitle(title, subtitle, fadeIn, stay, fadeOut);
	}

	public DisplayTitle.MessageBuilder prepare(Player player)
	{
		return new MessageBuilder(player, this);
	}

	public DisplayTitle.MessageBuilder prepare()
	{
		return new MessageBuilder(null, this);
	}

	public void send(Player player)
	{
		player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
	}

}
