package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex59Codec extends PacketCodec {

	@Override
	protected void decodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		if (packet.getCommandCode() != HANDLE_COMMAND)
			return;

		int routeVersion = scrollBuffer.get2ByteHL();
		int routeCount = scrollBuffer.getInt();
		Hex59RSP hex59 = (Hex59RSP) packet;
		hex59.setRouteVersion(routeVersion);

		for (int i = 0; i < routeCount; i++) {
			Route router = new Route();
			router.setPort(scrollBuffer.getInt());
			router.setId(scrollBuffer.get3ByteHL());
			hex59.addRoute(router);
		}
	}

	public static final int HANDLE_COMMAND = 0x59;
}