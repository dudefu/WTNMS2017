package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex79Codec extends PacketCodec {

	@Override
	protected void decodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		if (packet.getCommandCode() != HANDLE_COMMAND)
			return;

		Hex79RSP packet79 = (Hex79RSP) packet;
		packet79.setPacketIndex(scrollBuffer.get2ByteHL());
	}

	public static final int HANDLE_COMMAND = 0x79;
}