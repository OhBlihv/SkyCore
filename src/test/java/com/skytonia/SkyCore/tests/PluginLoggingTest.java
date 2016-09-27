package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import org.junit.Test;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class PluginLoggingTest
{
	
	@Test
	public void pluginLoggingPrefixTest()
	{
		BUtil.logMessage("Test Message #1");
		BUtil.logMessage("Test Message #2");
	}
	
	@Test
	public void pluginRetrievalTest()
	{
		System.out.println(BUtil.getCallingPlugin());
	}
	
}
