package com.skytonia.SkyCore.messaging;

import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.VariableReplacer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class DisplayMessage
{

	public class MessageBuilder
	{

		private CommandSender sender;
		private final VariableReplacer messageLines;

		public MessageBuilder(CommandSender sender, List<String> messageLines)
		{
			this.sender = sender;
			this.messageLines = new VariableReplacer(messageLines);
		}

		public MessageBuilder replace(String variable, String replacement)
		{
			messageLines.replace(variable, replacement);

			return this;
		}

		public void send()
		{
			if(sender == null)
			{
				throw new IllegalArgumentException("Sender cannot be null!");
			}

			send(sender);
		}

		public void send(CommandSender sender)
		{
			final List<String> lines = messageLines.getLines();

			if(lines.isEmpty() || lines.get(0).isEmpty())
			{
				BUtil.log("No message provided, but sent to player at");
				new Throwable().printStackTrace();
			}

			for(String line : lines)
			{
				sender.sendMessage(line);
			}
		}

		public void broadcast()
		{
			for(String line : messageLines.getLines())
			{
				Bukkit.broadcastMessage(line);
			}
		}

		public void execute(CommandSender executor)
		{
			for(String command : messageLines.getLines())
			{
				Bukkit.dispatchCommand(executor, command);
			}
		}

		public void execute()
		{
			final CommandSender executor = Bukkit.getConsoleSender();

			for(String command : messageLines.getLines())
			{
				Bukkit.dispatchCommand(executor, command);
			}
		}

	}

	private final List<String> messageLines;

	public DisplayMessage(Object object)
	{
		if(object != null)
		{
			List<String> tempMessageLines;
			if(object instanceof String)
			{
				tempMessageLines = Collections.singletonList((String) object);
			}
			else if(object instanceof List && (((List) object).isEmpty() || ((List) object).get(0) instanceof String))
			{
				tempMessageLines = (List<String>) object;
			}
			else
			{
				throw new IllegalArgumentException("Message not of valid type '" + object + "'");
			}

			messageLines = BUtil.translateColours(tempMessageLines);
		}
		else
		{
			messageLines = null;
		}
	}

	public boolean isPresent()
	{
		return messageLines != null;
	}

	public void send(CommandSender sender)
	{
		if(messageLines.isEmpty() || messageLines.get(0).isEmpty())
		{
			BUtil.log("No message provided, but sent to player at");
			new Throwable().printStackTrace();
		}

		for(String line : messageLines)
		{
			sender.sendMessage(line);
		}
	}

	public MessageBuilder prepare()
	{
		return new MessageBuilder(null, messageLines);
	}

	public MessageBuilder prepare(CommandSender sender)
	{
		return new MessageBuilder(sender, messageLines);
	}

}

