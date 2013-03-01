package com.imo.network.packages;

public interface IMOCommand {
	// 错误代码
	/** 远端已经关闭连接 */
	public static final short ERROR_CONNECTION_BROKEN = 0;
	/** 有网络连接但是无法发送数据 */
	public static final short ERROR_CANNOT_SEND = 5;
	/** 操作超时 */
	public static final short ERROR_TIMEOUT = 1;
	/** 代理服务器错误 */
	public static final short ERROR_PROXY = 2;
	/** 网络错误 */
	public static final short ERROR_NETWORK = 3;

	public static final short ERROR_REMOTE_DATA = 5;
	/** 运行时错误，调试用 */
	public static final short RUNTIME_ERROR = 4;
	// [End]

	public static final short IMO_HEART_BEAT = 1001;
	public static final short IMO_LOGIN = 1002;
	public static final short IMO_LOAD_GROUP = 2002;
	public static final short IMO_GET_CORP_INFO = 2001;

	public static final short IMO_GET_DEPT_UC = 2009;
	public static final short IMO_GET_DEPT_INFO = 2010;
	public static final short IMO_GET_ALL_EMPLOYEE_UID = 2003;
	public static final short IMO_GET_ALL_EMPLOYEE_BASIC_INFO = 3009;
	public static final short IMO_GET_EMPLOYEE_STATUS = 3900;
	public static final short IMO_GET_EMPLOYEE_PROFILE = 4010;
	public static final short IMO_UPDATE_USER_STATUS = 3901;

	public static final short IMO_UPDATE_STATUS = 1007;
	public static final short IMO_EXIT = 1003;
	public static final short IMO_FORCE_EXIT = 1004;
	public static final short IMO_GET_RELOGIN = 1015;

	public static final short IMO_SEND_MESSAGE = 5007;
	public static final short IMO_SEND_MESSAGE_ACK = 5008;
	public static final short IMO_GET_OFFLINE_MSG = 5009;
	public static final short IMO_DELETE_OFFLINE_MSG = 5010;
	public static final short IMO_GET_OFFLINE_MSG_PROFILE = 5026;
	public static final short IMO_GET_OFFLINE_MSG_CONTENTS = 5027;

	public static final short IMO_EDIT_PROFILE = 2007;

	public static final short IMO_INNER_CONTACTOR_GROUP_UC = 3101;
	public static final short IMO_OUTER_CONTACTOR_GROUP_UC = 4101;
	public static final short IMO_INNER_CONTACTOR_LIST_UC = 3102;
	public static final short IMO_OUTER_CONTACTOR_LIST_UC = 4102;

	public static final short IMO_INNER_CONTACTOR_GROUP = 3001;
	public static final short IMO_OUTER_CONTACTOR_GROUP = 4001;
	public static final short IMO_INNER_CONTACTOR_LIST = 3002;
	public static final short IMO_OUTER_CONTACTOR_LIST = 4002;

	public static final short IMO_OUTER_BASIC_INFO = 4009;

	public static final short IMO_REPORT_ERROR = 11001;
	public static final short IMO_STATUS_ACK = 3903;

	public static final short IMO_UPDATE_VERSION = 11000;

	public static final short IMO_SERVER_PROMPT = 1000;

	public static final short IMO_ERROR_PACKET = 4444;

	// -----------------------------群－－－－－－－－－－－－－－－－－－－
	/**
	 * 登录群
	 */
	public static final short IMO_LOGIN_QGROUP = 8036;
	/**
	 * 获取用户群列表
	 */
	public static final short IMO_GET_USER_QGROUP_LIST = 8009;
	/**
	 * 获取群信息
	 */
	public static final short IMO_GET_QGROUP_INFO = 8008;
	/**
	 * 获取群用户列表
	 */
	public static final short IMO_GET_QGROUP_USER_LIST = 8010;
	/**
	 * 获取用户群列表UC
	 */
	public static final short IMO_GET_USER_QGROUP_LIST_UC = 8109;
	/**
	 * 批量获取群用户列表
	 */
	public static final short IMO_BATCH_GET_QGROUP_USER_LIST_MULRET = 8111;
	/**
	 * 批量获取群信息
	 */
	public static final short IMO_BATCH_GET_USER_QGROUP_LIST_MULRET = 8110;
	/**
	 * 群操作通知消息
	 */
	public static final short IMO_QGROUP_NOTICE = 8000;
	/**
	 * 群有新人加入，通知其他用户
	 */
	public static final short IMO_NOTICE_USER_JOIN_QGROUP = 8005;
	/**
	 * 群公告更新通知
	 */
	public static final short IMO_QGROUP_MODIFY_ANNOUNCEMENT_NOTICE = 8007;
	/**
	 * 获取群用户状态
	 */
	public static final short IMO_GET_QGROUP_USERS_STATUS = 8011;

	/**
	 * 群聊天
	 */
	public static final short IMO_QGOURP_CHAT = 8012;
	/**
	 * 新消息到达通知
	 */
	public static final short IMO_NEW_MSG_NOTICE_QGROUP = 8013;
	/**
	 * 状态改变通知
	 */
	public static final short IMO_QGROUP_USER_CHANGE_STATUS_NOTICE = 8031;
	/**
	 * 退出群
	 */
	public static final short IMO_EXIT_QGROUP = 8015;
	/**
	 * 退出群通知
	 */
	public static final short IMO_EXIT_QGROUP_NOTICE_USERS = 8016;
	/**
	 * 修改群名通知
	 */
	public static final short IMO_MODIFY_QGROUP_NAME_NOTICE = 8018;
	/**
	 * 解散群
	 */
	public static final short IMO_DESTORY_THE_QGROUP = 8019;
	/**
	 * 解散群通知
	 */
	public static final short IMO_QGROUP_DESTROY_NOTICE_USER = 8020;
	/**
	 * 踢人通知
	 */
	public static final short IMO_QGROUP_KICK_USER_NOTICE = 8022;
	/**
	 * 拉群相关的脱机消息
	 */
	public static final short IMO_GET_QGROUP_OFFLINE = 8029;
	/**
	 * 拉脱机群聊天消息
	 */
	public static final short IMO_GET_OFFLINE_QGROUP_CHAT_MSG = 8030;
	/**
	 * 删除群相关的脱机消息
	 */
	public static final short IMO_DELETE_QGROUPD_OFFLINE = 8032;

	// -----------------------------多人会话－－－－－－－－－－－－－－－－－－－
	/**
	 * 2.5.1 获取用户多人会话列表UC
	 */
	public static final short IMO_GET_USER_NGROUP_LIST_UC = 9109;

	/**
	 * 2.5.2 新批量获取多人会话信息
	 */
	public static final short IMO_GET_USER_NGROUP_LIST_UC_NEW = 9110;

	/**
	 * 2.5.4 登录多人会话
	 */
	public static final short IMO_CLIENT_LOGIN_NGROUPD = 9036;

	/**
	 * 2.5.6 同意加入多人会话
	 */
	public static final short IMO_AGREE_JOIN_NGROUP = 9003;

	/**
	 * 2.5.7 多人会话 通知其他用户 有新人加入
	 */
	public static final short IMO_NOTICE_USER_JOIN_NGROUP = 9005;

	/**
	 * 2.5.9 多人会话公告更新通知
	 */
	public static final short IMO_NGROUP_MODIFY_ANNOUNCEMENT_NOTICE = 9007;

	/**
	 * 2.5.10 获取用户多人会话信息
	 */
	public static final short IMO_GET_NGROUP_INFO = 9008;

	/**
	 * 2.5.11 获取用户多人会话列表
	 */
	public static final short IMO_GET_USER_NGROUP_LIST = 9009;

	/**
	 * 2.5.12 获取多人会话用户列表
	 */
	public static final short IMO_GET_NGROUP_USER_LIST = 9010;

	/**
	 * 2.5.13 获取多人会话用户状态
	 */
	public static final short IMO_GET_NGROUP_USERS_STATUS = 9011;

	/**
	 * 2.5.14 多人会话聊天
	 */
	public static final short IMO_NGOURP_CHAT = 9012;

	/**
	 * 2.5.15 新消息到达通知
	 */
	public static final short IMO_NEW_MSG_NOTICE_NGROUP = 9013;

	/**
	 * 2.5.16 状态改变通知
	 */
	public static final short IMO_NGROUP_USER_CHANGE_STATUS_NOTICE = 9031;

	/**
	 * 2.5.17 退出多人会话
	 */
	public static final short IMO_EXIT_NGROUP = 9015;

	/**
	 * 2.5.18 退出多人会话通知
	 */
	public static final short IMO_EXIT_NGROUP_NOTICE_USERS = 9016;

	/**
	 * 2.5.20 多人会话标题修改通知
	 */
	public static final short IMO_MODIFY_NGROUP_NAME_NOTICE = 9018;

	/**
	 * 2.5.22 踢人通知
	 */
	public static final short IMO_NGROUP_KICK_USER_NOTICE = 9022;

	/**
	 * 2.5.23 拉脱机多人会话聊天消息
	 */
	public static final short IMO_GET_OFFLINE_NGROUP_CHAT_MSG = 9023;

	/**
	 * 2.5.26 多人会话操作通知消息
	 */
	public static final short IMO_NGROUP_NOTICE = 9000;
}
