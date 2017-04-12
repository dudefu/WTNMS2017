package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex93Codec extends PacketCodec {

	@Override
	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		Hex93REQ packet93 = (Hex93REQ) packet;
		scrollBuffer.put(packet93.getWaveBand1());
		scrollBuffer.put(packet93.getWaveBand2());
		scrollBuffer.put2ByteHL(packet93.getTimeout1());
		// scrollBuffer.put2ByteHL(packet93.getTimeout2());
	}
}