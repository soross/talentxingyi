package com.imo.module.organize;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.contact.OuterContactBasicInfo;
import com.imo.module.contact.OuterContactItem;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;
import com.imo.module.organize.struct.NodeManager;
import com.imo.network.Log.ConnectionLog;
import com.imo.network.net.EngineConst;
import com.imo.network.packages.CommonOutPacket;
import com.imo.network.packages.ContactorGroupInPacket;
import com.imo.network.packages.ContactorGroupUCInPacket;
import com.imo.network.packages.ContactorGroupUCOutPacket;
import com.imo.network.packages.CorpMaskItem;
import com.imo.network.packages.DeptMaskItem;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.GetAllEmployeesInfoInPacket;
import com.imo.network.packages.GetAllEmployeesInfoOutPacket;
import com.imo.network.packages.GetAllEmployeesUIDInPacket;
import com.imo.network.packages.GetAllEmployeesUIDOutPacket;
import com.imo.network.packages.GetCorpInfoInPacket;
import com.imo.network.packages.GetCorpInfoOutPacket;
import com.imo.network.packages.GetDeptInfoInPacket;
import com.imo.network.packages.GetDeptInfoOutPacket;
import com.imo.network.packages.GetDeptUCInPacket;
import com.imo.network.packages.GetDeptUCOutPacket;
import com.imo.network.packages.GetEmployeeProfileInPacket;
import com.imo.network.packages.GetEmployeeProfileOutPacket;
import com.imo.network.packages.GetEmployeesStatusInPacket;
import com.imo.network.packages.GetEmployeesStatusOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.InnerContactorItem;
import com.imo.network.packages.InnerContactorListInPacket;
import com.imo.network.packages.OuterBasicInfoInPacket;
import com.imo.network.packages.OuterBasicInfoOutPacket;
import com.imo.network.packages.OuterContactorItem;
import com.imo.network.packages.OuterContactorListInPacket;
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;

/**
 * ������º󣬱��ؼ������ݹ�����֯�ܹ�����
 * 
 * <br>
 * ��Ҫ�߼������˳�3����� <br>
 * 1-AddDept <br>
 * 2-DelDept <br>
 * 3-UpdateDept<br>
 */
public class NormalLoadingActivity extends AbsBaseActivityNetListener {

	private String TAG = "NormalLoading";
	private String TAGUC = "NormalLoading-UC";

	private boolean stopLoad = false;

	public static void launch(Context c) {
		Intent intent = new Intent(c, NormalLoadingActivity.class);
		c.startActivity(intent);
	}

	private View loading;
	private TextView tip;
	private Button btn_close;

	@Override
	protected void installViews() {

		stopLoad = false;

		setContentView(R.layout.loading_data_activity);

		View root = findViewById(R.id.root);
		root.setBackgroundDrawable(new BitmapDrawable(getResources().openRawResource(R.drawable.loading_bg)));

		loading = findViewById(R.id.loading);
		tip = (TextView) findViewById(R.id.tip);
		tip.setText(R.string.loading);
		btn_close = (Button) findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stopLoad = true;
				EngineConst.isLoginSuccess = false;
				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
				IMOApp.getApp().turn2LoginForLogout();
			}
		});
	}

	int n = 0;

	private Handler loadingHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
				case 0: {
					Drawable localDrawable = loading.getBackground();
					if (n > 10000) {
						n -= 10000;
					} else {
						n += 160;
					}

					localDrawable.setLevel(n);
					if (!isFinishing()) {
						loadingHandler.sendEmptyMessage(0);
					}
				}
					break;
				default:
					break;
			}
		};
	};

	@Override
	protected void registerEvents() {
		loadingHandler.sendEmptyMessage(0);

		LogFactory.d(TAG, "-------------------------> Create");
		if (AppService.getService() != null) {
			resetGroupMap();
			beginLoading();
			// beginLoadingContact();
		} else {
			super.finish();
		}

	}

	/**
	 * ���Ĺ�������ʱ��Ĳ����������ݿ��ж�ȡ���е����ݡ�
	 */
	private void loadDBDataSource() {

		try {
			// ArrayList<int[]> deptUCList = new ArrayList<int[]>();

			deptInfoMap = new HashMap<Integer, DeptMaskItem>();

			deptUserIdsMap = new HashMap<Integer, int[]>();
			deptUserNextSiblingMap = new HashMap<Integer, int[]>();

			deptUserInfoMap = new HashMap<Integer, HashMap<Integer, EmployeeInfoItem>>();

			int[][] temp_deptUC = IMOApp.imoStorage.get(deptInfoMap, deptUserIdsMap, deptUserNextSiblingMap, deptUserInfoMap);

			if (temp_deptUC != null) {

				deptid = temp_deptUC[0];
				dept_uc = temp_deptUC[1];
				dept_user_uc = temp_deptUC[2];
			}
			LogFactory.d(TAG, "deptId = " + Arrays.toString(deptid));
			LogFactory.d(TAG, "dept_uc = " + Arrays.toString(dept_uc));
			LogFactory.d(TAG, "dept_user_uc = " + Arrays.toString(dept_user_uc));
			LogFactory.d(TAG, "deptInfoMap size = " + deptInfoMap.keySet().size());
			LogFactory.d(TAG, "deptUserIdsMap size = " + deptUserIdsMap.size());

		} catch (Exception e) {

			Toast.makeText(mContext, "LocalDB read Failed", 1).show();
			e.printStackTrace();
		}
	}

	ArrayList<Integer> needDelLocalDept = new ArrayList<Integer>();

	ArrayList<Integer> needUpdateLocalDept = new ArrayList<Integer>();
	ArrayList<Integer> needUpdateLocalDeptUC = new ArrayList<Integer>();

	ArrayList<Integer> needUpdateLocalDeptUser = new ArrayList<Integer>();

	ArrayList<Integer> needAddLocalDept = new ArrayList<Integer>();

	/**
	 * ʵ�ֹ��˱Ƚ� �õ���Ҫ <br>
	 * ɾ�������£���ӵ�DeptId
	 */
	private void doFilter(int[] local_deptid, int[] local_dept_uc, int[] local_dept_user_uc) {
		needDelLocalDept.clear();
		needUpdateLocalDept.clear();
		needUpdateLocalDeptUC.clear();
		needUpdateLocalDeptUser.clear();
		needAddLocalDept.clear();

		int[] temp_deptId = (int[]) local_dept_uc.clone();

		if (temp_deptId != null) {
			for (int i = 0; i < deptid.length; i++) {

				for (int j = 0; j < local_deptid.length; j++) {

					if (deptid[i] == local_deptid[j]) {

						if (dept_uc[i] != local_dept_uc[j]) {
							needUpdateLocalDept.add(deptid[i]);
							LogFactory.e(TAG, "needUpdateLocalDept size = " + needUpdateLocalDept.size() + ",deptid = " + deptid[i]);
							needUpdateLocalDeptUC.add(dept_uc[i]);
							LogFactory.e(TAG, "needUpdateLocalDeptUC size = " + needUpdateLocalDeptUC.size() + ",dept_uc = " + dept_uc[i]);
						}

						if (dept_user_uc[i] != local_dept_user_uc[j]) {
							needUpdateLocalDeptUser.add(deptid[i]); // ��Ҫ����Ա���Ĳ��š�
							LogFactory.e(TAG, "needUpdateLocalDeptUser size = " + needUpdateLocalDeptUser.size() + ",deptid = " + deptid[i]);
						}

						temp_deptId[j] = -1; // ���˳���Ҫɾ���ġ�

						break;
					}

					if (j == local_deptid.length - 1) {
						needAddLocalDept.add(deptid[i]); // �����ӵ�

						LogFactory.e(TAG, "needAddLocalDept size = " + needAddLocalDept.size() + ",deptid = " + deptid[i]);

						needUpdateLocalDept.add(deptid[i]); // ��Ҫ���µĲ���
						LogFactory.e(TAG, "needUpdateLocalDept size = " + needUpdateLocalDept.size() + ",deptid = " + deptid[i]);
						needUpdateLocalDeptUC.add(dept_uc[i]);
						LogFactory.e(TAG, "needUpdateLocalDeptUC size = " + needUpdateLocalDeptUC.size() + ",dept_uc = " + dept_uc[i]);
					}
				}
			}

			for (int i = 0; i < temp_deptId.length; i++) {
				if (temp_deptId[i] != -1) {
					needDelLocalDept.add(temp_deptId[i]);
				}
			}

			LogFactory.d(TAG + "doFilter", "needAddLocalDept = " + needAddLocalDept.toString());
			LogFactory.d(TAG + "doFilter", "needUpdateLocalDept = " + needUpdateLocalDept.toString());
			LogFactory.d(TAG + "doFilter", "needDelLocalDept = " + needDelLocalDept.toString());

			LogFactory.d(TAG, "needUpdateLocalDeptUser = " + needUpdateLocalDeptUser.toString());
		}
	}

	/**
	 * ������ǰ��Ҫ���µĲ���self info
	 */
	private boolean buildUpdateDeptInfo() {

		if (needUpdateLocalDept.size() == 0) {

			LogFactory.d(TAG, "update===dept self not need update.");
			return false;
		}

		LogFactory.d(TAG, "update===dept self need update.");

		this.deptid = new int[needUpdateLocalDept.size()];
		this.dept_uc = new int[needUpdateLocalDept.size()];

		for (int i = 0; i < needUpdateLocalDept.size(); i++) {

			this.deptid[i] = needUpdateLocalDept.get(i);
			this.dept_uc[i] = needUpdateLocalDeptUC.get(i);
		}

		return true;
	}

	private void doFilter() {

		// ����ǰ����mPosλ��
		this.mPos = 0;

		LogFactory.d(TAG, "Begin: Get Local UC.");

		try {
			int[][] temp_deptUC = IMOApp.imoStorage.getDeptAndUserUC();
			LogFactory.d(TAG, "Remote--UC = " + Arrays.toString(new_dept_uc));
			LogFactory.d(TAG, "Local--UC = " + Arrays.toString(temp_deptUC[1]));

			doFilter(temp_deptUC[0], temp_deptUC[1], temp_deptUC[2]);

			LogFactory.d(TAG, "Get Local UC Success.");

		} catch (Exception e) {

			LogFactory.d(TAG, "Get Local UC failed.");
			e.printStackTrace();
		}

		if (buildUpdateDeptInfo()) {
			// DeptInfo ��Ҫ����
			LogFactory.d(TAG, "Update DeptInfo deptId = " + needUpdateLocalDept.toString());

			// ��ʼ��������map
			for (int i = 0; i < this.deptid.length; i++) {
				dept_uc_map.put(this.deptid[i], this.dept_uc[i]);
				dept_user_uc_map.put(this.deptid[i], this.dept_user_uc[i]);
				dept_loadinfo_map.put(this.deptid[i], LOAD_STAT.NOT_LOAD);
				dept_loaduids_map.put(this.deptid[i], LOAD_STAT.NOT_LOAD);
				LogFactory.e(TAG, "------> dept_uc_map size=" + dept_uc_map.size() + ", dept_loadinfo_map size=" + dept_loadinfo_map.size() + ", dept_loaduids_map size=" + dept_loaduids_map.size());
			}

			// ��ʼ��������deptinfo
			do_concurrent_dept_info_req();
			// ��ʼ��������deptuids
			do_concurrent_dept_uids_req();
		} else {
			// DeptInfo ����Ҫ����
			LogFactory.d(TAG, "Nothing need update");

			// ����ȫ������
			updateGlobalData();

		}

	}

	/**********************************************************/
	// ����������ڿ��Ʋ������Ĳ�������
	private int concurrent_deptinfo_loadreq_limit = EngineConst.CONCURRENT_MAX_VALUE;
	private int concurrent_deptinfo_loading_cnt = 0;
	private int concurrent_deptuids_loadreq_limit = EngineConst.CONCURRENT_MAX_VALUE;
	private int concurrent_deptuids_loading_cnt = 0;
	private int concurrent_dept_employeeinfo_loading_cnt = 0;

	private int concurrent_request_status_limit = EngineConst.CONCURRENT_MAX_VALUE;

	// TODO: Review info: add for concurrent data load --reviewed by davidfan
	// 2012-may-24
	// dept_loadinfo_map ��ʶ��ǰ����deptid��deptinfo�Ļ�ȡ�������δ���룬�����У�������3��״̬
	private Map<Integer, Integer> dept_loadinfo_map = new HashMap<Integer, Integer>();
	// dept_loadinfo_map ��ʶ��ǰ����deptid��uids��Ա�б�Ļ�ȡ�������δ���룬�����У�������3��״̬
	private Map<Integer, Integer> dept_loaduids_map = new HashMap<Integer, Integer>();

	private Map<Integer, EmployeeInfoItem[]> dept_EmployeeInfo_map = new HashMap<Integer, EmployeeInfoItem[]>();

	// �洢dept ucֵ
	private Map<Integer, Integer> dept_uc_map = new HashMap<Integer, Integer>();
	// �洢dept user ucֵ
	private Map<Integer, Integer> dept_user_uc_map = new HashMap<Integer, Integer>();
	// ��ʾdept_loadinfo����������������յ�endflag����ΪLOADED������ΪLOADING
	private Map<Integer, Integer> dept_loadinfo_pack_map = new HashMap<Integer, Integer>();
	// ��ʾdept_loaduids����������������յ�endflag����ΪLOADED������ΪLOADING
	private Map<Integer, Integer> dept_loaduids_pack_map = new HashMap<Integer, Integer>();
	// ��ʾdept_employeeinfo����������������յ�endflag����ΪLOADED������ΪLOADING
	private Map<Integer, Integer> dept_loademployeeinfo_pack_map = new HashMap<Integer, Integer>();
	// ��ʾÿһ��uid��info��������������ΪLOADED������ΪLOADING
	private Map<Integer, Integer> dept_uid_loademployeeinfo_map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> uid_dept_map = new HashMap<Integer, Integer>();

	// ���漸��map���Ժϲ�������Ϥjavaд�����Ժ����Ż�

	private class LOAD_STAT {
		static final int NOT_LOAD = 0;
		static final int LOADING = 1;
		static final int LOADED = 2;
		static final int LOAD_FAIL = 3;
	}

	int dept_uc_load = LOAD_STAT.NOT_LOAD;
	int dept_info_load = LOAD_STAT.NOT_LOAD;
	int dept_uids_load = LOAD_STAT.NOT_LOAD;

	int employee_info_load = LOAD_STAT.NOT_LOAD;

	/******************************************************************************
	 * FirstLoadingActivity.do_concurrent_dept_info_req - ������deptinfo
	 * get���󣬲����ط�������ĸ��� DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int do_concurrent_dept_info_req() {
		// ��ʼ��������deptinfo
		int init_cnt = concurrent_deptinfo_loading_cnt;
		for (int i = 0; i < this.deptid.length && concurrent_deptinfo_loading_cnt < concurrent_deptinfo_loadreq_limit; i++) {
			if (dept_loadinfo_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD) {
				doRequestDeptInfo(this.deptid[i], dept_uc_map.get(this.deptid[i]));
				dept_loadinfo_map.put(this.deptid[i], LOAD_STAT.LOADING);
				concurrent_deptinfo_loading_cnt++;
			} else {}
		}

		return concurrent_deptinfo_loading_cnt - init_cnt;
	}

	/******************************************************************************
	 * FirstLoadingActivity.do_concurrent_dept_uids_req - ������deptuids
	 * get���󣬲����ط�������ĸ��� DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int do_concurrent_dept_uids_req() {
		// ��ʼ��������deptuids
		int init_cnt = concurrent_deptuids_loading_cnt;
		for (int i = 0; i < this.deptid.length && concurrent_deptuids_loading_cnt < concurrent_deptuids_loadreq_limit; i++) {
			if (dept_loaduids_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD) {
				doRequestAllEmployeeUid(this.deptid[i]);
				dept_loaduids_map.put(this.deptid[i], LOAD_STAT.LOADING);
				concurrent_deptuids_loading_cnt++;
			}
		}

		return concurrent_deptuids_loading_cnt - init_cnt;
	}

	/******************************************************************************
	 * FirstLoadingActivity.do_concurrent_dept_emyployeeinfo_req - ������dept
	 * employee get���󣬲����ط�������ĸ��� DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int do_concurrent_dept_emyployeeinfo_req(int deptid) {
		if (null == deptUserIdsMap.get(deptid)) {
			LogFactory.d(TAG, "------> do_concurrent_dept_emyployeeinfo_req fail deptUserIdsMap get(deptid) null deptid:" + deptid + ", loadingcnt:" + concurrent_dept_employeeinfo_loading_cnt);
			return 0;
		}

		int req_cnt = 0;
		int req_num = 0;
		int[] req_uids = new int[50];
		int[] deptuids = deptUserIdsMap.get(deptid);
		for (int i = 0; i < deptuids.length; i++) {
			if (null == dept_uid_loademployeeinfo_map.get(deptuids[i])) {
				req_uids[req_num++] = deptuids[i];
				dept_uid_loademployeeinfo_map.put(deptuids[i], LOAD_STAT.LOADING);
				uid_dept_map.put(deptuids[i], deptid);
			}

			if (req_num == USERINFO_REQUEST_MAX_COUNT) {
				doRequestAllEmployeeInfo(req_num, req_uids);
				req_cnt++;
				req_num = 0;
			}
		}

		if (req_num != 0) {
			doRequestAllEmployeeInfo(req_num, req_uids);
			req_cnt++;
		}
		concurrent_dept_employeeinfo_loading_cnt += req_cnt;
		LogFactory.d(TAG, "------> do_concurrent_dept_emyployeeinfo_req deptid:" + deptid + "," + ", req_cnt:" + req_cnt + ", loading_cnt:" + concurrent_dept_employeeinfo_loading_cnt);
		return req_cnt;
	}

	/******************************************************************************
	 * FirstLoadingActivity.check_dept_info_req_finish - ����Ƿ����е�deptinfo��������
	 * DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int check_dept_info_req_finish() {
		for (int i = 0; i < this.deptid.length && concurrent_deptinfo_loading_cnt < concurrent_deptinfo_loadreq_limit; i++) {
			if (dept_loadinfo_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD || dept_loadinfo_map.get(this.deptid[i]) == LOAD_STAT.LOADING) {
				return 0;
			}
		}

		// ����deptinfo���������ɣ��˴�����������ʹ�õ��ĸ���map
		dept_info_load = LOAD_STAT.LOADED;
		dept_loadinfo_map.clear();
		dept_loadinfo_pack_map.clear();

		// ��ʼ�洢���ݵ�DB��ȥ
		if (dept_info_load == LOAD_STAT.LOADED && employee_info_load == LOAD_STAT.LOADED)
			doSaveData2DB();

		return 1;
	}

	/******************************************************************************
	 * FirstLoadingActivity.check_dept_uids_req_finish - ����Ƿ�����depot uids����������
	 * DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int check_dept_uids_req_finish() {
		for (int i = 0; i < this.deptid.length && concurrent_deptuids_loading_cnt < concurrent_deptuids_loadreq_limit; i++) {

			if (dept_loaduids_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD || dept_loaduids_map.get(this.deptid[i]) == LOAD_STAT.LOADING) {
				return 0;
			}
		}

		// ����deptinfo���������ɣ��˴�����������ʹ�õ��ĸ���map
		dept_uids_load = LOAD_STAT.LOADED;
		dept_loaduids_map.clear();
		dept_loaduids_pack_map.clear();

		LogFactory.e("check_dept_uids_req_finish", "over");

		return 1;
	}

	private int check_employeeinfo_req_finish() {
		if (concurrent_dept_employeeinfo_loading_cnt == 0) {
			LogFactory.e("check_employeeinfo_req_finish", "over");
			return 1;
		}
		return 0;
	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		if (IMOCommand.IMO_GET_DEPT_UC == command || IMOCommand.IMO_GET_DEPT_INFO == command || IMOCommand.IMO_GET_ALL_EMPLOYEE_UID == command || IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO == command || IMOCommand.IMO_GET_EMPLOYEE_STATUS == command || IMOCommand.IMO_UPDATE_STATUS == command
				|| IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC == command || IMOCommand.IMO_INNER_CONTACTOR_LIST_UC == command || IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC == command || IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC == command || IMOCommand.IMO_INNER_CONTACTOR_GROUP == command
				|| IMOCommand.IMO_INNER_CONTACTOR_LIST == command || IMOCommand.IMO_OUTER_CONTACTOR_GROUP == command || IMOCommand.IMO_OUTER_CONTACTOR_LIST == command || IMOCommand.IMO_OUTER_BASIC_INFO == command || IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO == command
				|| IMOCommand.IMO_GET_EMPLOYEE_PROFILE == command || IMOCommand.IMO_GET_EMPLOYEE_STATUS == command || IMOCommand.IMO_GET_CORP_INFO == command)
			return true;

		return false;
	}

	public Handler mCommandHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			short command = (short) msg.arg1;

			switch (command) {

				// 1-��ò���UC
				case IMOCommand.IMO_GET_DEPT_UC: {
					responseDeptUC(command);
					break;
				}
					// 2-��ò���Info
				case IMOCommand.IMO_GET_DEPT_INFO: {
					responseDeptInfo(command);
					break;
				}

					// 3-��ȡ���е�Ա��UID
				case IMOCommand.IMO_GET_ALL_EMPLOYEE_UID:
					responseAllEmployeeUid(command);
					break;
				// 4-��ȡ���е�Ա��������Ϣ
				case IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO:
					responseAllEmployeeInfo(command);
					break;
				// 5-��ȡԱ����״̬
				case IMOCommand.IMO_GET_EMPLOYEE_STATUS:
					if (forOrganize) {
						responseEmployeeState(command);
					} else {
						responseOuterContactState(command);
					}
					break;
				case IMOCommand.IMO_UPDATE_STATUS:
					break;

				case IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC:
					responseInnerGroupUC(command);
					break;
				case IMOCommand.IMO_INNER_CONTACTOR_LIST_UC:
					responseInnerContactUC(command);
					break;
				case IMOCommand.IMO_INNER_CONTACTOR_GROUP:
					responseInnerGroupInfo(command);
					break;
				case IMOCommand.IMO_INNER_CONTACTOR_LIST:
					responseInnerContactId(command);
					break;
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE:
					responseInnerContactWorkSign(command);
					break;
				case IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC:
					responseOuterContactGroupUC(command);
					break;
				case IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC:
					responseOuterContactUC(command);
					break;
				case IMOCommand.IMO_OUTER_CONTACTOR_GROUP:
					responseOuterGroupInfo(command);
					break;
				case IMOCommand.IMO_OUTER_CONTACTOR_LIST:
					responseOuterContactId(command);
					break;
				case IMOCommand.IMO_OUTER_BASIC_INFO:
					responseOuterBasicInfo(command);
					break;
				case IMOCommand.IMO_GET_CORP_INFO:
					responseOuterCorpInfo(command);
					break;
				default:
					break;
			}
		}
	};
	// ================ʵ��ѭ����ȥ����������=====================
	// ProgressDialog dialog = null;
	int mPos = 0;

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {
		super.NotifyPacketArrived(aConnectionId, command);

		if (stopLoad)
			return;

		Message msg1 = new Message();
		msg1.arg1 = command;
		mCommandHandler.sendMessage(msg1);

	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object param) {
		// //������ϵ������
		beginLoadingContact();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogFactory.d(TAG, "NormalLoadingActivity has destory.");
	}

	/**
	 * ��ʼ��������
	 */
	private void beginLoading() {
		// ��ȡ����������UC
		doRequestDeptUC();
	}

	/**
	 * ״̬��������ܴ���
	 */
	private int stateResponseCount = 0;
	// /ͳ��ȫ�����Ƿ���ȫ����
	private int stateResponseCountEx = 0;

	private int hasConcurrentSended = 0;

	/**
	 * ����Ա��״̬
	 */
	private void updateUserState() {

		System.out.println("updateUserState-----");

		stateResponseCount = 0;

		for (hasConcurrentSended = 0; hasRequestCount < mStateRequestCount && hasConcurrentSended < concurrent_request_status_limit; hasConcurrentSended++) {
			Object[] idArrays = getRequestUserIdArray();
			LogFactory.d("SendUid", Arrays.toString((int[]) idArrays[1]));
			LogFactory.e("hasConcurrentSended", "hasConcurrentSended :" + hasConcurrentSended);
			doRequestEmployeeState(((int[]) idArrays[0]).length, ((int[]) idArrays[0]), ((int[]) idArrays[1]));
		}
	}

	/**
	 * ��ʼ������״̬����Ĵ���
	 */
	private void initAllStateRequestCount() {
		mStateRequestCount = initUserStateRequest(deptUserIdsMap);
		LogFactory.d(TAG + "StateCount", "Total mStateRequestCount = " + mStateRequestCount);
	}

	/**
	 * ״̬��������ID
	 * 
	 * @return
	 */
	private Object[] getRequestUserIdArray() {

		int[] requestCIds = null;
		int[] requestUIds = null;

		// һ����Ҳû�е����
		if (mStateRequestCount == 0) {
			return null;
		}

		if (mStateRequestCount - 1 > hasRequestCount) {

			requestCIds = new int[MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[MAX_STATE_REQUEST_COUNT];

			LogFactory.d(TAG + "State--HasRequestCount", "hasRequestCount = " + hasRequestCount);

			System.arraycopy(mAllCIds, hasRequestCount * MAX_STATE_REQUEST_COUNT, requestCIds, 0, MAX_STATE_REQUEST_COUNT);

			// TODO bug�޸ĵ㣬 �ڳ��ȴ��ڵ�����£� ˵�� ���µ�״̬��������Ҫ����
			if (mAllUserIds.length > hasRequestCount * MAX_STATE_REQUEST_COUNT) {

				System.arraycopy(mAllUserIds, hasRequestCount * MAX_STATE_REQUEST_COUNT, requestUIds, 0, MAX_STATE_REQUEST_COUNT);
			}

			this.hasRequestCount++;

		} else {
			LogFactory.d(TAG + "StateHasRequestCount", " State has Request Completed !! hasRequestCount = " + hasRequestCount);

			requestCIds = new int[mAllUserIds.length - hasRequestCount * MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[mAllUserIds.length - hasRequestCount * MAX_STATE_REQUEST_COUNT];

			System.arraycopy(mAllCIds, hasRequestCount * MAX_STATE_REQUEST_COUNT, requestCIds, 0, requestUIds.length);

			// TODO bug�޸ĵ㣬 �ڳ��ȴ��ڵ�����£� ˵�� ���µ�״̬��������Ҫ����
			if (mAllUserIds.length > hasRequestCount * MAX_STATE_REQUEST_COUNT) {
				System.arraycopy(mAllUserIds, hasRequestCount * MAX_STATE_REQUEST_COUNT, requestUIds, 0, requestUIds.length);

			}

			// ������ɺ���Ҫ����
			// this.hasRequestCount = 0;
			this.hasRequestCount++;

		}
		LogFactory.d("hasRequestCount", "state hasRequestCount = " + hasRequestCount);
		return new Object[] {
				requestCIds, requestUIds
		};
	}

	private ActivityManager mActivityManager = null;

	// ���ϵͳ�����ڴ���Ϣ
	private String getSystemAvaialbeMemorySize() {
		// ���ActivityManager����Ķ���
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		// ���MemoryInfo����
		MemoryInfo memoryInfo = new MemoryInfo();
		// ���ϵͳ�����ڴ棬������MemoryInfo������
		mActivityManager.getMemoryInfo(memoryInfo);
		long memSize = memoryInfo.availMem;

		// �ַ�����ת��
		String availMemStr = formateFileSize(memSize);

		return availMemStr;
	}

	// ����ϵͳ�������ַ���ת�� long -String KB/MB
	private String formateFileSize(long size) {
		return Formatter.formatFileSize(NormalLoadingActivity.this, size);
	}

	/**
	 * 1-����DeptUC�����
	 */
	private void doRequestDeptUC() {

		LogFactory.e(TAG, "getSystemAvaialbeMemorySize*******" + getSystemAvaialbeMemorySize());

		GetDeptUCOutPacket outPacket = new GetDeptUCOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_GET_DEPT_UC, EngineConst.cId, EngineConst.uId);

		// if (!isNewConnection || mNIOThread != null) {
		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			IMOApp.getDataEngine().addToObserverList(this);
		} else {
			finish();
		}

	}

	/**
	 * 2-����DeptInfo�����
	 */
	private void doRequestDeptInfo(int deptid, int dept_uc) {
		// ����DeptInfo
		LogFactory.d(TAG, "doRequestDeptInfo :[ deptid = " + deptid + "\t dept_uc = " + dept_uc + "]");

		int mask = 1;
		mask |= (mask << 1);
		mask |= (mask << 2);
		mask |= (mask << 3);
		mask |= (mask << 4);
		mask |= (mask << 5);
		mask |= (mask << 6);
		mask |= (mask << 7);
		mask |= (mask << 8);
		mask |= (mask << 9);
		mask |= (mask << 10);
		mask |= (mask << 11);
		mask |= (mask << 12);
		mask |= (mask << 13);

		ByteBuffer bufferBody = GetDeptInfoOutPacket.GenerateDeptInfoBody(deptid, dept_uc, mask);

		GetDeptInfoOutPacket outPacket = new GetDeptInfoOutPacket(bufferBody, IMOCommand.IMO_GET_DEPT_INFO, EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
		}
	}

	/**
	 * 3-����AllEmployeeUid������� ��ò����µ�����Ա����UId
	 * 
	 * @param aDeptID
	 *        ����
	 */
	private void doRequestAllEmployeeUid(int aDeptID) {

		LogFactory.d(TAG, "doRequestAllEmployeeUid  deptId = " + aDeptID);
		ByteBuffer bufferBody = GetAllEmployeesUIDOutPacket.GenerateEmplyeesUIDBody(aDeptID);
		GetAllEmployeesUIDOutPacket outPacket = new GetAllEmployeesUIDOutPacket(bufferBody, IMOCommand.IMO_GET_ALL_EMPLOYEE_UID, EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
		}
	}

	private int hasRequestCount = 0; // ��Ҫ����Ĵ���

	private int USERINFO_REQUEST_MAX_COUNT = 20; // Ա����Ϣ�������������ĸ���

	private void doRequestAllEmployeeInfo(int aContactorsNum, int[] aContactorUidArray) {

		LogFactory.d(TAG + "2", "doRequestAllEmployeeInfo : uids = " + Arrays.toString(aContactorUidArray) + ",num = " + aContactorsNum);

		ByteBuffer bufferBody = GetAllEmployeesInfoOutPacket.GenerateEmployeesBasicInfoBody(aContactorsNum, aContactorUidArray);

		LogFactory.d(TAG + "2", "bufferBody = " + bufferBody.toString());

		GetAllEmployeesInfoOutPacket outPacket = new GetAllEmployeesInfoOutPacket(bufferBody, IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO, EngineConst.cId, EngineConst.uId);

		int header_seq = outPacket.get_header_seq();

		dept_loademployeeinfo_pack_map.put(header_seq, LOAD_STAT.LOADING);
		LogFactory.d(TAG, "doRequestAllEmployeeInfo :[ aContactorsNum = " + aContactorsNum + ", aContactorUidArray = " + Arrays.toString(aContactorUidArray) + "]" + ", seq=" + header_seq);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	private boolean forOrganize = true;

	private int mStateRequestCount = 0;

	/**
	 * 5-����AllEmployeeInfo�����
	 * 
	 * @param aContactorsNum
	 *        �����Ա������ ����
	 * @param aContactorCidArray
	 *        �ڲ���ϵ�� ���ⲿ��ϵ�� CidArray
	 * @param aContactorUidArray
	 *        �û� UIdArray
	 * 
	 */
	private void doRequestEmployeeState(int aContactorsNum, int[] aContactorCidArray, int[] aContactorUidArray) {

		if (stopLoad) {
			return;
		}

		LogFactory.d(TAG, "doRequestEmployeeState ");

		ByteBuffer bufferBody = GetEmployeesStatusOutPacket.GenerateEmployeesStatusBody(aContactorsNum, aContactorCidArray, aContactorUidArray);
		GetEmployeesStatusOutPacket outPacket = new GetEmployeesStatusOutPacket(bufferBody, IMOCommand.IMO_GET_EMPLOYEE_STATUS, EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
		}
	}

	/**
	 * �����ݱ��浽���ݿ���ȥ
	 * 
	 * @throws Exception
	 */
	private void doSaveData2DB() {

		try {
			// �õ����ڸ�������ʵ������
			needUpdateLocalDept.removeAll(needAddLocalDept);

			LogFactory.d(TAG, "Begin to Save ");

			needUpdateLocalDeptUser.clear();
			needUpdateLocalDeptUser.addAll(needAddLocalDept);
			needUpdateLocalDeptUser.addAll(needUpdateLocalDept);

			new DataOperatorTask(true).execute("");

		} catch (Exception e) {

			LogFactory.d("DB", "Exception: Update Db data failed .");

			LoginActivity.launch(mContext);
			this.finish();

			e.printStackTrace();
		}

	}

	/**
	 * ����ȫ������ <br>
	 * �����ݿ��ж�ȡ���ݣ�����ȫ������
	 */
	private void updateGlobalData() {
		new DataOperatorTask().execute("");
	}

	private final int TYPE_DB_SAVE_FAILED = 2;
	private final int TYPE_SEND_STATE_PACKAGE = 3;
	/**
	 * 1-״̬���� 2-���ݿ�洢ʧ��
	 */
	private Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TYPE_SEND_STATE_PACKAGE:

					break;
				case TYPE_DB_SAVE_FAILED:
					stopLoad = true;
					EngineConst.isLoginSuccess = false;
					DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
					IMOApp.getApp().turn2LoginForLogout();
					break;

				default:
					break;
			}
		};
	};

	/**
	 * ���ݴ洢����
	 */
	class DataOperatorTask extends AsyncTask<String, Integer, Boolean> {

		private boolean needUpdate = false;

		public DataOperatorTask() {

		}

		public DataOperatorTask(boolean needUpdate) {
			this.needUpdate = needUpdate;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				if (needUpdate) {
					System.out.println("before needAddLocalDept size = " + needAddLocalDept.size() + ", needDelLocalDept = " + needDelLocalDept.size() + ", needUpdateLocalDept = " + needUpdateLocalDept.size() + ", needUpdateLocalDeptUser = " + needUpdateLocalDeptUser.size() + ", deptInfoMap = "
							+ deptInfoMap.keySet().size() + ", deptUserIdsMap size = " + deptUserIdsMap.size() + ", deptUserNextSiblingMap size = " + deptUserNextSiblingMap.size() + ", deptUserInfoMap size = " + deptUserInfoMap.size());

					IMOApp.imoStorage.update(new_deptid, new_dept_uc, new_dept_user_uc, needAddLocalDept, needDelLocalDept, needUpdateLocalDept, needUpdateLocalDeptUser, deptInfoMap, deptUserIdsMap, deptUserNextSiblingMap, deptUserInfoMap);
				}
			} catch (Exception e) {
				if (IMOApp.getApp().getAppMode()) {
					e.printStackTrace();
				}
				return false;
			}

			try {
				loadDBDataSource();

				buildData();

				mGlobal.updateData((int[]) deptid.clone(), (int[]) dept_uc.clone(), (int[]) dept_user_uc.clone(), (HashMap<Integer, DeptMaskItem>) deptInfoMap.clone(), (HashMap<Integer, int[]>) deptUserIdsMap.clone(), (HashMap<Integer, int[]>) deptUserNextSiblingMap.clone(),
						(HashMap<Integer, HashMap<Integer, Integer>>) deptUserSiblingMap.clone(), (HashMap<Integer, HashMap<Integer, EmployeeInfoItem>>) deptUserInfoMap.clone());

				initAllStateRequestCount();

				updateUserState();
				return true;
			} catch (Exception e) {
				if (IMOApp.getApp().getAppMode()) {
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				// mUIHandler.sendEmptyMessage(TYPE_REQUEST_USER_STATE);
			} else {
				mUIHandler.sendEmptyMessage(TYPE_DB_SAVE_FAILED);
			}

		}

	}

	// ======================������Ӧ==========================================

	// ===============����UC���Ӧ �ֶ�===============================
	private int[] dept_uc = null;
	private int[] deptid = null;
	private int[] dept_user_uc = null;

	private Vector<Integer> deptid_vector = new Vector<Integer>();
	private Vector<Integer> dept_uc_vector = new Vector<Integer>();
	private Vector<Integer> dept_user_uc_vector = new Vector<Integer>();

	private int[] new_deptid = null;
	private int[] new_dept_uc = null;
	private int[] new_dept_user_uc = null;

	/**
	 * <1>��Ӧ ����UC ����
	 * 
	 * @param command
	 */
	private void responseDeptUC(short command) {

		GetDeptUCInPacket inPacket = (GetDeptUCInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (inPacket == null)
			return;

		short commandRet = inPacket.getCommandRet();

		LogFactory.d(TAG, "commandRet = " + commandRet);

		if (commandRet == 0) {

			byte endflag = inPacket.getEndflag();

			int[] temp_deptid = inPacket.getDeptid();
			int[] temp_dept_uc = inPacket.getDept_uc();
			int[] temp_dept_user_uc = inPacket.getDept_user_uc();

			for (int i = 0; i < temp_deptid.length; i++) {
				deptid_vector.add(new Integer(temp_deptid[i]));
			}

			for (int i = 0; i < temp_dept_uc.length; i++) {
				dept_uc_vector.add(new Integer(temp_dept_uc[i]));
			}

			for (int i = 0; i < temp_dept_user_uc.length; i++) {
				dept_user_uc_vector.add(new Integer(temp_dept_user_uc[i]));
			}

			// ������Ҫ����Ĵ���
			if (endflag == 1) {
				deptid = new int[deptid_vector.size()];
				dept_uc = new int[dept_uc_vector.size()];
				dept_user_uc = new int[dept_user_uc_vector.size()];

				for (int i = 0; i < deptid.length; i++) {
					deptid[i] = deptid_vector.elementAt(i);
				}
				for (int i = 0; i < dept_uc.length; i++) {
					dept_uc[i] = dept_uc_vector.elementAt(i);
				}
				for (int i = 0; i < dept_user_uc.length; i++) {
					dept_user_uc[i] = dept_user_uc_vector.elementAt(i);
				}

				new_deptid = (int[]) deptid.clone();
				new_dept_uc = (int[]) dept_uc.clone();
				new_dept_user_uc = (int[]) dept_user_uc.clone();

				LogFactory.d(TAG, "------> ���µ�DeptUC ��ȡ�ɹ� \t  TotalDeptCount=" + deptid.length);

				// �����ʱ����Ҫ���Ĺ����ǣ�ȥ�Ƚϣ��õ���Щ�� add,update,del
				doFilter();
			}
		}
	}

	// ===============����Info���Ӧ �ֶ�===============================

	private HashMap<Integer, DeptMaskItem> deptInfoMap = new HashMap<Integer, DeptMaskItem>();

	/**
	 * ��Ӧ ����Info ����
	 * 
	 * @param command
	 */
	private void responseDeptInfo(short command) {

		GetDeptInfoInPacket inPacket = (GetDeptInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (inPacket == null)
			return;

		DeptMaskItem mDeptMaskItem = inPacket.getMaskItem();

		LogFactory.d(TAG, " mDeptMaskItem =" + mDeptMaskItem.toString());
		ConnectionLog.MusicLogInstance().addLog("NormalLoadingActivity mDeptMaskItem =" + mDeptMaskItem.toString());

		deptInfoMap.put(inPacket.getDeptid(), mDeptMaskItem);
		int seq = inPacket.getSequence();
		dept_loadinfo_pack_map.put(seq, LOAD_STAT.LOADED);
		concurrent_deptinfo_loading_cnt--;

		LogFactory.d(TAG, "DeptInfo Loaded Deptid=" + inPacket.getDeptid() + " mDeptMaskItem =" + mDeptMaskItem.toString() + ", concurrent_deptinfo_loading_cnt:" + concurrent_deptinfo_loading_cnt + "seq:" + seq);

		dept_loadinfo_map.put(inPacket.getDeptid(), LOAD_STAT.LOADED);
		int send_cnt = do_concurrent_dept_info_req();

		if (send_cnt == 0) {
			// �������������Ϊ0����������е�deptid���Ѿ���������ʱ���Լ��deptinfo�����Ƿ��Ѿ�ȫ�����
			check_dept_info_req_finish();
		}
	}

	// ===============����Ա��Uid���Ӧ �ֶ�===============================

	int[] uid = null;

	int[] nextSibling = null;

	/**
	 * �����µ�����Ա��Id Mapӳ��
	 */
	private HashMap<Integer, int[]> deptUserIdsMap = new HashMap<Integer, int[]>();

	private HashMap<Integer, int[]> deptUserNextSiblingMap = new HashMap<Integer, int[]>();

	/**
	 * ���������û�������Mapӳ��
	 */
	private HashMap<Integer, Integer> deptUserInfoRequestCountMap = new HashMap<Integer, Integer>();

	/**
	 * ����Ա��SiblingId
	 */
	private HashMap<Integer, HashMap<Integer, Integer>> deptUserSiblingMap = new HashMap<Integer, HashMap<Integer, Integer>>();

	/**
	 * ��Ӧ���ŵ�Ա��Id
	 * 
	 * @param command
	 */
	private void responseAllEmployeeUid(short command) {

		GetAllEmployeesUIDInPacket inPacket = (GetAllEmployeesUIDInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (inPacket == null)
			return;

		short commandRet = inPacket.getCommandRet();
		int deptid = inPacket.getDeptid();
		byte endflag = inPacket.getEndflag();
		int seq = inPacket.getSequence();

		if (commandRet == 0) {
			int[] temp_uid = inPacket.getUid();
			int[] temp_nextSibling = inPacket.getNextSibling();

			if (uid == null) {

				// for the first time
				uid = temp_uid;
				nextSibling = temp_nextSibling;
			} else {

				// for the next time
				uid = mergeIntArray(uid, temp_uid);
				nextSibling = mergeIntArray(nextSibling, temp_nextSibling);
			}

		} else // ����ʧ�ܣ���Ҫ��������
		{
			concurrent_deptuids_loading_cnt--;
			dept_loaduids_map.put(inPacket.getDeptid(), LOAD_STAT.LOAD_FAIL);
			dept_loaduids_pack_map.put(seq, LOAD_STAT.LOAD_FAIL);
		}

		// ������Ҫ����Ĵ���
		if (endflag == 1) {
			LogFactory.d(TAG, "------> Dept�µ�  UserId ��ȡ�ɹ� \t  deptid=" + deptid);

			// һ��deptid��uids������ϣ������������seq��أ�������������--
			concurrent_deptuids_loading_cnt--;
			dept_loaduids_pack_map.put(seq, LOAD_STAT.LOADED);
			dept_loaduids_map.put(deptid, LOAD_STAT.LOADED);

			int deptUserCount = uid.length;

			// ���deptId �� �ò����µ�userId��ӳ��
			deptUserIdsMap.put(deptid, uid);
			deptUserNextSiblingMap.put(deptid, nextSibling);

			// �����ܴ�����ӳ��
			deptUserInfoRequestCountMap.put(deptid, deptUserCount % USERINFO_REQUEST_MAX_COUNT == 0 ? deptUserCount / USERINFO_REQUEST_MAX_COUNT : deptUserCount / USERINFO_REQUEST_MAX_COUNT + 1);

			// ��ǰ���ŵ�����uid�Ѿ���ȫȡ������ʼȡuid��info
			do_concurrent_dept_emyployeeinfo_req(deptid);

			int send_cnt = do_concurrent_dept_uids_req();
			if (send_cnt == 0) {
				// �������������Ϊ0����������е�debated���Ѿ���������ʱ���Լ��deptinfo�����Ƿ��Ѿ�ȫ�����
				int finish = check_dept_uids_req_finish();

				// ��ȡԱ����Ϣ�����ݲ���Ϊ����
				if (finish != 0) {
					LogFactory.d(TAG, " all dept uids get done");

					deptUserIdsMap.size();
					int sum = 0;

					Iterator<Entry<Integer, int[]>> iter = deptUserIdsMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<Integer, int[]> entry = iter.next();
						sum += ((int[]) entry.getValue()).length;
					}
					if (sum < 1)
						employee_info_load = LOAD_STAT.LOADED;

					// ��ʼ�洢���ݵ�DB��ȥ
					if (dept_info_load == LOAD_STAT.LOADED && employee_info_load == LOAD_STAT.LOADED)
						doSaveData2DB();
				}
			}
		}
	}

	// ===============����Ա��Info���Ӧ �ֶ�==============================endflag

	/**
	 * �����µ������û���ϢMap -------------------- ��Ҫ��ε����󣬵�������
	 */
	private HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap = new HashMap<Integer, HashMap<Integer, EmployeeInfoItem>>();

	/**
	 * ��Ӧ���ŵ�Ա��������Ϣ
	 * 
	 * @param command
	 */
	private void responseAllEmployeeInfo(short command) {

		GetAllEmployeesInfoInPacket inPacket = (GetAllEmployeesInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (inPacket == null)
			return;

		LogFactory.d(TAG + "2", "----------responseAllEmployeeInfo -----------deptID = ");

		int seq = inPacket.getSequence();

		HashMap<Integer, EmployeeInfoItem> deptUserInfoMapItem = null;

		short commandRet = inPacket.getCommandRet();
		int deptid = 0;
		if (commandRet != 0) {
			concurrent_dept_employeeinfo_loading_cnt--;
		} else {
			byte endflag = inPacket.getEndflag();
			EmployeeInfoItem[] temp_employeeInfoArray = inPacket.getEmployeesInfoArray();

			if (null == temp_employeeInfoArray || temp_employeeInfoArray.length == 0) {
				LogFactory.d(TAG, "responseAllEmployeeInfo------> no data return for seq =" + seq);
				// int[] deptUidArray = deptUserIdsMap.get(this.deptid[mPos]);
			} else {
				deptid = uid_dept_map.get(temp_employeeInfoArray[0].getUid());
				EmployeeInfoItem[] cache_employeeInfoArray = dept_EmployeeInfo_map.get(deptid);

				if (null == cache_employeeInfoArray) {
					dept_EmployeeInfo_map.put(deptid, temp_employeeInfoArray);
				} else {
					cache_employeeInfoArray = mergeUserArray(cache_employeeInfoArray, temp_employeeInfoArray);
					dept_EmployeeInfo_map.put(deptid, cache_employeeInfoArray);
				}

				EmployeeInfoItem[] testArray = dept_EmployeeInfo_map.get(deptid);

				for (int i = 0; i < testArray.length; i++) {
					LogFactory.e(TAG, "EmployeeInfo : " + testArray[i].toString());
				}
			}

			// һ�����������󷵻أ���Ҫ�������ݣ�����Ƿ�������������
			if (endflag == 1) {
				concurrent_dept_employeeinfo_loading_cnt--;
				LogFactory.d(TAG, "------> һ��Dept�µ�  EmployeeInfo����ɹ� \t  deptId =" + deptid);
			}
		}

		int finish = check_employeeinfo_req_finish();
		if (finish != 0) {
			// �ӻ��������������ת��Ϊmap����deptUserInfoMap��
			Iterator<Map.Entry<Integer, EmployeeInfoItem[]>> it = dept_EmployeeInfo_map.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<Integer, EmployeeInfoItem[]> entry = (Map.Entry<Integer, EmployeeInfoItem[]>) it.next();
				int cur_deptid = (Integer) entry.getKey();
				EmployeeInfoItem[] cache_employeeInfoArray = (EmployeeInfoItem[]) entry.getValue();
				if (deptUserInfoMap.get(cur_deptid) != null) {
					// ͬһ�����ŵ�UserId-Info-Map
					deptUserInfoMapItem = deptUserInfoMap.get(cur_deptid);

					for (int i = 0; i < cache_employeeInfoArray.length; i++) {
						// deptUserInfoMapItem.put(deptUidArray[i],employeeInfoArray[i]);
						deptUserInfoMapItem.put(cache_employeeInfoArray[i].getUid(), cache_employeeInfoArray[i]);
					}

					deptUserInfoMap.put(cur_deptid, deptUserInfoMapItem);

				} else {
					deptUserInfoMapItem = new HashMap<Integer, EmployeeInfoItem>();

					for (int i = 0; i < cache_employeeInfoArray.length; i++) {

						deptUserInfoMapItem.put(cache_employeeInfoArray[i].getUid(), cache_employeeInfoArray[i]);
					}

					deptUserInfoMap.put(cur_deptid, deptUserInfoMapItem);
				}
			}

			// ���֮ǰ����ʱmap
			dept_EmployeeInfo_map.clear();
			uid_dept_map.clear();
			dept_uid_loademployeeinfo_map.clear();
			// ��ǰ���������

			Log.d(TAG, "all EmployeeInfo get done Response count,time 1:" + System.currentTimeMillis());
			// ���е�����ȫ����ȡ�ɹ���
			mPos = 0;

			employee_info_load = LOAD_STAT.LOADED;
			// ��ʼ�洢���ݵ�DB��ȥ
			if (dept_info_load == LOAD_STAT.LOADED && employee_info_load == LOAD_STAT.LOADED)
				doSaveData2DB();

			Log.d(TAG, "all EmployeeInfo get done Response count,time 2:" + System.currentTimeMillis());
		}
		return;
	}

	/**
	 * �û�״̬ Map����,����û�������״̬��
	 */
	HashMap<Integer, Integer> userStateMap = new HashMap<Integer, Integer>();

	// ״̬�������󳤶�
	private int MAX_STATE_REQUEST_COUNT = 100;

	// ͳ������������
	private int onLineTotalCount = 0;

	/**
	 * All User IDs
	 */
	int[] mAllUserIds = null;
	int[] mAllCIds = null;

	/**
	 * ���ȵõ����е�Ա���� �õ�������ܴ�����
	 */
	private Vector<Integer> mAllUserIds_vector = new Vector<Integer>();

	private int initUserStateRequest(HashMap<Integer, int[]> deptUserIdsMap) {

		mAllUserIds = null;

		LogFactory.d(TAG + "StateRequest", "deptid.length = " + deptid.length);

		for (int i = 0; i < this.deptid.length; i++) {
			int[] temp_UserId = deptUserIdsMap.get(this.deptid[i]);
			if (temp_UserId != null) {
				for (int j = 0; j < temp_UserId.length; j++) {
					mAllUserIds_vector.add(new Integer(temp_UserId[j]));
				}
			}
		}

		mAllUserIds = new int[mAllUserIds_vector.size()];

		for (int i = 0; i < mAllUserIds.length; i++) {
			mAllUserIds[i] = mAllUserIds_vector.elementAt(i);
		}

		// ==============�ⲿ��ϵ�˵�ʱ������Դ�======================
		mAllCIds = new int[mAllUserIds.length];
		for (int i = 0; i < mAllCIds.length; i++) {
			mAllCIds[i] = EngineConst.cId;
		}

		return mAllUserIds.length % MAX_STATE_REQUEST_COUNT == 0 ? mAllUserIds.length / MAX_STATE_REQUEST_COUNT : mAllUserIds.length / MAX_STATE_REQUEST_COUNT + 1;
	}

	/**
	 * ��Ӧ��Ա��������״̬
	 * 
	 * һ���������ֵΪ100
	 * 
	 * 
	 * @param command
	 */
	private void responseEmployeeState(short command) {

		LogFactory.d(TAG + "3", "----------responseEmployeeState ---------");

		GetEmployeesStatusInPacket inPacket = (GetEmployeesStatusInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (inPacket == null)
			return;
		inPacket.getCid();
		int[] uid = inPacket.getUid();
		int[] status = inPacket.getStatus(); // ״̬

		for (int i = 0; i < uid.length; i++) {
			// ����û�״̬��ӳ��
			userStateMap.put(uid[i], status[i]);
		}

		// ��ǰ���������
		LogFactory.d(TAG + "StateCount", "responseEmployeeState single Response count =" + uid.length);

		// �յ������������1
		stateResponseCount++;
		stateResponseCountEx++;

		LogFactory.e("stateResponseCount", "stateResponseCount: " + stateResponseCount);

		if (hasConcurrentSended == stateResponseCount) {
			updateUserState();
		}

		if (stateResponseCountEx == mStateRequestCount) {
			mPos = 0;
			LogFactory.d(TAG, "User State request has completed !!! \n " + "responseEmployeeState single Response count =" + uid.length);

			forOrganize = false;// / ��֯�ܹ�״̬��ȡ���

			LogFactory.d(TAG + "StateCount", "onLineTotalCount = " + onLineTotalCount);

			mGlobal.updateStateMap((HashMap<Integer, Integer>) userStateMap.clone());

			refresh(null);
		}
	}

	/**
	 * ��������
	 */
	private void buildData() {
		deptUserSiblingMap.clear();

		int[] uid = null;

		int[] nextSibling = null;

		for (int i = 0; i < this.deptid.length; i++) {

			uid = deptUserIdsMap.get(this.deptid[i]);

			nextSibling = deptUserNextSiblingMap.get(this.deptid[i]);

			if (uid != null) {
				HashMap<Integer, Integer> deptUserSiblingMapItem = new HashMap<Integer, Integer>();

				for (int t = 0; t < uid.length; t++) {
					deptUserSiblingMapItem.put(uid[t], nextSibling[t]);
				}

				deptUserSiblingMap.put(this.deptid[i], deptUserSiblingMapItem);
			}

		}
	}

	/**
	 * �������󵽵����ݣ���ʼ������֯�ܹ���
	 */
	Node rootNodeDept;

	DeptMaskItem rootDeptMask;

	Node curNode;

	/**
	 * ���Žڵ� Map
	 */
	HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();

	// ����Id List
	ArrayList<Integer> deptIdList = new ArrayList<Integer>();

	HashMap<Integer, HashMap<Integer, Node>> userNodeMap = new HashMap<Integer, HashMap<Integer, Node>>();// ////

	HashMap<Integer, Node> userNodeMapItem = null;// ////

	/**
	 * @param parentNode
	 *        ���Žڵ�
	 * @param curEmployeeInfo
	 *        ��ǰ���û���Ϣ
	 * @param deptUserInfoMap
	 *        ����UserInfoMap
	 */
	private EmployeeInfoItem addUserNode(Node parentNode, EmployeeInfoItem curEmployeeInfo, HashMap<Integer, EmployeeInfoItem> deptUserInfoMap, HashMap<Integer, Integer> deptUserSiblingMapItem) {

		curEmployeeInfo.getUid();

		int siblingId = deptUserSiblingMapItem.get(curEmployeeInfo.getUid());

		EmployeeInfoItem employeeInfoItem = null;

		if (siblingId != -1) {

			// LogFactory.d("siblingId", "siblingId= " + siblingId);

			employeeInfoItem = deptUserInfoMap.get(siblingId);// /nullpoint
																// employeeInfoItem
																// = null

			boolean isBoy = isBoy(employeeInfoItem.getGender());

			String nodeName = employeeInfoItem.getName();

			Node leafNode = new Node(new NodeData(isBoy, nodeName));

			leafNode.setOnLineState(userStateMap.get(employeeInfoItem.getUid()));
			leafNode.setId(employeeInfoItem.getUid());// ====||

			NodeManager.addChildNode(parentNode, leafNode);

			// userNodeMapItem.put(curEmployeeInfo.getUid(), leafNode);//////===

			this.curNode = leafNode;

		}
		return employeeInfoItem;
	}

	/**
	 * ����ĺϲ�
	 * 
	 * @param curArray
	 * @param tempArray
	 * @return
	 */
	private int[] mergeIntArray(int[] curArray, int[] tempArray) {

		int[] destArray = new int[curArray.length + tempArray.length];

		System.arraycopy(curArray, 0, destArray, 0, curArray.length);
		System.arraycopy(tempArray, 0, destArray, curArray.length, tempArray.length);
		curArray = tempArray = null;
		System.gc();
		return destArray;
	}

	/**
	 * �ϲ�Ա������
	 * 
	 * @param curArray
	 * @param tempArray
	 * @return
	 */
	private EmployeeInfoItem[] mergeUserArray(EmployeeInfoItem[] curArray, EmployeeInfoItem[] tempArray) {

		EmployeeInfoItem[] destArray = new EmployeeInfoItem[curArray.length + tempArray.length];

		System.arraycopy(curArray, 0, destArray, 0, curArray.length);
		System.arraycopy(tempArray, 0, destArray, curArray.length, tempArray.length);
		curArray = tempArray = null;
		System.gc();
		return destArray;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

	/** �����б����ʾ */
	private final int TYPE_REFRESH_TREE = 1;

	public Handler mContactHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

				case TYPE_REFRESH_TREE:
					turn2Main();
					break;

				default:
					break;
			}

		}

	};

	/**
	 * ����ȫ��������ɺ���ת������
	 */
	private void turn2Main() {
		LogFactory.e(TAG, "trun2Main---------------> hasGetInnerContactId :" + hasGetInnerContactId + ", hasGetInnerGroupInfo :" + hasGetInnerGroupInfo + ", hasGetInnerContactWorkSign :" + hasGetInnerContactWorkSign + ", hasGetOuterGroupInfo :" + hasGetOuterGroupInfo + ", hasGetOuterContactId :"
				+ hasGetOuterContactId + ", hasGetOuterCropName :" + hasGetOuterCropName + ", hasGetOuterContactBasicInfoState :" + hasGetOuterContactBasicInfoState + ", hasGetOuterContactState_State :" + hasGetOuterContactState_State);

		if (hasGetInnerContactId && hasGetInnerGroupInfo && hasGetInnerContactWorkSign && hasGetOuterGroupInfo && hasGetOuterContactId && hasGetOuterCropName && hasGetOuterContactBasicInfoState && hasGetOuterContactState_State) {
			// /1- ����ȫ������
			// HashMap<Integer,InnerContactorItem> innerGroupIdMap,
			// HashMap<Integer,ArrayList<Integer>> innerGroupContactMap,
			// HashMap<Integer, String> innerContactWorkSignMap,
			// HashMap<Integer,OuterContactorItem> outerGroupIdMap ,
			// HashMap<Integer,ArrayList<OuterContactItem>>
			// outerGroupContactMap,
			// HashMap<Integer, String> outerContactCorpMap,
			// HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap

			mGlobal.updateContactData((HashMap<Integer, InnerContactorItem>) innerGroupIdMap.clone(), (HashMap<Integer, ArrayList<Integer>>) innerGroupContactMap.clone(), (HashMap<Integer, String>) innerContactWorkSignMap.clone(), (HashMap<Integer, OuterContactorItem>) outerGroupIdMap.clone(),
					(HashMap<Integer, ArrayList<OuterContactItem>>) outerGroupContactMap.clone(), (HashMap<Integer, String>) outerContactCorpMap.clone(), (HashMap<Integer, OuterContactBasicInfo>) outerContactInfoMap.clone());
			// /2- �����ݿ�
			if (doSaveData2Local()) {
				// /3- ��תҳ��
				// System.gc();
				MainActivityGroup.launch(mContext);
				NormalLoadingActivity.this.finish();
			} else {
				LogFactory.d(TAG, "���ݴ�ȡʧ�ܡ�...��");
			}
		}
	}

	// ============================begin========================================================
	private int untransID = -1;
	private int innerGroupUC = -1;
	private int innerGroupListUC = -1;
	private int outerGroupUC = -1;
	private int outerGroupListUC = -1;

	/** �ڲ���ϵ��Group Map */
	private HashMap<Integer, InnerContactorItem> innerGroupIdMap;

	/** �ڲ���ϵ��Group_ContactId_Map */
	private HashMap<Integer, ArrayList<Integer>> innerGroupContactMap = new HashMap<Integer, ArrayList<Integer>>();

	/** �ⲿ��ϵ��Group Map */
	private HashMap<Integer, OuterContactorItem> outerGroupIdMap;

	/** �ⲿ��ϵ��Group_Contact_Map */
	private HashMap<Integer, ArrayList<OuterContactItem>> outerGroupContactMap = new HashMap<Integer, ArrayList<OuterContactItem>>();

	/**
	 * reset innerGroup and outer Group
	 */
	private void resetGroupMap() {
		innerGroupIdMap = new HashMap<Integer, InnerContactorItem>();
		innerGroupIdMap.put(0, new InnerContactorItem(0, getResources().getString(R.string.uncategorized_inner_contact)));
		// innerGroupContactMap.put(0, new ArrayList<Integer>());

		outerGroupIdMap = new HashMap<Integer, OuterContactorItem>();
		outerGroupIdMap.put(0, new OuterContactorItem(0, getResources().getString(R.string.uncategorized_outer_contact)));
		// outerGroupContactMap.put(0, null);
	}

	private boolean innerGroupInfoNeedUpdate = true;
	private boolean innerContactNeedUpdate = true;
	private boolean outerGroupInfoNeedUpdate = true;
	private boolean outerContactNeedUpdate = true;

	/**
	 * ��ʼ�������ݣ�InnerGroupUC ,OuterGroupUC
	 */
	private void beginLoadingContact() {

		IMOApp.getDataEngine().addToObserverList(this);

		doRequestInnerGroupUC();
		doRequestInnerContactUC();

		doRequestOuterGroupUC();
		// doRequestOuterContactUC();
	}

	private boolean hasGetInnerGroupInfo = false;

	/**
	 * 1-�����ڲ���ϵ�� GroupUC
	 */
	private void doRequestInnerGroupUC() {

		LogFactory.d(TAG, "doRequestInnerGroupUC ................");

		hasGetInnerGroupInfo = false;
		Random random = new Random();
		untransID = random.nextInt();
		ByteBuffer buffer = ContactorGroupUCOutPacket.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacket = new ContactorGroupUCOutPacket(buffer, IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	private boolean hasGetInnerContactId = false;

	/**
	 * 2-�����ڲ���ϵ�� ContactUC
	 */
	private void doRequestInnerContactUC() {

		LogFactory.d(TAG, "doRequestInnerContactUC ................");

		hasGetInnerContactId = false;
		Random random = new Random();
		untransID = random.nextInt();
		ByteBuffer buffer = ContactorGroupUCOutPacket.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacket = new ContactorGroupUCOutPacket(buffer, IMOCommand.IMO_INNER_CONTACTOR_LIST_UC, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * �������е�InnerGroupInfo
	 */
	private void doRequestInnerGroupInfo() {
		CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_INNER_CONTACTOR_GROUP, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * �����ڲ���ϵ�˵�����Id
	 */
	private void doRequestInnerContactId() {

		innerGroupContactMap.clear();

		CommonOutPacket outPacketList = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_INNER_CONTACTOR_LIST, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacketList, false);
	}

	private boolean hasGetOuterContactState_State = false;

	/**
	 * request outer contact user state
	 * 
	 * max= 100
	 * 
	 * @param aContactorsNum
	 * @param aContactorCidArray
	 * @param aContactorUidArray
	 */
	private void doRequestOuterContactState(int aContactorsNum, int[] aContactorCidArray, int[] aContactorUidArray) {

		LogFactory.d(TAG, "......................doRequest Outer Contact State ");

		hasGetOuterContactState_State = false;

		ByteBuffer bufferBody = GetEmployeesStatusOutPacket.GenerateEmployeesStatusBody(aContactorsNum, aContactorCidArray, aContactorUidArray);
		GetEmployeesStatusOutPacket outPacket = new GetEmployeesStatusOutPacket(bufferBody, IMOCommand.IMO_GET_EMPLOYEE_STATUS, EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	// //has get inner contact work sign
	private boolean hasGetInnerContactWorkSign = false;

	/**
	 * ����ڲ���ϵ��ǩ�� :����ȥ���󣬷����޷����ƣ��Ƿ��Ѿ�ȫ���յ���
	 * 
	 * @param cid
	 * @param uid
	 */
	private void doRequestInnerContactWorkSign(int cid, int uid) {

		LogFactory.d(TAG, "doRequestInnerContactWorkSign ................");

		if (curPos_InnerWorkSign == 0) {
			innerContactWorkSignMap.clear();
		}

		hasGetInnerContactWorkSign = false;

		int mask = (1 << 4);// ǩ��
		Random random = new Random();
		untransID = random.nextInt();

		ByteBuffer bodyBuffer = GetEmployeeProfileOutPacket.GenerateEmployeeProfileBody(untransID, cid, uid, mask);
		GetEmployeeProfileOutPacket out = new GetEmployeeProfileOutPacket(bodyBuffer, IMOCommand.IMO_GET_EMPLOYEE_PROFILE, EngineConst.cId, EngineConst.uId);

		outer_loadinfo_seq_map.put(out.get_header_seq(), uid);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	private boolean hasGetOuterGroupInfo = false;

	/**
	 * Request Outer GroupUC
	 */
	private void doRequestOuterGroupUC() {
		LogFactory.d(TAG, "................doRequestOuterGroupUC ");

		Random random = new Random();
		untransID = random.nextInt();

		ByteBuffer bufferList = ContactorGroupUCOutPacket.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacketList = new ContactorGroupUCOutPacket(bufferList, IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC, EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacketList, false);
	}

	private boolean hasGetOuterContactId = false;

	/**
	 * Request Outer ContactUC
	 */
	private void doRequestOuterContactUC() {

		LogFactory.d(TAG, "................doRequestOuterContactUC ");

		Random random = new Random();
		untransID = random.nextInt();
		ByteBuffer buffer = ContactorGroupUCOutPacket.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacket = new ContactorGroupUCOutPacket(buffer, IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * Request Outer GroupInfo
	 */
	private void doRequestOuterGroupInfo() {

		LogFactory.d(TAG, "doRequestOuterGroupInfo ................");

		CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_OUTER_CONTACTOR_GROUP, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	private void doRequestOuterContactId() {

		LogFactory.d(TAG, "doRequestOuterContactId ................");

		CommonOutPacket outPacketList = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_OUTER_CONTACTOR_LIST, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacketList, false);
	}

	private boolean hasGetOuterContactBasicInfoState = false;

	/**
	 * do request outer BasicInfo
	 * 
	 * @param cid
	 * @param uid
	 */
	private void doRequestOuterContactBasicInfo(int cid, int uid) {

		LogFactory.d(TAG, " ................doRequestOuterBasicInfo cid=" + cid + " uid = " + uid);

		hasGetOuterContactBasicInfoState = false;
		ByteBuffer bodyBuffer = OuterBasicInfoOutPacket.GenerateOuterBasicInfoBody(cid, uid);
		OuterBasicInfoOutPacket out = new OuterBasicInfoOutPacket(bodyBuffer, IMOCommand.IMO_OUTER_BASIC_INFO, EngineConst.cId, EngineConst.uId);
		outer_basic_info_seq_map.put(out.get_header_seq(), uid);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	/** has get outer crop */
	private boolean hasGetOuterCropName = false;

	/**
	 * do request outer corp info
	 * 
	 * @param cid
	 *        company id
	 */
	private void doRequestOuterCorpInfo(int cid) {

		LogFactory.d(TAG, "................doRequestOuterCorpInfo ................cid =" + cid);

		hasGetOuterCropName = false;
		int mask = (1 << 1);// ��˾���
		ByteBuffer bodyBuffer = GetCorpInfoOutPacket.GenerateCorpInfoBody(cid, mask);
		GetCorpInfoOutPacket out = new GetCorpInfoOutPacket(bodyBuffer, IMOCommand.IMO_GET_CORP_INFO, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	// -----------------------------------------------

	/** �ⲿ��ϵ�ˣ���˾��Ϣ */
	private HashMap<Integer, String> outerContactCorpMap = new HashMap<Integer, String>();

	private int MAX_REQUEST_CONCURRENT_OUTER_CORP_NUMBER = 100;
	private int concurrent_outer_corp_loading_count = 0;
	// outer_loadinfo_map ��ʶ��ǰ����deptid��deptinfo�Ļ�ȡ�������δ���룬�����У�������3��״̬
	private Map<Integer, Integer> outer_corp_loadinfo_map = new HashMap<Integer, Integer>();

	private int do_concurrent_outer_corp_info_req() {
		// ��ʼ��������deptinfo
		int init_cnt = concurrent_outer_corp_loading_count;
		for (int i = 0; i < this.outerCidList.size() && concurrent_outer_corp_loading_count < MAX_REQUEST_CONCURRENT_OUTER_CORP_NUMBER; i++) {

			if (outer_corp_loadinfo_map.get(outerCidList.get(i)) == LOAD_STAT.NOT_LOAD) {
				doRequestOuterCorpInfo(outerCidList.get(i));
				outer_corp_loadinfo_map.put(outerCidList.get(i), LOAD_STAT.LOADING);
				concurrent_outer_corp_loading_count++;
			} else {}
		}

		return concurrent_outer_corp_loading_count - init_cnt;
	}

	/**
	 * response Outer Corp info
	 * 
	 * @param command
	 */
	private void responseOuterCorpInfo(short command) {

		GetCorpInfoInPacket getCorpInfoInPacket = (GetCorpInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (getCorpInfoInPacket == null)
			return;
		short commandRet = getCorpInfoInPacket.getRet();

		if (commandRet == 0) {

			CorpMaskItem maskItem = getCorpInfoInPacket.getMaskItem();
			String shortName = "";
			if (maskItem != null) {
				shortName = maskItem.getShort_name();
			}
			if (shortName == null) {
				shortName = "";
			}
			outerContactCorpMap.put(getCorpInfoInPacket.getContactorID(), shortName);

			LogFactory.e(TAG, "cid = " + getCorpInfoInPacket.getContactorID() + ",shortName = " + shortName);
		}

		outer_corp_loadinfo_map.put(getCorpInfoInPacket.getContactorID(), LOAD_STAT.LOADED);
		concurrent_outer_corp_loading_count--;

		int send_cnt = do_concurrent_outer_corp_info_req();

		if (send_cnt == 0) {
			check_outer_corp_info_req_finish();
		}
	}

	private int check_outer_corp_info_req_finish() {
		for (int i = 0; i < this.outerCidList.size() && concurrent_outer_corp_loading_count < MAX_REQUEST_CONCURRENT_OUTER_CORP_NUMBER; i++) {
			if (outer_corp_loadinfo_map.get(outerCidList.get(i)) == LOAD_STAT.NOT_LOAD || outer_corp_loadinfo_map.get(outerCidList.get(i)) == LOAD_STAT.LOADING) {

				return 0;
			}
		}

		// ����deptinfo���������ɣ��˴����������ʹ�õ��ĸ���map
		outer_corp_loadinfo_map.clear();

		LogFactory.e("check_outer_corp_info_req_finish", "over");

		updateGetOuterCropNameState();

		return 1;
	}

	/** Outer Contact Basic info Map */
	private HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap = new HashMap<Integer, OuterContactBasicInfo>();

	/**
	 * response outer basic info
	 * 
	 * @param command
	 */
	private void responseOuterBasicInfo(short command) {

		LogFactory.d(TAG, "........................responseOuterBasicInfo");

		OuterBasicInfoInPacket outerContactorBasicInfo = (OuterBasicInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (outerContactorBasicInfo == null)
			return;
		short commandRet = outerContactorBasicInfo.getRet();

		if (commandRet == 0) {
			int cid = outerContactorBasicInfo.getContactor_cid();
			int uid = outerContactorBasicInfo.getContactor_uid();
			String corpAccount = outerContactorBasicInfo.getCorp_account();
			String userAccount = outerContactorBasicInfo.getUser_account();
			String name = outerContactorBasicInfo.getName();
			int gender = outerContactorBasicInfo.getGender();

			OuterContactBasicInfo basicInfo = new OuterContactBasicInfo(cid, uid, corpAccount, userAccount, name, gender);

			// // add to the map
			outerContactInfoMap.put(uid, basicInfo);

			LogFactory.d(TAG, ".......................�ⲿ��ϵ�˻�������:name =" + name + "  userAccount = " + userAccount);
		}

		if (outer_basic_info_map.containsKey(outerContactorBasicInfo.getContactor_uid())) {
			outer_basic_info_map.put(outerContactorBasicInfo.getContactor_uid(), LOAD_STAT.LOADED);
		} else {
			int uid = outer_basic_info_seq_map.get(outerContactorBasicInfo.get_header_seq());
			outer_basic_info_map.put(uid, LOAD_STAT.LOADED);
		}

		LogFactory.e(TAG, "outer_basic_info_map end : " + outer_basic_info_map.keySet() + ",value :" + outer_basic_info_map.values());
		concurrent_outer_basic_info_loading_count--;

		int send_cnt = do_concurrent_outer_basic_info_req();

		if (send_cnt == 0) {
			check_outer_basic_info_req_finish();
		}
	}

	private int check_outer_basic_info_req_finish() {
		for (int i = 0; i < this.outerContactItemList.size() && concurrent_outer_basic_info_loading_count < MAX_REQUEST_CONCURRENT_OUTER_BASIC_INFO_NUMBER; i++) {
			if (outer_basic_info_map.get(outerContactItemList.get(i).getUid()) == LOAD_STAT.NOT_LOAD || outer_basic_info_map.get(outerContactItemList.get(i).getUid()) == LOAD_STAT.LOADING) {

				return 0;
			}
		}

		// ����deptinfo���������ɣ��˴����������ʹ�õ��ĸ���map
		outer_basic_info_map.clear();
		outer_basic_info_seq_map.clear();

		LogFactory.e("check_outer_basic_info_req_finish", "over");

		updateGetOuterContactBasicInfoState();

		return 1;
	}

	/** Outer Conctact Crop cid_name Map :for get the crop name */
	private HashMap<Integer, String> outerContactCropMap = new HashMap<Integer, String>();
	/** Outer Conctact Crop id List */
	private ArrayList<Integer> outerCidList = new ArrayList<Integer>();
	/** Outer Conctact Id --[cid,uid,group,flag] */
	private ArrayList<OuterContactItem> outerContactItemList = new ArrayList<OuterContactItem>();
	private Integer curPos_OuterContactState = 0;// / outer contact state

	// // outer contact request total count for state
	private int outerStateRequestTotalCount = 0;

	/**
	 * response outer Contact Id
	 * 
	 * @param command
	 */
	private void responseOuterContactId(short command) {

		LogFactory.e(TAG + "Outer", "................responseOuterContactId.");

		OuterContactorListInPacket outerContactorList = (OuterContactorListInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (outerContactorList == null)
			return;
		short commandRet = outerContactorList.getRet();
		byte endflag = outerContactorList.getEndflag();

		if (commandRet == 0) {
			int[] outertempContactorCid = outerContactorList.getContactor_cid();
			int[] outertempContactorUid = outerContactorList.getContactor_uid();
			int[] outertempInnerGroupId = outerContactorList.getGroup_id();
			int[] outFlag = outerContactorList.getFlag();

			LogFactory.e(TAG, "responseOuterContactId size : " + outertempContactorCid.length);

			for (int i = 0; i < outertempContactorUid.length; i++) {

				OuterContactItem item = new OuterContactItem(outertempContactorCid[i], outertempContactorUid[i], outertempInnerGroupId[i], outFlag[i]);
				outerContactItemList.add(item);// /All Outer Contact id info for
												// basic info

				outer_basic_info_map.put(outertempContactorUid[i], LOAD_STAT.NOT_LOAD);

				outerContactCropMap.put(outertempContactorCid[i], ""); //
																		// update
																		// outer
																		// contact
																		// cropId

				if (outerGroupIdMap.containsKey(outertempInnerGroupId[i])) {
					ArrayList<OuterContactItem> outerContactList = outerGroupContactMap.get(outertempInnerGroupId[i]);
					if (outerContactList == null) {
						outerContactList = new ArrayList<OuterContactItem>();
					}
					outerContactList.add(item);

					outerGroupContactMap.put(outertempInnerGroupId[i], outerContactList);
				} else {
					item.setGroupId(0);

					ArrayList<OuterContactItem> outerContactList = outerGroupContactMap.get(0);
					if (outerContactList == null) {
						outerContactList = new ArrayList<OuterContactItem>();
					}
					outerContactList.add(item);

					outerGroupContactMap.put(0, outerContactList);
				}
			}
		}

		if (1 == endflag) {

			LogFactory.e(TAG, "outerContactCropMap size : " + outerContactCropMap.size());

			// //has got all outer contact id
			LogFactory.e(TAG + "Outer", "................�ⲿ��ϵ��ContactorID����-������ϣ�");

			updateGetOuterContactIdState();

			// 1- get all cId, and begin doRequestCropName
			for (Integer cId : outerContactCropMap.keySet()) {
				outerCidList.add(cId);
				outer_corp_loadinfo_map.put(cId, LOAD_STAT.NOT_LOAD);
			}

			if (outerCidList.size() > 0) {
				LogFactory.e(TAG, "outerCidList size : " + outerCidList.size());
				do_concurrent_outer_corp_info_req();
			} else {
				updateGetOuterCropNameState();
			}

			// 2- begin doRequestOuterContactBasicInfo
			/*
			 * if (outerContactItemList.size() > 0) { OuterContactItem
			 * requestItem = outerContactItemList.get(curPos_OuterBasicInfo++);
			 * doRequestOuterContactBasicInfo
			 * (requestItem.getCid(),requestItem.getUid()); } else {
			 * updateGetOuterContactBasicInfoState(); }
			 */

			if (outerContactItemList.size() > 0) {
				do_concurrent_outer_basic_info_req();
			} else {
				updateGetOuterContactBasicInfoState();
			}

			// 3- begin doRequestOuterContactState
			outerStateRequestTotalCount = outerContactItemList.size() % MAX_STATE_REQUEST_COUNT == 0 ? outerContactItemList.size() / MAX_STATE_REQUEST_COUNT : outerContactItemList.size() / MAX_STATE_REQUEST_COUNT + 1;

			LogFactory.e(TAG, "................outerStateRequestTotalCount = " + outerStateRequestTotalCount);

			if (outerStateRequestTotalCount > 0) {
				Object[] objs = getRequestOuterContactIdArray();
				doRequestOuterContactState(((int[]) objs[0]).length, (int[]) objs[0], (int[]) objs[1]);
			} else {
				// /û��Ա������Ч����state
				updateGetOuterContactState_State();
			}
		}
	}

	private int MAX_REQUEST_CONCURRENT_OUTER_BASIC_INFO_NUMBER = 100;
	private int concurrent_outer_basic_info_loading_count = 0;
	// outer_loadinfo_map ��ʶ��ǰ����deptid��deptinfo�Ļ�ȡ�������δ���룬�����У�������3��״̬
	private Map<Integer, Integer> outer_basic_info_map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> outer_basic_info_seq_map = new HashMap<Integer, Integer>();

	private int do_concurrent_outer_basic_info_req() {
		// ��ʼ��������deptinfo
		int init_cnt = concurrent_outer_basic_info_loading_count;
		for (int i = 0; i < this.outerContactItemList.size() && concurrent_outer_basic_info_loading_count < MAX_REQUEST_CONCURRENT_OUTER_BASIC_INFO_NUMBER; i++) {

			if (outer_basic_info_map.get(this.outerContactItemList.get(i).getUid()) == LOAD_STAT.NOT_LOAD) {

				OuterContactItem requestItem = outerContactItemList.get(i);
				doRequestOuterContactBasicInfo(requestItem.getCid(), requestItem.getUid());

				outer_basic_info_map.put(requestItem.getUid(), LOAD_STAT.LOADING);
				concurrent_outer_basic_info_loading_count++;
			} else {}
		}

		return concurrent_outer_basic_info_loading_count - init_cnt;
	}

	/**
	 * ״̬�����ⲿ��ϵ������ID
	 * 
	 * @return
	 */
	private Object[] getRequestOuterContactIdArray() {

		int[] requestCIds = null;
		int[] requestUIds = null;

		if (outerStateRequestTotalCount - 1 > curPos_OuterContactState) {
			// /request max
			requestCIds = new int[MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[MAX_STATE_REQUEST_COUNT];

			LogFactory.d(TAG + "State--HasRequestCount", "curPos_OuterContactState = " + curPos_OuterContactState);

			int start = MAX_STATE_REQUEST_COUNT * curPos_OuterContactState;
			int end = MAX_STATE_REQUEST_COUNT * curPos_OuterContactState + MAX_STATE_REQUEST_COUNT;

			List<OuterContactItem> tempList = outerContactItemList.subList(start, end);

			LogFactory.d(TAG, "request state arraySize = " + MAX_STATE_REQUEST_COUNT);

			for (int i = 0; i < tempList.size(); i++) {
				requestCIds[i] = tempList.get(i).getCid();
				requestUIds[i] = tempList.get(i).getUid();
			}

			curPos_OuterContactState++;

		} else {
			LogFactory.d(TAG + "State--HasRequestCount", "curPos_OuterContactState = " + curPos_OuterContactState);
			int arraySize = outerContactItemList.size() - curPos_OuterContactState * MAX_STATE_REQUEST_COUNT;

			LogFactory.d(TAG, "last request state arraySize = " + arraySize);

			if (arraySize > 0) {
				requestCIds = new int[arraySize];
				requestUIds = new int[arraySize];

				int start = MAX_STATE_REQUEST_COUNT * curPos_OuterContactState;
				int end = outerContactItemList.size();

				List<OuterContactItem> tempList = outerContactItemList.subList(start, end);

				for (int i = 0; i < tempList.size(); i++) {
					requestCIds[i] = tempList.get(i).getCid();
					requestUIds[i] = tempList.get(i).getUid();
				}

				curPos_OuterContactState++;
			} else {
				return null;
			}
		}

		return new Object[] {
				requestCIds, requestUIds
		};
	}

	/**
	 * response outer contact group info
	 * 
	 * @param command
	 */
	private void responseOuterGroupInfo(short command) {

		LogFactory.d(TAG, "................responseOuterContactGroupInfo.");

		ContactorGroupInPacket outerGroupInPacket = (ContactorGroupInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (outerGroupInPacket == null) {
			return;
		}
		short commandRet = outerGroupInPacket.getRet();

		byte endflag = outerGroupInPacket.getEndflag();

		if (commandRet == 0) {

			int[] outertempGroupId = outerGroupInPacket.getGroup_id();
			String[] outertempGroupName = outerGroupInPacket.getGroup_name();

			for (int i = 0; i < outertempGroupName.length; i++) {
				LogFactory.d(TAG, "outertempGroupId = " + outertempGroupId[i] + "  outertempGroupName" + outertempGroupName[i]);
				outerGroupIdMap.put(outertempGroupId[i], new OuterContactorItem(outertempGroupId[i], outertempGroupName[i]));
			}
		}
		if (endflag == 1) {
			updateGetOuterGroupInfoState();
		}
	}

	/**
	 * response outer Contact UC
	 * 
	 * @param command
	 */
	private void responseOuterContactUC(short command) {

		LogFactory.d(TAG, "................responseOuterContactUC");

		ContactorGroupUCInPacket outerlistinPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (outerlistinPacket == null) {
			return;
		}

		// /��ñ���Contact Uc
		outerGroupListUC = (Integer) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				OUTER_GROUP_LIST_UC, outerGroupListUC
		});

		int curOuterGroupListUC = outerlistinPacket.getUnContactorGroupListUC();

		LogFactory.d(TAGUC, "curOuterGroupListUC = " + curOuterGroupListUC);

		if (curOuterGroupListUC != outerGroupListUC) {
			// outer Contact need update
			outerGroupListUC = curOuterGroupListUC;
			doRequestOuterContactId();
		} else {
			// /outer Contact needn't update , get data from local
			outerContactNeedUpdate = false;
			// //1- get local date
			outerGroupContactMap.clear();
			outerContactCorpMap.clear();
			outerContactInfoMap.clear();
			// /////��ñ��ص�3������
			try {
				IMOApp.imoStorage.getAllOuterContactListInfo(outerGroupContactMap, outerContactCorpMap, outerContactInfoMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateGetOuterContactIdState();
			updateGetOuterCropNameState();
			updateGetOuterContactBasicInfoState();

			// 3- begin doRequestOuterContactState
			outerContactItemList.clear();
			for (Integer groupId : outerGroupContactMap.keySet()) {
				ArrayList<OuterContactItem> itemList = outerGroupContactMap.get(groupId);
				if (itemList != null) {
					outerContactItemList.addAll(itemList);
				}
			}
			outerStateRequestTotalCount = outerContactItemList.size() % MAX_STATE_REQUEST_COUNT == 0 ? outerContactItemList.size() / MAX_STATE_REQUEST_COUNT : outerContactItemList.size() / MAX_STATE_REQUEST_COUNT + 1;

			LogFactory.d(TAG, "................outerStateRequestTotalCount = " + outerStateRequestTotalCount);

			if (outerStateRequestTotalCount > 0) {

				Object[] objs = getRequestOuterContactIdArray();
				if (objs != null) {
					doRequestOuterContactState(((int[]) objs[0]).length, (int[]) objs[0], (int[]) objs[1]);
				}
			} else {
				// /û��Ա������������state
				updateGetOuterContactState_State();
			}
		}
	}

	/**
	 * response outer contactor group uc
	 * 
	 * @param command
	 */
	private void responseOuterContactGroupUC(short command) {

		LogFactory.d(TAG, "................responseOuterContactGroupUC");

		ContactorGroupUCInPacket outerinPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (outerinPacket == null) {
			return;
		}

		// /��������
		outerGroupUC = (Integer) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				OUTER_GROUP_UC, outerGroupUC
		});

		int curOuterGroupUC = outerinPacket.getUnContactorGroupListUC();

		LogFactory.d(TAGUC, "curOuterGroupUC = " + curOuterGroupUC);

		if (curOuterGroupUC != outerGroupUC) {
			// /Group info need update
			outerGroupUC = curOuterGroupUC;
			doRequestOuterGroupInfo();
		} else {
			outerGroupInfoNeedUpdate = false;
			// /GroupInfo needn't update , get data from local
			outerGroupIdMap.clear();
			try {
				IMOApp.imoStorage.getOuterGroupInfo(outerGroupIdMap);// //
																		// ������ݿ�ӿڣ�������е�OuterGroupInfo
			} catch (Exception e) {
				e.printStackTrace();
			}

			updateGetOuterGroupInfoState();
		}
	}

	/**
	 * Outer contact state map
	 */
	private HashMap<Integer, Integer> outerId_State_Map = new HashMap<Integer, Integer>();

	/**
	 * response Employee state
	 * 
	 * �ڲ���ϵ�˲���Ҫ����state
	 * 
	 * @param command
	 */
	private void responseOuterContactState(short command) {

		LogFactory.d(TAG, "...............responseOuterContactState");

		GetEmployeesStatusInPacket statusInPacket = (GetEmployeesStatusInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (statusInPacket == null) {
			return;
		}
		LogFactory.d(TAG, "Outer Contact ״̬" + statusInPacket.toString());

		int[] uid = statusInPacket.getUid();
		int[] status = statusInPacket.getStatus(); // ״̬

		for (int i = 0; i < uid.length; i++) {
			LogFactory.d(TAG, "uid =" + uid[i] + "  status =" + status[i]);
			outerId_State_Map.put(uid[i], status[i]);
		}

		if (curPos_OuterContactState < outerStateRequestTotalCount) {
			Object[] objs = getRequestOuterContactIdArray();
			if (objs != null) {
				doRequestOuterContactState(((int[]) objs[0]).length, (int[]) objs[0], (int[]) objs[1]);
			} else {
				updateGetOuterContactState_State();
			}
		}

		if (outerId_State_Map.size() == outerContactItemList.size()) {
			LogFactory.d(TAG, "...............outer contact state has got .....");
			updateGetOuterContactState_State();
		}
	}

	/** �ڲ���ϵ�˹���ǩ��ӳ�� */
	private HashMap<Integer, String> innerContactWorkSignMap = new HashMap<Integer, String>();

	private int MAX_REQUEST_CONCURRENT_INNER_NUMBER = 100;
	private int concurrent_outer_loading_count = 0;
	// outer_loadinfo_map ��ʶ��ǰ����deptid��deptinfo�Ļ�ȡ�������δ���룬�����У�������3��״̬
	private Map<Integer, Integer> outer_loadinfo_map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> outer_loadinfo_seq_map = new HashMap<Integer, Integer>();

	private int do_concurrent_outer_info_req() {
		// ��ʼ��������deptinfo
		int init_cnt = concurrent_outer_loading_count;
		for (int i = 0; i < this.innerContactIdList.size() && concurrent_outer_loading_count < MAX_REQUEST_CONCURRENT_INNER_NUMBER; i++) {

			if (outer_loadinfo_map.get(innerContactIdList.get(i)) == LOAD_STAT.NOT_LOAD) {
				doRequestInnerContactWorkSign(EngineConst.cId, innerContactIdList.get(i));
				outer_loadinfo_map.put(innerContactIdList.get(i), LOAD_STAT.LOADING);
				concurrent_outer_loading_count++;
			} else {}
		}

		return concurrent_outer_loading_count - init_cnt;
	}

	/**
	 * response Inner Contact work sign
	 * 
	 * @param command
	 */
	private void responseInnerContactWorkSign(short command) {

		LogFactory.e(TAG, "responseInnerContactWorkSign ----------------->");

		EmployeeProfileItem employeeProfileItem = null;

		String sign = "";

		GetEmployeeProfileInPacket getEmployeeProfileInPacket = (GetEmployeeProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (getEmployeeProfileInPacket == null)
			return;

		short commandRet = getEmployeeProfileInPacket.getRet();

		if (commandRet == 0) {
			int uid = getEmployeeProfileInPacket.getUid();

			employeeProfileItem = getEmployeeProfileInPacket.getEmployeeItem();

			if (employeeProfileItem != null) {
				sign = employeeProfileItem.getSign();
			}
			LogFactory.d(TAG, "WorkSign --------->uid=" + uid + "  sign=" + sign);

			innerContactWorkSignMap.put(uid, sign);// / add work sign
		}

		concurrent_outer_loading_count--;

		LogFactory.e(TAG, "Outer Loaded cid=" + getEmployeeProfileInPacket.getCid() + ",employeeProfileItem = , concurrent_outer_loading_count:" + concurrent_outer_loading_count + ",seq:" + getEmployeeProfileInPacket.getSequence());

		if (outer_loadinfo_map.containsKey(getEmployeeProfileInPacket.getUid())) {
			outer_loadinfo_map.put(getEmployeeProfileInPacket.getUid(), LOAD_STAT.LOADED);
		} else {
			int uid = outer_loadinfo_seq_map.get(getEmployeeProfileInPacket.get_header_seq());
			outer_loadinfo_map.put(uid, LOAD_STAT.LOADED);
		}

		int send_cnt = do_concurrent_outer_info_req();

		if (send_cnt == 0) {
			// �������������Ϊ0����������е�deptid���Ѿ���������ʱ���Լ��deptinfo�����Ƿ��Ѿ�ȫ�����
			// curPos_InnerWorkSign = 0;
			// has got all sign
			check_InnerContact_WorkSign_req_finish();
		}
	}

	private int check_InnerContact_WorkSign_req_finish() {
		for (int i = 0; i < this.innerContactIdList.size() && concurrent_outer_loading_count < MAX_REQUEST_CONCURRENT_INNER_NUMBER; i++) {
			if (outer_loadinfo_map.get(innerContactIdList.get(i)) == LOAD_STAT.NOT_LOAD || outer_loadinfo_map.get(innerContactIdList.get(i)) == LOAD_STAT.LOADING) {

				return 0;
			}
		}

		// ����deptinfo���������ɣ��˴����������ʹ�õ��ĸ���map
		outer_loadinfo_map.clear();
		outer_loadinfo_seq_map.clear();

		LogFactory.e("check_InnerContact_WorkSign_req_finish", "over");

		updateGetInnerContactWorkSignState();

		return 1;
	}

	/**
	 * response Inner GroupInfo
	 * 
	 * @param command
	 */
	private void responseInnerGroupInfo(short command) {

		LogFactory.d(TAG, "responseInnerGroupInfo ................");

		ContactorGroupInPacket innerGroupInPacket = (ContactorGroupInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (innerGroupInPacket == null) {
			return;
		}

		short commandRet = innerGroupInPacket.getRet();

		if (commandRet == 0) {

			byte endflag = innerGroupInPacket.getEndflag();

			int[] tempGroupId = innerGroupInPacket.getGroup_id();
			String[] tempGroupName = innerGroupInPacket.getGroup_name();

			for (int i = 0; i < tempGroupName.length; i++) {
				LogFactory.d(TAG, "GroupId = " + tempGroupId[i] + "  GroupName =" + tempGroupName[i]);

				innerGroupIdMap.put(tempGroupId[i], new InnerContactorItem(tempGroupId[i], tempGroupName[i]));
			}

			if (endflag == 1) {
				updateGetInnerGroupInfoState();
			}
		}
	}

	private int curPos_InnerWorkSign = 0;
	/** inner Contact Id List */
	private ArrayList<Integer> innerContactIdList = new ArrayList<Integer>();

	/**
	 * response InnerContactId
	 * 
	 * @param command
	 */
	private void responseInnerContactId(short command) {

		LogFactory.d(TAG, "responseInnerContactId ................");

		InnerContactorListInPacket innerContactorList = (InnerContactorListInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (innerContactorList == null) {
			return;
		}

		int commandRet = innerContactorList.getRet();

		byte endflag = innerContactorList.getEndflag();

		if (commandRet == 0) {
			int[] tempInnerGroupId = innerContactorList.getGroup_id();
			int[] tempContactorId = innerContactorList.getContactor_uid();
			// //��group��û����ϵ��
			if (tempInnerGroupId != null && tempContactorId != null) {

				LogFactory.d(TAGUC, "innerContactId Size = " + tempContactorId.length);
				// //Add contact id to the corresponding GroupId
				for (int i = 0; i < tempContactorId.length; i++) {

					LogFactory.d(TAG, "InnerGroupId = " + tempInnerGroupId[i] + "  ContactId = " + tempContactorId[i]);

					ArrayList<Integer> groupContactIdList = innerGroupContactMap.get(tempInnerGroupId[i]);
					if (groupContactIdList == null) {
						groupContactIdList = new ArrayList<Integer>();
					}
					groupContactIdList.add(tempContactorId[i]);

					innerGroupContactMap.put(tempInnerGroupId[i], groupContactIdList);
				}
			}
		}

		if (1 == endflag) {
			LogFactory.d(TAGUC, "�ڲ���ϵ��ContactorID����,������ϣ�");
			updateGetInnerContactIdState();
		}
	}

	/**
	 * response InnerContactUC
	 * 
	 * @param command
	 */
	private void responseInnerContactUC(short command) {

		LogFactory.d(TAG, "responseInnerContactUC ................");

		ContactorGroupUCInPacket listinPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		if (listinPacket == null) {
			return;
		}

		// /get local innerGroupListUC
		innerGroupListUC = (Integer) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				INNER_GROUP_LIST_UC, innerGroupListUC
		});
		int curInnerGroupListUC = listinPacket.getUnContactorGroupListUC();

		LogFactory.d(TAGUC, "curInnerGroupListUC = " + curInnerGroupListUC);
		LogFactory.d(TAGUC, "localInnerGroupListUC = " + innerGroupListUC);

		if (curInnerGroupListUC != innerGroupListUC || innerGroupListUC == -1) {
			// inner Contact need update
			innerGroupListUC = curInnerGroupListUC;
			doRequestInnerContactId();
		} else {

			LogFactory.d(TAGUC, "localInnerGroupListUC  load local data");
			// /inner Contact needn't update , get data from local
			innerContactNeedUpdate = false;
			// //1- get local date
			innerGroupContactMap.clear();
			try {
				IMOApp.imoStorage.getAllInnerContactListInfo(innerGroupContactMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateGetInnerContactIdState();
		}
	}

	/**
	 * 1-��ӦInnerGroupUC �ͱ������ݱȽ�
	 * 
	 * @param command
	 */
	private void responseInnerGroupUC(short command) {

		LogFactory.d(TAG, "responseInnerGroupUC ................");

		ContactorGroupUCInPacket inPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (inPacket == null) {
			return;
		}

		// ����innerGroupUC
		innerGroupUC = (Integer) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				INNER_GROUP_UC, innerGroupUC
		});

		int curInnerGroupUC = inPacket.getUnContactorGroupListUC();

		LogFactory.d(TAGUC, "curInnerGroupUC = " + curInnerGroupUC);

		if (curInnerGroupUC != innerGroupUC) {
			// /Group info need update
			innerGroupUC = curInnerGroupUC;
			doRequestInnerGroupInfo();
		} else {
			innerGroupInfoNeedUpdate = false;
			// /GroupInfo needn't update , get data from local
			innerGroupIdMap.clear();
			try {
				IMOApp.imoStorage.getInnerGroupInfo(innerGroupIdMap);
			} catch (Exception e) {
				e.printStackTrace();
			}// // ������ݿ�ӿڣ�������е�InnerGroupInfo
			updateGetInnerGroupInfoState();
		}
	}

	// [��ϵ��]
	public static String INNER_GROUP_UC = "innergroupuc";
	public static String INNER_GROUP_LIST_UC = "innnergrouplistuc";
	public static String OUTER_GROUP_UC = "outergroupuc";
	public static String OUTER_GROUP_LIST_UC = "outergrouplistuc";

	/**
	 * ִ�� ���ݱ��浽����
	 */
	private boolean doSaveData2Local() {

		// /��������
		boolean save2DbResult = save2DB();

		// When DB save success!
		if (save2DbResult) {

			// 1- save innerGroupUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					INNER_GROUP_UC, innerGroupUC
			});
			// 2- save innerContactUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					INNER_GROUP_LIST_UC, innerGroupListUC
			});
			// 3- save outerGroupUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					OUTER_GROUP_UC, outerGroupUC
			});
			// 4- save outerContactUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					OUTER_GROUP_LIST_UC, outerGroupListUC
			});

		}
		return save2DbResult;
	}

	/**
	 * save to db
	 * 
	 * @return
	 */
	private boolean save2DB() {

		try {
			if (innerGroupInfoNeedUpdate) {
				IMOApp.imoStorage.putInnerGroupInfo(innerGroupIdMap);
			}

			if (innerContactNeedUpdate) {
				IMOApp.imoStorage.putInnerContactListInfo(innerGroupContactMap);
			}

			if (outerGroupInfoNeedUpdate) {
				IMOApp.imoStorage.putOuterGroupInfo(outerGroupIdMap);
			}

			if (outerContactNeedUpdate) {
				IMOApp.imoStorage.putOuterContactListInfo(outerGroupContactMap);
				IMOApp.imoStorage.putOuterContactBasicInfo(outerContactInfoMap);
				IMOApp.imoStorage.putOuterCorpInfo(outerContactCorpMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * ����getInnerGroupInfo��״̬��
	 */
	private void updateGetInnerGroupInfoState() {
		hasGetInnerGroupInfo = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * ����getInnerConnectId��״̬��
	 */
	private void updateGetInnerContactIdState() {
		hasGetInnerContactId = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);

		innerContactIdList.clear();
		for (Integer groupId : innerGroupContactMap.keySet()) {
			// ���group��Ϊ�յ���������洦����ˣ���߲����ܳ�����
			innerContactIdList.addAll(innerGroupContactMap.get(groupId));
		}

		// ��ʼ��out_loadinfo_map��״̬
		for (int i = 0; i < innerContactIdList.size(); i++) {
			outer_loadinfo_map.put(innerContactIdList.get(i), LOAD_STAT.NOT_LOAD);
		}

		if (innerContactIdList.size() > 0) {
			// �޸�Ϊ��������
			do_concurrent_outer_info_req();
		} else {
			updateGetInnerContactWorkSignState();
		}
	}

	/**
	 * ����getInnerConnectWorkSign��״̬��
	 */
	private void updateGetInnerContactWorkSignState() {
		hasGetInnerContactWorkSign = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * ����getOuterGroupInfo��״̬��
	 */
	private void updateGetOuterGroupInfoState() {
		hasGetOuterGroupInfo = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
		doRequestOuterContactUC();
	}

	/**
	 * ����getOuterConnectId��״̬��
	 */
	private void updateGetOuterContactIdState() {
		hasGetOuterContactId = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * ����getOuterCropName��״̬��
	 */
	private void updateGetOuterCropNameState() {
		hasGetOuterCropName = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * ����getOuterContactBasicInfoState��״̬��
	 */
	private void updateGetOuterContactBasicInfoState() {
		hasGetOuterContactBasicInfoState = true;
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * ����getOuterState--State��״̬��
	 */
	private void updateGetOuterContactState_State() {
		hasGetOuterContactState_State = true;
		// update global data
		mGlobal.outerUserStateMap = (HashMap<Integer, Integer>) outerId_State_Map.clone();
		// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}
}
