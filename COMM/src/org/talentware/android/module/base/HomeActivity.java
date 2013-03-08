package org.talentware.android.module.base;

import java.nio.ByteBuffer;

import org.talentware.android.comm.net.EngineConst;
import org.talentware.android.comm.packet.OutPacket;
import org.talentware.android.comm.packet.Packet;
import org.talentware.android.comm.packet.QuoteLinuxPackets;
import org.talentware.android.comm.packet.ServerTimePacket;
import org.talentware.android.comm.packet.command.EMCommand;
import org.talentware.android.comm.util.LogFactory;
import org.talentware.android.global.Globe;
import org.talentware.android.service.AppService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class HomeActivity extends AbsBaseActivityNetListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		waitForServiceStart();
	}

	// 刚进应用，可能service没能及时开启
	private void waitForServiceStart() {
		Handler mServiceStartCallBackHandler = new Handler() {
			public void handleMessage(Message msg) {
				LogFactory.i(HomeActivity.class.getSimpleName(), "ServiceStartSuccess!");

				Thread thread = new Thread(mNIOThread);
				if (!thread.isAlive()) {
					thread.start();
				}
				
//				ByteBuffer bodyBuffer = LoginOutPacket.GenerateLoginBody((byte)0, "4948997", "admin", "admin");
//				LoginOutPacket out = new LoginOutPacket(bodyBuffer, IMOCommand.IMO_LOGIN, 0, 0);
				
				ServerTimePacket packet = new ServerTimePacket(ServerTimePacket.GenerateServerTimeOutPacket(), EMCommand.COMM_SERVER_TIME);
				Packet[] packets = new Packet[1];
				packets[0] = packet;
				OutPacket sendpacket = new QuoteLinuxPackets(packets);
				mNIOThread.send(EngineConst.IMO_CONNECTION_ID, sendpacket, false);
			}
		};

		WaitThreadStart yStart = new WaitThreadStart(mServiceStartCallBackHandler);
		Thread thread = new Thread(yStart);
		thread.start();
	}

	private class WaitThreadStart implements Runnable {
		private Handler mHandler;

		public WaitThreadStart(Handler handler) {
			mHandler = handler;
		}

		public void run() {
			while (AppService.getService() == null) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			mNIOThread = AppService.getService().getNIOThreadInstance();

			Globe.canConnect = true;
			getTcpConnection();

			mHandler.sendEmptyMessage(0);
		}
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void installViews() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerEvents() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object param) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			mGlobal.exitApp();
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}
}
