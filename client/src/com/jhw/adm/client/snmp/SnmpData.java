package com.jhw.adm.client.snmp;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpData {
	  public static final int DEFAULT_VERSION = SnmpConstants.version2c;
	  public static final String DEFAULT_PROTOCOL = "udp";
	  public static final int DEFAULT_PORT = 161;
	  public static final long DEFAULT_TIMEOUT = 3 * 1000L;
	  public static final int DEFAULT_RETRY = 3;

	  /**
	   * 创建对象communityTarget
	   *
	   * @param targetAddress
	   * @param community
	   * @param version
	   * @param timeOut
	   * @param retry
	   * @return CommunityTarget
	   */
	  public static CommunityTarget createDefault(String ip, String community) {
	    Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip
	        + "/" + DEFAULT_PORT);
	    CommunityTarget target = new CommunityTarget();
	    target.setCommunity(new OctetString(community));
	    target.setAddress(address);
	    target.setVersion(DEFAULT_VERSION);
	    target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
	    target.setRetries(DEFAULT_RETRY);
	    return target;
	  }
	  
	  /*获取信息*/
	  public static boolean snmpGet(String ip, String community, String oid) {

	    CommunityTarget target = createDefault(ip, community);
	    Snmp snmp = null;
	    try {
	      PDU pdu = new PDU();
	      pdu.add(new VariableBinding(new OID(oid)));
	      DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
	      snmp = new Snmp(transport);
	      snmp.listen();
	      pdu.setType(PDU.GET);
	      ResponseEvent respEvent = snmp.send(pdu, target);
	      System.out.println("PeerAddress:" + respEvent.getPeerAddress());
	      PDU response = respEvent.getResponse();
	      if (response == null) {
	        System.out.println("response is null, request time out");
	        return false;
	      } else {
	        System.out.println("response pdu size is " + response.size());
	        for (int i = 0; i < response.size(); i++) {
	          VariableBinding vb = response.get(i);
	          System.out.println(vb.getOid() + " = " + vb.getVariable());
	        }
	        return true;
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.out.println("SNMP Get Exception:" + e);
	      return false;
	    } finally {
	      if (snmp != null) {
	        try {
	          snmp.close();
	        } catch (IOException ex1) {
	          snmp = null;
	        }
	      }
	    }
	  }
	  /*设置信息*/
	  public static boolean snmpSet(String ip, String community, String oid,String str) {

	    CommunityTarget target = createDefault(ip, community);
	    Snmp snmp = null;
	    try {
	      PDU pdu = new PDU();
	      // pdu.add(new VariableBinding(new OID(new int[]
	      // {1,3,6,1,2,1,1,2})));
	      pdu.add(new VariableBinding(new OID(oid),new OctetString(str)));

	      DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
	      snmp = new Snmp(transport);
	      snmp.listen();
	      pdu.setType(PDU.SET);
	      ResponseEvent respEvent = snmp.send(pdu, target);
	      System.out.println("PeerAddress:" + respEvent.getPeerAddress());
	      System.out.println("SNMP SET one OID value finished !");
	      if(null != respEvent.getPeerAddress()){
	    	  return true;
	      }else{
	    	  return false;
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.out.println("SNMP Set Exception:" + e);
	      return false;
	    } finally {
	      if (snmp != null) {
	        try {
	          snmp.close();
	        } catch (IOException ex1) {
	          snmp = null;
	        }
	      }

	    }
	  }
	  
	  public static void main(String[] args) {
		  	String ip = "192.168.1.113";
		    String community = "private";
		    String oidval = "1.3.6.1.4.1.44405.71.2.23.1.0";
		    SnmpData.snmpSet(ip, community, oidval,"");
	}
}
