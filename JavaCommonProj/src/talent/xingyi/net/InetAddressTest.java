package talent.xingyi.net;

import java.net.InetAddress;

public class InetAddressTest {

	public static void main(String[] args) throws Exception {
		InetAddress iIpAddress = InetAddress.getByName("java.sun.com");
		System.out.println("sina 是否可达:" + iIpAddress.isReachable(2000));
		System.out.println(iIpAddress.getHostAddress());
		// System.out.println(iIpAddress.getCanonicalHostName());
		// System.out.println(iIpAddress.getHostName());

		InetAddress iLocalAddress = InetAddress.getByAddress(new byte[] { 127,
				0, 0, 1 });
		System.out.println("本机是否可达:" + iLocalAddress.isReachable(2000));
		System.out.println(iLocalAddress.getCanonicalHostName());
	}

}
