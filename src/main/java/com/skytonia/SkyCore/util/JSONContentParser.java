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

	private static final Pattern PATTERN_JSON_EVENT_CONTENT = Pattern.compile("\\{.*;(.*);.*}"),
								 PATTERN_COLOUR = Pattern.compile("[&|§][a-r0-9]"); //Ignore bold for now
	private static final String VARIABLE_JSON_CONTENT = "{JSON}";
	private static final int MAX_LINE_LENGTH = 200; //140 or 240?

	public BaseComponent[] parseLines()
	{
		formatMap.put(FormatType.COLOUR, "§f");

		Deque<BaseComponent> components = new ArrayDeque<>();
		for(Iterator<String> contentItr = content.iterator();contentItr.hasNext();)
		{
			final String originalLine = contentItr.next();
			/*Deque<StringBuilder> visibleLines = new ArrayDeque<>();
			Deque<StringBuilder> contentLines = new ArrayDeque<>();

			visibleLines.addLast(new StringBuilder()); //Always edit the last line content
			contentLines.addLast(new StringBuilder()); //Always edit the last line content
			int bracketLevels = 0;
			StringBuilder bracketContent = new StringBuilder();

			int lineLength = 0;

			for(String word : originalLine.split("((?<= )|(?= ))"))
			{
				final int openBracketCount = StringUtils.countMatches(word, "{"),
					      closeBracketCount = StringUtils.countMatches(word, "}");

				if(openBracketCount > 0 || closeBracketCount > 0)
				{
					//Add a marker where this content would go in the line
					if(bracketLevels == 0 && openBracketCount > 0)
					{
						visibleLines.getLast().append("{JSON}");
						contentLines.getLast().append("{JSON}");
					}

					bracketLevels += openBracketCount;
					bracketContent.append(word);
					bracketLevels -= closeBracketCount;

					if(bracketLevels <= 0)
					{
						//Prevent negative bracket counts
						bracketLevels = 0;

						BUtil.log("Bracket content '" + bracketContent.toString() + "'");

						//Attempt to measure/input the visible content of the line
						final String jsonActualContent = bracketContent.toString();
						String jsonVisibleContent = jsonActualContent;
						Matcher jsonVisibleContentMatcher = PATTERN_JSON_EVENT_CONTENT.matcher(bracketContent.toString());
						if(jsonVisibleContentMatcher.find())
						{
							jsonVisibleContent = jsonVisibleContentMatcher.group(1);
						}

						final int jsonIndex = visibleLines.getLast().indexOf(VARIABLE_JSON_CONTENT);
						final int wordLength = TextUtil.getLineLength(PATTERN_COLOUR.matcher(jsonVisibleContent).replaceAll(""));
						//Not enough space to replace. Remove our variable and start a new line
						if(lineLength + wordLength > MAX_LINE_LENGTH)
						{
							//Remove the variable
							visibleLines.getLast().replace(jsonIndex, jsonIndex + VARIABLE_JSON_CONTENT.length(), "");
							//Line is too long, appendWord will create a new line correctly.
							appendWord(jsonVisibleContent, lineLength, visibleLines);
							//Add the entire JSON content to the actual line
							appendWord(bracketContent.toString(), Integer.MAX_VALUE, contentLines);
						}
						//There's enough space for this replacement to occur
						else
						{
							visibleLines.getLast().replace(jsonIndex, jsonIndex + VARIABLE_JSON_CONTENT.length(), jsonVisibleContent);
							contentLines.getLast().replace(jsonIndex, jsonIndex + VARIABLE_JSON_CONTENT.length(), jsonActualContent);
						}

						bracketContent = new StringBuilder();
					}
				}
				else
				{
					if(bracketLevels > 0)
					{
						bracketContent.append(word);
					}
					else
					{
						//Add to both the visible lines (to count length)
						//and the actual content lines to be passed to the JSON parser
						lineLength = appendWord(word, lineLength, visibleLines);
						appendWord(word, Integer.MAX_VALUE, contentLines);
					}
				}
			}

			BUtil.log("Line: '" + visibleLines.getLast() + "");

			if(bracketLevels != 0)
			{
				BUtil.log("Line has a non equal amount of bracket pairs! -> " + originalLine);
			}

			for(StringBuilder lineBuilder : contentLines)
			{
				parseLine(components, lineBuilder.toString(), contentItr.hasNext());
			}*/

			parseLine(components, originalLine, contentItr.hasNext());
		}

		return components.toArray(new BaseComponent[]{});
	}

	private int appendWord(String word, int lineLength, Deque<StringBuilder> splitLines)
	{
		final StringBuilder lineContent = splitLines.getLast();

		//Strip colour codes when checking length
		final int wordLength = TextUtil.getLineLength(PATTERN_COLOUR.matcher(word).replaceAll(""));
		if(lineLength + wordLength > MAX_LINE_LENGTH)
		{
			BUtil.log("Line longer than " + MAX_LINE_LENGTH + " (" + lineLength + wordLength + ") -> '" + lineContent + "'");
			BUtil.log("Splitting...");

			BUtil.log("Line: '" + lineContent + "");

			splitLines.addLast(new StringBuilder());
			lineLength = 0;
		}

		lineContent.append(word);
		return lineLength + wordLength;
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
