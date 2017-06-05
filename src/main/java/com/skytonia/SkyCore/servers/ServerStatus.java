package com.skytonia.SkyCore.servers;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public enum ServerStatus
{
	
	ONLINE(true),
	FULL(false),
	VIP_JOIN(true),
	OFFLINE(false),
	LOCAL_SERVER(false),
	REBOOTING(false),
	WHITELIST(true);
	
	public final boolean joinable;
	
	ServerStatus(boolean joinable)
	{
		this.joinable = joinable;
	}
	
}
