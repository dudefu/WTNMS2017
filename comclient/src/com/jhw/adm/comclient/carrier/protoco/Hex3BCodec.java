package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex3BCodec extends PacketCodec {

	@Override
	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		Hex3BREQ packet3B = (Hex3BREQ) packet;
		scrollBuffer.put((byte) packet3B.getRouterCount());

		for (Route route : packet3B.getRoutes()) {
			scrollBuffer.put((byte) route.getPort());
			scrollBuffer.put3ByteLH(route.getId());
		}
	}
}
