package org.talentware.android.service;

import org.talentware.android.comm.net.EngineConst;
import org.talentware.android.comm.net.NIOThread;
import org.talentware.android.comm.net.TCPConnection;
import org.talentware.android.comm.util.LogFactory;
import org.talentware.android.global.Globe;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 保持网络通信线程较高的优先级别 <br>
 * 在服务中启动网络通信线程
 */
public class AppService extends Service {

	private static final String TAG = AppService.class.getSimpleName();

	public static AppService appService = null;

	private NIOThread nioThreadInstance = null;

	private TCPConnection tcpConnection = null;

	public static AppService getService() {
		return appService;
	}

	private Object lock_getNIOThreadInstance = new Object();

	/**
	 * 获得NIOThread线程实例对象
	 * 
	 * @return
	 */
	public NIOThread getNIOThreadInstance() {
		synchronized (lock_getNIOThreadInstance) {
			if (nioThreadInstance == null) {
				nioThreadInstance = new NIOThread();
			}
			return nioThreadInstance;
		}
	}

	private Object lock_getTcpConnection = new Object();

	public TCPConnection getTcpConnection() {
		synchronized (lock_getTcpConnection) {
			LogFactory.d(TAG, "tcpConnection == null:" + (tcpConnection == null) + ",Globe.canConnect:" + Globe.canConnect);
			if (tcpConnection == null && Globe.canConnect) {
				if (nioThreadInstance == null) {
					nioThreadInstance = new NIOThread();
				}
				LogFactory.d(TAG, "EngineConst.IMO_CONNECTION_ID:" + EngineConst.IMO_CONNECTION_ID + ",EngineConst.IMO_SERVER_ADDRESS:" + EngineConst.IMO_SERVER_ADDRESS);
				tcpConnection = (TCPConnection) nioThreadInstance.newTCPConnection(EngineConst.IMO_CONNECTION_ID, EngineConst.IMO_SERVER_ADDRESS, true);
			}
			return tcpConnection;
		}
	}

	public NIOThread getCurNIOThreadInstance() {
		return nioThreadInstance;
	}

	public void setTcpConnection(TCPConnection aConnection) {
		tcpConnection = aConnection;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		appService = this;
		LogFactory.i(TAG, "App Service Start");
//		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	private Object lock_reset = new Object();

	public void reset() {
		synchronized (lock_reset) {
			if (nioThreadInstance != null) {
				nioThreadInstance.release(EngineConst.IMO_CONNECTION_ID, true);
				nioThreadInstance.dispose();
				nioThreadInstance = null;
				tcpConnection = null;
			}
		}
	}
}
