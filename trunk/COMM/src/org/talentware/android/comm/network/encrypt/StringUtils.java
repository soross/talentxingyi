/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talentware.android.comm.network.encrypt;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts String to and from bytes using the encodings required by the Java
 * specification. These encodings are specified in <a href=
 * "http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html"
 * >Standard charsets</a>
 * 
 * @see CharEncoding
 * @see <a
 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
 *      charsets</a>
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: StringUtils.java 801391 2009-08-05 19:55:54Z ggregory $
 * @since 1.4
 */
public class StringUtils {

	/**
	 * Encodes the given string into a sequence of bytes using the ISO-8859-1
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesIso8859_1(String string) {
		return StringUtils.getBytesUnchecked(string, CharEncoding.ISO_8859_1);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the US-ASCII
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUsAscii(String string) {
		return StringUtils.getBytesUnchecked(string, CharEncoding.US_ASCII);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the UTF-16
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUtf16(String string) {
		return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_16);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the UTF-16BE
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUtf16Be(String string) {
		return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_16BE);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the UTF-16LE
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUtf16Le(String string) {
		return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_16LE);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the UTF-8
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUtf8(String string) {
		return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_8);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the named
	 * charset, storing the result into a new byte array.
	 * <p>
	 * This method catches {@link UnsupportedEncodingException} and rethrows it
	 * as {@link IllegalStateException}, which should never happen for a
	 * required charset name. Use this method when the encoding is required to
	 * be in the JRE.
	 * </p>
	 * 
	 * @param string
	 *            the String to encode
	 * @param charsetName
	 *            The name of a required {@link java.nio.charset.Charset}
	 * @return encoded bytes
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen for a required charset name.
	 * @see CharEncoding
	 * @see String#getBytes(String)
	 */
	public static byte[] getBytesUnchecked(String string, String charsetName) {
		if (string == null) {
			return null;
		}
		try {
			return string.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw StringUtils.newIllegalStateException(charsetName, e);
		}
	}

	private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
		return new IllegalStateException(charsetName + ": " + e);
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the given charset.
	 * <p>
	 * This method catches {@link UnsupportedEncodingException} and re-throws it
	 * as {@link IllegalStateException}, which should never happen for a
	 * required charset name. Use this method when the encoding is required to
	 * be in the JRE.
	 * </p>
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @param charsetName
	 *            The name of a required {@link java.nio.charset.Charset}
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen for a required charset name.
	 * @see CharEncoding
	 * @see String#String(byte[], String)
	 */
	public static String newString(byte[] bytes, String charsetName) {
		if (bytes == null) {
			return null;
		}
		try {
			return new String(bytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw StringUtils.newIllegalStateException(charsetName, e);
		}
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the ISO-8859-1 charset.
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen since the charset is required.
	 */
	public static String newStringIso8859_1(byte[] bytes) {
		return StringUtils.newString(bytes, CharEncoding.ISO_8859_1);
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the US-ASCII charset.
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen since the charset is required.
	 */
	public static String newStringUsAscii(byte[] bytes) {
		return StringUtils.newString(bytes, CharEncoding.US_ASCII);
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the UTF-16 charset.
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen since the charset is required.
	 */
	public static String newStringUtf16(byte[] bytes) {
		return StringUtils.newString(bytes, CharEncoding.UTF_16);
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the UTF-16BE charset.
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen since the charset is required.
	 */
	public static String newStringUtf16Be(byte[] bytes) {
		return StringUtils.newString(bytes, CharEncoding.UTF_16BE);
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the UTF-16LE charset.
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen since the charset is required.
	 */
	public static String newStringUtf16Le(byte[] bytes) {
		return StringUtils.newString(bytes, CharEncoding.UTF_16LE);
	}

	/**
	 * Constructs a new <code>String</code> by decoding the specified array of
	 * bytes using the UTF-8 charset.
	 * 
	 * @param bytes
	 *            The bytes to be decoded into characters
	 * @return A new <code>String</code> decoded from the specified array of
	 *         bytes using the given charset.
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen since the charset is required.
	 */
	public static String newStringUtf8(byte[] bytes) {
		return StringUtils.newString(bytes, CharEncoding.UTF_8);
	}

	public static byte[] ToUnicode(String str) {
		if (str.length() == 0) {
			byte temp[] = new byte[3];
			return temp;
		}

		byte[] temp = null;
		try {
			temp = str.getBytes("Unicode");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] dataBuf = new byte[temp.length];

		System.arraycopy(temp, 2, dataBuf, 0, temp.length - 2);
		dataBuf[temp.length - 2] = '\0';
		dataBuf[temp.length - 1] = '\0';

		return dataBuf;
	}

	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	/**
	 * ���ַ����� Unicode ��ʽ���ַ�. �� "��" to "\u9EC4"
	 * 
	 * @param theString
	 *            ��ת����Unicode������ַ�
	 * @param escapeSpace
	 *            �Ƿ���Կո�Ϊtrueʱ�ڿո�����Ƿ�Ӹ���б�ܡ�
	 * @return ����ת����Unicode������ַ�
	 */
	public static String toEncodedUnicode(String theString, boolean escapeSpace) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);
		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace)
					outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if ((aChar < 0x0020) || (aChar > 0x007e)) {
					// ÿ��unicode��16λ��ÿ��λ��Ӧ��16���ƴӸ�λ���浽��λ
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	private static final int MASKBITS = 0x3F;
	private static final int MASKBYTE = 0x80;
	private static final int MASK2BYTES = 0xC0;
	private static final int MASK3BYTES = 0xE0;

	public static String UNICODE_TO_UTF8(byte[] b) {
		int bufLen = b.length;
		if (bufLen == 3 && b[0] == 0 && b[1] == 0 && b[2] == 0) {
			return "";
		}

		int i = 0;
		int j = 0;
		byte[] utf8Byte = new byte[b.length * 2];
		while (i < b.length - 2) {
			byte[] bUTF = new byte[1];
			int nCode = (b[i] & 0xFF) | ((b[i + 1] & 0xFF) << 8);
			if (nCode < 0x80) {
				bUTF = new byte[1];
				bUTF[0] = (byte) nCode;
			}
			// 110xxxxx 10xxxxxx
			else if (nCode < 0x800) {
				bUTF = new byte[2];
				bUTF[0] = (byte) (MASK2BYTES | nCode >> 6);
				bUTF[1] = (byte) (MASKBYTE | nCode & MASKBITS);
			}
			// 1110xxxx 10xxxxxx 10xxxxxx
			else if (nCode < 0x10000) {
				bUTF = new byte[3];
				bUTF[0] = (byte) (MASK3BYTES | nCode >> 12);
				bUTF[1] = (byte) (MASKBYTE | nCode >> 6 & MASKBITS);
				bUTF[2] = (byte) (MASKBYTE | nCode & MASKBITS);
			}
			for (int k = 0; k < bUTF.length; k++) {
				utf8Byte[j++] = bUTF[k];
			}
			i += 2;
		}
		b = new byte[j];
		System.arraycopy(utf8Byte, 0, b, 0, j);
		return new String(b);
	}

	public static final int imageItemType = 0x01;
	public static final int textItemType = 0x02;
	public static final int linkItemType = 0x03;
	public static ArrayList<RegexItem> itemArrayList = new ArrayList<RegexItem>();

	public static Comparator<RegexItem> comparator = new Comparator<RegexItem>() {
		public int compare(RegexItem s1, RegexItem s2) {
			// ������ʼλ�ô�С��������
			return s1.getStart() - s2.getStart();
		}
	};

	public static JSONArray AnalyseStr2Json(String aOriginStr) throws JSONException {
		ArrayList<JSONObject> jsonCollection = new ArrayList<JSONObject>();

		AnalyseHrefTags(aOriginStr);
		AnalyseImageTags(aOriginStr);

		Collections.sort(itemArrayList, comparator);

		for (int i = 0; i < itemArrayList.size(); i++) {
			RegexItem tempItem = itemArrayList.get(i);

			String textStr = null;
			if (0 == i)
				textStr = aOriginStr.substring(0, tempItem.getStart());
			else {
				textStr = aOriginStr.substring(itemArrayList.get(i - 1).getEnd(), itemArrayList.get(i).getStart());
			}

			if (textStr.length() > 0) {
				JSONObject txtJsonObj = new JSONObject();
				txtJsonObj.put("txt", textStr);
				jsonCollection.add(txtJsonObj);
			}

			JSONObject sepcialJsonObj = new JSONObject();

			if (imageItemType == tempItem.getItemType()) {
				sepcialJsonObj.put("img", tempItem.getContent());
			} else if (linkItemType == tempItem.getItemType()) {
				sepcialJsonObj.put("lnk", tempItem.getContent());
			}
			jsonCollection.add(sepcialJsonObj);

		}

		itemArrayList.clear();

		JSONArray jsonArr = new JSONArray(jsonCollection);
		return jsonArr;
	}

	public static void AnalyseHrefTags(String aOriginStr) {
		try {
			// search for all occurrences of pattern
			String patternString = "(http://|ftp://|https://)?(w{3}\\.)?([a-zA-Z0-9]+)(\\.)((org)|(cn)|(com)|(net)|(edu)|(gov))([\\[a-zA-Z0-9]\\-\\.,@?^=%&amp;:/~\\+#]*[\\[a-zA-Z0-9]\\-\\@?^=%&amp;/~\\+#])?";
			Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(aOriginStr);

			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String match = aOriginStr.substring(start, end);
				RegexItem hrefItem = new RegexItem(start, end, match, linkItemType);
				itemArrayList.add(hrefItem);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static void AnalyseImageTags(String aOriginStr) {
		try {
			// search for all occurrences of pattern
			String patternString = "\\{[^{}]*\\}";
			Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(aOriginStr);

			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String match = aOriginStr.substring(start, end);
				RegexItem imageItem = new RegexItem(start, end, match, imageItemType);
				itemArrayList.add(imageItem);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw e;
		}

	}

}
