package com.skytonia.SkyCore.packets;

/**
 * Created by Chris Brown (OhBlihv) on 8/1/2017.
 */
public interface TextRunnable
{
	
	/**
	 * @return While this method returns true, the runnable will continue to execute until it has expired.
	 */
	boolean run();
	
}
