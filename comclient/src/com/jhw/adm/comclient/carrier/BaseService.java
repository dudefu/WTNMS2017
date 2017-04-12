package com.jhw.adm.comclient.carrier;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.PacketFormatter;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.carriers.CarrierEntity;

/**
 * 
 * @author xiongbo
 * 
 */
public class BaseService {
	public final Logger log = Logger.getLogger(this.getClass().getName());
	//
	public final String SUCCESS = "S";
	public final String FAIL = "F";
	//
	public final String INSERT = "I";
	public final String UPDATE = "U";
	public final String DELETE = "D";

	public int srcId = SCode.nmsId;

	private MessageSend messageSend;

	public String getTrace(Exception e) {
		StackTraceElement[] ste = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(e + "\n");
		for (int i = 0; i < ste.length; i++) {
			sb.append(ste[i].toString() + "\n");
		}
		return sb.toString();
	}

	public String getTraceMessage(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		e.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	// public Vector<VariableBinding> checkResponseVar(PDU response) {
	// if (response == null) {
	// return null;
	// }
	// if (SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
	// Vector<VariableBinding> responseVar = response
	// .getVariableBindings();
	// return responseVar;
	// }
	// return null;
	// }

	public class Client {
		private String ip;
		private String client;

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}
	}

	protected void correspondClientIp(String client, String clientIp,
			byte[] data) {
		byte seqNum = data[PacketFormatter.SEQ_NUM_INDEX];
		// log.info((seqNum & 0xFF));
		Client clientc = new Client();
		clientc.setClient(client);
		clientc.setIp(clientIp);
		BaseOperateService.plcIpTable.put((seqNum & 0xFF), clientc);
	}

	/**
	 * 当从服务端查询到的中心载波个数不为1时， 前置机不发消息到载波机
	 * 
	 * @return
	 */
	public boolean isValid_SrcID(MessageSend messageSend) {
		this.messageSend = messageSend;
		boolean bool = true;
		List<CarrierEntity> carrierEntityList = messageSend
				.getServiceBeanRemote().queryCarrierEntity();
		if (null == carrierEntityList) {
			bool = false;
			log.info("***************Error：Center Carrier is null！ ");
		} else if (carrierEntityList.size() != 1) {
			bool = false;
			log.info("***************Error：Center Carrier's number != 1！ ");
		} else {
			// 源ID由默认的1改为中心载波的code
			srcId = carrierEntityList.get(0).getCarrierCode();
			log.info("***************Center Carrier's code = " + srcId);
		}
		return bool;
	}

	public int getSrcId() {
		return srcId;
	}
}
