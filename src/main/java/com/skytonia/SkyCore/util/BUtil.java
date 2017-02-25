package com.skytonia.SkyCore.util;

import com.skytonia.SkyCore.firework.FireworkType;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BUtil
{
	
	//Get either SkyCore OR the hosting plugin
	private static Plugin pluginInstance = null;

	private static boolean useConsoleColours = false;
	private static final Random random = new Random();

	// ------------------------------------------------------------------------------------------------------
	// Miscellaneous
	// ------------------------------------------------------------------------------------------------------

	public static void createFirework(Location location, boolean instantExplosion, FireworkType fireworkType)
	{
		if(fireworkType == null)
		{
			fireworkType = FireworkType.CRATE_SPAWN;
		}

		FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean())
				.withColor(fireworkType.getColour1(), fireworkType.getColour2()).withFade(fireworkType.getColour3())
				.with(fireworkType.getType()).trail(random.nextBoolean()).build();

		if(instantExplosion)
		{
			location.setY(location.getY() + 2.0D);
			//CustomEntityFirework_1_8_R3.spawn(location, effect);
		}
		else
		{
			Firework firework = location.getWorld().spawn(location, Firework.class);
			FireworkMeta meta = firework.getFireworkMeta();
			meta.addEffect(effect);
			firework.setFireworkMeta(meta);
			firework.setVelocity(new Vector(0.00, 0.05, 0.00));
		}
	}
	
	public static int getPosNegIntFromRange(int range)
	{
		return random.nextBoolean() ? 0 - random.nextInt(range) : random.nextInt(range);
	}
	
	public static double getPosNegDoubleFromRange(double range)
	{
		return random.nextBoolean() ? 0 - (random.nextDouble() * range) : (random.nextDouble() * range);
	}
	
	public static String getNMSVersion()
	{
		final String packageName = Bukkit.getServer().getClass().getPackage().getName();
		
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

	public static Material parseMaterial(String materialString)
	{
		Material toReturn = Material.getMaterial(materialString);
		if(toReturn != null)
		{
			return toReturn;
		}

		BUtil.logError("Invalid material: " + materialString);
		return Material.POTATO_ITEM;
	}

	public static String getLargestUnitAgo(long comparedMillis)
	{
		return compareTimes(-1, comparedMillis);
	}
	
	public static String compareTimes(long timeStamp, long comparedTimeStamp)
	{
		LocalDateTime   initialTime,
						comparedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(comparedTimeStamp), ZoneId.systemDefault()),
						runningTime = LocalDateTime.from(comparedTime);
		
		if(timeStamp < 0)
		{
			initialTime = LocalDateTime.now(Clock.tickSeconds(ZoneId.systemDefault()));
		}
		else
		{
			initialTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(comparedTimeStamp), ZoneId.systemDefault());
		}

		long    days, hours, minutes, seconds,
				largestValue = 0;
		ChronoUnit largestUnit = null; //Default to the lowest unit

		//Calculate the unit, then add it on to total time
		days = runningTime.until(initialTime, ChronoUnit.DAYS);
		if(days != 0)
		{
			largestUnit = ChronoUnit.DAYS; //No Checking required
			largestValue = days;
		}
		runningTime = runningTime.plusDays(days);

		if(largestUnit == null)
		{
			hours = runningTime.until(initialTime, ChronoUnit.HOURS);
			if(hours != 0)
			{
				largestUnit = ChronoUnit.HOURS;
				largestValue = hours;
			}
			runningTime = runningTime.plusHours(hours);
		}

		if(largestUnit == null)
		{
			minutes = runningTime.until(initialTime, ChronoUnit.MINUTES);
			if(minutes != 0)
			{
				largestUnit = ChronoUnit.MINUTES;
				largestValue = minutes;
			}
			runningTime = runningTime.plusMinutes(minutes);
		}

		if(largestUnit == null)
		{
			seconds = runningTime.until(initialTime, ChronoUnit.SECONDS);
			
			largestUnit = ChronoUnit.SECONDS;
			largestValue = seconds;
			//runningTime = runningTime.plusSeconds(seconds);
		}

		String unitString = BUtil.capitaliseFirst(largestUnit.name().toLowerCase());
		if(unitString.endsWith("s") && largestValue == 1)
		{
			//Remove the trailing 's' if not required
			unitString = unitString.substring(0, unitString.length() - 1);
		}

		return largestValue + " " + unitString;
	}

	public static String getTimeTilFuture(long futureTime)
	{
		long timeTilFuture = (futureTime - System.currentTimeMillis()) / 1000L;
		
		long hours = TimeUnit.SECONDS.toHours(timeTilFuture);
		long minutes = TimeUnit.SECONDS.toMinutes(timeTilFuture) - (TimeUnit.SECONDS.toHours(timeTilFuture) * 60);
		long seconds = TimeUnit.SECONDS.toSeconds(timeTilFuture) - (TimeUnit.SECONDS.toMinutes(timeTilFuture) * 60);
		
		StringBuilder timeVariable = new StringBuilder();
		
		if(hours > 0)
		{
			timeVariable.append(String.valueOf(hours)).append(" hour");
			if(hours > 1)
			{
				timeVariable.append('s');
			}
		}
		
		if(minutes > 0)
		{
			if(hours > 0)
			{
				timeVariable.append(" and ");
			}
			timeVariable.append(String.valueOf(minutes)).append(" minute");
			if(minutes > 1)
			{
				timeVariable.append('s');
			}
		}
		
		if(seconds > 0 && minutes < 1 && hours == 0)
		{
			if(minutes > 0)
			{
				timeVariable.append(" and ");
			}
			timeVariable.append(String.valueOf(seconds)).append(" second");
			if(seconds > 1)
			{
				timeVariable.append('s');
			}
		}
		
		return timeVariable.toString();
	}
	
	// ------------------------------------------------------------------------------------------------------
	// String Translation
	// ------------------------------------------------------------------------------------------------------

	private static final Map<Integer, Character> numeralMap = new TreeMap<>(
	(o1, o2) ->
	{
		int compare = 0 - Integer.compare(o1, o2);
		if(compare == 0)
		{
			compare = 1;
		}
		return compare;
	}
	);
	
	static
	{
		numeralMap.put(1000, 'M');
		numeralMap.put(500, 'D');
		numeralMap.put(100, 'C');
		numeralMap.put(50, 'L');
		numeralMap.put(10, 'X');
		numeralMap.put(5, 'V');
		numeralMap.put(1, 'I');
	}
	
	public static String toRomanNumerals(int number)
	{
		StringBuilder romanNumeralString = new StringBuilder();
		
		for(Map.Entry<Integer, Character> entry : numeralMap.entrySet())
		{
			while(number >= entry.getKey())
			{
				number -= entry.getKey();
				romanNumeralString.append(entry.getValue());
			}
		}
		
		return romanNumeralString.toString();
	}
	
	public static String capitaliseFirst(String string)
	{
		return Character.toTitleCase(string.charAt(0)) + string.substring(1, string.length());
	}
	
	public static String capitaliseAllFirst(String string)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		String[] split = string.split(" ");
		for(String splitWord : split)
		{
			stringBuilder.append(capitaliseFirst(splitWord)).append(" ");
		}
		
		return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
	}
	
	public static List<String> translateVariable(List<String> lines, String variable, String content)
	{
		if(lines == null) { return null; }

		if(!lines.isEmpty())
		{
			return lines.stream().map(line -> line.replace(variable, content)).collect(Collectors.toList());
		}
		return lines;
	}
	
	public static String stripColours(String toFix)
	{
		return Pattern.compile("[&|§](.)").matcher(toFix).replaceAll("");
	}

	public static String translateConsoleColours(String toFix)
	{
		if(!useConsoleColours)
		{
			return Pattern.compile("(?i)(&|Â§)([a-f0-9k-r])").matcher(toFix).replaceAll("");
		}
		toFix = Pattern.compile("(?i)(&|Â§)([a])").matcher(toFix).replaceAll("\u001B[32m\u001B[1m"); // Light Green
		toFix = Pattern.compile("(?i)(&|Â§)([b])").matcher(toFix).replaceAll("\u001B[36m"); // Aqua
		toFix = Pattern.compile("(?i)(&|Â§)([c])").matcher(toFix).replaceAll("\u001B[31m"); // Red
		toFix = Pattern.compile("(?i)(&|Â§)([d])").matcher(toFix).replaceAll("\u001B[35m\u001B[1m"); // Pink
		toFix = Pattern.compile("(?i)(&|Â§)([e])").matcher(toFix).replaceAll("\u001B[33m\u001B[1m"); // Yellow
		toFix = Pattern.compile("(?i)(&|Â§)([f])").matcher(toFix).replaceAll("\u001B[0m"); // White
		toFix = Pattern.compile("(?i)(&|Â§)([0])").matcher(toFix).replaceAll("\u001B[30m"); // Black
		toFix = Pattern.compile("(?i)(&|Â§)([1])").matcher(toFix).replaceAll("\u001B[34m"); // Dark Blue
		toFix = Pattern.compile("(?i)(&|Â§)([2])").matcher(toFix).replaceAll("\u001B[32m"); // Dark Green
		toFix = Pattern.compile("(?i)(&|Â§)([3])").matcher(toFix).replaceAll("\u001B[34m\u001B[1m"); // Light Blue
		toFix = Pattern.compile("(?i)(&|Â§)([4])").matcher(toFix).replaceAll("\u001B[31m"); // Dark Red
		toFix = Pattern.compile("(?i)(&|Â§)([5])").matcher(toFix).replaceAll("\u001B[35m"); // Purple
		toFix = Pattern.compile("(?i)(&|Â§)([6])").matcher(toFix).replaceAll("\u001B[33m"); // Gold
		toFix = Pattern.compile("(?i)(&|Â§)([7])").matcher(toFix).replaceAll("\u001B[37m"); // Light Grey
		toFix = Pattern.compile("(?i)(&|Â§)([8])").matcher(toFix).replaceAll("\u001B[30m\u001B[1m"); // Dark Grey
		toFix = Pattern.compile("(?i)(&|Â§)([9])").matcher(toFix).replaceAll("\u001B[34m"); // Dark Aqua
		toFix = Pattern.compile("(?i)(&|Â§)([r])").matcher(toFix).replaceAll("\u001B[0m");
		toFix += "\u001B[0m"; // Stop colour from overflowing to the next line with a reset code

		return toFix;
	}

	private static final Pattern colourPattern = Pattern.compile("(?i)&([0-9A-Fa-f-l-oL-OrR])");

	public static String translateColours(String toFix)
	{
		if(toFix == null || toFix.isEmpty())
		{
			return toFix;
		}
		
		// Convert every single colour code and formatting code, excluding
		// 'magic' (&k), capitals and lowercase are converted.
		return colourPattern.matcher(toFix).replaceAll("\u00A7$1");
	}

	public static List<String> translateColours(List<String> lines)
	{
		if (lines == null || lines.isEmpty())
		{
			return null;
		}

		//list.add(i, colourPattern.matcher(list.get(i)).replaceAll("\u00A7$1"));
		return lines.stream().map(line -> colourPattern.matcher(line).replaceAll("\u00A7$1")).collect(Collectors.toList());
	}

	public static List<String> convertPlaceholders(List<String> lines, String[] placeholders, String[] content)
	{
		if (placeholders.length != content.length || lines == null)
		{
			if(lines != null)
			{
				BUtil.logError("Placeholder length does not match content length! Returning plain lines:\n" + lines.toString());
			}
			return lines;
		}
		lines = new ArrayList<>(lines);

		for(int lineNum = 0;lineNum < lines.size();lineNum++)
		{
			String line = lines.get(lineNum);
			for(int index = 0;index < placeholders.length;index++)
			{
				line = line.replace(placeholders[index], content[index]);
			}
			lines.set(lineNum, line);
		}
		return lines;
	}

	public static String convertPlaceholders(String line, String[] placeholders, String[] content)
	{
		if (placeholders.length != content.length)
		{
			return null;
		}

		for (int index = 0; index < placeholders.length; index++)
		{
			line = line.replace(placeholders[index], content[index]);
		}
		return line;
	}

	/**
	 * Very primitive variable search algorithm.
	 * Will only generally work with a single variable and no doubly-used characters
	 * found in both the variable and the char after the variable
	 *
	 * @param string String to find replacement in
	 * @param original Original String without the variable replacement
	 * @param variable Variable to search for
	 * @return The variable's replacement if found, else null
	 */
	public static String getVariableFromString(String string, String original, String variable)
	{
		Matcher variableMatcher = Pattern.compile(variable).matcher(original);
		if(!variableMatcher.find())
		{
			return null;
		}

		int startIndex = variableMatcher.start(),
			endFoundIndex = -1;
		boolean charAfterIsEnd = false;
		char charAfterVariable;
		if(variableMatcher.end() + 1 >= original.length())
		{
			charAfterVariable = '^';
			charAfterIsEnd = true;
		}
		else
		{
			charAfterVariable = original.charAt(variableMatcher.end() + 1);
		}

		if(charAfterIsEnd)
		{
			return string.substring(startIndex, string.length());
		}
		else
		{
			for(int charIdx = startIndex;charIdx < original.length();charIdx++)
			{
				//We've 'found the end'
				if(charIdx == string.length())
				{
					endFoundIndex = charIdx;
					break;
				}
				
				if(string.charAt(charIdx) != charAfterVariable)
				{
					continue;
				}
				
				endFoundIndex = charIdx;
				break;
			}
		}
		
		if(endFoundIndex != -1)
		{
			return string.substring(startIndex, endFoundIndex);
		}
		else
		{
			return null;
		}
	}

	// ------------------------------------------------------------------------------------------------------
	// Printing
	// ------------------------------------------------------------------------------------------------------

	public static void printSelf(String message)
	{
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer("OhBlihv");
		if(offlinePlayer != null && offlinePlayer.isOnline())
		{
			offlinePlayer.getPlayer().sendMessage(translateColours(message));
		}
	}

	public static void printToOfflinePlayer(String playerName, String message)
	{
		if(playerName == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(playerName), message);
	}

	public static void printToOfflinePlayer(UUID uuid, String message)
	{
		if(uuid == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(uuid), message);
	}

	private static void printToOfflinePlayer(OfflinePlayer offlinePlayer, String message)
	{
		if(offlinePlayer != null && offlinePlayer.isOnline())
		{
			offlinePlayer.getPlayer().sendMessage(message);
		}
	}

	public static void printToOfflinePlayer(String playerName, List<String> message)
	{
		if(playerName == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(playerName), message);
	}

	public static void printToOfflinePlayer(UUID uuid, List<String> message)
	{
		if(uuid == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(uuid), message);
	}

	private static void printToOfflinePlayer(OfflinePlayer offlinePlayer, List<String> message)
	{
		if(offlinePlayer != null && offlinePlayer.isOnline())
		{
			Player player = offlinePlayer.getPlayer();
			for(String line : message)
			{
				player.sendMessage(line);
			}
		}
	}
	
	private static String getPluginPrefix()
	{
		return "[" + getCallingPlugin() + "]";
	}
	
	// ------------------------------------------------------------------------------------------------------
	// Plugin Retrieval
	// ------------------------------------------------------------------------------------------------------
	
	private String skyCoreName = null;
	
	/**
	 * Assuming a structure of 'com.skytonia.<plugin>.package.class' and so-forth,
	 * this method will return the <plugin> portion of the fully-qualified class name.
	 *
	 * - This method
	 * - Calling BUtil Method
	 * - Calling Plugin Method <-- Bingo.
	 *
	 * @return The full name of the calling plugin
	 */
	public static String getCallingPlugin()
	{
		String pluginName = null;
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for(int i = 2;i < 10;i++) //Parse 8 lines for a plugin name before giving up.
		{
			if(i >= stackTrace.length)
			{
				break;
			}
			
			String thirdPackage = stackTrace[i].getClassName();
			//Package Blacklist
			if( !thirdPackage.startsWith("org.bukkit") &&       //Bukkit/CraftBukkit
				!thirdPackage.startsWith("org.spigot") &&       //Spigot
				!thirdPackage.startsWith("com.destroystokyo") &&//PaperSpigot
				!thirdPackage.startsWith("co.aikar") &&        //Timings
				!thirdPackage.startsWith("java") &&             //Java API
				!thirdPackage.startsWith("sun")) //&&             //Internal Oracle/Sun Libraries
			    //!thirdPackage.contains(".shade.")) //Ignore shaded libraries. They do not count towards a plugin.
			{
				pluginName = thirdPackage.split("[.]")[2];
				
				//We may have picked up a helper method. Continue searching if it matches SkyCore
				//If not, we've found our calling plugin!
				if(!pluginName.equals("SkyCore"))
				{
					break;
				}
			}
		}
		
		//Default to claiming responsibility
		if(pluginName == null)
		{
			pluginName = "SkyCore";
		}
		
		return pluginName;
	}
	
	public static JavaPlugin getCallingJavaPlugin(int depth)
	{
		return getCallingJavaPlugin();
	}
	
	public static JavaPlugin getCallingJavaPlugin()
	{
		String mainClassName = getCallingPlugin();
		
		try
		{
			Plugin javaPlugin = Bukkit.getPluginManager().getPlugin(mainClassName);
			if(javaPlugin instanceof JavaPlugin)
			{
				return (JavaPlugin) javaPlugin;
			}
			else
			{
				throw new IllegalArgumentException("Plugin '" + mainClassName + "' was not found or not currently loaded.");
			}
		}
		catch(Exception e)
		{
			logMessage("Package Structure seems to be invalid for the calling plugin: '" + mainClassName + "'. Please notify the author to correct this issue.");
			logStackTrace(e);
		}
		
		return null;
	}

	// ------------------------------------------------------------------------------------------------------
	// Broadcasting
	// ------------------------------------------------------------------------------------------------------

	public void broadcastPlain(String message)
	{
		Bukkit.broadcastMessage(message);
	}

	// ------------------------------------------------------------------------------------------------------
	// Logging
	// ------------------------------------------------------------------------------------------------------

	public static void logInfo(String message)
	{
		logMessage("INFO: " + message);
	}

	public static void logError(String message)
	{
		logMessage("ERROR: " + message);
	}
	
	public static void logMessage(String message)
	{
		logMessageAsPlugin(getCallingPlugin(), message);
	}
	
	public static void logMessageAsPlugin(String plugin, String message)
	{
		String pluginPrefix;
		if(plugin == null || plugin.isEmpty())
		{
			pluginPrefix = "";
		}
		else
		{
			pluginPrefix = "[" + plugin + "] ";
		}
		
		System.out.println(pluginPrefix + message);
	}
	
	public static void logStackTrace(Throwable e)
	{
		logMessageAsPlugin(null, "Caught Exception (" + e.getClass().getSimpleName() + ")" + (e.getMessage() != null ? (": " + e.getMessage()) : ""));
		
		logStackTrace(e.getStackTrace());
	}
	
	public static void logStackTrace(StackTraceElement[] stackTrace)
	{
		int element = 0;
		for(StackTraceElement stackTraceElement : stackTrace)
		{
			element++;
			
			logMessageAsPlugin(null, "at " + stackTraceElement.toString());
			
			//Only log one line from the bukkit api to determine where the call originated.
			//Anything past that is irrelevant for common use.
			//BUT - Allow at least 5 lines. Since the error may occur within bukkit itself.
			if(stackTraceElement.getClassName().startsWith("org.bukkit") && element > 5)
			{
				return;
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------
	// UUID Encoding
	// ------------------------------------------------------------------------------------------------------

	public static String compressUUID(UUID uuid)
	{
		return Base64.getEncoder().encodeToString(toBytes(uuid)).split("=")[0];
	}

	public static UUID deCompressUUID(String uuid)
	{
		return fromBytes(Base64.getDecoder().decode(uuid.split(":")[0].concat("==")));
	}

	public static byte[] toBytes(UUID uuid)
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		return byteBuffer.array();
	}

	public static UUID fromBytes(byte[] array)
	{
		if (array.length != 16)
		{
			throw new IllegalArgumentException("Illegal byte array length: " + array.length);
		}

		ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		long mostSignificant = byteBuffer.getLong();
		long leastSignificant = byteBuffer.getLong();

		return new UUID(mostSignificant, leastSignificant);
	}

}
