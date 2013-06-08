package com.snda.inote.model;

import java.io.Serializable;
import java.math.BigInteger;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    public Category() {
    }

    public Category(String id, String name, int type, String pid, int _default, int count) {
        this.NoteCategoryID = id;
        this.Name = name;
        this.AccessLevel = type;
        this.ParentID = pid;
        this.IsDefault = _default == 1;
        this.NoteCount = count;
    }

    private Integer _id;
    private String NoteCategoryID;
    private String ParentID;
    private String Name;

    private BigInteger UserId;
    private Integer AccessLevel;
    private boolean IsDefault;
    public boolean Encrypted;
    private int NoteCount;



    private boolean isGroupName = false; //For listView

    public Integer get_ID() {
        return _id;
    }

    public void set_ID(Integer id) {
        this._id = id;
    }


    public String getNoteCategoryID() {
        return NoteCategoryID;
    }

    public void setNoteCategoryID(String NoteCategoryID) {
        this.NoteCategoryID = NoteCategoryID;
    }


    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String ParentID) {
        this.ParentID = ParentID;
    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }


    public boolean getIsGroupName() {
        return isGroupName;
    }

    public void setIsGroupName(boolean isGroupName) {
        this.isGroupName = isGroupName;
    }


    public BigInteger getUserId() {
        return UserId;
    }

    public void setUserId(BigInteger UserId) {
        this.UserId = UserId;
    }


    public Integer getAccessLevel() {
        return AccessLevel;
    }

    public void setAccessLevel(Integer AccessLevel) {
        this.AccessLevel = AccessLevel;
    }


    public boolean getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(boolean IsDefault) {
        this.IsDefault = IsDefault;
    }

    private String Password;

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }



    public int getNoteCount() {
        return NoteCount;
    }

    public void setNoteCount(int NoteCount) {
        this.NoteCount = NoteCount;
    }


    public boolean getEncrypted() {
        return Encrypted;
    }

    public void setEncrypted(boolean Encrypted) {
        this.Encrypted = Encrypted;
    }
}
