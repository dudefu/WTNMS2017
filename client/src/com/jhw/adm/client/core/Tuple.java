package com.jhw.adm.client.core;

/**
 * ิชื้
 */
public class Tuple<T0, T1, T2> {
	
	public Tuple(){}
	
	public Tuple(T0 t0, T1 t1, T2 t2) {
		this.t0 = t0;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public T0 getT0() {
		return t0;
	}
	public void setT0(T0 t0) {
		this.t0 = t0;
	}
	public T1 getT1() {
		return t1;
	}
	public void setT1(T1 t1) {
		this.t1 = t1;
	}
	public T2 getT2() {
		return t2;
	}
	public void setT2(T2 t2) {
		this.t2 = t2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			Tuple that = (Tuple)obj;
			
			if (that.getT0() != null &&
				that.getT1() != null &&
				that.getT2() != null &&
				that.getT0().equals(this.getT0()) &&
				that.getT1().equals(this.getT1()) &&
				that.getT2().equals(this.getT2())) {
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
		if (getT0() != null &&
			getT1() != null &&
			getT2() != null) {
			return getT0().hashCode() +
				    getT1().hashCode() +
				    getT2().hashCode();
		} else {
			return super.hashCode();
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s:%s", t0, t1, t2);
	}

	private T0 t0;
	private T1 t1;
	private T2 t2;
}