package com.jhw.adm.client.util;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * �ͻ��˹���
 */
public final class ClientUtils {

	/**
	 * ��ȡ������ַ
	 */
	public static String getLocalAddress() {
		String localAddress = "127.0.0.1";
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			localAddress = localHost.getHostAddress();
		} catch (UnknownHostException e) {
			LOG.error("get local host address error", e);
		}
		
		return localAddress;
	}
	
	/**
	 * ����ʼʱ����ڽ���ʱ��ʱ,����false,���򷵻�true;
	 * @param beginText
	 * @param endText
	 * @return
	 */
	public static boolean compareTime(String beginText,String endText){
		String pattern = "yyyy-MM-dd HH:mm";
		
		boolean bool = true;
		SimpleDateFormat  fm=new SimpleDateFormat(pattern); 
		Date beginTime = null;
		Date endTime = null;
		try {
			beginTime =fm.parse(beginText);
			endTime = fm.parse(endText);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		long begin = beginTime.getTime();
		long end = endTime.getTime();
		if (begin > end){
			return false;
		}
		
		return bool;
	}
	
	/** 
     * �ж�ip�Ƿ���ָ�������� 
     * @author dh 
     * @param iparea [xx.xx.xx.xx-xx.xx.xx.xx]
     * @param ip 
     * @return boolean 
     */  
    public static boolean ipIsInNet(String iparea, String ip) {  
        if (iparea == null){
        	throw new NullPointerException("IP�β���Ϊ�գ�"); 
        }
        if (ip == null){
        	throw new NullPointerException("IP����Ϊ�գ�");  
        }
            
        iparea = iparea.trim();
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!iparea.matches(REGX_IPB) || !ip.matches(REGX_IP)){
        	return false; 
        }
             
        int idx = iparea.indexOf('-');
        String[] sips = iparea.substring(0, idx).split("\\.");
        String[] sipe = iparea.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }  
        return ips <= ipt && ipt <= ipe;
    }
	
	/**
	 * ��ȡ��Ļ���Χ
	 */
	public static Rectangle getMaxBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets scrInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());
		Rectangle r = new Rectangle();
		r.x = scrInsets.left;
		r.y = scrInsets.top;
		r.width = screenSize.width - scrInsets.left - scrInsets.right;
		r.height = screenSize.height - scrInsets.top - scrInsets.bottom;
		return r;
	}

	/**
	 * ���жԻ����ڴ���<br>
	 * ��ȥ������
	 */
	public static void centerDialog(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets scrInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());

		Rectangle windowSize = new Rectangle();
		windowSize.x = scrInsets.left;
		windowSize.y = scrInsets.top;
		windowSize.width = screenSize.width - scrInsets.left - scrInsets.right;
		windowSize.height = screenSize.height - scrInsets.top - scrInsets.bottom;
		
		Dimension size = dialog.getSize();
		screenSize.height = windowSize.height / 2;
		screenSize.width = windowSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		dialog.setLocation(x, y);
	}

	/**
	 * ���жԻ�������Ļ
	 */
	public static void screenCenter(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = dialog.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		dialog.setLocation(x, y);
	}
	
	/**
	 * ��ȡSpring��������<br>
	 * �÷����Ѿ�����������ʹ��"getSpringBean(String name)"����
	 * @param clazz Spring�������������
     * @param name Spring���������ID
     * @return Spring��������<br>
     * @����
	 * <code>ImageRegistry imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);</code>
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T> T getSpringBean(Class<T> clazz, String name) {
		return (T)getSpringContext().getBean(name);
	}
	
	/**
	 * ��ȡSpring��������
     * @param name Spring���������ID
     * @return Spring��������<br>
     * @����
	 * <code>ImageRegistry imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSpringBean(String name) {
		return (T)getSpringContext().getBean(name);
	}
	
	/**
	 * ��ȡSpring����<br>
	 * ��Ҫ��������֮��Ķ���ʹ����������Ķ���
	 */
	public static ApplicationContext getSpringContext() {
		return (ApplicationContext) variable.get(SRPING_CONTEXT);
	}

	public static void setSpringContext(ApplicationContext springContext) {
		variable.put(SRPING_CONTEXT, springContext);
	}

	public static void setRootFrame(JFrame frame) {
		variable.put(ROOT_FRAME, frame);
	}

	/**
	 * ��ȡ�ͻ��˸�����<br>
	 * ��Ҫ�������öԻ��������
	 */
	public static JFrame getRootFrame() {
		return (JFrame) variable.get(ROOT_FRAME);
	}
	
	public static void setLoginUser(String username) {
		variable.put(LOGIN_USER, username);
	}
	
	public static String getLoginUser() {
		return (String)variable.get(LOGIN_USER);
	}
	
	public static void setAppName(String appName) {
		variable.put(APP_NAME, appName);
	}
	
	public static String getAppName() {
		return (String)variable.get(APP_NAME);
	}

	public static void put(String key, Object value) {
		variable.put(key, value);
	}

	public static Object get(String key) {
		return variable.containsKey(key) ? variable.get(key) : null;
	}
	
//	private static final String[] RESERVE_IP_VALUE = {"247.255.255","233.86.195"};
	private static final String[] ILLEGAL_IP_VALUE = {"255.255.255.255","0.0.0.0"};
	public static boolean isIllegal(String ipValue){
		boolean isIllegal = false;
		
		int first = NumberUtils.toInt(ipValue.substring(0, ipValue.indexOf('.')));
		
		if(first >= 224 && first <= 239){//D���ַ��Χ��224.0.0.1��239.255.255.254
			isIllegal = true;
			return isIllegal;
		}
		
		if(first >= 240 && first <= 255){//E���ַ��Χ��240.0.0.1��255.255.255.255 
			isIllegal = true;
			return isIllegal;
		}
		
		for(String address : ILLEGAL_IP_VALUE){
			if(address.equals(ipValue)){
				isIllegal = true;
				return isIllegal;
			}
		}
		
//		for(String address : RESERVE_IP_VALUE){
//			if(address.equals(ipValue.substring(0, ipValue.lastIndexOf(".")))){
//				isIllegal = true;
//				return isIllegal;
//			}
//		}
		
		String endIP = ipValue.substring(ipValue.lastIndexOf(".") + 1, ipValue.length());
		if(NumberUtils.toInt(endIP) == 0){
			isIllegal = true;
			return isIllegal;
		}
		
		return isIllegal;
	}

//	private static final Object NULL_OBJECT = new Object();
	private static final Logger LOG = LoggerFactory.getLogger(ClientUtils.class);
	private static final Map<String, Object> variable = new HashMap<String, Object>();

	public static final String ROOT_FRAME = "ROOT_FRAME";
	public static final String LOGIN_USER = "LOGIN_USER";
	public static final String APP_NAME = "APP_NAME";
	public static final String SRPING_CONTEXT = "SRPING_CONTEXT";
}