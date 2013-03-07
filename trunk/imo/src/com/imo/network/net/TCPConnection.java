package com.imo.network.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import com.imo.R;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.IMOApp;
import com.imo.network.Log.ConnectionLog;
import com.imo.network.packages.HeartBeatOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.InPacket;
import com.imo.network.packages.OutPacket;
import com.imo.network.packages.PacketParseException;
import com.imo.util.Functions;
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;

public class TCPConnection extends ConnectionImp {
	private static final String TAG = TCPConnection.class.getSimpleName();

	/** 用于通信的channel */
	private final SocketChannel channel;
	/**
	 * true表示远程已经关闭了这个连接
	 */
	private boolean remoteClosed;

	// private Timer mTimer;

	private int testPortNum = 0;

	private String ipAddress = null;

	private long heartControlTime;

	// private boolean hasNotified = false;

	/**
	 * 构造一个连接到指定地址的TCPPort.
	 * 
	 * @param address
	 *        连接到的地址.
	 * @throws IOException
	 *         端口打开/端口配置/连接到地址出错.
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
		LogFactory.d(TAG, "testPortNum:" + testPortNum);
		if (0 == testPortNum) {// 第一次的时候会往下走
			ipAddress = (String) PreferenceManager.get(IMOApp.getApp().getResources().getString(R.string.init_file), new String[] {
					TCPConnection.DNS_IP, new String()
			});
			LogFactory.d(TAG, "ipAddress:" + ipAddress);

			if (ipAddress.length() != 0) {
				LogFactory.d("TCPConnection", "ipAddress Exits :" + ipAddress);
			} else {
				try {
					ipAddress = EngineConst.performDNSLookup(EngineConst.hostName);
					LogFactory.d(TAG, "ipAddress:" + ipAddress);
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					LogFactory.e(TAG, "UnknownHostException");
					e2.printStackTrace();

					try {
						if (null != channel) {
							DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CONNECTING);

							isConnected = channel.connect(EngineConst.IMO_SERVER_ADDRESS);
							LogFactory.d(TAG, "isConnected :" + isConnected);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						processError(e1, IMOCommand.ERROR_CONNECTION_BROKEN);
						e1.printStackTrace();
						LogFactory.d(TAG, "IOException");
					}

					return;
				}

				PreferenceManager.save(IMOApp.getApp().getResources().getString(R.string.init_file), new String[] {
						TCPConnection.DNS_IP, ipAddress
				});
			}
		}
		// 若已经保存过或者是能解析的出来域名的，会继续往下走

		if (testPortNum < 3) {
			try {
				int port = EngineConst.portArray[testPortNum++];
				InetSocketAddress address = new InetSocketAddress(ipAddress, port);

				Socket theSocket = new Socket();
				theSocket.connect(address, 10 * 1000);
				if (theSocket != null && theSocket.isConnected())
					theSocket.close();

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CONNECTING);

				isConnected = channel.connect(address);

				LogFactory.d(TAG, "isConnected :" + isConnected);

				ConnectionLog.MusicLogInstance().addLog("ServerIPAddress:" + ipAddress + ":" + port);

				LogFactory.d(TAG, "TCPConnection :" + this.name + " 第" + testPortNum + "次connect,ip = " + ipAddress + ",port = " + port);
			} catch (Exception e) {
				e.printStackTrace();
				LogFactory.e(TAG, "第" + testPortNum + "次连接失败");

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

				if (3 == testPortNum) {// 三次都失败的话，则不会保存由域名解析出来的ip，保存为 ""
					testPortNum = 0;
					try {
						DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CONNECTING);

						isConnected = channel.connect(EngineConst.IMO_SERVER_ADDRESS);
						LogFactory.e("isConnected", "isConnected :" + isConnected);

						PreferenceManager.save(IMOApp.getApp().getResources().getString(R.string.init_file), new String[] {
								TCPConnection.DNS_IP, ""
						});

					} catch (IOException e1) {
						// TODO Auto-generated catch block
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
		// 接收数据
		int oldPos = receiveBuf.position();

		for (int r = readBuffer(); r > 0; r = readBuffer()) {
			int bufferLength = receiveBuf.position() - oldPos;
			if (bufferLength < 2) {
				// buffer length不大于2则连个长度字段都没有 int
				continue;
			}

			// 如果可读内容小于包长，则这个包还没收完
			short length = receiveBuf.getShort(oldPos);// TODO:怀疑开头short为包长度
			ConnectionLog.MusicLogInstance().addLog("Recevied : bufferLength = " + bufferLength + ", length = " + length);

			if (bufferLength < length) {// 没有读完则继续读
				continue;
			}
		}

		// 得到当前位置
		int pos = receiveBuf.position();
		// 检查是否读了0字节，这种情况一般表示远程已经关闭了这个连接
		if (oldPos == pos) {
			return;
		} else {
			heartControlTime = System.currentTimeMillis();
			EngineConst.HEARTBEAT_SEND_COUNT = 0;
		}

		receiveBuf.flip();// flip后就开始读了
		ConnectionLog.MusicLogInstance().addLog(this.name + " TCPConnection Received:, Length = " + receiveBuf.limit());

		// 一直循环到无包可读
		while (true) {
			/* 如果有完整的包，则添加这个包，调整各个参数 */
			// 解析出一个包
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
					LogFactory.d(NIOThread.class.getSimpleName(), "relocateLen:" + relocateLen);
					if (relocateLen != 0) {
						receiveBuf.position(relocateLen);
					}

					LogFactory.d(NIOThread.class.getSimpleName(), "packet.getCommand():" + packet.getCommand());
					boolean isAdded = DataEngine.getInstance().getInQueue().add(packet);
					if (isAdded) {
						DataEngine.getInstance().observerNotifyPacketArrived(this.name, packet.getCommand());
					}
				}

			}

			if (packet == null) {
				/*
				 * packet为null有三种情况: 一是缓冲内的数据都已经解析完， 二是数据还没有解析完，但是不是一个完整的包，
				 * 三是服务器返回的是一个错误包，这种情况下断线重连
				 */
				break;
			}
		}

		adjustBuffer(pos);
	}

	/**
	 * 调整buffer
	 * 
	 * @param pos
	 */
	private void adjustBuffer(int pos) {
		if (receiveBuf.position() > 0) {// 说明至少分析了一个包
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
		// buffer length不大于2则连个长度字段都没有
		int bufferLength = buf.limit() - buf.position();

		if (bufferLength < 2)
			return -1;

		// 如果可读内容小于包长，则这个包还没收完
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
		if (!DataEngine.getInstance().getTimeoutQueue().isEmpty()) {// 超时队列不为空
			LogFactory.d(NIOThread.class.getSimpleName(), "TimeoutQueue is not empty , size = " + DataEngine.getInstance().getTimeoutQueue().size());
			Iterator<Map.Entry<Integer, OutPacket>> it = DataEngine.getInstance().getTimeoutQueue().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, OutPacket> entry = (Map.Entry<Integer, OutPacket>) it.next();
				int seq = (Integer) entry.getKey();
				OutPacket out = (OutPacket) entry.getValue();

				if (filterCommand(out)) {
					long timeLeft = out.getTimeout() - System.currentTimeMillis();

					if (timeLeft < 0) {
						if (out.getResendCountDown() > 0) {// 重新加入发送队列，发送
							LogFactory.d(NIOThread.class.getSimpleName(), "Resend Packet , Packet's Command = " + out.getCommand());
							DataEngine.getInstance().add(out);
							DataEngine.getInstance().removePacketFromTimeoutQueue(seq);
						} else {// 通知界面请求包超时
							LogFactory.d(NIOThread.class.getSimpleName(), "Packet TimeOut, Packet's Command = " + out.getCommand());
							DataEngine.getInstance().removePacketFromTimeoutQueue(seq);
							DataEngine.getInstance().observerNotifyPacketTimeOut(name, out.getCommand());
						}
					}
				}
			}
		}

		if (DataEngine.getInstance().isEmpty()) {// 发送队列为空
			/** 在连接存在的情况下，如果30s内还没有数据发送，就发送心跳数据包到服务器 */
			if (EngineConst.isLoginSuccess) {
				if (System.currentTimeMillis() - heartControlTime > 30 * 1000) {
					if (EngineConst.HEARTBEAT_SEND_COUNT < 5) {
						HeartBeatOutPacket heartbeatPackage = new HeartBeatOutPacket(ByteBuffer.wrap("".getBytes()), IMOCommand.IMO_HEART_BEAT, EngineConst.cId, EngineConst.uId);
						heartbeatPackage.setTimeout(10 * 1000);

						DataEngine.getInstance().add(heartbeatPackage);
						heartControlTime = System.currentTimeMillis();
					} else {
						EngineConst.HEARTBEAT_SEND_COUNT = 0;
						processError(new Exception("HeartBeat times > 5"), IMOCommand.ERROR_NETWORK);
					}
				}

			}
		} else {
			heartControlTime = System.currentTimeMillis();// 记录心跳时间
		}

		while (!DataEngine.getInstance().isEmpty()) {// 发送队列不为空
			LogFactory.d(NIOThread.class.getSimpleName(), "OutQueueSize is not empty , Size = " + DataEngine.getInstance().getOutQueue().size());
			sendBuf.clear();
			short sendedLen = 0;

			// 避免发送重登陆之前发送心跳导致连接reset
			OutPacket p = DataEngine.getInstance().getPacketByCommand(IMOCommand.IMO_GET_RELOGIN);

			if (null != p) {
				DataEngine.getInstance().getOutQueue().clear();
				DataEngine.getInstance().add(p);
			}

			OutPacket packet = DataEngine.getInstance().remove();
			ByteBuffer body = ByteBuffer.allocate(packet.getHeader().length + packet.getBody().length);
			body.put(packet.getHeader());
			body.put(packet.getBody());
			body.flip();
			try {
				while (sendedLen < body.limit()) {
					sendedLen += (short) channel.write(body);
					LogFactory.e(NIOThread.class.getSimpleName(), "Send Packet , Command = " + packet.getCommand() + ", BodyLength = " + body.limit() + ", remaining = " + body.remaining() + ", sendedLen = " + sendedLen);
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
				LogFactory.d(NIOThread.class.getSimpleName(), "Packet[" + packet.getCommand() + "]'s Resend Count = " + (packet.getResendCountDown() - 1));
				packet.setResendCountDown(packet.getResendCountDown() - 1);
				body = null;
			}

			// 设置Packet超时时间
			if (packet.getCommand() == IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS) {// 120s
				packet.setTimeout(System.currentTimeMillis() + EngineConst.IMO_TIMEOUT_GET_OFFLINE_CONTENTS);
			} else {// 60s
				if (Functions.isWifi()) {
					packet.setTimeout(System.currentTimeMillis() + EngineConst.IMO_TIMEOUT_SEND_WIFI);
				} else {
					packet.setTimeout(System.currentTimeMillis() + EngineConst.IMO_TIMEOUT_SEND_GPRS);
				}
			}

			// 心跳包不做超时判断
			if (filterCommand(packet)) {
				LogFactory.d(NIOThread.class.getSimpleName(), "Packet Header Seq = " + packet.get_header_seq());
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
		// 完成SocketChannel的连接
		channel.finishConnect();
		while (!channel.isConnected()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// 没有什么要做的
				e.printStackTrace();
			}
			channel.finishConnect();
		}
		sk.interestOps(SelectionKey.OP_READ);
		LogFactory.d(TAG, "Success To Connect Server!!!");
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
			LogFactory.d(NIOThread.class.getSimpleName(), "Can Write");
			send();
		}
	}

	public void processError(Exception e, short aErrorCode) {
		LogFactory.e("TCPConnection :" + this.name, "网络出错，关闭连接, id: " + getId());
		if (this.name.equals(EngineConst.IMO_CONNECTION_ID)) {
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			EngineConst.isReloginSuccess = false;
			EngineConst.isNetworkValid = false;
			DataEngine.getInstance().observerNotifyPacketFailed(this.name, aErrorCode);
		}
	}

}
