package com.snda.inote.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/*
 * @Author KevinComo@gmail.com
 * 2010-7-5
 */

public class IOUtil {
    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean isRemovedSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED);
    }

    /**
     * 获取SD卡中的文件
     *
     * @param file
     * @return
     */
    public static File getExternalFile(String file) {
        return new File(Environment.getExternalStorageDirectory(), file);
    }

    /**
     * 获取Cache中的文件
     *
     * @param context
     * @param file
     * @return
     */
    public static File getCacheFile(Context context, String file) {
        return new File(context.getCacheDir(), file);
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static File createFile(File file) throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent.exists() || parent.mkdirs())
                if (file.createNewFile())
                    return file;
            return null;
        }
        return file;
    }

    public static void move(File f1, File f2) throws Exception {
        if (!f1.exists()) {
            return;
        }
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        File dir = new File(f2.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (f2.exists()) {
            f2.delete();
        }
        FileOutputStream out = new FileOutputStream(f2);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        ByteBuffer b;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
                f1.delete();
                return;
            }
            if ((inC.size() - inC.position()) < length) {
                length = (int) (inC.size() - inC.position());
            } else
                length = 2097152;
            b = ByteBuffer.allocateDirect(length);
            inC.read(b);
            b.flip();
            outC.write(b);
            outC.force(false);
        }

    }

    public static void createFile(File file, String str) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        file = createFile(file);
        if (file == null) return;
        OutputStream os = new FileOutputStream(file);
        BufferedWriter write = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
        write.write(str);
        write.close();
        os.close();
    }

    /**
     * 删除文件
     * 删除目录下的全部文件和目录
     *
     * @param path 文件或目录名
     */
    public static void deleteAll(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (File file : files) {
            deleteAll(file);
        }
        path.delete();
    }

    public static String fileToString(File path) throws Exception {
        InputStream is = new FileInputStream(path);
        int b;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
            bs.write(b);
        }
        return bs.toString();
    }

    public static String saveImageFileTo2CacheFolder(String url) throws Exception {
        if (isRemovedSDCard()) {
            throw new Exception("not sdcard");
        }

        buildLocalDir(Consts.PATH_IMAGE_CACHE);

        int authIndex = url.indexOf("?auth");
        String bashImageUrl = authIndex != -1 ? url.substring(0, authIndex) : url;  //remove auth part
        String hashCode = String.valueOf(bashImageUrl.hashCode()) + ".jpg";
        File file = new File(getExternalFile(Consts.PATH_IMAGE_CACHE), hashCode);
        if (file.exists()) {
            return hashCode;
        }

        try {
            InputStream inputStream = fetch(url);
            Bitmap b = BitmapFactory.decodeStream(inputStream);
            FileOutputStream fos = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        }

        return hashCode;
    }


    public static String saveAttachFile2CacheFolder(String url, String fileName, String flag, boolean overwrite) throws IOException {
        buildLocalDir(Consts.PATH_FILE_CACHE);
        File dir = getExternalFile(Consts.PATH_FILE_CACHE + flag + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = getExternalFile(Consts.PATH_FILE_CACHE + flag + "/" + fileName);

        if (file.exists()) {
            if (overwrite) {
                file.delete();
            } else {
                return file.getPath();
            }
        }
        InputStream inputStream = fetch(url.replaceAll("\\\\", "/"));
        FileOutputStream fos = new FileOutputStream(file);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.flush();
        fos.close();
        inputStream.close();

        return file.getPath();
    }


    public static File copyFileToLocalTempFolder(InputStream io, String fileName) throws IOException {
        buildLocalDir(Consts.PATH_TEMP);
        if (fileName == null) return null;
        File file = getExternalFile(Consts.PATH_TEMP + fileName);
        String namebody, extension;
        String[] nameparts = fileName.split("\\.");
        if (nameparts.length > 1) {
            extension = nameparts[1];
            namebody = nameparts[0];
        } else {
            namebody = "";
            extension = fileName;
        }

        int i = 1;
        while (file.exists()) {
            file = getExternalFile(Consts.PATH_TEMP + namebody + "(" + i + ")" + "." + extension);
            i++;
        }
        FileOutputStream fi = new FileOutputStream(file);
        byte[] bt = new byte[1024];
        int b;
        while ((b = io.read(bt)) != -1) {
            fi.write(bt, 0, b);
        }
        fi.flush();
        fi.close();
        io.close();
        return file;
    }


    private static InputStream fetch(String address) throws IOException {
        HttpGet httpRequest = new HttpGet(URI.create(address));
        httpRequest.addHeader(HttpUtil.CUSTOM_OLD_HEAD_NAME, Inote.getUser().getToken());
        httpRequest.addHeader("Cookie", ".iNoteVisiting=true");
        HttpResponse response = HttpManager.execute(httpRequest);
        HttpEntity entity = response.getEntity();
//        entity.getContent();
//        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        return entity.getContent();
    }

    private static void buildLocalDir(String folderPath) {
        File file = getExternalFile(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


}