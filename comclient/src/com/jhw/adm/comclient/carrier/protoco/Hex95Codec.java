package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex95Codec extends PacketCodec {

	@Override
	protected void decodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		if (packet.getCommandCode() != HANDLE_COMMAND)
			return;

		int waveBand1 = scrollBuffer.getInt();
		int waveBand2 = scrollBuffer.getInt();
		int timeout1 = scrollBuffer.get2ByteHL();
		// int timeout2 = scrollBuffer.get2ByteHL();
		Hex95RSP hex95 = (Hex95RSP) packet;
		hex95.setWaveBand1(waveBand1);
		hex95.setWaveBand2(waveBand2);
		hex95.setTimeout1(timeout1);
		// hex95.setTimeout2(timeout2);
	}

	public static final int HANDLE_COMMAND = 0x95;
}