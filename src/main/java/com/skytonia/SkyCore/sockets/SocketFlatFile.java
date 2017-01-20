package com.skytonia.SkyCore.sockets;

import com.skytonia.SkyCore.util.file.FlatFile;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class SocketFlatFile extends FlatFile
{
	
	private static SocketFlatFile instance = null;
	public static SocketFlatFile getInstance()
	{
		if(instance == null)
		{
			instance = new SocketFlatFile();
		}
		return instance;
	}
	
	public SocketFlatFile()
	{
		super("sockets.yml", "SkyCore");
	}
	
}
