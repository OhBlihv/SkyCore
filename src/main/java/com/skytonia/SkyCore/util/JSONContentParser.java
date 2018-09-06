package com.skytonia.SkyCore.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONContentParser extends VariableReplacer
{

	private static final Pattern
		HOVER_PATTERN = Pattern.compile("(\\{hov(;)).*(;).*(})"),
		FORMAT_PATTERN = Pattern.compile("([&§][a-f0-9k-r])");

	public JSONContentParser(String content)
	{
		super(content);
	}

	public JSONContentParser(List<String> content)
	{
		super(content);
	}

	public JSONContentParser(BaseComponent[] content)
	{
		super(toStringList(content));
	}

	private static List<String> toStringList(BaseComponent[] components)
	{
		List<String> content = new ArrayList<>();

		Iterator<BaseComponent> contentItr = Arrays.asList(components).iterator();

		String lastContent = "";
		while(contentItr.hasNext())
		{
			BaseComponent component = contentItr.next();

			//Substring at 2 to avoid the prefixed §f
			String legacyContent = component.toLegacyText()/*.substring(2)*/;

			if(component.getHoverEvent() != null)
			{
				lastContent += "{hov;" + legacyContent + ";" + component.getHoverEvent().getValue()[0].toLegacyText().substring(2) + "}";
			}
			else
			{
				lastContent += legacyContent;
			}

			if(lastContent.endsWith("\n"))
			{
				content.add(lastContent.substring(0, lastContent.length() - 1));

				lastContent = "";
			}
		}

		if(!lastContent.isEmpty() && lastContent.charAt(0) != '\n')
		{
			//Remove any trailing newlines
			content.add(lastContent.replace("\n", ""));
		}

		//BUtil.log(content.toString());

		return content;
	}

	//

	@Override
	public JSONContentParser replace(String variable, String replacementContent)
	{
		return (JSONContentParser) super.replace(variable, replacementContent);
	}

	private enum FormatType
	{
		COLOUR,
		BOLD,
		ITALICS,
		UNDERLINE,
		STRIKETHROUGH;

		public void applyType(BaseComponent component, String value)
		{
			switch(this)
			{
				case COLOUR:
				{
					//Default to §f
					ChatColor chatColor = ChatColor.WHITE;
					try
					{
						chatColor = ChatColor.getByChar(value.charAt(1));
					}
					catch(Exception e)
					{
						//Not value colour
					}

					component.setColor(chatColor);
					break;
				}
				case BOLD: component.setBold(value != null); break;
				case ITALICS: component.setItalic(value != null); break;
				case UNDERLINE: component.setUnderlined(value != null); break;
				case STRIKETHROUGH: component.setStrikethrough(value != null); break;
			}
		}
	}

	final Map<FormatType, String> formatMap = new EnumMap<>(FormatType.class);

	private void handleFormat(String formatCode)
	{
		char formatChar = formatCode.charAt(1);
		if((formatChar >= 'a' && formatChar <= 'f') ||
			(formatChar >= '0' && formatChar <= '9'))
		{
			setFormat(FormatType.COLOUR, formatCode);
		}
		else
		{
			switch(formatChar)
			{
				//No support for 'Obfuscated'
				case 'l': setFormat(FormatType.BOLD); break;
				case 'm': setFormat(FormatType.STRIKETHROUGH); break;
				case 'n': setFormat(FormatType.UNDERLINE); break;
				case 'o': setFormat(FormatType.ITALICS); break;
				//Use the reset character to reset the colour to white and clear other formatting codes
				case 'r': setFormat(FormatType.COLOUR, "§f"); break;
			}
		}
	}

	private void setFormat(FormatType formatType)
	{
		if(formatType == FormatType.COLOUR)
		{
			throw new IllegalArgumentException("Required to provide colour code alongside format type COLOUR");
		}

		setFormat(formatType, "true");
	}

	private void setFormat(FormatType formatType, String value)
	{
		switch(formatType)
		{
			//Setting a colour removes all other formatting options until they're enabled again
			case COLOUR:
			{
				formatMap.clear();
			}
			default:
			{
				formatMap.put(formatType, value);
			}
		}
	}

	public BaseComponent[] parseLines()
	{
		formatMap.put(FormatType.COLOUR, "§f");

		Deque<BaseComponent> components = new ArrayDeque<>();
		for(Iterator<String> contentItr = content.iterator();contentItr.hasNext();)
		{
			String line = contentItr.next();

			parseLine(components, line, contentItr.hasNext());
		}

		return components.toArray(new BaseComponent[]{});
	}

	private void parseLine(Deque<BaseComponent> components, String line, boolean hasNext)
	{
		//BUtil.log("Parsing Line '" + line + "'");

		Matcher formatMatcher = FORMAT_PATTERN.matcher(line);
		int lineIdx = 0;

		boolean skipNextColourCheck = false;
		boolean usePrevFormat = false;
		//Loop through all colours
		while(lineIdx < line.length())
		{
			while((skipNextColourCheck || formatMatcher.find()) && !(usePrevFormat = !(formatMatcher.start() <= lineIdx)))
			{
				//BUtil.log("Found format '" + formatMatcher.group() + "' at " + formatMatcher.start() + " to " + formatMatcher.end() + " (Min:" + lineIdx + ")");

				handleFormat(formatMatcher.group());

				lineIdx = formatMatcher.end();

				skipNextColourCheck = false;
			}

			//Run this segment up until either the next colour code, or the end of the line
			int segmentEnd = line.length();
			//Ensure we use the previously found (but out of range) format
			if(usePrevFormat || formatMatcher.find())
			{
				segmentEnd = formatMatcher.start();

				//Ensure we don't skip a colour code by finding the next code down here
				skipNextColourCheck = true;
				usePrevFormat = false;

				//BUtil.log("Stopping before next colour code '" + formatMatcher.group() + "' at idx=" + formatMatcher.start());
			}

			//BUtil.log("Line -> (" + lineIdx + "->" + segmentEnd + ") '" + line.substring(lineIdx, segmentEnd) + "'");

			components.addAll(updateEvents(line.substring(lineIdx, segmentEnd)));

			lineIdx = segmentEnd;
		}

		//Ensure each line is separated properly
		if(hasNext)
		{
			components.add(new TextComponent("\n"));
		}
	}

	private Deque<BaseComponent> updateEvents(String line)
	{
		Deque<BaseComponent> components = new ArrayDeque<>();

		//TODO: Support more modifiers
		Matcher matcher = HOVER_PATTERN.matcher(line);
		if(matcher.find())
		{
			if(matcher.start() > 0)
			{
				components.add(updateComponent(new TextComponent(line.substring(0, matcher.start(1)))));
			}

			//Use the first entry in the component array, as the hover component
			//only takes up one component max.
			String lineContent = line.substring(matcher.end(2), matcher.start(3));
			//Add any trailing content after the variable to the line content
			if(matcher.end(4) < line.length())
			{
				lineContent += line.substring(matcher.end(4));
			}

			BaseComponent hoverComponent = new ComponentBuilder(lineContent)
				.event(new HoverEvent(
					HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(line.substring(matcher.end(3), matcher.start(4))).create()
				)).create()[0];

			components.add(updateComponent(hoverComponent));

			if(matcher.end() + 1 < line.length())
			{
				components.add(updateComponent(new TextComponent(line.substring(matcher.end() + 1))));
			}
		}
		else
		{
			components.add(updateComponent(new TextComponent(line)));
		}

		return components;
	}

	private BaseComponent updateComponent(BaseComponent component)
	{
		for(Map.Entry<FormatType, String> entry : formatMap.entrySet())
		{
			entry.getKey().applyType(component, entry.getValue());
		}

		return component;
	}

}
