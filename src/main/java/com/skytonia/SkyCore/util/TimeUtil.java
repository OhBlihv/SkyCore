package com.skytonia.SkyCore.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Chris Brown (OhBlihv) on 6/11/2017.
 */
public class TimeUtil
{
	
	public static Map<ChronoUnit, Long> getTimeComparison(long timeOneMillis, long timeTwoMillis, ChronoUnit... usedTimeArr)
	{
		Set<ChronoUnit> usedTimes = new TreeSet<>((o1, o2) -> 0 - o1.compareTo(o2));
		
		if(usedTimeArr.length == 0)
		{
			usedTimes.add(ChronoUnit.YEARS);
			usedTimes.add(ChronoUnit.MONTHS);
			usedTimes.add(ChronoUnit.WEEKS);
			usedTimes.add(ChronoUnit.DAYS);
			usedTimes.add(ChronoUnit.HOURS);
			usedTimes.add(ChronoUnit.MINUTES);
			usedTimes.add(ChronoUnit.SECONDS);
		}
		else
		{
			usedTimes.addAll(Arrays.asList(usedTimeArr));
		}
		
		Map<ChronoUnit, Long> timeCounts = new HashMap<>();
		
		LocalDateTime   runningTime,
						comparedTime;
		
		if(timeOneMillis == timeTwoMillis)
		{
			for(ChronoUnit chronoUnit : usedTimes)
			{
				timeCounts.put(chronoUnit, 0L);
			}
			return timeCounts;
		}
		
		if(timeOneMillis <= timeTwoMillis)
		{
			runningTime    = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeOneMillis), ZoneId.systemDefault());
			comparedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeTwoMillis), ZoneId.systemDefault());
		}
		else
		{
			runningTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeTwoMillis), ZoneId.systemDefault());
			comparedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeOneMillis), ZoneId.systemDefault());
		}
		
		for(ChronoUnit chronoUnit : usedTimes)
		{
			long units = runningTime.until(comparedTime, chronoUnit);
			runningTime = runningTime.plus(units, chronoUnit);
			
			timeCounts.put(chronoUnit, units);
		}
		
		return timeCounts;
	}
	
	public static String getNeatComparison(long timeOneMillis, long timeTwoMillis, ChronoUnit... displayedUnitsArr)
	{
		//Use default displayed units
		Map<ChronoUnit, Long> timeCounts = getTimeComparison(timeOneMillis, timeTwoMillis);
		
		return getNeatComparison(timeCounts, displayedUnitsArr);
	}
	
	public static String getNeatComparison(Map<ChronoUnit, Long> timeCounts, ChronoUnit... displayedUnitsArr)
	{
		if(displayedUnitsArr.length == 0)
		{
			throw new IllegalArgumentException("No units provided in getNeatComparison!");
		}
		
		Set<ChronoUnit> usedUnits = new TreeSet<>((o1, o2) -> 0 - o1.compareTo(o2));
		usedUnits.addAll(Arrays.asList(displayedUnitsArr));
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(ChronoUnit usedUnit : usedUnits)
		{
			long timeValue = timeCounts.get(usedUnit);
			if(timeValue > 0)
			{
				stringBuilder.append(String.valueOf(timeValue)).append(" ");
				
				String timeUnit = BUtil.capitaliseAllFirst(usedUnit.name().toLowerCase());
				if(timeValue == 1)
				{
					timeUnit = timeUnit.substring(0, timeUnit.length() - 1);
				}
				
				stringBuilder.append(timeUnit);
				
				stringBuilder.append(" ");
			}
		}
		
		String formattedString = stringBuilder.toString();
		if(formattedString.endsWith(" "))
		{
			formattedString = formattedString.substring(0, formattedString.length() - 1);
		}
		
		return formattedString;
	}
	
}
