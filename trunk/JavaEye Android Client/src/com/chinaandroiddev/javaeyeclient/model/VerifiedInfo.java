package com.chinaandroiddev.javaeyeclient.model;
/**
 * 
 * @author mqqqvpppm
 *
 */
public class VerifiedInfo {
    public static final int VERIFY_SUCCESS = 1;
    public static final int VERIFY_ERROR = -1;
    
    public int verifyCode;
    public String verifyMessage;
    public String domain; 
    public String name;
    public long id;
}
