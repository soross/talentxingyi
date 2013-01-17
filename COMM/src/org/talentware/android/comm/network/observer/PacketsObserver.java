package org.talentware.android.comm.network.observer;

import java.nio.ByteBuffer;

public interface PacketsObserver {
	/* TCP Packet arrived * */
	public void NotifyPacketArrived(String aConnectionId, short command);

	/* HTTP Packet arrived * */
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer);

	/* Packet progress * */
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen);

	/* Packet Timeout * */
	public void NotifyPacketTimeOut(String aConnectionId, short aErrorCode);

	/* Packet Failed * */
	public void NotifyPacketFailed(String aConnectionId, short aErrorCode);

	/* Can Accept TCP Packet * */
	public boolean CanAcceptPacket(int command);

	/* Can Accept Http Packet * */
	public boolean CanAcceptHttpPacket();
}
