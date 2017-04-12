package com.jhw.adm.comclient.protocol.tftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TftpRequest {
	private int timeout = 3;
	private int retries = 3;
	private InetAddress host = null;
	private int port = 69;
	private int operation = 1;
	private String fileName = "";
	private String mode = "";
	private DatagramSocket socket;
	private DatagramSocket serverSocket;

	private static final int STANDARD_TFTP_PORT = 69;
	public static final int STANDARD_BLOCK_SIZE = 512;

	public TftpRequest() {
		this(3, 3);
	}

	public TftpRequest(int timeout, int retries) {
		this.setTimeout(timeout);
		this.setRetries(retries);
	}

	// 创建TFTP客户端
	public void createClient() {
		this.setPort(69);
		this.setMode("octet");
	}

	// 创建TFTP服务端
	public boolean createServer() {
		try {
			serverSocket = new DatagramSocket(STANDARD_TFTP_PORT);
			serverSocket.setReceiveBufferSize(STANDARD_BLOCK_SIZE);
			DatagramPacket datagrampacket = new DatagramPacket(
					new byte[STANDARD_BLOCK_SIZE], STANDARD_BLOCK_SIZE);
			serverSocket.setSoTimeout(5000);
			serverSocket.receive(datagrampacket);
			serverSocket.close();

			TftpPacket tftppacket = TftpPacket.getPacket(datagrampacket);
			this.setHost(tftppacket.getAddress());
			this.setOperation(tftppacket.getOpCode());
			this.setFileName(tftppacket.getFileName());
			this.setMode(tftppacket.getMode());
			this.setPort(tftppacket.getPort());
		} catch (java.net.SocketTimeoutException e) {
			serverSocket.close();
			return false;
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	// public void close()
	// {
	// serverSocket.close();
	// }

	private class TftpOutputStream extends OutputStream {
		private byte buffer[];
		private int next;
		private int blockNumber;

		public TftpOutputStream() {
			buffer = new byte[STANDARD_BLOCK_SIZE];
			next = 0;
			blockNumber = 1;
		}

		public void write(int i) throws IOException {
			buffer[next++] = (byte) i;
			if (next == buffer.length) {
				sendAndGetAck(TftpPacket.getDataPacket(blockNumber++, buffer));
				next = 0;
			}
		}

		public void close() throws IOException {
			byte abyte0[] = new byte[next];
			System.arraycopy(buffer, 0, abyte0, 0, next);
			sendAndGetAck(TftpPacket.getDataPacket(blockNumber++, abyte0));
			socket.close();
		}
	}

	private class TftpInputStream extends InputStream {
		private byte buffer[];
		private int next;
		private int blockNumber;

		public TftpInputStream() throws IOException {
			next = 0;
			blockNumber = 1;
			buffer = sendAndGetAck(TftpPacket.getAckPacket(0)).getData();
		}

		public TftpInputStream(byte abyte0[]) throws IOException {
			next = 0;
			blockNumber = 1;
			buffer = abyte0;
		}

		public int read() throws IOException {
			if (next == buffer.length) {
				if (buffer.length < STANDARD_BLOCK_SIZE) {
					return -1;
				}
				buffer = sendAndGetAck(TftpPacket.getAckPacket(blockNumber++))
						.getData();
				next = 0;
				if (buffer.length == 0) {
					return -1;
				}
			}
			return buffer[next++] & 0xff;
		}

		public void close() throws IOException {
			socket.send(TftpPacket.getAckPacket(blockNumber).getDatagram());
			socket.close();
		}
	}

	// 设置超时
	public void setTimeout(int i) {
		timeout = i;
	}

	// 返回超时
	public int getTimeout() {
		return timeout;
	}

	// 设置重试次数
	public void setRetries(int i) {
		retries = i;
	}

	// 返回重试次数
	public int getRetries() {
		return retries;
	}

	// 设置tftp server的ip地址
	public void setHost(InetAddress inetaddress) {
		if (inetaddress == null) {
			// 向客户端发送"服务地址不能为空"的消息
		} else {
			host = inetaddress;
		}
	}

	// 返回tftp server的ip地址
	public InetAddress getHost() {
		return host;
	}

	// 设置操作模式
	public void setOperation(int i) {
		if (i != 1 && i != 2) {
			// 向客户端发送"无效操作模式 (" + i + ")"的消息
		} else {
			operation = i;
		}
	}

	// 返回操作模式
	public int getOperation() {
		return operation;
	}

	// 设置文件名
	public void setFileName(String s) {
		if (s == null) {
			// 向客户端发送"文件名不用为空"的消息
		} else {
			fileName = s;
		}
	}

	// 返回文件名
	public String getFileName() {
		return fileName;
	}

	// 设置传输模式
	public void setMode(String s) {
		if (s == null) {
			// 向客户端发送"传输模式不能为空"的消息
		} else {
			mode = s.toLowerCase();
		}
	}

	public void setPort(int i) {
		if (i < 1 || i > 65535) {
			// 向客户端发送"无效端口号 (" + i + ")"的消息
		} else {
			port = i;
		}
	}

	// 返回传输模式
	public String getMode() {
		return mode;
	}

	public InputStream getInputStream() throws IOException {
		if (host == null) {
			// 向客户端发送"没有指定服务地址"的消息
		}
		if (fileName == null) {
			// 向客户端发送"没有指定文件名"的消息
		}
		if (operation != 1 && operation != 2) {
			// 向客户端发送"无效操作模式 (" + operation + ")"的消息
		}
		socket = new DatagramSocket();
		socket.setSendBufferSize(STANDARD_BLOCK_SIZE + 4);
		socket.setReceiveBufferSize(STANDARD_BLOCK_SIZE + 4);
		if (operation == 1) {
			TftpPacket tftppacket = TftpPacket.getRrqPacket(fileName, mode);
			tftppacket = sendAndGetAck(tftppacket);
			port = tftppacket.getPort();
			socket.connect(host, port);
			return new TftpInputStream(tftppacket.getData());
		}
		socket.connect(host, port);
		TftpPacket tftppacket1 = TftpPacket.getAckPacket(0);
		tftppacket1 = sendAndGetAck(tftppacket1);
		return new TftpInputStream(tftppacket1.getData());
	}

	public OutputStream getOutputStream() throws IOException {
		if (host == null) {
			// 向客户端发送"没有指定服务地址"的消息
		}
		if (fileName == null) {
			// 向客户端发送"没有指定文件名"的消息
		}
		if (operation != 1 && operation != 2) {
			// 向客户端发送"无效操作模式 (" + operation + ")"的消息
		}
		socket = new DatagramSocket();
		socket.setSendBufferSize(STANDARD_BLOCK_SIZE + 4);
		socket.setReceiveBufferSize(STANDARD_BLOCK_SIZE);
		if (operation == 2) {
			TftpPacket tftppacket = TftpPacket.getWrqPacket(fileName, mode);
			tftppacket = sendAndGetAck(tftppacket);
			port = tftppacket.getPort();
			socket.connect(host, port);
			return new TftpOutputStream();
		}
		socket.connect(host, port);
		return new TftpOutputStream();
	}

	private TftpPacket sendAndGetAck(TftpPacket tftppacket) throws IOException {
		long l = getTimeout() * 1000;
		int i = socket.getReceiveBufferSize();
		DatagramPacket datagrampacket = new DatagramPacket(new byte[i], i);
		DatagramPacket datagrampacket1 = tftppacket.getDatagram();
		int j = tftppacket.getOpCode();
		if (j == 1 || j == 2) {
			datagrampacket1.setAddress(host);
			datagrampacket1.setPort(port);
		}
		int k = 0;
		while (true) {
			socket.send(datagrampacket1);
			k++;
			long l1 = System.currentTimeMillis();
			try {
				TftpPacket tftppacket1;
				long l2 = System.currentTimeMillis() - l1;
				if (l2 > l) {
					throw new InterruptedIOException();
				}
				socket.setSoTimeout((int) (l - l2));
				socket.receive(datagrampacket);
				tftppacket1 = TftpPacket.getPacket(datagrampacket);
				switch (tftppacket1.getOpCode()) {
				default:
					break;

				case 3:
					if (j == 1 && tftppacket1.getBlockNumber() == 1) {
						return tftppacket1;
					}
					if (j == 4
							&& tftppacket1.getBlockNumber() == tftppacket
									.getBlockNumber() + 1) {
						return tftppacket1;
					}
					break;

				case 4:
					if (j == 2 && tftppacket1.getBlockNumber() == 0) {
						return tftppacket1;
					}
					if (j == 3
							&& tftppacket1.getBlockNumber() == tftppacket
									.getBlockNumber()) {
						return tftppacket1;
					}
					break;

				case 5:
					// 发送错误码
					// throw new TftpException(tftppacket1.getErrorCode(),
					// tftppacket1.getErrorMsg());

				}
				return tftppacket1;
			} catch (InterruptedIOException ex) {
				if (k > retries) {
					// 发送"连接超时"消息
					// throw new TftpException("连接超时:" + ex.getMessage());
				}
			}
		}
	}

}
