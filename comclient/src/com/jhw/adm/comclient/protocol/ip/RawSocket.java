//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//	Brian Weaver	<weave@opennms.org>
//	http://www.opennms.org/
//
//
// Tab Size = 8
//
//
package com.jhw.adm.comclient.protocol.ip;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * This class provides a bridge between the host operating system so that ICMP
 * messages may be sent and received.
 * 
 * @author <a href="mailto:weave@opennms.org">Brian Weaver</a>
 * @author <a href="http://www.opennms.org/">OpenNMS</a>
 * 
 */
public class RawSocket {
	/**
	 * This instance is used by the native code to save and store file
	 * descriptor information about the icmp socket. This needs to be
	 * constructed prior to calling the init method, preferable in the
	 * constructor.
	 */
	private FileDescriptor m_rawFd;

	/**
	 * This method is used to open the initial operating system icmp socket. The
	 * descriptor for the socket is stored in the member m_rawFd.
	 * 
	 * @throws java.io.IOException
	 *             This is thrown if an error occurs opening the ICMP socket.
	 */
	private native void initSocket() throws IOException;

	// Load the requried system library. This
	// should be delayed until the class is actually
	// loaded and used, so normally it should not
	// take unecessary resources from an application that
	// is not going to use ICMP.
	//
	static {
		System.loadLibrary("rawsocket");
	}

	/**
	 * Constructs a new socket that is able to send and receive ICMP messages.
	 * The newly constructed socket will receive all ICMP messages directed at
	 * the local machine. The application must be prepared to handle any and
	 * discard any non-interesting ICMP messages.
	 * 
	 * @exception java.io.IOException
	 *                This exception is thrown if the socket fails to be opened
	 *                correctly.
	 */
	public RawSocket() {
		m_rawFd = new FileDescriptor();
	}

	public void openRawSocket() throws IOException {
		initSocket();
	}

	/**
	 * This method is used to receive the next ICMP datagram from the operating
	 * system. The returned datagram packet's address is set to the sending
	 * host's address. The port number is always set to Zero, and the buffer is
	 * set to the contents of the raw ICMP message.
	 * 
	 * @exception java.io.IOException
	 *                Thrown if an error occurs reading the next ICMP message.
	 * 
	 */
	public native DatagramPacket receive(int timeout) throws IOException;

	/**
	 * This method is used to send the passed datagram using the ICMP transport.
	 * The destination of the datagram packet is used as the send to destination
	 * for the underlying ICMP socket. The port number of the datagram packet is
	 * ignored completely.
	 * 
	 * @exception java.io.IOException
	 *                Thrown if an error occurs sending the datagram to the
	 *                remote host.
	 * @exception java.net.NoRouteToHostException
	 *                Thrown if the destination address is a broadcast address.
	 */
	public native void send(DatagramPacket packet) throws IOException;

	/**
	 * This method is used to close and release the resources assocated with the
	 * instance. The file descriptor is closed at the operating system level and
	 * any subsequent calls to this instance should result in exceptions being
	 * generated.
	 */
	public native void close();
}
