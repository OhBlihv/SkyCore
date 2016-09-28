package com.skytonia.SkyCore.sockets.server;

import com.skytonia.SkyCore.sockets.SocketAPI;

import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public interface SocketServerApp
{
	
	void onConnect(SocketMessenger mess);
	
	void onHandshake(SocketMessenger mess, String name);
	
	void onJSON(SocketMessenger mess, Map<String, String> map);
	
	void onDisconnect(SocketMessenger mess);
	
	void run(SocketMessenger mess);
	
	void run(SocketServer server);
	
}