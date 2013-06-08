package com.snda.inote.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import com.snda.inote.Consts;
import com.snda.inote.fileselector.FileSelector;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-17
 * Time: 下午5:08
 */
public class IntentUtils {
        public static void openFileSelector(Activity context,int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, FileSelector.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static String openCamera(Activity context,int requestCode) {
        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Consts.PATH_BASE + StringUtil.getDataFormatFileName("photo_") + ".jpg";
        File file = IOUtil.getExternalFile(path);
        Uri outputFileUri = Uri.fromFile(file);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        context.startActivityForResult(mIntent, requestCode);
        return path;
    }

    public static void openAudioRecord(Activity context,int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr");
        context.startActivityForResult(intent, requestCode);
    }
}
