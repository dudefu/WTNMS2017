package com.jhw.adm.comclient.carrier.protoco;

public class SCode {

	/**
	 * 网管ID
	 */
	public static final int nmsId = 1;

	public static final int BroadcastId = 0xFFFFFE;

	public static final byte dataIdentify = -86; // int值为0xAA
	public static final byte dataEnd = 0x16;
	public static final int FRAMETAG = 0xAA;

	public static final int ADM2000_ID = 1;

	public static final byte SPD_DATA = 0x78; // 配网仪的数据帧初步定位 120，0x78

	public static final byte ACK_TRUE = 0x05; // 正确应答帧
	public static final byte ACK_FALSE = 0x06; // 错误应答帧

	public static final byte UPLOAD_START = 0x76; // 开始升级命令码，2*byte(数据帧数量，从0开始)+4*byte（crc）+1*byte(版本号)
	public static final byte UPLOAD = 0x77; // 升级命令码,(2*byte帧数量)+data
	public static final byte UPLOAD_END = 0x28; // 升级结束命令码
	public static final byte UPLOAD_REGET_FRAME = 0x79; // 升级命令码，（2*byte帧数量）

	public static final byte UART1A = 0x03;
	public static final byte UART1B = 0x04;
	public static final byte UART1C = 0x05;
	public static final byte UART1D = 0x06;
	public static final byte UART2A = 0x07;
	public static final byte UART2B = 0x08;
	public static final byte UART2C = 0x09;
	public static final byte UART2D = 0x0A;
	public static final byte UART3A = 0x0B;
	public static final byte UART3B = 0x0C;
	public static final byte UART3C = 0x0D;
	public static final byte UART3D = 0x0E;
	public static final byte UART4A = 0x0F;
	public static final byte UART4B = 0x10;
	public static final byte UART4C = 0x11;
	public static final byte UART4D = 0x12;
	public static final byte UARTLAST = UART4D;
	public static final byte UARTALL = 0x00;

	public static final byte LOCALSERIAL = 0x00;
	// public static final byte LOCALDEV = 0x01;

	public static final byte CHANNEL1A = 0x01;
	public static final byte CHANNEL1B = 0x02;
	public static final byte CHANNEL1C = 0x03;
	public static final byte CHANNEL1D = 0x04;
	public static final byte CHANNEL2A = 0x05;
	public static final byte CHANNEL2B = 0x06;
	public static final byte CHANNEL2C = 0x07;
	public static final byte CHANNEL2D = 0x08;
	public static final byte CHANNEL3A = 0x09;
	public static final byte CHANNEL3B = 0x0A;
	public static final byte CHANNEL3C = 0x0B;
	public static final byte CHANNEL3D = 0x0C;
	public static final byte CHANNEL4A = 0x0D;
	public static final byte CHANNEL4B = 0x0E;
	public static final byte CHANNEL4C = 0x0F;
	public static final byte CHANNEL4D = 0x10;

	public static final byte INVALID_PORT_NUM = -1;

	public static final byte PLC_SET_DEVINFO = 0x32;
	public static final byte PLC_SET_DEVMANAGE = 0x33;
	public static final byte PLC_SET_DEVID = 0x34;
	public static final byte PLC_SET_DATE = 0x35;
	public static final byte PLC_SET_TIME = 0x36;

	public static final byte PLC_SET_IMAGE = 0x39;
	public static final byte PLC_SET_CONFIG = 0x3A;
	public static final byte PLC_SET_ROUTE = 0x3B;

	public static final byte PLC_SET_CONDEVID = 0x3C;
	public static final byte PLC_SET_BAUDRATE = 0x3D;
	public static final byte PLC_SET_DATABIT = 0x3E;
	public static final byte PLC_SET_STOPBIT = 0x3F;
	public static final byte PLC_SET_PARITY = 0x40;
	public static final byte PLC_SET_SRLMODE = 0x41;
	public static final byte PLC_SET_RETRANSMIT = 0x42;
	public static final byte PLC_SET_INTERVAL = 0x43;
	public static final byte PLC_SET_DEVUPPORT = 0x44;
	public static final byte PLC_SET_UPPORT = 0x45;
	public static final byte PLC_SET_ROUTCLEAR = 0x46;

	public static final byte PLC_SET_START = PLC_SET_DEVINFO;
	public static final byte PLC_SET_END = PLC_SET_ROUTCLEAR;

	public static final byte PLC_GET_DEVINFO = 0x50;
	public static final byte PLC_GET_DEVMANAGE = 0x51;
	public static final byte PLC_GET_DEVID = 0x52;
	public static final byte PLC_GET_DATE = 0x53;
	public static final byte PLC_GET_TIME = 0x54;

	public static final byte PLC_GET_IMAGE = 0x57;
	public static final byte PLC_GET_CONFIG = 0x58;
	public static final byte PLC_GET_ROUTE = 0x59;

	public static final byte PLC_GET_CONDEVID = 0x5A;
	public static final byte PLC_GET_BAUDRATE = 0x5B;
	public static final byte PLC_GET_DATABIT = 0x5C;
	public static final byte PLC_GET_STOPBIT = 0x5D;
	public static final byte PLC_GET_PARITY = 0x5E;
	public static final byte PLC_GET_SRLMODE = 0x5F;
	public static final byte PLC_GET_RETRANSMIT = 0x60;
	public static final byte PLC_GET_INTERVAL = 0x61;

	public static final byte PLC_GET_SENDBYTE = 0x64;
	public static final byte PLC_GET_RECVBYTE = 0x65;
	public static final byte PLC_GET_SENDFM = 0x66;
	public static final byte PLC_GET_RECVFM = 0x67;
	public static final byte PLC_GET_RECVERR = 0x68;
	public static final byte PLC_GET_DEV_INFO = 0x69;
	public static final byte PLC_GET_STATE = (byte) 0x90;
	public static final byte PLC_START = (byte) 0x91;
	public static final byte PLC_GET_PORT_INFO = (byte) 0x92;
	public static final byte PLC_SET_WAVE_BAND = (byte) 0x93;
	public static final byte PLC_SET_PORT_INFO = (byte) 0x94;
	public static final byte PLC_GET_WAVE_BAND = (byte) 0x95;
	public static final byte PLC_SET_DEVICE_TYPE = (byte) 0x97;

	public static final byte PLC_GET_START = PLC_GET_DEVINFO;
	public static final byte PLC_GET_END = PLC_GET_RECVERR;

	/*
	 * public static final byte sendByteRread = 0x64; //发送字节数 public static
	 * final byte reciveByteRead = 0x65; //接收字节数 public static final byte
	 * sendFrameRead = 0x66; //发送数据帧数 public static final byte reciveFrameRead =
	 * 0x67; //接收数据帧数 public static final byte reciveWrongFrame = 0x68 ;
	 * //接收错误帧数 public static final byte devChannalRead = 0x3c; //设备的编号 设备端口号：1
	 * Byte；设备编号：3 Byte public static final byte devChannalWrite = 0x5a;
	 * 
	 * public static final byte serialBandRateRead = 0x5b; //串口波特率 public static
	 * final byte serialBandRateWrite = 0x6d; public static final byte
	 * serialDataBitRead = 0x5c; //串口数据位 public static final byte
	 * serialDataVitWrite = 0x6e; public static final byte serialStopBitRead =
	 * 0x5d; //串口停止位 public static final byte serialStopBitWrite = 0x6f; public
	 * static final byte serialParityRead = 0x5e; //串口奇偶校验 public static final
	 * byte serialParityWrite = 0x70;
	 * 
	 * public static final byte transferModeRead = 0x41; //传输模式 public static
	 * final byte transferModeWrite = 0x5f; public static final byte
	 * transferNumRead = 0x60; //重传次数 public static final byte transferNumWrite
	 * =0x42; public static final byte transferTimeRead = 0x61; //重传间隔 public
	 * static final byte transferTimeWrite = 0x43;
	 * 
	 * public static final byte devLocationRead = 0x32 ; //设备安装信息 public static
	 * final byte devLocationWrite = 0x50; public static final byte
	 * devManagerRead = 0x33; //设备管理者信息 public static final byte devManagerWrite
	 * = 0x51; public static final byte devCodeNumRead = 0x34; //设备编号等信息 public
	 * static final byte devCodeNumWrite = 0x52;
	 * 
	 * public static final byte dateRead = 0x35; //日期 public static final byte
	 * dateWrite = 0x53; public static final byte timeRead = 0x36; //时间 public
	 * static final byte timeWrite = 0x54;
	 * 
	 * public static final byte setDevId = 0x3c; //设置下连设备的ID
	 */

}
