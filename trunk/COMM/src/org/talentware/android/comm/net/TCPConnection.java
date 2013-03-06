package org.talentware.android.comm.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import org.talentware.android.comm.dataengine.DataEngine;
import org.talentware.android.comm.dataengine.DataEngine.LOGICSTATUS;
import org.talentware.android.comm.packet.InPacket;
import org.talentware.android.comm.packet.OutPacket;
import org.talentware.android.comm.packet.command.IMOCommand;
import org.talentware.android.comm.packet.exception.PacketParseException;
import org.talentware.android.comm.util.ConnectionLog;
import org.talentware.android.comm.util.LogFactory;

public class TCPConnection extends ConnectionImp {
	private static final String TAG = TCPConnection.class.getSimpleName();

	/** ����ͨ�ŵ�channel */
	private final SocketChannel channel;
	/**
	 * true��ʾԶ���Ѿ��ر����������
	 */
	private boolean remoteClosed;

	private int testPortNum = 0;

	private String ipAddress = null;

	private long heartControlTime;

	/**
	 * 
	 * @param id
	 * @param address
	 * @param ConnType
	 * @throws IOException
	 */
	public TCPConnection(String id, InetSocketAddress address, byte ConnType) throws IOException {
		super(id);
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.remoteAddress = address;
		remoteClosed = false;
		heartControlTime = System.currentTimeMillis();
	}

	/**
	 * ����һ�����ӵ�ָ����ַ��TCPPort.
	 * 
	 * @param address
	 *        ���ӵ��ĵ�ַ.
	 * @throws IOException
	 *         �˿ڴ�/�˿�����/���ӵ���ַ����.
	 */
	public TCPConnection(String id, InetSocketAddress address) throws IOException {
		super(id);
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.remoteAddress = address;
		remoteClosed = false;
		heartControlTime = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 */
	public static String DNS_IP = "DNS_IP";
	boolean isConnected = false;

	public void start() {
		// TODO:below
		// ��һ�λ�����һ�α�������ŵķ��������ó�������û����ȡӦ����Ĭ��ip
		// ipAddress = (String)
		// PreferenceManager.get(IMOApp.getApp().getResources().getString(R.string.init_file),
		// new String[] { TCPConnection.DNS_IP, new String() });
		// �˴�ipAddress��д��һ����������

		ipAddress = EngineConst.hostIP;

		if (testPortNum < 3) {
			try {
				testPortNum++;
				// TODO:�˿�ÿ�����͵ķ����ֻ��һ�������Դ˴�ʡ�ԣ���ʱ����2860
				int port = 2860;// EngineConst.portArray[testPortNum++];
				InetSocketAddress address = new InetSocketAddress(ipAddress, port);

				Socket theSocket = new Socket();
				theSocket.connect(address, 10 * 1000);
				if (theSocket != null && theSocket.isConnected())
					theSocket.close();

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CONNECTING);

				isConnected = channel.connect(address);

				LogFactory.d(NIOThread.class.getSimpleName(), "isConnected :" + isConnected);

				ConnectionLog.MusicLogInstance().addLog("ServerIPAddress:" + ipAddress + ":" + port);

				LogFactory.d(TAG, "TCPConnection :" + this.name + " ��" + testPortNum + "��connect,ip = " + ipAddress + ",port = " + port);
			} catch (Exception e) {
				e.printStackTrace();
				LogFactory.e(TAG, "��" + testPortNum + "������ʧ��");

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

				if (3 == testPortNum) {// ���ζ�ʧ�ܵĻ����򲻻ᱣ������������������ip������Ϊ ""
					testPortNum = 0;
					try {
						DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CONNECTING);

						isConnected = channel.connect(EngineConst.IMO_SERVER_ADDRESS);
						LogFactory.e(NIOThread.class.getSimpleName(), "isConnected :" + isConnected);

						// TODO:��ʱȥ��
						// PreferenceManager.save(IMOApp.getApp().getResources().getString(R.string.init_file),
						// new String[] { TCPConnection.DNS_IP, "" });

					} catch (IOException e1) {
						processError(e1, IMOCommand.ERROR_CONNECTION_BROKEN);
						e1.printStackTrace();
					}
				} else {
					start();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public SelectableChannel channel() {
		return channel;
	}

	private int readBuffer() {
		try {
			int temp = channel.read(receiveBuf);
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 */
	public void receive() throws IOException {

		if (remoteClosed)
			return;
		// ��������
		int oldPos = receiveBuf.position();

		for (int r = readBuffer(); r > 0; r = readBuffer()) {
			// buffer length������2�����������ֶζ�û�� int
			int bufferLength = receiveBuf.position() - oldPos;
			if (bufferLength < 2)
				continue;

			// ����ɶ�����С�ڰ��������������û����
			short length = receiveBuf.getShort(oldPos);// TODO:���ɿ�ͷshortΪ������
			ConnectionLog.MusicLogInstance().addLog("Recevied : bufferLength = " + bufferLength + ", length = " + length);

			if (bufferLength < length) {
				continue;
			}
		}

		// �õ���ǰλ��
		int pos = receiveBuf.position();
		// ����Ƿ����0�ֽڣ��������һ���ʾԶ���Ѿ��ر����������
		if (oldPos == pos) {
			return;
		} else {
			heartControlTime = System.currentTimeMillis();
			EngineConst.HEARTBEAT_SEND_COUNT = 0;
		}

		receiveBuf.flip();
		ConnectionLog.MusicLogInstance().addLog(this.name + " TCPConnection Received:, Length = " + receiveBuf.limit());

		// һֱѭ�����ް��ɶ�
		while (true) {
			/* ����������İ���������������������������� */
			// ������һ����
			InPacket packet = null;
			int packageLen = checkTCP(receiveBuf);
			ConnectionLog.MusicLogInstance().addLog("packageLen = " + packageLen + ",position = " + receiveBuf.position());

			if (packageLen > 0) {
				try {
					packet = DataEngine.getInstance().parseIn(receiveBuf, packageLen);
				} catch (PacketParseException e) {
					e.printStackTrace();
					adjustBuffer(pos);
				}
			}

			if (-2 == packageLen) {
				processError(new Exception("receive exception!"), IMOCommand.ERROR_REMOTE_DATA);
				return;
			}

			if (packet != null) {

				if (IMOCommand.IMO_ERROR_PACKET == packet.getCommand()) {
					processError(new Exception("Error Packet!"), IMOCommand.ERROR_REMOTE_DATA);
					return;
				} else {
					int relocateLen = packet.relocate(receiveBuf);
					if (relocateLen != 0) {
						receiveBuf.position(relocateLen);
					}

					LogFactory.d(NIOThread.class.getSimpleName(), "receive packet , command = " + packet.getCommand());
					boolean isAdded = DataEngine.getInstance().getInQueue().add(packet);
					if (isAdded) {
						DataEngine.getInstance().observerNotifyPacketArrived(this.name, packet.getCommand());
					}
				}

			}

			if (packet == null) {
				/*
				 * packetΪnull���������: һ�ǻ����ڵ����ݶ��Ѿ������꣬ �������ݻ�û�н����꣬���ǲ���һ�������İ���
				 * ���Ƿ��������ص���һ�����������������¶�������
				 */
				break;
			}
		}

		adjustBuffer(pos);
	}

	/**
	 * ����buffer
	 * 
	 * @param pos
	 */
	private void adjustBuffer(int pos) {
		// ���0�����ڵ�ǰpos��˵�����ٷ�����һ����
		if (receiveBuf.position() > 0) {
			LogFactory.e("adjustBuffer", "Compact: position = " + receiveBuf.position());
			receiveBuf.compact();
			receiveBuf.limit(receiveBuf.capacity());
			LogFactory.e("adjustBuffer", "Compacted: position = " + receiveBuf.position());
		} else {
			receiveBuf.limit(receiveBuf.capacity());
			receiveBuf.position(pos);
		}
	}

	private int checkTCP(ByteBuffer buf) {
		// buffer length������2�����������ֶζ�û��
		int bufferLength = buf.limit() - buf.position();

		if (bufferLength < 2)
			return -1;

		// ����ɶ�����С�ڰ��������������û����
		int length = buf.getShort(buf.position());

		if (length < 17 || length > 4400) {
			return -2;
		}

		if (length > bufferLength) {
			return -1;
		}

		return length;
	}

	public boolean filterCommand(OutPacket aOut) {
		if (aOut.getCommand() != IMOCommand.IMO_HEART_BEAT && aOut.getCommand() != IMOCommand.IMO_UPDATE_STATUS && aOut.getCommand() != IMOCommand.IMO_SEND_MESSAGE && aOut.getCommand() != IMOCommand.IMO_SEND_MESSAGE_ACK && aOut.getCommand() != IMOCommand.IMO_EXIT
				&& aOut.getCommand() != IMOCommand.IMO_OUTER_CONTACTOR_LIST
				// && aOut.getCommand() != IMOCommand.IMO_GET_EMPLOYEE_STATUS
				&& aOut.getCommand() != IMOCommand.IMO_STATUS_ACK && aOut.getCommand() != IMOCommand.IMO_REPORT_ERROR && aOut.getCommand() != IMOCommand.IMO_UPDATE_VERSION)
			return true;
		return false;

	}

	/*
	 * (non-Javadoc)
	 */
	public void send() {
		if (!DataEngine.getInstance().getTimeoutQueue().isEmpty()) {// ��ʱ���в�Ϊ��
			LogFactory.d(NIOThread.class.getSimpleName(), "TimeoutQueue is not empty , size = " + DataEngine.getInstance().getTimeoutQueue().size());
			Iterator<Map.Entry<Integer, OutPacket>> it = DataEngine.getInstance().getTimeoutQueue().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, OutPacket> entry = (Map.Entry<Integer, OutPacket>) it.next();
				int seq = (Integer) entry.getKey();
				OutPacket out = (OutPacket) entry.getValue();

				if (filterCommand(out)) {
					long timeLeft = out.getTimeout() - System.currentTimeMillis();

					if (timeLeft < 0) {
						if (out.getResendCountDown() > 0) {// ���¼��뷢�Ͷ���
							LogFactory.d(NIOThread.class.getSimpleName(), "Resend packet , packet's command = " + out.getCommand());
							DataEngine.getInstance().add(out);
							DataEngine.getInstance().removePacketFromTimeoutQueue(seq);
						} else {// ֪ͨ�����������ʱ
							LogFactory.d(NIOThread.class.getSimpleName(), "Packet Timeout , packet's command = " + out.getCommand());
							DataEngine.getInstance().removePacketFromTimeoutQueue(seq);
							DataEngine.getInstance().observerNotifyPacketTimeOut(name, out.getCommand());
						}
					}
				}
			}
		}

		if (DataEngine.getInstance().isEmpty()) {// ���Ͷ���Ϊ��
			/** �����Ӵ��ڵ�����£����30s�ڻ�û�����ݷ��ͣ��ͷ����������ݰ��������� */
			if (EngineConst.isLoginSuccess /* && EngineConst.isReloginSuccess* */) {
				if (System.currentTimeMillis() - heartControlTime > 30 * 1000) {
					if (EngineConst.HEARTBEAT_SEND_COUNT < 5) {
						// TODO:��ʱȥ��
						// HeartBeatOutPacket heartbeatPackage = new
						// HeartBeatOutPacket(ByteBuffer.wrap("".getBytes()),
						// IMOCommand.IMO_HEART_BEAT, EngineConst.cId,
						// EngineConst.uId);
						// heartbeatPackage.setTimeout(10 * 1000);
						//
						// DataEngine.getInstance().add(heartbeatPackage);
						// heartControlTime = System.currentTimeMillis();
					} else {
						EngineConst.HEARTBEAT_SEND_COUNT = 0;
						processError(new Exception("HeartBeat times > 5"), IMOCommand.ERROR_NETWORK);
					}
				}

			}
		} else {
			heartControlTime = System.currentTimeMillis();// ��¼����ʱ��
		}

		LogFactory.d(NIOThread.class.getSimpleName(), "OutQueueSize:" + DataEngine.getInstance().getOutQueue().size());
		while (!DataEngine.getInstance().isEmpty()) {// ���Ͷ��в�Ϊ��
			sendBuf.clear();
			short sendedLen = 0;

			// ���ⷢ���ص�½֮ǰ����������������reset
			OutPacket p = DataEngine.getInstance().getPacketByCommand(IMOCommand.IMO_GET_RELOGIN);

			if (null != p) {
				DataEngine.getInstance().getOutQueue().clear();
				DataEngine.getInstance().add(p);
			}

			OutPacket packet = DataEngine.getInstance().remove();
			ByteBuffer body = ByteBuffer.allocate(packet.getBody().length);
			// TODO:��ʱȥ������֪����û��Ӱ��
			// ByteBuffer body = ByteBuffer.allocate(packet.getHeader().length +
			// packet.getBody().length);
			// body.put(packet.getHeader());
			body.put(packet.getBody());
			body.flip();
			try {
				while (sendedLen < body.limit()) {
					sendedLen += (short) channel.write(body);
					LogFactory.d(NIOThread.class.getSimpleName(), "Send Packet Success!!! command :" + packet.getCommand() + ", bodyLength :" + body.limit() + ", remaining :" + body.remaining() + ", sendedLen :" + sendedLen);
				}
				DataEngine.getInstance().observerNotifyPacketProgress(this.name, packet.getCommand(), (short) body.limit(), sendedLen);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogFactory.e("TCPConnection Reconnect:", " exception :" + e.toString());

				if (EngineConst.isLoginSuccess) {
					if (!DataEngine.getInstance().getTimeoutQueue().isEmpty()) {
						DataEngine.getInstance().getTimeoutQueue().clear();
					}
					processError(e, IMOCommand.ERROR_NETWORK);
				}

			} finally {
				packet.setResendCountDown(100);
//				packet.setResendCountDown(packet.getResendCountDown() - 1);
				body = null;
			}

			// ����Packet��ʱʱ��
			if (packet.getCommand() == IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS) {// 120s
				packet.setTimeout(System.currentTimeMillis() + EngineConst.IMO_TIMEOUT_GET_OFFLINE_CONTENTS);
			} else {// 60s
				// ��ʱȥ��
				// if (Functions.isWifi()) {
				// packet.setTimeout(System.currentTimeMillis() +
				// EngineConst.IMO_TIMEOUT_SEND_WIFI);
				// } else {
				// packet.setTimeout(System.currentTimeMillis() +
				// EngineConst.IMO_TIMEOUT_SEND_GPRS);
				// }
			}

			// ������������ʱ�ж�
			if (filterCommand(packet)) {
				DataEngine.getInstance().getTimeoutQueue().put(packet.get_header_seq(), packet);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void send(OutPacket packet) {
		try {
			sendBuf.clear();
			ByteBuffer body = ByteBuffer.allocate(packet.getHeader().length + packet.getBody().length);
			body.put(packet.getHeader());
			body.put(packet.getBody());
			body.flip();
			channel.write(body);
			LogFactory.e("TCPConnection", "have sended packet - " + packet.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void send(ByteBuffer buffer) {
		try {
			channel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	Object lock_dispose = new Object();

	public void dispose() {
		synchronized (lock_dispose) {
			try {
				LogFactory.e("dispose :", "dispose name --->" + this.name + ", isClosed :" + channel.socket().isClosed());
				if (!channel.socket().isClosed()) {
					channel.close();
					LogFactory.e("dispose :", "after dispose name --->" + this.name + ", isClosed :" + channel.socket().isClosed());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public boolean isConnected() {
		return channel != null && channel.isConnected();
	}

	/*
	 * (non-Javadoc)
	 */
	public void processConnect(SelectionKey sk) throws IOException {
		// ���SocketChannel������
		channel.finishConnect();
		while (!channel.isConnected()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// û��ʲôҪ����
				e.printStackTrace();
			}
			channel.finishConnect();
		}
		sk.interestOps(SelectionKey.OP_READ);
		LogFactory.d(NIOThread.class.getSimpleName(), "Connect To Server Success!!!");
	}

	/*
	 * (non-Javadoc)
	 */
	public void processRead(SelectionKey sk) throws IOException {
		receive();
	}

	/*
	 * (non-Javadoc)
	 */
	public void processWrite() {
		if (isConnected()) {
			LogFactory.d(NIOThread.class.getSimpleName(), "CanWrite");
			send();
		}
	}

	public void processError(Exception e, short aErrorCode) {
		LogFactory.e("TCPConnection :" + this.name, "����������ر�����, id: " + getId());
		if (this.name.equals(EngineConst.IMO_CONNECTION_ID)) {
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			EngineConst.isReloginSuccess = false;
			EngineConst.isNetworkValid = false;
			DataEngine.getInstance().observerNotifyPacketFailed(this.name, aErrorCode);
		}
	}

}