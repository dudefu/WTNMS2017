package com.jhw.adm.comclient.carrier;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.serial.SerialServer;
import com.jhw.adm.comclient.util.PropertyFileUtil;

/**
 * 
 * @author xiongbo
 * 
 */
public class SerialSetService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private SerialServer serialServer;
	public static final String CONFIG_FILE = "conf/carrier-serial.ini";
	public static final String SERIAL_PORT = "SERIAL_PORT";
	public static final String PORT = "COM1";
	public static final String BAUD_RATE = "BAUD_RATE";
	public static final int BAUD = 9600;
	public static final String TEST_BIT = "TEST_BIT";
	public static final int TEST = 0;
	public static final String STOP_BIT = "STOP_BIT";
	public static final int STOP = 1;
	public static final String DATA_BIT = "DATA_BIT";
	public static final int DATA = 8;
	public static final String BUFFER_SIZE = "BUFFER_SIZE";
	public static final int BUFFER = 1024;

	public SerialSetService() {
		serialServer = SerialServer.getInstance();
	}

	public void writeSet(String port, int baud, int data, int stop, int test,
			int buffer) {
		Properties serialProperties = new Properties();
		serialProperties.setProperty(SERIAL_PORT, port);
		serialProperties.setProperty(BAUD_RATE, Integer.toString(baud));
		serialProperties.setProperty(DATA_BIT, Integer.toString(data));
		serialProperties.setProperty(STOP_BIT, Integer.toString(stop));
		serialProperties.setProperty(TEST_BIT, Integer.toString(test));
		serialProperties.setProperty(BUFFER_SIZE, Integer.toString(buffer));
		PropertyFileUtil.save(serialProperties, CONFIG_FILE,
				"serial port config");

		//
		serialServer.setBaudRate(baud);
		serialServer.setDataBits(data);
		serialServer.setStopBits(stop);
		serialServer.setParity(test);
		serialServer.setBufferSize(buffer);
		serialServer.setup();
	}

}
