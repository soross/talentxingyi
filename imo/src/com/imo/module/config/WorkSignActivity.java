package com.imo.module.config;

import java.nio.ByteBuffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.network.net.EngineConst;
import com.imo.network.packages.EditProfileInPacket;
import com.imo.network.packages.EditProfileOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.util.DialogFactory;
import com.imo.util.LogFactory;
import com.imo.view.LinedEditText;

/**
 * 工作签名
 */
public class WorkSignActivity extends AbsBaseActivityNetListener {

	private String TAG = "WorkSign";

	private LinedEditText work_sign;

	private Button btn_del;

	// 当前的工作签名
	private String curWorkSign = "";

	private static WorkSignActivity workSignActivity;

	public static WorkSignActivity getActivity() {
		return workSignActivity;
	}

	public static void launch(Context c) {
		Intent intent = new Intent(c, WorkSignActivity.class);
		c.startActivity(intent);
	}

	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, WorkSignActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.work_sign_activity);

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.cancel), resources.getString(R.string.edit_work_sign), resources.getString(R.string.complete));

		mTitleBar.setLeftBtnListene(listener);

		mTitleBar.setRightBtnListener(listener);

		work_sign = (LinedEditText) findViewById(R.id.et_work_sign);

		btn_del = (Button) findViewById(R.id.btn_delete);

		btn_del.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				work_sign.setText("");
			}
		});

		initDataByIntent();

		dialog = DialogFactory.progressDialog(mContext, "正在提交数据，请稍后");
	}

	private void initDataByIntent() {
		Bundle data = getIntent().getExtras();
		if (data != null) {
			work_sign.setText(data.getString("sign"));
		}
		for (int i = 0; i < 7; i++) {
			work_sign.append("\n");
		}
		work_sign.setSelection(data.getString("sign").length());
	}

	@Override
	protected void registerEvents() {

		// tcpConnection.addToObserverList(this);

		workSignActivity = this;

		work_sign.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				while (work_sign.getLineCount() < 7) {
					work_sign.append("\n");
				}

				if (work_sign.getText().toString().trim().length() > 0) {
					btn_del.setVisibility(View.VISIBLE);
				} else {
					work_sign.setSelection(work_sign.getText().toString().trim().length());
					btn_del.setVisibility(View.GONE);
				}

				if (work_sign.getText().toString().trim().length() > 140) {
					needTipFlag++;
					if (needTipFlag == 1) {
						Toast.makeText(mContext, getString(R.string.work_sign_max_tip), Toast.LENGTH_LONG).show();
					}
					work_sign.setText(work_sign.getText().toString().trim().substring(0, 140));
					work_sign.setSelection(work_sign.getText().toString().trim().length());
				} else if (work_sign.getText().toString().trim().length() < 140) {
					needTipFlag = 0;
				} else {
					needTipFlag++;
					if (needTipFlag == 1) {
						Toast.makeText(mContext, getString(R.string.work_sign_max_tip), Toast.LENGTH_LONG).show();
					}
				}

				// work_sign.setSelection(work_sign.getText().toString().trim().length());
				LogFactory.d("length", "work_sign length =" + work_sign.getText().toString().trim().length());

			}
		});

	}

	private int needTipFlag = 0;

	@Override
	public void refresh(Object param) {
		// TODO Auto-generated method stub

	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {

				case R.id.btn_left:
					doCancel();

					break;
				case R.id.btn_right:

					doUpdateWorkSign();

					break;

				default:
					break;
			}

		}
	};

	private ProgressDialog dialog;

	public void showDialog() {

		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
	}

	public void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissDialog();
	}

	private Handler mHandlerUI = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// update db data
			try {
				dialog.dismiss();
				Globe.myself.setSign(curWorkSign);
				finish();

			} catch (Exception e) {
				dialog.dismiss();
				// Toast.makeText(mContext,
				// resources.getString(R.string.local_db_save_failed),
				// 1).show();
				e.printStackTrace();
			}
		}
	};

	private void doUpdateWorkSign() {

		String worksign = work_sign.getText().toString().trim();
		if (worksign.length() > 0) {
			isEmpty = false;
		} else {
			isEmpty = true;
			// Toast.makeText(mContext,
			// resources.getString(R.string.sign_empty), 1).show();
		}

		if (EngineConst.isNetworkValid && AppService.getService().getTcpConnection() != null && AppService.getService().getTcpConnection().isConnected()) {
			if (!dialog.isShowing()) {
				dialog.show();
				doRequestUpdateWorkSign(worksign);
			}
		} else {
			Toast.makeText(mContext, getResources().getString(R.string.net_connected_failed), Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 请求工作签名
	 */
	private void doRequestUpdateWorkSign(String sign) {

		this.curWorkSign = sign;

		LogFactory.d(TAG, "doRequestUpdateWorkSign   curWorkSign= " + curWorkSign + "\t length = " + curWorkSign.length());

		int mask = 1;
		mask = (mask << 4);

		ByteBuffer bufferBody = EditProfileOutPacket.GenerateEdifProfileMsgBody(mask, sign);

		EditProfileOutPacket outPacket = new EditProfileOutPacket(bufferBody, IMOCommand.IMO_EDIT_PROFILE, EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);

		// tcpConnection.addToObserverList(this);
	}

	private void doCancel() {

		finish();
	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		if (IMOCommand.IMO_EDIT_PROFILE == command) {
			return true;
		}
		return false;
	}

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {

		LogFactory.d(TAG, "command = " + command);

		super.NotifyPacketArrived(aConnectionId, command);

		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId))

			switch (command) {

				case IMOCommand.IMO_EDIT_PROFILE: {
					responseUpdateWorkSign(command);
					break;
				}

				default:
					break;
			}
	}

	private boolean isEmpty = false;

	/**
	 * 响应工作签名的更新
	 * 
	 * @param command
	 */
	private void responseUpdateWorkSign(short command) {

		LogFactory.d(TAG, "------------responseUpdateWorkSing----->");

		EditProfileInPacket inPacket = (EditProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		short commandRet = inPacket.getRet();

		LogFactory.d(TAG, "commandRet = " + commandRet);

		if (commandRet == 0) {
			if (isEmpty) {
				mHandlerUI.sendEmptyMessage(0);
			} else {
				mHandlerUI.sendEmptyMessage(0);
			}
		}
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {
		// TODO Auto-generated method stub
	}

}
