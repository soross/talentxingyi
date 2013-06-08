package com.snda.inote.util;

import com.snda.inote.exception.ApiRequestErrorException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.List;

public class HttpUtil {
    public static final String CUSTOM_HEAD_NAME = "Authorization";
    public static final String CUSTOM_OLD_HEAD_NAME = "auth";
    private static final String CONTENT_TYPE = "application/json";
    private static final String HTTP_ERROR = "connection was error.";

    public static String get(final String url, final String headValue) throws IOException {
        HttpGet httpRequest = new HttpGet(url);
        if (headValue != null) {
            httpRequest.setHeader(CUSTOM_OLD_HEAD_NAME, headValue);
        }
        httpRequest.setHeader("Content-Type", CONTENT_TYPE);
        HttpResponse httpResponse;
        try {
            httpResponse = HttpManager.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
            } else {
                throw new IOException(HTTP_ERROR);
            }
        } catch (IOException e) {
            throw new IOException(HTTP_ERROR);
        }
    }


    public static String post(final String url, List<NameValuePair> params, final String headValue) throws Exception {
        HttpPost httpRequest = new HttpPost(url);
        if (headValue != null) {
            httpRequest.setHeader(CUSTOM_HEAD_NAME, headValue);
        }
        httpRequest.setHeader("Content-Type", CONTENT_TYPE);
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpResponse httpResponse;
        try {
            httpResponse = HttpManager.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
            } else {
                String result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                throw new IOException(HTTP_ERROR);
            }
        } catch (Exception e) {
            throw new IOException(HTTP_ERROR);
        }
    }


    public static String postJSON(final String url, JSONObject params, final String headValue) throws Exception {
        HttpPost httpRequest = new HttpPost(url);
        if (headValue != null) {
            httpRequest.setHeader(CUSTOM_HEAD_NAME, headValue);
        }
        httpRequest.setHeader("Content-Type", CONTENT_TYPE);
        httpRequest.setEntity(new StringEntity(params.toString(), HTTP.UTF_8));
        HttpResponse httpResponse;

        try {
            httpResponse = HttpManager.execute(httpRequest);
        } catch (SocketException e) {
            httpResponse = HttpManager.execute(httpRequest);
        }
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
        } else if (statusCode == 302) {
            throw new IOException("302");
        } else {
            String result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
//            JSONObject jsonObject = new JSONObject(result);
            throw new ApiRequestErrorException(result);
        }
    }

    public static InputStream uploadFile(final String uploadUrl, List<NameValuePair> params, final String headValue, InputStream uploadStream, String formName, String fileName) throws IOException {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        URL url = new URL(uploadUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        /* 允许Input、Output,不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        /* setRequestProperty */
        con.setConnectTimeout(60 * 1000);
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("charset", HTTP.UTF_8);
        con.setRequestProperty(CUSTOM_HEAD_NAME, headValue);
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        /* 设置DataOutputStream */
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        for (NameValuePair nvp : params) {
            ds.writeBytes("Content-Disposition: form-data; name=\"" + nvp.getName() + "\"" + end + end);
            ds.write(nvp.getValue().getBytes(HTTP.UTF_8));
            ds.writeBytes(end + twoHyphens + boundary + end);
        }
        ds.writeBytes("Content-Disposition: form-data; name=\"" + formName + "\";filename=\"" + fileName + "\"" + end);
        //ds.writeBytes("Content-Type: image/pjpeg" + end + end);
        ds.writeBytes(end);

        /* 设置每次写入1024bytes */
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int length;
        /* 从文件读取数据至缓冲区 */
        while ((length = uploadStream.read(buffer)) != -1) {
            /* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
        uploadStream.close();
        ds.flush();
        ds.close();
        /* 取得Response内容 */
        return con.getInputStream();
    }
}
