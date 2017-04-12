package com.jhw.adm.comclient.ui;

public class Test {
	public static void main(String[] args) {
		String str1 = "54:68:65:20:63:75:72:72:65:6e:74:20:74:65:6d:70:65:72:61:74:75:72:65:20:6f:66:20:73:77:69:74:63:68:20:69:73:20:68:69:67:68:65:72:20:74:68:61:6e:20:34:30:20:a1:e6:20:21";
		String[] s = str1.split(":");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length - 4; i++) {
			sb.append(s[i]);
		}
		System.out.println(sb);
		// String s =
		// "5468652063757272656e742074656d7065726174757265206f662073776974636820697320686967686572207468616e20343020a1e62021";
		System.out.println(toStringHex(sb.toString()) + "\u2103 !");
	}

	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
						i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}
}
