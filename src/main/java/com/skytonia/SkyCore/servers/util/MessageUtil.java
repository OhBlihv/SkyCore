package com.skytonia.SkyCore.servers.util;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class MessageUtil
{
	
	private static final String SPLITTER = "¿¡";
	
	public static String mergeArguments(String... args)
	{
		StringBuilder merged = new StringBuilder();
		for(String arg : args)
		{
			merged.append(arg).append(SPLITTER);
		}
		
		String mergedString = merged.toString();
		
		if(args.length > 0)
		{
			mergedString = mergedString.substring(0, mergedString.length() - SPLITTER.length());
		}
		
		return mergedString;
	}
	
	public static String[] splitArguments(String merged)
	{
		return merged.split(SPLITTER);
	}
	
}
