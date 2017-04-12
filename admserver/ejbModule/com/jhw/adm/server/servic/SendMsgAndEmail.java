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
		// "您好！\n\r   Ip为：192.168.10.200 的设备的端口5 相连接的交换机线被拔掉，请及时注意检查，以免带来影响，此邮件勿需回复!",
		// "线被拔掉告警", "yangxiao@jhw.com.cn",
		// "zuojunyong@jhw.com.cn", "zjy123456");
		// boolean aa=andEmail1.startSendMsg("COM1", "15814488224",
		// "192.168.5.200", Constants.PINGOUT, 0);
		// andEmail1.startSendEmail("中国人民共和国来电", "中国人民共和国来电中国人民共和国来电中国人民共和国来电",
		// "adm2000@sina.cn","zuojunyong@jhw.com.cn", "zjy123456",
		// "smtp.sina.cn");

	}

	public void sendMsgAndEmail(UserEntity userEntity, String ip,
			int warningType, int sPort, int warningLevel,String content) {

		String msg = "";
		try {
			if (userEntity != null) {
				logger
						.info("用户:" + userEntity.getUserName() + "告警通知方式："
								+ userEntity.getWarningStyle() + "告警级别："
								+ warningLevel);
				String type = userEntity.getWarningStyle();
				if (type != null) {
					String[] types = type.split(",");
					String level = userEntity.getCareLevel();
					String[] levels = level.split(",");
					String subLevel = "";
					if (warningLevel == Constants.URGENCY) {
						subLevel = "紧急";
						logger.info("告警类型：" + subLevel);
					} else if (warningLevel == Constants.SERIOUS) {
						subLevel = "严重";
						logger.info("告警类型：" + subLevel);
					} else if (warningLevel == Constants.INFORM) {
						subLevel = "通知";
						logger.info("告警类型：" + subLevel);
					} else if (warningLevel == Constants.GENERAL) {
						subLevel = "普通";
						logger.info("告警类型：" + subLevel);
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
							msg = "线被拔掉告警，IP为：" + ip + "的设备的端口" + sPort
									+ "相连接的交换机线被拔掉";
							contents = userName + " 您好！\n\r   IP为" + ip
									+ "的设备的端口" + sPort
									+ "相连接的交换机线被拔掉告警，请及时注意检查，以免带来影响，此邮件勿需回复!";
						} else if (warningType == Constants.REMONTHING) {
							msg = "流量告警";
							contents = userName + " 您好！\n\r   IP为" + ip
									+ "的设备的端口" + sPort
									+ "流量告警，请及时注意检查，以免带来影响，此邮件勿需回复!";
						} else if (warningType == Constants.PINGOUT) {

							msg = "定时Ping告警，IP为：" + ip + "的设备Ping不通";
							contents = userName + " 您好！\n\r   IP为" + ip
									+ "的设备Ping不通，请及时注意检查，以免带来影响，此邮件勿需回复!";
						} else if (warningType == Constants.PINGIN) {

							msg = "定时Ping告警，IP为：" + ip + "的设备Ping通";
							contents = userName + " 您好！\n\r   IP为" + ip
									+ "的设备Ping通，请及时注意检查，以免带来影响，此邮件勿需回复!";
						}
						else if (warningType == Constants.LINKUP) {

							msg = "线连接提醒，IP为：" + ip + "的设备的端口" + sPort + "连接成功";
							contents = userName + " 您好！\n\r   IP为" + ip
									+ "的设备的端口" + sPort
									+ "连接成功，请及时注意检查，以免带来影响，此邮件勿需回复!";
						}
						
						else if (warningType == Constants.SYSLOG) {

							msg = "syslog提醒，IP为：" + ip ;
							contents = userName + " 您好！\n\r   IP为" + ip
									+ "的设备syslog："+ content + "，请及时注意检查，以免带来影响，此邮件勿需回复!";
						}
						String subject = "设备告警！级别：" + subLevel + "！！！";
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
								logger.info("告警通知方式:" + typeSplit);
								if (typeSplit.equals("S")) {
									if (checkLevelSendMsg(levels, warningLevel)) {
										WarningRecord warningRecord = new WarningRecord();
										warningRecord.setTime(new Date());
										warningRecord.setMessage("发送短信通知" + msg);
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
													logger.info("用户："+ userEntity.getUserName()+ "手机："+ personInfo.getMobile()
																	+ "告警方式："+ userEntity.getWarningStyle());
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
												logger.info("用户："+ userEntity.getUserName()+ "手机："+ personInfo.getMobile()+ "告警方式："+ userEntity.getWarningStyle());
											}
										} else {
											logger.info("未配置串口不能发短信！");

										}

									}
								} else if (typeSplit.equals("E")) {
									if (checkLevelSendMsg(levels, warningLevel)) {
										WarningRecord warningRecord = new WarningRecord();
										warningRecord.setTime(new Date());
										warningRecord
												.setMessage("发送邮件通知" + msg);
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
														logger.info("***************"+ toAddress+ " 发送成功！");

													} else {
														warningRecord
																.setSendTag(false);
													}
													logger.info("用户："+ userEntity.getUserName()+ "邮箱地址："+ personInfo.getEmail()+ "告警方式："+ userEntity.getWarningStyle());
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
													logger.info("***************邮件发送成功！");
												} else {
													warningRecord.setSendTag(false);
												}
												logger.info("用户："+ userEntity.getUserName()
																+ "邮箱地址："
																+ personInfo.getEmail()
																+ "告警方式："
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
						logger.info("发送邮件或短信时PersonInfo为空！");
					}
				} else {

					logger.info("发送邮件或短信时未配置告警方式！");
				}
			} else {
				logger.info("发送邮件或短信时UserEntity为空！");
			}
		} catch (Exception e) {
			logger.error("邮件短信异常：", e);
		}

	}
	
	
	public boolean checkLevelSendMsg(String[] levels, int warningLevel) {
		boolean flag = false;

		if (levels != null && levels.length > 0) {
			for (int i = 0; i < levels.length; i++) {
				logger.info("用户接受告警的级别:" + (levels[i]));
				logger.info("接受到的告警级别：" + warningLevel);
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
				logger.info("端口名称:" + portId.getName());
				if (portId.getName().equals(port)) {
					logger.info("找到端口： " + port);
					sendMsgToMobile(portId, mobile, ip, warningType, sPort);
					Thread.sleep(8000);
					break;
				} else {
					logger.info("未找到串口" + port + "！");
				}
			}
		} catch (Exception e) {
			flag = false;
			logger.error("查找 串口：" + port + "异常：", e);
		}
		}else{
			logger.error("手机号为空不能发短信");
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

	private int convertASC(String str) {// 字符串转换为ASCII码
		str = str.toUpperCase();
		char[] chars = str.toCharArray(); // 把字符中转换为字符数组
		int ascStr = 0;
		for (int i = 0; i < chars.length; i++) {
			ascStr += chars[i];
		}
		return ascStr;
	}

	// *******************************************************************发送邮件

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
			logger.info("邮件发送成功！");

		} catch (MessagingException e) {
			logger.info("发送邮件时异常请检查网络设置！", e);
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
		String str = "您好！设备";
		String str2 = "为：";
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
			msg = "线被拔掉告警";
			str3 = "的端口";
			str4 = "相连接的交换机" + msg + "，请及时注意检查，以免带来影响，此短信勿需回复！";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(
					convertUnicode(str3)).concat(portConvert.trim()).concat(
					convertUnicode(str4));
		} else if (warningType == Constants.REMONTHING) {
			msg = "流量告警";
			str4 = msg + "，请及时注意检查，以免带来影响，此短信勿需回复！";
			str3 = "的端口";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(
					convertUnicode(str3)).concat(portConvert.trim()).concat(
					convertUnicode(str4));

		} else if (warningType == Constants.PINGOUT) {
			msg = "&#50&#49&#4E&#47";
			str4 = "不通告警，请及时注意检查，以免带来影响，此短信勿需回复！";
			allStr = convertUnicode(str).concat("&#49&#50").concat(
					convertUnicode(str2)).concat(ipConvert.trim()).concat(msg)
					.concat(convertUnicode(str4));
		} else if (warningType == Constants.LINKUP) {
			msg = "连接成功";
			str3 = "的端口";
			str4 = "相连接的设备" + msg + "，请及时注意检查，以免带来影响，此短信勿需回复！";
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
						logger.error("串口打开");
					} else {
						logger.error("串口已打开");
					}

				}
			} catch (PortInUseException e) {
				logger.error("串口正在使用！", e);
				flag = false;
				return flag;
			}
			if (serialPort != null) {
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,// 数据位数
						SerialPort.STOPBITS_1, // 停止位
						SerialPort.PARITY_NONE);// 奇偶位
				serialPort.enableReceiveTimeout(2000);
				logger.info("串口已打开。\n发送AT指令 ！");
				InputStream inputStream = serialPort.getInputStream();
				OutputStream outputStream = serialPort.getOutputStream();
				String allUnicode = allUnicode(ip, warningType, sPort);
				logger.info("消息内容：" + allUnicode);
				int size = allUnicode.length() / 2;
				String message = ((size >= 10) ? Integer.toHexString(size)
						: ("0" + size))
						+ allUnicode;
				String pduInfo = smsDecodedNumber(mobile) + message;
				String cmd0 = "AT+CMGF=0 \r";
				String cmd1 = "AT+CMGS=" + (pduInfo.length() - 2) / 2 + " \r";
				String cmd2 = (char) (Integer.parseInt("1a", 16)) + "z";// 用ctrl-z表示结束并发送
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
						logger.info("成功收到指令返回值！");
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("串口信息:", e);
			flag = false;
		}
		return flag;

	}

}
