package com.jhw.adm.comclient.gprs.hongdian;

public class DataServiceCenter {
	static {
		System.loadLibrary("DataServiceCenter");
	}

	// DSCDLLAPI int HDCALL SetWorkMode(int nWorkMode);
	public native int setWorkMode(int workMode);

	// DSCDLLAPI int HDCALL SetWorkMode(int nWorkMode);
	public native int selectProtocol(int protocol);

	// DSCDLLAPI int HDCALL start_net_service(HWND hWnd,unsigned int wMsg,int
	// nServerPort,char *mess);
	public native int start(int msg, int port, String message);

	// DSCDLLAPI int HDCALL stop_net_service(char *mess);
	public native int stop(String message);

	// DSCDLLAPI int HDCALL do_read_proc(data_record *recdPtr,char *mess,BOOL
	// reply);
	public native int read(DSCData data, String message, boolean reply);

	// DSCDLLAPI int HDCALL do_send_user_data(uint8* userid,uint8*data,uint
	// len,char *mess);
	public native int send(byte[] userId, byte[] data, int length,
			String message);

	// DSCDLLAPI int HDCALL get_user_info(uint8 *userid,user_info *infoPtr);
	public native int getUser(byte[] userId, DSCUser user);

	// JNIEXPORT void JNICALL
	// Java_com_jhw_adm_comclient_gprs_hongdian_DataServiceCenter_cancel_1read_1block
	// (JNIEnv *, jobject);
	public native void cancel_read_block();

	// JNIEXPORT jint JNICALL
	// Java_com_jhw_adm_comclient_gprs_hongdian_DataServiceCenter_get_1max_1user_1amount
	// (JNIEnv *, jobject);
	public native int get_max_user_amount();

	// JNIEXPORT jint JNICALL
	// Java_com_jhw_adm_comclient_gprs_hongdian_DataServiceCenter_get_1user_1at
	// (JNIEnv *, jobject, jint, jobject);
	public native int get_user_at(int index, DSCUser user);

	// JNIEXPORT jint JNICALL
	// Java_com_jhw_adm_comclient_gprs_hongdian_DataServiceCenter_do_1close_1one_1user
	// (JNIEnv *, jobject, jbyteArray, jstring);
	public native int do_close_one_user(byte[] userId, String message);

}
