package com.jhw.adm.comclient.gprs.hongdian;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * Notice:Instantiated only once
 * 
 * @author xiongbo
 * 
 */
public class DDPServer {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private List<IDDPListener> ddpListeners;
	private DataServiceCenter dsc;
	private DDPServerListener ddpServerListener;
	private DDPServer ddpServer;
	private boolean enable;
	//
	private final int TYPE_REGISTER = 0X01;
	private final int TYPE_OFFLINE = 0X02;
	private final int TYPE_RECEIVED = 0x09;

	// private int port;
	private MessageSend messageSend;
	private Timer timer;

	public DDPServer(MessageSend messageSend) {
		this.messageSend = messageSend;
		// this.port = port;
		// super(port, Protocols.DDP);
		// enable = true;
		// setBufferSize(1024);
		ddpServer = this;
		// dsc = new DataServiceCenter();
		// dsc.setWorkMode(0);
		// dsc.selectProtocol(0);
		ddpListeners = new CopyOnWriteArrayList<IDDPListener>();
	}

	public void sendBuffer(String userId, byte[] buffer) {
		DDPData data = new DDPData(buffer);
		data.setUserId(userId);
		sendData(data);
	}

	public void sendData(DDPData data) {
		dsc.send(data.getUserId().getBytes(), data.getData(),
				data.getData().length, "");
		notifySentListener(data);
	}

	public int start(int port) {
		dsc = new DataServiceCenter();
		dsc.setWorkMode(0);
		dsc.selectProtocol(0);
		// try {
		// getLogger().info(dsc.start(0, getPort(), ""));
		int result = dsc.start(0, port, "");
		if (result == 0) {
			enable = true;
			ddpServerListener = new DDPServerListener();
			ddpServerListener.start();
			//
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new DoTask(), 1000, 5000);

		} else {
			log.error("DDP Server start Error");
		}
		// setStatus(ServerStatus.ONLINE);
		// } catch (Exception e) {
		// log.error("DDP Server start error", e);
		// setStatus(ServerStatus.OFFLINE);
		// }
		// notifyObjectChangedListener();
		return result;
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		//
		dsc.cancel_read_block();
		enable = false;
		// getLogger().info(dsc.stop(""));
		dsc.stop("");
		// setStatus(ServerStatus.OFFLINE);
		// notifyObjectChangedListener();
	}

	public void addDDPListener(IDDPListener ddpListener) {
		ddpListeners.add(ddpListener);
	}

	public void removeDDPListener(IDDPListener ddpListener) {
		ddpListeners.remove(ddpListener);
	}

	protected void notifyRegisteredListener(final DSCUser user) {
		Thread notify = new Thread() {
			public void run() {
				for (IDDPListener ddpListener : ddpListeners) {
					// ddpListener.heartbeat(ddpServer, user);
					ddpListener.heartbeat(user);
				}
			}
		};
		notify.setName("Notify DataReceivedListener Thread");
		notify.setDaemon(true);
		notify.start();
	}

	protected void notifyReceivedListener(final DDPData data) {
		Thread notify = new Thread() {
			public void run() {
				for (IDDPListener ddpListener : ddpListeners) {
					// ddpListener.received(ddpServer, data);
					ddpListener.received(data);
				}
			}
		};
		notify.setName("Notify DataReceivedListener Thread");
		notify.setDaemon(true);
		notify.start();
	}

	protected void notifySentListener(final DDPData data) {
		log.info("Send data:" + data.getUserId());
		// Thread notify = new Thread() {
		// public void run() {
		// for (IDDPListener ddpListener : ddpListeners) {
		// // ddpListener.sent(ddpServer, data);
		// }
		// }
		// };
		// notify.setName("Notify DataSendingListener Thread");
		// notify.setDaemon(true);
		// notify.start();
	}

	public class DDPServerListener extends Thread {

		public DDPServerListener() {
			// setName("DDP Server Thread(" + getAddress() + ":"
			// + Integer.toString(getPort()) + ")");
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				DSCData dscData = new DSCData();
				int readEnd = -1;
				String address = "0.0.0.0";
				int port = 0;

				while (enable) {
					if ((readEnd = dsc.read(dscData, null, false)) >= 0) {
						// getLogger().info(readEnd);
						// log.info(readEnd);
						// test();
						if (dscData.getType() == TYPE_REGISTER
								|| dscData.getType() == TYPE_OFFLINE) {
							DSCUser user = new DSCUser();
							dsc.getUser(dscData.getUserId().getBytes(), user);
							address = user.getAddress();
							port = user.getPort();
							notifyRegisteredListener(user);
							// log.info(user.getUserId());
							// log.info(user.getStatus());
						} else if (dscData.getType() == TYPE_RECEIVED) {
							sendBuffer(dscData.getUserId(), dscData.getBuffer());
							DDPData data = new DDPData(dscData.getBuffer());
							data.setUserId(dscData.getUserId());
							data.setAddress(address);
							data.setPort(port);
							notifyReceivedListener(data);
						}
					}
					yield();
				}
			} catch (Exception e) {
				log.error("DDP Server listening error", e);
			}
		}
	}

	class DoTask extends TimerTask {
		private DSCUser user;
		private int maxDTUAmount;
		private final int OFFLINE_TIME = 40;

		public DoTask() {
			user = new DSCUser();
			maxDTUAmount = dsc.get_max_user_amount();
		}

		@Override
		public void run() {
			// log.info("Run task");
			for (int i = 0; i < maxDTUAmount; i++) {
				dsc.get_user_at(i, user);
				if (user.getStatus() == 1) {
					// long interTime = (new Date().getTime() - getDate(
					// user.getUpdateDate()).getTime()) / 1000;
					long interTime = (new Date().getTime() - new Date(user
							.getUpdateDate()).getTime()) / 1000;
					if (interTime > OFFLINE_TIME + 5) {
						log.info("DTU:Over time");
						dsc
								.do_close_one_user(user.getUserId().getBytes(),
										null);
						GPRSEntity gprsEntity = new GPRSEntity();
						gprsEntity.setUserId(user.getUserId());
						gprsEntity.setInternateAddress(user.getAddress());
						gprsEntity.setPort(user.getPort());
						gprsEntity.setLocalAddress(user.getLocalAddress());
						gprsEntity.setLocalPort(user.getLocalPort());
						gprsEntity.setLogonDate(user.getLogonDate());
						gprsEntity.setStatus(0);

						messageSend.sendObjectMessage(0, gprsEntity,
								MessageNoConstants.GPRSMESSAGE, user
										.getUserId(), "");
					}
				}
			}
		}

		private Date getDate(String dateStr) {
			Date date = null;
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = format.parse(dateStr);
			} catch (ParseException e) {
				log.error(e);
				return null;
			}
			return date;
		}
	}

	private void test() {
		int maxU = dsc.get_max_user_amount();
		DSCUser user = new DSCUser();
		for (int i = 0; i < maxU; i++) {
			dsc.get_user_at(i, user);
			if (user.getStatus() == 1) {
				user.getUpdateDate();
				Date d = new Date(user.getUpdateDate());
			}
		}
	}
}
