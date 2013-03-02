package com.imo.util;

import java.math.BigInteger;
import java.sql.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class Json {
	private JSONObject json;

	public Json(JSONObject json) {
		this.json = json;
	}

	public String getString(String key) {
		try {
			return json.getString(key);
		} catch (Exception e) {
			return "";
		}
	}

	public Integer getInt(String key) {
		try {
			return json.getInt(key);
		} catch (Exception e) {
			return 0;
		}
	}

	public Boolean getBoolean(String key) {
		try {
			return json.getBoolean(key);
		} catch (Exception e) {
			return false;
		}
	}

	public Double getDouble(String key) {
		try {
			return json.getDouble(key);
		} catch (Exception e) {
			return 0.0;
		}
	}

	public Date getDate(String key) {
		try {
			return Date.valueOf(json.getString(key));
		} catch (Exception e) {
			return new Date(2009 - 1900, 3 - 1, 9);
		}
	}

	public BigInteger getBigInteger(String key) {
		try {
			return BigInteger.valueOf(json.getInt(key));
		} catch (Exception e) {
			return BigInteger.ZERO;
		}
	}

	public String[] getStrings(String key) {
		try {
			JSONArray ja = json.getJSONArray(key);
			String arr[] = new String[ja.length()];
			for (int i = 0; i < ja.length(); i++) {
				arr[i] = ja.getString(i);
			}
			return arr;
		} catch (Exception e) {
			return new String[0];
		}
	}

	public String getStringByArray(String key) {
		try {
			JSONArray ja = json.getJSONArray(key);
			String t = "";
			for (int i = 0; i < ja.length(); i++) {
				t += ja.getString(i) + ",";
			}
			return t;
		} catch (Exception e) {
			return "";
		}
	}
}
