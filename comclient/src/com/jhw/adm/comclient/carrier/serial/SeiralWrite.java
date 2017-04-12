package com.jhw.adm.comclient.carrier.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class SeiralWrite {

	/* ���ϵͳ�п��õ�ͨѶ�˿��� */
	private static CommPortIdentifier portId;
	/* Enumeration Ϊö������,��util�� */
	private static Enumeration portList;
	private OutputStream outputStream;
	/* RS-232�Ĵ��п� */
	private SerialPort serialPort;

	private byte data[] = new byte[10240];

	/*
	 * public SeiralWrite(){ portList = CommPortIdentifier.getPortIdentifiers();
	 * while (portList.hasMoreElements()) { portId = (CommPortIdentifier)
	 * portList.nextElement(); System.out.println(portId.getName()); if
	 * (portId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
	 * portId.getName().equals(SerialLC.comNum)) { try { serialPort =
	 * (SerialPort)portId.open("GH_ZAIBO_App", 2000); } catch
	 * (PortInUseException e) {e.getMessage();} try { outputStream =
	 * serialPort.getOutputStream(); } catch (IOException e) {} } } }
	 */

	public SeiralWrite(SerialPort serialPort) {
		this.serialPort = serialPort;
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
		}
	}

	public boolean setPortParams(int bout, int dataBit, int stopBit, int parity) {
		try {
			serialPort.setSerialPortParams(bout, dataBit, stopBit, parity);
			return true;
		} catch (UnsupportedCommOperationException e) {
			return false;
		}
	}

	public boolean setPortParams() {
		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			return true;
		} catch (UnsupportedCommOperationException e) {
			return false;
		}
	}

	public boolean sendInfo(byte data[]) {
		// setPortParams();
		this.data = data;
		printfWriteData(this.data);
		try {
			outputStream.write(this.data);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean sendInfo(String strData) {
		// setPortParams();
		this.data = strData.getBytes();
		try {
			outputStream.write(this.data);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void printfWriteData(byte data[]) {
		for (int i = 0; i < data.length; i++)
			System.out.print(data[i] + ":");
		System.out.println("");
	}

}
