package com.skytonia.SkyCore;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.event.Listener;

/**
 * Created by Chris Brown (OhBlihv) on 2/8/2017.
 */
public class PacketHandling implements Listener
{
	
	public PacketHandling(SkyCore plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SCOREBOARD_TEAM)
		{
			
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
					//
			}
			
			@Override
			public void onPacketSending(PacketEvent event)
			{
				WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam(event.getPacket());
				int packetMode = packet.getMode();
				if(packetMode == 0 || packetMode == 2)
				{
					if(!packet.getCollisionRule().equals("never"))
					{
						packet.setCollisionRule("never");
					}
				}
				
				packet.setNameTagVisibility("never");
			}
			
		});
		
		/*final List<Collection<WrappedSignedProperty>> gameProfiles = new ArrayList<>();
		try
		{
			gameProfiles.add(getProfileFrom("Obliviator"));
			gameProfiles.add(getProfileFrom("ImSoFuckingGay"));
			gameProfiles.add(getProfileFrom("Abstain"));
			gameProfiles.add(getProfileFrom("DaddyWheelz"));
			gameProfiles.add(getProfileFrom("Washlie"));
			gameProfiles.add(getProfileFrom("SkytoniaNetwork"));
			gameProfiles.add(getProfileFrom("Erictigerfart"));
			gameProfiles.add(getProfileFrom("Erictoigaroar"));
			gameProfiles.add(getProfileFrom("mmadoe"));
		}
		catch(Throwable updateNav)
		{
			updateNav.printStackTrace();
		}
		
		//April Fools
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO)
		{
			
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				//
			}
			
			private final Random random = new Random();
			
			/*private final String[] randomisedSkins = new String[] {
				"https://static.namemc.com/texture/0ea9a9fdd8fe2950.png"//,
				//"https://static.namemc.com/texture/ba6d1fad0777a593.png",
				//"https://static.namemc.com/texture/ee4c5db73f9d8f46.png",
				//"https://static.namemc.com/texture/a13cb6c41d926809.png",
				//"https://static.namemc.com/texture/0ae0463cb63e9493.png",
				//"https://static.namemc.com/texture/7bf211bc7ffd6c40.png"
			};
			
			private final String[] randomisedSignatures = new String[] {
				"rOwZqnqMHkI7Erdc2SeK4wMvNwEO9YzqDEEPUdUh7Eu06tUVQgVDdHasUlWTL2kq7WgLsXhSYcNXflXfWFePy8iD3N/8BeMWATpbrIzWdmF41K8DW+d5/zmFLu8fzTC1tu2IHnrkzOHc3C4UcdQi4Jpb4r+A7cylkgk1Wjl6oaaM5AbEQotrzljGVLko8ibZRwmx5anJq4bd2SdXImTzLxU0GR071l+cJHc9oa6RNU+hgmsdI+Ffmh2F5ZbS4tzmkLD2AxZsrOYiqwRd+286W5jlK8tXXv/V3Y7lpfe2lmKqUpHj6RwlxCVv9aBa1u0uvePcL/ls49MzqZvFu+BcS4kahaeELExHtzQA1FMqDVEALPT2OkQ2MrJiqARJunRP6zMHbUpiTsE7m/ZdMZAoCuLwi32M1Rx/gfgo94rjbR17TlbcLFQ8DdNs0SYPjXkkWIs5to2WTYT0hSPDPOv6W0j1WP2HkOXEoi2cIc7h1HsFnxOlX1IxJqKUMCpSqbKpThVc71k7IXmnRL9WLvsPn/vFXzJeZ5nQ0vtfRFHFeNqnTehGer05FrdIFEDzr6542k9OmqgB0gq/Y6/Jta8iuw01VBEcDJHkxtJfqBis5DBdPX8G0foVvXb3QLql5Z5qojcE+OIBUrS1L+9cS+B0ogyi5/Fx7LbfLnwCLCXkcyY="
			};*
			
			@Override
			public void onPacketSending(PacketEvent event)
			{
				WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
				
				if(packet.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER)
				{
					List<PlayerInfoData> data = packet.getData(),
						fooledData = new ArrayList<>();
					
					for(PlayerInfoData playerData : data)
					{
						/*PlayerInfoData fooledPlayerData = new PlayerInfoData(
							new WrappedGameProfile(playerData.getProfile().getUUID(), /*"Dinnerbone"* playerData.getProfile().getName()),
							playerData.getLatency(),
							playerData.getGameMode(),
							playerData.getDisplayName()
						);*
						
						int randomNumber = random.nextInt(gameProfiles.size());
						Collection<WrappedSignedProperty> selectedTexture = gameProfiles.get(randomNumber);
						
						//BUtil.logInfo(playerData.getProfile().getName() + " is using " + randomNumber);
						
						playerData.getProfile().getProperties().removeAll("textures");
						playerData.getProfile().getProperties().putAll("textures", selectedTexture);
						
						/*int skinNum = random.nextInt(randomisedSkins.length);
						
						BUtil.logInfo("-------------------------------");
						BUtil.logInfo(playerData.getProfile().getProperties().toString());
						fooledPlayerData.getProfile().getProperties().put("textures", new WrappedSignedProperty("textures",
						                                                                                        Base64.getEncoder().encodeToString(
							    ("{\"timestamp\":" + System.currentTimeMillis() + 000 + "," +
								     "\"profileId\":\"" + playerData.getProfile().getUUID().toString().replace("-", "") + "\"," +
								     "\"profileName\":\"" + playerData.getProfile().getName() + "\"," +
								     "\"textures\":{\"SKIN\":{\"url\":\"" + randomisedSkins[skinNum] + "\"}}}").getBytes()
						    ), randomisedSignatures[skinNum]));
						BUtil.logInfo("---------------------------");
						BUtil.logInfo(fooledPlayerData.getProfile().getProperties().toString());
						BUtil.logInfo("---------------------------");*/
						
						//fooledData.add(fooledPlayerData);
					}
					
					/*if(fooledData.isEmpty())
					{
						return;
					}*
					
					//packet.setData(fooledData);
					packet.setData(data);
				}
			}
			
		});
	}
	
	private Collection<WrappedSignedProperty> getProfileFrom(String playerName)
	{
		WrappedGameProfile profile = WrappedGameProfile.fromOfflinePlayer(Bukkit.getOfflinePlayer(playerName));
		Object handle = profile.getHandle();
		Object sessionService = getSessionService();
		try
		{
			Method method = getFillMethod(sessionService);
			method.invoke(sessionService, handle, true);
		}
		catch (IllegalAccessException | InvocationTargetException updateNav)
		{
			updateNav.printStackTrace();
			return null;
		}
		profile = WrappedGameProfile.fromHandle(handle);
		return profile.getProperties().get("textures");
	}
	
	private Object getSessionService()
	{
		Server server = Bukkit.getServer();
		try
		{
			Object mcServer = server.getClass().getDeclaredMethod("getServer").invoke(server);
			for (Method m : mcServer.getClass().getMethods())
			{
				if (m.getReturnType().getSimpleName().equalsIgnoreCase("MinecraftSessionService"))
				{
					return m.invoke(mcServer);
				}
			}
		}
		catch (Exception ex)
		{
			throw new IllegalStateException("An error occurred while trying to get the session service", ex);
		}
		throw new IllegalStateException("No session service found :o");
	}
	
	private Method getFillMethod(Object sessionService)
	{
		for(Method m : sessionService.getClass().getDeclaredMethods())
		{
			if(m.getName().equals("fillProfileProperties"))
			{
				return m;
			}
		}
		throw new IllegalStateException("No fillProfileProperties method found in the session service :o");
	}
	
	/*private static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	private static final Team dinnerboneTeam;
	static
	{
		Team team;
		try
		{
			team = scoreboard.registerNewTeam("Dinnerbone");
		}
		catch(Throwable updateNav)
		{
			team = scoreboard.getTeam("Dinnerbone");
		}
		
		dinnerboneTeam = team;
		
		dinnerboneTeam.setPrefix("§7");
		dinnerboneTeam.addEntry("Dinnerbone");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(!dinnerboneTeam.hasPlayer(event.getPlayer()))
		{
			dinnerboneTeam.addPlayer(event.getPlayer());
		}
	}*/
	
}
