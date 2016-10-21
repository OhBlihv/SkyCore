package com.skytonia.SkyCore.movement;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.skytonia.SkyCore.SkyCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class MovementInfo
{
	
	@Getter
	private final Player player;
	
	@Getter
	private final String targetServer;
	
	@Getter
	@Setter
	private String response;
	
	private final MovementAction movementAction;
	
	private int timeoutTaskId = -1;
	
	public MovementInfo(Player player, String targetServer, MovementAction movementAction)
	{
		this.player = player;
		this.targetServer = targetServer;
		this.movementAction = movementAction;
		
		timeoutTaskId = Bukkit.getScheduler().runTaskLater(SkyCore.getPluginInstance(), () -> MovementManager.onFailTransfer(player, "TIMEOUT"), MovementManager.timeoutDelay).getTaskId();
	}
	
	public void processSuccess()
	{
		if(timeoutTaskId != -1)
		{
			Bukkit.getScheduler().cancelTask(timeoutTaskId);
			timeoutTaskId = -1;
		}
		
		movementAction.onSuccessReceive(player, targetServer);
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(targetServer);
		
		player.sendPluginMessage(SkyCore.getPluginInstance(), "BungeeCord", out.toByteArray());
	}
	
	public void processFailure()
	{
		timeoutTaskId = -1;
		
		if(movementAction != null)
		{
			movementAction.onFailReceive(player, targetServer, response);
		}
	}
	
}
