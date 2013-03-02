package com.imo.db.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.imo.db.entity.Dept;
import com.imo.db.entity.DeptUser;
import com.imo.db.entity.ExternalContactList;
import com.imo.db.entity.ExternalContactorUC;
import com.imo.db.entity.ExternalGroupUc;
import com.imo.db.entity.InnerContactList;
import com.imo.db.entity.InnerContactorUC;
import com.imo.db.entity.InnerGroup;
import com.imo.db.entity.InnerGroupUc;
import com.imo.db.entity.MessageInfo;
import com.imo.db.entity.OuterGroup;
import com.imo.db.entity.User;
import com.imo.global.Globe;
import com.imo.module.contact.OuterContactBasicInfo;
import com.imo.module.contact.OuterContactItem;
import com.imo.module.dialogue.recent.RecentContactInfo;
import com.imo.network.net.EngineConst;
import com.imo.network.packages.CorpMaskItem;
import com.imo.network.packages.DeptMaskItem;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.InnerContactorItem;
import com.imo.network.packages.OfflineMsgItem;
import com.imo.network.packages.OuterContactorItem;
import com.imo.util.Functions;

/**
 * 方法addEmployeesUID(2003),getDeptAndUserUC和addDeptAndUserUC(2009),addDepts(2010
 * ),addEmployeesInfo(3009)可用。
 */
public class IMOStorage {
	private static String CorpInfo = DataHelper.CorpInfo;
	private static String DeptInfo = DataHelper.DeptInfo;
	private static String DeptAndUserUC = DataHelper.DeptAndUserUC;
	private static String DeptUserInfo = DataHelper.DeptUserInfo;
	private static String OuterGroupInfo = DataHelper.OuterGroupInfo;
	private static String RecentContact = DataHelper.RecentContact;
	private static String UserBaseInfo = DataHelper.UserBaseInfo;
	public static String UserProfile = DataHelper.UserProfile;
	private static String InnerGroupInfo = DataHelper.InnerGroupInfo;
	private static String InnerContactListInfo = DataHelper.InnerContactListInfo;
	private static String ExternalContactListInfo = DataHelper.ExternalContactListInfo;
	private static String InnerGroupUC = DataHelper.InnerGroupUC;
	private static String InnerContactUC = DataHelper.InnerContactUC;
	private static String ExternalGroupUC = DataHelper.ExternalGroupUC;
	private static String ExternalContactUC = DataHelper.ExternalContactUC;
	private static String MessageInfo = DataHelper.MessageInfo;
	private static String OuterContactInfo = DataHelper.OuterContactInfo;
	private static String OuterCorpInfo = DataHelper.OuterCorpInfo;

	private Context mContext;
	private DataHelper dataHelper;
	private SQLiteDatabase mDatabase;
	private static IMOStorage instance;

	private String sql;

	private IMOStorage(Context context) {
		this.mContext = context;
	}

	public static IMOStorage getInstance(Context context) {
		if (instance == null) {
			synchronized (IMOStorage.class) {
				if (instance == null) {
					instance = new IMOStorage(context);
				}
			}
		}
		return instance;
	}

	public boolean isOpen() {
		return mDatabase.isOpen();
	}

	public synchronized void open(Integer uId) throws Exception {
		if (dataHelper == null) {
			dataHelper = new DataHelper(mContext, uId);
		}
		this.mDatabase = dataHelper.getDb();
	}

	public synchronized void close() {
		if (mDatabase != null && mDatabase.isOpen()) {
			this.mDatabase.close();
			this.mDatabase = null;
			dataHelper = null;
		}
	}

	// public ArrayList<int[]> search(String key) {
	// sql = "select * from  " + UserBaseInfo + " where Name like '%" + key
	// + "%' or Account like '%" + key + "%' or SimplePY like '%"
	// + key + "%'";
	//
	// Cursor cursor = null;
	// ArrayList<int[]> list = null;
	// try {
	// cursor = mDatabase.rawQuery(sql, null);
	// int count = cursor.getCount();
	// if (count < 1)
	// return null;
	//
	// list = new ArrayList<int[]>();
	// for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
	// .moveToNext()) {
	// int uId = cursor.getInt(0);
	// int did = cursor.getInt(2);
	// list.add(new int[] { did, uId });
	// }
	// } catch (Exception e) {
	// } finally {
	// if (cursor != null && !cursor.isClosed()) {
	// cursor.close();
	// }
	// }
	//
	// return list;
	// }

	public ArrayList<Integer> search(String key) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		searchUserBaseInfo(key, list);
		searchOuterContactInfo(key, list);
		return list;
	}

	public ArrayList<Integer> findAllUids() {

		sql = "select Uid from  " + UserBaseInfo;

		Cursor cursor = null;
		ArrayList<Integer> result = new ArrayList<Integer>();
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return result;

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				result.add(cursor.getInt(0));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;

	}

	public Integer findDidByUid(int uid) {
		sql = "select Did from  " + UserBaseInfo + " where Uid = " + uid;

		Cursor cursor = null;
		Integer result = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return result;

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				result = cursor.getInt(0);
			}
			return result;
		} catch (Exception e) {} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;
	}

	public void searchUserBaseInfo(String key, ArrayList<Integer> list) {
		sql = "select * from  " + UserBaseInfo + " where Name like '%" + key + "%' or SimplePY like '%" + key + "%'";

		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return;

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				list.add(cursor.getInt(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public void searchOuterContactInfo(String key, ArrayList<Integer> list) {
		sql = "select * from  " + OuterContactInfo + " where Name like '%" + key + "%' or SimplePY like '%" + key + "%'";

		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return;

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				list.add(cursor.getInt(1));
			}
		} catch (Exception e) {} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public boolean update(int[] deptid, int[] dept_uc, int[] dept_user_uc, ArrayList<Integer> needAddLocalDept, ArrayList<Integer> needDelLocalDept, ArrayList<Integer> needUpdateLocalDept, ArrayList<Integer> needUpdateLocalDeptUser, HashMap<Integer, DeptMaskItem> deptInfoMap,
			HashMap<Integer, int[]> deptUserIdsMap, HashMap<Integer, int[]> deptUserNextSiblingMap, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap) throws Exception {
		mDatabase.beginTransaction();
		try {
			updateDeptAndUserUC(deptid, dept_uc, dept_user_uc);

			deleteDepts(needDelLocalDept.toArray());

			// 针对有dept_user_uc的情况
			updateDepts(EngineConst.cId, needUpdateLocalDept, deptInfoMap);

			// 针对没有dept_user_uc的情况
			// deleteDepts(needUpdateLocalDept.toArray());
			// addDepts(EngineConst.cId, needUpdateLocalDept, deptInfoMap);

			addDepts(EngineConst.cId, needAddLocalDept, deptInfoMap);

			deleteEmployeesByDeptId(needUpdateLocalDeptUser);

			updateEmployeesInfo(EngineConst.cId, needUpdateLocalDeptUser, deptUserInfoMap);

			addEmployeesUID(deptUserIdsMap, deptUserNextSiblingMap);// ?

			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return true;
	}

	// public boolean update(int[] deptid, int[] dept_uc, int[] dept_user_uc,
	// ArrayList<Integer> needAddLocalDept,
	// ArrayList<Integer> needDelLocalDept,
	// ArrayList<Integer> needUpdateLocalDept,
	// ArrayList<Integer> needUpdateLocalDeptUser,
	// HashMap<Integer, DeptMaskItem> deptInfoMap,
	// HashMap<Integer, int[]> deptUserIdsMap,
	// HashMap<Integer, int[]> deptUserNextSiblingMap,
	// HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap)
	// throws Exception {
	// mDatabase.beginTransaction();
	// try {
	// updateDeptAndUserUC(deptid, dept_uc, dept_user_uc);
	//
	// deleteDepts(needDelLocalDept.toArray());
	//
	// updateDepts(EngineConst.cId, needUpdateLocalDept, deptInfoMap);
	//
	// addDepts(EngineConst.cId, needAddLocalDept, deptInfoMap);
	//
	// updateEmployeesInfo(EngineConst.cId, needUpdateLocalDeptUser,
	// deptUserInfoMap);
	//
	// updateEmployeesUID(deptUserIdsMap, deptUserNextSiblingMap);// ?
	//
	// mDatabase.setTransactionSuccessful();
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// mDatabase.endTransaction();
	// }
	//
	// return true;
	// }

	/**
	 * 第一次拉取数据结束以后，调用这个方法，把所有数据存入数据库
	 * 
	 * @param deptid
	 * @param dept_uc
	 * @param dept_user_uc
	 * @param deptInfoMap
	 * @param deptUserIdsMap
	 * @param deptUserNextSiblingMap
	 * @param deptUserInfoMap
	 * @return
	 * @throws Exception
	 */
	public boolean add(int[] deptid, int[] dept_uc, int[] dept_user_uc, HashMap<Integer, DeptMaskItem> deptInfoMap, HashMap<Integer, int[]> deptUserIdsMap, HashMap<Integer, int[]> deptUserNextSiblingMap, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap) throws Exception {
		mDatabase.beginTransaction();
		try {
			addDeptAndUserUC(deptid, dept_uc, dept_user_uc);
			// 这里使用的CID是本公司cid，后面可能需要更改
			addDepts(EngineConst.cId, deptInfoMap);

			addEmployeesUID(deptUserIdsMap, deptUserNextSiblingMap);
			// 这里使用的CID是本公司cid，后面可能需要更改
			addEmployeesInfo(EngineConst.cId, deptUserInfoMap);

			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return true;
	}

	/**
	 * 把数据库所有需要拉取的数据拉取出来，但是返回的方式比较恶心，deptInfoMap，deptUserIdsMap，
	 * deptUserNextSiblingMap，deptUserInfoMap由逻辑层初始化。
	 * 
	 * @param deptInfoMap
	 * @param deptUserIdsMap
	 * @param deptUserNextSiblingMap
	 * @param deptUserInfoMap
	 * @return int[] dId, int[] uc, int[] user_uc组成的二维数组
	 * @throws Exception
	 */
	public int[][] get(HashMap<Integer, DeptMaskItem> deptInfoMap, HashMap<Integer, int[]> deptUserIdsMap, HashMap<Integer, int[]> deptUserNextSiblingMap, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap) throws Exception {
		mDatabase.beginTransaction();
		int[][] result = null;
		try {
			result = getDeptAndUserUC();

			int[] deptIds = getDepts(deptInfoMap);

			getEmployeesUID(deptIds, deptUserIdsMap, deptUserNextSiblingMap);
			getEmployeesInfo(deptIds, deptUserInfoMap);

			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}
		return result;

	}

	public boolean addDeptAndUserUC(int[] dId, int[] uc, int[] user_uc) {
		if (dId == null || uc == null || user_uc == null)
			return false;
		sql = "replace into " + DeptAndUserUC + "(Did , UC , USER_UC )" + " values (?,?,?)";
		for (int i = 0; i < dId.length; i++) {
			mDatabase.execSQL(sql, new Object[] {
					dId[i], uc[i], user_uc[i]
			});
		}
		return true;
	}

	public void getDeptAndUserUC(int[] deptid, int[] dept_uc, int[] dept_user_uc) throws Exception {
		sql = "select * from " + DeptAndUserUC;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		int count = cursor.getCount();
		if (count < 1)
			return;

		int i = 0;
		deptid = new int[count];
		dept_uc = new int[count];
		dept_user_uc = new int[count];

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			deptid[i] = cursor.getInt(0);
			dept_uc[i] = cursor.getInt(1);
			dept_user_uc[i] = cursor.getInt(2);
			i++;
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	public int[][] getDeptAndUserUC() {
		sql = "select * from " + DeptAndUserUC + " order by Did desc";
		Cursor cursor = null;
		int[][] result = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return null;

			int i = 0;
			result = new int[3][count];

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				result[0][i] = cursor.getInt(0);
				result[1][i] = cursor.getInt(1);
				result[2][i] = cursor.getInt(2);
				i++;
			}
		} catch (Exception e) {} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 删除部门的所有信息，包括部门下员工信息。谨慎调用。涉及四张表
	 * 
	 * @param deptIds
	 */
	public void deleteDepts(Object[] deptIds) {
		sql = "delete from " + DeptInfo + " where Did = ?";
		for (Object deptId : deptIds) {
			mDatabase.execSQL(sql, new Object[] {
				deptId
			});
		}
		sql = "delete from " + DeptAndUserUC + " where Did = ?";
		for (Object deptId : deptIds) {
			mDatabase.execSQL(sql, new Object[] {
				deptId
			});
		}
		sql = "delete from " + DeptUserInfo + " where Did = ?";
		for (Object deptId : deptIds) {
			mDatabase.execSQL(sql, new Object[] {
				deptId
			});
		}
		sql = "delete from " + UserBaseInfo + " where Did = ?";
		for (Object deptId : deptIds) {
			mDatabase.execSQL(sql, new Object[] {
				deptId
			});
		}

	}

	/**
	 * update uc和user_uc 涉及一张表，逻辑是先删除DeptAndUserUC，然后添加
	 * 
	 * @param dId
	 * @param uc
	 * @param user_uc
	 */
	public void updateDeptAndUserUC(int[] dId, int[] uc, int[] user_uc) {
		sql = "delete from " + DeptAndUserUC;
		mDatabase.execSQL(sql);

		addDeptAndUserUC(dId, uc, user_uc);
	}

	/**
	 * update uc和user_uc 涉及两张表
	 * 
	 * @param dId
	 * @param uc
	 * @param user_uc
	 */
	public void updateDeptAndUserUC1(int[] dId, int[] uc, int[] user_uc) {
		sql = "update " + DeptAndUserUC + " set " + "UC = ?,USER_UC = ? where Did = ?";
		for (int i = 0; i < dId.length; i++) {
			mDatabase.execSQL(sql, new Object[] {
					uc[i], user_uc[i], dId[i]
			});
		}

		sql = "update " + DeptInfo + " set " + "UC = ?,DeptUserUC = ? where Did = ?";
		for (int i = 0; i < dId.length; i++) {
			mDatabase.execSQL(sql, new Object[] {
					uc[i], user_uc[i], dId[i]
			});
		}

	}

	/**
	 * 批量添加员工UID，deptUserNextSibling
	 * 
	 * @param deptUserIdsMap
	 * @param deptUserNextSiblingMap
	 * @return
	 * @throws Exception
	 */
	public boolean addEmployeesUID(HashMap<Integer, int[]> deptUserIdsMap, HashMap<Integer, int[]> deptUserNextSiblingMap) throws Exception {
		if (deptUserIdsMap == null || deptUserNextSiblingMap == null)
			return false;
		sql = "replace into " + DeptUserInfo + "(Did, Uid, NextSiblingUid) values (?,?,?)";
		SQLiteStatement sqLiteStatement = mDatabase.compileStatement(sql);
		Set<Integer> keys = deptUserIdsMap.keySet();
		Integer dId = null;
		int[] deptUserIds = null;
		int[] deptUserNextSibling = null;
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			dId = it.next();
			deptUserIds = deptUserIdsMap.get(dId);
			deptUserNextSibling = deptUserNextSiblingMap.get(dId);
			if (dId == null || deptUserIds == null || deptUserNextSibling == null)
				continue;
			for (int i = 0; i < deptUserIds.length; i++) {
				try {
					sqLiteStatement.bindLong(1, dId);
					sqLiteStatement.bindLong(2, deptUserIds[i]);
					sqLiteStatement.bindLong(3, deptUserNextSibling[i]);
					sqLiteStatement.executeInsert();
				} catch (Exception e) {
					throw e;
				}
			}
		}

		return true;
	}

	public boolean updateEmployeesUID(HashMap<Integer, int[]> deptUserIdsMap, HashMap<Integer, int[]> deptUserNextSiblingMap) throws Exception {
		if (deptUserIdsMap == null || deptUserNextSiblingMap == null)
			return false;
		Set<Integer> keys = deptUserIdsMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer dId = it.next();
			int[] deptUserIds = deptUserIdsMap.get(dId);
			int[] deptUserNextSibling = deptUserNextSiblingMap.get(dId);
			updateEmployeesUID(dId, deptUserIds, deptUserNextSibling);
		}
		return true;
	}

	/**
	 * 批量得到员工UID，deptUserNextSibling
	 * 
	 * @param deptIds
	 * @param deptUserIdsMap
	 * @param deptUserNextSiblingMap
	 * @throws Exception
	 */
	public void getEmployeesUID(int[] deptIds, HashMap<Integer, int[]> deptUserIdsMap, HashMap<Integer, int[]> deptUserNextSiblingMap) throws Exception {

		sql = "select * from " + DeptUserInfo + " where Did = ?";

		Cursor cursor = null;
		int[] uIds = null;
		int[] nextSiblingUids = null;
		for (int deptId : deptIds) {
			try {
				cursor = mDatabase.rawQuery(sql, new String[] {
					deptId + ""
				});
				int count = cursor.getCount();
				if (count < 1)
					continue;
				uIds = new int[count];
				nextSiblingUids = new int[count];
				int i = 0;

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					uIds[i] = cursor.getInt(1);
					nextSiblingUids[i] = cursor.getInt(2);
					i++;
				}
				deptUserIdsMap.put(deptId, uIds);
				deptUserNextSiblingMap.put(deptId, nextSiblingUids);
			} catch (Exception e) {} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}

	// public boolean sb1(List<EmployeeInfoItem> employeeInfoItems)
	// throws Exception {
	//
	// sql = "insert into " + UserBaseInfo
	// + "(Uid ,Cid ,Did,Flag ,Account ,SimplePY,Name ,Gender)"
	// + " values (?,?,?,?,?,?,?,?)";
	// SQLiteStatement sqLiteStatement = mDatabase.compileStatement(sql);
	//
	// mDatabase.beginTransaction();
	// try {
	// for (EmployeeInfoItem employeeInfoItem : employeeInfoItems) {
	// sqLiteStatement.bindLong(1, employeeInfoItem.getUid());
	// sqLiteStatement.bindLong(2, 0);
	// sqLiteStatement.bindLong(3, 0);
	// sqLiteStatement.bindLong(4, employeeInfoItem.getFlag());
	// sqLiteStatement.bindString(5,
	// employeeInfoItem.getUser_account());
	// sqLiteStatement.bindString(6, employeeInfoItem.getName());
	// sqLiteStatement.bindString(7, employeeInfoItem.getName());
	// sqLiteStatement.bindLong(8, employeeInfoItem.getGender());
	// sqLiteStatement.executeInsert();
	// }
	// mDatabase.setTransactionSuccessful();
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// mDatabase.endTransaction();
	// }
	//
	// return true;
	// }

	/**
	 * 批量添加员工信息
	 * 
	 * @param cId
	 * @param employeeInfoItems
	 * @return
	 * @throws Exception
	 */
	public boolean addEmployeesInfo(Integer cId, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> employeeInfoItems) throws Exception {
		if (employeeInfoItems == null)
			return false;

		sql = "replace into " + UserBaseInfo + "(Uid ,Cid ,Did,Flag ,Account ,SimplePY,Name ,Gender)" + " values (?,?,?,?,?,?,?,?)";
		SQLiteStatement sqLiteStatement = mDatabase.compileStatement(sql);
		Set<Integer> dIds = employeeInfoItems.keySet();
		Integer dId = null;
		Integer uId = null;
		HashMap<Integer, EmployeeInfoItem> employeeInfoItemMap = null;
		EmployeeInfoItem employeeInfoItem = null;
		try {
			for (Iterator<Integer> it = dIds.iterator(); it.hasNext();) {
				dId = it.next();
				employeeInfoItemMap = employeeInfoItems.get(dId);
				Set<Integer> uIds = employeeInfoItemMap.keySet();
				for (Iterator<Integer> it1 = uIds.iterator(); it1.hasNext();) {
					uId = it1.next();
					employeeInfoItem = employeeInfoItemMap.get(uId);
					sqLiteStatement.bindLong(1, employeeInfoItem.getUid());
					sqLiteStatement.bindLong(2, cId);
					sqLiteStatement.bindLong(3, dId);
					sqLiteStatement.bindLong(4, employeeInfoItem.getFlag());
					sqLiteStatement.bindString(5, employeeInfoItem.getUser_account());
					sqLiteStatement.bindString(6, Functions.getChinessFirstSpellInstance().GetChineseSpell(employeeInfoItem.getName()));
					sqLiteStatement.bindString(7, employeeInfoItem.getName());
					sqLiteStatement.bindLong(8, employeeInfoItem.getGender());
					sqLiteStatement.executeInsert();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	// /**
	// * 批量添加员工信息
	// *
	// * @param cId
	// * @param employeeInfoItems
	// * @return
	// * @throws Exception
	// */
	// public boolean addEmployeesInfo(
	// Integer cId,
	// HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> employeeInfoItems)
	// throws Exception {
	// if (employeeInfoItems == null)
	// return false;
	//
	// sql = "insert into " + UserBaseInfo
	// + "(Uid ,Cid ,Did,Flag ,Account ,SimplePY,Name ,Gender)"
	// + " values (?,?,?,?,?,?,?,?)";
	// Set<Integer> dIds = employeeInfoItems.keySet();
	// try {
	// for (Iterator<Integer> it = dIds.iterator(); it.hasNext();) {
	// Integer dId = it.next();
	// HashMap<Integer, EmployeeInfoItem> employeeInfoItemMap =
	// employeeInfoItems
	// .get(dId);
	// Set<Integer> uIds = employeeInfoItemMap.keySet();
	// for (Iterator<Integer> it1 = uIds.iterator(); it1.hasNext();) {
	// Integer uId = it1.next();
	// EmployeeInfoItem employeeInfoItem = employeeInfoItemMap
	// .get(uId);
	// mDatabase
	// .execSQL(
	// sql,
	// new Object[] {
	// employeeInfoItem.getUid(),
	// cId,
	// dId,
	// employeeInfoItem.getFlag(),
	// employeeInfoItem.getUser_account(),
	// Functions
	// .getChinessFirstSpellInstance()
	// .GetChineseSpell(
	// employeeInfoItem
	// .getName()),
	// employeeInfoItem.getName(),
	// employeeInfoItem.getGender() });
	// }
	// }
	// } catch (Exception e) {
	// throw e;
	// }
	// return true;
	// }

	public boolean sb2(List<EmployeeInfoItem> employeeInfoItems) throws Exception {

		sql = "replace into " + UserBaseInfo + "(Uid ,Cid ,Did,Flag ,Account ,SimplePY,Name ,Gender)" + " values (?,?,?,?,?,?,?,?)";

		mDatabase.beginTransaction();
		try {
			for (EmployeeInfoItem employeeInfoItem : employeeInfoItems) {
				mDatabase.execSQL(sql, new Object[] {
						employeeInfoItem.getUid(), 0, 0, employeeInfoItem.getFlag(), employeeInfoItem.getUser_account(), employeeInfoItem.getName(), employeeInfoItem.getName(), employeeInfoItem.getGender()
				});
			}
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return true;
	}

	public void sbsb() {
		try {
			sql = "delete from " + UserBaseInfo;
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNameByUid(int uid) {
		sql = "select Name from " + UserBaseInfo + " where Uid = ?";
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, new String[] {
				uid + ""
			});
			int count = cursor.getCount();
			if (count < 1)
				return "";
			cursor.moveToFirst();
			String name = cursor.getString(0);
			return name;
		} catch (Exception e) {

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return "";
	}

	/**
	 * 批量获取员工信息
	 * 
	 * @param deptIds
	 * @param employeeInfoItems
	 */
	public void getEmployeesInfo(int[] deptIds, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> employeeInfoItems) {

		sql = "select * from " + UserBaseInfo + " where Did = ?";
		HashMap<Integer, EmployeeInfoItem> employeeInfoItemMap = null;
		Cursor cursor = null;
		int uId = 0;
		EmployeeInfoItem employeeInfoItem = null;
		for (int deptId : deptIds) {
			try {
				cursor = mDatabase.rawQuery(sql, new String[] {
					deptId + ""
				});
				int count = cursor.getCount();
				if (count < 1)
					continue;

				employeeInfoItemMap = new HashMap<Integer, EmployeeInfoItem>();
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					uId = cursor.getInt(0);
					employeeInfoItem = new EmployeeInfoItem(uId, cursor.getInt(3), cursor.getString(4), cursor.getString(6), cursor.getInt(7));
					employeeInfoItemMap.put(uId, employeeInfoItem);
				}
				employeeInfoItems.put(deptId, employeeInfoItemMap);

			} catch (Exception e) {

			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}

		}

	}

	String delDeptString = "delete from " + DeptInfo + " where Cid= ? and Did= ?";

	public boolean addDepts(Integer cId, ArrayList<Integer> needAddLocalDept, Map<Integer, DeptMaskItem> deptMaskItems) throws Exception {
		if (deptMaskItems == null)
			return false;

		sql = "replace into " + DeptInfo + "(Cid ,Did ,Name ,PDid ,UC ,DeptUserUC ,FirstChild ,NextSibling ,Desp ,Fax ,HideDeptList ,Addr ,Tel ,Website ,FirstChildUser)" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Set<Integer> keys = deptMaskItems.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			if (needAddLocalDept.contains(key)) {
				DeptMaskItem deptMaskItem = deptMaskItems.get(key);
				try {
					mDatabase.execSQL(delDeptString, new Object[] {
							cId, deptMaskItem.getDept_id()
					});
					mDatabase.execSQL(sql, new Object[] {
							cId, deptMaskItem.getDept_id(), deptMaskItem.getName(), deptMaskItem.getParent_dept_id(), deptMaskItem.getDept_uc(), deptMaskItem.getFdept_user_uc(), deptMaskItem.getFirst_child(), deptMaskItem.getNext_sibling(), deptMaskItem.getDesp(), deptMaskItem.getFax(),
							deptMaskItem.getHide_dept_list(), deptMaskItem.getFaddr(), deptMaskItem.getFtel(), deptMaskItem.getFwebsite(), deptMaskItem.getFfirst_user()
					});
				} catch (Exception e) {
					throw e;
				}
			}
		}

		return true;
	}

	public boolean sb1(List<EmployeeInfoItem> employeeInfoItems) throws Exception {

		sql = "replace into " + UserBaseInfo + "(Uid ,Cid ,Did,Flag ,Account ,SimplePY,Name ,Gender)" + " values (?,?,?,?,?,?,?,?)";
		SQLiteStatement sqLiteStatement = mDatabase.compileStatement(sql);

		mDatabase.beginTransaction();
		try {
			for (EmployeeInfoItem employeeInfoItem : employeeInfoItems) {
				sqLiteStatement.bindLong(1, employeeInfoItem.getUid());
				sqLiteStatement.bindLong(2, 0);
				sqLiteStatement.bindLong(3, 0);
				sqLiteStatement.bindLong(4, employeeInfoItem.getFlag());
				sqLiteStatement.bindString(5, employeeInfoItem.getUser_account());
				sqLiteStatement.bindString(6, employeeInfoItem.getName());
				sqLiteStatement.bindString(7, employeeInfoItem.getName());
				sqLiteStatement.bindLong(8, employeeInfoItem.getGender());
				sqLiteStatement.executeInsert();
			}
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return true;
	}

	public boolean addDepts(Integer cId, Map<Integer, DeptMaskItem> deptMaskItems) throws Exception {
		if (deptMaskItems == null)
			return false;

		sql = "replace into " + DeptInfo + "(Cid ,Did ,Name ,PDid ,UC ,DeptUserUC ,FirstChild ,NextSibling ,Desp ,Fax ,HideDeptList ,Addr ,Tel ,Website ,FirstChildUser)" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		SQLiteStatement sqLiteStatement = mDatabase.compileStatement(sql);
		Set<Integer> keys = deptMaskItems.keySet();
		Integer key = null;
		DeptMaskItem deptMaskItem = null;
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			key = it.next();
			deptMaskItem = deptMaskItems.get(key);
			try {
				sqLiteStatement.bindLong(1, cId);
				sqLiteStatement.bindLong(2, deptMaskItem.getDept_id());
				sqLiteStatement.bindString(3, deptMaskItem.getName());
				sqLiteStatement.bindLong(4, deptMaskItem.getParent_dept_id());
				sqLiteStatement.bindLong(5, deptMaskItem.getDept_uc());
				sqLiteStatement.bindLong(6, deptMaskItem.getFdept_user_uc());
				sqLiteStatement.bindLong(7, deptMaskItem.getFirst_child());
				sqLiteStatement.bindLong(8, deptMaskItem.getNext_sibling());
				sqLiteStatement.bindString(9, deptMaskItem.getDesp());
				sqLiteStatement.bindString(10, deptMaskItem.getFax());
				sqLiteStatement.bindString(11, deptMaskItem.getHide_dept_list());
				sqLiteStatement.bindString(12, deptMaskItem.getFaddr());
				sqLiteStatement.bindString(13, deptMaskItem.getFtel());
				sqLiteStatement.bindString(14, deptMaskItem.getFwebsite());
				sqLiteStatement.bindLong(15, deptMaskItem.getFfirst_user());
				sqLiteStatement.executeInsert();
			} catch (Exception e) {
				throw e;
			}
		}

		return true;
	}

	public int[] getDepts(Map<Integer, DeptMaskItem> deptMaskItems) throws Exception {
		sql = "select * from " + DeptInfo;
		Cursor cursor = null;
		int[] deptIds = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return null;
			deptIds = new int[count];
			int i = 0;
			DeptMaskItem dept = null;
			int deptId = 0;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				deptId = cursor.getInt(1);
				dept = new DeptMaskItem(deptId, cursor.getInt(3), cursor.getString(2), cursor.getString(8), cursor.getInt(4), cursor.getString(9), cursor.getString(10), cursor.getInt(6), cursor.getInt(7), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getInt(14),
						cursor.getInt(5));
				deptMaskItems.put(deptId, dept);
				deptIds[i] = deptId;
				i++;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return deptIds;

	}

	/**
	 * 批量修改部门信息
	 * 
	 * @param cId
	 * @param deptMaskItems
	 * @throws Exception
	 */
	public void updateDepts(Integer cId, ArrayList<Integer> needUpdateLocalDept, Map<Integer, DeptMaskItem> deptMaskItems) throws Exception {
		if (deptMaskItems == null)
			return;
		sql = "update " + DeptInfo + " set " + "Cid = " + EngineConst.cId + " ,Name=? ,PDid=? ,UC=? ,DeptUserUC=? ,FirstChild=? ,NextSibling=? ,Desp=? ,Fax=? ,HideDeptList=? ,Addr=? ,Tel=? ,Website=? ,FirstChildUser=? " + " where Did = ?";
		Set<Integer> keys = deptMaskItems.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			if (needUpdateLocalDept.contains(key)) {
				DeptMaskItem deptMaskItem = deptMaskItems.get(key);
				try {
					mDatabase.execSQL(sql, new Object[] {
							deptMaskItem.getName(), deptMaskItem.getParent_dept_id(), deptMaskItem.getDept_uc(), deptMaskItem.getFdept_user_uc(), deptMaskItem.getFirst_child(), deptMaskItem.getNext_sibling(), deptMaskItem.getDesp(), deptMaskItem.getFax(), deptMaskItem.getHide_dept_list(),
							deptMaskItem.getFaddr(), deptMaskItem.getFtel(), deptMaskItem.getFwebsite(), deptMaskItem.getFfirst_user(), deptMaskItem.getDept_id()
					});
				} catch (Exception e) {
					throw e;
				}
			}

		}

	}

	/**
	 * 批量修改部门信息
	 * 
	 * @param cId
	 * @param deptMaskItems
	 * @throws Exception
	 */
	public void updateDepts(Integer cId, Map<Integer, DeptMaskItem> deptMaskItems) throws Exception {
		if (deptMaskItems == null)
			return;
		sql = "update " + DeptInfo + " set " + "Cid = " + EngineConst.cId + " ,Name=? ,PDid=? ,UC=? ,DeptUserUC=? ,FirstChild=? ,NextSibling=? ,Desp=? ,Fax=? ,HideDeptList=? ,Addr=? ,Tel=? ,Website=? ,FirstChildUser=? " + " where Did = ?";
		Set<Integer> keys = deptMaskItems.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			DeptMaskItem deptMaskItem = deptMaskItems.get(key);
			try {
				mDatabase.execSQL(sql,
						new Object[] {
								deptMaskItem.getName(), deptMaskItem.getParent_dept_id(), deptMaskItem.getDept_uc(), deptMaskItem.getFdept_user_uc(), deptMaskItem.getFirst_child(), deptMaskItem.getNext_sibling(), deptMaskItem.getDesp(), deptMaskItem.getFax(), deptMaskItem.getHide_dept_list(),
								deptMaskItem.getFaddr(), deptMaskItem.getFtel(), deptMaskItem.getFwebsite(), deptMaskItem.getFfirst_user(), deptMaskItem.getDept_id()
						});
			} catch (Exception e) {
				throw e;
			}
		}

	}

	public void deleteEmployeesByDeptId(ArrayList<Integer> needUpdateLocalDeptUser) throws Exception {
		if (needUpdateLocalDeptUser == null)
			return;
		sql = "delete from " + UserBaseInfo + " where " + "Did = ?";
		Object[] objects = needUpdateLocalDeptUser.toArray();
		try {
			for (int i = 0; i < objects.length; i++) {
				mDatabase.execSQL(sql, new Object[] {
					objects[i]
				});
			}
		} catch (Exception e) {
			throw e;
		}

		sql = "delete from " + DeptUserInfo + " where " + "Did = ?";
		try {
			for (int i = 0; i < objects.length; i++) {
				mDatabase.execSQL(sql, new Object[] {
					objects[i]
				});
			}
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 本来的逻辑应该是删除对应部门下所有员工，然后添加。但是删除操作已经在删除部门的时候完成，所以这里仅仅需要添加即可
	 * 
	 * @param cId
	 * @param needUpdateLocalDeptUser
	 *        有员工需要更新的部门ID列表
	 * @param employeeInfoItems
	 * @throws Exception
	 */
	public void updateEmployeesInfo(Integer cId, ArrayList<Integer> needUpdateLocalDeptUser, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> employeeInfoItems) throws Exception {

		if (employeeInfoItems == null)
			return;

		sql = "replace into " + UserBaseInfo + "(Uid ,Cid ,Did,Flag ,Account ,SimplePY,Name ,Gender)" + " values (?,?,?,?,?,?,?,?)";
		Set<Integer> dIds = employeeInfoItems.keySet();
		try {
			for (Iterator<Integer> it = dIds.iterator(); it.hasNext();) {
				Integer dId = it.next();
				if (needUpdateLocalDeptUser.contains(dId)) {
					HashMap<Integer, EmployeeInfoItem> employeeInfoItemMap = employeeInfoItems.get(dId);
					Set<Integer> uIds = employeeInfoItemMap.keySet();
					for (Iterator<Integer> it1 = uIds.iterator(); it1.hasNext();) {
						Integer uId = it1.next();
						EmployeeInfoItem employeeInfoItem = employeeInfoItemMap.get(uId);
						mDatabase.execSQL(sql, new Object[] {
								employeeInfoItem.getUid(), cId, dId, employeeInfoItem.getFlag(), employeeInfoItem.getUser_account(), Functions.getChinessFirstSpellInstance().GetChineseSpell(employeeInfoItem.getName()), employeeInfoItem.getName(), employeeInfoItem.getGender()
						});
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}

	}

	public void updateEmployeesInfo(Integer cId, HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> employeeInfoItems) throws Exception {
		if (employeeInfoItems == null)
			return;

		sql = "update " + UserBaseInfo + " set " + "Cid=" + EngineConst.cId + " ,Did = ?,Flag=? ,Account=? ,SimplePY=?,Name=? ,Gender=?" + " where Uid = ?";
		Set<Integer> dIds = employeeInfoItems.keySet();
		try {
			for (Iterator<Integer> it = dIds.iterator(); it.hasNext();) {
				Integer dId = it.next();
				HashMap<Integer, EmployeeInfoItem> employeeInfoItemMap = employeeInfoItems.get(dId);
				Set<Integer> uIds = employeeInfoItemMap.keySet();
				for (Iterator<Integer> it1 = uIds.iterator(); it1.hasNext();) {
					Integer uId = it1.next();
					EmployeeInfoItem employeeInfoItem = employeeInfoItemMap.get(uId);
					mDatabase.execSQL(sql, new Object[] {
							dId, employeeInfoItem.getFlag(), employeeInfoItem.getUser_account(), Functions.getChinessFirstSpellInstance().GetChineseSpell(employeeInfoItem.getName()), employeeInfoItem.getName(), employeeInfoItem.getGender(), employeeInfoItem.getUid()
					});
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void deleteEmployeesInfo(int[] uIds) throws Exception {

		if (uIds == null)
			return;
		sql = "delete from " + UserBaseInfo + " where " + "Uid = ?";
		try {
			for (int i = 0; i < uIds.length; i++) {
				mDatabase.execSQL(sql, new Object[] {
					uIds[i]
				});
			}
		} catch (Exception e) {
			throw e;
		}

		sql = "delete from " + DeptAndUserUC + " where " + "Uid = ?";
		try {
			for (int i = 0; i < uIds.length; i++) {
				mDatabase.execSQL(sql, new Object[] {
					uIds[i]
				});
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// ****************************************上面的方法是经过测试，可以使用的*******************************************

	// 以下是对CorpInfo表的增删改查操作
	public boolean addCorpInfo(String corp_account, String short_name, String cn_name, String domain, int state, int user_card, int logo_type, String en_name, String nation, String province, String city, String country, String addr, int type, String desp, int zipcode, String tel, String fax,
			String contactor, String email, String website, int reg_capital, int employee_num, int pc_num, String slogan, String config) throws Exception {
		if (corp_account == null)
			return false;
		sql = "replace into " + CorpInfo + "(corp_account ,short_name ,cn_name ,domain ,state ," + "user_card ,logo_type ,en_name ,nation ,province ,city ,country ,addr ,type ,desp ,zipcode ,tel ,fax ,contactor ," + "email ,website ,reg_capital ,employee_num ,pc_num ,slogan ,config )"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			mDatabase.execSQL(sql, new Object[] {
					corp_account, short_name, cn_name, domain, state, user_card, logo_type, en_name, nation, province, city, country, addr, type, desp, zipcode, tel, fax, contactor, email, website, reg_capital, employee_num, pc_num, slogan, config
			});
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addCorpInfo(CorpMaskItem corp) throws Exception {
		return addCorpInfo(corp.getCorp_account(), corp.getShort_name(), corp.getCn_name(), corp.getDomain(), corp.getState(), corp.getUser_card(), corp.getLogo_type(), corp.getEn_name(), corp.getNation(), corp.getProvince(), corp.getCity(), corp.getCountry(), corp.getAddr(), corp.getType(),
				corp.getDesp(), corp.getZipcode(), corp.getTel(), corp.getFax(), corp.getContactor(), corp.getEmail(), corp.getWebsite(), corp.getReg_capital(), corp.getEmployee_num(), corp.getPc_num(), corp.getSlogan(), corp.getConfig());
	}

	public boolean deleteCorpInfo(String corp_account) throws Exception {
		if (corp_account == null)
			return false;
		sql = "delete from " + CorpInfo + " where " + "corp_account = " + corp_account;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteCorpInfo(CorpMaskItem corp) throws Exception {
		return deleteCorpInfo(corp.getCorp_account());
	}

	// public boolean updateCorpInfo(CorpMaskItem corp) throws Exception {
	// if (corp.getCorp_account() == null)
	// return false;
	// sql = "update " + CorpInfo + " set " + "corp_short_name='"
	// + corp.getCorp_short_name() + "'," + "corp_name='"
	// + corp.getCorp_name() + "'," + "tel='" + corp.getTel() + "',"
	// + "addr='" + corp.getAddr() + "'," + "desp='" + corp.getDesp()
	// + "'," + "website='" + corp.getWebsite() + "' where "
	// + "corp_account = " + corp.getCorp_account();
	// try {
	// mDatabase.execSQL(sql);
	// } catch (Exception e) {
	// throw e;
	// }
	// return true;
	// }

	public CorpMaskItem getCorpInfo() throws Exception {
		sql = "select * from " + CorpInfo;
		CorpMaskItem corp = null;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				corp = new CorpMaskItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11),
						cursor.getString(12), cursor.getInt(13), cursor.getString(14), cursor.getInt(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19), cursor.getString(20), cursor.getInt(21), cursor.getInt(22), cursor.getInt(23), cursor.getString(24),
						cursor.getString(25));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return corp;
	}

	public EmployeeProfileItem getSelfInfo(String user_account) throws Exception {
		sql = "select * from " + UserProfile + " where user_account = '" + user_account + "'";
		Cursor cursor = null;
		EmployeeProfileItem employeeProfileItem = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return null;
			if (cursor.moveToFirst()) {
				employeeProfileItem = new EmployeeProfileItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9), cursor.getInt(10),
						cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return employeeProfileItem;
	}

	public void updateSelfInfo(String sign) throws Exception {
		sql = "update " + UserProfile + " set " + "sign='" + sign + "'" + " where RecId=1";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
	}

	public void addSelfInfo(EmployeeProfileItem employeeProfileItem) throws Exception {
		if (employeeProfileItem == null)
			return;
		sql = "replace into " + UserProfile + " (user_account ,corp_account ,name ,gender ,sign ,mobile , email ," + "role_id ,head_pic ,privacy_flag ,birth ,pos ,tel ,desp ,hide_dept_list )" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			mDatabase.execSQL(sql, new Object[] {
					employeeProfileItem.getUser_account(), employeeProfileItem.getCorp_account(), employeeProfileItem.getName(), employeeProfileItem.getGender(), employeeProfileItem.getSign(), employeeProfileItem.getMobile(), employeeProfileItem.getEmail(), employeeProfileItem.getRole_id(),
					employeeProfileItem.getHead_pic(), employeeProfileItem.getPrivacy_flag(), employeeProfileItem.getBirth(), employeeProfileItem.getPos(), employeeProfileItem.getTel(), employeeProfileItem.getDesp(), employeeProfileItem.getHide_dept_list()
			});
		} catch (Exception e) {
			throw e;
		}
	}

	// 以下是对ExternalContactUC表的增删改查操作
	public boolean addInnerContactUC(Integer innerContactUC) throws Exception {
		if (innerContactUC == null)
			return false;
		sql = "replace into " + InnerContactUC + " values (" + innerContactUC + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addInnerContactUC(InnerContactorUC innerContactorUC) throws Exception {
		return addInnerContactUC(innerContactorUC.getInnerContactUC());
	}

	public boolean deleteInnerContactUC(Integer innerContactUC) throws Exception {
		if (innerContactUC == null)
			return false;
		sql = "delete from " + InnerContactUC + " where " + "InnerContactUC = " + innerContactUC;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteInnerContactUC(InnerContactorUC innerContactorUC) throws Exception {
		return deleteInnerContactUC(innerContactorUC.getInnerContactUC());
	}

	public InnerContactorUC getInnerContactUC() throws Exception {
		sql = "select * from " + InnerContactUC;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		InnerContactorUC innerContactorUC = new InnerContactorUC();
		if (cursor.moveToFirst()) {
			innerContactorUC.setInnerContactUC(cursor.getInt(0));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return innerContactorUC;
	}

	// 以下是对InnerGroupUC表的增删改查操作
	public boolean addInnerGroupUC(Integer innerGroupUC) throws Exception {
		if (innerGroupUC == null)
			return false;
		sql = "replace into " + InnerGroupUC + " values (" + innerGroupUC + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addInnerGroupUC(InnerGroupUc innerGroupUC) throws Exception {
		return addInnerGroupUC(innerGroupUC.getInnerGroupUC());
	}

	public boolean deleteInnerGroupUC(Integer innerGroupUC) throws Exception {
		if (innerGroupUC == null)
			return false;
		sql = "delete from " + InnerGroupUC + " where " + "InnerGroupUC = " + innerGroupUC;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteInnerGroupUC(InnerGroupUc innerGroupUC) throws Exception {
		return deleteInnerGroupUC(innerGroupUC.getInnerGroupUC());
	}

	public InnerGroupUc getInnerGroupUC() throws Exception {
		sql = "select * from " + InnerGroupUC;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		InnerGroupUc innerGroupUc = new InnerGroupUc();
		if (cursor.moveToFirst()) {
			innerGroupUc.setInnerGroupUC(cursor.getInt(0));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return innerGroupUc;
	}

	// 以下是对InnerContactListInfo表的增删改查操作
	public boolean addInnerContactListInfo(Integer gId, Integer cId, Integer uId, Integer flag) throws Exception {
		if (uId == null)
			return false;
		sql = "replace into " + InnerContactListInfo + " values (" + gId + "," + cId + "," + uId + "," + flag + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addInnerContactListInfo(InnerContactList innerContactList) throws Exception {
		return addInnerContactListInfo(innerContactList.getgId(), innerContactList.getcId(), innerContactList.getuId(), innerContactList.getFlag());
	}

	public boolean deleteInnerContactListInfo(Integer uId) throws Exception {
		if (uId == null)
			return false;
		sql = "delete from " + InnerContactListInfo + " where " + "Uid = " + uId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteInnerContactListInfo(InnerContactList innerContactList) throws Exception {
		return deleteInnerContactListInfo(innerContactList.getuId());
	}

	public boolean updateInnerContactListInfo(InnerContactList innerContactList) throws Exception {
		if (innerContactList.getuId() == null)
			return false;
		sql = "update " + InnerContactListInfo + " set " + "Gid=" + innerContactList.getgId() + "," + "Cid=" + innerContactList.getcId() + "," + "Flag=" + innerContactList.getFlag() + " where " + "Uid = " + innerContactList.getuId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public InnerContactList getInnerContactListInfo(Integer uId) throws Exception {
		sql = "select * from " + InnerContactListInfo + " where " + "Uid = " + uId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		InnerContactList innerContactList = new InnerContactList();
		if (cursor.moveToFirst()) {
			innerContactList.setgId(cursor.getInt(0));
			innerContactList.setcId(cursor.getInt(1));
			innerContactList.setuId(cursor.getInt(2));
			innerContactList.setFlag(cursor.getInt(3));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return innerContactList;
	}

	// 以下是对InnerGroupInfo表的增删改查操作
	public boolean addInnerGroupInfo(Integer gId, String name) throws Exception {
		if (gId == null || name == null)
			return false;
		sql = "replace into " + InnerGroupInfo + " values (" + gId + ",'" + name + "')";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addInnerGroupInfo(InnerGroup innerGroup) throws Exception {
		return addInnerGroupInfo(innerGroup.getgId(), innerGroup.getName());
	}

	public boolean deleteInnerGroupInfo(Integer gId) throws Exception {
		if (gId == null)
			return false;
		sql = "delete from " + InnerGroupInfo + " where " + "Gid = " + gId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteInnerGroupInfo(InnerGroup innerGroup) throws Exception {
		return deleteInnerGroupInfo(innerGroup.getgId());
	}

	public boolean updateInnerGroupInfo(InnerGroup innerGroup) throws Exception {
		if (innerGroup.getgId() == null)
			return false;
		sql = "update " + InnerGroupInfo + " set " + "Name='" + innerGroup.getName() + "'" + " where " + "Gid = " + innerGroup.getgId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public InnerGroup getInnerGroupInfo(Integer gId) throws Exception {
		sql = "select * from " + InnerGroupInfo + " where " + "Gid = " + gId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		InnerGroup innerGroup = new InnerGroup();
		if (cursor.moveToFirst()) {
			innerGroup.setgId(cursor.getInt(0));
			innerGroup.setName(cursor.getString(1));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return innerGroup;
	}

	// 以下是对DeptUserInfo表的增删改查操作
	public boolean addEmployeesUID(Integer dId, int[] uId, int[] nextSiblingUid) throws Exception {
		if (dId == null || uId == null || nextSiblingUid == null)
			return false;
		sql = "replace into " + DeptUserInfo + "(Did, Uid, NextSiblingUid) values (?,?,?)";

		for (int i = 0; i < uId.length; i++) {
			try {
				mDatabase.execSQL(sql, new Object[] {
						dId, uId[i], nextSiblingUid[i]
				});
			} catch (Exception e) {
				throw e;
			}
		}
		return true;
	}

	public boolean updateEmployeesUID(Integer dId, int[] uId, int[] nextSiblingUid) throws Exception {
		if (dId == null || uId == null || nextSiblingUid == null)
			return false;
		sql = "update " + DeptUserInfo + " set " + "NextSiblingUid=? where Did = ? and Uid=?";

		for (int i = 0; i < uId.length; i++) {
			try {
				mDatabase.execSQL(sql, new Object[] {
						nextSiblingUid[i], dId, uId[i]
				});
			} catch (Exception e) {
				throw e;
			}
		}
		return true;
	}

	public boolean addDeptUserInfo(Integer dId, Integer uId, Integer nextSiblingUid) throws Exception {
		if (dId == null || uId == null)
			return false;
		sql = "replace into " + DeptUserInfo + " values (" + dId + "," + uId + "," + nextSiblingUid + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addDeptUserInfo(DeptUser deptUser) throws Exception {
		return addDeptUserInfo(deptUser.getdId(), deptUser.getuId(), deptUser.getNextSiblingUid());
	}

	public boolean deleteDeptUserInfo(Integer dId, Integer uId) throws Exception {
		if (dId == null || uId == null)
			return false;
		sql = "delete from " + DeptUserInfo + " where " + "Did = " + dId + " and Uid = " + uId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteDeptUserInfo(DeptUser deptUser) throws Exception {
		return deleteDeptUserInfo(deptUser.getdId(), deptUser.getuId());
	}

	public boolean updateDeptUserInfo(DeptUser deptUser) throws Exception {
		if (deptUser.getdId() == null || deptUser.getuId() == null)
			return false;
		sql = "update " + DeptUserInfo + " set " + "NextSiblingUid=" + deptUser.getNextSiblingUid() + " where " + "Did = " + deptUser.getdId() + " and Uid = " + deptUser.getuId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public DeptUser getDeptUserInfo(Integer dId, Integer uId) throws Exception {
		sql = "select * from " + DeptUserInfo + " where " + "Did = " + dId + " and Uid = " + uId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		DeptUser deptUser = new DeptUser();
		if (cursor.moveToFirst()) {
			deptUser.setdId(cursor.getInt(0));
			deptUser.setuId(cursor.getInt(1));
			deptUser.setNextSiblingUid(cursor.getInt(2));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return deptUser;
	}

	// 以下是对OuterGroupInfo表的增删改查操作
	public boolean addOuterGroupInfo(Integer gId, String name) throws Exception {
		if (gId == null || name == null)
			return false;
		sql = "replace into " + OuterGroupInfo + " values (" + gId + ",'" + name + "')";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addOuterGroupInfo(OuterGroup outerGroup) throws Exception {
		return addOuterGroupInfo(outerGroup.getgId(), outerGroup.getName());
	}

	public boolean deleteOuterGroupInfo(Integer gId) throws Exception {
		if (gId == null)
			return false;
		sql = "delete from " + OuterGroupInfo + " where " + "Gid = " + gId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
		// return mDatabase.delete(OuterGroupInfo, "Gid = ? ", new String[] {
		// String.valueOf(gId) });
	}

	public boolean deleteOuterGroupInfo(OuterGroup outerGroup) throws Exception {
		return deleteOuterGroupInfo(outerGroup.getgId());
	}

	public boolean updateOuterGroupInfo(OuterGroup outerGroup) throws Exception {
		if (outerGroup.getgId() == null)
			return false;
		sql = "update " + OuterGroupInfo + " set " + "Name='" + outerGroup.getName() + "'" + " where " + "Gid = " + outerGroup.getgId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
		// ContentValues values = new ContentValues();
		// values.put("Name", outerGroup.getName());
		// return mDatabase.update(OuterGroupInfo, values, "Gid = ? ", new
		// String[] { String.valueOf(outerGroup.getgId()) });
	}

	public OuterGroup getOuterGroupInfo(Integer gId) throws Exception {
		sql = "select * from " + OuterGroupInfo + " where " + "Gid = " + gId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		// Cursor cursor = mDatabase.query(OuterGroupInfo, new String[] { "Gid",
		// "Name" }, "Gid = " + gId, null, null, null, null);
		OuterGroup outerGroup = new OuterGroup();
		if (cursor.moveToFirst()) {
			outerGroup.setgId(cursor.getInt(0));
			outerGroup.setName(cursor.getString(1));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return outerGroup;
	}

	// 以下是对ExternalGroupUC表的增删改查操作
	public boolean addExternalGroupUC(Integer externalGroupUC) throws Exception {
		if (externalGroupUC == null)
			return false;
		sql = "replace into " + ExternalGroupUC + " values (" + externalGroupUC + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addExternalGroupUC(ExternalGroupUc externalGroupUc) throws Exception {
		return addExternalGroupUC(externalGroupUc.getExternalGroupUC());
	}

	public boolean deleteExternalGroupUC(Integer externalGroupUc) throws Exception {
		if (externalGroupUc == null)
			return false;
		sql = "delete from " + ExternalGroupUC + " where " + "ExternalGroupUC = " + externalGroupUc;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteExternalGroupUC(ExternalGroupUc externalGroupUc) throws Exception {
		return deleteExternalGroupUC(externalGroupUc.getExternalGroupUC());
	}

	public ExternalGroupUc getExternalGroupUC() throws Exception {

		sql = "select * from " + ExternalGroupUC;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}

		ExternalGroupUc externalGroupUc = new ExternalGroupUc();
		if (cursor.moveToFirst()) {
			externalGroupUc.setExternalGroupUC(cursor.getInt(0));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return externalGroupUc;
	}

	// 以下是对ExternalContactUC表的增删改查操作
	public boolean addExternalContactUC(Integer externalContactUC) throws Exception {
		if (externalContactUC == null)
			return false;
		sql = "replace into " + ExternalContactUC + " values (" + externalContactUC + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
		// ContentValues values = new ContentValues();
		// values.put("ExternalContactUC", externalContactUC);
		// return mDatabase.insert(ExternalContactUC, null, values);
	}

	public boolean addExternalContactUC(ExternalContactorUC externalContactorUC) throws Exception {
		return addExternalContactUC(externalContactorUC.getExternalContactUC());
	}

	public boolean deleteExternalContactUC(Integer externalContactUC) throws Exception {
		if (externalContactUC == null)
			return false;
		sql = "delete from " + ExternalContactUC + " where " + "ExternalContactUC = " + externalContactUC;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
		// return mDatabase.delete(ExternalContactUC, "ExternalContactUC = ? ",
		// new String[] { String.valueOf(externalContactUC) });
	}

	public boolean deleteExternalContactUC(ExternalContactorUC externalContactorUC) throws Exception {
		return deleteExternalContactUC(externalContactorUC.getExternalContactUC());
	}

	public ExternalContactorUC getExternalContactUC() throws Exception {
		sql = "select * from " + ExternalContactUC;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		// Cursor cursor = mDatabase.query(ExternalContactUC, new String[] {
		// "ExternalContactUC" }, null, null, null, null, null);
		ExternalContactorUC externalContactorUC = new ExternalContactorUC();
		if (cursor.moveToFirst()) {
			externalContactorUC.setExternalContactUC(cursor.getInt(0));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return externalContactorUC;
	}

	// 以下是对ExternalContactListInfo表的增删改查操作
	public boolean addExternalContactListInfo(Integer gId, Integer cId, Integer uId, Integer flag) throws Exception {
		if (uId == null)
			return false;
		sql = "replace into " + ExternalContactListInfo + " values (" + gId + "," + cId + "," + uId + "," + flag + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addExternalContactListInfo(ExternalContactList externalContactList) throws Exception {
		return addExternalContactListInfo(externalContactList.getgId(), externalContactList.getcId(), externalContactList.getuId(), externalContactList.getFlag());
	}

	public boolean deleteExternalContactListInfo(Integer uId) throws Exception {
		if (uId == null)
			return false;
		sql = "delete from " + ExternalContactListInfo + " where " + "Uid = " + uId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteExternalContactListInfo(ExternalContactList externalContactList) throws Exception {
		return deleteExternalContactListInfo(externalContactList.getuId());
	}

	public boolean updateExternalContactListInfo(ExternalContactList externalContactList) throws Exception {
		if (externalContactList.getuId() == null)
			return false;
		sql = "update " + ExternalContactListInfo + " set " + "Gid=" + externalContactList.getgId() + "," + "Cid=" + externalContactList.getcId() + "," + "Flag=" + externalContactList.getFlag() + " where " + "Uid = " + externalContactList.getuId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public ExternalContactList getExternalContactListInfo(Integer uId) throws Exception {
		sql = "select * from " + ExternalContactListInfo + " where " + "Uid = " + uId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		ExternalContactList externalContactList = new ExternalContactList();
		if (cursor.moveToFirst()) {
			externalContactList.setgId(cursor.getInt(0));
			externalContactList.setcId(cursor.getInt(1));
			externalContactList.setuId(cursor.getInt(2));
			externalContactList.setFlag(cursor.getInt(3));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return externalContactList;
	}

	/**
	 * 维护最近联系人列表，当超过200条的时候，删除前面100条，此方法还有bug，不可使用
	 * 
	 * @throws Exception
	 */
	public void keepRecentContact(int max) throws Exception {
		sql = "select count(*) from " + RecentContact;
		Cursor cursor = null;
		int sum = 0;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				sum = cursor.getInt(0);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		if (sum >= max) {
			sql = "delete from " + RecentContact + " limit 0," + (sum - 100);
			try {
				mDatabase.execSQL(sql);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private boolean hasRecentContact(RecentContactInfo recentContactInfo) {
		sql = "select count(*) from " + RecentContact + " where type = " + recentContactInfo.getType() + " and UId = " + recentContactInfo.getUid();
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				if (count > 0)
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return false;
	}

	/**
	 * type,UId,Name, Date, Time,CId
	 * 
	 * @param recentContactInfo
	 * @param date
	 * @throws Exception
	 */
	public void addRecentContact(RecentContactInfo recentContactInfo, String date) throws Exception {
		if (hasRecentContact(recentContactInfo))
			deleteRecentContact(recentContactInfo);

		sql = "replace into " + RecentContact + "(type,UId,Name, Date, Time,CId)" + " values (?,?,?,?,?,?)";
		try {
			mDatabase.execSQL(sql, new Object[] {
					recentContactInfo.getType(), recentContactInfo.getUid(), recentContactInfo.getName(), date, recentContactInfo.getTime(), recentContactInfo.getCid()
			});
		} catch (Exception e) {
			throw e;
		}

	}

	public boolean deleteRecentContact(Integer type, Integer uid) throws Exception {
		if (type == null || uid == null) {
			return false;
		}

		return mDatabase.delete(RecentContact, "type = ? and UId = ?", new String[] {
				type + "", uid + ""
		}) > 0;

		// sql = "delete from " + RecentContact + " where " + "type = " + type
		// + " and UId = " + uid;
		// try {
		// mDatabase.execSQL(sql);
		// } catch (Exception e) {
		// throw e;
		// }
		// return true;
	}

	public boolean deleteRecentContact(RecentContactInfo recentContactInfo) throws Exception {
		return deleteRecentContact(recentContactInfo.getType(), recentContactInfo.getUid());
	}

	public boolean updateRecentContact(RecentContactInfo recentContactInfo, String date) throws Exception {
		// deleteRecentContact(recentContactInfo);
		addRecentContact(recentContactInfo, date);
		return true;
	}

	public void getRecentContactData(ArrayList<String> dateList, ArrayList<Integer> idList, HashMap<String, HashMap<Integer, RecentContactInfo>> recentContactMap) throws Exception {

		getDateList(dateList);

		if (dateList.size() > 0) {
			getIdList(idList);

			getRecentContactMap(dateList, recentContactMap);
		}
	}

	private void getRecentContactMap(ArrayList<String> dateList, HashMap<String, HashMap<Integer, RecentContactInfo>> recentContactMap) throws Exception {
		Cursor cursor = null;
		RecentContactInfo recentContactInfo = null;
		HashMap<Integer, RecentContactInfo> recentContactInfoMap = null;
		for (String date : dateList) {
			sql = "select * from " + RecentContact + " where Date ='" + date + "'";
			try {

				recentContactInfoMap = new HashMap<Integer, RecentContactInfo>();
				cursor = mDatabase.rawQuery(sql, null);
				if (cursor.getCount() < 1)
					break;
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					int uid = cursor.getInt(1);
					String name = cursor.getString(2);
					String time = date + " " + cursor.getString(4);
					int cid = cursor.getInt(5);
					int count = getUnReadMessageCount(uid);
					String info = getLastMessage(uid);
					recentContactInfo = new RecentContactInfo(cid, uid, name, info, time, count);
					recentContactInfoMap.put(uid, recentContactInfo);
				}
				recentContactMap.put(date, recentContactInfoMap);

			} catch (Exception e) {
				throw e;
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}

	}

	private void getIdList(ArrayList<Integer> idList) throws Exception {
		Cursor cursor = null;
		// 查出所有UID
		sql = "select UId from " + RecentContact + " order by Time desc";
		try {
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.getCount() < 1)
				return;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				idList.add(cursor.getInt(0));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	private void getDateList(ArrayList<String> dateList) throws Exception {
		Cursor cursor = null;
		// 查出所有Date
		sql = "select distinct Date from " + RecentContact + " order by Date desc";
		try {
			cursor = mDatabase.rawQuery(sql, null);

			if (cursor.getCount() < 1)
				return;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				dateList.add(cursor.getString(0));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	public int getUnReadMessageCount(int uid) {
		try {
			String sql_messageInfo = "select count(*) from " + MessageInfo + " where FromUid =" + uid + " and IsRead = " + com.imo.db.entity.MessageInfo.MessageInfo_UnRead;
			Cursor cursor_messageInfo = null;
			int count = 0;
			try {
				cursor_messageInfo = mDatabase.rawQuery(sql_messageInfo, null);
				if (cursor_messageInfo.getCount() < 1)
					return 0;
				cursor_messageInfo.moveToFirst();
				count = cursor_messageInfo.getInt(0);
			} catch (Exception e) {
				throw e;
			} finally {
				if (cursor_messageInfo != null && !cursor_messageInfo.isClosed()) {
					cursor_messageInfo.close();
				}
			}

			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	private int getLastMessageRecId(int uid) throws Exception {
		int max_msgId = -1;
		sql = "select max(RecId) from " + MessageInfo + " where FromUid =" + uid + " or ToUid = " + uid;

		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.getCount() < 1)
				return max_msgId;
			cursor.moveToFirst();
			max_msgId = cursor.getInt(0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return max_msgId;
	}

	public String getLastMessage(int uid) throws Exception {
		int max_msgRecId = getLastMessageRecId(uid);

		String sql_messageInfo = "select Message from " + MessageInfo + " where  RecId =" + max_msgRecId;
		Cursor cursor_messageInfo = null;
		String message = null;
		try {
			cursor_messageInfo = mDatabase.rawQuery(sql_messageInfo, null);
			if (cursor_messageInfo.getCount() < 1)
				return "";
			cursor_messageInfo.moveToFirst();
			message = cursor_messageInfo.getString(0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor_messageInfo != null && !cursor_messageInfo.isClosed()) {
				cursor_messageInfo.close();
			}
		}
		return message;

	}

	// 以下是对DeptInfo表的增删改查操作

	public boolean addDept(Integer cId, Integer dId, String name, Integer pDid, Integer uC, Integer deptUserUC, Integer firstChild, Integer nextSibling, String desp, String fax, String hideDeptList, String addr, String tel, String website, Integer firstChildUser) throws Exception {
		if (cId == null || dId == null)
			return false;
		sql = "replace into " + DeptInfo + " values (" + cId + "," + dId + ",'" + name + "'," + pDid + "," + uC + "," + deptUserUC + "," + firstChild + "," + nextSibling + ",'" + desp + "','" + fax + "','" + hideDeptList + "','" + addr + "','" + tel + "','" + website + "'," + firstChildUser + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addDept(Dept dept) throws Exception {
		return addDept(dept.getcId(), dept.getdId(), dept.getName(), dept.getpDid(), dept.getuC(), dept.getDeptUserUC(), dept.getFirstChild(), dept.getNextSibling(), dept.getDesp(), dept.getFax(), dept.getHideDeptList(), dept.getAddr(), dept.getTel(), dept.getWebsite(), dept.getFirstChildUser());
	}

	public boolean addDept(ArrayList<Dept> depts) throws Exception {
		mDatabase.beginTransaction();
		try {
			for (Dept dept : depts) {
				addDept(dept);
			}
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			mDatabase.endTransaction();
		}
		return true;
	}

	public boolean deleteDept(Integer cId, Integer dId) throws Exception {
		if (dId == null || cId == null)
			return false;
		sql = "delete from " + DeptInfo + " where " + "Cid = " + cId + " and Did = " + dId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteDept(Dept dept) throws Exception {
		return deleteDept(dept.getcId(), dept.getdId());
	}

	public boolean updateDept(Dept dept) throws Exception {
		if (dept.getcId() == null || dept.getdId() == null)
			return false;
		sql = "update " + DeptInfo + " set " + "Name='" + dept.getName() + "'," + "PDid=" + dept.getpDid() + "," + "UC=" + dept.getuC() + "," + "DeptUserUC=" + dept.getDeptUserUC() + "," + "FirstChild=" + dept.getFirstChild() + "," + "NextSibling=" + dept.getNextSibling() + "," + "Desp='"
				+ dept.getDesp() + "'," + "Fax='" + dept.getFax() + "'," + "HideDeptList='" + dept.getHideDeptList() + "'," + "Addr='" + dept.getAddr() + "'," + "Tel='" + dept.getTel() + "'," + "Website='" + dept.getWebsite() + "'," + "FirstChildUser=" + dept.getFirstChildUser() + " where "
				+ "Cid = " + dept.getcId() + " and Did = " + dept.getdId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public Dept getDept(Integer cId, Integer dId) throws Exception {
		sql = "select * from " + DeptInfo + " where " + "Cid=" + cId + " and Did=" + dId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		Dept dept = new Dept();
		if (cursor.moveToFirst()) {
			dept.setcId(cursor.getInt(0));
			dept.setdId(cursor.getInt(1));
			dept.setName(cursor.getString(2));
			dept.setpDid(cursor.getInt(3));
			dept.setuC(cursor.getInt(4));
			dept.setDeptUserUC(cursor.getInt(5));
			dept.setFirstChild(cursor.getInt(6));
			dept.setNextSibling(cursor.getInt(7));
			dept.setDesp(cursor.getString(8));
			dept.setFax(cursor.getString(9));
			dept.setHideDeptList(cursor.getString(10));
			dept.setAddr(cursor.getString(11));
			dept.setTel(cursor.getString(12));
			dept.setWebsite(cursor.getString(13));
			dept.setFirstChildUser(cursor.getInt(14));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return dept;
	}

	// 以下是对UserBaseInfo表的增删改查操作

	public boolean addUser(Integer uId, Integer cId, String corpAccount, String account, String name, Integer gender) throws Exception {
		if (uId == null)
			return false;
		sql = "replace into " + UserBaseInfo + " values (" + uId + "," + cId + ",'" + corpAccount + "','" + account + "','" + name + "'," + gender + ")";
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addUser(User user) throws Exception {
		return addUser(user.getuId(), user.getcId(), user.getCorpAccount(), user.getAccount(), user.getName(), user.getGender());
	}

	public boolean deleteUser(Integer uId) throws Exception {
		if (uId == null)
			return false;
		sql = "delete from " + UserBaseInfo + " where " + "Uid = " + uId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean deleteUser(User user) throws Exception {
		return deleteUser(user.getuId());
	}

	public boolean updateUser(User user) throws Exception {
		if (user.getuId() == null)
			return false;
		sql = "update " + UserBaseInfo + " set " + "Cid=" + user.getcId() + "," + "CorpAccount='" + user.getCorpAccount() + "'," + "Account='" + user.getAccount() + "'," + "Name='" + user.getName() + "'," + "Gender=" + user.getGender() + " where " + "Uid = " + user.getuId();
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public User getUser(Integer uId) throws Exception {
		sql = "select * from " + UserBaseInfo + " where " + "Uid=" + uId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			throw e;
		}
		User user = new User();
		if (cursor.moveToFirst()) {
			user.setuId(cursor.getInt(0));
			user.setcId(cursor.getInt(1));
			user.setCorpAccount(cursor.getString(2));
			user.setAccount(cursor.getString(3));
			user.setName(cursor.getString(4));
			user.setGender(cursor.getInt(5));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return user;

	}

	/**
	 * 存脱机消息
	 * 
	 * @param offlineMsgItems
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Integer[]> addMessages(ArrayList<OfflineMsgItem> offlineMsgItems, int cid, int uid) throws Exception {
		// ArrayList<Integer[]> arrayList = new ArrayList<Integer[]>();
		// Set<Integer> set = new HashSet<Integer>();
		mDatabase.beginTransaction();
		try {
			for (int i = offlineMsgItems.size() - 1; i >= 0; i--) {
				OfflineMsgItem offlineMsgItem = offlineMsgItems.get(i);
				addMessage(0, uid, "", EngineConst.uId, Globe.myself.getName(), Functions.getDate(offlineMsgItem.getTime() * 1000L), Functions.getTime(offlineMsgItem.getTime() * 1000L), offlineMsgItem.getMsg(), com.imo.db.entity.MessageInfo.MessageInfo_From, offlineMsgItem.getMsgid(),
						com.imo.db.entity.MessageInfo.MessageInfo_UnRead, com.imo.db.entity.MessageInfo.MessageInfo_UnFailed);
			}
			// for (OfflineMsgItem offlineMsgItem : offlineMsgItems) {
			// addMessage(0, uid, "", EngineConst.uId, Globe.myself.getName(),
			// Functions.getDate(offlineMsgItem.getTime() * 1000L),
			// Functions.getTime(offlineMsgItem.getTime() * 1000L),
			// offlineMsgItem.getMsg(),
			// com.imo.db.entity.MessageInfo.MessageInfo_From,
			// offlineMsgItem.getMsgid(),
			// com.imo.db.entity.MessageInfo.MessageInfo_UnRead,
			// com.imo.db.entity.MessageInfo.MessageInfo_UnFailed);
			// }
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			// arrayList = null;
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return null;
		// return dataFilter_arrayList(arrayList, set);
	}

	private ArrayList<Integer[]> dataFilter_arrayList(ArrayList<Integer[]> arrayList, Set<Integer> set) {
		Iterator<Integer> iterator1 = set.iterator();

		ArrayList<Integer[]> resultList = new ArrayList<Integer[]>();
		Iterator<Integer> iterator = set.iterator();
		while (iterator.hasNext()) {
			Integer uid = iterator.next();
			Integer[] result = {
					0, 0, 0
			};
			for (Integer[] temp : arrayList) {
				if (String.valueOf(temp[1]).equals(String.valueOf(uid))) {
					if (temp[2] > result[2])
						result = temp;
				}
			}
			resultList.add(result);
		}

		return resultList;
	}

	public boolean addMessage(int sessionId, int fromUid, String fromName, int toUid, String toName, String date, String time, String text, int type, int msgId, int isRead, int isFailed) throws Exception {
		sql = "replace into " + MessageInfo + " (SessionId,FromUid,FromName,ToUid,ToName,Date,Time,Message,Type,MsgId,IsRead,isFailed)" + " values (?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			mDatabase.execSQL(sql, new Object[] {
					sessionId, fromUid, fromName, toUid, toName, date, time, text, type, msgId, isRead, isFailed
			});
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public boolean addMessage(MessageInfo message) throws Exception {
		return addMessage(message.getSessionId(), message.getFromUid(), message.getFromName(), message.getToUid(), message.getToName(), message.getDate(), message.getTime(), message.getText(), message.getType(), message.getMsgId(), message.getIsRead(), message.getIsFailed());
	}

	public boolean deleteMessage(Integer uId) throws Exception {
		if (uId == null)
			return false;
		sql = "delete from " + MessageInfo + " where " + "ToUid=" + uId + " or FromUid=" + uId;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	//
	// public boolean deleteMessage(Message message) throws Exception {
	// return deleteMessage(message.getuId());
	// }
	//
	public int getMessageSum(Integer uId) throws Exception {
		sql = "select count(ToUid) from " + MessageInfo + " where " + "ToUid=" + uId + " or FromUid=" + uId;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			cursor.moveToFirst();
			int sum = cursor.getInt(0);
			return sum;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	/**
	 * 最多返回count条
	 * 
	 * @param uId
	 *        发送者或者接收者的UID
	 * @param index
	 *        从index开始取
	 * @return
	 * @throws Exception
	 */
	public ArrayList<MessageInfo> getMessage(Integer uId, int index, int count) throws Exception {
		sql = "select * from " + MessageInfo + " where " + "ToUid=" + uId + " or FromUid=" + uId + " order by Date,Time" + " limit " + index + "," + count;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			ArrayList<MessageInfo> messages = new ArrayList<MessageInfo>();
			MessageInfo message = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				message = new MessageInfo();
				message.setFromUid(cursor.getInt(2));
				message.setFromName(cursor.getString(3));
				message.setToUid(cursor.getInt(4));
				message.setToName(cursor.getString(5));
				message.setDate(cursor.getString(6));
				message.setTime(cursor.getString(7));
				message.setText(cursor.getString(8));
				message.setType(cursor.getInt(9));
				message.setMsgId(cursor.getInt(10));
				message.setIsRead(cursor.getInt(11));
				message.setIsFailed(cursor.getInt(12));
				messages.add(message);
			}
			return messages;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public void updateMessage(int uid) {
		sql = "update " + MessageInfo + " set " + "IsRead=" + com.imo.db.entity.MessageInfo.MessageInfo_Readed + " where " + "FromUid = " + uid;
		try {
			mDatabase.execSQL(sql);
		} catch (Exception e) {}

	}

	public void getInnerGroupInfo(Map<Integer, InnerContactorItem> innerGroupIdMap) throws Exception {
		sql = "select * from " + InnerGroupInfo;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return;
			InnerContactorItem innerContactorItem = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				int gid = cursor.getInt(0);
				innerContactorItem = new InnerContactorItem(gid, cursor.getString(1));
				innerGroupIdMap.put(gid, innerContactorItem);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	public void getOuterGroupInfo(Map<Integer, OuterContactorItem> outerGroupIdMap) throws Exception {
		sql = "select * from " + OuterGroupInfo;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return;
			OuterContactorItem outerGroup = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				int gid = cursor.getInt(0);
				outerGroup = new OuterContactorItem(gid, cursor.getString(1));
				outerGroupIdMap.put(gid, outerGroup);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	public void getAllInnerContactListInfo(Map<Integer, ArrayList<Integer>> innerGroupContactMap) throws Exception {
		int[] gids = getAllInnerContactGid();
		if (gids == null)
			return;

		sql = "select  Uid  from " + InnerContactListInfo + " where " + "Gid = ?";
		Cursor cursor = null;
		ArrayList<Integer> uids = null;
		for (int i = 0; i < gids.length; i++) {
			uids = new ArrayList<Integer>();
			try {
				cursor = mDatabase.rawQuery(sql, new String[] {
					gids[i] + ""
				});
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					uids.add(cursor.getInt(0));
				}
				innerGroupContactMap.put(gids[i], uids);
			} catch (Exception e) {
				throw e;
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}

	public void getAllOuterContactListInfo(Map<Integer, ArrayList<OuterContactItem>> outerGroupContactMap, Map<Integer, String> outerContactCorpMap, Map<Integer, OuterContactBasicInfo> outerContactInfoMap) throws Exception {
		int[] gids = getAllOuterContactGid();
		if (gids == null)
			return;
		getOuterContactListInfoByGid(outerGroupContactMap, gids);

		getOuterContactCorpInfo(outerContactCorpMap);

		getOuterContactInfo(outerContactInfoMap);

	}

	private void getOuterContactInfo(Map<Integer, OuterContactBasicInfo> outerContactInfoMap) throws Exception {
		sql = "select  *  from " + OuterContactInfo;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				int uid = cursor.getInt(1);
				outerContactInfoMap.put(uid, new OuterContactBasicInfo(cursor.getInt(0), uid, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5)));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	private void getOuterContactCorpInfo(Map<Integer, String> outerContactCorpMap) throws Exception {
		sql = "select  *  from " + OuterCorpInfo;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				outerContactCorpMap.put(cursor.getInt(0), cursor.getString(1));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	private void getOuterContactListInfoByGid(Map<Integer, ArrayList<OuterContactItem>> outerGroupContactMap, int[] gids) throws Exception {
		sql = "select  *  from " + ExternalContactListInfo + " where " + "Gid = ?";
		Cursor cursor = null;
		ArrayList<OuterContactItem> outerContactItems = null;
		for (int i = 0; i < gids.length; i++) {
			outerContactItems = new ArrayList<OuterContactItem>();
			try {
				cursor = mDatabase.rawQuery(sql, new String[] {
					gids[i] + ""
				});
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					outerContactItems.add(new OuterContactItem(cursor.getInt(1), cursor.getInt(2), cursor.getInt(0), cursor.getInt(3)));
				}
				outerGroupContactMap.put(gids[i], outerContactItems);
			} catch (Exception e) {
				throw e;
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}

	private int[] getAllInnerContactGid() {
		sql = "select distinct Gid from " + InnerContactListInfo;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return null;
			int[] gids = new int[count];
			int i = 0;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				gids[i] = cursor.getInt(0);
				i++;
			}
			return gids;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return null;
	}

	private int[] getAllOuterContactGid() {
		sql = "select distinct Gid from " + OuterGroupInfo;
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count < 1)
				return null;
			int[] gids = new int[count];
			int i = 0;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				gids[i] = cursor.getInt(0);
				i++;
			}
			return gids;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return null;
	}

	public void putInnerGroupInfo(Map<Integer, InnerContactorItem> innerGroupIdMap) throws Exception {
		if (innerGroupIdMap == null)
			return;
		sql = "delete from " + InnerGroupInfo;
		mDatabase.execSQL(sql);
		sql = "replace into " + InnerGroupInfo + " values (?,?)";
		Set<Integer> keys = innerGroupIdMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			InnerContactorItem innerContactorItem = innerGroupIdMap.get(key);
			try {
				mDatabase.execSQL(sql, new Object[] {
						innerContactorItem.getGroupID(), innerContactorItem.getGroupName()
				});
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void putOuterGroupInfo(Map<Integer, OuterContactorItem> outerGroupIdMap) throws Exception {
		if (outerGroupIdMap == null)
			return;
		sql = "delete from " + OuterGroupInfo;
		mDatabase.execSQL(sql);
		sql = "replace into " + OuterGroupInfo + " values (?,?)";
		Set<Integer> keys = outerGroupIdMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			OuterContactorItem outerContactorItem = outerGroupIdMap.get(key);
			try {
				mDatabase.execSQL(sql, new Object[] {
						outerContactorItem.getGroupID(), outerContactorItem.getGroupName()
				});
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void putOuterCorpInfo(Map<Integer, String> outerContactCorpMap) throws Exception {
		if (outerContactCorpMap == null)
			return;
		sql = "delete from " + OuterCorpInfo;
		mDatabase.execSQL(sql);
		sql = "replace into " + OuterCorpInfo + " values (?,?)";
		Set<Integer> keys = outerContactCorpMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			try {
				mDatabase.execSQL(sql, new Object[] {
						key, outerContactCorpMap.get(key)
				});
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void putOuterContactBasicInfo(Map<Integer, OuterContactBasicInfo> outerContactInfoMap) throws Exception {
		if (outerContactInfoMap == null)
			return;
		sql = "delete from " + OuterContactInfo;
		mDatabase.execSQL(sql);
		sql = "replace into " + OuterContactInfo + " values (?,?,?,?,?,?,?)";
		Set<Integer> keys = outerContactInfoMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			OuterContactBasicInfo outerContactBasicInfo = outerContactInfoMap.get(key);
			try {
				mDatabase.execSQL(sql,
						new Object[] {
								outerContactBasicInfo.getCid(), outerContactBasicInfo.getUid(), outerContactBasicInfo.getCorpAccount(), outerContactBasicInfo.getUserAccount(), outerContactBasicInfo.getName(), outerContactBasicInfo.getGender(),
								Functions.getChinessFirstSpellInstance().GetChineseSpell(outerContactBasicInfo.getName()),
						});
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void putInnerContactListInfo(Map<Integer, ArrayList<Integer>> innerGroupContactMap) throws Exception {
		if (innerGroupContactMap == null)
			return;
		sql = "delete from " + InnerContactListInfo;
		mDatabase.execSQL(sql);
		sql = "replace into " + InnerContactListInfo + " values (?,?,?,?)";
		Set<Integer> keys = innerGroupContactMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			ArrayList<Integer> uids = innerGroupContactMap.get(key);
			for (int uid : uids) {
				try {
					mDatabase.execSQL(sql, new Object[] {
							key, 0, uid, 0
					});
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	public void putOuterContactListInfo(Map<Integer, ArrayList<OuterContactItem>> outerGroupContactMap) throws Exception {
		if (outerGroupContactMap == null)
			return;

		sql = "delete from " + ExternalContactListInfo;
		mDatabase.execSQL(sql);

		sql = "replace into " + ExternalContactListInfo + " values (?,?,?,?)";
		Set<Integer> keys = outerGroupContactMap.keySet();
		for (Iterator<Integer> it = keys.iterator(); it.hasNext();) {
			Integer key = it.next();
			ArrayList<OuterContactItem> outerContactItems = outerGroupContactMap.get(key);
			for (OuterContactItem outerContactItem : outerContactItems) {
				try {
					mDatabase.execSQL(sql, new Object[] {
							outerContactItem.getGroupId(), outerContactItem.getCid(), outerContactItem.getUid(), outerContactItem.getFlag()
					});
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	/**
	 * 存放所有联系人信息，包括联系人组信息。由于是重构，所以参数列表很长。
	 * 
	 * @param innerGroupInfoNeedUpdate
	 * @param innerContactNeedUpdate
	 * @param outerGroupInfoNeedUpdate
	 * @param outerContactNeedUpdate
	 * @param innerGroupIdMap
	 * @param innerGroupContactMap
	 * @param outerGroupIdMap
	 * @param outerGroupContactMap
	 * @param outerContactInfoMap
	 * @param outerContactCorpMap
	 * @throws Exception
	 */
	public void putContactAndGroupInfo(boolean innerGroupInfoNeedUpdate, boolean innerContactNeedUpdate, boolean outerGroupInfoNeedUpdate, boolean outerContactNeedUpdate, HashMap<Integer, InnerContactorItem> innerGroupIdMap, HashMap<Integer, ArrayList<Integer>> innerGroupContactMap,
			HashMap<Integer, OuterContactorItem> outerGroupIdMap, HashMap<Integer, ArrayList<OuterContactItem>> outerGroupContactMap, HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap, HashMap<Integer, String> outerContactCorpMap) throws Exception {

		mDatabase.beginTransaction();
		try {
			if (innerGroupInfoNeedUpdate) {
				putInnerGroupInfo(innerGroupIdMap);
			}

			if (innerContactNeedUpdate) {
				putInnerContactListInfo(innerGroupContactMap);
			}

			if (outerGroupInfoNeedUpdate) {
				putOuterGroupInfo(outerGroupIdMap);
			}

			if (outerContactNeedUpdate) {
				putOuterContactListInfo(outerGroupContactMap);
				putOuterContactBasicInfo(outerContactInfoMap);
				putOuterCorpInfo(outerContactCorpMap);
			}
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}
	}

}
