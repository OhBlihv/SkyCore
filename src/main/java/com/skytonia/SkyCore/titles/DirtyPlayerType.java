package com.skytonia.SkyCore.titles;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public enum DirtyPlayerType
{
	
	ADD,          //Resends all titles
	REMOVE,       //Removes all titles
	UPDATE,       //Updates the content of the titles
	CLEAN,        //No updates required
	NOT_VISIBLE,  //Tags despawned/Not in range for updates

	//TODO:
	/*
	 * Per-line dirty types are required in future
	 * If a line has it's content updated, and another line is added;
	 * the content update will be ignored, and the new line will be added -
	 * creating a mismatch between client and server - and more issues.
	 */

	;
	
}
