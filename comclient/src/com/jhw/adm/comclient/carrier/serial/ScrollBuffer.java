package com.jhw.adm.comclient.carrier.serial;

public class ScrollBuffer {

	public ScrollBuffer(byte[] buffer) {
		bufferRef = buffer;
		bufferIndex = 0;
	}

	public byte getByte() {
		return bufferRef[bufferIndex++];
	}

	public int getInt() {
		return getByte() & 0xFF;
	}

	public double getDouble() {
		return getByte() & 0xFF;
	}

	public int getIndex() {
		return bufferIndex;
	}

	public void put(byte b) {
		bufferRef[bufferIndex++] = b;
	}

	public void put(int i) {
		bufferRef[bufferIndex++] = (byte) i;
	}

	/**
	 * 高位在前(H)，低位在后(L)
	 */
	public void put2ByteHL(int value) {
		bufferRef[bufferIndex++] = (byte) (value >> 8);
		bufferRef[bufferIndex++] = (byte) (value & 0x0FF);
	}

	/**
	 * 低位在前(L)，高位在后(H)
	 */
	public void put3ByteLH(int value) {
		bufferRef[bufferIndex++] = (byte) (value & 0x0FF);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF00) >> 8);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF0000) >> 16);
	}

	/**
	 * 低位在前(L)，高位在后(H)
	 */
	public void put4ByteLH(long value) {
		bufferRef[bufferIndex++] = (byte) (value & 0x0FF);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF00) >> 8);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF0000) >> 16);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF000000) >> 24);
	}

	/**
	 * 高位在前(H)，低位在后(L)
	 */
	public void put4ByteHL(long value) {
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF000000) >> 24);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF0000) >> 16);
		bufferRef[bufferIndex++] = (byte) ((value & 0xFF00) >> 8);
		bufferRef[bufferIndex++] = (byte) (value & 0x0FF);
	}

	/**
	 * 高位在前(H)，低位在后(L)
	 */
	public int get2ByteHL() {
		int b1 = getInt();
		int b2 = getInt();
		int result = ((b1 << 8) & 0x0000FF00) + (b2 & 0x000000FF);
		return result;
	}

	/**
	 * 高位在前(H)，低位在后(L)
	 */
	public int get3ByteHL() {
		int b1 = getInt();
		int b2 = getInt();
		int b3 = getInt();
		int result = ((b1 << 16) & 0x00FF0000) + ((b2 << 8) & 0x0000FF00)
				+ (b3 & 0x000000FF);
		return result;
	}

	/**
	 * 高位在前(H)，低位在后(L)
	 */
	public long get4ByteHL() {
		byte b1 = getByte();
		byte b2 = getByte();
		byte b3 = getByte();
		byte b4 = getByte();
		long result = (((long) b1 << 24) & 0xFF000000)
				+ (((long) b2 << 16) & 0x00FF0000)
				+ (((long) b3 << 8) & 0x0000FF00) + ((long) b4 & 0x000000FF);
		return result;
	}

	public int length() {
		return bufferRef == null ? 0 : bufferRef.length;
	}

	public void move(int step) {
		bufferIndex += step;
	}

	private byte[] bufferRef;
	private int bufferIndex;
}