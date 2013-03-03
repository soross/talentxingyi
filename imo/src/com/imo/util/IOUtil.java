package com.imo.util;

import android.content.Context;
import android.os.Environment;

import java.io.*;

public class IOUtil {
	/* 是否存在SD卡 */
	public static boolean isRemovedSDCard = Environment
			.getExternalStorageState().equals(Environment.MEDIA_REMOVED);// MEDIA_MOUNTED:已经挂载并且拥有可读可写权限
	/* SDCard存储路径 */
	public static String sdCardPath = Environment.getExternalStorageDirectory()
			.getPath() + File.separator + "imo";

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

	/**
	 * 获取SD卡中的文件
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static byte[] getSDCardFile(String file) throws Exception {
		File f = new File(sdCardPath, file);
		if (f == null || !f.exists()) {
			return null;
		}
		FileInputStream fis = new FileInputStream(f);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = fis.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		fis.close();
		return outStream.toByteArray();
	}

	/**
	 * 把文件保存到SDCard中
	 * 
	 * @param filename
	 *            文件名称
	 * @param content
	 *            文件内容
	 * @throws Exception
	 */
	public static boolean saveToSDCard(String filename, byte[] content)
			throws Exception {
		File file = new File(sdCardPath, filename);
		file = createFile(file);
		if (file == null || !file.exists()) {
			return false;
		}
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(content);
		outStream.close();
		return true;
	}

	/**
	 * 以私有文件保存内容，保存到手机自带存储
	 * 
	 * @param filename
	 *            文件名称
	 * @param content
	 *            文件内容
	 * @param mode
	 *            文件操作模式[Context.MODE_APPEND,Context.MODE_PRIVATE,Context.
	 *            MODE_WORLD_READABLE,Context.MODE_WORLD_WRITEABLE]
	 * @throws Exception
	 */
	public static void saveToPhone(String filename, byte[] content,
			Context context, int mode) throws Exception {
		File file = new File(context.getFilesDir(), filename);
		createFile(file);
		FileOutputStream outStream = context.openFileOutput(file.getName(),
				mode);
		outStream.write(content);
		outStream.close();
	}

	/**
	 * 读取手机自带存储设备上的文件内容 实现方式是先把文件读取到内存，然后由内存输出，如果是读取大型文件，此方法还需要优化
	 * 
	 * @param filename
	 *            文件名称
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFileFromPhone(String filename, Context context)
			throws Exception {
		File file = new File(context.getFilesDir(), filename);
		// createFile(file);
		FileInputStream inStream = context.openFileInput(file.getName());
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 如果SDCard存在，则在SDCard中读取文件，如果读取不到，则到手机中读取，如果SDCard不存在，则在手机自带存储中读取文件
	 * 
	 * @param filename
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFile(String filename, Context context) {
		try {
			if (isRemovedSDCard) {
				return readFileFromPhone(filename, context);
			} else {
				byte[] tmp = getSDCardFile(filename);
				return tmp == null ? readFileFromPhone(filename, context) : tmp;
			}
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 如果SDCard存在，则在SDCard中存储文件，如果存储不成功，则在手机自带存储中存储文件。如果SDCard不存在，则在手机自带存储中存储文件
	 * 
	 * @param filename
	 * @param content
	 * @param context
	 * @param mode
	 * @throws Exception
	 */
	public static void saveFile(String filename, byte[] content,
			Context context, int mode) throws Exception {
		if (isRemovedSDCard) {
			saveToPhone(filename, content, context, mode);
		} else {
			boolean ok = saveToSDCard(filename, content);
			if (!ok)
				saveToPhone(filename, content, context, mode);
		}

	}

	/**
	 * 删除文件 删除目录下的全部文件和目录
	 * 
	 * @param path
	 *            文件或目录名
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

}