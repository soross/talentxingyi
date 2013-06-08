package com.snda.inote.controller;

import com.snda.inote.Inote;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseController {

	public static JSONArray getArrayData(String body, boolean getVersion) throws Exception{
		JSONObject json = new JSONObject(body);
		JSONObject d = json.getJSONObject("d");
		if(getVersion){
			Inote.SyncVersion = String.valueOf(d.get("Version"));
		}
		return d.getJSONArray("Data");
	}
	
	public static JSONObject getJsonData(String body) throws Exception{
		JSONObject json = new JSONObject(body);
		JSONObject d = json.getJSONObject("d");
		return d.getJSONObject("Data");
	}


	public static JSONObject getStringData(String body) throws Exception{
		JSONObject json = new JSONObject(body);
        return json.getJSONObject("d");
	}
}
