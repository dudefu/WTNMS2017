package com.jhw.adm.comclient.carrier;

import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.DataPacket;
import com.jhw.adm.comclient.carrier.protoco.Hex3BCodec;
import com.jhw.adm.comclient.carrier.protoco.Hex3BREQ;
import com.jhw.adm.comclient.carrier.protoco.PacketCodec;
import com.jhw.adm.comclient.carrier.protoco.Route;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class RoutingSetService extends BaseService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	private MessageSend messageSend;
	//
	private final int PLC_CONSOLE = 1;

	public RoutingSetService() {
		serialServer = SerialServer.getInstance();
	}

	/**
	 * sync single PLC
	 * 
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void sync(String client, String clientIp, Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		CarrierEntity carrierEntity = getCarrierEntity(message);
		if (carrierEntity != null) {
			Hex3BCodec codec3B = new Hex3BCodec();
			Hex3BREQ hex3Breq = new Hex3BREQ();
			hex3Breq.setDestId(carrierEntity.getCarrierCode());
			hex3Breq.setSrcId(this.getSrcId());
			hex3Breq.setCommandCode(SCode.PLC_SET_ROUTE);

			Set<CarrierRouteEntity> carrierRouteEntitys = carrierEntity
					.getRoutes();
			boolean flag = true;
			for (CarrierRouteEntity carrierRouteEntity : carrierRouteEntitys) {
				Route route = new Route();
				route.setId(carrierRouteEntity.getCarrierCode());
				if (route.getId() == PLC_CONSOLE) {
					flag = true;
				}
				route.setPort(carrierRouteEntity.getPort());
				hex3Breq.addRoute(route);
			}

			// /* 1. 从载波机本机端口到网管的路由 */
			// Route route = new Route();
			// route.setId(SCode.nmsId);
			// route.setPort(localPort);
			// hex3Breq.addRoute(route);
			//
			// for (MObject subObject : listOfSub) {
			// int subId =
			// Integer.parseInt(subObject.get(MField.Name).toString());
			// int subUpPort = Integer.parseInt(subObject.get(MField.UpPort)
			// .toString());
			// /* 2. 从载波机外联端口到下联设备的路由，外联端口由下联设备在上联端口决定 */
			// Route sub = new Route();
			// sub.setId(subId);
			// sub.setPort(subUpPort);
			// hex3Breq.addRoute(sub);
			// }
			//
			// if (upId != SCode.nmsId) {
			// /* 3. 从载波机本机端口到上联设备的路由 */
			// Route upRoute = new Route();
			// upRoute.setId(upId);
			// upRoute.setPort(localPort);
			// hex3Breq.addRoute(upRoute);
			// }
			//
			// /* 4. 手动添加的路由 */
			// for (Route r : routeTableModel.getData()) {
			// hex3Breq.addRoute(r);
			// }
			if (flag) {
				byte[] data = codec3B.encode(hex3Breq);

				correspondClientIp(client, clientIp, data);

				serialServer.sendAsync(data);
			}
		}
	}

	/**
	 * sync all PLC
	 * 
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void syncAll(String client, String clientIp, Message message) {
		List datas = messageSend.getServiceBeanRemote().findAll(
				CarrierEntity.class);

		messageSend.sendTextMessageRes("syn all", SUCCESS,
				MessageNoConstants.CARRIERROUTEFINISH, "", client, clientIp);
	}

	public void syncRoutings(int plcId, int upId, int localPort) {
		sendHex46REQ(plcId);

		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		Hex3BCodec codec3B = new Hex3BCodec();
		Hex3BREQ hex3Breq = new Hex3BREQ();
		hex3Breq.setDestId(plcId);
		hex3Breq.setSrcId(this.getSrcId());
		hex3Breq.setCommandCode(SCode.PLC_SET_ROUTE);

		/* 1. 从载波机本机端口到网管的路由 */
		Route route = new Route();
		route.setId(SCode.nmsId);
		route.setPort(localPort);
		hex3Breq.addRoute(route);

		// for (MObject subObject : listOfSub) {
		// int subId = Integer.parseInt(subObject.get(MField.Name).toString());
		// int subUpPort = Integer.parseInt(subObject.get(MField.UpPort)
		// .toString());
		// /* 2. 从载波机外联端口到下联设备的路由，外联端口由下联设备在上联端口决定 */
		// Route sub = new Route();
		// sub.setId(subId);
		// sub.setPort(subUpPort);
		// hex3Breq.addRoute(sub);
		// }

		if (upId != SCode.nmsId) {
			/* 3. 从载波机本机端口到上联设备的路由，主载波已经默认连网管 */
			Route upRoute = new Route();
			upRoute.setId(upId);
			upRoute.setPort(localPort);
			hex3Breq.addRoute(upRoute);
		}

		serialServer.sendAsync(codec3B.encode(hex3Breq));

		// for (MObject subObject : listOfSub) {
		// int subId = Integer.parseInt(subObject.get(MField.Name).toString());
		// int subUpId = Integer.parseInt(subObject.get(MField.UpID)
		// .toString());
		// int subLocalPort = Integer.parseInt(subObject.get(MField.LocalPort)
		// .toString());
		// /* 4. 设置下联设备 */
		// syncRoutings(subId, subUpId, subLocalPort);
		// }
	}

	// TODO
	private void sendHex46REQ(int destId) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		PacketCodec codec46 = new PacketCodec();
		DataPacket hex46req = new DataPacket();
		hex46req.setDestId(destId);
		hex46req.setSrcId(this.getSrcId());
		hex46req.setCommandCode(SCode.PLC_SET_ROUTCLEAR);
		serialServer.sendAsync(codec46.encode(hex46req));
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

	public void clearRouting(String client, String clientIp, Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		String plcId = getPlcId(message);
		if (plcId != null) {

			PacketCodec codec46 = new PacketCodec();
			DataPacket hex46req = new DataPacket();
			hex46req.setDestId(Integer.parseInt(plcId));
			hex46req.setSrcId(this.getSrcId());
			hex46req.setCommandCode(SCode.PLC_SET_ROUTCLEAR);

			byte[] data = codec46.encode(hex46req);

			correspondClientIp(client, clientIp, data);

			serialServer.sendAsync(data);
		}
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
