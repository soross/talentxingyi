package org.talentware.android.comm.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.talentware.android.comm.dataengine.DataEngine;
import org.talentware.android.comm.dataengine.DataEngine.LOGICSTATUS;

public class EngineConst {
	public static short HEARTBEAT_SEND_COUNT = 0;

	public static boolean isConnected = false;

	public static boolean isNetworkValid = true;

	public static boolean isReloginSuccess = true;

	public static boolean isStartRelogin = true;

	public static byte version = 0x00;
	/** Http请求Command */
	public static final short IMO_HTTP_REQUEST = 0x00;
	/** 心跳数据标识 */
	public static final short IMO_TCP_HEARTBEAT = 0x01;

	public static final short IMO_TCP_CONNECT_TIMEOUT = 0x02;
	/** 心跳时间间隔 */
	public static final int IMO_TCP_HEARTBEAT_INTERVAL = 30 * 1000;
	/** 包头大小 */
	public static final short IMO__PACKET_HEADER_SIZE = 17;
	/** 包最大大小 */
	public static final int IMO_MAX_PACKET_SIZE = 65535;
	/** 客户端版本号 */
	public static final char IMO_CLIENT_VERSION_0F5F = 0x0F5F;
	/** 不需要确认的包的发送次数，这个值应该是随便的，由于QQ Logout包发了4次，所以我选4 */
	public static final int IMO_SEND_TIME_NOACK_PACKET = 3;
	/** 单位: ms */
	public static final long IMO_TIMEOUT_SEND_WIFI = 60 * 1000;

	public static final long IMO_TIMEOUT_SEND_GPRS = 60 * 1000;

	public static final long IMO_TIMEOUT_GET_OFFLINE_CONTENTS = 120 * 1000;

	/** TCP连接的ID */
	public static String IMO_CONNECTION_ID = "TCP_CONNECTION_";

	public static int IMO_CONNETION_COUNT = 0;

	public static int CONCURRENT_MAX_VALUE = 50;

	/* 取logo链接 */
	public static String IMO_HTTP_GETLOGO = "IMO_HTTP_GETLOGO";

	/* 取头像链接 */
	public static String IMO_HTTP_GETSELFHEAD = "IMO_HTTP_GETSELFHEAD";

	public static String GenerateRandomString() {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 生成字符串从此序列中取
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		sb.append("TCP_CONNECTION_");
		sb.append(IMO_CONNETION_COUNT++);
		sb.append("_");

		for (int i = 0; i < 6; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}

		return sb.toString();
	}

	public static int cId = 0;

	public static int uId = 0;

	public static boolean isLoginSuccess = false;
	/** 解密使用的password */
	public static String password = "ffffff";
	/** 服务器端返回的sessionKey,主要为了加/解密使用 */
	public static byte[] sessionKey = new byte[16];

	public static int[] portArray = new int[] {
			5186, 1863, 8000
	};

	public static String performDNSLookup(String aHostName) throws UnknownHostException {

		try {
			// 设置状态机状态为CHECKING
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CHECKING);

			InetAddress inetHost = InetAddress.getByName(aHostName);

			String hostName = inetHost.getHostName();
			String hostAddress = inetHost.getHostAddress();

			return hostAddress;
		} catch (UnknownHostException ex) {
			// 地址无法达到，设置状态机状态为DISCONNECTED
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			throw ex;
		}
	}

	// public static final String hostName = "m1.imoffice.cn";
	public static final String hostName = "m1.imoffice.com";

	public static final String serverInfo = "http://errortip.app.imoffice.cn/message/error_tip.txt";

	// 123.129.204.227:2860
	public static final String hostIP = "202.104.236.72";// "123.129.204.227";// 即时行情服务器

	public static final InetSocketAddress IMO_SERVER_ADDRESS = new InetSocketAddress(hostIP, 2860);

	public static final int COCURRENT_MAX_VALUE = 0;
}
