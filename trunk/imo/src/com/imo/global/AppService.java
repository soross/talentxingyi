package com.imo.global;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.imo.network.Log.CustomExceptionHandler;
import com.imo.network.net.EngineConst;
import com.imo.network.net.NIOThread;
import com.imo.network.net.TCPConnection;
import com.imo.util.LogFactory;

/**
 * 保持网络通信线程较高的优先级别 <br>
 * 在服务中启动网络通信线程
 * 
 * @author CaixiaoLong
 * 
 */
public class AppService extends Service {

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
			if (tcpConnection == null && Globe.canConnect) {
				if (nioThreadInstance == null) {
					nioThreadInstance = new NIOThread();
				}
				tcpConnection = 
						(TCPConnection) nioThreadInstance
						.newTCPConnection(EngineConst.IMO_CONNECTION_ID,EngineConst.IMO_SERVER_ADDRESS, true);
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

		LogFactory.e("Exception", "启动异常捕获");
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
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
				nioThreadInstance.release(EngineConst.IMO_CONNECTION_ID,true);
				nioThreadInstance.dispose();
				nioThreadInstance = null;
				tcpConnection = null;
			}
		}
	}
}
