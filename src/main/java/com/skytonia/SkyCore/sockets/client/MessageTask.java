package com.skytonia.SkyCore.sockets.client;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by Chris Brown (OhBlihv) on 2/20/2017.
 */
public class MessageTask
{
	
	private long executionTime = -1;
	
	@Getter
	private final StackTraceElement[] callTrace;
	
	@Getter
	private final String data;
	
	@Getter
	private final Future<?> future;
	
	public MessageTask(SocketClient client, String data, ExecutorService executorService)
	{
		if(data == null)
		{
			throw new IllegalArgumentException("Null Data");
		}
		
		//super(() -> client.writeData(data));
		future = executorService.submit(() ->
		{
			setExecutionTime();
			
			//client.writeData(this);
		});
		
		this.callTrace = new Exception().getStackTrace();
		this.data = data;
	}
	
	public boolean isPossiblyHung()
	{
		if(executionTime == -1)
		{
			return false;
		}
		
		return System.currentTimeMillis() - executionTime > 1000; // > 500 ms ago should indicate an issue.
	}
	
	public void setExecutionTime()
	{
		this.executionTime = System.currentTimeMillis();
	}
	
}
