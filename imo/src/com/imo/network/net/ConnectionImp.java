package com.imo.network.net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public abstract class ConnectionImp implements IConnection, INIOHandler {

	/** ���ͻ����� */
	protected ByteBuffer sendBuf;
	/** ���ջ����� */
	protected ByteBuffer receiveBuf;
	/** �������port��ӳ�䣬�����ж�һ�����������ĸ�port */
	// protected Map<InPacket, Short> inConn;
	/** �˿����� */
	protected String name;
	/** Զ�̵�ַ */
	protected InetSocketAddress remoteAddress;

	public String getConnectionID() {
		return name;
	}

	/**
	 * ���캯��
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
