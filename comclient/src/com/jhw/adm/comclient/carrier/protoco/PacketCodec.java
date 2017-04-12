package com.jhw.adm.comclient.carrier.protoco;

import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class PacketCodec {

	public PacketCodec() {
		log4jLogger = Logger.getLogger(PacketCodec.class);
	}

	/**
	 * 把DataPacket编码为byte[]
	 */
	public byte[] encode(DataPacket packet) {
		// overallBuffer = new byte[calcOverallLength(packet.getBodyLen())];
		// ScrollBuffer scrollBuffer = new ScrollBuffer(overallBuffer);
		// scrollBuffer.put(HEAD_VALUE);
		// scrollBuffer.put(HEAD_VALUE);
		// scrollBuffer.put2ByteHL(packet.getBodyLen());
		// scrollBuffer.put3ByteLH(packet.getDestId());
		// scrollBuffer.put3ByteLH(packet.getSrcId());
		//		
		// byte seqNum = PlcFrameNumXmlOprater.nextSeqNum();
		// scrollBuffer.put(seqNum);
		// scrollBuffer.put((byte) packet.getCommandCode());
		//		
		// encodeBody(scrollBuffer, packet);
		//		
		// byte[] calcData = new byte[overallBuffer.length - 5];
		// System.arraycopy(overallBuffer, 0, calcData, 0, overallBuffer.length
		// - 5);
		// long crc32Value = calcCRC32Value(calcData);
		//		
		// scrollBuffer.put4ByteLH(crc32Value);
		// scrollBuffer.put(TAIL_VALUE);
		//		
		// return overallBuffer;

		byte[] body = new byte[packet.getBodyLen()];
		packet.setBody(body);
		encodeBody(new ScrollBuffer(body), packet);

		PacketFormatter formatter = new PacketFormatter();
		return formatter.unformat(packet);
	}

	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
	}

	/**
	 * 把byte[]解码为DataPacket
	 */
	public void decode(byte[] data, DataPacket packet) {
		if (data.length == 0) {
			logger().info("data.length == 0");
			return;
		}

		if ((data[HEAD_INDEX1]) != HEAD_VALUE
				&& (data[HEAD_INDEX2]) != HEAD_VALUE) {
			logger().info("Head is not eq 0xAA 0xAA");
			return;
		}

		if (data[data.length - 1] != TAIL_VALUE) {
			logger().info("Tail is not eq 0x16");
			return;
		}

		long crc32Value = (long) (data[data.length - 5] & 0xFF);
		crc32Value += ((long) (data[data.length - 4] & 0xFF)) << 8;
		crc32Value += ((long) (data[data.length - 3] & 0xFF)) << 16;
		crc32Value += ((long) (data[data.length - 2] & 0xFF)) << 24;

		byte[] calcData = new byte[data.length - 5];
		System.arraycopy(data, 0, calcData, 0, data.length - 5);
		long calcCRC32Value = calcCRC32Value(calcData);

		// if (crc32Value != calcCRC32Value) {
		// logger().info(String.format("crc32 invalid", crc32Value,
		// calcCRC32Value));
		// return;
		// }

		int actualBodyLength = data.length - MIN_LEN;
		int expectBodyLength = (data[BODY_LEN_INDEX1] & 0xFF << 8)
				+ data[BODY_ILEN_NDEX2] & 0xFF;

		if (actualBodyLength != expectBodyLength) {
			logger().info("actualBodyLength != expectBodyLength");
			return;
		}

		int commandCode = data[CMD_CODE_INDEX] & 0xFF;
		int destId = (int) (data[DEST_ID_INDEX1] & 0xFF);
		destId += ((int) (data[DEST_ID_INDEX2] & 0xFF)) << 8;
		destId += ((int) (data[DEST_ID_INDEX3] & 0xFF)) << 16;

		int srcId = (int) (data[SRC_ID_INDEX1] & 0xFF);
		srcId += ((int) (data[SRC_ID_INDEX2] & 0xFF)) << 8;
		srcId += ((int) (data[SRC_ID_INDEX3] & 0xFF)) << 16;

		packet.setDestId(destId);
		packet.setSrcId(srcId);
		packet.setCommandCode(commandCode);

		if (expectBodyLength > 0) {
			byte[] bodyData = new byte[expectBodyLength];
			System.arraycopy(data, BODY_INDEX, bodyData, 0, expectBodyLength);
			decodeBody(new ScrollBuffer(bodyData), packet);
		}
	}

	protected void decodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
	}

	public int calcOverallLength(int bodyLen) {
		return MIN_LEN + bodyLen;
	}

	protected long calcCRC32Value(byte[] data) {
		CRC32 crc32 = new CRC32();
		// TODO: use update(byte[] b, int off, int len)
		crc32.update(data);
		return crc32.getValue();
	}

	private int head;
	private int tail;
	private long crc32Value;
	private byte[] overallBuffer;
	private static Logger log4jLogger;

	public static final int HEAD_INDEX1 = 0;
	public static final int HEAD_INDEX2 = 1;

	public static final int BODY_LEN_INDEX1 = 2;
	public static final int BODY_ILEN_NDEX2 = 3;

	public static final int DEST_ID_INDEX1 = 4;
	public static final int DEST_ID_INDEX2 = 5;
	public static final int DEST_ID_INDEX3 = 6;

	public static final int SRC_ID_INDEX1 = 7;
	public static final int SRC_ID_INDEX2 = 8;
	public static final int SRC_ID_INDEX3 = 9;

	public static final int SEQ_NUM_INDEX = 10;
	public static final int CMD_CODE_INDEX = 11;

	public static final int BODY_INDEX = 13;

	public static final byte HEAD_VALUE = (byte) 0xAA;
	public static final byte TAIL_VALUE = (byte) 0x16;

	public static final int MIN_LEN = 18;

	protected Logger logger() {
		return log4jLogger;
	}

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}

	public int getTail() {
		return tail;
	}

	public void setTail(int tail) {
		this.tail = tail;
	}

	public long getCrc32Value() {
		return crc32Value;
	}

	public byte[] getOverallBuffer() {
		return overallBuffer;
	}
}
