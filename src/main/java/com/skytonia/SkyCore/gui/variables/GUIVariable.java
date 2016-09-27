package com.skytonia.SkyCore.gui.variables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
@NoArgsConstructor
public abstract class GUIVariable
{
	
	@Getter
	protected String variable;
	
	//Can most definitely be null
	//But only if a sub-class overrides the doReplacement method and/or replaces the variable replacement
	protected List<String> replacement;
	
	/*
	 * Construction
	 */
	
	public abstract GUIVariable loadGUIVariable(Object... objects);
	
	/*
	 * Variable Processing
	 */
	
	public boolean containsVariable(String line)
	{
		return containsVariable(line, variable);
	}
	
	public boolean containsVariable(List<String> line)
	{
		return containsVariable(line, variable);
	}
	
	public static boolean containsVariable(String line, String variable)
	{
		if(variable == null)
		{
			return false;
		}
		
		return line.contains(variable);
	}
	
	public static boolean containsVariable(List<String> lines, String variable)
	{
		if(!lines.contains(variable)) //This only checks if an entire line is the variable
		{
			for(String line : lines)
			{
				if(containsVariable(line, variable))
				{
					return true;
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	public String doReplacement(String line, Player player)
	{
		return doReplacement(line, player, replacement);
	}
	
	public String doReplacement(String line, Player player, List<String> replacement)
	{
		return doReplacement(Arrays.asList(line), player, replacement).get(0);
	}
	
	//Simple Variable Replacement
	public List<String> doReplacement(List<String> line, Player player)
	{
		return doReplacement(line, player, replacement);
	}
	
	public String doReplacement(String line, Player player, Object replacement)
	{
		return doReplacement(Arrays.asList(line), player, Arrays.asList(String.valueOf(replacement))).get(0);
	}
	
	public List<String> doReplacement(List<String> line, Player player, String replacement)
	{
		return doReplacement(line, player, Arrays.asList(replacement));
	}
	
	//Dynamic Variable Replacement
	public List<String> doReplacement(List<String> line, Player player, List<String> replacement)
	{
		return doReplacement(line, variable, replacement);
	}
	
	public static List<String> doReplacement(List<String> original, String variable, List<String> originalReplacement)
	{
		//Copy the source material to avoid altering it
		List<String>    line = new ArrayList<>(original),
			replacement = null;
		
		//Avoid altering intended behaviour
		if(originalReplacement != null)
		{
			replacement = new ArrayList<>(originalReplacement);
		}
		
		for(int lineNum = 0; lineNum < line.size(); lineNum++)
		{
			String innerLine = line.get(lineNum);
			
			if(containsVariable(innerLine, variable))
			{
				if(replacement == null)
				{
					//Is the current line entire comprised of this variable?
					if(innerLine.length() == variable.length())
					{
						line.remove(lineNum); //We can't replace this line!
						continue;
					}
					else
					{
						line.set(lineNum, line.get(lineNum).replace(variable, ""));
					}
					continue;
				}
				
				if(replacement.size() > 1)
				{
					int replacementLineNum = 0;
					for(String replacementLine : replacement)
					{
						if(replacementLineNum == 0)
						{
							line.set(lineNum, replacementLine);
						}
						else
						{
							line.add(lineNum + replacementLineNum, replacementLine);
						}
						replacementLineNum++;
					}
				}
				else
				{
					String  replacementString = replacement.get(0),
						currentLine = line.get(lineNum);
					if(replacementString == null)
					{
						//Is the current line entire comprised of this variable?
						if(currentLine.length() == variable.length())
						{
							line.remove(lineNum); //We can't replace this line!
							continue;
						}
						else
						{
							replacementString = "§c<error>";
						}
					}
					
					line.set(lineNum, currentLine.replace(variable, replacementString));
				}
			}
		}
		
		return line;
	}
}
