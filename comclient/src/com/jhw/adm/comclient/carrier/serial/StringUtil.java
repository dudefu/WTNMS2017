package com.jhw.adm.comclient.carrier.serial;

import java.math.BigInteger;

public final class StringUtil {

	static {
		stringBuilder = new StringBuilder();
	}

	public static String toHexString(byte[] data) {

		return toHexString(data, SPACE);
	}

	/**
	 * 线程安全
	 */
	public static synchronized String toHexString(byte[] data, String separator) {

		stringBuilder.delete(0, stringBuilder.length());

		for (int i = 0; i < data.length; i++) {

			String hex = Integer.toHexString(data[i] & 0xFF);

			if (i > 0 && separator != null) {
				stringBuilder.append(separator);
			}

			if (hex.length() == 1) {
				stringBuilder.append(ZERO_STRING);
			}

			stringBuilder.append(hex.toUpperCase());
		}

		return stringBuilder.toString();
	}

	public static String fillZero(String value, int length) {
		String result = EMPTY;
		int valueLength = 0;

		if (value != null)
			valueLength = value.length();

		int diff = length - valueLength;

		for (int i = 0; i < diff; i++) {
			result += ZERO_STRING;
		}

		result += value;

		return result;
	}

	public static byte[] toHex(String hexString, int end) {
		byte[] data = null;
		int hexStringLen = hexString.length() - end;
		int step = 2;
		int radix = 16;

		if (hexString != null && (hexStringLen % step == 0)) {
			int length = hexStringLen / step;
			data = new byte[length];

			for (int i = 0, j = 0; j < hexStringLen; i++) {
				String aHex = hexString.substring(j, j + step);
				BigInteger bigInt = new BigInteger(aHex, radix);
				data[i] = bigInt.byteValue();
				j = j + step;
			}
		}

		return data;
	}

	public final static String SPACE = " ";
	public final static String EMPTY = "";
	public final static String NEWLINE = "\r\n";
	public final static String ZERO_STRING = "0";

	private final static StringBuilder stringBuilder;
}