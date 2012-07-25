package AsyncEngineAndroid.source;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

public abstract class ConnectionImp implements IConnection, INIOHandler {

	/** ���ͻ����� */
	protected ByteBuffer sendBuf;
	/** ���ջ����� */
	protected ByteBuffer receiveBuf;
	/** ���Ͷ��� */
	protected Queue<OutPacket> sendQueue;
	/** ���ն��� */
	protected Queue<InPacket> inQueue;
	/** �˿����� */
	protected String name;
	/** Զ�̵�ַ */
	protected InetSocketAddress remoteAddress;

	/**
	 * ���캯��
	 */
	public ConnectionImp(String connectionID) {
		this.name = connectionID;
		sendQueue = new LinkedList<OutPacket>();
		inQueue = new LinkedList<InPacket>();
		sendBuf = ByteBuffer.allocateDirect(EnginConst.QQ_MAX_PACKET_SIZE);
		receiveBuf = ByteBuffer.allocateDirect(EnginConst.QQ_MAX_PACKET_SIZE);
	}

	public String getId() {
		return name;
	}

	public synchronized void clearSendQueue() {
		sendQueue.clear();
	}
	
	public synchronized Queue<InPacket> getInQueue() {
		return inQueue;
	}

	public synchronized void add(OutPacket packet) {
		sendQueue.offer(packet);
	}
	
	public synchronized boolean isEmpty() {
		return sendQueue.isEmpty();
	}

	public synchronized OutPacket remove() {
		return sendQueue.poll();
	}

	public INIOHandler getNIOHandler() {
		return this;
	}

	public void processError(Exception e) {
		//Log.d("debug","��������ر�����, id: " + getId());;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
}
