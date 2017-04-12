package com.jhw.adm.comclient.carrier;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.DataPacket;
import com.jhw.adm.comclient.carrier.protoco.Hex93Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex93REQ;
import com.jhw.adm.comclient.carrier.protoco.Hex95Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex95RSP;
import com.jhw.adm.comclient.carrier.protoco.PacketCodec;
import com.jhw.adm.comclient.carrier.protoco.PacketFormatter;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.carrier.serial.DataAvailableEvent;
import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.carrier.serial.SerialServerListener;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class WaveBandService extends BaseService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	private MessageSend messageSend;

	public WaveBandService() {
		serialServer = SerialServer.getInstance();
		serialServer.addSerialServerListener(new WaveBandListener());
	}

	public void queryWaveBand(String plcId0, String client, String clientIp,
			Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		String plcId = getPlcId(message);
		if (plcId != null) {
			PacketCodec codec95 = new PacketCodec();
			DataPacket hex95req = new DataPacket();
			hex95req.setDestId(Integer.parseInt(plcId));
			hex95req.setSrcId(this.getSrcId());
			hex95req.setCommandCode(SCode.PLC_GET_WAVE_BAND);

			byte[] data = codec95.encode(hex95req);

			correspondClientIp(client, clientIp, data);

			// if (serialServer.isCurrentlyOwned()) {
			serialServer.sendAsync(data);
			// } else {
			// Not Available
			// }
		}
	}

	public void setWaveBand(String plcId, String client, String clientIp,
			Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		CarrierEntity carrierEntity = getCarrierEntity(message);
		if (carrierEntity != null) {

			Hex93Codec codec93 = new Hex93Codec();
			Hex93REQ hex93req = new Hex93REQ();
			hex93req.setDestId(Integer.parseInt(plcId));
			hex93req.setSrcId(this.getSrcId());
			hex93req.setCommandCode(SCode.PLC_SET_WAVE_BAND);
			hex93req.setWaveBand1(carrierEntity.getWaveBand1());
			hex93req.setWaveBand2(carrierEntity.getWaveBand2());
			hex93req.setTimeout1(carrierEntity.getTimeout1());

			byte[] data = codec93.encode(hex93req);

			correspondClientIp(client, clientIp, data);

			// if (serialServer.isCurrentlyOwned()) {
			serialServer.sendAsync(data);
			// } else {
			// Not Available
			// }
		}
	}

	class WaveBandListener extends SerialServerListener {
		@Override
		public void dataAvailable(final DataAvailableEvent event) {
			PacketFormatter formatter = new PacketFormatter();
			DataPacket dataPacket = new DataPacket();
			formatter.format(event.getData(), dataPacket);
			if (dataPacket.getCommandCode() == Hex95Codec.HANDLE_COMMAND) {
				int srcId = dataPacket.getSrcId();
				Hex95Codec codec = new Hex95Codec();
				Hex95RSP hex95 = new Hex95RSP();
				codec.decode(event.getData(), hex95);

				// Send to Server
				log.info(dataPacket.getSeqNum() + " " + hex95.getSeqNum());
				// TODO
				int seqNum = hex95.getSeqNum();
				Object obj = BaseOperateService.plcIpTable.get(seqNum);

				// Modify:2010-9-1
				// if (obj != null) {
				// Client client = (Client) obj;
				// messageSend.sendTextMessageRes(srcId + " Operate", hex95
				// .getWaveBand()
				// + "", MessageNoConstants.CARRIERWAVEBANDQUERYREP,
				// srcId + "", client.getClient(), client.getIp());
				// BaseOperateService.plcIpTable.remove(seqNum);
				// } else {
				// messageSend.sendTextMessageRes(srcId + " Operate", hex95
				// .getWaveBand()
				// + "", MessageNoConstants.CARRIERWAVEBANDQUERYREP,
				// srcId + "", "", "");
				// }

				CarrierEntity carrierEntity = new CarrierEntity();
				carrierEntity.setCarrierCode(srcId);
				carrierEntity.setWaveBand1(hex95.getWaveBand1());
				carrierEntity.setWaveBand2(hex95.getWaveBand2());
				carrierEntity.setTimeout1(hex95.getTimeout1());

				// TODO
				if (obj != null) {
					Client client = (Client) obj;
					messageSend.sendObjectMessageRes(0, carrierEntity, "",
							MessageNoConstants.CARRIERWAVEBANDQUERYREP, srcId
									+ "", client.getClient(), client.getIp());
					BaseOperateService.plcIpTable.remove(seqNum);
				} else {
					messageSend.sendObjectMessageRes(0, carrierEntity, "",
							MessageNoConstants.CARRIERWAVEBANDQUERYREP, srcId
									+ "", "", "");
				}

				// messageSend.sendTextMessageRes(srcId + " Operate", hex95
				// .getWaveBand1()
				// + "", MessageNoConstants.CARRIERWAVEBANDQUERYREP, srcId
				// + "", "", "");
			}
		}
	}

	private CarrierEntity getCarrierEntity(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		CarrierEntity carrierEntity = null;
		try {
			carrierEntity = (CarrierEntity) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return carrierEntity;
	}

	private String getPlcId(Message message) {
		String waveBand = null;
		try {
			TextMessage tm = (TextMessage) message;
			waveBand = tm.getText();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return waveBand;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
