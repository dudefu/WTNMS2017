package com.jhw.adm.server.servic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import com.jhw.adm.server.entity.emailLog.SendEmailLog;
import com.jhw.adm.server.entity.system.PersonInfo;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.EmailConfigEntity;
import com.jhw.adm.server.entity.warning.WarningRecord;
import com.jhw.adm.server.interfaces.SendMsgAndEmailLocal;

@Stateless
@LocalBinding(jndiBinding = "local/SendMsgAndEmail")
public class SendMsgAndEmail implements SendMsgAndEmailLocal {
	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	private Logger logger = Logger.getLogger(SendMsgAndEmail.class.getName());
	private static SerialPort serialPort = null;

	public static void main(String[] args) throws InterruptedException {

		SendMsgAndEmail andEmail1 = new SendMsgAndEmail();
		// andEmail1
		// .startSendEmail(
		// "���ã�\n\r   IpΪ��192.168.10.200 ���豸�Ķ˿�5 �����ӵĽ������߱��ε����뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!",
		// "�߱��ε��澯", "yangxiao@jhw.com.cn",
		// "zuojunyong@jhw.com.cn", "zjy123456");
		// boolean aa=andEmail1.startSendMsg("COM1", "15814488224",
		// "192.168.5.200", Constants.PINGOUT, 0);
		// andEmail1.startSendEmail("�й����񹲺͹�����", "�й����񹲺͹������й����񹲺͹������й����񹲺͹�����",
		// "adm2000@sina.cn","zuojunyong@jhw.com.cn", "zjy123456",
		// "smtp.sina.cn");

	}

	public void sendMsgAndEmail(UserEntity userEntity, String ip,
			int warningType, int sPort, int warningLevel,String content) {

		String msg = "";
		try {
			if (userEntity != null) {
				logger
						.info("�û�:" + userEntity.getUserName() + "�澯֪ͨ��ʽ��"
								+ userEntity.getWarningStyle() + "�澯����"
								+ warningLevel);
				String type = userEntity.getWarningStyle();
				if (type != null) {
					String[] types = type.split(",");
					String level = userEntity.getCareLevel();
					String[] levels = level.split(",");
					String subLevel = "";
					if (warningLevel == Constants.URGENCY) {
						subLevel = "����";
						logger.info("�澯���ͣ�" + subLevel);
					} else if (warningLevel == Constants.SERIOUS) {
						subLevel = "����";
						logger.info("�澯���ͣ�" + subLevel);
					} else if (warningLevel == Constants.INFORM) {
						subLevel = "֪ͨ";
						logger.info("�澯���ͣ�" + subLevel);
					} else if (warningLevel == Constants.GENERAL) {
						subLevel = "��ͨ";
						logger.info("�澯���ͣ�" + subLevel);
					}

					PersonInfo personInfo = userEntity.getPersonInfo();
					if (personInfo != null) {
						String mobile = personInfo.getMobile();
						String userName = personInfo.getName();
						String toAddress = personInfo.getEmail();
						String fromAddress = "";
						String password = "";
						String serialPortName = "";
						String smtp = "";
						boolean flag = false;
						EmailConfigEntity emailConfigEntity = this
								.queryEmailConfigEntity();
						String contents = "";
						if (userName == null | userName.equals("")) {
							userName = userEntity.getUserName();
						} else {
							userName = personInfo.getName() + "("
									+ userEntity.getUserName() + ")";
						}
						if (warningType == Constants.LINKDOWN) {
							msg = "�߱��ε��澯��IPΪ��" + ip + "���豸�Ķ˿�" + sPort
									+ "�����ӵĽ������߱��ε�";
							contents = userName + " ���ã�\n\r   IPΪ" + ip
									+ "���豸�Ķ˿�" + sPort
									+ "�����ӵĽ������߱��ε��澯���뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!";
						} else if (warningType == Constants.REMONTHING) {
							msg = "�����澯";
							contents = userName + " ���ã�\n\r   IPΪ" + ip
									+ "���豸�Ķ˿�" + sPort
									+ "�����澯���뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!";
						} else if (warningType == Constants.PINGOUT) {

							msg = "��ʱPing�澯��IPΪ��" + ip + "���豸Ping��ͨ";
							contents = userName + " ���ã�\n\r   IPΪ" + ip
									+ "���豸Ping��ͨ���뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!";
						} else if (warningType == Constants.PINGIN) {

							msg = "��ʱPing�澯��IPΪ��" + ip + "���豸Pingͨ";
							contents = userName + " ���ã�\n\r   IPΪ" + ip
									+ "���豸Pingͨ���뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!";
						}
						else if (warningType == Constants.LINKUP) {

							msg = "���������ѣ�IPΪ��" + ip + "���豸�Ķ˿�" + sPort + "���ӳɹ�";
							contents = userName + " ���ã�\n\r   IPΪ" + ip
									+ "���豸�Ķ˿�" + sPort
									+ "���ӳɹ����뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!";
						}
						
						else if (warningType == Constants.SYSLOG) {

							msg = "syslog���ѣ�IPΪ��" + ip ;
							contents = userName + " ���ã�\n\r   IPΪ" + ip
									+ "���豸syslog��"+ content + "���뼰ʱע���飬�������Ӱ�죬���ʼ�����ظ�!";
						}
						String subject = "�豸�澯������" + subLevel + "������";
						if (emailConfigEntity != null) {
							fromAddress = emailConfigEntity.getAccounts();
							password = emailConfigEntity.getPassword();
							serialPortName = emailConfigEntity
									.getSerialPortName();
							smtp = emailConfigEntity.getEmailServer();
							flag = true;
						}
						if (types.length > 0) {
							for (int i = 0; i < types.length; i++) {
								String typeSplit = types[i];
								logger.info("�澯֪ͨ��ʽ:" + typeSplit);
								if (typeSplit.equals("S")) {
									if (checkLevelSendMsg(levels, warningLevel)) {
										WarningRecord warningRecord = new WarningRecord();
										warningRecord.setTime(new Date());
										warningRecord.setMessage("���Ͷ���֪ͨ" + msg);
										warningRecord.setPersonInfo(personInfo);
										warningRecord.setUserName(userName);
										warningRecord.setIpValue(ip);
										warningRecord
												.setSendType(Constants.SENDNOTE);
										warningRecord.setSendTag(false);
										if (serialPortName != null&& !serialPortName.equals("")) {
											SendEmailLog emailLog = findSendEmailLog(ip, "", mobile,warningType, userEntity.getUserName());
											if (emailLog != null) {
												boolean flagDate = this.checkSendAgain(emailLog);
												if (!flagDate) {
													boolean sendmsg = this.startSendMsg(serialPortName.toUpperCase(),mobile,ip,warningType,sPort);
													if (sendmsg) {
														warningRecord.setSendTag(true);
														emailLog.setSendDate(new Date());
														emailLog.setNextDate(this.setNextSendDate());
														emailLog.setMobile(mobile);
														emailLog.setIp_value(ip);
														manager.merge(emailLog);
													} else {
														warningRecord.setSendTag(false);
													}
													manager.persist(warningRecord);
													logger.info("�û���"+ userEntity.getUserName()+ "�ֻ���"+ personInfo.getMobile()
																	+ "�澯��ʽ��"+ userEntity.getWarningStyle());
												}

											} else {
												boolean sendmsg = this.startSendMsg(serialPortName.toUpperCase(),mobile,ip,warningType,sPort);
												if (sendmsg) {
													warningRecord.setSendTag(true);
													emailLog = new SendEmailLog();
													emailLog.setSendDate(new Date());
													emailLog.setNextDate(this.setNextSendDate());
													emailLog.setMobile(mobile);
													emailLog.setIp_value(ip);
													emailLog.setWarningType(warningType);
													manager.persist(emailLog);
												} else {
													warningRecord
															.setSendTag(false);
												}
												manager.persist(warningRecord);
												logger.info("�û���"+ userEntity.getUserName()+ "�ֻ���"+ personInfo.getMobile()+ "�澯��ʽ��"+ userEntity.getWarningStyle());
											}
										} else {
											logger.info("δ���ô��ڲ��ܷ����ţ�");

										}

									}
								} else if (typeSplit.equals("E")) {
									if (checkLevelSendMsg(levels, warningLevel)) {
										WarningRecord warningRecord = new WarningRecord();
										warningRecord.setTime(new Date());
										warningRecord
												.setMessage("�����ʼ�֪ͨ" + msg);
										warningRecord.setPersonInfo(personInfo);
										warningRecord.setUserName(userName);
										warningRecord.setIpValue(ip);
										warningRecord.setSendTag(false);
										warningRecord
												.setSendType(Constants.SENDEMAIL);
										if (flag) {
											SendEmailLog emailLog = findSendEmailLog(ip, toAddress, "",warningType, userEntity.getUserName());
											if (emailLog != null) {
												boolean flagDate = this.checkSendAgain(emailLog);
												if (!flagDate) {
													boolean sendMail = this.startSendEmail(contents,subject,toAddress,fromAddress,password,smtp);
													if (sendMail) {
														warningRecord.setSendTag(true);
														emailLog.setSendDate(new Date());
														emailLog.setNextDate(this.setNextSendDate());
														emailLog.setEmail(toAddress);
														emailLog.setIp_value(ip);
														manager.merge(emailLog);
														logger.info("***************"+ toAddress+ " ���ͳɹ���");

													} else {
														warningRecord
																.setSendTag(false);
													}
													logger.info("�û���"+ userEntity.getUserName()+ "�����ַ��"+ personInfo.getEmail()+ "�澯��ʽ��"+ userEntity.getWarningStyle());
													manager.persist(warningRecord);
												}

											} else {
												boolean sendMail = this.startSendEmail(contents,subject,toAddress,fromAddress,password, smtp);
												if (sendMail) {
													warningRecord.setSendTag(true);
													emailLog = new SendEmailLog();
													emailLog.setSendDate(new Date());
													emailLog.setNextDate(this.setNextSendDate());
													emailLog.setEmail(toAddress);
													emailLog.setIp_value(ip);
													emailLog.setWarningType(warningType);
													manager.persist(emailLog);
													logger.info("***************�ʼ����ͳɹ���");
												} else {
													warningRecord.setSendTag(false);
												}
												logger.info("�û���"+ userEntity.getUserName()
																+ "�����ַ��"
																+ personInfo.getEmail()
																+ "�澯��ʽ��"
																+ userEntity
																		.getWarningStyle());
												manager.persist(warningRecord);
											}
										}

									}
								}
							}
						}
					} else {
						logger.info("�����ʼ������ʱPersonInfoΪ�գ�");
					}
				} else {

					logger.info("�����ʼ������ʱδ���ø澯��ʽ��");
				}
			} else {
				logger.info("�����ʼ������ʱUserEntityΪ�գ�");
			}
		} catch (Exception e) {
			logger.error("�ʼ������쳣��", e);
		}

	}
	
	
	public boolean checkLevelSendMsg(String[] levels, int warningLevel) {
		boolean flag = false;

		if (levels != null && levels.length > 0) {
			for (int i = 0; i < levels.length; i++) {
				logger.info("�û����ܸ澯�ļ���:" + (levels[i]));
				logger.info("���ܵ��ĸ澯����" + warningLevel);
				if (Integer.parseInt(levels[i]) == warningLevel) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	private synchronized boolean startSendMsg(String port, String mobile,
			String ip, int warningType, int sPort) {
		boolean flag = true;
		if(mobile!=null&&!mobile.equals("")){
		try {
			
			for (Enumeration<CommPortIdentifier> en = CommPortIdentifier
					.getPortIdentifiers(); en.hasMoreElements();) {
				CommPortIdentifier portId = en.nextElement();
				logger.info("�˿�����:" + portId.getName());
				if (portId.getName().equals(port)) {
					logger.info("�ҵ��˿ڣ� " + port);
					sendMsgToMobile(portId, mobile, ip, warningType, sPort);
					Thread.sleep(8000);
					break;
				} else {
					logger.info("δ�ҵ�����" + port + "��");
				}
			}
		} catch (Exception e) {
			flag = false;
			logger.error("���� ���ڣ�" + port + "�쳣��", e);
		}
		}else{
			logger.error("�ֻ���Ϊ�ղ��ܷ�����");
			flag=false;
		}
		return flag;
	}

	private boolean checkedSendMsg(InputStream inputStream) throws IOException {
		int c = 0;
		StringBuffer str = new StringBuffer();
		while ((c = inputStream.read()) != -1) {
			str.append((char) c);
		}
		if (str.indexOf("+CMGS:") > -1) {
			return true;
		} else if (str.indexOf("ERROR") > -1) {
			return false;
		} else {
			return true;
		}

	}

	private int convertASC(String str) {// �ַ���ת��ΪASCII��
		str = str.toUpperCase();
		char[] chars = str.toCharArray(); // ���ַ���ת��Ϊ�ַ�����
		int ascStr = 0;
		for (int i = 0; i < chars.length; i++) {
			ascStr += chars[i];
		}
		return ascStr;
	}

	// *******************************************************************�����ʼ�

	private boolean startSendEmail(String contents, String subject,
			String toAddress, String fromAddress, String password, String smtp) {
		Session session = null;
		boolean flag = true;
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", smtp);
			props.put("mail.smtp.auth", "true");

			Authenticator auth = new PopupAuthenticator(fromAddress, password);
			// Get session
			session = Session.getDefaultInstance(props, auth);
			session.setDebug(false);
			// Define message
			MimeMessage message = new MimeMessage(session);
			Address[] to = new InternetAddress[] { new InternetAddress(
					toAddress) };
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO, to);

			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setText(contents);
			Transport.send(message);
			logger.info("�ʼ����ͳɹ���");

		} catch (MessagingException e) {
			logger.info("�����ʼ�ʱ�쳣�����������ã�", e);
			flag = false;
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		return flag;
	}

	private EmailConfigEntity queryEmailConfigEntity() {

		String sql = "select c from EmailConfigEntity c";
		Query query = manager.createQuery(sql);
		EmailConfigEntity emailConfigEntity = null;
		if (query.getResultList() != null && query.getResultList().size() > 0) {

			emailConfigEntity = (EmailConfigEntity) query.getResultList()
					.get(0);
		}
		return emailConfigEntity;
	}

	private SendEmailLog findSendEmailLog(String ip_value, String email,
			String mobile, int warningType, String userName) {

		String sql = "select c from SendEmailLog c where 1=1";
		if (ip_value != null && !ip_value.equals("")) {
			sql += " and c.ip_value=:ip_value";
		}
		if (email != null && !email.equals("")) {

			sql += " and c.email=:email";
		}
		if (userName != null && !userName.equals("")) {
			sql += " and c.userName=:userName";
		}
		if (mobile != null && !mobile.equals("")) {

			sql += " and c.mobile=:mobile";
		}
		if (warningType > -1) {
			sql += " and c.warningType=:warningType";
		}
		Query query = manager.createQuery(sql);

		if (email != null && !email.equals("")) {

			query.setParameter("email", email);
		}

		if (mobile != null && !mobile.equals("")) {

			query.setParameter("mobile", mobile);
		}
		if ((ip_value != null && !ip_value.equals(""))) {
			query.setParameter("ip_value", ip_value);
		}
		if (warningType > -1) {
			query.setParameter("warningType", warningType);
		}
		if (userName != null && !userName.equals("")) {
			query.setParameter("userName", userName);
		}
		List<SendEmailLog> list = query.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}

	}

	private Date setNextSendDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY), calendar
						.get(Calendar.MINUTE) + 10);
		return calendar.getTime();
	}

	private boolean checkSendAgain(SendEmailLog log) {
		boolean flag = true;
		Date nowDate = new Date();
		Calendar c = Calendar.getInstance();
		Date logDate = log.getNextDate();
		c.setTime(logDate);

		if (nowDate.after((logDate))) {
			flag = false;
		}
		return flag;
	}

	private String smsDecodedNumber(String srvNumber) {
		StringBuffer s = new StringBuffer();
		s.append("0011000D91");
		if (!(srvNumber.substring(0, 2) == "86")) {
			s.append("68");
		}
		int nLength = srvNumber.length();
		for (int i = 1; i < nLength; i += 2) {
			s.append(srvNumber.charAt(i));
			s.append(srvNumber.charAt(i - 1));
		}
		if (!(nLength % 2 == 0)) {
			s.append('F');
			s.append(srvNumber.charAt(nLength - 1));
		}
		s.append("000800");
		return s.toString();
	}

	private String convertUnicode(String msg) {

		String[] args = new String[] { msg };
		int len = args[0].length();
		String[] s = new String[len];
		for (int i = 0; i < len; i++) {
			char c = args[0].charAt(i);
			s[i] = Integer.toString(c, 16);
		}
		String aa = "";
		for (int i = 0; i < s.length; i++) {
			aa += s[i];
		}
		return aa.trim();
	}

	private String allUnicode(String ip, int warningType, int sPort) {
		String msg = "";
		String str = "���ã��豸";
		String str2 = "Ϊ��";
		String str4 = "";
		String[] ips = ip.split("\\.");
		String ipConvert = "";
		String portConvert = "";
		String portStr = "";
		String allStr = "";
		String str3 = "";
		Map<String, String> mapStore = mapStore();
		if (ips.length > 0) {
			for (int i = 0; i < ips.length; i++) {
				String ipSp = ips[i];
				for (int j = 0; j < ipSp.toCharArray().length; j++) {
					String single = String.valueOf(ipSp.toCharArray()[j]);
					ipConvert += mapStore.get(single);
				}
				if (i < (ips.length - 1)) {
					ipConvert += "&#2E";
				}
			}
		}
		portStr = "" + sPort;
		if (portStr.length() > 0) {
			for (int j = 0; j < portStr.toCharArray().length; j++) {
				String single = String.valueOf(portStr.toCharArray()[j]);
				portConvert += mapStore.get(single);
			}
		}
		if (warningType == Constants.LINKDOWN) {
			msg = "�߱��ε��澯";
			str3 = "�Ķ˿�";
			str4 = "�����ӵĽ�����" + msg + "���뼰ʱע���飬�������Ӱ�죬�˶�������ظ���";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(
					convertUnicode(str3)).concat(portConvert.trim()).concat(
					convertUnicode(str4));
		} else if (warningType == Constants.REMONTHING) {
			msg = "�����澯";
			str4 = msg + "���뼰ʱע���飬�������Ӱ�죬�˶�������ظ���";
			str3 = "�Ķ˿�";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(
					convertUnicode(str3)).concat(portConvert.trim()).concat(
					convertUnicode(str4));

		} else if (warningType == Constants.PINGOUT) {
			msg = "&#50&#49&#4E&#47";
			str4 = "��ͨ�澯���뼰ʱע���飬�������Ӱ�죬�˶�������ظ���";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(msg)
					.concat(convertUnicode(str4));
		} else if (warningType == Constants.LINKUP) {
			msg = "���ӳɹ�";
			str3 = "�Ķ˿�";
			str4 = "�����ӵ��豸" + msg + "���뼰ʱע���飬�������Ӱ�죬�˶�������ظ���";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(
					convertUnicode(str3)).concat(portConvert.trim()).concat(
					convertUnicode(str4));
		}
		return allStr;
	}

	private Map<String, String> mapStore() {
		Map<String, String> mapStore = new HashMap<String, String>();
		mapStore.put("0", "&#30");
		mapStore.put("1", "&#31");
		mapStore.put("2", "&#32");
		mapStore.put("3", "&#33");
		mapStore.put("4", "&#34");
		mapStore.put("5", "&#35");
		mapStore.put("6", "&#36");
		mapStore.put("7", "&#37");
		mapStore.put("8", "&#38");
		mapStore.put("9", "&#39");
		return mapStore;
	}

	private boolean sendMsgToMobile(CommPortIdentifier portId, String mobile,
			String ip, int warningType, int sPort) throws InterruptedException {
		boolean flag = true;
		try {
			try {
				if (portId != null) {
					if (!portId.isCurrentlyOwned()) {
						serialPort = (SerialPort) portId.open("modem", 2000);
						logger.error("���ڴ�");
					} else {
						logger.error("�����Ѵ�");
					}

				}
			} catch (PortInUseException e) {
				logger.error("��������ʹ�ã�", e);
				flag = false;
				return flag;
			}
			if (serialPort != null) {
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,// ����λ��
						SerialPort.STOPBITS_1, // ֹͣλ
						SerialPort.PARITY_NONE);// ��żλ
				serialPort.enableReceiveTimeout(2000);
				logger.info("�����Ѵ򿪡�\n����ATָ�� ��");
				InputStream inputStream = serialPort.getInputStream();
				OutputStream outputStream = serialPort.getOutputStream();
				String allUnicode = allUnicode(ip, warningType, sPort);
				logger.info("��Ϣ���ݣ�" + allUnicode);
				int size = allUnicode.length() / 2;
				String message = ((size >= 10) ? Integer.toHexString(size)
						: ("0" + size))
						+ allUnicode;
				String pduInfo = smsDecodedNumber(mobile) + message;
				String cmd0 = "AT+CMGF=0 \r";
				String cmd1 = "AT+CMGS=" + (pduInfo.length() - 2) / 2 + " \r";
				String cmd2 = (char) (Integer.parseInt("1a", 16)) + "z";// ��ctrl-z��ʾ����������
				String cmd3 = pduInfo + cmd2;
				outputStream.write("AT\r".getBytes());
				Thread.sleep(1000);
				outputStream.write(cmd0.getBytes());
				Thread.sleep(1000);
				outputStream.write(cmd1.getBytes());
				Thread.sleep(1000);
				outputStream.write(cmd3.getBytes());
				outputStream.flush();
				outputStream.close();
				Thread.sleep(1000);
				byte[] data = new byte[1024];
				for (int i = inputStream.read(data);; i = inputStream
						.read(data)) {
					if (i > 0) {
						logger.info(new String(data, 0, i));
						logger.info("�ɹ��յ�ָ���ֵ��");
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("������Ϣ:", e);
			flag = false;
		}
		return flag;

	}

}
