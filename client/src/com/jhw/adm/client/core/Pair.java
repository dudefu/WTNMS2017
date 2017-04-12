package com.jhw.adm.client.core;

public class Pair<H, T> {
	
	public Pair() {
	}
	
	public Pair(H h, T t) {
		head = h;
		tail = t;
	}

	public H getHead() {
		return head;
	}
	public void setHead(H head) {
		this.head = head;
	}
	public T getTail() {
		return tail;
	}
	public void setTail(T tail) {
		this.tail = tail;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair that = (Pair)obj;
			
			if (that.getHead() != null &&
				that.getTail() != null &&
				that.getHead().equals(this.getHead()) &&
				that.getTail().equals(this.getTail())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		if (getHead() != null &&
			getTail() != null) {
			return getHead().hashCode() +
			        getTail().hashCode();
		} else {
			return super.hashCode();
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s:%s", head, tail);
	}
	private H head;
	private T tail;
}