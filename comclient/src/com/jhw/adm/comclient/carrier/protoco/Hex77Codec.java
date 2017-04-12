package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex77Codec extends PacketCodec {

	@Override
	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		Hex77REQ packet77 = (Hex77REQ) packet;
		scrollBuffer.put2ByteHL(packet77.getPacketIndex());
		int packetOffset = packet77.getPacketIndex() * Hex77REQ.PACKET_LEN;
		int packetLen = packet77.calcPacketLen();

		for (int index = 0; index < packetLen; packetOffset++, index++) {
			scrollBuffer.put(packet77.getFileBuffer()[packetOffset]);
		}
	}
}