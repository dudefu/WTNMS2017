package com.jhw.adm.comclient.carrier;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.DataPacket;
import com.jhw.adm.comclient.carrier.protoco.Hex05Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex05RSP;
import com.jhw.adm.comclient.carrier.protoco.Hex97Codec;
import com.jhw.adm.comclient.carrier.protoco.Hex97REQ;
import com.jhw.adm.comclient.carrier.protoco.PacketCodec;
import com.jhw.adm.comclient.carrier.protoco.PacketFormatter;
import com.jhw.adm.comclient.carrier.protoco.SCode;
import com.jhw.adm.comclient.carrier.serial.DataAvailableEvent;
import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.carrier.serial.SerialServerListener;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.util.PropertyFileUtil;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.MonitorConfigEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * Carrier Test
 * 
 * Carrier Restart
 * 
 * @author xiongbo
 * 
 */
public class BaseOperateService extends BaseService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	private Timer plcTimer;
	private int period;
	private int delay;
	private int timeout;
	private DoTask doTask;
	private Map<Integer, Boolean> plcStateTable;
	private MessageSend messageSend;
	public static Map<Integer, Client> plcIpTable = new Hashtable<Integer, Client>();
	private ExecutorService executorService;

	public BaseOperateService() {
		serialServer = SerialServer.getInstance();
		serialServer.addSerialServerListener(new BaseOperateListener());
		plcStateTable = new Hashtable<Integer, Boolean>();
		// TODO maybe it be moved to method start()
		plcTimer = new Timer("PLCMonitor", true);
		executorService = Executors.newSingleThreadExecutor();
	}

	private void readMonitorSet() {
		List datas = messageSend.getServiceBeanRemote().findAll(
				MonitorConfigEntity.class);
		if (datas != null && datas.size() > 0) {
			MonitorConfigEntity monitorConfigEntity = (MonitorConfigEntity) datas
					.get(0);
			period = monitorConfigEntity.getFrequence();
			delay = monitorConfigEntity.getDistance();
			timeout = monitorConfigEntity.getOuttime();
		} else {
			period = AutoMonitorSetService.FREQUENCY;
			delay = AutoMonitorSetService.INTERVAL;
			timeout = AutoMonitorSetService.TIMEOT;
		}

		period = period * 1000 * 60;
		timeout = timeout * 1000;
	}

	private void openDefaultSerialPort() {
		String[] ports = SerialServer.getPortIdentifiers();
		if (!serialServer.isCurrentlyOwned() && ports.length > 0) {
			// serialPortName = ports[0];
			serialServer.open(ports[0]);
		} else if (!serialServer.isCurrentlyOwned()) {
			// startSerialPort(ports);
		}
	}

	private boolean openSerialPort() {
		boolean openStatue = false;

		String[] ports = SerialServer.getPortIdentifiers();
		if (!serialServer.isCurrentlyOwned() && ports.length > 0) {
			openStatue = startSerialPort(ports);
		}
		return openStatue;
	}

	private boolean startSerialPort(String[] ports) {
		boolean openStatue = false;

		Properties serialProperties = PropertyFileUtil
				.load(SerialSetService.CONFIG_FILE);
		if (serialProperties == null) {
			serialProperties = new Properties();
			// TODO
			serialProperties.setProperty(SerialSetService.SERIAL_PORT,
					SerialSetService.PORT);
			serialProperties.setProperty(SerialSetService.BAUD_RATE, Integer
					.toString(SerialSetService.BAUD));
			serialProperties.setProperty(SerialSetService.DATA_BIT, Integer
					.toString(SerialSetService.DATA));
			serialProperties.setProperty(SerialSetService.STOP_BIT, Integer
					.toString(SerialSetService.STOP));
			serialProperties.setProperty(SerialSetService.TEST_BIT, Integer
					.toString(SerialSetService.TEST));
			serialProperties.setProperty(SerialSetService.BUFFER_SIZE, Integer
					.toString(SerialSetService.BUFFER));
			PropertyFileUtil.save(serialProperties,
					SerialSetService.CONFIG_FILE, "serial port config");
		}
		serialServer.setBaudRate(Integer.parseInt(serialProperties
				.getProperty(SerialSetService.BAUD_RATE)));
		serialServer.setDataBits(Integer.parseInt(serialProperties
				.getProperty(SerialSetService.DATA_BIT)));
		serialServer.setStopBits(Integer.parseInt(serialProperties
				.getProperty(SerialSetService.STOP_BIT)));
		serialServer.setParity(Integer.parseInt(serialProperties
				.getProperty(SerialSetService.TEST_BIT)));
		serialServer.setBufferSize(Integer.parseInt(serialProperties
				.getProperty(SerialSetService.BUFFER_SIZE)));

		// Modify 9.11
		openStatue = serialServer.open(serialProperties
				.getProperty(SerialSetService.SERIAL_PORT));
		if (!openStatue) {
			openStatue = serialServer.open(ports[0]);
		}

		serialProperties.clear();

		return openStatue;
	}

	/**
	 * Monitor All PLC State
	 * 
	 * 1.When System setup
	 * 
	 * 2.In 'SerialSet', when open
	 */
	public void start() {
		// Modify 6-9
		if (openSerialPort()) {
			List datas = messageSend.getServiceBeanRemote().findAll(
					CarrierEntity.class);
			if (datas != null) {
				int size = datas.size();
				if (size > 0) {
					List<Integer> plcs = new ArrayList<Integer>();
					for (int i = 0; i < size; i++) {
						CarrierEntity carrierEntity = (CarrierEntity) datas
								.get(i);
						plcs.add(carrierEntity.getCarrierCode());
					}
					readMonitorSet();

					// openSerialPort();

					// Sometime be remarked
					doTask = new DoTask(plcs);
					plcTimer.schedule(doTask, 500, period);
				}
			}
		}
	}

	class DoTask extends TimerTask {
		private List<Integer> plcs;
		private boolean flag = true;

		public DoTask(List<Integer> plcs) {
			this.plcs = plcs;
		}

		@Override
		public void run() {
			try {
				for (int plc : plcs) {
					if (flag) {
						test(null, null, plc, timeout);
						Thread.sleep(delay);
					} else {
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}

	/**
	 * 1.When System shutdown
	 * 
	 * 2.In 'SerialSet', when close
	 */
	public void stop() {
		// Timer
		if (doTask != null) {
			doTask.setFlag(false);
		}

		plcTimer.cancel();
		plcTimer.purge();
		plcStateTable.clear();
		// Serial
		serialServer.close();
		//
		plcIpTable.clear();
		//
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public void test(String client, String clientIp, int plcId, int timeout) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (bool) {
			PacketCodec codec90 = new PacketCodec();
			DataPacket hex90req = new DataPacket();
			hex90req.setDestId(plcId);
			hex90req.setSrcId(this.getSrcId());
			hex90req.setCommandCode(SCode.PLC_GET_STATE);

			byte[] data = codec90.encode(hex90req);
			byte seqNum = data[PacketFormatter.SEQ_NUM_INDEX];
			if (clientIp != null) {
				correspondClientIp(client, clientIp, data);
			}
			serialServer.sendAsync(data);
		}
		// Set default value:false
		plcStateTable.put(plcId, false);

		CheckThreeThread checkThreeThread = new CheckThreeThread(plcId, timeout);
		executorService.execute(checkThreeThread);
	}

	class CheckThreeThread extends Thread {
		private int timeout;
		private final int COUNT = 3;
		private int plcId;

		public CheckThreeThread(int plcId, int timeout) {
			this.plcId = plcId;
			this.timeout = timeout;
		}

		@Override
		public void run() {
			try {
				boolean bool = isValid_SrcID(messageSend);
				// 取中心载波的编号，如果没有取到，就不下发给载波机
				if (!bool) {
					// 如果没有取到中心载波机编号，直接设置为离线状态
					messageSend.sendTextMessageRes(plcId + " PLC Fail", FAIL,
							MessageNoConstants.CARRIERMONITORREP, plcId + "",
							"", "");
					return;
				}

				for (int number = 0; number < COUNT; number++) {
					Thread.sleep(timeout);
					boolean result = plcStateTable.get(plcId);
					if (!result) {
						PacketCodec codec90 = new PacketCodec();
						DataPacket hex90req = new DataPacket();
						hex90req.setDestId(plcId);
						hex90req.setSrcId(getSrcId());
						hex90req.setCommandCode(SCode.PLC_GET_STATE);

						byte[] data = codec90.encode(hex90req);
						serialServer.sendAsync(data);
					} else {
						break;
					}
				}
				// 如果重试三次，还没有在线，设置为离线状态.Maybe it doesn't return
				boolean result = plcStateTable.get(plcId);
				if (!result) {
					messageSend.sendTextMessageRes(plcId + " PLC Fail", FAIL,
							MessageNoConstants.CARRIERMONITORREP, plcId + "",
							"", "");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void testPLC(String client, String clientIp, Message message) {
		String plcId = getPlcId(message);
		if (plcId != null) {
			test(client, clientIp, Integer.parseInt(plcId), timeout);
		}
	}

	public void restartPLC(String client, String clientIp, Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		String plcId = getPlcId(message);
		if (plcId != null) {
			PacketCodec codec91 = new PacketCodec();
			DataPacket hex91req = new DataPacket();
			hex91req.setDestId(Integer.parseInt(plcId));
			hex91req.setSrcId(this.getSrcId());
			hex91req.setCommandCode(SCode.PLC_START);

			byte[] data = codec91.encode(hex91req);

			correspondClientIp(client, clientIp, data);

			serialServer.sendAsync(data);
		}

	}

	public void configPLCMarking(String plcId, String client, String clientIp,
			Message message) {
		boolean bool = isValid_SrcID(messageSend);
		// 取中心载波的编号，如果没有取到，就不下发给载波机
		if (!bool) {
			return;
		}

		CarrierEntity carrierEntity = getCarrierEntity(message);
		if (carrierEntity != null) {

			Hex97Codec codec97 = new Hex97Codec();
			Hex97REQ hex97req = new Hex97REQ();
			hex97req.setDestId(Integer.parseInt(plcId));
			hex97req.setSrcId(this.getSrcId());
			hex97req.setCommandCode(SCode.PLC_SET_DEVICE_TYPE);
			hex97req.setDeviceType(carrierEntity.getMarking());

			byte[] data = codec97.encode(hex97req);

			correspondClientIp(client, clientIp, data);

			serialServer.sendAsync(data);
		}
	}

	private class BaseOperateListener extends SerialServerListener {
		@Override
		public void dataAvailable(final DataAvailableEvent event) {
			PacketFormatter formatter = new PacketFormatter();
			DataPacket dataPacket = new DataPacket();
			formatter.format(event.getData(), dataPacket);

			int srcId = dataPacket.getSrcId();
			// Response Thread CheckThreeThread
			plcStateTable.put(srcId, true);

			if (dataPacket.getCommandCode() == Hex05Codec.SUCCESS
					|| dataPacket.getCommandCode() == Hex05Codec.FAIL) {

				Hex05Codec codec = new Hex05Codec();
				Hex05RSP hex05 = codec.decode(event.getData());

				// int seqNum = dataPacket.getSeqNum();
				int seqNum = hex05.getReturnSeqNum();
				Object obj = plcIpTable.get(seqNum);
				Client client = null;
				if (obj != null) {
					client = (Client) obj;
				}

				int returnCommand = hex05.getReturnCommand();
				if (returnCommand != (SCode.PLC_GET_STATE & 0xFF)) {

					String whichCommand = getReturnCommand(returnCommand);
					String yesOrNo = getReturnCommand(dataPacket
							.getCommandCode());

					if (client != null) {
						messageSend.sendTextMessageRes(srcId + " Operate",
								yesOrNo, Integer.parseInt(whichCommand), srcId
										+ "", client.getClient(), client
										.getIp());
						plcIpTable.remove(seqNum);
					} else {
						messageSend.sendTextMessageRes(srcId + " Operate",
								yesOrNo, Integer.parseInt(whichCommand), srcId
										+ "", "", "");
					}
				} else {
					if (client != null) {
						messageSend.sendTextMessageRes(srcId + " PLC Normal",
								SUCCESS, MessageNoConstants.CARRIERMONITORREP,
								srcId + "", client.getClient(), client.getIp());
						plcIpTable.remove(seqNum);
					} else {
						messageSend.sendTextMessageRes(srcId + " PLC Normal",
								SUCCESS, MessageNoConstants.CARRIERMONITORREP,
								srcId + "", "", "");
					}
				}
			}

			// else {
			// if (client != null) {
			// messageSend.sendTextMessageRes(srcId + " PLC Normal",
			// SUCCESS, MessageNoConstants.CARRIERMONITORREP,
			// srcId + "", client.getClient(), client.getIp());
			// plcIpMap.remove(seqNum);
			// } else {
			// messageSend.sendTextMessageRes(srcId + " PLC Normal",
			// SUCCESS, MessageNoConstants.CARRIERMONITORREP,
			// srcId + "", "", "");
			// }
			// }

		}
	}

	private String getReturnCommand(int command) {
		String whichCommand = "0";
		if (command == 0x46) {
			whichCommand = MessageNoConstants.CARRIERROUTECLEARREP + "";
		} else if (command == 0x3B) {
			whichCommand = MessageNoConstants.CARRIERROUTECONFIGREP + "";
		} else if (command == Hex05Codec.FAIL) {
			whichCommand = FAIL;
		} else if (command == Hex05Codec.SUCCESS) {
			whichCommand = SUCCESS;
		} else if (command == (SCode.PLC_START & 0xFF)) {
			whichCommand = MessageNoConstants.CARRIERRESTART + "";
		} else if (command == (SCode.PLC_SET_WAVE_BAND & 0xFF)) {
			whichCommand = MessageNoConstants.CARRIERWAVEBANDCONFIGREP + "";
		} else if (command == (SCode.PLC_SET_PORT_INFO & 0xFF)) {
			whichCommand = MessageNoConstants.CARRIERPORTCONFIGREP + "";
		} else if (command == (SCode.PLC_START & 0xFF)) {
			whichCommand = MessageNoConstants.CARRIERONLINE + "";
		} else if (command == (SCode.PLC_SET_DEVICE_TYPE & 0xFF)) {
			whichCommand = MessageNoConstants.CARRIERMARKINGREP + "";
		}

		return whichCommand;
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

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	// public Map<Integer, Client> getPlcIpMap() {
	// return plcIpMap;
	// }

}
