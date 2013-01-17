package org.talentware.android.comm.network.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.talentware.android.comm.network.util.LogFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class CustomExceptionHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CustomExceptionHandler";

	private final int SDCARD_TYPE = 0; // 当前的日志记录类型为存储在SD卡下面
	private final int MEMORY_TYPE = 1; // 当前的日志记录类型为存储在内存中

	private int CURR_LOG_TYPE = SDCARD_TYPE; // 当前的日志记录类型
	private String LOG_PATH_MEMORY_DIR; // 日志文件在内存中的路径(日志文件在安装目录中的路径)
	private String LOG_PATH_SDCARD_DIR; // 日志文件在sdcard中的路径
	private String logServiceLogName = "IMO.log"; // 本服务输出的日志文件名称
	private static String CURR_INSTALL_LOG_NAME;
	public static final int MEMORY_LOG_FILE_MAX_SIZE = 4 * 1024; // 内存中日志文件最大值，1K
	private static Context context;
	private SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private OutputStreamWriter writer;
	private File file;
	private File fileLog;

	private UncaughtExceptionHandler defaultUEH;

	public CustomExceptionHandler(Context aContext) {
		context = aContext;
		init();
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	/**
	 * 记录日志服务的基本信息 防止日志服务有错，在LogCat日志中无法查找 此日志名称为IMO.log
	 * 
	 * @param msg
	 */
	private void recordLogServiceLog(String msg) {
		try {
			LogFactory.e("Exception", "catch :" + msg);
			writer = new OutputStreamWriter(new FileOutputStream(fileLog, false));// 字节流变为字符流
			Date time = new Date();
			writer.write(myLogSdf.format(time) + " : " + msg);
			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String checkLogSize() {
		if (CURR_INSTALL_LOG_NAME != null && !"".equals(CURR_INSTALL_LOG_NAME)) {
			File file = new File(CURR_INSTALL_LOG_NAME);
			if (!file.exists()) {
				return null;
			}
			LogFactory.e(TAG, "checkLog() ==> The size of the log is too big?");

			// [读取日志文件]
			FileInputStream in = null;
			byte[] buffer = null;
			try {
				in = new FileInputStream(file);
				int totalLen = in.available();
				buffer = new byte[totalLen];

				LogFactory.e("CustomExceptionHandler", "CustomExceptionHandler totalLen :" + totalLen);

				while (in.read(buffer) != -1) {
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage(), e);
				return null;
			} finally {
				try {
					if (in != null) {
						in.close();
					}

					file.delete();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage(), e);
					return null;
				}
			}

			return new String(buffer);
			// [End]
		}

		return null;
	}

	/**
	 * 创建日志目录
	 */
	private void createLogDir() {
		String filename = CURR_LOG_TYPE == MEMORY_TYPE ? LOG_PATH_MEMORY_DIR : LOG_PATH_SDCARD_DIR;
		Log.e(TAG, "filename: " + filename);

		file = new File(filename);
		boolean mkOk;
		if (!file.isDirectory()) {
			mkOk = file.mkdirs();
			if (!mkOk) {
				mkOk = file.mkdirs();
			}
		}

		CURR_INSTALL_LOG_NAME = filename + File.separator + logServiceLogName;
		fileLog = new File(CURR_INSTALL_LOG_NAME);
	}

	private void init() {
		LOG_PATH_MEMORY_DIR = context.getFilesDir().getAbsolutePath() + File.separator + "log";
		LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MyApp" + File.separator + "log";

		CURR_LOG_TYPE = getCurrLogType();

		createLogDir();
	}

	/**
	 * 获取当前应存储在内存中还是存储在SDCard中
	 * 
	 * @return
	 */
	public int getCurrLogType() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
			Log.e(TAG, "MEMORY_TYPE");
			return MEMORY_TYPE;
		} else {
			Log.e(TAG, "SDCARD_TYPE");
			return SDCARD_TYPE;
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		/**
		 * 处理异常，保存异常log或向服务器发送异常报告
		 */

		// 1.获取当前程序的版本号. 版本的id
		String versioninfo = getVersionInfo();

		// 2.获取手机的硬件信息.
		String mobileInfo = getMobileInfo();

		// 3.把错误的堆栈信息 获取出来
		String errorinfo = getErrorInfo(ex);

		recordLogServiceLog(getErrorInfo(ex));

		/*
		 * JSONObject message = new JSONObject(); try {
		 * message.put("deviceType", mobileInfo); message.put("platForm",
		 * versioninfo); message.put("errorInfo", errorinfo); } catch
		 * (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * ByteBuffer dataBuf =
		 * ReportErrorInfoOutPacket.GenerateErrorInfoBody(message.toString());
		 * ReportErrorInfoOutPacket out = new ReportErrorInfoOutPacket(dataBuf,
		 * IMOCommand.IMO_REPORT_ERROR, EngineConst.cId, EngineConst.uId);
		 * AppService
		 * .getService().getNIOThreadInstance().send(EngineConst.IMO_CONNECTION_ID
		 * , out, false);
		 */

		// 干掉当前的程序
		android.os.Process.killProcess(android.os.Process.myPid());

		// defaultUEH.uncaughtException(thread, ex);
	}

	/**
	 * 获取错误的信息
	 * 
	 * @param arg1
	 * @return
	 */
	public static String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	/**
	 * 获取手机的硬件信息
	 * 
	 * @return
	 */
	public static String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		try {

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射 ,获取私有的信息
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				if (sb.toString().getBytes().length < 512) {
					sb.append(name + "=" + value);
					sb.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取手机的版本信息
	 * 
	 * @return
	 */
	public static String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			String versionInfo = " Version:" + info.versionName + " Build:" + info.versionCode;
			return versionInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

}