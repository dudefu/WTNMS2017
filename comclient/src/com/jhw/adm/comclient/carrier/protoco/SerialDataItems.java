package com.jhw.adm.comclient.carrier.protoco;

import java.util.Random;

public class SerialDataItems {
	public static boolean isDataInit = false;
	// 本机状态信息
	public static String devLocationLocal;
	public static String devManagerLocal;
	public static String devCodeNumLocal;

	// 通道及时刷新信息
	public static long[] sendByte;
	public static long[] recivedByte;
	public static long[] sendFrame;
	public static long[] recivedFrame;
	public static long[] recivedWrongFrame;

	// 通道串口配置
	public static int[] portBoute;
	public static int[] dataBit;
	public static int[] parity;
	public static int[] stopbit;
	public static int[] transferMode;
	// 通道设备信息
	public static String[] devLocation;
	public static String[] devManager;
	public static String[] devCodeNum;
	// 电气量信息
	public static String[] eleV; // 电压A/B/C
	public static String[] eleC; // 电流A/B/C
	public static String[] powerYS; // 功率因素
	public static String[] powerUsed; // 有功功率
	public static String[] powerWaste; // 无功功率
	public static String[] electricityUsed; // 有功电量
	public static String[] electricityWaste; // 无功电量
	public static int[] switchState; // 开关状态

	public static float[] spdVol;
	public static float[] spdEle;
	public static float[] spdGlys;
	public static float[] spdUsePower;
	public static float[] spdUnuserPower;

	public static float[] spdIdandu;

	public SerialDataItems() {
		dataInit();
	}

	public static void dataInit() {
		int channelNum = 8;

		sendByte = new long[channelNum];
		recivedByte = new long[channelNum];
		sendFrame = new long[channelNum];
		recivedFrame = new long[channelNum];
		recivedWrongFrame = new long[channelNum];

		portBoute = new int[channelNum];
		dataBit = new int[channelNum];
		parity = new int[channelNum];
		stopbit = new int[channelNum];
		transferMode = new int[channelNum];

		devLocation = new String[channelNum];
		devManager = new String[channelNum];
		devCodeNum = new String[channelNum];

	}

	public static void spddataInit() {
		spdVol = new float[3];

		spdEle = new float[3];
		spdGlys = new float[3];
		spdUsePower = new float[3];
		spdUnuserPower = new float[3];
		spdIdandu = new float[4];

		/**
		 * 假数据显示，
		 */
		Random randomData = new Random();
		spdVol[0] = 221 + randomData.nextInt(15)
				+ ((float) randomData.nextInt(9)) / 10;

		spdGlys[0] = (float) 1.0;
		spdGlys[1] = (float) 1.0;
		spdGlys[2] = (float) 1.0;

		spdIdandu[3] = (float) 50.0;

	}

}
