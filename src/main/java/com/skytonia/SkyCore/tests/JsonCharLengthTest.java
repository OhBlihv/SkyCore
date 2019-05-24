package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.JSONContentParser;
import com.skytonia.SkyCore.util.TextUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.junit.Test;

public class JsonCharLengthTest
{

	@Test
	public void jsonLineSplitTest()
	{
		final int lineSplit = 60; //60 Chars?
		final String lineLimit = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22";

		//String text = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34";
		final String text = "&e&l(!) &e&lUNSCRAMBLE: &e{hov;Hover to see the word!;{scrambled-word}} &7Be the first to unscramble this word!";
		final int textLength = TextUtil.getLineLength(text);

		BUtil.log("Split Length = " + TextUtil.getLineLength(lineLimit));
		BUtil.log("Line Length = " + textLength);

		BUtil.log("--------------------");

		for(BaseComponent resultingLine : new JSONContentParser(text).parseLines())
		{
			BUtil.log(">" + resultingLine.toLegacyText());
		}
	}

}
