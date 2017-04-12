package com.jhw.adm.comclient.protocol.icmp;

import java.net.DatagramPacket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.protocol.ip.RawSocket;

/**
 * 
 * @author xiongbo
 * 
 */

public class ReceiveReply implements Runnable {

	/**
	 * The thread's status
	 */
	private boolean status = false;
	private RawSocket rawSocket;
	private Thread worker = null;
	private int filterID;
	private ArrayList replys = null;
	private int timeout = 1000;
	private boolean isSyn = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public ReceiveReply(RawSocket rawSocket, ArrayList replys, int filterID,
			int timeout, boolean isSyn) {
		this.rawSocket = rawSocket;
		this.filterID = filterID;
		this.replys = replys;
		this.timeout = timeout;
		this.isSyn = isSyn;
	}

	protected void process(DatagramPacket pkt) throws InterruptedException {
		if (pkt == null)
			return;

		if (status) {
			Reply reply = null;
			try {
				reply = Reply.create(pkt); // create a reply
				if (reply == null)
					return;
			} catch (IllegalArgumentException iaE) {
				// logger.info(iaE.getMessage());
				return;
			} catch (IndexOutOfBoundsException iooB) {
				// logger.info(iooB.getMessage());
				return;
			}

			if (reply.isEchoReply() && reply.getIdentity() == filterID) {
				synchronized (replys) {
					replys.add(reply);
					if (isSyn)
						replys.notify();
				}
			}
		}
	}

	public void start() {
		worker = new Thread(this);
		worker.setDaemon(true);
		worker.start();
	}

	public void stop() {
		if (worker.isAlive()) {
			worker.interrupt();
		}
		this.status = false;
	}

	public void run() {
		status = true;
		try {
			for (;;) {
				if (status == false)
					break;
				process(rawSocket.receive(this.timeout));
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			status = false;
		}
	}

	public boolean isActive() {
		return status;
	}

}