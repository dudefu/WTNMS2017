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

package com.jhw.adm.comclient.protocol.icmp;

import com.jhw.adm.comclient.protocol.ip.OC16ChecksumProducer;

/**
 * The ping packet for discovery
 * 
 * @author <A HREF="mailto:weave@opennms.org">Brian Weaver</A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 * 
 * @version 1.1.1.1
 * 
 */
public final class Packet extends ICMPHeader {
	/**
	 * Unique named padding that is placed in front of the incremental padding.
	 */
	private static final byte NAMED_PAD[] = { (byte) 'O', (byte) 'p',
			(byte) 'e', (byte) 'n', (byte) 'N', (byte) 'M', (byte) 'S',
			(byte) '!' };

	/**
	 * Timestamp when packet was sent
	 */
	private long m_sent;

	/**
	 * Timestamp of when packet was received.
	 */
	private long m_recv;

	/**
	 * The thread id of the sender. Effective key for the packet.
	 */
	private long m_tid; // thread id

	/**
	 * Padding used to make the packet conform to the defacto unix ping program
	 * (56 bytes).
	 */
	private byte[] m_pad;

	/**
	 * This is the amount of padding required to make the ICMP echo request 56
	 * bytes in length. This is just following the available code for ping ;)
	 */
	private static final int PAD_SIZE = 24;

	/**
	 * Converts a byte to a long and wraps the value to avoid sign extension.
	 * The method essentially treats the value of 'b' as an 8-bit unsigned value
	 * for conversion purposes.
	 * 
	 * @param b
	 *            The byte to convert.
	 * 
	 * @return The converted long value.
	 */
	static private long byteToLong(byte b) {
		long r = (long) b;
		if (r < 0)
			r += 256;
		return r;
	}

	/**
	 * Private constructor to disallow default construction of an object.
	 * 
	 * @exception java.lang.UnsupportedOperationException
	 *                Always thrown.
	 */
	private Packet() {
		throw new java.lang.UnsupportedOperationException(
				"illegal constructor call");
	}

	/**
	 * Creates a new discovery ping packet that can be sent to a remote protocol
	 * stack. The ICMP type is set to an Echo Request. The next sequence in the
	 * ICMPHeader base class is set and the sent time is set to the current
	 * time.
	 * 
	 * @param tid
	 *            The thread id for the packet.
	 * 
	 * @see java.lang.System#currentTimeMillis
	 */
	public Packet(long tid, int datasize) {
		super(ICMPHeader.TYPE_ECHO_REQUEST, (byte) 0);
		setNextSequenceId();

		m_recv = 0;
		m_tid = tid;

		if (datasize <= 0)
			datasize = PAD_SIZE;

		m_pad = new byte[datasize];
		for (int x = 0; x < NAMED_PAD.length && x < PAD_SIZE; x++)
			m_pad[x] = NAMED_PAD[x];
		for (int x = NAMED_PAD.length; x < PAD_SIZE; x++)
			m_pad[x] = (byte) x;

		m_sent = System.currentTimeMillis();
	}

	/**
	 * Creates a new discovery ping packet from the passed buffer.
	 * 
	 * @param buf
	 *            The buffer containing a refected ping packet.
	 */
	public Packet(byte[] buf) {
		m_pad = null; // ensure loadFromBuffer allocates pad
		loadFromBuffer(buf, 0);
	}

	/**
	 * Returns the time the packet was sent.
	 */
	public long getSentTime() {
		return m_sent;
	}

	/**
	 * Sets the sent time to the current time.
	 * 
	 * @see java.lang.System#currentTimeMillis
	 */
	public long setSentTime() {
		m_sent = System.currentTimeMillis();
		return m_sent;
	}

	/**
	 * Sets the sent time to the passed value.
	 * 
	 * @param time
	 *            The new sent time.
	 */
	public void setSentTime(long time) {
		m_sent = time;
	}

	/**
	 * Gets the currently set received time.
	 */
	public long getReceivedTime() {
		return m_recv;
	}

	/**
	 * Sets the recieved time for the packet.
	 * 
	 * @see java.lang.System#currentTimeMillis
	 */
	public long setReceivedTime() {
		m_recv = System.currentTimeMillis();
		return m_recv;
	}

	/**
	 * Sets the received time to the passed value.
	 * 
	 * @param time
	 *            The new received time.
	 * 
	 */
	public void setReceivedTime(long time) {
		m_recv = time;
	}

	/**
	 * Returns the network size for this packet. This is a combination of the
	 * headers size, plus the padding, and the IPHeader size.
	 */
	public static int getNetworkSize() {
		return (ICMPHeader.getNetworkSize() + 24 + PAD_SIZE);
	}

	/**
	 * Computes and stores the current checksum based upon the data currently
	 * contained in the object.
	 */
	public void computeChecksum() {
		OC16ChecksumProducer summer = new OC16ChecksumProducer();

		super.computeChecksum(summer);
		summer.add(m_sent);
		summer.add(m_recv);
		summer.add(m_tid);

		//
		// do all the elements combining two bytes
		// into a single short.
		//
		int stop = m_pad.length - (m_pad.length % 2);
		for (int i = 0; i < stop; i += 2)
			summer.add(m_pad[i], m_pad[i + 1]);

		//
		// take care of any stray bytes
		//
		if ((m_pad.length % 1) == 1)
			summer.add(m_pad[m_pad.length]);

		//
		// set the checksum in the header
		//
		super.setChecksum(summer.getChecksum());
	}

	/**
	 * Returns the currently set Thread ID
	 */
	public long getTID() {
		return m_tid;
	}

	/**
	 * Sets the current Thread Id
	 */
	public void setTID(long tid) {
		m_tid = tid;
	}

	/**
	 * Loads the data from the passed buffer into the current object. Once
	 * loaded the object's values should reflect the contents of the buffer.
	 * 
	 * @param buf
	 *            The buffer to load from
	 * @param offset
	 *            The offset to begin loading from
	 * 
	 * @return The offset of the next byte of data that was not used to
	 *         initialize this object.
	 * 
	 * @exception java.lang.IndexOutOfBoundsException
	 *                Thrown if there is not enough data contained in the buffer
	 *                to sufficent set the state of the object
	 * 
	 */
	public int loadFromBuffer(byte[] buf, int offset) {
		if ((buf.length - offset) < getNetworkSize())
			throw new IndexOutOfBoundsException("Insufficient Data");

		offset = super.loadFromBuffer(buf, offset);
		if (!isEchoReply() && !isEchoRequest())
			throw new IllegalArgumentException(
					"Invalid type, must be echo request/reply packet");

		m_sent = 0;
		for (int x = 0; x < 8; x++) {
			m_sent <<= 8;
			m_sent |= byteToLong(buf[offset++]);
		}
		m_recv = 0;
		for (int x = 0; x < 8; x++) {
			m_recv <<= 8;
			m_recv |= byteToLong(buf[offset++]);
		}
		m_tid = 0;
		for (int x = 0; x < 8; x++) {
			m_tid <<= 8;
			m_tid |= byteToLong(buf[offset++]);
		}

		if (m_pad == null)
			m_pad = new byte[PAD_SIZE];
		for (int x = 0; x < PAD_SIZE; x++)
			m_pad[x] = buf[offset++];

		return offset;
	}

	/**
	 * Writes the objects data out to the specified buffer at the starting
	 * offset. If the buffer does not have sufficent data to store the
	 * information then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param buf
	 *            The storage buffer.
	 * @param offst
	 *            The location to start in buf.
	 * 
	 * @return The new offset after storing to the buffer.
	 * 
	 * @exception IndexOutOfBoundsException
	 *                Thrown if the buffer does not have enough storage space.
	 * 
	 */
	public int storeToBuffer(byte[] buf, int offset) {
		if ((buf.length - offset) < getNetworkSize())
			throw new IndexOutOfBoundsException("Insufficient Buffer Size");

		offset = super.storeToBuffer(buf, offset);

		long t = m_sent;
		for (int x = 0; x < 8; x++) {
			buf[offset++] = (byte) (t >>> 56);
			t <<= 8;
		}

		t = m_recv;
		for (int x = 0; x < 8; x++) {
			buf[offset++] = (byte) (t >>> 56);
			t <<= 8;
		}

		t = m_tid;
		for (int x = 0; x < 8; x++) {
			buf[offset++] = (byte) (t >>> 56);
			t <<= 8;
		}

		for (int x = 0; x < PAD_SIZE; x++)
			buf[offset++] = m_pad[x];

		return offset;
	}

	/**
	 * Converts the object into an array of bytes which is suitable for
	 * transmission to remote hosts.
	 * 
	 * @return The object as an array of bytes.
	 */
	public byte[] toBytes() {
		byte[] buf = new byte[getNetworkSize()];
		storeToBuffer(buf, 0);
		return buf;
	}

} // end Packet.
