package com.skytonia.SkyCore.redis;

import com.skytonia.SkyCore.util.file.FlatFile;

/**
 * Created by Chris Brown (OhBlihv) on 3/3/2017.
 */
public class RedisFlatFile extends FlatFile
{
	
	private static RedisFlatFile instance = null;
	public static RedisFlatFile getInstance()
	{
		if(instance == null)
		{
			instance = new RedisFlatFile();
		}
		return instance;
	}
	
	public RedisFlatFile()
	{
		super("redis.yml", "SkyCore");
	}
	
}
