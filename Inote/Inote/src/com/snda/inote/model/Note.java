package com.snda.inote.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean hasAttachments;
    private Integer _id;
    private String NoteID;
    private String Title;
    private BigInteger UserID;
    private String Content;
    private String UpdateTime;
    private String Tags;
    private String CategoryID;
    private Integer ContentTypes;
    private Integer SaveAsCount;
    private String Abstract;
    private Integer CommentCount;
    private boolean cache;
    private int cate_id;

    public int getCate_id() {
        return cate_id;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }


    public Integer get_ID() {
        return _id;
    }

    public void set_ID(Integer id) {
        this._id = id;
    }


    public void setNoteID(String noteID) {
        this.NoteID = noteID;
    }

    public String getNoteID() {
        return this.NoteID;
    }


    public void setTitle(String title) {
        this.Title = title;
    }

    public String getTitle() {
        return this.Title;
    }


    public void setContent(String content) {
        this.Content = content;
    }

    public String getContent() {
        if(this.Content==null){
            return "";
        }
        return this.Content;
    }


    public void setUserID(BigInteger userID) {
        this.UserID = userID;
    }

    public BigInteger getUserID() {
        return this.UserID;
    }


    public void setCategoryID(String categoryID) {
        this.CategoryID = categoryID;
    }

    public String getCategoryID() {
        return this.CategoryID;
    }


    public void setTags(String tags) {
        this.Tags = tags;
    }

    public String getTags() {
        if(this.Tags==null){
            return "";
        }
        return this.Tags;
    }


    public void setUpdateTime(String updateTime) {
        if (updateTime != null && !updateTime.equalsIgnoreCase("null")) {
            if (updateTime.contains("Date")) {
                long d = Long.parseLong(updateTime.replace("/Date(", "").replace(")/", ""));
                this.UpdateTime = String.valueOf(d);
                return;
            }
        }
        this.UpdateTime = String.valueOf(System.currentTimeMillis());
    }

    public String getUpdateTime() {
        return this.UpdateTime;
    }

    public Date getUpdateDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(this.UpdateTime));
        return calendar.getTime();
    }


    public void setContentTypes(Integer contentTypes) {
        this.ContentTypes = contentTypes;
    }

    public Integer getContentTypes() {
        return this.ContentTypes;
    }


    public void setCommentCount(Integer commentCount) {
        this.CommentCount = commentCount;
    }

    public Integer getCommentCount() {
        return this.CommentCount;
    }


    public void setSaveAsCount(Integer saveAsCount) {
        this.SaveAsCount = saveAsCount;
    }

    public Integer getSaveAsCount() {
        return this.SaveAsCount;
    }


    public void setAbstract(String _abstract) {
        this.Abstract = _abstract;
    }

    public String getAbstract() {
        return this.Abstract;
    }
}
