package com.jhw.adm.comclient.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;

import com.jhw.adm.comclient.util.PropertyFileUtil;

/**
 * System config
 * 
 * @author xiongbo
 * 
 */
public class Configuration {

	private static final String logconfig = "conf/log.xml";
	//
	private static final String setupConfig = "conf/sysConfig.ini";

	private static final String FEP_CODE = "FEP_CODE";
	public static String fepCode;
	private static final String FEP_PASSWORD = "FEP_PASSWORD";
	public static String fepPassword;

	private static final String GPRS_PORT = "GPRS_PORT";
	public static int gprsPort;
	// TODO
	private static final String THREE_LAYER_COMMUNITY = "THREE_LAYER_COMMUNITY";
	public static String three_layer_community;
	private static final String OLT_COMMUNITY = "OLT_COMMUNITY";
	public static String olt_community;
	private static final String LAYER3_LOOPBACK = "LAYER3_LOOPBACK";
	public static int layer3_loopback;
	// TODO Temporary
	public static String jndi_ipPort;

	private static final String TOPOLOGY_SLEEP = "TOPOLOGY_SLEEP";
	public static int topology_sleep;

	// private static final String TOPOLOGY_DISCOVERYWAY =
	// "TOPOLOGY_DISCOVERYWAY";
	// public static int topology_discoveryWay;

	private static final String OLT_TYPE = "OLT_TYPE";
	public static String olt_type;
	private static final String PING_COUNT = "PING_COUNT";
	public static int ping_count;
	private static final String SNMP_COUNT = "SNMP_COUNT";
	public static int snmp_count;
	private static final String LAYER3_TYPE = "LAYER3_TYPE";
	public static String layer3_type;
	private static final String DEVICELOG_LISTENPORT = "DEVICELOG_LISTENPORT";
	public static int devicelog_listenport;
	//
	private static final String MOXA_OBJECTID = "MOXA_OBJECTID";
	public static String moxa_objectid;
	private static final String KYLAND_OBJECTID = "KYLAND_OBJECTID";
	public static String kyland_objectid;
	private static final String JHW_OBJECTID = "JHW_OBJECTID";
	public static String jhw_objectid;

	private static final String RMON_REPORT_INTERVAL = "RMON_REPORT_INTERVAL";
	public static int rmon_report_interval;

	private static final String TRAP_PORT = "TRAP_PORT";
	public static int trap_port;

	private static final String TOPOLOGY_THREADPOOL_COUNT = "TOPOLOGY_THREADPOOL_COUNT";
	public static int topology_threadpool_count;

	//

	/**
	 * Init logger
	 */
	public static void initLoggerConf() {
		File configfile = new File(logconfig);
		DOMConfigurator.configure(configfile.toString());
	}

	/**
	 * Init sys setup conf
	 */
	public static void loadSetupConf() {
		Properties setupConf = PropertyFileUtil.load(setupConfig);

		fepCode = (setupConf.getProperty(FEP_CODE, "WTFEP")).trim();
		fepPassword = (setupConf.getProperty(FEP_PASSWORD, "111111")).trim();

		gprsPort = Integer.parseInt(setupConf.getProperty(GPRS_PORT, "8081")
				.trim());
		// TODO
		three_layer_community = (setupConf.getProperty(THREE_LAYER_COMMUNITY,
				"public")).trim();

		olt_community = (setupConf.getProperty(OLT_COMMUNITY, "public")).trim();

		layer3_loopback = Integer.parseInt(setupConf.getProperty(
				LAYER3_LOOPBACK, "0").trim());

		topology_sleep = Integer.parseInt(setupConf.getProperty(TOPOLOGY_SLEEP,
				"5").trim());

		// topology_discoveryWay = Integer.parseInt(setupConf.getProperty(
		// TOPOLOGY_DISCOVERYWAY, "1").trim());

		olt_type = (setupConf.getProperty(OLT_TYPE, "3010")).trim();

		ping_count = Integer.parseInt(setupConf.getProperty(PING_COUNT, "2")
				.trim());

		snmp_count = Integer.parseInt(setupConf.getProperty(SNMP_COUNT, "2")
				.trim());

		layer3_type = (setupConf.getProperty(LAYER3_TYPE, "3424")).trim();

		devicelog_listenport = Integer.parseInt(setupConf.getProperty(
				DEVICELOG_LISTENPORT, "8888").trim());
		//
		moxa_objectid = (setupConf.getProperty(MOXA_OBJECTID, "1.8691")).trim();
		kyland_objectid = (setupConf.getProperty(KYLAND_OBJECTID, "1.26067"))
				.trim();
		jhw_objectid = (setupConf.getProperty(JHW_OBJECTID, "1.16001")).trim();
		//

		rmon_report_interval = Integer.parseInt(setupConf.getProperty(
				RMON_REPORT_INTERVAL, "60").trim());

		trap_port = Integer.parseInt(setupConf.getProperty(TRAP_PORT, "162")
				.trim());

		topology_threadpool_count = Integer.parseInt(setupConf.getProperty(
				TOPOLOGY_THREADPOOL_COUNT, "200").trim());

		setupConf.clear();
	}

	public static void writeSetupConf() {
		// File configfile = new File(logconfig);
		// DOMConfigurator.configure(configfile.toString());
	}

	public static String readJNDI() {
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			String tempStr = "";
			fis = new FileInputStream("conf\\jndi.properties");
			br = new BufferedReader(new InputStreamReader(fis));
			while ((tempStr = br.readLine()) != null) {
				if (tempStr.startsWith("url=")) {
					String[] temp = tempStr.split("=");
					jndi_ipPort = temp[1];
					return jndi_ipPort;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}
		return "0.0.0.0:1099";
	}

	public static void writeJNDI(String ipPort) {
		String temp = "#java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory";
		temp += System.getProperty("line.separator");
		temp += "#java.naming.provider.url=0.0.0.0:1099";
		temp += System.getProperty("line.separator");
		temp += "#java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces";
		temp += System.getProperty("line.separator");
		temp += "url=" + ipPort;

		// StringBuilder sb = new StringBuilder();
		// sb.append(data).append(System.getProperty("line.separator"));
		// }
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("conf\\jndi.properties")));
			out.write(temp);
			out.flush();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static String removeBlank(String str) {
		str = str.replaceAll("¡¡", "").replaceAll(" ", "").trim();
		return str;
	}
}
