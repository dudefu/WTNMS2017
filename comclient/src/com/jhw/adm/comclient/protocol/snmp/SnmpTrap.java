package com.jhw.adm.comclient.protocol.snmp;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import com.jhw.adm.comclient.system.Configuration;

/**
 * 
 * @author xiongbo
 * 
 *         The Class handle Trap operation
 * 
 */
// public class SnmpTrap extends Observable implements CommandResponder, Trap {
public class SnmpTrap implements CommandResponder, Trap {
	private static Logger log = Logger.getLogger(SnmpTrap.class);
	// initialize Log4J logging

	// static {
	// LogFactory.setLogFactory(new Log4jLogFactory());
	// BER.setCheckSequenceLength(false);
	// }

	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress = GenericAddress.parse(System.getProperty(
			"snmp4j.listenAddress", "udp:0.0.0.0/" + Configuration.trap_port));
	private ThreadPool threadPool;
	private Monitor monitor;
	private TransportMapping transport;

	// public Trap()
	// {
	// BasicConfigurator.configure();
	// }

	// public SnmpTrap(String ip, int port) {
	// if (ip != null && !"".equalsIgnoreCase(ip.trim()) && port != 0) {
	// listenAddress = new UdpAddress(ip + "/" + port);
	// }
	// }
	//
	// public SnmpTrap(String ip) {
	// if (ip != null && !"".equalsIgnoreCase(ip.trim())) {
	// listenAddress = new UdpAddress(ip + "/" + Constants.SNMP_TRAP_PORT);
	// }
	// }

	private void init() throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool,
				new MessageDispatcherImpl());

		// listenAddress =
		// GenericAddress.parse(System.getProperty("snmp4j.listenAddress",
		// "udp:0.0.0.0/162"));

		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping(
					(UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping(
					(TcpAddress) listenAddress);
		}
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3
				.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();
	}

	public void start() {
		try {
			init();
			snmp.addCommandResponder(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stop() {
		try {
			snmp.removeNotificationListener(listenAddress);
			snmp.removeTransportMapping(transport);
			snmp.removeCommandResponder(this);
			snmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPdu(CommandResponderEvent e) {
		// deliver trap event
		monitor.handleTrap(e);
	}

	// public Address getListenAddress()
	// {
	// return listenAddress;
	// }
	//
	// public void setListenAddress(Address listenAddress)
	// {
	// this.listenAddress = listenAddress;
	// }

	@Override
	public void deliverTrap(Monitor monitor) {
		this.monitor = monitor;

	}

	public static void main(String[] args) {

		SnmpTrap snmpTrap = new SnmpTrap();
		// snmpTrap.setListenAddress(GenericAddress.parse(System.getProperty("snmp4j.listenAddress",
		// "udp:0.0.0.0/162")));
		// snmpTrap.setListenAddress(new UdpAddress("0.0.0.0/162"));
		snmpTrap.start();

	}
}
