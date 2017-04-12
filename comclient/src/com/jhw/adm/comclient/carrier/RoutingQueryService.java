package com.jhw.adm.comclient.carrier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.DataPacket;
import com.jhw.adm.comclient.carrier.protoco.Hex59Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex59RSP;
import com.jhw.adm.comclient.carrier.protoco.PacketCodec;
import com.jhw.adm.comclient.carrier.protoco.Route;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.carrier.serial.DataAvailableEvent;
import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.carrier.serial.SerialServerListener;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class RoutingQueryService extends BaseService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	private MessageSend messageSend;

	public RoutingQueryService() {
		serialServer = SerialServer.getInstance();
		serialServer.addSerialServerListener(new RouterQueryListener());
	}

	public void queryRouting(String client, String clientIp, Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		String plcId = getPlcId(message);
		if (plcId != null) {
			PacketCodec codec59 = new PacketCodec();
			DataPacket hex59req = new DataPacket();
			hex59req.setDestId(Integer.parseInt(plcId));
			hex59req.setSrcId(this.getSrcId());
			hex59req.setCommandCode(SCode.PLC_GET_ROUTE);

			byte[] data = codec59.encode(hex59req);

			correspondClientIp(client, clientIp, data);
			// if (serialServer.isCurrentlyOwned()) {
			serialServer.sendAsync(data);
			// } else {
			// Not Available
			// }
		}
	}

	class RouterQueryListener extends SerialServerListener {
		@Override
		public void dataAvailable(final DataAvailableEvent event) {
			Hex59Codec codec = new Hex59Codec();
			Hex59RSP hex59rsp = new Hex59RSP();
			codec.decode(event.getData(), hex59rsp);
			if (hex59rsp.getCommandCode() == Hex59Codec.HANDLE_COMMAND) {
				int srcId = hex59rsp.getSrcId();
				// TODO
				int seqNum = hex59rsp.getSeqNum();
				Object obj = BaseOperateService.plcIpTable.get(seqNum);

				CarrierEntity carrierEntity = new CarrierEntity();
				carrierEntity.setVersion(hex59rsp.getRouteVersion());
				Set<CarrierRouteEntity> carrierRouteEntitys = new HashSet<CarrierRouteEntity>();
				List<Route> routes = hex59rsp.getRouters();
				if (routes != null) {
					for (Route route : routes) {
						CarrierRouteEntity carrierRouteEntity = new CarrierRouteEntity();
						carrierRouteEntity.setCarrierCode(route.getId());
						carrierRouteEntity.setPort(route.getPort());
						carrierRouteEntitys.add(carrierRouteEntity);
					}
				}
				carrierEntity.setRoutes(carrierRouteEntitys);

				if (obj != null) {
					Client client = (Client) obj;
					messageSend.sendObjectMessageRes(0, carrierEntity, "",
							MessageNoConstants.CARRIERROUTEQUERYREP,
							srcId + "", client.getClient(), client.getIp());
					BaseOperateService.plcIpTable.remove(seqNum);
				} else {
					messageSend.sendObjectMessageRes(0, carrierEntity, "",
							MessageNoConstants.CARRIERROUTEQUERYREP,
							srcId + "", "", "");
				}
			}
		}
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
