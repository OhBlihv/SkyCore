package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import org.junit.Test;

/**
 * Created by Chris Brown (OhBlihv) on 11/28/2016.
 */
public class UtilTest
{
	
	@Test
	public void romanNumeralTest()
	{
		System.out.println(BUtil.toRomanNumerals(2016));
		System.out.println(BUtil.toRomanNumerals(1400));
		System.out.println(BUtil.toRomanNumerals(14));
		System.out.println(BUtil.toRomanNumerals(206));
	}
	
}
