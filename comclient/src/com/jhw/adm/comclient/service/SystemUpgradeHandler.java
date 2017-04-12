package com.jhw.adm.comclient.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.protocol.snmp.TrapMonitor;
import com.jhw.adm.comclient.service.upgrade.SystemRestartInter;
import com.jhw.adm.comclient.service.upgrade.TftpServiceThread;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class SystemUpgradeHandler extends BaseHandler implements
		SystemRestartInter {
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private TftpServiceThread tftpServer = null;
	private TrapMonitor trapMonitor = null;

	private LinkedList<String> messageQueue = new LinkedList<String>();

	private SwitcherRestartThread thread = null;

	private MessageSend messageSend;

	private SystemHandler systemHandler;

	// ���������Ľ�����ip
	private String restartingIp = "";

	private static final int MAX_TIMEOUT = 5 * 60 * 1000;

	private static final String FILE_PATH = "\\tftp\\vxWorks.Z";

	private boolean isRestartSuccess = false;

	public void upgradeSwitcher(String ip, String client, String clientIp,
			Message message) {
		trapMonitor.registSystemRestartInter(this);

		StreamMessage sm = (StreamMessage) message;
		byte[] object = null;
		log.info("Start receive stream2......");
		try {
			object = (byte[]) sm.readObject();
		} catch (JMSException e1) {
			e1.printStackTrace();
		}

		log.info("Start File gernate......");
		// �ѷ���˴�����ֽ���ת��Ϊ���浽����·���¡�
		File file = getFile(object);
		// int timeout = 0;
		// while (!file.exists()) {
		// file = getFile(object);
		// try {
		// timeout = timeout + 100;
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// if (timeout > 10000) { // ������10����ʱ�˳�ѭ��
		// System.err
		// .println("wwwwwwwwwwzzzzzzzzzzzzzzzwwwwwwwwwwwwwwwwwwwwwww");
		// return;
		// }
		// }
		//
		// try {
		// sm.clearBody();
		// sm.clearProperties();
		// sm.reset();
		// message.clearBody();
		// message.clearProperties();
		// } catch (JMSException e2) {
		// log.error(e2);
		// }
		log.info("File gernate finish......");
		// ���������ɹ��Ľ�����IP
		// List<String> switcherList = new ArrayList<String>();

		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		log.info("Start Upgrade......");

		// ��ʼ����������
		startUpgrade(file, ip, client, clientIp);

		// ɾ�������ļ�vxWorks.Z
		deleteFile(file);

		// ���ݴ����ֵ�����Ƿ�������������
		// ����������˳��������ֻ�е�һ̨������Ϻ��������һ̨��
		// �Ƿ�����
		boolean isRestart;
		try {
			isRestart = (Boolean) message
					.getBooleanProperty(com.jhw.adm.server.entity.util.Constants.RESTART);
		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}

		if (null == ip || "".equals(ip)) {
			return;
		}
		String[] ipList = ip.split(";");
		for (int i = 0; i < ipList.length; i++) {
			String ipValue = ipList[i];
			if (isRestart) {
				restartSwitcher(client, clientIp, ipValue);
			} else {
				messageSend.sendTextMessageRes("������" + ipValue + "��������", "",
						MessageNoConstants.ALL_SWITCHER_UPGRADEREP, client,
						client, clientIp);
				log.info("Upgrader Finish......");

			}
		}
		// messageSend.sendTextMessageRes("��������������", "",
		// MessageNoConstants.ALL_SWITCHER_UPGRADEREP, client, client,
		// clientIp);
		// log.info("Upgrader Finish......");
		log.info("Seted up Restart device thread......");
	}

	/**
	 * �Ѵ�����ֽ���ת��Ϊ���ص��ļ�
	 * 
	 * @param object
	 * @return
	 */
	private File getFile(byte[] object) {
		String path = System.getProperty("user.dir");
		String fileName = path + FILE_PATH;
		File file = new File(fileName);
		FileOutputStream fileOutStream = null;
		if (file.isFile()) {
			file.delete();
		}
		try {

			if (file.createNewFile() && file.canWrite()) {
				fileOutStream = new FileOutputStream(file);
				fileOutStream.write(object);
			}
		} catch (IOException e) {
			System.out.println("IOException occur");
			e.getMessage();
		} finally {
			try {
				if (null != fileOutStream) {
					fileOutStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * ������ɺ�ɾ���ļ�
	 * 
	 * @param file
	 */
	private void deleteFile(final File file) {
		new Thread(new Runnable() {
			public void run() {
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
		}).start();
	}

	/**
	 * ��������ʼ����
	 * 
	 * @param file
	 * @param ip
	 */
	private void startUpgrade(File file, String ipStr, String client,
			String clientIp) {
		if (null == ipStr || "".equals(ipStr)) {
			return;
		}
		String[] ips = ipStr.split(";");
		for (int i = 0; i < ips.length; i++) {
			String ip = ips[i];

			snmpV2.setAddress(ip, Constants.SNMP_PORT);
			snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
			snmpV2.setTimeout(7 * 1000);
			PDU response = null;
			try {
				// byte[] dataBuffer = dataBufferBuilder.upgradeOperate(
				// Constants.UPGRADE, getLocalAddress(), file.getName());
				FEPEntity fepEntity = messageSend.getLoginService()
						.getFEPByCode(Configuration.fepCode);
				String localIpvalue = fepEntity.getIpValue();
				response = snmpV2.snmpSet(OID.SNMP_UPGRADE, localIpvalue + "/"
						+ file.getName());
				// ����tftp����
				startTftpService();

				// ��ͻ��˷�����Ϣ
				messageSend.sendTextMessageRes(ip, "",
						MessageNoConstants.SWITCHER_UPGRADEING, client,
						clientIp, clientIp);
				log.info("upgrading switcher---------ip:" + ip);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				int timeout = 0;
				int timeout2 = 0;
				while (true) {
					int returnValue = testSwitcherState(ip);
					log.info("Switcher--------------State:" + returnValue);
					if (returnValue == Constants.FAILED) {
						// ��ͻ��˷�����Ϣ
						messageSend.sendTextMessageRes(ip + "����ʧ��", "",
								MessageNoConstants.ALL_SWITCHER_UPGRADEREP, ip,
								client, clientIp);
						log.info("upgrade switcher fail---------ip:" + ip);
						// return;
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					} else if (returnValue == Constants.OK) {
						// ��ͻ��˷�����Ϣ
						messageSend.sendTextMessageRes(ip + "�����ɹ�", "",
								MessageNoConstants.ONE_SWITCHER_UPGRADEREP, ip,
								client, clientIp);
						log.info("upgrade switcher finished---------ip:" + ip);
						messageQueue.push(ip);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					} else if (returnValue == Constants.DOWNLOADING) {
						try {
							messageSend.sendTextMessageRes(ip + "��������", "",
									MessageNoConstants.ONE_SWITCHER_UPGRADEREP,
									ip, client, clientIp);
							Thread.sleep(5000);
							timeout = timeout + 5000;
							log.info("upgrade switcher downloading---------ip:"
									+ ip);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (timeout > MAX_TIMEOUT) {
							// return;
							break;
						}// else {
							// continue;
							// }
					} else if (returnValue == Constants.COMMAND_ERROR) {
						// ��ͻ��˷�����Ϣ
						messageSend.sendTextMessageRes(ip + "����ʧ��", "",
								MessageNoConstants.ALL_SWITCHER_UPGRADEREP, ip,
								client, clientIp);
						log.info("upgrade switcher command error---------ip:"
								+ ip);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// return;
						break;
					} else {
						try {
							Thread.sleep(2500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// TODO
						timeout2++;
						if (timeout2 >= 2) {
							messageSend.sendTextMessageRes(ip + "����ʧ��", "",
									MessageNoConstants.ALL_SWITCHER_UPGRADEREP,
									ip, client, clientIp);
							log.info("upgrade switcher command error---------ip:"
									+ ip);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}
					}
				}
			} catch (RuntimeException e) {
				log.error(getTraceMessage(e));
			} finally {
				if (response != null) {
					response.clear();
				}
			}
		}
	}

	/**
	 * ��⽻����״̬��
	 * 
	 * 0 failed ; 1 ok ; 2 downloding
	 */
	private int testSwitcherState(String ip) {
		int value = 0;
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_GET_UPDATE_STATE);
			if (null != response) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				value = responseVar.elementAt(0).getVariable().toInt();
			}
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
		} finally {
			if (response != null) {
				response.clear();
			}
		}
		return value;
	}

	/**
	 * ����������
	 */
	private void restartSwitcher(String client, String clientIp, String ip) {
		thread = new SwitcherRestartThread(client, clientIp, ip);
		thread.start();
	}

	class SwitcherRestartThread extends Thread {
		private ArrayList<String> list = new ArrayList<String>();
		private String client;
		private String clientIp;
		private String ip;

		public SwitcherRestartThread(String client, String clientIp, String ip) {
			this.client = client;
			this.clientIp = clientIp;
			this.ip = ip;
		}

		public void run() {
			synchronized (messageQueue) {
				while (messageQueue.size() > 0) {
					// restartingIp = messageQueue.getFirst();
					// System.out.println(restartingIp);
					restartingIp = ip;
					snmpV2.setAddress(restartingIp, Constants.SNMP_PORT);
					snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
					snmpV2.setTimeout(4 * 1000);
					PDU response = null;
					try {
						// byte[] dataBuffer =
						// dataBufferBuilder.upgradeOperate(
						// Constants.REBOOT, getLocalAddress(), "0"); //
						// getLocalAddress()

						// ��ͻ��˷�����Ϣ
						messageSend.sendTextMessageRes(restartingIp
								+ "����������. . . ", "",
								MessageNoConstants.ONE_SWITCHER_UPGRADEREP,
								restartingIp, client, clientIp);
						messageSend.sendTextMessage(restartingIp
								+ "����������. . . ",
								MessageNoConstants.SWITCHRESTING, client,
								clientIp);
						log.info("restarting switcher---------ip:"
								+ restartingIp);

						response = snmpV2.snmpSet(OID.SNMP_REBOOT, 1);

						pingDevice(restartingIp);// ͨ��ping�豸�������豸�Ƿ������ɹ�

						messageQueue.wait(MAX_TIMEOUT); // /�ȴ���ʱ

						// ��isRestartSuccessΪtrueʱ�����յ����豸�����󷢵�trap,
						if (isRestartSuccess) {
							// ��ͻ��˷�����Ϣ
							if ("" != restartingIp) {
								messageSend
										.sendTextMessageRes(
												"������" + restartingIp + "�����ɹ�",
												"",
												MessageNoConstants.ONE_SWITCHER_UPGRADEREP,
												restartingIp, client, clientIp);
								Thread.sleep(3000);
								messageSend.sendTextMessage(restartingIp,
										MessageNoConstants.SWITCHRESTSUCCESS,
										client, clientIp);
								log.info("restart switcher finished---------ip:"
										+ restartingIp);
								list.add(restartingIp);
								messageQueue.removeFirst();
							}
							// else {
							// messageSend
							// .sendTextMessageRes(
							// "��������������",
							// "",
							// MessageNoConstants.ALL_SWITCHER_UPGRADEREP,
							// client, client, clientIp);
							// log.info("Upgrader Finish......");
							// }
						} else {
							// ��isRestartSuccessΪfalseʱ�����豸����ʧ���ˣ�
							// ��ʱ�豸Ϊ���ɷ��ʵ�״̬����ô���豸�����ӵ������������豸
							// Ҳ�Ͳ��ܽ�������������,����ֱ���˳���ѭ��
							if ("" != restartingIp) {
								messageSend
										.sendTextMessageRes(
												"������" + restartingIp + "����ʧ��",
												"",
												MessageNoConstants.ONE_SWITCHER_UPGRADEREP,
												restartingIp, client, clientIp);
								Thread.sleep(3000);
								messageSend.sendTextMessage(restartingIp,
										MessageNoConstants.SWITCHRESTFAIL,
										client, clientIp);
								log.info("restart switcher failed---------ip:"
										+ restartingIp);
								break;
							}
							// else {
							// messageSend
							// .sendTextMessageRes(
							// "��������������",
							// "",
							// MessageNoConstants.ALL_SWITCHER_UPGRADEREP,
							// client, client, clientIp);
							// log.info("Upgrader Finish......");
							// }
						}

					} catch (InterruptedException e1) {
						log.error(getTraceMessage(e1));
					} catch (RuntimeException e2) {
						log.error(getTraceMessage(e2));
					} finally {
						if (response != null) {
							response.clear();
						}
						restartingIp = "";
						isRestartSuccess = false;
					}
				}
			}

			// ��ѯ������Ϣ
			if (list.size() < 1) {
				return;
			}
			HashMap<String, SwitchBaseInfo> dataMap = new HashMap<String, SwitchBaseInfo>();
			for (int i = 0; i < list.size(); i++) {
				String ip = list.get(i);
				SwitchBaseInfo switchBaseInfo = systemHandler.getSysInfo(ip);
				dataMap.put(ip, switchBaseInfo);
			}
			// �Ѳ�ѯ���Ľ�����������Ϣ�͸������
			messageSend
					.sendObjectMessage(
							com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
							dataMap, MessageNoConstants.SWITCHRECEIVE, client,
							clientIp);
			messageSend.sendTextMessageRes("��������������", "",
					MessageNoConstants.ALL_SWITCHER_UPGRADEREP, client, client,
					clientIp);
			log.info("Upgrader Finish......");
		}

	}

	private void pingDevice(final String ip) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				long t = 0l;
				BufferedReader in = null;
				Runtime r = Runtime.getRuntime();
				String osName = System.getProperty("os.name").trim();
				System.out.println(osName);
				String pingCommand = "";
				boolean isReceive = false;// �ж��Ƿ���յ������������ɹ�

				while (true) {
					try {
						if (t > MAX_TIMEOUT || isReceive) {
							break;
						}
						if (osName.startsWith("Windows")) {
							pingCommand = "ping " + ip + " -n " + 1 + " -w "
									+ 1000;
						} else {
							pingCommand = "ping " + ip + " -c " + 1 + " -w "
									+ 1000;
						}

						Process p = r.exec(pingCommand);
						if (p != null) {
							in = new BufferedReader(new InputStreamReader(
									p.getInputStream()));
							String line = null;
							while ((line = in.readLine()) != null) {
								if (line.endsWith("TTL=64")) {
									isReceive = true;
									notifySwitcherRestart(ip);
									break;
								}
							}
						}

						t = t + 1000;
						Thread.sleep(1000);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});

		thread.start();
	}

	/**
	 * ��ȡ������ַ
	 */
	private String getLocalAddress() {
		String localAddress = "0.0.0.0";
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			localAddress = localHost.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return localAddress;
	}

	/**
	 * ����tftp����
	 * 
	 * @return
	 */
	public boolean startTftpService() {
		try {
			tftpServer = new TftpServiceThread();
			tftpServer.start();
		} catch (Exception e) {
			log.info("����TFTP����ʧ��" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public byte[] setIP(String IP) {
		byte[] ipTypes = null;
		try {
			ipTypes = InetAddress.getByName(IP).getAddress();
		} catch (UnknownHostException ex) {
		}
		return ipTypes;
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public DataBufferBuilder getDataBufferBuilder() {
		return dataBufferBuilder;
	}

	public void setDataBufferBuilder(DataBufferBuilder dataBufferBuilder) {
		this.dataBufferBuilder = dataBufferBuilder;
	}

	public TrapMonitor getTrapMonitor() {
		return trapMonitor;
	}

	public void setTrapMonitor(TrapMonitor trapMonitor) {
		this.trapMonitor = trapMonitor;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	@Override
	public void notifySwitcherRestart(final String ip) {
		new Thread(new Runnable() {
			public void run() {
				synchronized (messageQueue) {
					if (thread != null && thread.isAlive()
							|| ip.equals(restartingIp)) {
						isRestartSuccess = true;
						messageQueue.notify();
					}
				}
			}
		}).start();
	}
}
