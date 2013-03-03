package com.imo.network.net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public abstract class ConnectionImp implements IConnection, INIOHandler {

	/** 发送缓冲区 */
	protected ByteBuffer sendBuf;
	/** 接收缓冲区 */
	protected ByteBuffer receiveBuf;
	/** 输入包和port的映射，用来判断一个包来自于哪个port */
	// protected Map<InPacket, Short> inConn;
	/** 端口名称 */
	protected String name;
	/** 远程地址 */
	protected InetSocketAddress remoteAddress;

	public String getConnectionID() {
		return name;
	}

	/**
	 * 构造函数
	 */
	public ConnectionImp(String connectionID) {
		this.name = connectionID;
		sendBuf = ByteBuffer.allocateDirect(EngineConst.IMO_MAX_PACKET_SIZE);
		receiveBuf = ByteBuffer.allocateDirect(EngineConst.IMO_MAX_PACKET_SIZE);
	}

	public String getId() {
		return name;
	}

	public INIOHandler getNIOHandler() {
		return this;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
}
