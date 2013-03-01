package com.imo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imo.global.IMOApp;
import com.imo.module.dialogue.Emotion;
import com.imo.network.Encrypt.RegexItem;

import android.R.integer;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;

public class MessageDataFilter {

	private static ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			Drawable drawable = IMOApp.getApp().getResources().getDrawable(id);
			drawable.setBounds(0, 0, 30, 30);
			return drawable;
		}
	};

	/**
	 * ��JSONObject��ʽ��Ϣת��Ϊ����ʾ����Ϣ��Ҳ���Ǿ���HTML���ε���Ϣ��
	 * 
	 * @param message
	 * @return
	 * @throws JSONException
	 */
	public static CharSequence jsonToCharSequence(JSONObject message)
			throws JSONException {
		StringBuffer buffer = new StringBuffer();
		JSONArray dataArray = (JSONArray) message.get("dt");
		// JSONObject dataValue = (JSONObject) message.get("dt");
		// JSONArray dataArray = new JSONArray(dataValue.toString());
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject item = (JSONObject) dataArray.get(i);
			Iterator<String> iterator = item.keys();
			JSONObject valueItem = null;
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ("txt".equals(key)) {
					valueItem = new JSONObject(item.getString("txt"));
					buffer.append(valueItem.getString("v")
							.replaceAll(" ", "&nbsp;").replaceAll("<", "&lt ")
							.replaceAll(">", " &gt "));
				} else if ("fmt".equals(key)) {
					valueItem = new JSONObject(item.getString("fmt"));
					String temp = valueItem.getString("v");
					if (temp.equals("nl")) {
						buffer.append("<br>");
					}
				} else if ("img".equals(key)) {
					valueItem = new JSONObject(item.getString("img"));
					String temp = valueItem.getString("v");
					if (temp.length() > 4 && temp.indexOf(".") > -1) {// ����Ӧ����xx.gif������������Ӧ�ô���4
						String index = temp.substring(0, temp.indexOf("."));
						String name = "{[" + index + "]}";
						for (int j = 0; j < Functions.getEmotion().emotion_indexs.length; j++) {
							if (name.equals(Functions.getEmotion().emotion_indexs[j])) {
								buffer.append("<img src='"
										+ Functions.getEmotion().emotion_ids[j]
										+ "'/>");
								break;
							}
						}
					}
				} else if ("lnk".equals(key)) {
					valueItem = new JSONObject(item.getString("lnk"));
					String temp = valueItem.getString("v");
					// buffer.append("<a href='" + temp + "'>" + temp + "</a>");
					buffer.append(temp);
				}
			}
		}
		return Html.fromHtml(buffer.toString(), imageGetter, null);
	}

	/**
	 * ��JSONObject��ʽ��Ϣת��Ϊ����ʾ����Ϣ��Ҳ���Ǿ���HTML���ε���Ϣ��
	 * 
	 * @param message
	 * @return
	 * @throws JSONException
	 */
	public static CharSequence jsonToCharSequenceForUrl(JSONObject message)
			throws JSONException {
		StringBuffer buffer = new StringBuffer();
		JSONArray dataArray = (JSONArray) message.get("dt");
		// JSONObject dataValue = (JSONObject) message.get("dt");
		// JSONArray dataArray = new JSONArray(dataValue.toString());
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject item = (JSONObject) dataArray.get(i);
			Iterator<String> iterator = item.keys();
			JSONObject valueItem = null;
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ("txt".equals(key)) {
					valueItem = new JSONObject(item.getString("txt"));
					buffer.append(valueItem.getString("v")
							.replaceAll(" ", "&nbsp;").replaceAll("<", "&lt ")
							.replaceAll(">", " &gt "));
				} else if ("fmt".equals(key)) {
					valueItem = new JSONObject(item.getString("fmt"));
					String temp = valueItem.getString("v");
					if (temp.equals("nl")) {
						buffer.append("<br>");
					}
				} else if ("img".equals(key)) {
					valueItem = new JSONObject(item.getString("img"));
					String temp = valueItem.getString("v");
					if (temp.length() > 4 && temp.indexOf(".") > -1) {// ����Ӧ����xx.gif������������Ӧ�ô���4
						String index = temp.substring(0, temp.indexOf("."));
						String name = "{[" + index + "]}";
						for (int j = 0; j < Functions.getEmotion().emotion_indexs.length; j++) {
							if (name.equals(Functions.getEmotion().emotion_indexs[j])) {
								buffer.append("<img src='"
										+ Functions.getEmotion().emotion_ids[j]
										+ "'/>");
								break;
							}
						}
					}
				} else if ("lnk".equals(key)) {
					valueItem = new JSONObject(item.getString("lnk"));
					String temp = valueItem.getString("v");
					// buffer.append("<a href='" + temp + "'>" + temp + "</a>");
					buffer.append(temp);
				}
			}
		}
		return Html.fromHtml(buffer.toString(), imageGetter, null);
	}

	/**
	 * ��JSONObject��ʽ��Ϣת��Ϊ����ʾ����Ϣ��Ҳ���Ǿ���HTML���ε���Ϣ,����ͼƬ��ʾΪ���֡�
	 * 
	 * @param message
	 * @return
	 * @throws JSONException
	 */
	public static CharSequence jsonToText(JSONObject message)
			throws JSONException {
		
		StringBuffer buffer = new StringBuffer();
		JSONArray dataArray = (JSONArray) message.get("dt");
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject item = (JSONObject) dataArray.get(i);
			Iterator<String> iterator = item.keys();
			JSONObject valueItem = null;
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ("txt".equals(key)) {
					valueItem = new JSONObject(item.getString("txt"));
					buffer.append(valueItem.getString("v")
							.replaceAll("<", "&lt ").replaceAll(">", " &gt "));
				} else if ("fmt".equals(key)) {
					valueItem = new JSONObject(item.getString("fmt"));
					String temp = valueItem.getString("v");
					if (temp.equals("nl")) {
						buffer.append("<br>");
					}
				} else if ("img".equals(key)) {
					valueItem = new JSONObject(item.getString("img"));
					String temp = valueItem.getString("v");
					if (temp.length() > 4 && temp.indexOf(".") > -1) {// ����Ӧ����xx.gif������������Ӧ�ô���4
						String index = temp.substring(0, temp.indexOf("."));
						String name = "{[" + index + "]}";
						for (int j = 0; j < emotion.emotion_indexs.length; j++) {
							if (name.equals(emotion.emotion_indexs[j])) {
								buffer.append("/" + emotion.emotion_texts[j]);
								break;
							}
						}
					}
				} else if ("lnk".equals(key)) {
					valueItem = new JSONObject(item.getString("lnk"));
					String temp = valueItem.getString("v");
					buffer.append("<a href='" + temp + "'>" + temp + "</a>");
				}
			}
		}
		return Html.fromHtml(buffer.toString(), imageGetter, null);
	}

	private static final int imageItemType = 0x01;
	private static final int linkItemType = 0x03;
	private static final int newlineItemType = 0x04;

	private static ArrayList<RegexItem> itemArrayList = new ArrayList<RegexItem>();
	private static Comparator<RegexItem> comparator = new Comparator<RegexItem>() {
		public int compare(RegexItem s1, RegexItem s2) {
			// ������ʼλ�ô�С��������
			return s1.getStart() - s2.getStart();
		}
	};

	public static JSONArray AnalyseStr2Json(String aOriginStr)
			throws JSONException {

		try {
			ArrayList<JSONObject> jsonCollection = new ArrayList<JSONObject>();

			AnalyseHrefTags(aOriginStr);
			AnalyseImageTags(aOriginStr);
			AnalyseNewLineTags(aOriginStr);

			if (itemArrayList.size() == 0) {
				JSONObject txtJsonObj = new JSONObject();
				JSONObject subTextJsonObj = new JSONObject();

				// aOriginStr = aOriginStr.replace(" ", "&nbsp;");

				subTextJsonObj.put("v", aOriginStr);
				txtJsonObj.put("txt", subTextJsonObj);
				jsonCollection.add(txtJsonObj);

				JSONArray jsonArr = new JSONArray(jsonCollection);
				return jsonArr;
			}

			Collections.sort(itemArrayList, comparator);

			System.out.println("itemArrayList.size()" + itemArrayList.size());
			for (int i = 0; i < itemArrayList.size(); i++) {
				RegexItem tempItem = itemArrayList.get(i);

				String textStr = null;
				if (0 == i) {
					textStr = aOriginStr.substring(0, tempItem.getStart());
				} else {
					textStr = aOriginStr.substring(itemArrayList.get(i - 1)
							.getEnd(), itemArrayList.get(i).getStart());
				}

				JSONObject sepcialJsonObj = new JSONObject();
				if (linkItemType == tempItem.getItemType()) {
					System.out.println("linkItemType" + textStr);
					JSONObject subLinkJsonObj = new JSONObject();
					String temp = textStr;
					String txtString = "";
					String mailString = "";
					// System.out.println("---------");
					if (temp.lastIndexOf(" ") > -1) {
						txtString = temp
								.substring(0, temp.lastIndexOf(" ") + 1);
						mailString = temp.substring(temp.lastIndexOf(" ") + 1);
						System.out.println(txtString);
						System.out.println(mailString);

						JSONObject txtJsonObj = new JSONObject();
						JSONObject subTextJsonObj = new JSONObject();

						// temp = temp.replace(" ", "&nbsp;");

						subTextJsonObj.put("v", txtString);
						txtJsonObj.put("txt", subTextJsonObj);
						jsonCollection.add(txtJsonObj);

						subLinkJsonObj.put("v",
								mailString + tempItem.getContent());
						sepcialJsonObj.put("lnk", subLinkJsonObj);
						jsonCollection.add(sepcialJsonObj);

					} else {
						subLinkJsonObj
								.put("v", textStr + tempItem.getContent());
						sepcialJsonObj.put("lnk", subLinkJsonObj);
						jsonCollection.add(sepcialJsonObj);
					}

					// subLinkJsonObj.put("v", textStr + tempItem.getContent());
					// sepcialJsonObj.put("lnk", subLinkJsonObj);
					// jsonCollection.add(sepcialJsonObj);
				} else {

					if (textStr.length() > 0) {
						JSONObject txtJsonObj = new JSONObject();
						JSONObject subTextJsonObj = new JSONObject();

						// textStr = textStr.replace(" ", "&nbsp;");

						subTextJsonObj.put("v", textStr);
						txtJsonObj.put("txt", subTextJsonObj);
						jsonCollection.add(txtJsonObj);
					}

					if (imageItemType == tempItem.getItemType()) {
						JSONObject subImageJsonObj = new JSONObject();
						subImageJsonObj.put("t", "sys");

						int imageStart = tempItem.getContent().indexOf("[");
						int imageEnd = tempItem.getContent().indexOf("]");
						subImageJsonObj.put("v", tempItem.getContent()
								.substring(imageStart + 1, imageEnd) + ".gif");
						sepcialJsonObj.put("img", subImageJsonObj);
					} else if (newlineItemType == tempItem.getItemType()) {
						JSONObject subNewLineJsonObj = new JSONObject();
						subNewLineJsonObj.put("v", "nl");
						sepcialJsonObj.put("fmt", subNewLineJsonObj);
					}
					jsonCollection.add(sepcialJsonObj);

					// �����ı����������⴦��
					if (i == itemArrayList.size() - 1) {
						String temp = aOriginStr.substring(itemArrayList.get(i)
								.getEnd());
						JSONObject txtJsonObj = new JSONObject();
						JSONObject subTextJsonObj = new JSONObject();

						// temp = temp.replace(" ", "&nbsp;");

						subTextJsonObj.put("v", temp);
						txtJsonObj.put("txt", subTextJsonObj);
						jsonCollection.add(txtJsonObj);
					}

				}
			}

			itemArrayList.clear();

			System.out.println("jsonCollection==========" + jsonCollection);
			JSONArray jsonArr = new JSONArray(jsonCollection);
			return jsonArr;
		} catch (Exception e) {
			e.printStackTrace();
			itemArrayList.clear();
			return null;
		}
	}

	private static void AnalyseHrefTags(String aOriginStr) {
		try {
			// search for all occurrences of pattern
			String patternString = "(http://|ftp://|https:|svn://)?(([a-zA-Z0-9]+)(\\.))?([a-zA-Z0-9]+)(\\.)((org)|(cn)|(com)|(net)|(edu)|(gov))([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
			// String patternString =
			// "(http://|ftp://|https:|svn://)?(([a-zA-Z0-9]+)(\\.))?([a-zA-Z0-9]+)([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?(\\.)((org)|(cn)|(com)|(net)|(edu)|(gov))";
			Pattern pattern = Pattern.compile(patternString,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(aOriginStr);

			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				System.out.println("start--" + start);
				System.out.println("end--" + end);
				String match = aOriginStr.substring(start, end);
				RegexItem hrefItem = new RegexItem(start, end, match,
						linkItemType);
				itemArrayList.add(hrefItem);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw e;
		}

	}

	private static void AnalyseImageTags(String aOriginStr) {
		try {
			// search for all occurrences of pattern
			String patternString = "\\{\\[\\d+\\]\\}";
			Pattern pattern = Pattern.compile(patternString,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(aOriginStr);

			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String match = aOriginStr.substring(start, end);
				RegexItem imageItem = new RegexItem(start, end, match,
						imageItemType);
				itemArrayList.add(imageItem);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static void AnalyseNewLineTags(String aOriginStr) {
		try {
			// search for all occurrences of pattern
			String patternString = "\t|\r\n|\n";
			Pattern pattern = Pattern.compile(patternString,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(aOriginStr);

			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String match = aOriginStr.substring(start, end);
				RegexItem imageItem = new RegexItem(start, end, match,
						newlineItemType);
				itemArrayList.add(imageItem);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static JSONObject buildMessage(JSONArray data, int type)
			throws JSONException {
		JSONObject message = new JSONObject();
		message.put("ver", "6.0");
		message.put("app", "mob-a");
		message.put("type", type);
		message.put("guid", "");
		message.put("ft", "");
		message.put("dt", data);
		return message;
	}

	private static Emotion emotion = Functions.getEmotion();

	/**
	 * ���û����ϵͳͼƬ��ʱ�򣬳����������֮����ʾ�����£�/��ŭ,�˷����������ǰ�/��ŭת��Ϊ{[101]}���͵Ķ�Ӧ����
	 * 
	 * @param text
	 * @return
	 */
	public static String emotionFilter(String text) {

		if (text.indexOf("/") < 0)
			return text;

		StringBuffer buffer = new StringBuffer();
		char[] chars = text.toCharArray();

		System.out.println(Arrays.toString(chars));
		boolean isAppend1 = false;// �ж��Ƿ�������
		boolean isAppend2 = false;// �ж��Ƿ�������
		boolean isAppend3 = false;// �ж��Ƿ�������
		boolean isAppend4 = false;// �ж��Ƿ�������
		boolean isAppend5 = false;// �ж��Ƿ�������
		String tmp1 = null;
		String tmp2 = null;
		String tmp3 = null;
		String tmp4 = null;
		String tmp5 = null;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '/') {
				if (i < chars.length - 1)
					tmp1 = new String(new char[] { chars[i + 1] });
				if (i < chars.length - 2)
					tmp2 = new String(new char[] { chars[i + 1], chars[i + 2] });
				if (i < chars.length - 3)
					tmp3 = new String(new char[] { chars[i + 1], chars[i + 2],
							chars[i + 3] });
				if (i < chars.length - 4)
					tmp4 = new String(new char[] { chars[i + 1], chars[i + 2],
							chars[i + 3], chars[i + 4] });
				if (i < chars.length - 5)
					tmp5 = new String(new char[] { chars[i + 1], chars[i + 2],
							chars[i + 3], chars[i + 4], chars[i + 5] });
				for (int j = 0; j < emotion.emotion_texts.length; j++) {
					if (tmp5 != null && tmp5.equals(emotion.emotion_texts[j])) {
						buffer.append(emotion.emotion_indexs[j]);
						isAppend5 = true;
						tmp5 = null;
						break;
					}
					if (tmp4 != null && tmp4.equals(emotion.emotion_texts[j])) {
						buffer.append(emotion.emotion_indexs[j]);
						isAppend4 = true;
						tmp4 = null;
						break;
					}
					if (tmp3 != null && tmp3.equals(emotion.emotion_texts[j])) {
						buffer.append(emotion.emotion_indexs[j]);
						isAppend3 = true;
						tmp3 = null;
						break;
					}
					if (tmp2 != null && tmp2.equals(emotion.emotion_texts[j])) {
						buffer.append(emotion.emotion_indexs[j]);
						isAppend2 = true;
						tmp2 = null;
						break;
					}
					if (tmp1 != null && tmp1.equals(emotion.emotion_texts[j])) {
						buffer.append(emotion.emotion_indexs[j]);
						isAppend1 = true;
						tmp1 = null;
						break;
					}
				}
				if (isAppend5) {
					isAppend5 = false;
					i = i + 5;// ��ѭ���ڲ��޸�ѭ������������
				} else if (isAppend4) {
					isAppend4 = false;
					i = i + 4;// ��ѭ���ڲ��޸�ѭ������������
				} else if (isAppend3) {
					isAppend3 = false;
					i = i + 3;// ��ѭ���ڲ��޸�ѭ������������
				} else if (isAppend2) {
					isAppend2 = false;
					i = i + 2;// ��ѭ���ڲ��޸�ѭ������������
				} else if (isAppend1) {
					isAppend1 = false;
					i = i + 1;// ��ѭ���ڲ��޸�ѭ������������
				} else {
					buffer.append(chars[i]);
				}
			} else {
				buffer.append(chars[i]);
			}
		}

		return buffer.toString();

	}
}
