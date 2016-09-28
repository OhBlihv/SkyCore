package com.skytonia.SkyCore.sockets.client;

import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public interface SocketClientApp
{
	
	void onConnect(SocketClient client);
	
	void onDisconnect(SocketClient client);
	
	void onHandshake(SocketClient client);
	
	void onJSON(SocketClient client, Map<String, String> map);
	
}
