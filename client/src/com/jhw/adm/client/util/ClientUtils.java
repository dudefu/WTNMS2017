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
 * 客户端工具
 */
public final class ClientUtils {

	/**
	 * 获取本机地址
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
	 * 当开始时间大于结束时间时,返回false,否则返回true;
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
     * 判断ip是否在指定网段中 
     * @author dh 
     * @param iparea [xx.xx.xx.xx-xx.xx.xx.xx]
     * @param ip 
     * @return boolean 
     */  
    public static boolean ipIsInNet(String iparea, String ip) {  
        if (iparea == null){
        	throw new NullPointerException("IP段不能为空！"); 
        }
        if (ip == null){
        	throw new NullPointerException("IP不能为空！");  
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
	 * 获取屏幕最大范围
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
	 * 居中对话框于窗口<br>
	 * 减去任务栏
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
	 * 居中对话框于屏幕
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
	 * 获取Spring容器对象<br>
	 * 该方法已经被费弃，请使用"getSpringBean(String name)"方法
	 * @param clazz Spring容器对象的类型
     * @param name Spring容器对象的ID
     * @return Spring容器对象<br>
     * @例子
	 * <code>ImageRegistry imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);</code>
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T> T getSpringBean(Class<T> clazz, String name) {
		return (T)getSpringContext().getBean(name);
	}
	
	/**
	 * 获取Spring容器对象
     * @param name Spring容器对象的ID
     * @return Spring容器对象<br>
     * @例子
	 * <code>ImageRegistry imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSpringBean(String name) {
		return (T)getSpringContext().getBean(name);
	}
	
	/**
	 * 获取Spring容器<br>
	 * 主要用于容器之外的对象使用容器里面的对象
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
	 * 获取客户端根窗口<br>
	 * 主要用于设置对话框的容器
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
		
		if(first >= 224 && first <= 239){//D类地址范围：224.0.0.1到239.255.255.254
			isIllegal = true;
			return isIllegal;
		}
		
		if(first >= 240 && first <= 255){//E类地址范围：240.0.0.1到255.255.255.255 
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