package com.snda.inote.model;

import com.snda.inote.util.Json;
import com.snda.inote.util.StringUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: chenheng
 * Date: 11-1-18
 * Time: 上午10:35
 */
public class AttachFile {
    private int id = -1;
    private String fileName;
    private String fileDownPath;
    private double fileSize;
    private int FileType;
    private String time;
    private File file;

    private String noteId;
    private int note_id;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        if (StringUtil.hasText(time) && !time.equalsIgnoreCase("null")) {
            if (time.contains("Date")) {
                long d = Long.parseLong(time.replace("/Date(", "").replace(")/", ""));
                this.time = String.valueOf(d);
                return;
            }
        }
        this.time = String.valueOf(System.currentTimeMillis());
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownPath() {
        return fileDownPath;
    }

    public void setFileDownPath(String fileDownPath) {
        this.fileDownPath = fileDownPath;
    }


    public int getFileType() {
        return FileType;
    }

    public void setFileType(int fileType) {
        FileType = fileType;
    }

    public void loadPropertyByJson(Json json) {
        setFileName(json.getString("FileName"));
        setFileDownPath(json.getString("FilePath"));
        setFileSize(json.getDouble("FileSize"));
        setFileType(json.getInt("FileType"));
        setTime(json.getString("UploadTime"));
    }
}
