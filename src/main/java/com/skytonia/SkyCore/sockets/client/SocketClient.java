package com.skytonia.SkyCore.sockets.client;

import com.google.gson.JsonSyntaxException;
import com.skytonia.SkyCore.sockets.SocketUtil;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class SocketClient extends Thread
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
		//Override System.err to ignore errors
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
		
		this.host = host;
		this.port = port;
		this.keys = keys;
		this.app = app;
		this.name = name;
		enabled.set(true);
		socket = new Socket();
		
		/*new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				if(isHandshaked())
				{
					this.cancel();
				}
				else
				{
					//Attempt to handshake until shook.
					handshake();
				}
			}
			
		}.runTaskTimerAsynchronously(SkyCore.getPluginInstance(), 20L, 20L);*/
	}
	
	private long lastHandshakeAttempt = System.currentTimeMillis() - 6000L; //Ensure we get an initial handshake
	
	public void run()
	{
		while(enabled.get())
		{
			try
			{
				if(socket != null && !socket.isClosed())
				{
					socket.close();
					socket = null;
					BUtil.logInfo("Removing old socket");
				}
				
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
								keyread +=read;
							}
							else
							{
								try
								{
									key = SocketUtil.RSA.loadPublicKey(keyread);
									writer.println(SocketUtil.RSA.savePublicKey(keys.getPublic()));
									writer.println("--end--"); //Print the end tag unencrypted for efficiency
									writer.flush();
									//BUtil.logInfo("Sent Public Keys");
								}
								catch(GeneralSecurityException e)
								{
									e.printStackTrace();
								}
							}
						}
						else
						{
							if(!read.equals("--end--"))
							{
								fullmessage += read + "\n";
							}
							else
							{
								String message = "";
								for(String part : fullmessage.split("[\n]"))
								{
									message += SocketUtil.RSA.decrypt(part, keys.getPrivate());
								}
								
								if(!message.isEmpty())
								{
									Map<String, String> map = null;
									try
									{
										map = SocketUtil.gson().fromJson(message, Map.class);
									}
									catch(JsonSyntaxException e)
									{
										e.printStackTrace();
									}
									
									if(map != null)
									{
										//BUtil.logInfo("Channel: " + map.get("channel"));
										if(map.get("channel").equals("SocketAPI"))
										{
											String data = map.get("data");
											if(data.equals("handshake"))
											{
												BUtil.logInfo("Replying to Handshake...");
												handshake();
											}
											else if(data.equals("handshaked"))
											{
												BUtil.logMessage("Registered socket listener as '" + name + "'");
												handshaked.set(true);
												app.onHandshake(this);
											}
										}
										else
										{
											app.onJSON(this, map);
										}
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
				BUtil.logInfo("Socket Error (" + e.getClass().getSimpleName() + "). Reopening Socket...");
				
				System.out.println("> " + e.getMessage());
				
				if(e.getStackTrace().length == 0)
				{
					BUtil.logInfo("No Stack Trace Provided");
				}
				else
				{
					for(StackTraceElement element : e.getStackTrace())
					{
						System.out.println(" at " + element.toString());
					}
				}
				
				BUtil.logInfo("===================================");
				if(e instanceof SocketException)
				{
					close();
				}
				
				try
				{
					sleep(5000);
				}
				catch(InterruptedException e2)
				{
					e.printStackTrace();
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
	
	public void handshake()
	{
		if(System.currentTimeMillis() - lastHandshakeAttempt > 5000L)
		{
			lastHandshakeAttempt = System.currentTimeMillis();
			
			BUtil.logMessage("Attempting handshake with proxy...");
			
			HashMap<String, String> dataMap = new HashMap<>();
			dataMap.put("channel", "SocketAPI");
			dataMap.put("data", "handshake");
			dataMap.put("name", name);
			write(SocketUtil.gson().toJson(dataMap), true);
		}
	}
	
	public void writeJSON(String channel, String data)
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("channel", channel);
		map.put("data", data);
		write(SocketUtil.gson().toJson(map));
	}
	
	private void write(String data)
	{
		write(data, false);
	}
	
	private void write(String data, boolean force)
	{
		if(!force && (!isConnectedAndOpened() || !isHandshaked()))
		{
			return; //No Exception Required.
		}
		
		try
		{
			synchronized(writer)
			{
				String[] split = SocketUtil.split(data, 20);
				for(String str : split)
				{
					writer.println(SocketUtil.RSA.encrypt(str, key));
				}
				//writer.println(SocketUtil.RSA.encrypt("--end--", key));
				writer.println("--end--"); //Print the end tag unencrypted for efficiency
				writer.flush();
				
				//BUtil.logInfo("Wrote " + data);
			}
		}
		catch(NullPointerException e)
		{
			//
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
	public IOException interruptClient()
	{
		enabled.set(false);
		return close();
	}
	
	public boolean isEnabled()
	{
		return enabled.get();
	}
}
