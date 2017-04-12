package com.jhw.adm.comclient.carrier.protoco;

import java.util.zip.CRC32;

import com.jhw.adm.comclient.carrier.PlcFrameNumXmlOprater;

public class SendDataBulder {
	private byte[] dataSend; // ���͵�����֡
	private static final byte dataIdentify = SCode.dataIdentify; // ��ʾλ [0]

	private int datalength; // ���ݶγ���[1][2]
	private int ojeDevSerialNum; // �豸���[3][4][5] ���ֽ���ǰ�����ֽ��ں�
	private int sourceSerialNum; // �豸���[6][7][8] ���ֽ���ǰ�����ֽ��ں�
	private byte dataFrameNum; // ����֡���к�[9]
	private byte dataCodeType; // ����ģʽ[10]
	private byte[] dataBody; // ���ݶ�����
	private long frameCRSD; // crcУ��
	public static byte frameNum = 1; // ���͵�֡��

	public SendDataBulder() {

	}

	public SendDataBulder(int ojeDevSerialNum, int sourceSerialNum,
			byte dataCodeType, byte[] dataBody) {
		this.datalength = dataBody.length;
		this.ojeDevSerialNum = ojeDevSerialNum;
		this.dataCodeType = dataCodeType;
		this.sourceSerialNum = sourceSerialNum;
		this.setDataBody(dataBody);

		initData();
	}

	public void setDataPara(int ojeDevSerialNum, int sourceSerialNum,
			byte dataCodeType, byte[] dataBody) {
		this.datalength = dataBody.length;
		this.ojeDevSerialNum = ojeDevSerialNum;
		this.dataCodeType = dataCodeType;
		this.sourceSerialNum = sourceSerialNum;
		this.setDataBody(dataBody);

		initData();
	}

	public byte[] getAck(byte ackType) {
		// dataSend = new byte[256];

		this.datalength = 0;

		byte[] dataSendTemp = new byte[17];
		dataSendTemp[0] = dataIdentify;
		dataSendTemp[1] = dataIdentify;
		dataSendTemp[2] = (byte) (datalength >> 8);
		dataSendTemp[3] = (byte) (datalength & 0x0ff);

		dataSendTemp[4] = (byte) (ojeDevSerialNum & 0x0ff);
		dataSendTemp[5] = (byte) ((ojeDevSerialNum & 0xff00) >> 8);
		dataSendTemp[6] = (byte) ((ojeDevSerialNum & 0xff0000) >> 16);

		dataSendTemp[7] = (byte) (sourceSerialNum & 0x0ff);
		dataSendTemp[8] = (byte) ((sourceSerialNum & 0xff00) >> 8);
		dataSendTemp[9] = (byte) ((sourceSerialNum & 0xff0000) >> 16);

		dataSendTemp[10] = 0;
		dataSendTemp[11] = ackType;

		// ��Ӧ֡���ݶ�
		dataSendTemp[12] = frameNum;
		frameNum++;
		PlcFrameNumXmlOprater.writeFrameNum(frameNum);

		frameCRSD = getCRCSD(12);
		dataSendTemp[13] = (byte) (frameCRSD & 0x0ff);
		dataSendTemp[14] = (byte) ((frameCRSD & 0xff00) >> 8);
		dataSendTemp[15] = (byte) ((frameCRSD & 0xff0000) >> 16);
		dataSendTemp[16] = (byte) ((frameCRSD & 0xff000000) >> 24);
		dataSendTemp[117] = 0x16;
		return dataSend;
	}

	/*
	 * public boolean sendAck(int devSerialNum,byte dataFrameNum,byte dataCodeType){
	 * 
	 * this.devSerialNum = devSerialNum; this.dataFrameNum = dataFrameNum;
	 * this.dataCodeType = dataCodeType; dataBody = new byte[1]; datalength = 0;
	 * initData();
	 * 
	 * return true; }
	 */

	public void setDataBody(byte[] dataBody) {
		this.dataBody = dataBody;
	}

	private void initData() { // ��ʼ����������
		dataSend = new byte[256];
		dataSend[0] = dataIdentify;
		dataSend[1] = dataIdentify;
		dataSend[2] = (byte) (datalength >> 8);
		dataSend[3] = (byte) (datalength & 0x0ff);

		dataSend[4] = (byte) (ojeDevSerialNum & 0x0ff);
		dataSend[5] = (byte) ((ojeDevSerialNum & 0xff00) >> 8);
		dataSend[6] = (byte) ((ojeDevSerialNum & 0xff0000) >> 16);

		dataSend[7] = (byte) (sourceSerialNum & 0x0ff);
		dataSend[8] = (byte) ((sourceSerialNum & 0xff00) >> 8);
		dataSend[9] = (byte) ((sourceSerialNum & 0xff0000) >> 16);

		dataSend[10] = frameNum;
		frameNum++;
		PlcFrameNumXmlOprater.writeFrameNum(frameNum);
		dataSend[11] = dataCodeType;

		if (datalength == 0) {

			frameCRSD = getCRCSD(12);
			dataSend[12] = (byte) (frameCRSD & 0x0ff);
			dataSend[13] = (byte) ((frameCRSD & 0xff00) >> 8);
			dataSend[14] = (byte) ((frameCRSD & 0xff0000) >> 16);
			dataSend[15] = (byte) ((frameCRSD & 0xff000000) >> 24);

		} else {

			for (int i = 0; i < datalength; i++) {
				dataSend[12 + i] = dataBody[i];
			}

			frameCRSD = getCRCSD(datalength + 12);
			dataSend[12 + datalength] = (byte) (frameCRSD & 0x0ff);
			dataSend[13 + datalength] = (byte) ((frameCRSD & 0xff00) >> 8);
			dataSend[14 + datalength] = (byte) ((frameCRSD & 0xff0000) >> 16);
			dataSend[15 + datalength] = (byte) ((frameCRSD & 0xff000000) >> 24);
		}

	}

	private long getCRCSD(int num) {
		byte[] data = new byte[num];
		for (int i = 0; i < num; i++) {
			data[i] = dataSend[i];
		}

		CRC32 crc32 = new CRC32();
		crc32.update(data);
		return crc32.getValue();
	}

	public byte[] getSendData() {
		byte[] dataTemp = new byte[17 + datalength];

		for (int i = 0; i < (16 + datalength); i++) {
			dataTemp[i] = dataSend[i];
		}
		dataTemp[16 + datalength] = 0x16;

		/*
		 * for(int i=0;i<dataTemp.length;i++)
		 * System.out.print(dataTemp[i]+"-"); System.out.println("");
		 */
		return dataTemp;
	}

	public static void setFrameNum(byte frameNum1) {
		frameNum = frameNum1;
	}

}
