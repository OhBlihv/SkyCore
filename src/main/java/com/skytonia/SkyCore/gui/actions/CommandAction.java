package com.skytonia.SkyCore.gui.actions;

import com.skytonia.SkyCore.util.BUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class CommandAction extends ElementAction
{
	
	@AllArgsConstructor
	private static class CommandContainer
	{
		
		@Getter
		private String command;
		
		@Getter
		private CommandActionExecutor executor;
		
	}
	
	public enum CommandActionExecutor
	{
		
		CONSOLE,
		PLAYER
		
	}
	
	@Getter
	private Deque<CommandContainer> commands;
	
	@Override
	public boolean onClick(Player player, int slot)
	{
		for(CommandContainer commandContainer : commands)
		{
			if(commandContainer.getExecutor() == CommandActionExecutor.CONSOLE)
			{
				String finalCommand = commandContainer.getCommand().replaceAll("\\{player\\}", player.getName());
				//TODO: Re-enable these with a toggle-able debug
				//BUtil.logInfo("Executing as CONSOLE: " + finalCommand);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
			}
			else //Change this if more executors come up. But I doubt it
			{
				String finalCommand = "/" + commandContainer.getCommand().replaceAll("\\{player\\}", player.getName());
				//BUtil.logInfo("Executing as PLAYER: " + finalCommand);
				player.chat(finalCommand);
			}
		}
		return true;
	}
	
	@Override
	public ElementAction loadAction(ConfigurationSection configurationSection)
	{
		Deque<CommandContainer> commandContainers = new ArrayDeque<>();
		
		if(configurationSection.getKeys(false).isEmpty())
		{
			BUtil.logError( "No commands defined for COMMAND action. Configure as such: \n" +
				                "actions: COMMAND\n" +
				                "options:\n" +
				                "   COMMAND:\n" +
				                "       \"say Example\":\n" +
				                "           execute-as: CONSOLE");
		}
		else
		{
			for(String commandName : configurationSection.getKeys(false))
			{
				CommandActionExecutor commandActionExecutor = CommandActionExecutor.CONSOLE;
				if(configurationSection.isConfigurationSection(commandName))
				{
					ConfigurationSection commandSection = configurationSection.getConfigurationSection(commandName);
					
					if(!configurationSection.isConfigurationSection(commandName) || !commandSection.contains("execute-as"))
					{
						BUtil.logInfo("'" + commandName + "' command does not contain an explicit execute-as, and has been ignored.");
						continue;
					}
					
					if(commandSection.getString("execute-as").equalsIgnoreCase("PLAYER"))
					{
						commandActionExecutor = CommandActionExecutor.PLAYER;
					}
				}
				
				commandContainers.add(new CommandContainer(commandName, commandActionExecutor));
			}
		}
		
		this.commands = commandContainers;
		
		return this;
	}
	
}