package com.imo.db;

import android.app.Activity;
import android.os.Bundle;

import com.imo.db.sql.IMOStorage;

public class DemoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		// 测试数据库
		IMOStorage imoStorage = IMOStorage.getInstance(this);
		try {
			imoStorage.open(1015);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// ********************************

		try {
			// Map<Integer, InnerContactorItem> innerGroupIdMap = new
			// HashMap<Integer, InnerContactorItem>();
			// innerGroupIdMap.put(1, new InnerContactorItem(1, "组1"));
			// innerGroupIdMap.put(2, new InnerContactorItem(2, "组2"));
			// innerGroupIdMap.put(3, new InnerContactorItem(3, "组3"));
			// innerGroupIdMap.put(4, new InnerContactorItem(4, "组4"));
			// innerGroupIdMap.put(5, new InnerContactorItem(5, "组5"));
			// imoStorage.putInnerGroupInfo(innerGroupIdMap);
			//
			// Map<Integer, OuterContactorItem> outerGroupIdMap = new
			// HashMap<Integer, OuterContactorItem>();
			// outerGroupIdMap.put(1, new OuterContactorItem(1, "外组1"));
			// outerGroupIdMap.put(2, new OuterContactorItem(2, "外组2"));
			// outerGroupIdMap.put(3, new OuterContactorItem(3, "外组3"));
			// outerGroupIdMap.put(4, new OuterContactorItem(4, "外组4"));
			// imoStorage.putOuterGroupInfo(outerGroupIdMap);
			//
			//
			// Map<Integer, ArrayList<Integer>> innerGroupContactMap = new
			// HashMap<Integer, ArrayList<Integer>>();
			// ArrayList<Integer> uids= new ArrayList<Integer>();
			// uids.add(100);
			// uids.add(101);
			// uids.add(102);
			// innerGroupContactMap.put(1, uids);
			//
			// ArrayList<Integer> uids1= new ArrayList<Integer>();
			// uids1.add(200);
			// uids1.add(201);
			// uids1.add(202);
			// innerGroupContactMap.put(2, uids1);
			//
			// ArrayList<Integer> uids2= new ArrayList<Integer>();
			// uids2.add(300);
			// uids2.add(301);
			// uids2.add(302);
			// innerGroupContactMap.put(3, uids2);
			// imoStorage.putInnerContactListInfo(innerGroupContactMap);
			//
			//
			//
			// Map<Integer, ArrayList<OuterContactItem>> outerGroupContactMap =
			// new HashMap<Integer, ArrayList<OuterContactItem>>();
			// ArrayList<OuterContactItem> outerContactItems = new
			// ArrayList<OuterContactItem>();
			// outerContactItems.add(new OuterContactItem(0, 1000, 1, 0));
			// outerContactItems.add(new OuterContactItem(0, 1001, 1, 0));
			// outerGroupContactMap.put(1, outerContactItems);
			//
			// ArrayList<OuterContactItem> outerContactItems1 = new
			// ArrayList<OuterContactItem>();
			// outerContactItems1.add(new OuterContactItem(0, 2000, 2, 0));
			// outerContactItems1.add(new OuterContactItem(0, 2001, 2, 0));
			// outerGroupContactMap.put(2, outerContactItems1);
			//
			// ArrayList<OuterContactItem> outerContactItems2 = new
			// ArrayList<OuterContactItem>();
			// outerContactItems2.add(new OuterContactItem(0, 3000, 3, 0));
			// outerContactItems2.add(new OuterContactItem(0, 3001, 3, 0));
			// outerGroupContactMap.put(3, outerContactItems2);
			// imoStorage.putOuterContactListInfo(outerGroupContactMap);

			// Map<Integer, ArrayList<Integer>> innerGroupContactMap = new
			// HashMap<Integer, ArrayList<Integer>>();
			// imoStorage.getAllInnerContactListInfo(innerGroupContactMap);
			//
			// Map<Integer, InnerContactorItem> innerGroupIdMap = new
			// HashMap<Integer, InnerContactorItem>();
			// imoStorage.getInnerGroupInfo(innerGroupIdMap);
			//
			// Map<Integer, ArrayList<OuterContactItem>> outerGroupContactMap =
			// new HashMap<Integer, ArrayList<OuterContactItem>>();
			// imoStorage.getAllOuterContactListInfo(outerGroupContactMap);
			//
			// Map<Integer, OuterContactorItem> outerGroupIdMap = new
			// HashMap<Integer, OuterContactorItem>();
			// imoStorage.getOuterGroupInfo(outerGroupIdMap);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// imoStorage.sbsb();
		//
		// try {
		// s = System.currentTimeMillis();
		// imoStorage.sb2(employeeInfoItems);
		// e = System.currentTimeMillis();
		// System.out.println(e-s);
		// } catch (Exception e2) {
		// e2.printStackTrace();
		// }

		//
		// try {
		// for(int i=0;i<9;i++){
		// MessageInfo messageInfo = new MessageInfo(0, 1, "冯小卫",
		// EngineConst.uId, "刘晓军", "2012-06-1"+i, Functions.getTime(), "没屁事"+i,
		// 0, i, 0);
		// imoStorage.addMessage(messageInfo);
		// }
		// String vv = imoStorage.getLastUnReadMessage(1);
		// System.out.println(vv);
		// for(int i=0;i<9;i++){
		// MessageInfo messageInfo = new MessageInfo(0, 2, "冯小龙",
		// EngineConst.uId, "刘晓军", "2012-03-2"+i, Functions.getTime(), "烦恼"+i,
		// 0, i+10, 0);
		// imoStorage.addMessage(messageInfo);
		// }
		// String vv1 = imoStorage.getLastUnReadMessage(2);
		// System.out.println(vv1);
		// for(int i=0;i<9;i++){
		// MessageInfo messageInfo = new MessageInfo(0, 3, "大包",
		// EngineConst.uId, "刘晓军", "2011-06-1"+i, Functions.getTime(), "包子好吃"+i,
		// 0, i, 0);
		// imoStorage.addMessage(messageInfo);
		// }
		// String vv2 = imoStorage.getLastUnReadMessage(3);
		// System.out.println(vv2);
		// for(int i=0;i<9;i++){
		// MessageInfo messageInfo = new MessageInfo(0, 4, "米粉",
		// EngineConst.uId, "刘晓军", "1987-06-1"+i, Functions.getTime(), "米粉好吃"+i,
		// 0, i, 0);
		// imoStorage.addMessage(messageInfo);
		// }
		// String vv3 = imoStorage.getLastUnReadMessage(4);
		// System.out.println(vv3);
		// for(int i=0;i<9;i++){
		// MessageInfo messageInfo = new MessageInfo(0, 5, "油条",
		// EngineConst.uId, "刘晓军", "2017-06-1"+i, Functions.getTime(), "油条"+i,
		// 0, i+5, 0);
		// imoStorage.addMessage(messageInfo);
		// }
		// String vv4 = imoStorage.getLastUnReadMessage(5);
		// System.out.println(vv4);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// try {
		// RecentContactInfo recentContactInfo = new RecentContactInfo(1, 1,
		// "冯小卫", "没屁事", Functions.getTime(),9);
		// imoStorage.addRecentContact(recentContactInfo, "2012-06-18");
		//
		// recentContactInfo = new RecentContactInfo(1, 2, "冯小龙", "没屁事",
		// Functions.getTime(),9);
		// imoStorage.addRecentContact(recentContactInfo, "2012-03-28");
		//
		// recentContactInfo = new RecentContactInfo(1, 4, "米粉", "没屁事",
		// Functions.getTime(),9);
		// imoStorage.addRecentContact(recentContactInfo, "1987-06-18");
		//
		// recentContactInfo = new RecentContactInfo(1, 3, "大包", "没屁事",
		// Functions.getTime(),9);
		// imoStorage.addRecentContact(recentContactInfo, "2011-06-18");
		//
		// recentContactInfo = new RecentContactInfo(1, 5, "油条", "没屁事",
		// Functions.getTime(),9);
		// imoStorage.addRecentContact(recentContactInfo, "2017-06-18");
		//
		//
		// ArrayList<String> dateList = new ArrayList<String>();
		// ArrayList<Integer> idList = new ArrayList<Integer>();
		// HashMap<String, HashMap<Integer, RecentContactInfo>> recentContactMap
		// = new HashMap<String, HashMap<Integer,RecentContactInfo>>();
		// imoStorage.getRecentContactData(dateList, idList, recentContactMap);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// ArrayList<OfflineMsgItem> offlineMsgItems = new
		// ArrayList<OfflineMsgItem>();
		// for(int i=0;i<50;i++){
		// OfflineMsgItem offlineMsgItem = new OfflineMsgItem(0, i%10, i, i,
		// "消息"+i);
		// offlineMsgItems.add(offlineMsgItem);
		// }
		// try {
		// imoStorage.addMessages(offlineMsgItems);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// RecentContactInfo recentContactInfo = null;
		// for(int i=0;i<200;i++){
		// recentContactInfo = new RecentContactInfo(43, 1111, "小风", "", "", 11,
		// i);
		// try {
		// imoStorage.addRecentContact(recentContactInfo, Functions.getDate());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// try {
		// imoStorage.keepRecentContact(100);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// for(int i=0;i<50;i++){
		// // 存放到数据库
		// try {
		// MessageInfo msg = new MessageInfo(0, i, "", i+1,
		// "", Functions.getDate(), "", "内容"+i, 0);
		// IMOApp.imoStorage.addMessage(msg);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// try {
		// IMOApp.imoStorage.getMessageSum(1);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// ArrayList<MessageInfo> messageInfos = IMOApp.imoStorage.getMessage(9,
		// 0);
		// for(MessageInfo messageInfo:messageInfos){
		// System.out.println(messageInfo);
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//

		// try {
		// HashMap<Integer, DeptMaskItem> deptInfoMap = new HashMap<Integer,
		// DeptMaskItem>();
		// HashMap<Integer, int[]> deptUserIdsMap = new HashMap<Integer,
		// int[]>();
		// HashMap<Integer, int[]> deptUserNextSiblingMap = new HashMap<Integer,
		// int[]>();
		// HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap
		// = new HashMap<Integer, HashMap<Integer,EmployeeInfoItem>>();
		//
		// int[][] result = imoStorage.get( deptInfoMap,
		// deptUserIdsMap, deptUserNextSiblingMap, deptUserInfoMap);
		// System.out.println("deptId");
		// } catch (Exception e) {
		// }

		// try {
		// Map<Integer, DeptMaskItem> deptMaskItems = new HashMap<Integer,
		// DeptMaskItem>();
		// imoStorage.getDepts(deptMaskItems);
		// Set<Integer> keys = deptMaskItems.keySet();
		// for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
		// Integer key = it.next();
		// DeptMaskItem deptMaskItem = deptMaskItems.get(key);
		// System.out.println(deptMaskItem.getDept_id()+
		// deptMaskItem.getName()+
		// deptMaskItem.getParent_dept_id()+
		// deptMaskItem.getDept_uc()+
		// deptMaskItem.getFdept_user_uc()+
		// deptMaskItem.getFirst_child()+
		// deptMaskItem.getNext_sibling()+
		// deptMaskItem.getDesp()+deptMaskItem.getFax()+
		// deptMaskItem.getHide_dept_list()+
		// deptMaskItem.getFaddr()+
		// deptMaskItem.getFtel()+
		// deptMaskItem.getFwebsite()+
		// deptMaskItem.getFfirst_user());
		// }
		// } catch (Exception e) {
		// }

		// try {
		// Message message = new Message(10, 10, 1, "老子很火大",
		// System.currentTimeMillis());
		// imoStorage.addMessage(message);
		// message = new Message(10, 9, 2, "老子很火大", System.currentTimeMillis());
		// imoStorage.addMessage(message);
		// message = new Message(10, 10, 3, "老子很火大",
		// System.currentTimeMillis());
		// imoStorage.addMessage(message);
		//
		// imoStorage.deleteMessage(9);
		// ArrayList<Message> messages = imoStorage.getMessage(10);
		// for(Message message2:messages){
		// System.out.println(message2);
		// }
		// } catch (Exception e) {
		// System.out.println(e);
		// }
		// try {
		// imoStorage.addMessage(new Message(20, "我不是傻逼", 20120306));
		// imoStorage.addMessage(new Message(20, "你才是傻逼", 20120306));
		// imoStorage.addMessage(new Message(20, "我是傻逼", 20120306));
		// ArrayList<Message> ms = imoStorage.getMessage(20);
		// for (Message m : ms) {
		// System.out.println(m);
		// }
		//
		// imoStorage.deleteMessage(20);
		// System.out.println(imoStorage.getMessage(20).size());
		//
		// } catch (Exception e) {
		// }

		// try {
		// imoStorage.addEmployeesUID(43, new Integer[]{1,2,3,4,5,6,7,8,9},new
		// Integer[]{1,2,3,4,5,6,7,8,9});
		// } catch (Exception e) {
		// System.out.println(1111);
		// e.printStackTrace();
		// }
		//
		//
		// try {
		// Map<Integer, EmployeeInfoItem> employeeInfoItems = new
		// HashMap<Integer,EmployeeInfoItem>();
		// employeeInfoItems.put(1, new GetAllEmployeesInfoInPacket(null,0).new
		// EmployeeInfoItem(1, 1, "fengxiaowei", "fengxiaowei", 2));
		// employeeInfoItems.put(2, new GetAllEmployeesInfoInPacket(null,0).new
		// EmployeeInfoItem(2, 2, "fengxiaowei", "fengxiaowei", 3));
		// employeeInfoItems.put(3, new GetAllEmployeesInfoInPacket(null,0).new
		// EmployeeInfoItem(3, 3, "fengxiaowei", "fengxiaowei", 4));
		// imoStorage.addEmployeesInfo(10, employeeInfoItems);
		// } catch (Exception e) {
		// System.out.println(2222);
		// e.printStackTrace();
		// }
		//
		// try {
		// //int dept_id, int parent_dept_id, String name, String desp, int
		// dept_uc, String fax, String hide_dept_list, int first_child, int
		// next_sibling, String faddr, String ftel, String fwebsite, int
		// ffirst_user, int fdept_user_uc
		// Map<Integer, DeptMaskItem> deptMaskItems = new HashMap<Integer,
		// DeptMaskItem>();
		// deptMaskItems.put(1, new DeptMaskItem(1, 2, "sb", "sb", 1, "546",
		// "535", 3, 3, "5235", "6346547", "46547347", 3, 3));
		// deptMaskItems.put(2, new DeptMaskItem(2, 2, "sb", "sb", 1, "546",
		// "535", 3, 3, "5235", "6346547", "46547347", 3, 3));
		// deptMaskItems.put(3, new DeptMaskItem(3, 2, "sb", "sb", 1, "546",
		// "535", 3, 3, "5235", "6346547", "46547347", 3, 3));
		// imoStorage.addDepts(10, deptMaskItems);
		//
		// } catch (Exception e) {
		// System.out.println(3333);
		// e.printStackTrace();
		// }

		// imoStorage.addDeptAndUserUC(new int[] { 1, 2, 3, 4 }, new int[] { 11,
		// 12, 13, 14 }, new int[] { 21, 22, 23, 24 });
		//
		// int[][] result = imoStorage.getDeptAndUserUC();
		//
		// for (int i : result[0]) {
		// System.out.print(i + "  ");
		// }
		// System.out.println("");
		// for (int i : result[1]) {
		// System.out.print(i + "  ");
		// }
		// System.out.println("");
		// for (int i : result[2]) {
		// System.out.print(i + "  ");
		// }

		// try {
		// Corp corp = new Corp("1015", "imo运营中心", "111", "22", "333", "444",
		// "5555");
		// imoStorage.addCorpInfo(corp);
		// System.out.println(imoStorage.getCorpInfo("1015"));
		//
		// imoStorage.updateCorpInfo(new Corp("1015", "imo运营中心", "111", "22",
		// "AAAA", "BBBB", "CCCC"));
		// System.out.println(imoStorage.getCorpInfo("1015"));
		//
		// imoStorage.deleteCorpInfo("1015");
		// System.out.println(imoStorage.getCorpInfo("1015"));
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// ********************************
		// try {
		// imoStorage.addUser(new User(2, 2, "b", "2b", "2b", 1));
		// System.out.println(imoStorage.getUser(2));
		// imoStorage.updateUser(new User(2, 2, "4b", "3b", "3b", 1));
		// System.out.println(imoStorage.getUser(2));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// ********************************
		// try {
		// imoStorage.addDept(new Dept(1, 1, "研发1", 1, 1, 1, 1, 1, "好部门", "2b",
		// "没有", "汇成二村", "18917232050", "www.baidu.com", 2));
		// System.out.println(imoStorage.getDept(1, 1));
		//
		// imoStorage.updateDept(new Dept(1, 1, "研发1", 1, 1, 1, 1, 1, "好部门",
		// "2b", "没有", "汇成二村", "18917232049", "www.baidu.com", 2));
		// System.out.println(imoStorage.getDept(1, 1));
		//
		// imoStorage.deleteDept(1, 1);
		// System.out.println(imoStorage.getDept(1, 1));
		//
		// } catch (Exception e) {
		// System.out.println("出错了");
		// }
		// ********************************
		// try {
		// imoStorage.addRecentContact(new RecentContactor(10, 10, 20));
		// System.out.println(imoStorage.getRecentContact(10, 10));
		//
		// imoStorage.updateRecentContact(new RecentContactor(10, 10, 30));
		// System.out.println(imoStorage.getRecentContact(10, 10));
		//
		// imoStorage.deleteRecentContact(10, 10);
		// System.out.println(imoStorage.getRecentContact(10, 10));
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addExternalContactListInfo(new ExternalContactList(1, 2,
		// 3, 4));
		// System.out.println(imoStorage.getExternalContactListInfo(3));
		//
		// imoStorage.updateExternalContactListInfo(new ExternalContactList(10,
		// 20, 3, 40));
		// System.out.println(imoStorage.getExternalContactListInfo(3));
		//
		// imoStorage.deleteExternalContactListInfo(3);
		// System.out.println(imoStorage.getExternalContactListInfo(3));
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addExternalContactUC(new ExternalContactorUC(33));
		// System.out.println(imoStorage.getExternalContactUC());
		//
		// imoStorage.deleteExternalContactUC(33);
		// System.out.println(imoStorage.getExternalContactUC());
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addExternalGroupUC(new ExternalGroupUc(20));
		// System.out.println(imoStorage.getExternalGroupUC());
		//
		// imoStorage.deleteExternalGroupUC(20);
		// System.out.println(imoStorage.getExternalGroupUC());
		//
		// } catch (Exception e) {
		// }
		// ********************************
		// try {
		// imoStorage.addOuterGroupInfo(new OuterGroup(1111, "傻逼"));
		// System.out.println(imoStorage.getOuterGroupInfo(1111));
		//
		// imoStorage.updateOuterGroupInfo(new OuterGroup(1111, "牛逼"));
		// System.out.println(imoStorage.getOuterGroupInfo(1111));
		//
		// imoStorage.deleteOuterGroupInfo(1111);
		// System.out.println(imoStorage.getOuterGroupInfo(1111));
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

		// ********************************
		// try {
		// imoStorage.addDeptUserInfo(new DeptUser(11, 22, 33));
		// System.out.println(imoStorage.getDeptUserInfo(11, 22));
		//
		// imoStorage.updateDeptUserInfo(new DeptUser(11, 22, 44));
		// System.out.println(imoStorage.getDeptUserInfo(11, 22));
		//
		// imoStorage.deleteDeptUserInfo(11, 22);
		// System.out.println(imoStorage.getDeptUserInfo(11, 22));
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addInnerGroupInfo(new InnerGroup(1111, "傻逼1"));
		// System.out.println(imoStorage.getInnerGroupInfo(1111));
		//
		// imoStorage.updateInnerGroupInfo(new InnerGroup(1111, "牛逼1"));
		// System.out.println(imoStorage.getInnerGroupInfo(1111));
		//
		// imoStorage.deleteInnerGroupInfo(1111);
		// System.out.println(imoStorage.getInnerGroupInfo(1111));
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addInnerContactListInfo(new InnerContactList(11, 21, 31,
		// 41));
		// System.out.println(imoStorage.getInnerContactListInfo(31));
		//
		// imoStorage.updateInnerContactListInfo(new InnerContactList(10, 20,
		// 31, 40));
		// System.out.println(imoStorage.getInnerContactListInfo(31));
		//
		// imoStorage.deleteInnerContactListInfo(31);
		// System.out.println(imoStorage.getInnerContactListInfo(31));
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addInnerGroupUC(new InnerGroupUc(234));
		// System.out.println(imoStorage.getInnerGroupUC());
		//
		// imoStorage.deleteInnerGroupUC(234);
		// System.out.println(imoStorage.getInnerGroupUC());
		// } catch (Exception e) {
		// }

		// ********************************
		// try {
		// imoStorage.addInnerContactUC(new InnerContactorUC(123));
		// System.out.println(imoStorage.getInnerContactUC());
		//
		// imoStorage.deleteInnerContactUC(123);
		// System.out.println(imoStorage.getInnerContactUC());
		//
		// } catch (Exception e) {
		// }

		imoStorage.close();
	}
}