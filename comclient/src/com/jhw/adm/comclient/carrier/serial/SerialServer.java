package com.jhw.adm.comclient.carrier.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.protoco.PacketFormatter;

/**
 * Send and Receive carrier command
 * 
 * @author xiongbo
 * 
 */
public class SerialServer {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private ExecutorService executorService;
	private CommPortIdentifier portIdentifier;
	private SerialPort serialPort;
	private List<SerialServerListener> serialServerListeners;
	private InputStream serialInputStream;
	private OutputStream serialOutputStream;
	private String portName;
	private int bufferSize;
	private int baudRate;
	private int dataBits;
	private int stopBits;
	private int parity;
	private int flowmode;
	public static final int Interval = 500;

	// private Logger log4jLogger;

	private SerialServer() {
		bufferSize = 1024;
		baudRate = 9600;
		dataBits = SerialPort.DATABITS_8;
		stopBits = SerialPort.STOPBITS_1;
		parity = SerialPort.PARITY_NONE;
		flowmode = SerialPort.FLOWCONTROL_NONE;
		// log4jLogger = Logger.getLogger(SerialServer.class);

		serialServerListeners = new CopyOnWriteArrayList<SerialServerListener>();
	}

	public boolean open(String portName) {
		portIdentifier = null;
		CommPort commPort = null;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		} catch (NoSuchPortException e) {
			// notifyExceptionCaught(e);
			log.error("Open Serial port " + portName + " Fial");
			return false;
		}
		if (portIdentifier.isCurrentlyOwned()) {
			Exception ex = new Exception("Error: Port(" + portName
					+ ") is currently in use");
			notifyExceptionCaught(ex);
		} else {
			try {
				commPort = portIdentifier.open(getClass().getName(), 2000);
			} catch (PortInUseException e) {
				notifyExceptionCaught(e);
			}

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				setup();
				log.info(String.format("Serial port(%s) opened...", portName));
				executorService = Executors.newSingleThreadExecutor();
				setPortName(portName);

				try {
					serialInputStream = serialPort.getInputStream();
					serialOutputStream = serialPort.getOutputStream();
				} catch (IOException e) {
					notifyExceptionCaught(e);
				}

				try {
					serialPort.addEventListener(new SerialReader(
							serialInputStream));
				} catch (TooManyListenersException e) {
					notifyExceptionCaught(e);
				}
				serialPort.notifyOnDataAvailable(true);

			} else {
				notifyExceptionCaught(new Exception(
						"Error: Only serial ports are handled by this server."));
			}
		}
		return true;
	}

	public void setup() {
		if (serialPort != null) {
			try {
				serialPort.setInputBufferSize(getBufferSize());
				serialPort.setSerialPortParams(getBaudRate(), getDataBits(),
						getStopBits(), getParity());
			} catch (UnsupportedCommOperationException e) {
				notifyExceptionCaught(e);
			}
		}
	}

	public void close() {
		log.info(String.format("Serial port(%s) closed...", portName));
		if (executorService != null) {
			executorService.shutdown();
		}

		// TODO if execute those code, when reopen,the function is fail
		//

		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
		portIdentifier = null;
	}

	public static String[] getPortIdentifiers() {
		List<String> listOfPort = new ArrayList<String>();

		for (Enumeration identifiers = CommPortIdentifier.getPortIdentifiers(); identifiers
				.hasMoreElements();) {
			CommPortIdentifier identifier = (CommPortIdentifier) identifiers
					.nextElement();
			if (identifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				listOfPort.add(identifier.getName());
			}
		}

		String[] serialComms = new String[listOfPort.size()];
		listOfPort.toArray(serialComms);

		return serialComms;
	}

	public boolean isCurrentlyOwned() {
		return portIdentifier == null ? false : portIdentifier
				.isCurrentlyOwned();
	}

	public void addSerialServerListener(SerialServerListener listener) {
		if (listener != null) {
			serialServerListeners.add(listener);
		}
	}

	public boolean removeSerialServerListener(SerialServerListener listener) {
		if (listener != null) {
			return serialServerListeners.remove(listener);
		}

		return false;
	}

	private void notifyDataAvailable(final DataAvailableEvent event) {
		Thread notify = new Thread() {
			@Override
			public void run() {
				for (SerialServerListener listener : serialServerListeners) {
					if (listener == null)
						continue;
					listener.dataAvailable(event);
				}
			}
		};
		executorService.execute(notify);
		// TODO
		// notify.start();

		log.info("Response data: " + event.getHexString());
	}

	// protected void notifyDataSent(final DataSentEvent event) {
	// Thread notify = new Thread() {
	// @Override
	// public void run() {
	// for (SerialServerListener listener : serialServerListeners) {
	// if (listener == null)
	// continue;
	// listener.dataSent(event);
	// }
	// }
	// };
	// notify.setName("Notify Data Sent Thread");
	// executorService.execute(notify);
	// log.info("data sent: " + event.getHexString());
	// }

	protected void notifyExceptionCaught(final Throwable cause) {
		// Thread notify = new Thread() {
		// @Override
		// public void run() {
		// for (SerialServerListener listener : serialServerListeners) {
		// if (listener == null)
		// continue;
		// listener.exceptionCaught(cause);
		// }
		// }
		// };
		// notify.setName("Notify Exception Caught Thread");
		// notify.start();
		log.info(cause);
	}

	public class SerialReader implements SerialPortEventListener {
		private InputStream in;
		private byte[] buffer = new byte[getBufferSize()];

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void serialEvent(SerialPortEvent event) {
			int data;

			switch (event.getEventType()) {
			case SerialPortEvent.BI:/* Break interrupt,通讯中断 */
			case SerialPortEvent.OE:/* Overrun error，溢位错误 */
			case SerialPortEvent.FE:/* Framing error，传帧错误 */
			case SerialPortEvent.PE:/* Parity error，校验错误 */
			case SerialPortEvent.CD:/* Carrier detect，载波检测 */
			case SerialPortEvent.CTS:/* Clear to send，清除发送 */
			case SerialPortEvent.DSR:/* Data set ready，数据设备就绪 */
			case SerialPortEvent.RI:/* Ring indicator，响铃指示 */
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/* 输出缓冲区清空 */
				notifyExceptionCaught(new Exception("SerialPortEvent: "
						+ Integer.toString(event.getEventType())));
				break;
			case SerialPortEvent.DATA_AVAILABLE:/* 端口有可用数据。读到缓冲数组，输出到终端 */
				try {
					int len = 0;
					while ((data = in.read()) > -1) {
						buffer[len++] = (byte) data;
					}

					if (buffer.length > 2) {
						for (int i = 0; i < len; i++) {
							if (buffer[i] == PacketFormatter.HEAD_VALUE
									&& buffer[i + 1] == PacketFormatter.HEAD_VALUE) {
								int theLen = len - i;
								byte[] available = new byte[theLen];
								System.arraycopy(buffer, i, available, 0,
										theLen);
								dataAvailable(available);
								break;
							}
						}
					}
				} catch (IOException e) {
					notifyExceptionCaught(e);
				}
				break;
			}
		}
	}

	private void dataAvailable(byte[] available) {
		if (available.length >= PacketFormatter.MIN_LEN) {

			if ((available[PacketFormatter.HEAD_INDEX1]) == PacketFormatter.HEAD_VALUE
					&& (available[PacketFormatter.HEAD_INDEX2]) == PacketFormatter.HEAD_VALUE) {
				int expectBodyLength = (available[PacketFormatter.BODY_LEN_INDEX1] & 0xFF << 8)
						+ available[PacketFormatter.BODY_ILEN_NDEX2] & 0xFF;
				int packetLen = expectBodyLength + PacketFormatter.MIN_LEN;

				if (packetLen == available.length) {
					System.arraycopy(available, 0, available, 0,
							available.length);
					onDataAvailable(available);
				}

				if (packetLen < available.length) {
					int offset = 0;
					byte[] packet = new byte[packetLen];
					System.arraycopy(available, offset, packet, 0, packetLen);
					onDataAvailable(packet);

					int nextLen = available.length - packetLen;
					byte[] next = new byte[nextLen];
					System.arraycopy(available, packetLen, next, 0, nextLen);
					dataAvailable(next);
				}
			}
		}
	}

	private void onDataAvailable(byte[] available) {
		// TODO
		String hexString = StringUtil.toHexString(available);
		DataAvailableEvent dataAvailableEvent = new DataAvailableEvent();
		dataAvailableEvent.setDate(new Date());
		dataAvailableEvent.setPortName(getPortName());
		dataAvailableEvent.setData(available);
		dataAvailableEvent.setHexString(hexString);

		notifyDataAvailable(dataAvailableEvent);
	}

	public void sendData(final byte[] data) {
		try {
			if (serialOutputStream == null)
				return;
			serialOutputStream.write(data);
			// TODO
			serialOutputStream.flush();

			// TODO
			// String hexString = StringUtil.toHexString(data);
			// DataSentEvent dataSentEvent = new DataSentEvent();
			// dataSentEvent.setDate(new Date());
			// dataSentEvent.setPortName(getPortName());
			// dataSentEvent.setData(data);
			// dataSentEvent.setHexString(hexString);

			// notifyDataSent(dataSentEvent);

			log.info("Data have sent: " + StringUtil.toHexString(data));
		} catch (IOException e) {
			notifyExceptionCaught(e);
		} catch (NullPointerException e) {
			notifyExceptionCaught(e);
		}
	}

	public void sendAsync(final byte[] data) {
		Thread async = new Thread() {
			@Override
			public void run() {
				sendData(data);
				delay(Interval);
			}
		};
		// TODO
		if (executorService != null) {
			executorService.execute(async);
		}
	}

	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			notifyExceptionCaught(e);
		}
	}

	public static SerialServer getInstance() {
		return LazyHolder.instance;
	}

	private static class LazyHolder {
		private static SerialServer instance = new SerialServer();
	}

	// protected Logger getLogger() {
	// return log4jLogger;
	// }

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public int getFlowmode() {
		return flowmode;
	}

	public void setFlowmode(int flowmode) {
		this.flowmode = flowmode;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	// public ExecutorService getExecutorService() {
	// return executorService;
	// }
}