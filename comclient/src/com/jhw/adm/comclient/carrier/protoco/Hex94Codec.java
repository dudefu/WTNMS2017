package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex94Codec extends PacketCodec {

	@Override
	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		Hex94REQ packet94 = (Hex94REQ) packet;

		scrollBuffer.put(packet94.getPortCount());

		for (PortInfo port : packet94.getPorts()) {
			scrollBuffer.put(port.getNumber());
			scrollBuffer.put(port.getCategory());
			scrollBuffer.put(port.getBaudRate());
			scrollBuffer.put(port.getParity());
			scrollBuffer.put(port.getDataBits());
			scrollBuffer.put(port.getStopBits());
			scrollBuffer.put(port.getSubnetCode());
		}
	}
}