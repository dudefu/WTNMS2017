package com.jhw.adm.comclient.carrier.protoco;

import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.PlcFrameNumXmlOprater;
import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class PacketFormatter {

	public void format(byte[] buffer, DataPacket packet) {
		if (buffer.length == 0) {
			packet.setError("data.length == 0");
			logger().error(packet.getError());
			return;
		}

		if ((buffer[HEAD_INDEX1]) != HEAD_VALUE
				&& (buffer[HEAD_INDEX2]) != HEAD_VALUE) {
			packet.setError("Head is not eq 0xAA 0xAA");
			logger().error(packet.getError());
			return;
		}

		if (buffer[buffer.length - 1] != TAIL_VALUE) {
			packet.setError("Tail is not eq 0x16");
			logger().error(packet.getError());
			return;
		}

		long crc32Value = (long) (buffer[buffer.length - 5] & 0xFF);
		crc32Value += ((long) (buffer[buffer.length - 4] & 0xFF)) << 8;
		crc32Value += ((long) (buffer[buffer.length - 3] & 0xFF)) << 16;
		crc32Value += ((long) (buffer[buffer.length - 2] & 0xFF)) << 24;

		byte[] calcData = new byte[buffer.length - 5];
		System.arraycopy(buffer, 0, calcData, 0, buffer.length - 5);
		long calcCRC32Value = calcCRC32Value(calcData);

		// if (crc32Value != calcCRC32Value) {
		// packet.setError(String.format("crc32 invalid", crc32Value,
		// calcCRC32Value));
		// logger().error(packet.getError());
		// return;
		// }

		int actualBodyLength = buffer.length - MIN_LEN;
		int expectBodyLength = (buffer[BODY_LEN_INDEX1] & 0xFF << 8)
				+ buffer[BODY_ILEN_NDEX2] & 0xFF;

		if (actualBodyLength != expectBodyLength) {
			packet.setError("actualBodyLength != expectBodyLength");
			logger().error(packet.getError());
			return;
		}

		int commandCode = buffer[CMD_CODE_INDEX] & 0xFF;
		int destId = (int) (buffer[DEST_ID_INDEX1] & 0xFF);
		destId += ((int) (buffer[DEST_ID_INDEX2] & 0xFF)) << 8;
		destId += ((int) (buffer[DEST_ID_INDEX3] & 0xFF)) << 16;

		int srcId = (int) (buffer[SRC_ID_INDEX1] & 0xFF);
		srcId += ((int) (buffer[SRC_ID_INDEX2] & 0xFF)) << 8;
		srcId += ((int) (buffer[SRC_ID_INDEX3] & 0xFF)) << 16;

		packet.setDestId(destId);
		packet.setSrcId(srcId);
		packet.setCommandCode(commandCode);

		byte[] data = new byte[expectBodyLength];
		System.arraycopy(buffer, BODY_INDEX, data, 0, expectBodyLength);
		packet.setBody(data);
	}

	public byte[] unformat(DataPacket packet) {
		byte[] overallBuffer = null;

		overallBuffer = new byte[calcOverallLength(packet.getBodyLen())];
		ScrollBuffer scrollBuffer = new ScrollBuffer(overallBuffer);
		scrollBuffer.put(HEAD_VALUE);
		scrollBuffer.put(HEAD_VALUE);
		scrollBuffer.put2ByteHL(packet.getBodyLen());
		scrollBuffer.put3ByteLH(packet.getDestId());
		scrollBuffer.put3ByteLH(packet.getSrcId());

		byte seqNum = PlcFrameNumXmlOprater.nextSeqNum();
		scrollBuffer.put(seqNum);
		scrollBuffer.put((byte) packet.getCommandCode());
		scrollBuffer.put((byte) 0);

		for (byte b : packet.getBody()) {
			scrollBuffer.put(b);
		}

		byte[] calcData = new byte[overallBuffer.length - 5];
		System.arraycopy(overallBuffer, 0, calcData, 0,
				overallBuffer.length - 5);
		long crc32Value = calcCRC32Value(calcData);

		scrollBuffer.put4ByteLH(crc32Value);
		scrollBuffer.put(TAIL_VALUE);

		return overallBuffer;
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

	protected Logger logger() {
		return log4jLogger;
	}

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

	private static final Logger log4jLogger = Logger
			.getLogger(PacketFormatter.class);
}