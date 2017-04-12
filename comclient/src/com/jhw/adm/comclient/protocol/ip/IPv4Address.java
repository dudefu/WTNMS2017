//
//
//    jSNMP - SNMPv1 & v2 Compliant Libraries for Java
//    Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
//    You can contact Oculan Corp. at:
//
//    email:  shaneo@opennms.org
//    snail mail:  Oculan Corp.
//                 4910 Waters Edge Drive, Suite 101
//                 Raleigh, N.C.  27606
//
//
//

package com.jhw.adm.comclient.protocol.ip;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.StringTokenizer;

/**
 * Represents an Internet Protocol version 4 address. An IPv4 address is a
 * 32-bit address that can be considered four eight bit octets. Each octet
 * represents a number in the range of [0..256). A string representation is a
 * dotted decimal address in the form of "xxx.xxx.xxx.xxx" where xxx is a single
 * octet.
 * 
 * The main purpose of the class is to represent an IPv4 Address without the
 * associated lookup baggage in the java.net.InetAddress class.
 * 
 * 
 * @author Brian Weaver &lt;weave@opennms.org&gt;
 * @revision 1.1.1.1
 * 
 */
public class IPv4Address extends Object implements Serializable {
	static final long serialVersionUID = 1946711645921732057L; // generated by
	// serialver
	// tool

	private byte[] m_addr; // 4 byte address

	/**
	 * Duplicates the array of bytes.
	 * 
	 * @param src
	 *            The source bytes to duplicate.
	 * 
	 * @return The duplicated array of bytes.
	 */
	private byte[] dup(byte[] src) {
		byte[] cpy = null;
		if (src != null) {
			cpy = new byte[src.length];
			for (int x = 0; x < src.length; x++) {
				cpy[x] = src[x];
			}
		}
		return cpy;
	}

	/**
	 * Converts a byte to an integer, treating the byte as unsigned.
	 * 
	 * @param b
	 *            The byte to convert.
	 * 
	 * @return The converted value.
	 * 
	 */
	private static int byteToInt(byte b) {
		int r = (int) b;
		if (r < 0)
			r += 256;
		return r;
	}

	/**
	 * Converts the passed 32-bit IPv4 address to a dotted decimal IP address
	 * string.
	 * 
	 * @param ipv4Addr
	 *            The 32-bit address
	 * 
	 * @return The dotted decimal address in the format "xxx.xxx.xxx.xxx" where
	 *         0 <= xxx < 256
	 * 
	 */
	public static String addressToString(int ipv4Addr) {
		StringBuffer buf = new StringBuffer();
		buf.append((ipv4Addr >> 24) & 0xff);
		buf.append('.');
		buf.append((ipv4Addr >> 16) & 0xff);
		buf.append('.');
		buf.append((ipv4Addr >> 8) & 0xff);
		buf.append('.');
		buf.append(ipv4Addr & 0xff);

		return buf.toString();
	}

	/**
	 * Converts the passed IPv4 address buffer to a dotted decimal IP address
	 * string.
	 * 
	 * @param buf
	 *            The 4 byte buffer
	 * 
	 * @return The dotted decimal address in the format "xxx.xxx.xxx.xxx" where
	 *         0 <= xxx < 256
	 * 
	 * @exception IllegalArgumentException
	 *                Thrown if the buffer is not exactly 4 bytes in length.
	 */
	public static String addressToString(byte[] buf) {
		if (buf.length != 4)
			throw new IllegalArgumentException(
					"IPv4 Address must be 4-bytes in length");

		int a = byteToInt(buf[0]);
		int b = byteToInt(buf[1]);
		int c = byteToInt(buf[2]);
		int d = byteToInt(buf[3]);

		StringBuffer sbuf = new StringBuffer();
		sbuf.append(a).append('.').append(b).append('.').append(c).append('.')
				.append(d);

		return sbuf.toString();
	}

	/**
	 * Constructs a new IPv4Address object. The default value for the object is
	 * "0.0.0.0"
	 * 
	 */
	public IPv4Address() {
		m_addr = new byte[4];
		m_addr[0] = 0;
		m_addr[1] = 0;
		m_addr[2] = 0;
		m_addr[3] = 0;
	}

	/**
	 * Constructs a new address object based upon the value of the first object.
	 * 
	 * @param second
	 *            The object to copy the address from.
	 */
	public IPv4Address(IPv4Address second) {
		m_addr = dup(second.m_addr);
	}

	/**
	 * Constructs a new object based on the value stored in the passed array. An
	 * IPv4 Address is 32-bits in length, thus the array must have exactly 4
	 * elements.
	 * 
	 * @param addr
	 *            The IPv4Address data
	 * 
	 * @exception IllegalArgumentException
	 *                Thrown if the passed buffer is not in the correct format
	 *                for an IPv4Address.
	 * 
	 */
	public IPv4Address(byte[] addr) {
		if (addr.length != 4)
			throw new IllegalArgumentException("Invalid address length");

		m_addr = dup(addr);
	}

	/**
	 * Constructs a new address object based on the 32-bit passed value. The
	 * 32-bit integer is split into four eight bit values that represent the
	 * IPv4 address.
	 * 
	 * @param ipv4Addr
	 *            The 32-bit IP address.
	 * 
	 */
	public IPv4Address(int ipv4Addr) {
		m_addr = new byte[4];
		m_addr[0] = (byte) (ipv4Addr >>> 24);
		m_addr[1] = (byte) ((ipv4Addr >> 16) & 0xff);
		m_addr[2] = (byte) ((ipv4Addr >> 8) & 0xff);
		m_addr[3] = (byte) (ipv4Addr & 0xff);
	}

	/**
	 * Creates a new object by decomposing the passed string into it four
	 * components. The string must be in the format of "xxx.xxx.xxx.xxx" where
	 * xxx is in the range of [0..256).
	 * 
	 * @param ipv4Addr
	 *            The dotted decimal address.
	 * 
	 * @exception IllegalArgumentException
	 *                Thrown if the string is a malformed dotted decimal
	 *                address.
	 * 
	 */
	public IPv4Address(String ipv4Addr) {
		StringTokenizer tok = new StringTokenizer(ipv4Addr, ".");
		if (tok.countTokens() != 4)
			throw new IllegalArgumentException(
					"Invalid Dotted Decimal IPv4 Address");
		m_addr = new byte[4];

		int x;
		for (x = 0; x < 4; x++) {
			int converted = Integer.parseInt(tok.nextToken());
			if (converted < 0 || converted > 255)
				throw new IllegalArgumentException(
						"Invalid IPv4 Address string");

			m_addr[x] = (byte) (converted & 0xff);
		}
	}

	/**
	 * Creates a new IPv4Address from the passed InetAddress object.
	 * 
	 * @param addr
	 *            The Internet Address containing the IPv4 address.
	 * 
	 */
	public IPv4Address(InetAddress addr) {
		m_addr = dup(addr.getAddress());
	}

	/**
	 * Returns the 32-bit IPv4 address.
	 * 
	 * @return 32-bit IPv4 address
	 * 
	 */
	public int getAddress() {
		int addr = byteToInt(m_addr[0]) << 24;
		addr |= byteToInt(m_addr[1]) << 16;
		addr |= byteToInt(m_addr[2]) << 8;
		addr |= byteToInt(m_addr[3]);

		return addr;
	}

	/**
	 * Sets the current address based upon the value of the passed object.
	 * 
	 * @param second
	 *            The new address.
	 */
	public void setAddress(IPv4Address second) {
		m_addr = dup(second.m_addr);
	}

	/**
	 * Sets the object based on the value stored in the passed array. An IPv4
	 * Address is 32-bits in length, thus the array must have exactly 4
	 * elements.
	 * 
	 * @param addr
	 *            The IPv4Address data
	 * 
	 * @exception IllegalArgumentException
	 *                Thrown if the passed buffer is not in the correct format
	 *                for an IPv4Address.
	 * 
	 */
	public void setAddress(byte[] addr) {
		if (addr.length != 4)
			throw new IllegalArgumentException("Invalid address length");

		m_addr = dup(addr);
	}

	/**
	 * Sets the address object based on the 32-bit passed value. The 32-bit
	 * integer is split into four eight bit values that represent the IPv4
	 * address.
	 * 
	 * @param ipv4Addr
	 *            The 32-bit IP address.
	 * 
	 */
	public void setAddress(int ipv4Addr) {
		m_addr = new byte[4];
		m_addr[0] = (byte) (ipv4Addr >>> 24);
		m_addr[1] = (byte) ((ipv4Addr >> 16) & 0xff);
		m_addr[2] = (byte) ((ipv4Addr >> 8) & 0xff);
		m_addr[3] = (byte) (ipv4Addr & 0xff);
	}

	/**
	 * Sets the object by decomposing the passed string into it four components.
	 * The string must be in the format of "xxx.xxx.xxx.xxx" where xxx is in the
	 * range of [0..256).
	 * 
	 * @param ipv4Addr
	 *            The dotted decimal address.
	 * 
	 * @exception IllegalArgumentException
	 *                Thrown if the string is a malformed dotted decimal
	 *                address.
	 * 
	 */
	public void setAddress(String ipv4Addr) {
		StringTokenizer tok = new StringTokenizer(ipv4Addr, ".");
		if (tok.countTokens() != 4)
			throw new IllegalArgumentException(
					"Invalid Dotted Decimal IPv4 Address");
		m_addr = new byte[4];

		int x;
		for (x = 0; x < 4; x++) {
			int converted = Integer.parseInt(tok.nextToken());
			if (converted < 0 || converted > 255)
				throw new IllegalArgumentException(
						"Invalid IPv4 Address string");

			m_addr[x] = (byte) (converted & 0xff);
		}
	}

	/**
	 * Sets the IPv4Address from the passed InetAddress object.
	 * 
	 * @param addr
	 *            The Internet Address containing the IPv4 address.
	 * 
	 */
	public void setAddress(InetAddress addr) {
		m_addr = dup(addr.getAddress());
	}

	/**
	 * Test to determine if the passed object is equal to self. The object may
	 * be an Integer, String, or IPv4Address. If the object is a String then it
	 * must be in the dotted decimal string format.
	 * 
	 * @param obj
	 *            The object to use in the comparison.
	 * 
	 * @return True if equal, false if not equal.
	 */
	public boolean equals(Object obj) {
		boolean bRC = false;

		if (obj instanceof IPv4Address) {
			byte[] t = ((IPv4Address) obj).m_addr;
			int x;

			bRC = true;
			for (x = 0; x < 4; x++) {
				if (m_addr[x] != t[x]) {
					bRC = false;
				}
			}
		} else if (obj instanceof Integer) {
			if (((Integer) obj).intValue() == getAddress()) {
				bRC = true;
			}
		} else if (obj instanceof String) {
			try {
				IPv4Address addr = new IPv4Address((String) obj);
				bRC = this.equals(addr);
			} catch (IllegalArgumentException e) {
				bRC = false;
			}
		}

		return bRC;
	}

	/**
	 * Converts the object to a string and returns the string to the caller.
	 * 
	 * @return The dotted decimal string for the address
	 * 
	 */
	public String toString() {
		return addressToString(m_addr);
	}

}
