package com.jhw.adm.comclient.gprs.hongdian;

/**
 * 
 * @author xiongbo
 * 
 */
public class CommunicateNative {
	static {
		System.loadLibrary("wcomm_dll");
	}

	public native int SetWorkMode(int nWorkMode);

	public native int SelectProtocol(int nProtocol);

	public native int SetCustomIP(long ulIPAddr);

	/**
	 * When the assignment,make 'obj' is null
	 * 
	 * @param obj
	 * @param wMsg
	 * @param nServerPort
	 * @param mess
	 * @return
	 */
	public native int start_net_service(Object obj, int wMsg, int nServerPort,
			String mess);

	public native int get_max_user_amount();

	public native int get_online_user_amount();
}
