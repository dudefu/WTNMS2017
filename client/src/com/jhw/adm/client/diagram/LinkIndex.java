package com.jhw.adm.client.diagram;

import org.apache.commons.lang.StringUtils;

/**
 * Á¬ÏßË÷Òý
 */
public class LinkIndex {
	
	public LinkIndex() {
		this(StringUtils.EMPTY, StringUtils.EMPTY);
	}
	
	public LinkIndex(String start, String end) {
		startAddress = start;
		endAddress = end;
	}
	
	@Override
	public String toString() {
		return startAddress + "-" + endAddress;
	}
	
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof LinkIndex) {
			LinkIndex other = (LinkIndex)obj;
			if (other.getStartAddress().equals(this.getStartAddress()) &&
				other.getEndAddress().equals(this.getEndAddress())) {
				result = true;
			} else if (other.getStartAddress().equals(this.getEndAddress()) &&
					    other.getEndAddress().equals(this.getStartAddress())) {
				result = true;
			}
		}
		
		return result;
    }
	
	@Override
	public int hashCode() {
		int startHashCode = startAddress.hashCode();
		int endHashCode = endAddress.hashCode();
		return startHashCode + endHashCode;
	}
	
	public String getStartAddress() {
		return startAddress;
	}

	public String getEndAddress() {
		return endAddress;
	}
	
	private String startAddress;
	private String endAddress;
}