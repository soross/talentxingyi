package com.imo.network.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;

import com.imo.network.packages.OutPacket;

public interface IConnection {

	public void start();

	public String getId();

	public void dispose();

	public InetSocketAddress getRemoteAddress();

	public SelectableChannel channel();

	public INIOHandler getNIOHandler();

	public void receive() throws IOException;

	public void send() throws IOException;

	public void send(ByteBuffer buffer);

	public void send(OutPacket packet);

	public boolean isConnected();

	// [Add Observer Operation]
	/*
	 * public InPacket getInPacketByCommand(short command); public void
	 * addToObserverList(PacketsObserver aObserver); public boolean
	 * hasExistObserver(PacketsObserver aObserver); public void
	 * removeFromObserverList(PacketsObserver aObserver); public void
	 * observerNotifyPacketArrived(String aConnectionId,short command); public
	 * void observerNotifyHttpPacketArrived(String aConnectionId,ByteBuffer
	 * buffer); public void observerNotifyPacketProgress(String aConnectionId,
	 * short command,short aTotalLen,short aSendedLen); public void
	 * observerNotifyPacketTimeOut(String aConnectionId,short aErrorCode);
	 * public void observerNotifyPacketFailed(String aConnectionId,short
	 * aErrorCode);
	 */
	// [End]
}
