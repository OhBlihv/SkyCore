package com.skytonia.SkyCore.servers;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public class ServerInfo
{
	
	@Getter
	@Setter
	private ServerStatus serverStatus = ServerStatus.OFFLINE;
	
	@Getter
	@Setter
	private int playerCount = 0;
	
}
