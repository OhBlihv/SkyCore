package com.skytonia.SkyCore.titles;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public enum DirtyPlayerType
{
	
	ADD,                //Resends all titles
	REMOVE,             //Removes all titles
	UPDATE,             //Updates the content of the titles
	CLEAN,              //No updates required

	//Per-Player Markers
	INDIV_ADD_QUEUE,    //Queued to resend titles
	INDIV_REMOVE_QUEUE, //Queued to remove all tags. Updated to INDIV_HIDDEN next update()
	INDIV_HIDDEN,       //Tags despawned/Not in range for updates

	//TODO:
	/*
	 * Per-line dirty types are required in future
	 * If a line has it's content updated, and another line is added;
	 * the content update will be ignored, and the new line will be added -
	 * creating a mismatch between client and server - and more issues.
	 */

	;

	public boolean isNotVisible()
	{
		return this == INDIV_REMOVE_QUEUE || this == INDIV_HIDDEN;
	}

	public boolean isVisible()
	{
		return this == ADD || this == UPDATE || this == CLEAN || this == INDIV_ADD_QUEUE || this == INDIV_REMOVE_QUEUE;
	}

	public boolean isIndividual()
	{
		return this == INDIV_ADD_QUEUE || this == INDIV_REMOVE_QUEUE || this == INDIV_HIDDEN;
	}
	
}
