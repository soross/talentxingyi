package org.talentware.android.comm.network.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * ��־������־Ĭ�ϻ�洢��SDcard�����û��SDcard��洢���ڴ��еİ�װĿ¼����; 1.������Ĭ����SDcard��ÿ�����һ����־�ļ�;
 * 2.�����SDCard�Ļ��Ὣ֮ǰ�ڴ��е��ļ�������SDCard�� ; 3.���û��SDCard���ڰ�װĿ¼��ֻ���浱ǰ��д��־;
 * 4.SDcard��װ��ж�ض������ڲ���2,3���л� 5.SDcard�е���־�ļ�ֻ����7��;
 * 
 * @author dfsun
 * 
 */
public class LogService extends Service {
	private static final String TAG = "LogService";

	private static final int MEMORY_LOG_FILE_MAX_SIZE = 10 * 1024 * 1024; // �ڴ�����־�ļ����ֵ��10M
	private static final int MEMORY_LOG_FILE_MONITOR_INTERVAL = 10 * 60 * 1000; // �ڴ��е���־�ļ���С���ʱ������10����
	private static final int SDCARD_LOG_FILE_SAVE_DAYS = 7; // sd������־�ļ�����ౣ������

	private String LOG_PATH_MEMORY_DIR; // ��־�ļ����ڴ��е�·��(��־�ļ��ڰ�װĿ¼�е�·��)
	private String LOG_PATH_SDCARD_DIR; // ��־�ļ���sdcard�е�·��

	@SuppressWarnings("unused")
	private String LOG_SERVICE_LOG_PATH; // ������������־����¼��־������ʧ����Ϣ

	private final int SDCARD_TYPE = 0; // ��ǰ����־��¼����Ϊ�洢��SD������
	private final int MEMORY_TYPE = 1; // ��ǰ����־��¼����Ϊ�洢���ڴ���
	private int CURR_LOG_TYPE = SDCARD_TYPE; // ��ǰ����־��¼����

	private String CURR_INSTALL_LOG_NAME; // ���ǰ����־д���ڴ��У���¼��ǰ����־�ļ����

	private String logServiceLogName = "Log.log"; // �������������־�ļ����
	private SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private OutputStreamWriter writer;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss"); // ��־��Ƹ�ʽ

	private Process process;

	private WakeLock wakeLock;

	private SDStateMonitorReceiver sdStateReceiver; // SDcard״̬���
	private LogTaskReceiver logTaskReceiver;

	/*
	 * �Ƿ����ڼ����־�ļ���С�� ���ǰ��־��¼��SDcard����Ϊfalse ���ǰ��־��¼���ڴ�����Ϊtrue
	 */
	private boolean logSizeMoniting = false;

	private static String MONITOR_LOG_SIZE_ACTION = "MONITOR_LOG_SIZE"; // ��־�ļ����action
	private static String SWITCH_LOG_FILE_ACTION = "SWITCH_LOG_FILE_ACTION"; // �л���־�ļ�action

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		init();
		register();
		deploySwitchLogFileTask();
		new LogCollectorThread().start();
	}

	private void init() {
		LOG_PATH_MEMORY_DIR = getFilesDir().getAbsolutePath() + File.separator + "log";
		LOG_SERVICE_LOG_PATH = LOG_PATH_MEMORY_DIR + File.separator + logServiceLogName;
		LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MyApp" + File.separator + "log";
		createLogDir();

		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

		CURR_LOG_TYPE = getCurrLogType();
		Log.i(TAG, "LogService onCreate");
	}

	private void register() {
		IntentFilter sdCarMonitorFilter = new IntentFilter();
		sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		sdCarMonitorFilter.addDataScheme("file");
		sdStateReceiver = new SDStateMonitorReceiver();
		registerReceiver(sdStateReceiver, sdCarMonitorFilter);

		IntentFilter logTaskFilter = new IntentFilter();
		logTaskFilter.addAction(MONITOR_LOG_SIZE_ACTION);
		logTaskFilter.addAction(SWITCH_LOG_FILE_ACTION);
		logTaskReceiver = new LogTaskReceiver();
		registerReceiver(logTaskReceiver, logTaskFilter);
	}

	/**
	 * ��ȡ��ǰӦ�洢���ڴ��л��Ǵ洢��SDCard��
	 * 
	 * @return
	 */
	public int getCurrLogType() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return MEMORY_TYPE;
		} else {
			return SDCARD_TYPE;
		}
	}

	/**
	 * ������־�л�����ÿ���賿�л���־�ļ�
	 */
	private void deploySwitchLogFileTask() {
		Intent intent = new Intent(SWITCH_LOG_FILE_ACTION);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		// ��������
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
		recordLogServiceLog("deployNextTask succ,next task time is:" + myLogSdf.format(calendar.getTime()));
	}

	/**
	 * ��־�ռ� 1.�����־���� 2.ɱ��Ӧ�ó����ѿ�����Logcat��̷�ֹ������д��һ����־�ļ� 3.������־�ռ���� 4.������־�ļ� �ƶ�
	 * OR ɾ��
	 */
	class LogCollectorThread extends Thread {

		public LogCollectorThread() {
			super("LogCollectorThread");
			Log.d(TAG, "LogCollectorThread is create");
		}

		@Override
		public void run() {
			try {
				wakeLock.acquire(); // �����ֻ�

				clearLogCache();

				List<String> orgProcessList = getAllProcess();
				List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);
				killLogcatProc(processInfoList);

				createLogCollector();

				Thread.sleep(1000);// ���ߣ������ļ���Ȼ�����ļ�����Ȼ���ļ���û��������Ӱ���ļ�ɾ��

				handleLog();

				wakeLock.release(); // �ͷ�
			} catch (Exception e) {
				e.printStackTrace();
				recordLogServiceLog(Log.getStackTraceString(e));
			}
		}
	}

	/**
	 * ÿ�μ�¼��־֮ǰ�������־�Ļ���, ��Ȼ����������־�ļ��м�¼�ظ�����־
	 */
	private void clearLogCache() {
		Process proc = null;
		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("-c");
		try {
			proc = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
			StreamConsumer errorGobbler = new StreamConsumer(proc.getErrorStream());

			StreamConsumer outputGobbler = new StreamConsumer(proc.getInputStream());

			errorGobbler.start();
			outputGobbler.start();
			if (proc.waitFor() != 0) {
				Log.e(TAG, " clearLogCache proc.waitFor() != 0");
				recordLogServiceLog("clearLogCache clearLogCache proc.waitFor() != 0");
			}
		} catch (Exception e) {
			Log.e(TAG, "clearLogCache failed", e);
			recordLogServiceLog("clearLogCache failed");
		} finally {
			try {
				proc.destroy();
			} catch (Exception e) {
				Log.e(TAG, "clearLogCache failed", e);
				recordLogServiceLog("clearLogCache failed");
			}
		}
	}

	/**
	 * �ر��ɱ���������logcat��̣� ����û����ɱ�����(����Ǳ������̿�����Logcat�ռ������ô���ߵ�USERһ��)
	 * ���رջ��ж����̶�ȡlogcat��־������Ϣд����־�ļ�
	 * 
	 * @param allProcList
	 * @return
	 */
	private void killLogcatProc(List<ProcessInfo> allProcList) {
		if (process != null) {
			process.destroy();
		}
		String packName = this.getPackageName();
		String myUser = getAppUser(packName, allProcList);

		for (ProcessInfo processInfo : allProcList) {
			if (processInfo.name.toLowerCase().equals("logcat") && processInfo.user.equals(myUser)) {
				android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
			}
		}
	}

	/**
	 * ��ȡ��������û����
	 * 
	 * @param packName
	 * @param allProcList
	 * @return
	 */
	private String getAppUser(String packName, List<ProcessInfo> allProcList) {
		for (ProcessInfo processInfo : allProcList) {
			if (processInfo.name.equals(packName)) {
				return processInfo.user;
			}
		}
		return null;
	}

	/**
	 * ���ps����õ������ݻ�ȡPID��User��name����Ϣ
	 * 
	 * @param orgProcessList
	 * @return
	 */
	private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
		List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
		for (int i = 1; i < orgProcessList.size(); i++) {
			String processInfo = orgProcessList.get(i);
			String[] proStr = processInfo.split(" ");

			List<String> orgInfo = new ArrayList<String>();
			for (String str : proStr) {
				if (!"".equals(str)) {
					orgInfo.add(str);
				}
			}
			if (orgInfo.size() == 9) {
				ProcessInfo pInfo = new ProcessInfo();
				pInfo.user = orgInfo.get(0);
				pInfo.pid = orgInfo.get(1);
				pInfo.ppid = orgInfo.get(2);
				pInfo.name = orgInfo.get(8);
				procInfoList.add(pInfo);
			}
		}
		return procInfoList;
	}

	/**
	 * ����PS����õ������Ϣ
	 * 
	 * @return USER PID PPID VSIZE RSS WCHAN PC NAME root 1 0 416 300 c00d4b28
	 *         0000cd5c S /init
	 */
	private List<String> getAllProcess() {
		List<String> orgProcList = new ArrayList<String>();
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("ps");
			StreamConsumer errorConsumer = new StreamConsumer(proc.getErrorStream());

			StreamConsumer outputConsumer = new StreamConsumer(proc.getInputStream(), orgProcList);

			errorConsumer.start();
			outputConsumer.start();
			if (proc.waitFor() != 0) {
				Log.e(TAG, "getAllProcess proc.waitFor() != 0");
				recordLogServiceLog("getAllProcess proc.waitFor() != 0");
			}
		} catch (Exception e) {
			Log.e(TAG, "getAllProcess failed", e);
			recordLogServiceLog("getAllProcess failed");
		} finally {
			try {
				proc.destroy();
			} catch (Exception e) {
				Log.e(TAG, "getAllProcess failed", e);
				recordLogServiceLog("getAllProcess failed");
			}
		}
		return orgProcList;
	}

	/**
	 * ��ʼ�ռ���־��Ϣ
	 */
	public void createLogCollector() {
		String logFileName = sdf.format(new Date()) + ".log";// ��־�ļ����
		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("-f");
		commandList.add(getLogPath());
		commandList.add("-v");
		commandList.add("time");
		commandList.add("*:I");

		try {
			process = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
			recordLogServiceLog("start collecting the log,and log name is:" + logFileName);
		} catch (Exception e) {
			Log.e(TAG, "CollectorThread == >" + e.getMessage(), e);
			recordLogServiceLog("CollectorThread == >" + e.getMessage());
		}
	}

	/**
	 * ��ݵ�ǰ�Ĵ洢λ�õõ���־�ľ�Դ洢·��
	 * 
	 * @return
	 */
	public String getLogPath() {
		createLogDir();
		String logFileName = sdf.format(new Date()) + ".log";// ��־�ļ����
		if (CURR_LOG_TYPE == MEMORY_TYPE) {
			CURR_INSTALL_LOG_NAME = logFileName;
			Log.d(TAG, "Log stored in memory, the path is:" + LOG_PATH_MEMORY_DIR + File.separator + logFileName);
			return LOG_PATH_MEMORY_DIR + File.separator + logFileName;
		} else {
			CURR_INSTALL_LOG_NAME = null;
			Log.d(TAG, "Log stored in SDcard, the path is:" + LOG_PATH_SDCARD_DIR + File.separator + logFileName);
			return LOG_PATH_SDCARD_DIR + File.separator + logFileName;
		}
	}

	/**
	 * ������־�ļ� 1.�����־�ļ��洢λ���л����ڴ��У�ɾ���������д����־�ļ� ���Ҳ�����־��С������񣬿�����־��С������涨ֵ
	 * 2.�����־�ļ��洢λ���л���SDCard�У�ɾ��7��֮ǰ����־���� �����д洢���ڴ��е���־��SDCard�У�����֮ǰ�������־��С ���ȡ��
	 */
	public void handleLog() {
		if (CURR_LOG_TYPE == MEMORY_TYPE) {
			deployLogSizeMonitorTask();
			deleteMemoryExpiredLog();
		} else {
			moveLogfile();
			cancelLogSizeMonitorTask();
			deleteSDcardExpiredLog();
		}
	}

	/**
	 * ������־��С�������
	 */
	private void deployLogSizeMonitorTask() {
		if (logSizeMoniting) { // ���ǰ���ڼ���ţ�����Ҫ������
			return;
		}
		logSizeMoniting = true;
		Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MEMORY_LOG_FILE_MONITOR_INTERVAL, sender);
		Log.d(TAG, "deployLogSizeMonitorTask() succ !");
	}

	/**
	 * ȡ������־��С�������
	 */
	private void cancelLogSizeMonitorTask() {
		logSizeMoniting = false;
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.cancel(sender);

		Log.d(TAG, "canelLogSizeMonitorTask() succ");
	}

	/**
	 * �����־�ļ���С�Ƿ񳬹��˹涨��С ���������¿���һ����־�ռ����
	 */
	private void checkLogSize() {
		if (CURR_INSTALL_LOG_NAME != null && !"".equals(CURR_INSTALL_LOG_NAME)) {
			String path = LOG_PATH_MEMORY_DIR + File.separator + CURR_INSTALL_LOG_NAME;
			File file = new File(path);
			if (!file.exists()) {
				return;
			}
			Log.d(TAG, "checkLog() ==> The size of the log is too big?");
			if (file.length() >= MEMORY_LOG_FILE_MAX_SIZE) {
				Log.d(TAG, "The log's size is too big!");
				new LogCollectorThread().start();
			}
		}
	}

	/**
	 * ������־Ŀ¼
	 */
	private void createLogDir() {
		File file = new File(LOG_PATH_MEMORY_DIR);
		boolean mkOk;
		if (!file.isDirectory()) {
			mkOk = file.mkdirs();
			if (!mkOk) {
				mkOk = file.mkdirs();
			}
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(LOG_PATH_SDCARD_DIR);
			if (!file.isDirectory()) {
				mkOk = file.mkdirs();
				if (!mkOk) {
					recordLogServiceLog("move file failed,dir is not created succ");
					return;
				}
			}
		}
	}

	/**
	 * ����־�ļ�ת�Ƶ�SD������
	 */
	private void moveLogfile() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File file = new File(LOG_PATH_SDCARD_DIR);
		if (!file.isDirectory()) {
			boolean mkOk = file.mkdirs();
			if (!mkOk) {
				return;
			}
		}

		file = new File(LOG_PATH_MEMORY_DIR);
		if (file.isDirectory()) {
			File[] allFiles = file.listFiles();
			for (File logFile : allFiles) {
				String fileName = logFile.getName();
				if (logServiceLogName.equals(fileName)) {
					continue;
				}
				boolean isSucc = copy(logFile, new File(LOG_PATH_SDCARD_DIR + File.separator + fileName));
				if (isSucc) {
					logFile.delete();
				}
			}
		}
	}

	/**
	 * ɾ���ڴ��¹��ڵ���־
	 */
	private void deleteSDcardExpiredLog() {
		File file = new File(LOG_PATH_SDCARD_DIR);
		if (file.isDirectory()) {
			File[] allFiles = file.listFiles();
			for (File logFile : allFiles) {
				String fileName = logFile.getName();
				if (logServiceLogName.equals(fileName)) {
					continue;
				}
				String createDateInfo = getFileNameWithoutExtension(fileName);
				if (canDeleteSDLog(createDateInfo)) {
					logFile.delete();
					Log.d(TAG, "delete expired log success,the log path is:" + logFile.getAbsolutePath());

				}
			}
		}
	}

	/**
	 * �ж�sdcard�ϵ���־�ļ��Ƿ����ɾ��
	 * 
	 * @param createDateStr
	 * @return
	 */
	public boolean canDeleteSDLog(String createDateStr) {
		boolean canDel = false;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1 * SDCARD_LOG_FILE_SAVE_DAYS);// ɾ��7��֮ǰ��־
		Date expiredDate = calendar.getTime();
		try {
			Date createDate = sdf.parse(createDateStr);
			canDel = createDate.before(expiredDate);
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
			canDel = false;
		}
		return canDel;
	}

	/**
	 * ɾ���ڴ��еĹ�����־��ɾ����� ���˵�ǰ����־���뵱ǰʱ��������־��������Ķ�ɾ��
	 */
	private void deleteMemoryExpiredLog() {
		File file = new File(LOG_PATH_MEMORY_DIR);
		if (file.isDirectory()) {
			File[] allFiles = file.listFiles();
			Arrays.sort(allFiles, new FileComparator());
			for (int i = 0; i < allFiles.length - 2; i++) { // "-2"��������������־�ļ�
				File _file = allFiles[i];
				if (logServiceLogName.equals(_file.getName()) || _file.getName().equals(CURR_INSTALL_LOG_NAME)) {
					continue;
				}
				_file.delete();
				Log.d(TAG, "delete expired log success,the log path is:" + _file.getAbsolutePath());
			}
		}
	}

	/**
	 * �����ļ�
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	private boolean copy(File source, File target) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			if (!target.exists()) {
				boolean createSucc = target.createNewFile();
				if (!createSucc) {
					return false;
				}
			}
			in = new FileInputStream(source);
			out = new FileOutputStream(target);
			byte[] buffer = new byte[8 * 1024];
			int count;
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage(), e);
			recordLogServiceLog("copy file fail");
			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage(), e);
				recordLogServiceLog("copy file fail");
				return false;
			}
		}

	}

	/**
	 * ��¼��־����Ļ���Ϣ ��ֹ��־�����д?��LogCat��־���޷����� ����־���ΪLog.log
	 * 
	 * @param msg
	 */
	private void recordLogServiceLog(String msg) {
		if (writer != null) {
			try {
				Date time = new Date();
				writer.write(myLogSdf.format(time) + " : " + msg);
				writer.write("\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	/**
	 * ȥ���ļ�����չ���ͣ�.log��
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileNameWithoutExtension(String fileName) {
		return fileName.substring(0, fileName.indexOf("."));
	}

	class ProcessInfo {
		public String user;
		public String pid;
		public String ppid;
		public String name;

		@Override
		public String toString() {
			String str = "user=" + user + " pid=" + pid + " ppid=" + ppid + " name=" + name;
			return str;
		}
	}

	class StreamConsumer extends Thread {
		InputStream is;
		List<String> list;

		StreamConsumer(InputStream is) {
			this.is = is;
		}

		StreamConsumer(InputStream is, List<String> list) {
			this.is = is;
			this.list = list;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (list != null) {
						list.add(line);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * ���SD��״̬
	 * 
	 * @author Administrator
	 * 
	 */
	class SDStateMonitorReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {

			if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) { // �洢����ж��
				if (CURR_LOG_TYPE == SDCARD_TYPE) {
					Log.d(TAG, "SDcar is UNMOUNTED");
					CURR_LOG_TYPE = MEMORY_TYPE;
					new LogCollectorThread().start();
				}
			} else { // �洢��������
				if (CURR_LOG_TYPE == MEMORY_TYPE) {
					Log.d(TAG, "SDcar is MOUNTED");
					CURR_LOG_TYPE = SDCARD_TYPE;
					new LogCollectorThread().start();

				}
			}
		}
	}

	/**
	 * ��־������� �л���־�������־��С
	 * 
	 * @author Administrator
	 * 
	 */
	class LogTaskReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (SWITCH_LOG_FILE_ACTION.equals(action)) {
				new LogCollectorThread().start();
			} else if (MONITOR_LOG_SIZE_ACTION.equals(action)) {
				checkLogSize();
			}
		}
	}

	class FileComparator implements Comparator<File> {
		public int compare(File file1, File file2) {
			if (logServiceLogName.equals(file1.getName())) {
				return -1;
			} else if (logServiceLogName.equals(file2.getName())) {
				return 1;
			}

			String createInfo1 = getFileNameWithoutExtension(file1.getName());
			String createInfo2 = getFileNameWithoutExtension(file2.getName());

			try {
				Date create1 = sdf.parse(createInfo1);
				Date create2 = sdf.parse(createInfo2);
				if (create1.before(create2)) {
					return -1;
				} else {
					return 1;
				}
			} catch (ParseException e) {
				return 0;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		recordLogServiceLog("LogService onDestroy");
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (process != null) {
			process.destroy();
		}

		unregisterReceiver(sdStateReceiver);
		unregisterReceiver(logTaskReceiver);
	}

}