package com.skytonia.SkyCore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableReplacer
{

	protected final List<String> content;

	public VariableReplacer(String content)
	{
		this.content = new ArrayList<>(Arrays.asList(content));
	}

	public VariableReplacer(List<String> content)
	{
		//Ensure we're not modifying the original object
		this.content = new ArrayList<>(content);
	}

	public VariableReplacer replace(String variable, String replacementContent)
	{
		if(variable == null || replacementContent == null)
		{
			return this;
		}

		for(int lineIdx = 0;lineIdx < content.size();lineIdx++)
		{
			String line = content.get(lineIdx);
			if(line == null || line.isEmpty())
			{
				continue;
			}

			content.set(lineIdx, line.replace(variable, replacementContent));
		}

		return this;
	}

	public String getLine()
	{
		return content.get(0);
	}

	public List<String> getLines()
	{
		return content;
	}

}
