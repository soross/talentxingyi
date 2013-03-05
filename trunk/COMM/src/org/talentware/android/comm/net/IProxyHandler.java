package org.talentware.android.comm.net;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface IProxyHandler {

	/**
	 * ������CONNECT����UDP ASSOCIATE����ɹ��������������������ڴ�����֤�ɹ�ʱ����
	 * 
	 * @param bindAddress
	 *        ���ص�BND.ADDR��BND.PORT���������ֻ��UDP��ʽ���ô�
	 * @throws IOException
	 */
	public void proxyReady(InetSocketAddress bindAddress) throws IOException;

	/**
	 * ������֤ʧ��ʱ���ô˷���
	 */
	public void proxyAuthFail();

	/**
	 * ����������������δ֪���󣬵����������
	 * 
	 * @param err
	 *        ������Ϣ
	 */
	public void proxyError(String err);

}
