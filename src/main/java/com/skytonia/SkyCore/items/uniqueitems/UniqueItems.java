package com.skytonia.SkyCore.items.uniqueitems;

import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.file.FlatFile;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Chris Brown (OhBlihv) on 6/17/2017.
 */
public class UniqueItems extends FlatFile
{
	
	public static final String UNIQUE_ID_KEY = "§0§0";
	public static final Pattern UNIQUE_ID_KEY_PATTERN = Pattern.compile("§0§0");
	public static final char[] ID_CHARACTERS = new char[]
	{
	    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	    'a', 'b', 'c', 'd', 'e', 'f',
	    'k', 'l', 'm', 'n', 'o', 'r'
	    //TODO: Check if upper-case characters are converted
	    //'A', 'B', 'C', 'D', 'E', 'F',
		//'K', 'L', 'M', 'N', 'O', 'R'
	};
	
	private final HashSet<String> usedItems = new HashSet<>();
	
	private String lastId = "0000";
	
	public UniqueItems()
	{
		this("unique-items.yml");
	}
	
	public UniqueItems(String fileName)
	{
		super(fileName, BUtil.getCallingPlugin());
		
		List<String> usedKeys = getStringList("keys");
		if(usedKeys != null && !getSave().contains("last-id"))
		{
			usedItems.addAll(usedKeys);
			
			String tempLastId = lastId;
			for(String itemId : usedItems)
			{
				if(compare(itemId, tempLastId) > 0)
				{
					tempLastId = itemId;
				}
			}
			
			lastId = tempLastId;
		}
		else if(getSave().contains("last-id"))
		{
			lastId = getSave().getString("last-id");
		}
	}
	
	/*
	 * Identifier Methods
	 */
	
	public void addUsedId(String id)
	{
		usedItems.add(id);
	}
	
	public boolean isUsedId(String id)
	{
		return usedItems.contains(id);
	}
	
	public String getNextId()
	{
		char[] newId = lastId.toCharArray();
		for(int i = newId.length - 1;i >= 0;i--)
		{
			int currentCharId = getCharacterId(newId[i]);
			if(currentCharId + 1 == ID_CHARACTERS.length)
			{
				newId[i] = ID_CHARACTERS[0];
			}
			else
			{
				newId[i] = ID_CHARACTERS[currentCharId + 1];
				break;
			}
		}
		
		lastId = String.valueOf(newId);
		
		StringBuilder formattedId = new StringBuilder();
		for(char idChar : lastId.toCharArray())
		{
			formattedId.append("§").append(idChar);
		}
		
		return UNIQUE_ID_KEY + formattedId.toString();
	}
	
	/*public String getIdFrom(Collection<String> collection)
	{
		String idKey;
		for(String line : collection)
		{
			if((idKey = getIdFrom(line)) != null)
			{
				return idKey;
			}
		}
		
		return null;
	}*/
	
	/*public String getIdFrom(String line)
	{
		Matcher matcher = UNIQUE_ID_KEY_PATTERN.matcher(line);
		if(matcher.find())
		{
			int endId = matcher.end();
			//Min of 8 characters for this method
			if(endId + 8 > line.length())
			{
				return null;
			}
			
			return line.substring(endId, endId + 8).replace("§", "");
		}
		
		return null;
	}*/
	
	/*
	 * File Management
	 */
	
	private void updateLastKey()
	{
		//TODO:
	}
	
	private void updateUsedList()
	{
		//TODO:
	}
	
	/*
	 * Internal Methods
	 */
	
	/**
	 *
	 * @param itemId1
	 * @param itemId2
	 * @return  1 if id1 > id2
	 *          0 if id1 = id2
	 *         -1 if id1 < id2
	 */
	private int compare(String itemId1, String itemId2)
	{
		for(int i = 0;i < itemId1.length();i++)
		{
			int id1 = getCharacterId(itemId1.charAt(i)),
				id2 = getCharacterId(itemId2.charAt(i));
			
			if(i == (itemId1.length() - 1))
			{
				if(id1 == id2)
				{
					return 0;
				}
			}
			
			if(id1 > id2)
			{
				return 1;
			}
		}
		
		return -1;
	}
	
	private int getCharacterId(char idChar)
	{
		int i = 0;
		for(char charEntry : ID_CHARACTERS)
		{
			if(idChar == charEntry)
			{
				return i;
			}
			
			i++;
		}
		
		return -1;
	}
	
	/*
	 * Public/Static API
	 */
	
	public static String getIdFor(int value)
	{
		if(value < 0)
		{
			throw new IllegalArgumentException("ID Value must be positive");
		}
		
		String idString = "";
		
		final int idCharLength = ID_CHARACTERS.length;
		
		int idElement;
		while(true)
		{
			idElement = value % idCharLength;
			
			value /= idCharLength;
			
			idString = "§" + ID_CHARACTERS[idElement] + idString;
			
			if(value <= 0)
			{
				break;
			}
		}
		
		return UNIQUE_ID_KEY + idString; //TODO: Add ID
	}
	
	public static int getValueFrom(Iterable<String> collection)
	{
		int value = 0;
		for(String line : collection)
		{
			if((value = getValueFrom(line)) != -1)
			{
				return value;
			}
		}
		
		return -1;
	}
	
	public static int getValueFrom(String idString)
	{
		int result = 0;
		
		final int idCharLength = ID_CHARACTERS.length;
		
		for(char idChar : idString.toCharArray())
		{
			for(int i = 0;i < idCharLength;i++)
			{
				if(ID_CHARACTERS[i] == idChar)
				{
					result = (result * idCharLength) + i;
					break;
				}
			}
		}
		
		return result;
	}
	
	public static int getIdFrom(String line)
	{
		StringBuilder resultingIdString = new StringBuilder();
		
		int validFlagIds = 0;
		for(char idChar : line.toCharArray())
		{
			//Parse rest of ID
			if(validFlagIds == 4)
			{
				if(idChar == '§')
				{
					continue;
				}
				else if(!isValidIDRange(idChar))
				{
					break;
				}
				
				resultingIdString.append(idChar);
			}
			else if( validFlagIds % 2 == 0 && idChar == '§' ||
				     idChar == '0')
			{
				validFlagIds++;
			}
		}
		
		if(resultingIdString.length() == 0)
		{
			return -1;
		}
		else
		{
			return getValueFrom(resultingIdString.toString());
		}
	}
	
	public static boolean isValidIDRange(char idChar)
	{
		return (idChar >= '0' && idChar <= '9') ||
			   (idChar >= 'a' && idChar <= 'f') ||
			   (idChar >= 'k' && idChar <= 'r');
	}
	
}
