package com.snda.inote.model;

import java.io.Serializable;

public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String token;
    private String SndaID;
    private String Email;
    private String NickName;
    private Boolean CanLogin;


    public User(String sndaID, String token) {
        SndaID = sndaID;
        this.token = token;
    }

    public User() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    public void setSndaID(String sndaID) {
        this.SndaID = sndaID;
    }

    public String getSndaID() {
        return SndaID;
    }


    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getEmail() {
        return Email;
    }


    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getNickName() {
        return NickName;
    }


    public void setCanLogin(Boolean CanLogin) {
        this.CanLogin = CanLogin;
    }

    public Boolean getCanLogin() {
        return CanLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", SndaID='" + SndaID + '\'' +
                ", Email='" + Email + '\'' +
                ", NickName='" + NickName + '\'' +
                ", CanLogin=" + CanLogin +
                '}';
    }

    public boolean isOffLineUser(){
        return "-1".equals(SndaID) && "-1".equals(token);
    }
}
