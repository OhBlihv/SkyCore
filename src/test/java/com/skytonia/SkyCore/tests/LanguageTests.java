package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageTests
{

	private final DecimalFormat percentFormat = new DecimalFormat("#.#####");
	private final DecimalFormat format = new DecimalFormat("#,###");

	private final char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();

	private final Map<Integer, Integer> hashCodeMap = new ConcurrentHashMap<>();

	private int stringEntries = 0;

	private volatile int parsers = 0;

	private final Object parserLock = new Object();

	@Test
	public void hashCodeTest()
	{
		for(char charLoop : chars)
		{
			new Thread(() ->
			{
				synchronized (parserLock)
				{
					parsers++;
				}

				getString(String.valueOf(charLoop), 0);

				synchronized (parserLock)
				{
					if(--parsers == 0)
					{
						parserLock.notify();
					}
				}
			}).start();
		}

		BUtil.log("Started " + chars.length + " threads to process hashcodes");

		synchronized (parserLock)
		{
			try
			{
				parserLock.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		BUtil.log("Finished!");

		BUtil.log("> Entries: " + format.format(stringEntries));
		BUtil.log("> Hashes: " + format.format(hashCodeMap.size()));
		BUtil.log("> Collision Rate: " + percentFormat.format(stringEntries / (double) hashCodeMap.size()) + "%");
	}

	private void getString(String string, int depth)
	{
		if(++depth > 15)
		{
			return;
		}

		for (char charLoop : chars)
		{
			String newString = string + charLoop;

			addHashCode(newString);
			getString(newString, depth);
		}
	}

	private void addHashCode(String string)
	{
		if(string == null)
		{
			return;
		}

		final int hashCode = string.hashCode();

		hashCodeMap.put(hashCode, hashCodeMap.getOrDefault(hashCode, 0) + 1);

		if(++stringEntries % 10000000 == 0)
		{
			synchronized (parserLock)
			{
				BUtil.log("> Entries: " + format.format(stringEntries));
				BUtil.log("> Hashes: " + format.format(hashCodeMap.size()));
				BUtil.log("> Length of String: " + format.format(string.length()));
				BUtil.log("> Sample String: " + string);
				BUtil.log("> Collision Rate: " + percentFormat.format(stringEntries / (double) hashCodeMap.size()) + "%");
				BUtil.log("| - - - - - - - - - - - - - - - - - |");
			}
		}
	}

}
