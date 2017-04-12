package com.jhw.adm.comclient.carrier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.DataPacket;
import com.jhw.adm.comclient.carrier.protoco.Hex92Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex92RSP;
import com.jhw.adm.comclient.carrier.protoco.Hex94Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex94REQ;
import com.jhw.adm.comclient.carrier.protoco.PacketCodec;
import com.jhw.adm.comclient.carrier.protoco.PortInfo;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.carrier.serial.DataAvailableEvent;
import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.carrier.serial.SerialServerListener;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierPortEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class PortQueryService extends BaseService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	private MessageSend messageSend;

	public PortQueryService() {
		serialServer = SerialServer.getInstance();
		serialServer.addSerialServerListener(new PortQueryListener());
	}

	public void queryPort(String client, String clientIp, Message message) {
		boolean bool = isValid_SrcID(this.messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		String plcId = getPlcId(message);
		if (plcId != null) {
			PacketCodec codec92 = new PacketCodec();
			DataPacket hex92req = new DataPacket();
			hex92req.setDestId(Integer.parseInt(plcId));
			hex92req.setSrcId(this.getSrcId());
			hex92req.setCommandCode(SCode.PLC_GET_PORT_INFO);

			byte[] data = codec92.encode(hex92req);

			correspondClientIp(client, clientIp, data);

			serialServer.sendAsync(data);
		}
	}

	public void setPort(String client, String clientIp, Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		CarrierEntity carrierEntity = getCarrierEntity(message);
		if (carrierEntity != null) {
			Hex94Codec codec94 = new Hex94Codec();
			Hex94REQ hex94req = new Hex94REQ();
			hex94req.setDestId(carrierEntity.getCarrierCode());
			hex94req.setSrcId(this.getSrcId());
			hex94req.setCommandCode(SCode.PLC_SET_PORT_INFO);

			Set<CarrierPortEntity> carrierPortEntitys = carrierEntity
					.getPorts();
			Set<Integer> sortSet = new TreeSet<Integer>();
			Map<Integer, CarrierPortEntity> seqMap = new HashMap<Integer, CarrierPortEntity>();
			for (CarrierPortEntity carrierPortEntity : carrierPortEntitys) {
				sortSet.add(carrierPortEntity.getPortCode());
				seqMap.put(carrierPortEntity.getPortCode(), carrierPortEntity);
			}

			for (int port : sortSet) {
				PortInfo portInfo = new PortInfo();
				portInfo.setNumber(seqMap.get(port).getPortCode());
				portInfo.setCategory(seqMap.get(port).getPortType());
				portInfo.setBaudRate(seqMap.get(port).getBaudRate());
				portInfo.setParity(seqMap.get(port).getVerify());
				portInfo.setDataBits(seqMap.get(port).getDataBit());
				portInfo.setStopBits(seqMap.get(port).getStopBit());

				log.info(portInfo.getNumber() + " " + portInfo.getCategory()
						+ " " + portInfo.getBaudRate() + " "
						+ portInfo.getParity() + " " + portInfo.getDataBits()
						+ " " + portInfo.getStopBits());
				hex94req.addPort(portInfo);
			}

			// for (CarrierPortEntity carrierPortEntity : carrierPortEntitys) {
			// PortInfo portInfo = new PortInfo();
			// portInfo.setNumber(carrierPortEntity.getPortCode());
			// portInfo.setCategory(carrierPortEntity.getPortType());
			// portInfo.setBaudRate(carrierPortEntity.getBaudRate());
			// log.info(carrierPortEntity.getPortCode() + " "
			// + carrierPortEntity.getPortType() + " "
			// + carrierPortEntity.getBaudRate());
			// hex94req.addPort(portInfo);
			// }

			// for (PortInfo portInfo : portInfoTableModel.getData()) {
			// hex94req.addPort(portInfo);
			// }

			byte[] data = codec94.encode(hex94req);

			correspondClientIp(client, clientIp, data);

			serialServer.sendAsync(data);
		}
	}

	class PortQueryListener extends SerialServerListener {
		@Override
		public void dataAvailable(final DataAvailableEvent event) {
			Hex92Codec codec = new Hex92Codec();
			Hex92RSP hex92rsp = new Hex92RSP();
			codec.decode(event.getData(), hex92rsp);
			if (hex92rsp.getCommandCode() == Hex92Codec.HANDLE_COMMAND) {
				int srcId = hex92rsp.getSrcId();
				CarrierEntity carrierEntity = new CarrierEntity();
				Set<CarrierPortEntity> carrierPortEntitys = new HashSet<CarrierPortEntity>();
				List<PortInfo> portInfos = hex92rsp.getPortInfoList();
				if (portInfos != null) {
					for (PortInfo portInfo : portInfos) {
						CarrierPortEntity carrierPortEntity = new CarrierPortEntity();
						carrierPortEntity.setPortCode(portInfo.getNumber());
						carrierPortEntity.setPortType(portInfo.getCategory());
						carrierPortEntity.setBaudRate(portInfo.getBaudRate());
						carrierPortEntity.setVerify(portInfo.getParity());
						carrierPortEntity.setDataBit(portInfo.getDataBits());
						carrierPortEntity.setStopBit(portInfo.getStopBits());
						carrierPortEntitys.add(carrierPortEntity);
					}
				}
				carrierEntity.setPorts(carrierPortEntitys);
				carrierEntity.setCarrierCode(srcId);

				// TODO
				int seqNum = hex92rsp.getSeqNum();
				Object obj = BaseOperateService.plcIpTable.get(seqNum);
				if (obj != null) {
					Client client = (Client) obj;
					messageSend.sendObjectMessageRes(0, carrierEntity, "",
							MessageNoConstants.CARRIERPORTQUERYREP, srcId + "",
							client.getClient(), client.getIp());
					BaseOperateService.plcIpTable.remove(seqNum);
				} else {
					messageSend.sendObjectMessageRes(0, carrierEntity, "",
							MessageNoConstants.CARRIERPORTQUERYREP, srcId + "",
							"", "");
				}
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
		String plcId = null;
		try {
			TextMessage tm = (TextMessage) message;
			plcId = tm.getText();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return plcId;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
