package com.jhw.adm.comclient.carrier.protoco;

import com.jhw.adm.comclient.carrier.serial.ScrollBuffer;

public class Hex92Codec extends PacketCodec {

	@Override
	protected void decodeBody(ScrollBuffer scrollBuffer, DataPacket packet) {
		if (packet.getCommandCode() != HANDLE_COMMAND)
			return;

		// int portCount = scrollBuffer.getInt();
		// Hex92RSP hex92 = (Hex92RSP) packet;
		//
		// for (int i = 0; i < portCount; i++) {
		// PortInfo portInfo = new PortInfo();
		// portInfo.setNumber(scrollBuffer.getInt());
		// portInfo.setCategory(scrollBuffer.getInt());
		// portInfo.setBaudRate(scrollBuffer.getInt());
		// hex92.addPortInfo(portInfo);
		// }

		// //
		if (packet.getCommandCode() != HANDLE_COMMAND)
			return;
		/*
		 * AA AA 00 2B 05 00 00 01 00 00 2A 92 07 00 00 00 00 00 00 01 01 01 03
		 * 03 02 02 02 02 02 02 02 03 03 03 03 03 03 04 04 04 04 04 04 05 04 05
		 * 05 05 05 02 05 06 03 03 02 4C 43 55 B4 16
		 */
		int portCount = scrollBuffer.getInt();
		Hex92RSP hex92 = (Hex92RSP) packet;

		for (int i = 0; i < portCount; i++) {
			PortInfo portInfo = new PortInfo();
			portInfo.setNumber(scrollBuffer.getInt());
			portInfo.setCategory(scrollBuffer.getInt());
			portInfo.setBaudRate(scrollBuffer.getInt());
			portInfo.setParity(scrollBuffer.getInt());
			portInfo.setDataBits(scrollBuffer.getInt());
			portInfo.setStopBits(scrollBuffer.getInt());
			portInfo.setSubnetCode(scrollBuffer.getInt());
			hex92.addPortInfo(portInfo);
		}
	}

	public static final int HANDLE_COMMAND = 0x92;
}