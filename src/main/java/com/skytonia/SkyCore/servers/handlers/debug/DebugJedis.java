package com.skytonia.SkyCore.servers.handlers.debug;

import lombok.Getter;
import redis.clients.jedis.Jedis;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

public class DebugJedis extends Jedis
{

	@Getter
	private final int connectionId;

	public DebugJedis(int connectionId, String host, int port, int connectionTimeout, int soTimeout, boolean ssl, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier)
	{
		super(host, port, connectionTimeout, soTimeout, ssl, sslSocketFactory, sslParameters, hostnameVerifier);

		this.connectionId = connectionId;
	}

	@Override
	public void close()
	{
		super.close();

		DebugJedisPool.registeredConnections.remove(this.connectionId);
	}

}
