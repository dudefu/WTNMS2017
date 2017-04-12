package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex76Codec extends PacketCodec {

	@Override
	protected void encodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		Hex76REQ packet76 = (Hex76REQ) packet;
		scrollBuffer.put2ByteHL(packet76.getPacketCount());
		scrollBuffer.put4ByteLH(packet76.getFileCRC32());
		scrollBuffer.put(packet76.getVersion());
		scrollBuffer.put4ByteHL(packet76.getFileSize());
		scrollBuffer.put2ByteHL(packet76.getInterval());
	}
}
