package com.imo.network.net;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.network.Observer.PacketsObserver;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.LoginOutPacket;

public class TestActivity extends Activity implements PacketsObserver {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);

		// [Make LoginOutPacket]
		ByteBuffer bodyBuffer = LoginOutPacket.GenerateLoginBody((byte) 0, "510086", "liuxiaojun", "liuxiaojun");
		LoginOutPacket out = new LoginOutPacket(bodyBuffer, IMOCommand.IMO_LOGIN, EngineConst.cId, EngineConst.uId);
		// [End]

		NIOThread nioThreadArg = new NIOThread();
		Log.e("debug", "NIOThread Start!");

		// [Test TCP]
		IConnection connection = nioThreadArg.newTCPConnection(EngineConst.IMO_CONNECTION_ID, EngineConst.IMO_SERVER_ADDRESS, true);
		// connection.addToObserverList(this);
		IMOApp.getDataEngine().addToObserverList(this);

		nioThreadArg.send(EngineConst.IMO_CONNECTION_ID, out, false);

		// [Test HTTP]
		// nioThreadArg.newHTTPConnection("HTTP-CONNECTION_ID_1",
		// "www.baidu.com", true, this);
	}

	public boolean CanAcceptHttpPacket() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean CanAcceptPacket(int command) {
		// TODO Auto-generated method stub
		if (1002 == command)
			return true;
		return false;
	}

	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub

	}

	public void NotifyPacketArrived(String aConnectionId, short command) {
		// TODO Auto-generated method stub

	}

	public void NotifyPacketFailed(String aConnectionId, short aErrorCode) {
		// TODO Auto-generated method stub

	}

	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {
		// TODO Auto-generated method stub

	}

	public void NotifyPacketTimeOut(String aConnectionId, short aErrorCode) {
		// TODO Auto-generated method stub

	}

}