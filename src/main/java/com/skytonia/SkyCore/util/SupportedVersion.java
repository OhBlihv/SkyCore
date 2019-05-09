package com.skytonia.SkyCore.util;

import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 4/28/2017.
 */
public enum SupportedVersion
{
	
	ONE_SEVEN(7),
	ONE_EIGHT(8),
	ONE_NINE(9),
	ONE_TEN(10),
	ONE_ELEVEN(11),
	ONE_TWELVE(12),
	ONE_THIRTEEN(13),
	ONE_FOURTEEN(14),

	;
	
	@Getter
	final int versionNum;
	
	SupportedVersion(int versionNum)
	{
		this.versionNum = versionNum;
	}
	
	public boolean isAtLeast(SupportedVersion supportedVersion)
	{
		return this.versionNum >= supportedVersion.versionNum;
	}
	
	public boolean isExact(SupportedVersion supportedVersion)
	{
		return this.versionNum == supportedVersion.versionNum;
	}
	
	public static SupportedVersion getVersionForNumber(int versionNum)
	{
		for(SupportedVersion supportedVersion : values())
		{
			if(supportedVersion.versionNum == versionNum)
			{
				return supportedVersion;
			}
		}
		
		throw new IllegalArgumentException("Unsupported Version: '" + versionNum + "'.");
	}
	
}
