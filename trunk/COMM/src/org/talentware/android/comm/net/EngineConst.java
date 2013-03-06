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
	/** Http����Command */
	public static final short IMO_HTTP_REQUEST = 0x00;
	/** �������ݱ�ʶ */
	public static final short IMO_TCP_HEARTBEAT = 0x01;

	public static final short IMO_TCP_CONNECT_TIMEOUT = 0x02;
	/** ����ʱ���� */
	public static final int IMO_TCP_HEARTBEAT_INTERVAL = 30 * 1000;
	/** ��ͷ��С */
	public static final short IMO__PACKET_HEADER_SIZE = 17;
	/** ������С */
	public static final int IMO_MAX_PACKET_SIZE = 65535;
	/** �ͻ��˰汾�� */
	public static final char IMO_CLIENT_VERSION_0F5F = 0x0F5F;
	/** ����Ҫȷ�ϵİ��ķ��ʹ��������ֵӦ�������ģ�����QQ Logout������4�Σ�������ѡ4 */
	public static final int IMO_SEND_TIME_NOACK_PACKET = 3;
	/** ��λ: ms */
	public static final long IMO_TIMEOUT_SEND_WIFI = 60 * 1000;

	public static final long IMO_TIMEOUT_SEND_GPRS = 60 * 1000;

	public static final long IMO_TIMEOUT_GET_OFFLINE_CONTENTS = 120 * 1000;

	/** TCP���ӵ�ID */
	public static String IMO_CONNECTION_ID = "TCP_CONNECTION_";

	public static int IMO_CONNETION_COUNT = 0;

	public static int CONCURRENT_MAX_VALUE = 50;

	/* ȡlogo���� */
	public static String IMO_HTTP_GETLOGO = "IMO_HTTP_GETLOGO";

	/* ȡͷ������ */
	public static String IMO_HTTP_GETSELFHEAD = "IMO_HTTP_GETSELFHEAD";

	public static String GenerateRandomString() {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // �����ַ����Ӵ�������ȡ
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
	/** ����ʹ�õ�password */
	public static String password = "ffffff";
	/** �������˷��ص�sessionKey,��ҪΪ�˼�/����ʹ�� */
	public static byte[] sessionKey = new byte[16];

	public static int[] portArray = new int[] {
			5186, 1863, 8000
	};

	public static String performDNSLookup(String aHostName) throws UnknownHostException {

		try {
			// ����״̬��״̬ΪCHECKING
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.CHECKING);

			InetAddress inetHost = InetAddress.getByName(aHostName);

			String hostName = inetHost.getHostName();
			String hostAddress = inetHost.getHostAddress();

			return hostAddress;
		} catch (UnknownHostException ex) {
			// ��ַ�޷��ﵽ������״̬��״̬ΪDISCONNECTED
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			throw ex;
		}
	}

	// public static final String hostName = "m1.imoffice.cn";
	public static final String hostName = "m1.imoffice.com";

	public static final String serverInfo = "http://errortip.app.imoffice.cn/message/error_tip.txt";

	// 123.129.204.227:2860
	public static final String hostIP = "202.104.236.72";// "123.129.204.227";// ��ʱ���������

	public static final InetSocketAddress IMO_SERVER_ADDRESS = new InetSocketAddress(hostIP, 2860);

	public static final int COCURRENT_MAX_VALUE = 0;
}
