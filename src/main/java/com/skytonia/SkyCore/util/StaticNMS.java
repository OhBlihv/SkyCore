package com.skytonia.SkyCore.util;

import com.skytonia.SkyCore.cosmetics.util.IParticlePacketFactory;
import com.skytonia.SkyCore.cosmetics.util.ParticlePacketFactory_1_10_R1;
import com.skytonia.SkyCore.cosmetics.util.ParticlePacketFactory_1_11_R1;
import com.skytonia.SkyCore.cosmetics.util.ParticlePacketFactory_1_8_R3;
import com.skytonia.SkyCore.cosmetics.util.ParticlePacketFactory_1_9_R2;
import com.skytonia.SkyCore.gui.nms.GUICreationFactory;
import com.skytonia.SkyCore.gui.nms.GUICreationFactory_1_10_R1;
import com.skytonia.SkyCore.gui.nms.GUICreationFactory_1_11_R1;
import com.skytonia.SkyCore.gui.nms.GUICreationFactory_1_8_R3;
import com.skytonia.SkyCore.gui.nms.GUICreationFactory_1_9_R2;
import com.skytonia.SkyCore.packets.PacketLibrary;
import com.skytonia.SkyCore.packets.nms.PacketLibrary_1_11_R1;
import com.skytonia.SkyCore.packets.nms.PacketLibrary_1_8_R3;
import com.skytonia.SkyCore.packets.nms.PacketLibrary_1_9_R2;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;

/**
 * Created by Chris Brown (OhBlihv) on 9/08/2016.
 */
public class StaticNMS
{
	
	private static boolean isForge = false;
	private static String serverName = "null";
	static
	{
		String packageServerName = "null";
		try //Forge is always the slowest D:
		{
			Class craftServerClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".CraftServer");
			Field serverNameField = craftServerClass.getDeclaredField("serverName");
			serverNameField.setAccessible(true);
			
			packageServerName = (String) serverNameField.get(Bukkit.getServer());
			
			//Very primitive Forge check, only really tested with Thermos
			if(packageServerName.equals("Cauldron"))
			{
				isForge = true;
			}
		}
		catch(Exception e)
		{
			//Handled below if particleFactoryInstance is not set.
		}
		
		serverName = packageServerName;
	}
	
	/*private static INMSHelper nmsHelper = null;
	public static INMSHelper getNMSHelper() throws IllegalArgumentException
	{
		if(nmsHelper == null)
		{
			switch(BUtil.getNMSVersion())
			{
				case "v1_7_R1": nmsHelper = new NMSHelper_1_7_R1(); break;
				case "v1_7_R2": nmsHelper = new NMSHelper_1_7_R2(); break;
				case "v1_7_R3": nmsHelper = new NMSHelper_1_7_R3(); break;
				case "v1_7_R4": nmsHelper = new NMSHelper_1_7_R4(); break;
				case "v1_8_R1": nmsHelper = new NMSHelper_1_8_R1(); break;
				case "v1_8_R2": nmsHelper = new NMSHelper_1_8_R2(); break;
				case "v1_8_R3": nmsHelper = new NMSHelper_1_8_R3(); break;
				case "v1_9_R1": nmsHelper = new NMSHelper_1_9_R2(); break;
				case "v1_9_R2": nmsHelper = new NMSHelper_1_9_R2(); break;
				case "v1_10_R1": nmsHelper = new NMSHelper_1_10_R1(); break;
				default: //Check if we're running forge
				{
					if(isForge)
					{
						//Cauldron is 1.7.10 -> v1_7_R4
						nmsHelper = new NMSHelper_1_7_R4();
					}
					
					if(nmsHelper == null)
					{
						throw new IllegalArgumentException("This server version is not supported '" + serverName + "'");
					}
				}
			}
			
			BUtil.logInfo("Hooked NMS Version: " + BUtil.getNMSVersion());
		}
		
		return nmsHelper;
	}*/
	
	private static IParticlePacketFactory particleFactoryInstance = null;
	public static IParticlePacketFactory getParticleFactoryInstance() throws IllegalArgumentException
	{
		if(particleFactoryInstance == null)
		{
			switch(BUtil.getNMSVersion())
			{
				//TODO: Convert to Factory
				//case "v1_7_R1": particleFactoryInstance = new ParticlePacketFactory_1_7_R1(); break;
				//case "v1_7_R2": particleFactoryInstance = new ParticlePacketFactory_1_7_R2(); break;
				//case "v1_7_R3": particleFactoryInstance = new ParticlePacketFactory_1_7_R3(); break;
				//case "v1_7_R4": particleFactoryInstance = new ParticlePacketFactory_1_7_R4(); break;
				//case "v1_8_R1": particleFactoryInstance = new ParticlePacketFactory_1_8_R1(); break;
				//case "v1_8_R2": particleFactoryInstance = new ParticlePacketFactory_1_8_R2(); break;
				case "v1_8_R3": particleFactoryInstance = new ParticlePacketFactory_1_8_R3(); break;
				//case "v1_9_R1": particleFactoryInstance = new ParticlePacketFactory_1_9_R1(); break;
				case "v1_9_R2": particleFactoryInstance = new ParticlePacketFactory_1_9_R2(); break;
				case "v1_10_R1": particleFactoryInstance = new ParticlePacketFactory_1_10_R1(); break;
				case "v1_11_R1": particleFactoryInstance = new ParticlePacketFactory_1_11_R1(); break;
				default: //Check if we're running forge
				{
					/*if(isForge)
					{
						//Cauldron is 1.7.10 -> v1_7_R4
						particleFactoryInstance = new ParticlePacketFactory_Cauldron_1_7_R4();
						break;
					}*/
					
					if(particleFactoryInstance == null)
					{
						throw new IllegalArgumentException("This server version is not supported '" + serverName + "'");
					}
				}
			}
		}
		
		return particleFactoryInstance;
	}
	
	private static GUICreationFactory guiCreationFactory = null;
	public static Inventory createInventory(int guiSize, String guiTitle) throws IllegalArgumentException
	{
		if(guiCreationFactory == null)
		{
			switch(BUtil.getNMSVersion())
			{
				//TODO: Convert to Factory
				//case "v1_7_R1": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R1(); break;
				//case "v1_7_R2": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R2(); break;
				//case "v1_7_R3": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R3(); break;
				//case "v1_7_R4": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R4(); break;
				//case "v1_8_R1": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_8_R1(); break;
				//case "v1_8_R2": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_8_R2(); break;
				case "v1_8_R3": guiCreationFactory = new GUICreationFactory_1_8_R3(); break;
				//case "v1_9_R1": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_9_R1(); break;
				case "v1_9_R2": guiCreationFactory = new GUICreationFactory_1_9_R2(); break;
				case "v1_10_R1": guiCreationFactory = new GUICreationFactory_1_10_R1(); break;
				case "v1_11_R1": guiCreationFactory = new GUICreationFactory_1_11_R1(); break;
				default: //Check if we're running forge
				{
					/*if(isForge)
					{
						//Cauldron is 1.7.10 -> v1_7_R4
						cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R4();
						break;
					}*/
					
					if(guiCreationFactory == null)
					{
						throw new IllegalArgumentException("This server version is not supported '" + serverName + "'");
					}
				}
			}
		}
		
		return guiCreationFactory.createInventory(guiSize, guiTitle);
	}
	
	private static PacketLibrary packetLibrary = null;
	public static PacketLibrary getPacketLibrary() throws IllegalArgumentException
	{
		if(packetLibrary == null)
		{
			switch(BUtil.getNMSVersion())
			{
				//case "v1_7_R1": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R1(); break;
				//case "v1_7_R2": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R2(); break;
				//case "v1_7_R3": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R3(); break;
				//case "v1_7_R4": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R4(); break;
				//case "v1_8_R1": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_8_R1(); break;
				//case "v1_8_R2": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_8_R2(); break;
				case "v1_8_R3": packetLibrary = new PacketLibrary_1_8_R3(); break;
				//case "v1_9_R1": cheapPlayerFactoryInstance = new CheapPlayerFactory_1_9_R1(); break;
				case "v1_9_R2": packetLibrary = new PacketLibrary_1_9_R2(); break;
				//case "v1_10_R1": guiCreationFactory = new GUICreationFactory_1_10_R1(); break;
				case "v1_11_R1": packetLibrary = new PacketLibrary_1_11_R1(); break;
				default: //Check if we're running forge
				{
					/*if(isForge)
					{
						//Cauldron is 1.7.10 -> v1_7_R4
						cheapPlayerFactoryInstance = new CheapPlayerFactory_1_7_R4();
						break;
					}*/
					
					if(packetLibrary == null)
					{
						throw new IllegalArgumentException("This server version is not supported '" + serverName + "'");
					}
				}
			}
		}
		
		return packetLibrary;
	}
	
}
