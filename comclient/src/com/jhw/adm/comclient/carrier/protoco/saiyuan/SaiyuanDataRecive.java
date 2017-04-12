package com.jhw.adm.comclient.carrier.protoco.saiyuan;

import com.jhw.adm.comclient.carrier.protoco.SerialDataItems;

/**
 * 赛源配电监测数据
 * 
 * @author Administrator
 * 
 */

public class SaiyuanDataRecive {
	private byte[] dataBuf; // 缓存所有收到的数据
	private byte[] dataRecived; // 数据帧格式.....
	// private byte[] dataTemp; //临时数据

	private static final int dataIdentify = 0x68; // 开始标示位 [0]
	private static final int dataEndIdentify = 0x16; // 结束标示位 [0]

	private int devNum; // 设备编号，地址域
	private int controlCode; // 控制码
	private int dataLength; // 数据段长度
	private int[] dataBody; // 数据域
	private int checkNum; // 校验码

	private int dateValidate = 0;
	private int bufDateRecivedNum = 0; // 当前缓存数据位数
	private int bufDataReadNum = 0; // 读取到的位数

	private static final int bufDataLength = 512; // 缓存buf大小

	/**
	 * 帧起始符 (68H) 地址域 (A0 A1) 帧起始符 (68H) 控制码 (C0 C1) 数据长度域 (L0 L1) 数据域 (DATA…)
	 * 校验码 (CS) 结束符 (16H)
	 */

	public SaiyuanDataRecive() {
		// dataTemp = new byte[bufDataLength];
		dataBuf = new byte[bufDataLength];
	}

	public void addFrameData(byte[] dataRecivedA, int numBytes) {

		System.out.println("Start SPD rec Data");
		// for(int i = 0; i<dataRecivedA.length;i++)
		// {
		// System.out.print(Integer.toHexString(dataRecivedA[i])+" ");
		// }
		// System.out.println("");
		if (bufDateRecivedNum + numBytes < bufDataLength) {
			for (int i = 0; i < numBytes; i++) {
				dataBuf[i + bufDateRecivedNum] = dataRecivedA[i];
			}
			bufDateRecivedNum += numBytes;
		} else {
			bufDateRecivedNum = 0;
			for (int i = 0; i < numBytes; i++) {
				dataBuf[i + bufDateRecivedNum] = dataRecivedA[i];
			}
			bufDateRecivedNum += numBytes;
		}

		getData();
	}

	public void getData() {
		// int readNum = 0;
		// System.out.println("Start SPD getData");

		while (true) {
			if (bufDataReadNum >= bufDataLength) {
				bufDataReadNum = 0;
				break;
			}
			// readNum ++;
			// System.out.println("dataBuf["+bufDataReadNum+"]="+dataBuf[bufDataReadNum]);

			if (dataBuf[bufDataReadNum] == dataIdentify) {// 0x68开头的数据
				dataLength = (int) dataBuf[bufDataReadNum + 6];
				dataLength += ((int) dataBuf[bufDataReadNum + 7]) << 8;
				// System.out.println("datalength="+dataLength);
				if (dataLength > 250 || dataLength < 0) // 数据帧长度过大，认为是错误的长度
				{
					bufDataReadNum++;
					continue;
				}

				int endDataNum = bufDataReadNum + dataLength + 9;

				if (endDataNum < bufDataLength) {
					if (endDataNum > bufDateRecivedNum) {
						break;
					}
				} else {
					endDataNum -= bufDataLength;
					if (endDataNum > bufDateRecivedNum) {
						break;
					}
				}
				if (dataBuf[bufDataReadNum + 3] == dataIdentify
						&& dataBuf[endDataNum] == dataEndIdentify)// 数据帧接收正确
				{
					// System.out.println("dataOK="+dataBuf[bufDataReadNum]);
					dataRecived = new byte[dataLength + 19];
					for (int i = 0; i < (dataLength + 19); i++) {
						dataRecived[i] = dataBuf[bufDataReadNum + i];
						dataBuf[bufDataReadNum + i] = 0;
					}
					bufDataReadNum += (dataLength + 10);
					validateData();
					break;
				}

				else// 数据帧接收出错,读下个0x68
				{
					bufDataReadNum++;
					continue;
				}
			}

			if (bufDataReadNum < bufDataLength) {
				bufDataReadNum++;
				continue;
			}

		}

	}

	public boolean validateData() {
		// System.out.println("Start SPD validateData");

		if (dataRecived[0] != dataIdentify || dataRecived[3] != dataIdentify) {
			// System.out.println("数据帧验证失败");
			return false;
		}

		/*
		 * int sunCheckNum = 0; for(int i=0;i<dataRecived.length-2;i++){
		 * sunCheckNum =+ dataRecived[i]; } checkNum =
		 * dataRecived[(dataRecived.length-2)];
		 * 
		 * if(checkNum != (sunCheckNum&0x0ff)) { //System.out.println("和校验错误");
		 * return false; }
		 */

		initData();

		return true;
	}

	public boolean initData() {
		// System.out.println("Start SPD initData");

		devNum = dataRecived[1];
		devNum += ((int) dataRecived[2]) << 8;

		controlCode = dataRecived[4];
		controlCode += ((int) dataRecived[5]) << 8;
		dataLength = dataRecived[6];
		dataLength += ((int) dataRecived[7]) << 8;

		dataBody = new int[dataLength];
		for (int i = 0; i < dataLength; i++) {
			if (dataRecived[8 + i] >= 0)
				dataBody[i] = dataRecived[8 + i];
			else
				dataBody[i] = dataRecived[8 + i] + 256;

		}
		setData();
		return true;
	}

	private boolean setData() {
		System.out.println("Start SPD  setData");

		switch (controlCode) {
		case SpdCode.realTimeDataGet: {
			// System.out.println(SerialDataItems.spdVol[0]);
			int i = 6;
			SerialDataItems.spdVol[0] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10; // 电压
			SerialDataItems.spdVol[1] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;
			SerialDataItems.spdVol[2] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;

			SerialDataItems.spdEle[0] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10; // 电流
			SerialDataItems.spdEle[1] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;
			SerialDataItems.spdEle[2] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;

			SerialDataItems.spdIdandu[2] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10; // 零序电流

			SerialDataItems.spdGlys[0] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 1000; // 功率因数
			SerialDataItems.spdGlys[1] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 1000;
			SerialDataItems.spdGlys[2] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 1000;

			SerialDataItems.spdIdandu[3] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10; // 频率

			i += 48;
			SerialDataItems.spdUsePower[0] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10; // 有功功率
			SerialDataItems.spdUsePower[1] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;
			SerialDataItems.spdUsePower[2] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;

			SerialDataItems.spdUnuserPower[0] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10; // 无功功率
			SerialDataItems.spdUnuserPower[1] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;
			SerialDataItems.spdUnuserPower[2] = ((float) (dataBody[i++] + (dataBody[i++] << 8))) / 10;

			return true;
		}
		case SpdCode.configDataGet: {

			return true;
		}

		}

		return false;
	}

	public class SpdCode {
		public static final int realTimeDataSend = 0x185; // 实时数据
		public static final int realTimeDataGet = 0x105; // 实时数据

		public static final int configDataGet = 0x106; // 配置数据
		public static final int configDataSend = 0x186; // 配置数据

	}

}
