package com.jhw.adm.comclient.protocol.icmp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.protocol.ip.RawSocket;

/**
 * 
 * @author xiongbo
 * 
 */
public class IcmpPing {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public static final int ECHO = 1;
	public static final int TIMEOUT = 0;
	public static final int ERROR = -1;

	/**
	 * The filter identifier for the ping reply receiver
	 */
	private static final short FILTER_ID = (short) (new java.util.Random(System
			.currentTimeMillis())).nextInt();

	/**
	 * The sequence number for pings
	 */
	private static short seqid = (short) 0xbabe;

	/**
	 * Builds a datagram compatable with the ping ReplyReceiver class.
	 */
	private synchronized DatagramPacket getDatagram(InetAddress addr, long tid,
			int datasize) {
		Packet iPkt = new Packet(tid, datasize);
		iPkt.setIdentity(FILTER_ID);
		iPkt.setSequenceId(seqid++);
		iPkt.computeChecksum();

		byte[] data = iPkt.toBytes();
		return new DatagramPacket(data, data.length, addr, 0);
	}

	// result的长度为retrys。
	public int ping(String IP, int retrys, int timeOut, int datasize,
			ArrayList result) {
		RawSocket rawSocket = null;
		ReceiveReply receiveReply = null;
		ArrayList replys = new ArrayList();
		int re = TIMEOUT;
		try {
			if (logger.isDebugEnabled())
				logger.debug("Open raw socket!");
			rawSocket = new RawSocket();
			rawSocket.openRawSocket();

			if (logger.isDebugEnabled())
				logger.debug("Start receive reply!");
			receiveReply = new ReceiveReply(rawSocket, replys, FILTER_ID,
					timeOut, true);
			receiveReply.start();

			if (result != null)
				result.add("Ping " + IP + ":");

			long tid = (long) Thread.currentThread().hashCode();
			InetAddress ipv4Addr = InetAddress.getByName(IP);
			DatagramPacket pkt = getDatagram(ipv4Addr, tid, datasize);

			for (int i = 0; i < retrys; i++) {
				ArrayList replybak = null;
				if (logger.isDebugEnabled())
					logger.debug("Send icmp request package.");

				rawSocket.send(pkt);
				synchronized (replys) {
					if (replys.isEmpty())
						replys.wait(timeOut);

					replybak = (ArrayList) replys.clone();
					replys.clear();
				}

				if (logger.isDebugEnabled())
					logger.debug("Receive icmp echo package number:"
							+ replybak.size());

				Iterator ireplys = replybak.iterator();
				while (ireplys.hasNext()) {
					Reply reply = (Reply) ireplys.next();
					if (reply.getAddress().getHostAddress().equals(IP)) {
						re = ECHO;
						if (result != null)
							result.add("Reply from "
									+ IP
									+ ": Time="
									+ String.valueOf(reply.getPacket()
											.getReceivedTime()
											- reply.getPacket().getSentTime()));
						break;
					}
				}

				if (result == null) {
					if (re == ECHO)
						break;
				} else {
					if (re != ECHO)
						result.add("Request time out.");
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
			re = ERROR;
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
			re = ERROR;
		}
		if (receiveReply != null)
			receiveReply.stop();
		if (rawSocket != null)
			rawSocket.close();
		return re;
	}

	// 执行简单的Ping 操作，不返回操作结果。
	public int simplePing(String IP, int retrys, int timeOut) {
		int re = this.ping(IP, retrys, timeOut, 32, null);
		return re;
	}

	// 最多支持同时Ping 256个设备,重复次数3次,间隔step最好是8的倍数。
	public int ping(String[] IP, int step, int retrys, int timeout) {
		RawSocket rawSocket = null;
		ReceiveReply receiveReply = null;
		ArrayList replys = new ArrayList();
		TreeSet echoIP = new TreeSet();
		int re = TIMEOUT;
		try {
			rawSocket = new RawSocket();
			rawSocket.openRawSocket();
			receiveReply = new ReceiveReply(rawSocket, replys, FILTER_ID,
					timeout, false);
			receiveReply.start();

			long tid = (long) Thread.currentThread().hashCode();

			for (int i = 0; i < retrys; i++) {
				int num = 0;
				while (true) {
					if (num >= IP.length)
						break;

					int startNum = num; // add by liuxw, 2005-06-13
					for (int j = 0; j < step; j++) {
						if (num >= IP.length)
							break;
						// add by liuxw, 2005-06-13, 只有不为空的IP才发请求
						if (!IP[num].equals("")) {
							InetAddress ipv4Addr = InetAddress
									.getByName(IP[num]);
							DatagramPacket pkt = getDatagram(ipv4Addr, tid, 32);
							rawSocket.send(pkt);
							Thread.sleep(100); // add by liuxw
						}
						num++;
					}
					ArrayList replybak = null;
					synchronized (replys) {
						if (replys.isEmpty())
							replys.wait(timeout);

						replybak = (ArrayList) replys.clone();
						replys.clear();
					}

					Iterator ireplys = replybak.iterator();
					while (ireplys.hasNext()) {
						Reply reply = (Reply) ireplys.next();
						// add by liuxw, 2005-6-13, 把已经有回复的IP地址改为空
						for (int j = 0; j < step; j++) {
							if (startNum + j >= IP.length)
								break;
							if (IP[startNum + j].equals(reply.getAddress()
									.getHostAddress())) {
								IP[startNum + j] = "";
								break;
							}
						}
						if (!echoIP.contains(reply.getAddress()
								.getHostAddress())) {
							echoIP.add(reply.getAddress().getHostAddress());
							// System.out.println("********* Reply:" +
							// reply.getAddress().getHostAddress());//info
						}
					}
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
			re = ERROR;
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
			re = ERROR;
		}
		if (receiveReply != null)
			receiveReply.stop();
		if (rawSocket != null)
			rawSocket.close();

		if (echoIP.size() > IP.length)
			re = IP.length;
		else
			re = echoIP.size();

		Object[] obs = echoIP.toArray();
		for (int i = 0; i < re; i++) {
			IP[i] = (String) obs[i];
		}
		return re;
	}

	public static void main(String[] args) {
		IcmpPing ping = new IcmpPing();

		//System.out.println(System.getProperty("java.library.path"));

		// ArrayList re=new ArrayList();
		// long startTime=System.currentTimeMillis();
		// int echo=ping.ping("192.168.88.3",3,3000,32,null);
		// System.out.println(System.currentTimeMillis()-startTime + "毫秒");
		// System.out.println("return :"+echo);
		// Iterator iRe=re.iterator();
		// while(iRe.hasNext())
		// System.out.println((String)iRe.next());

		/*
		 * String [] ip=new String[]{ "10.20.10.1", "10.20.10.2", "10.20.10.3",
		 * "10.20.10.4", "10.20.10.5", "10.20.10.6", "10.20.10.7", "10.20.10.8",
		 * "10.20.10.9", "10.20.10.10", "10.20.10.11", "10.20.10.12",
		 * "10.20.10.13", "10.20.10.14", "10.20.10.15", "10.20.10.17",
		 * "10.20.10.18", "10.20.10.19", "10.20.10.20", "10.20.10.21",
		 * "10.20.10.22", "10.20.10.23", "10.20.10.24", "10.20.10.50",
		 * "10.20.10.51", "10.20.10.52", "10.20.10.53", "10.20.10.54",
		 * "10.20.10.55", "10.20.10.56", "10.20.10.57", "10.20.10.58",
		 * "10.20.10.59", "10.20.10.60" };
		 */
		// System.out.println("***********************************************************");
		String[] ip = new String[] { "192.168.88.36" };
		int renum = ping.ping(ip, 4, 3, 2000);
		// System.out.println("return: " + renum);
		for (int i = 0; i < renum; i++) {
			// System.out.println(ip[i]);
		}
	}
}
