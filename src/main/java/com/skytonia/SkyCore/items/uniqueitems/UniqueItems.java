package com.skytonia.SkyCore.items.uniqueitems;

import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import com.skytonia.SkyCore.util.file.FlatFile;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
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
	
	private final ObjectOpenHashSet<String> usedItems = new ObjectOpenHashSet<>();
	
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
		getSave().set
		
		return lastId;
	}
	
	public String getIdFrom(Collection<String> collection)
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
	}
	
	public String getIdFrom(String line)
	{
		Matcher matcher = UNIQUE_ID_KEY_PATTERN.matcher(line);
		if(matcher.find())
		{
			int endId = matcher.end();
			//Min of 8 characters for this method
			if(endId + 8 >= line.length())
			{
				return null;
			}
			
			return line.substring(endId, endId + 8).replace("§", "");
		}
		
		return null;
	}
	
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
	
}