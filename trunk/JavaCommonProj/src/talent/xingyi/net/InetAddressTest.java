package talent.xingyi.net;

import java.net.InetAddress;

public class InetAddressTest {

	public static void main(String[] args) throws Exception {
		InetAddress iIpAddress = InetAddress.getByName("java.sun.com");
		System.out.println("sina �Ƿ�ɴ�:" + iIpAddress.isReachable(2000));
		System.out.println(iIpAddress.getHostAddress());
		// System.out.println(iIpAddress.getCanonicalHostName());
		// System.out.println(iIpAddress.getHostName());

		InetAddress iLocalAddress = InetAddress.getByAddress(new byte[] { 127,
				0, 0, 1 });
		System.out.println("�����Ƿ�ɴ�:" + iLocalAddress.isReachable(2000));
		System.out.println(iLocalAddress.getCanonicalHostName());
	}

}
