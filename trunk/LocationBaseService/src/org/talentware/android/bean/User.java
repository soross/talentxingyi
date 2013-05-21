package org.talentware.android.bean;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-5-21
 * Time: 下午10:12
 * To change this template use File | Settings | File Templates.
 */
public class User {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String userPswd;

    /**
     * 用户权限（默认免费）
     */
    private short userLevel;

    /**
     * 用户名昵称
     */
    private String userNickName;

    /**
     * 权限开始日期
     */
    private int permissionStartDate;

    /**
     * 权限结束日期
     */
    private int permissionEndDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPswd() {
        return userPswd;
    }

    public void setUserPswd(String userPswd) {
        this.userPswd = userPswd;
    }

    public short getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(short userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public int getPermissionStartDate() {
        return permissionStartDate;
    }

    public void setPermissionStartDate(int permissionStartDate) {
        permissionStartDate = permissionStartDate;
    }

    public int getPermissionEndDate() {
        return permissionEndDate;
    }

    public void setPermissionEndDate(int permissionEndDate) {
        permissionEndDate = permissionEndDate;
    }

    public User() {
        userName = "";
        userPswd = "";
        userLevel = 0;
        userNickName = "";
    }

    public User(String aUserName, String aUserPswd) {
        this();
        userName = aUserName;
        userPswd = aUserPswd;
    }

    public User(String aUserName, String aUserPswd, String aUserNickName) {
        this();
        userName = aUserName;
        userPswd = aUserPswd;
        userNickName = aUserNickName;
    }

    public User(String aUserName, String aUserPswd, int aPermissionStartDate, int aPermissionEndDate) {
        this();
        userName = aUserName;
        userPswd = aUserPswd;
        permissionStartDate = aPermissionStartDate;
        permissionEndDate = aPermissionEndDate;
    }

    public User(String aUserName, String aUserPswd, String aUserNickName, int aPermissionStartDate, int aPermissionEndDate) {
        this();
        userName = aUserName;
        userPswd = aUserPswd;
        userNickName = aUserNickName;
        permissionStartDate = aPermissionStartDate;
        permissionEndDate = aPermissionEndDate;
    }

    //TODO: 此处做详细分类对以后是否有帮助不得知，暂时保留此写法
    private static enum USERTYPE {
        COMMON, SINAWEIBO, TECENTWEIBO;
    }

}
