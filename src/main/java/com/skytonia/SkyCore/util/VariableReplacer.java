package com.skytonia.SkyCore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class VariableReplacer
{

	protected final List<String> content;

	public VariableReplacer(String content)
	{
		this(Arrays.asList(content));
	}

	public VariableReplacer(List<String> content)
	{
		//Ensure we're not modifying the original object
		if(content != null && !content.isEmpty())
		{
			this.content = new ArrayList<>(content);
		}
		else
		{
			this.content = new ArrayList<>();
		}
	}

	/*
	 * Custom Callable Functions
	 */

	public VariableReplacer replace(String variable, Callable<?> replacementCallable)
	{
		if(content.isEmpty() || variable == null || replacementCallable == null)
		{
			return this;
		}

		try
		{
			for(int lineIdx = 0;lineIdx < content.size();lineIdx++)
			{
				String line = content.get(lineIdx);
				//Avoid running callable function unless we're sure the line contains the variable
				if(line == null || line.isEmpty() || !line.contains(variable))
				{
					continue;
				}

				Object replacement = replacementCallable.call();
				if(replacement instanceof CharSequence)
				{
					content.set(lineIdx, line.replace(variable, (CharSequence) replacement));
				}
				else if(replacement instanceof List) //We have to assume it's of type string
				{
					insertMultipleLines(lineIdx, (List<String>) replacement);
				}
				else
				{
					throw new IllegalArgumentException("Replacement Callable returned object '" + replacement + "' of type '" +
						(replacement == null ? "null" : replacement.getClass().getSimpleName() + "'"));
				}
			}
		}
		catch(Exception e)
		{
			BUtil.log("Encountered exception while replacing variable '" + variable + "':");
			e.printStackTrace();
		}

		return this;
	}

	/*
	 * Simple String Replacements
	 */

	public VariableReplacer replace(String variable, String replacementContent)
	{
		if(content.isEmpty() || variable == null || replacementContent == null)
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

	public VariableReplacer replace(String variable, List<String> replacementContent)
	{
		if(content.isEmpty() || variable == null || replacementContent == null)
		{
			return this;
		}

		for(int lineIdx = 0;lineIdx < content.size();lineIdx++)
		{
			String line = content.get(lineIdx);
			if(line == null || line.isEmpty() || !line.contains(variable))
			{
				continue;
			}

			insertMultipleLines(lineIdx, replacementContent);
		}

		return this;
	}

	private void insertMultipleLines(int lineIdx, List<String> replacementContent)
	{
		content.remove(lineIdx);
		//Bulk insert the replacement lines in-between existing content
		for(String replacementLine : replacementContent)
		{
			content.add(lineIdx++, replacementLine);
		}
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
