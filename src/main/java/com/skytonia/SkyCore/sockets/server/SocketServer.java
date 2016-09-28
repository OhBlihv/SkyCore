package com.skytonia.SkyCore.sockets.server;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class SocketServer implements Runnable
{
	
	@Getter
	private ServerSocket serverSocket;
	
	@Getter
	private int port;
	
	@Getter
	public KeyPair keys;
	
	@Getter
	private SocketServerApp app;
	
	private List<SocketMessenger> messengers;
	
	public SocketServer(SocketServerApp app, int port, KeyPair keys)
	{
		this.keys = keys;
		this.port = port;
		this.app = app;
		messengers = new ArrayList<>();
		try
		{
			serverSocket = new ServerSocket();
		}
		catch(IOException e)
		{
		}
	}
	
	public IOException start()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			app.run(this);
			return null;
		}
		catch(IOException e)
		{
			return e;
		}
	}
	
	@Override
	public void run()
	{
		while(!serverSocket.isClosed())
		{
			try
			{
				Socket socket = serverSocket.accept();
				socket.setTcpNoDelay(true);
				SocketMessenger messenger = new SocketMessenger(this, socket);
				messengers.add(messenger);
				app.onConnect(messenger);
				app.run(messenger);
			}
			catch(IOException e)
			{
			}
		}
	}
	
	public IOException close()
	{
		if(!serverSocket.isClosed())
		{
			try
			{
				serverSocket.close();
				for(SocketMessenger messenger : new ArrayList<>(messengers))
				{
					messenger.close();
				}
			}
			catch(IOException e)
			{
				return e;
			}
		}
		return null;
	}
	
	public boolean isEnabled()
	{
		return !serverSocket.isClosed();
	}
	
	public void removeMessenger(SocketMessenger socketMessenger)
	{
		messengers.remove(socketMessenger);
	}
	
}
