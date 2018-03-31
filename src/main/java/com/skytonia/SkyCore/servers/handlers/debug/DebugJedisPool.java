package com.skytonia.SkyCore.servers.handlers.debug;

import javafx.util.Pair;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.ConcurrentHashMap;

public class DebugJedisPool extends Pool<Jedis>
{

	//Stores the stack trace where the jedis connection was registered
	public static final ConcurrentHashMap<Integer, Pair<Jedis, Throwable>> registeredConnections = new ConcurrentHashMap<>();
	public static int nextId = 1;

	public DebugJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout)
	{
		this(poolConfig, host, port, timeout, null, 0, null);
	}

	public DebugJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database, String clientName)
	{
		this(poolConfig, host, port, timeout, timeout, password, database, clientName, false, (SSLSocketFactory) null, (SSLParameters) null, (HostnameVerifier) null);
	}

	public DebugJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int connectionTimeout, int soTimeout, String password, int database, String clientName, boolean ssl, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier)
	{
		super(poolConfig, new DebugJedisFactory(host, port, connectionTimeout, soTimeout, password, database, clientName, ssl, sslSocketFactory, sslParameters, hostnameVerifier));
	}

	@Override
	public Jedis getResource()
	{
		Jedis jedis = super.getResource();
		jedis.setDataSource(this);

		registeredConnections.put((nextId++), new Pair<>(jedis, new Throwable()));

		return jedis;
	}

	/** @deprecated */
	@Deprecated
	public void returnResourceObject(Jedis resource)
	{
		if (resource != null)
		{
			try
			{
				this.internalPool.returnObject(resource);

				if(resource instanceof DebugJedis)
				{
					registeredConnections.remove(((DebugJedis) resource).getConnectionId());
				}
			}
			catch (Exception var3)
			{
				throw new JedisException("Could not return the resource to the pool", var3);
			}
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void returnBrokenResource(Jedis resource)
	{
		if (resource != null)
		{
			this.returnBrokenResourceObject(resource);

			if(resource instanceof DebugJedis)
			{
				registeredConnections.remove(((DebugJedis) resource).getConnectionId());
			}
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void returnResource(Jedis resource)
	{
		if (resource != null)
		{
			try
			{
				resource.resetState();
				this.returnResourceObject(resource);

				if(resource instanceof DebugJedis)
				{
					registeredConnections.remove(((DebugJedis) resource).getConnectionId());
				}
			}
			catch (Exception var3)
			{
				this.returnBrokenResource(resource);
				throw new JedisException("Could not return the resource to the pool", var3);
			}
		}
	}

	protected void returnBrokenResourceObject(Jedis resource)
	{
		try
		{
			this.internalPool.invalidateObject(resource);

			if(resource instanceof DebugJedis)
			{
				registeredConnections.remove(((DebugJedis) resource).getConnectionId());
			}
		}
		catch (Exception var3)
		{
			throw new JedisException("Could not return the resource to the pool", var3);
		}
	}

}
