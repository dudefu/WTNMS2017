package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex97Codec extends PacketCodec {
	@Override
	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		Hex97REQ packet97 = (Hex97REQ) packet;
		scrollBuffer.put(packet97.getDeviceType());
	}
}
