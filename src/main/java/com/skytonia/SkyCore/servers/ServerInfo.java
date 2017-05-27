package com.skytonia.SkyCore.servers;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
	
	@Getter
	private final List<String> staffList = new ArrayList<>();
	
	@Getter
	private final List<String> playerList = new ArrayList<>();
	
	@Getter
	@Setter
	private long lastUpdate = 0L;
	
}
