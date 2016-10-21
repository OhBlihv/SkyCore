package com.skytonia.SkyCore.cheapobjects.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

/**
 * Created by Chris Brown (OhBlihv) on 19/05/2016.
 */
@RequiredArgsConstructor
public abstract class CheapPlayer
{

	/*
	 * Player Retrieval/Storage
	 */

	public Player getPlayer()
	{
		if(isOnline())
		{
			return player.get();
		}
		player = new WeakReference<>(Bukkit.getPlayerExact(playerName));
		return player.get();
	}
	public void setPlayer(Player player)
	{
		this.player = new WeakReference<>(player);
	}
	public boolean isOnline()
	{
		return player != null && player.get() != null && player.get().isOnline();
	}
	
	final String playerName;
	private WeakReference<Player> player; //I wouldn't normally do this, but this needs easy access to a player's location.

	public abstract Object getPlayerConnection();
	
	//Implementations will need to ensure this is an instance of their versioned packet and not another object
	public abstract void queuePacket(Object packet);

	@Override
	public boolean equals(Object object)
	{
		// == allows memory addresses to be compared, which we are completely okay with.
		return object instanceof CheapPlayer && ((CheapPlayer) object).playerName == this.playerName;
	}

}
