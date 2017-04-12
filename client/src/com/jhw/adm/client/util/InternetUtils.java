package com.jhw.adm.client.util;

import org.apache.commons.lang.StringUtils;

public final class InternetUtils {

	/**
	 * ��IP�ַ���ת����ΪLong<br>
	 * ����<code>"100.4.5.6" => 1677985030</code>
	 * @param ip IP��ַ�ַ�����ʽ
	 * @return IP��ַLong��ʽ
	 */
	public static long toLong(String ip) {
		long result = 0L;
		
		String[] split = StringUtils.split(ip, "\\.");
		
		if (split != null && split.length == 4) {
			long[] longArray = new long[split.length];
			
			for (int i = 0; i < split.length; i++) {
				longArray[i] = Long.parseLong(split[i]);
			}
	
			result = 
				((longArray[0] & 0xFF) << 24) + 
				((longArray[1] & 0xFF) << 16) + 
				((longArray[2] & 0xFF) << 8) + 
				((longArray[3] & 0xFF));
		}
		
		return result;
	}
	
	/**
	 * �Ƚ�����IP�Ĵ�С<br>
	 * ����<code>"100.4.5.6" > "100.4.5.1" == true</code>
	 * @param ipa ��һ��IP��ַ
	 * @param ipb �ڶ���IP��ַ
	 * @return true ��һ��IP��ַ���ڵڶ���IP��ַ;<br>
	 *          false ��һ��IP��ַС�ڻ��ߵ��ڵڶ���IP��ַ
	 */
	public static boolean greaterThan(String ipa, String ipb) {
		boolean result = false;
		
		long ip1 = toLong(ipa);
		long ip2 = toLong(ipb);
		result = ip1 > ip2;
		
		return result;
	}
}