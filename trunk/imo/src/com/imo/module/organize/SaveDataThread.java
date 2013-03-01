package com.imo.module.organize;

import com.imo.global.IMOApp;
import com.imo.network.net.EngineConst;
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;

/**
 * ���߳���ִ�д洢���ݿ�Ĳ���
 * <br>
 * �����ݿ�����ɹ���ǰ���£���ʾ��һ�μ��سɹ���
 * 
 * @author CaixiaoLong
 *
 */
public class SaveDataThread extends Thread {

	@Override
	public void run() {
		try {
			IMOApp.imoStorage.add(
					IMOApp.getApp().deptid, 
					IMOApp.getApp().dept_uc, 
					IMOApp.getApp().dept_user_uc,
					IMOApp.getApp().deptInfoMap, 
					IMOApp.getApp().deptUserIdsMap,
					IMOApp.getApp().deptUserNextSiblingMap, 
					IMOApp.getApp().deptUserInfoMap
					);
			PreferenceManager.save("IMO-DATA"+EngineConst.uId, new String[] { "isFirst","No" });
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}
