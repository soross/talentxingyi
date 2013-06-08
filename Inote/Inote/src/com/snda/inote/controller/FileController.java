package com.snda.inote.controller;


import com.snda.inote.Consts;
import com.snda.inote.model.User;
import com.snda.inote.util.*;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/*
 * @Author KevinComo@gmail.com
 * 2010-7-1
 */

public class FileController {
    public static JSONObject fielUpload(String filePath, User user, List<NameValuePair> params) throws Exception {

        File file = new File(filePath);
        String fileName = file.getName();

        FileInputStream fStream = new FileInputStream(filePath);

        InputStream is = HttpUtil.uploadFile(Consts.URL_FILE_UPLOAD, params, user.getToken(), fStream, "file1", fileName);
        int b;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
            bs.write(b);
        }  //FIX Simplfy this for better performance  use buffer instead the byte
        String result = bs.toString();
        JSONObject json = new JSONObject(result);
        return json.getJSONObject("data");
    }


    public static JSONObject attachFileUpload(InputStream fStream, User user, List<NameValuePair> params, String fileName) throws Exception {
        InputStream is = HttpUtil.uploadFile(Consts.URL_FILE_UPLOAD, params, user.getToken(), fStream, "file1", fileName);
        int b;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
            bs.write(b);
        }  //FIX Simplfy this for better performance  use buffer instead the byte
        String result = bs.toString();
        JSONObject json = new JSONObject(result);
        return json.getJSONObject("data");
    }
}
