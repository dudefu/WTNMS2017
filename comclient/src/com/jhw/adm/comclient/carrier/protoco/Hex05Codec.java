package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex05Codec extends PacketCodec {

	@Override
	protected void decodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		// if (packet.getCommandCode() != HANDLE_COMMAND) return;

		int returnSeqNum = scrollBuffer.getInt();
		int returnCommand = scrollBuffer.getInt();
		hex05 = (Hex05RSP) packet;
		hex05.setReturnSeqNum(returnSeqNum);
		hex05.setReturnCommand(returnCommand);
	}

	public Hex05RSP decode(byte[] data) {
		Hex05RSP hex05 = new Hex05RSP();
		decode(data, hex05);

		return hex05;
	}

	private Hex05RSP hex05;
	public static final int SUCCESS = 0x07;
	public static final int FAIL = 0x08;
	public static final int STATE = 0x90;
}