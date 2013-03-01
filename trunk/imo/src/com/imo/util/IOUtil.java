package com.imo.util;

import android.content.Context;
import android.os.Environment;

import java.io.*;

public class IOUtil {
	/* �Ƿ����SD�� */
	public static boolean isRemovedSDCard = Environment
			.getExternalStorageState().equals(Environment.MEDIA_REMOVED);// MEDIA_MOUNTED:�Ѿ����ز���ӵ�пɶ���дȨ��
	/* SDCard�洢·�� */
	public static String sdCardPath = Environment.getExternalStorageDirectory()
			.getPath() + File.separator + "imo";

	/**
	 * �����ļ�
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
	 * ��ȡSD���е��ļ�
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
	 * ���ļ����浽SDCard��
	 * 
	 * @param filename
	 *            �ļ�����
	 * @param content
	 *            �ļ�����
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
	 * ��˽���ļ��������ݣ����浽�ֻ��Դ��洢
	 * 
	 * @param filename
	 *            �ļ�����
	 * @param content
	 *            �ļ�����
	 * @param mode
	 *            �ļ�����ģʽ[Context.MODE_APPEND,Context.MODE_PRIVATE,Context.
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
	 * ��ȡ�ֻ��Դ��洢�豸�ϵ��ļ����� ʵ�ַ�ʽ���Ȱ��ļ���ȡ���ڴ棬Ȼ�����ڴ����������Ƕ�ȡ�����ļ����˷�������Ҫ�Ż�
	 * 
	 * @param filename
	 *            �ļ�����
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
	 * ���SDCard���ڣ�����SDCard�ж�ȡ�ļ��������ȡ���������ֻ��ж�ȡ�����SDCard�����ڣ������ֻ��Դ��洢�ж�ȡ�ļ�
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
	 * ���SDCard���ڣ�����SDCard�д洢�ļ�������洢���ɹ��������ֻ��Դ��洢�д洢�ļ������SDCard�����ڣ������ֻ��Դ��洢�д洢�ļ�
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
	 * ɾ���ļ� ɾ��Ŀ¼�µ�ȫ���ļ���Ŀ¼
	 * 
	 * @param path
	 *            �ļ���Ŀ¼��
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