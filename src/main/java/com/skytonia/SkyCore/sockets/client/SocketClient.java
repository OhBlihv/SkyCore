package com.skytonia.SkyCore.sockets.client;

import com.google.gson.JsonSyntaxException;
import com.skytonia.SkyCore.sockets.SocketUtil;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class SocketClient implements Runnable
{
	
	@Getter
	private String host;
	@Getter
	private int port;
	@Getter
	private Socket socket;
	private AtomicBoolean enabled = new AtomicBoolean(true);
	private PublicKey key;
	private BufferedReader reader;
	private PrintWriter writer;
	private KeyPair keys;
	private AtomicBoolean handshaked = new AtomicBoolean(false);
	private SocketClientApp app;
	private String name;
	
	public SocketClient(SocketClientApp app, String name, String host, int port, KeyPair keys)
	{
		this.host = host;
		this.port = port;
		this.keys = keys;
		this.app = app;
		this.name = name;
		enabled.set(true);
		socket = new Socket();
	}
	
	public void run()
	{
		while(enabled.get())
		{
			try
			{
				socket = new Socket(host, port);
				socket.setTcpNoDelay(true);
				app.onConnect(this);
				
				key = null;
				handshaked.set(false);
				String keyread = "";
				String fullmessage = "";
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				while(enabled.get() && socket.isConnected() && !socket.isClosed())
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
									key = SocketUtil.RSA.loadPublicKey(keyread);
									writer.println(SocketUtil.RSA.savePublicKey(keys.getPublic()));
									writer.println("--end--");
									writer.flush();
								}
								catch(GeneralSecurityException e)
								{
									e.printStackTrace();
								}
							}
						}
						else
						{
							String message = SocketUtil.RSA.decrypt(read, keys.getPrivate());
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
											Map<String, String> map = SocketUtil.gson().fromJson(fullmessage, Map.class);
											if(map.get("channel").equals("SocketUtil"))
											{
												if(map.get("data").equals("handshake"))
												{
													handshake();
												}
												else if(map.get("data").equals("handshaked"))
												{
													handshaked.set(true);
													app.onHandshake(this);
												}
											}
											else
											{
												app.onJSON(this, map);
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
		return socket.isConnected() && !socket.isClosed();
	}
	
	public boolean isHandshaked()
	{
		return handshaked.get();
	}
	
	private void handshake()
	{
		try
		{
			HashMap<String, String> hashmap = new HashMap<>();
			hashmap.put("channel", "SocketUtil");
			hashmap.put("data", "handshake");
			hashmap.put("name", name);
			String json = SocketUtil.gson().toJson(hashmap);
			write(json);
		}
		catch(NullPointerException e)
		{
		}
	}
	
	public void writeJSON(String channel, String data)
	{
		try
		{
			HashMap<String, String> hashmap = new HashMap<>();
			hashmap.put("channel", channel);
			hashmap.put("data", data);
			String json = SocketUtil.gson().toJson(hashmap);
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
			String[] split = SocketUtil.split(data, 20);
			for(String str : split)
			{
				writer.println(SocketUtil.RSA.encrypt(str, key));
			}
			writer.println(SocketUtil.RSA.encrypt("--end--", key));
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
				app.onDisconnect(this);
			}
			catch(IOException e)
			{
				return e;
			}
		}
		return null;
	}
	
	public IOException interrupt()
	{
		enabled.set(false);
		return close();
	}
	
	public boolean isEnabled()
	{
		return enabled.get();
	}
}
