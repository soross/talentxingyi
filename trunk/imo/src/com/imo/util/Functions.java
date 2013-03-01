package com.imo.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Vibrator;
import android.text.format.Formatter;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.imo.R;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.dialogue.Emotion;

public class Functions {
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.equals(""))
			return true;
		return false;
	}

//	public static boolean isMobileNO(String mobiles) {
//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//		Matcher m = p.matcher(mobiles);
//		return m.matches();
//	}

	private static GetChinessFirstSpell getChinessFirstSpell = null;

	public static GetChinessFirstSpell getChinessFirstSpellInstance() {
		if (getChinessFirstSpell == null)
			getChinessFirstSpell = new GetChinessFirstSpell();
		return getChinessFirstSpell;
	}

	private static Emotion emotion = null;

	public static Emotion getEmotion() {
		if (emotion == null) {
			emotion = new Emotion(IMOApp.getApp());
		}
		return emotion;
	}

	public static String buildPersonPicUrl(int cid, int uid) {
		return "http://" + Globe.ips[0] + ":" + Globe.ports[0]
				+ "/corpmgr/Smart/UI/Imo_DownLoadUI.php?cid=" + cid + "&uid="
				+ uid + "&type=1&time=0";
	}

	public static String buildCorpLogoUrl(int cid) {
		return "http://" + Globe.ips[0] + ":" + Globe.ports[0]
				+ "/corpmgr/Smart/UI/Imo_DownLoadUI.php?cid=" + cid
				+ "&uid=0&type=2&time=0";
	}

	// public static byte[] http_get(String url) throws Exception {
	// HttpGet httpRequest = new HttpGet(url);
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpResponse httpResponse = null;
	// httpResponse = httpclient.execute(httpRequest);
	// // ����ɹ�
	// if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// byte[] bTemp = EntityUtils.toByteArray(httpResponse.getEntity());
	// if (bTemp != null && bTemp.length > 0)
	// return bTemp;
	// }
	//
	// return null;
	// }

	public static byte[] http_get(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection httpURLconnection = (HttpURLConnection) url
				.openConnection();
		httpURLconnection.setRequestMethod("GET");
		httpURLconnection.setReadTimeout(5 * 1000);
		InputStream in = null;
		if (httpURLconnection.getResponseCode() == 200) {
			in = httpURLconnection.getInputStream();
			byte[] result = readStream(in);
			in.close();
			if (result != null && result.length > 0)
				return result;
		}
		return null;

	}

	private static byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}

	private static SimpleDateFormat timeFormatter = new SimpleDateFormat(
			"HH:mm:ss");

	public static String getTime() {
		return timeFormatter.format(new Date());
	}

	private static SimpleDateFormat timeNoSecondFormatter = new SimpleDateFormat(
			"HH:mm");

	public static String getTimeNoSecond() {
		return timeNoSecondFormatter.format(new Date());
	}

	public static String getTime(long time) {
		return timeFormatter.format(new Date(time));
	}

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static String getDate() {
		return dateFormatter.format(new Date());
	}

	public static String getDate(long time) {
		return dateFormatter.format(new Date(time));
	}

	private static SimpleDateFormat dateAndtimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String getFullTime() {
		return dateAndtimeFormatter.format(new Date());
	}

	/**
	 * �ַ���ת�������� ���ת����ʽΪ�գ�������Ĭ�ϸ�ʽ����ת������
	 * 
	 * @param str
	 *            �ַ���
	 * @param format
	 *            ���ڸ�ʽ
	 * @return ����
	 * @throws java.text.ParseException
	 */
	public static long strToTime(String str) throws ParseException {
		return dateAndtimeFormatter.parse(str).getTime();
	}

	public static String getCurrentActivity() {
		ActivityManager am = (ActivityManager) IMOApp.getApp()
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String cn_name = cn.getClassName();
		return cn_name
				.substring(cn_name.lastIndexOf(".") + 1, cn_name.length());
	}

	public static void backToDesk(Activity activity) {
		Intent MyIntent = new Intent(Intent.ACTION_MAIN);
		MyIntent.addCategory(Intent.CATEGORY_HOME);
		activity.startActivity(MyIntent);
	}

	public static void backToDesk(Application application) {
		Intent MyIntent = new Intent(Intent.ACTION_MAIN);
		MyIntent.addCategory(Intent.CATEGORY_HOME);
		application.startActivity(MyIntent);
	}

	public static boolean isWifi() {
		ConnectivityManager conMan = (ConnectivityManager) IMOApp.getApp()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if ("CONNECTED".equalsIgnoreCase(wifi.toString()))
			return true;
		return false;
	}

	private static Vibrator vibrator = (Vibrator) IMOApp.getApp()
			.getSystemService(Service.VIBRATOR_SERVICE);
	private static long pattern = 300;

	private static SoundPool soundPool;
	private static int sourceId;

	public static void initSoundPool() {
		soundPool = new SoundPool(1, AudioManager.STREAM_RING, 5);
		sourceId = soundPool.load(IMOApp.getApp(), R.raw.type, 0);
	}

	public static void ring() {
		// ������Ƶ���ڶ�������Ϊ����������;����������Ϊ����������;���ĸ�����Ϊ���ȼ������������Ϊѭ��������0��ѭ����-1ѭ��;����������Ϊ���ʣ�����
		// ���0.5���Ϊ2��1���������ٶ�
		int volume = getVolume();
		if (soundPool != null)
			soundPool.play(sourceId, volume, volume, 0, 0, 1);
	}

	private static int getVolume() {
		int result = -1;
		AudioManager audioManager = (AudioManager) IMOApp.getApp()
				.getSystemService(Context.AUDIO_SERVICE);
		result = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		return result;
	}

	public static void msgNotification() {
		if (Globe.is_shock)
			vibrator.vibrate(pattern);
		if (Globe.is_sound){
			if(soundPool == null)
				initSoundPool();
			ring();
		}
	}

	// ���ϵͳ�����ڴ���Ϣ
	public static String getSystemAvaialbeMemorySize() {
		ActivityManager mActivityManager = (ActivityManager) IMOApp.getApp()
				.getSystemService(Context.ACTIVITY_SERVICE);
		// ���MemoryInfo����
		MemoryInfo memoryInfo = new MemoryInfo();
		// ���ϵͳ�����ڴ棬������MemoryInfo������
		mActivityManager.getMemoryInfo(memoryInfo);
		long memSize = memoryInfo.availMem;
		// �ַ�����ת��
		String availMemStr = formateFileSize(memSize);
		return availMemStr;
	}

	// ����ϵͳ�������ַ���ת�� long -String KB/MB
	private static String formateFileSize(long size) {
		return Formatter.formatFileSize(IMOApp.getApp(), size);
	}

	public static boolean isImeShow() {
		InputMethodManager imm = (InputMethodManager) IMOApp.getApp()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	// ����������Զ���ͼƬ������false
	public static boolean checkMessage(String msg) throws JSONException {
		JSONObject jsonObject = new JSONObject(msg);
		JSONArray dataArray = (JSONArray) jsonObject.get("dt");
		int j = 0;
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject item = (JSONObject) dataArray.get(i);
			Iterator<String> iterator = item.keys();
			JSONObject valueItem = null;
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ("img".equals(key)) {
					valueItem = new JSONObject(item.getString("img"));
					String temp = valueItem.getString("t");
					if (!temp.equals("sys"))
						j++;
				}
			}
		}
		if (j == dataArray.length())
			return false;
		return true;
	}

	// ����ͼƬ
	public static Bitmap zoomImg(Bitmap bm, float size) {
		// ���ͼƬ�Ŀ��
		if (bm == null)
			return null;
		int width = bm.getWidth();
		int height = bm.getHeight();
		if (width <= size && height <= size)
			return bm;

		// �������ű���
		float scale = 1;
		if (width > height) {
			scale = size / height;
		} else {
			scale = size / width;
		}
		// ȡ����Ҫ���ŵ�matrix����
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// �õ��µ�ͼƬ
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	/**
	 * ������popup����֮�䣬Ӧ����һ����ȴʱ�䣬�������ȴʱ��֮�ڣ��ڶ���popup����ʾ
	 * 
	 * @return
	 */
	public static boolean canShow(long lastTime) {
		if (System.currentTimeMillis() - lastTime > 1000)
			return true;
		return false;
	}

	public static void showToast(Context context, String text, long lastTime) {
		if (canShow(lastTime))
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ��ʽ���ֻ�����
	 */
	public static String formatPhone(String phone) {
		if (phone == null) {
			return null;
		} else if (phone.length() != 11) {
			return phone;
		} else {
			return phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-"
					+ phone.substring(7);
		}
	}

	public static void openBrowser(Activity activity, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_uri_browsers = Uri.parse(url);
		intent.setData(content_uri_browsers);
		intent.setClassName("com.android.browser",
				"com.android.browser.BrowserActivity");
		activity.startActivity(intent);
	}
}
