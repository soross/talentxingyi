package com.imo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.imo.global.IMOApp;

/**
 * SharedPreference封装
 */
public class PreferenceManager {

	/**
	 * 获得所有的数据Map
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Map<String, ?> getAll(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_APPEND);
		return sp.getAll();
	}

	/**
	 * 在指定的文件中保存数据
	 * 
	 * @param fileName
	 *        文件名称
	 * @param objs
	 *        数组{key,value}
	 */
	public static void save(String fileName, Object[] objs) {
		try {
			SharedPreferences sp = IMOApp.getApp().getSharedPreferences(fileName, Context.MODE_APPEND);
			Editor editor = sp.edit();
			if (objs[1] instanceof String) {
				editor.putString(objs[0].toString(), objs[1].toString());
			} else if (objs[1] instanceof Integer) {
				editor.putInt(objs[0].toString(), Integer.parseInt(objs[1].toString()));
			} else if (objs[1] instanceof Long) {
				editor.putLong(objs[0].toString(), Long.parseLong((objs[1].toString())));
			} else if (objs[1] instanceof Float) {
				editor.putFloat(objs[0].toString(), Float.parseFloat((objs[1].toString())));
			} else if (objs[1] instanceof Boolean) {
				editor.putBoolean(objs[0].toString(), Boolean.parseBoolean((objs[1].toString())));
			}
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * if(objs[1] instanceof String) {
		 * put(fileName,(String)objs[0],((String)objs[1]).getBytes()); } else
		 * if(objs[1] instanceof Integer) { byte[] bytes =
		 * ByteBuffer.allocate(4).putInt((Integer)objs[1]).array();
		 * put(fileName,objs[0].toString(),bytes); } else if (objs[1] instanceof
		 * Long) { byte[] bytes =
		 * ByteBuffer.allocate(8).putInt((Integer)objs[1]).array();
		 * put(fileName,objs[0].toString(),bytes); } else if (objs[1] instanceof
		 * Float) { byte[] bytes =
		 * ByteBuffer.allocate(4).putInt((Integer)objs[1]).array();
		 * put(fileName,objs[0].toString(),bytes); } else if (objs[1] instanceof
		 * Boolean) { byte[] bytes =
		 * ByteBuffer.allocate(1).putInt((Integer)objs[1]).array();
		 * put(fileName,objs[0].toString(),bytes); }
		 */
	}

	/**
	 * 在指定的文件中读取数据
	 * 
	 * @param fileName
	 *        文件名称
	 * @param objs
	 *        数组{key,defaultValue}
	 */
	public static Object get(String fileName, Object[] objs) {
		try {
			SharedPreferences sp = IMOApp.getApp().getSharedPreferences(fileName, Context.MODE_APPEND);
			if (objs[1] instanceof String) {
				return sp.getString(objs[0].toString(), objs[1].toString());
			} else if (objs[1] instanceof Integer) {
				return sp.getInt(objs[0].toString(), Integer.parseInt(objs[1].toString()));
			} else if (objs[1] instanceof Long) {
				return sp.getLong(objs[0].toString(), Long.parseLong((objs[1].toString())));
			} else if (objs[1] instanceof Float) {
				return sp.getFloat(objs[0].toString(), Float.parseFloat((objs[1].toString())));
			} else if (objs[1] instanceof Boolean) {
				return sp.getBoolean(objs[0].toString(), Boolean.parseBoolean((objs[1].toString())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * if (objs[1] instanceof String) { byte[] temp =
		 * getByteArray(fileName,(String)objs[0]); if( null != temp ) { return
		 * new String(getByteArray(fileName,(String)objs[0])); } else { return
		 * objs[1]; } } else if (objs[1] instanceof Integer) { ByteBuffer buf =
		 * ByteBuffer.allocate(4); byte[] temp =
		 * getByteArray(fileName,objs[0].toString()); if( null != temp ) {
		 * return buf.put(temp).getInt(); } else { return objs[1]; } } else if
		 * (objs[1] instanceof Long) { ByteBuffer buf = ByteBuffer.allocate(8);
		 * byte[] temp = getByteArray(fileName,objs[0].toString()); if( null !=
		 * temp ) { return buf.put(temp).getLong(); } else { return objs[1]; } }
		 * else if (objs[1] instanceof Float) { ByteBuffer buf =
		 * ByteBuffer.allocate(4); byte[] temp =
		 * getByteArray(fileName,objs[0].toString()); if( null != temp ) {
		 * return buf.put(temp).getFloat(); } else { return objs[1]; } } else if
		 * (objs[1] instanceof Boolean) { ByteBuffer buf =
		 * ByteBuffer.allocate(1); byte[] temp =
		 * getByteArray(fileName,objs[0].toString()); if( null != temp ) {
		 * return buf.put(temp).get(); } else { return objs[1]; } }
		 */
		return null;
	}

	public static boolean put(String aFileName, String key, byte[] value) {
		boolean bSaveOk = false;
		byte[] data = null;
		if (value == null) {
			throw new NullPointerException();
		}
		ByteArrayOutputStream bout = null;
		DataOutputStream dout = null;
		try {
			bout = new ByteArrayOutputStream();
			dout = new DataOutputStream(bout);
			dout.writeUTF(key);
			dout.writeInt(value.length);
			dout.write(value, 0, value.length);
			data = bout.toByteArray();

			write(aFileName, data);
			bSaveOk = true;
		} catch (Exception e) {
			bSaveOk = false;
			e.printStackTrace();
		}

		return bSaveOk;
	}

	public static byte[] getByteArray(String aFileName, String key) {
		ByteArrayInputStream bin = null;
		DataInputStream din = null;
		byte[] data = null;
		try {
			String valueKey = read(aFileName);
			din = new DataInputStream(new ByteArrayInputStream(valueKey.getBytes()));

			while (din.available() > 0) {
				String getKey = din.readUTF();
				int getLength = din.readInt();

				data = new byte[getLength];
				int bytesRead = 0;
				while (bytesRead < data.length) {
					int count = din.read(data, bytesRead, data.length - bytesRead);
					if (count == -1)
						break;
					bytesRead += count;
				}

				if (getKey.equals(key))
					break;
			}

			din.close();
			din = null;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
		}
		return data;
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			return true;
		} else {
			return false;
		}
	}

	public static String read(String file) {
		String data = "";
		try {
			FileInputStream stream = IMOApp.getApp().openFileInput(file);
			StringBuffer sb = new StringBuffer();
			int c;
			while ((c = stream.read()) != -1) {
				sb.append((char) c);
			}
			stream.close();
			data = sb.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void write(String file, byte[] msg) {
		try {
			Log.e("writeFile", "" + file);
			FileOutputStream stream = IMOApp.getApp().openFileOutput(file, Context.MODE_WORLD_WRITEABLE);
			stream.write(msg);
			stream.flush();
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
