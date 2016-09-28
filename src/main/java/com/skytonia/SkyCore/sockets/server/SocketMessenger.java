package com.skytonia.SkyCore.sockets.server;

import com.google.gson.JsonSyntaxException;
import com.skytonia.SkyCore.sockets.SocketAPI;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class SocketMessenger implements Runnable
{
	
	@Getter
	private Socket socket;
	@Getter
	private SocketServer server;
	private PublicKey key;
	private PrintWriter writer;
	private BufferedReader reader;
	private String keyread = "";
	private String fullmessage = "";
	private AtomicBoolean handshaked = new AtomicBoolean(false);
	
	@Getter
	private String name;
	
	public SocketMessenger(SocketServer socketServer, final Socket socket)
	{
		this.socket = socket;
		this.server = socketServer;
		if(server.isEnabled() && socket.isConnected() && !socket.isClosed())
		{
			try
			{
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				try
				{
					writer.println(SocketAPI.RSA.savePublicKey(server.keys.getPublic()));
					writer.println("--end--");
					writer.flush();
				}
				catch(GeneralSecurityException e)
				{
					e.printStackTrace();
				}
			}
			catch(IOException e)
			{
			}
		}
	}
	
	@Override
	public void run()
	{
		while(server.isEnabled() && socket.isConnected() && !socket.isClosed())
		{
			try
			{
				String read = reader.readLine();
				if(read == null)
				{
					close();
				}
				else
				{
					if(key == null)
					{
						if(!read.equals("--end--"))
						{
							keyread += read;
						}
						else
						{
							try
							{
								key = SocketAPI.RSA.loadPublicKey(keyread);
								writeJSON("SocketAPI", "handshake");
							}
							catch(GeneralSecurityException e)
							{
								e.printStackTrace();
							}
						}
					}
					else
					{
						String message = SocketAPI.RSA.decrypt(read, server.keys.getPrivate());
						if(message != null && !message.isEmpty())
						{
							if(!message.equals("--end--"))
							{
								fullmessage += message;
							}
							else
							{
								if(fullmessage != null && !fullmessage.isEmpty())
								{
									try
									{
										@SuppressWarnings("unchecked")
										Map<String, String> map = SocketAPI.gson().fromJson(fullmessage, Map.class);
										if(map.get("channel").equals("SocketAPI"))
										{
											if(map.get("data").equals("handshake"))
											{
												handshaked.set(true);
												name = map.get("name");
												server.getApp().onHandshake(this, name);
												writeJSON("SocketAPI", "handshaked");
											}
										}
										else
										{
											server.getApp().onJSON(this, map);
										}
									}
									catch(JsonSyntaxException e)
									{
									}
								}
								fullmessage = "";
							}
						}
					}
				}
			}
			catch(IOException e)
			{
				if(e.getClass().getSimpleName().equals("SocketException"))
				{
					close();
				}
			}
		}
	}
	
	public boolean isConnectedAndOpened()
	{
		return getSocket().isConnected() && !getSocket().isClosed();
	}
	
	public boolean isHandshaked()
	{
		return handshaked.get();
	}
	
	public void writeJSON(String channel, String data)
	{
		try
		{
			HashMap<String, String> hashmap = new HashMap<>();
			hashmap.put("channel", channel);
			hashmap.put("data", data);
			String json = SocketAPI.gson().toJson(hashmap);
			write(json);
		}
		catch(NullPointerException e)
		{
		}
	}
	
	private void write(String data)
	{
		try
		{
			String[] split = SocketAPI.split(data, 20);
			for(String str : split)
			{
				writer.println(SocketAPI.RSA.encrypt(str, key));
			}
			writer.println(SocketAPI.RSA.encrypt("--end--", key));
			writer.flush();
		}
		catch(NullPointerException e)
		{
		}
	}
	
	public IOException close()
	{
		if(!socket.isClosed())
		{
			try
			{
				socket.close();
				server.removeMessenger(this);
				server.getApp().onDisconnect(this);
			}
			catch(IOException e)
			{
				return e;
			}
		}
		return null;
	}
}
