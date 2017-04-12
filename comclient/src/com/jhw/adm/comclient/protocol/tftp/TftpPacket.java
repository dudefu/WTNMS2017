package com.jhw.adm.comclient.protocol.tftp;

/* Tftp包解析类
 * TFTP: Trivial File Transfer Protocol(简单文传输协议)
 * port: 69
 * 基本TFTP协议头结构:
 * --------------------------------------------------------------------
 * | 16 bits       String        16 bits   Stirng     16 bits       
 * | Opcode      Filename      0         Mode          0           
 * --------------------------------------------------------------------
 * Opcode: 操作代码或命令：
 * ----------------------------------------------------------------------------------------------
 * | Opcode    Command                        		Description                    		 
 * | 1              Read Request(RRQ)         		Request to read a file              
 * | 2              Write Request(WRQ)       		Request to write to a file   	  
 * | 3              File Data(DATA)               		Transfer of file data                 
 * | 4              Data Acknowledge(ACK)  		Acknowledgement of file data  
 * | 5              Error(ERROR)                   		Error indication                          
 * -----------------------------------------------------------------------------------------------
 * Filename: 传送的字段名称
 * Mode: 协议传输的文件数据格式，可以是 NetASCII, 也可以是标准 ASCII, 八位二进制数据或邮件标准ASCII
 *
 * RRQ/WRQ packet: Opcode 1 and 2; Mode: "netascii","octet","mail"
 * ------------------------------------------------------------------
 * | 2 bytes	String	1 byte	Stirng	2 bytes 
 * | Opcode   	Filename      0       	  Mode       0        
 * ------------------------------------------------------------------
 *
 * DATA packet: Opcode 3; 文件以512 bytes 进行传送，少于512字节的数据包说明传输结束
 * ----------------------------------------
 * | 2 bytes	2 bytes	n bytes
 * | Opcode	  Block	  Data 
 * ----------------------------------------
 *
 * ACK packet: Opcode 4
 * ----------------------------
 * | 2 bytes	2 bytes  
 * | Opcode	 Block 
 * ----------------------------
 *
 * ERROR packet: Opcode 5
 * -----------------------------------------------------------
 * | 2 bytes	2 bytes		Stirng	1 byte
 * | Opcode	ErrorCode	ErrMeg	   0 
 * -----------------------------------------------------------
 *  Error Codes:
 * ---------------------------------------------------------------------
 * | value		Meaning                               
 * | 0     		Not defined, see error message(if any).
 * | 1    		File not found.                       
 * | 2    		Access violation.                     
 * | 3     		Disk full or allocation exceeded.     
 * | 4    		Illegal TFTP operation.                
 * | 5   		Unknown transfer ID.                  
 * | 6   		File already exists.                 
 * | 7    		No such user.                         
 * ---------------------------------------------------------------------
 *
 * @author wuzhongwei
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class TftpPacket {
	private InetAddress address;
	private int port;
	private int opCode;
	private String fileName;
	private String mode;
	// private Map options;
	private int blockNumber;
	private byte data[];
	private int errorCode;
	private String errorMsg;

	private TftpPacket() {
	}

	/**
	 * Request to read a file
	 * 
	 * @param s
	 *            String
	 * @param s1
	 *            String
	 * @return TftpPacket
	 */
	public static TftpPacket getRrqPacket(String s, String s1) {
		TftpPacket tftppacket = new TftpPacket();
		tftppacket.opCode = 1;
		tftppacket.fileName = s;
		tftppacket.mode = s1;
		return tftppacket;
	}

	/**
	 * Request to write to a file
	 * 
	 * @param s
	 *            String
	 * @param s1
	 *            String
	 * @return TftpPacket
	 */
	public static TftpPacket getWrqPacket(String s, String s1) {
		TftpPacket tftppacket = new TftpPacket();
		tftppacket.opCode = 2;
		tftppacket.fileName = s;
		tftppacket.mode = s1;
		return tftppacket;
	}

	/**
	 * Transfer of file data
	 * 
	 * @param i
	 *            int
	 * @param abyte0
	 *            byte[]
	 * @return TftpPacket
	 */
	public static TftpPacket getDataPacket(int i, byte abyte0[]) {
		TftpPacket tftppacket = new TftpPacket();
		tftppacket.opCode = 3;
		tftppacket.blockNumber = i;
		tftppacket.data = abyte0;
		return tftppacket;
	}

	/**
	 * Acknowledgement of file data
	 * 
	 * @param i
	 *            int
	 * @return TftpPacket
	 */
	public static TftpPacket getAckPacket(int i) {
		TftpPacket tftppacket = new TftpPacket();
		tftppacket.opCode = 4;
		tftppacket.blockNumber = i;
		return tftppacket;
	}

	/**
	 * Error indication
	 * 
	 * @param i
	 *            int
	 * @param s
	 *            String
	 * @return TftpPacket
	 */
	public static TftpPacket getErrorPacket(int i, String s) {
		TftpPacket tftppacket = new TftpPacket();
		tftppacket.opCode = 5;
		tftppacket.errorCode = i;
		tftppacket.errorMsg = s;
		return tftppacket;
	}

	public static TftpPacket getPacket(DatagramPacket datagrampacket) {
		TftpPacket tftppacket = new TftpPacket();
		tftppacket.address = datagrampacket.getAddress();
		tftppacket.port = datagrampacket.getPort();
		tftppacket.opCode = -1;
		tftppacket.fileName = "";
		tftppacket.mode = "";
		// tftppacket.options = new HashMap();
		tftppacket.blockNumber = -1;
		tftppacket.data = new byte[0];
		tftppacket.errorCode = -1;
		tftppacket.errorMsg = "";
		DataInputStream datainputstream = new DataInputStream(
				new ByteArrayInputStream(datagrampacket.getData(), 0,
						datagrampacket.getLength()));
		try {
			tftppacket.opCode = datainputstream.readUnsignedShort();
			switch (tftppacket.opCode) {
			case 1:
			case 2:
				tftppacket.fileName = getString(datainputstream);
				tftppacket.mode = getString(datainputstream).toLowerCase();
			case 3:
				tftppacket.blockNumber = datainputstream.readUnsignedShort();
				tftppacket.data = new byte[datainputstream.available()];
				datainputstream.readFully(tftppacket.data);
				break;
			case 4:
				tftppacket.blockNumber = datainputstream.readUnsignedShort();
				break;
			case 5:
				tftppacket.errorCode = datainputstream.readUnsignedShort();
				tftppacket.errorMsg = getString(datainputstream);
				break;
			default:
				break;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tftppacket;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getOpCode() {
		return opCode;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMode() {
		return mode;
	}

	// public Map getOptions()
	// {
	// return options;
	// }

	public int getBlockNumber() {
		return blockNumber;
	}

	public byte[] getData() {
		return data;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public DatagramPacket getDatagram() {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		DataOutputStream dataoutputstream = new DataOutputStream(
				bytearrayoutputstream);

		try {
			dataoutputstream.writeShort(opCode);
			switch (opCode) {
			case 1:
			case 2:
				dataoutputstream.writeBytes(fileName);
				dataoutputstream.writeByte(0);
				dataoutputstream.writeBytes(mode);
				dataoutputstream.writeByte(0);
				break;
			case 3:
				dataoutputstream.writeShort(blockNumber);
				dataoutputstream.write(data);
				break;
			case 4:
				dataoutputstream.writeShort(blockNumber);
				break;
			case 5:
				dataoutputstream.writeShort(errorCode);
				dataoutputstream.writeBytes(errorMsg);
				dataoutputstream.writeByte(0);
				break;
			default:
				break;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		byte abyte0[] = bytearrayoutputstream.toByteArray();
		return new DatagramPacket(abyte0, abyte0.length);
	}

	private static String getString(InputStream inputstream) {
		StringBuffer stringbuffer = new StringBuffer();
		try {
			while (true) {
				int i = inputstream.read();
				if (i == 0 || i == -1) {
					break;
				}
				stringbuffer.append((char) i);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return stringbuffer.toString();
	}

}
