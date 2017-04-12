package com.jhw.adm.comclient.service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;

import com.jhw.adm.comclient.carrier.BaseService;
import com.jhw.adm.comclient.carrier.protoco.DataPacket;
import com.jhw.adm.comclient.carrier.protoco.Hex76Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex76REQ;
import com.jhw.adm.comclient.carrier.protoco.Hex77Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex77REQ;
import com.jhw.adm.comclient.carrier.protoco.Hex79Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex79RSP;
import com.jhw.adm.comclient.carrier.protoco.PacketCodec;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.carrier.serial.DataAvailableEvent;
import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.carrier.serial.SerialServerListener;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.carriers.CarrierEntity;

/**
 * 
 * @author xiongbo
 * 
 */
public class SystemUpgradeService extends BaseService {
	// private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	//
	private byte[] fileBuffer;
	private long fileCRC32;
	private int available;
	private int packetCount;
	private int interval;
	private File selectedFile;
	private Hex77Codec codec77;
	private MessageSend messageSend;
	private ExecutorService executorService;
	private SystemUpgradeHandler systemUpgradeHandler;
	private SystemHandler systemHandler;

	public SystemUpgradeService() {
		serialServer = SerialServer.getInstance();
		serialServer.addSerialServerListener(new SystemUpgradeListener());
		codec77 = new Hex77Codec();
		executorService = Executors.newSingleThreadExecutor();
	}

	private void stop() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public void upgradeSwitcher(String ip, String client, String clientIp,
			Message message) {
		log.info("Start receive stream1......");
		systemUpgradeHandler.setSystemHandler(systemHandler);
		systemUpgradeHandler.setMessageSend(messageSend);
		systemUpgradeHandler.upgradeSwitcher(ip, client, clientIp, message);
	}

	public void upgradeCarrier(String client, String clientIp, Message message) {

		// interval = (int) (Double.parseDouble(intervalField.getText()) *
		// 1000);
		int destId = SCode.BroadcastId;
		int fileSize = fileBuffer.length;
		// final SerialServer serialServer = SerialServer.getInstance();

		Hex76REQ req76 = new Hex76REQ();
		req76.setCommandCode(SCode.UPLOAD_START);
		req76.setFileCRC32(fileCRC32);
		req76.setFileSize(fileSize);
		// req76.setVersion(Integer.parseInt(versionField.getText()));
		req76.setPacketCount(packetCount);
		req76.setInterval(interval);
		req76.setDestId(destId);
		req76.setSrcId(SCode.nmsId);
		Hex76Codec codec76 = new Hex76Codec();

		serialServer.sendAsync(codec76.encode(req76));

		final Hex77REQ req77 = new Hex77REQ();
		req77.setFileBuffer(fileBuffer);
		req77.setPacketCount(packetCount);
		req77.setDestId(destId);
		req77.setSrcId(SCode.nmsId);
		req77.setCommandCode(SCode.UPLOAD);

		for (int index = 0; index < req77.getPacketCount(); index++) {
			send77REQAsync(req77, index);
		}

		final PacketCodec codec28 = new PacketCodec();
		final DataPacket req28 = new DataPacket();
		req28.setDestId(destId);
		req28.setSrcId(SCode.nmsId);
		req28.setCommandCode(SCode.UPLOAD_END);

		Thread async28 = new Thread() {
			@Override
			public void run() {
				serialServer.sendAsync(codec28.encode(req28));
			}
		};
		executorService.execute(async28);
	}

	private void send77REQAsync(final Hex77REQ req77, final int index) {
		Thread async = new Thread() {
			@Override
			public void run() {
				req77.setPacketIndex(index);
				send77REQ(req77, index);
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		executorService.execute(async);
	}

	private void send77REQ(final Hex77REQ req77, final int index) {
		serialServer.sendData(codec77.encode(req77));
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		// insertLine(String.format("已发送包号: %s；大小： %s；", index,
		// req77.calcPacketLen()));
		// }
		// });

		// Send to Server
	}

	private class SystemUpgradeListener extends SerialServerListener {
		@Override
		public void dataAvailable(final DataAvailableEvent event) {
			Hex79Codec codec = new Hex79Codec();
			Hex79RSP hex79rsp = new Hex79RSP();
			codec.decode(event.getData(), hex79rsp);
			int srcId = hex79rsp.getSrcId();

			if (hex79rsp.getCommandCode() != Hex79Codec.HANDLE_COMMAND)
				return;

			int packetIndex = hex79rsp.getPacketIndex();

			if (packetIndex > packetCount) {
				// insertLine(String.format("请求包号(%s)大于总包数(%s)；", packetIndex,
				// packetCount));

				// Maybe send to Server
			} else {
				Hex77REQ req77 = new Hex77REQ();
				req77.setFileBuffer(fileBuffer);
				req77.setPacketCount(packetCount);
				int destId = srcId;
				req77.setDestId(destId);
				req77.setSrcId(SCode.nmsId);
				req77.setCommandCode(SCode.UPLOAD);
				Hex77Codec codec77 = new Hex77Codec();

				req77.setPacketIndex(packetIndex);

				serialServer.sendAsync(codec77.encode(req77));

				// insertLine(String.format("已发送包号: %s；大小： %s；", packetIndex,
				// req77.calcPacketLen()));

				// May be Send to Server
			}
		}
	}

	private void getFileStream(Message message) {
		StreamMessage sm = (StreamMessage) message;

		ObjectMessage om = (ObjectMessage) message;
		CarrierEntity carrierEntity = null;
		try {
			carrierEntity = (CarrierEntity) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			// return null;
		}
		// return carrierEntity;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public SystemUpgradeHandler getSystemUpgradeHandler() {
		return systemUpgradeHandler;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	public void setSystemUpgradeHandler(
			SystemUpgradeHandler systemUpgradeHandler) {
		this.systemUpgradeHandler = systemUpgradeHandler;
	}

}
