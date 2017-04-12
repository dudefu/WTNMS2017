package com.jhw.adm.server.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jhw.adm.server.entity.message.TopoFoundFEPs;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;
import com.jhw.adm.server.util.ContextUtil;

public class QueueMessageSend {
	InitialContext ctx;
	QueueSender sender = null;
	QueueSession session = null;
	QueueConnection cnn = null;
	Queue queue = null;

	public static void main(String[] args) {
		QueueMessageSend ms = new QueueMessageSend();
		ms.initContext();
		try {
			ms.sendTopoMessage();
			Thread.sleep(2000);
			System.out.println("*********************");
			System.out.println("*********************");
			System.out.println("*********************");
			System.out.println("*********************");
			System.out.println("*********************");
			System.out.println("*********************");
			System.out.println("*********************");
			System.out.println("*********************");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void initContext() {
		try {
			ctx = new InitialContext();
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		try {
			cnn = ContextUtil.getInstance().getQueueConnection();
			queue = ContextUtil.getInstance().getQueue("queue/FTSQueue");
			session = cnn.createQueueSession(false,
					QueueSession.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException je) {
			je.printStackTrace();
		}

	}


	/**
	 * 构造客户单要求拓扑发现消息中的前置机编号列表
	 * 
	 * @return
	 */
	private TopoFoundFEPs sendTopoSearchMessage() {
		List feps = new ArrayList();
		feps.add("shenzhen");
//		feps.add("guangdong");
		TopoFoundFEPs tffeps = new TopoFoundFEPs();
		tffeps.setFepCodes(feps);
		return tffeps;
	}

	/**
	 * 测试发送拓扑发现请求消息
	 * 
	 * @throws Exception
	 */
	public void sendTopoMessage() throws Exception {
        TrapWarningEntity entity =new TrapWarningEntity();
        entity.setIpValue("192.168.5.20");
        entity.setWarningEvent(Constants.LINKDOWN);
        entity.setSampleTime(new Date());
        entity.setCurrentStatus(0);
        entity.setPortNo(8);
		ObjectMessage om = session.createObjectMessage(entity);
		om.setIntProperty(Constants.MESSAGETYPE,MessageNoConstants.TRAPMESSAGE);
		om.setStringProperty(Constants.MESSAGEFROM,"杨霄");
		sender.send(om);
		sender.close();
		session.close();
		cnn.close();

	}
	
	public void sendSynchMessage()throws Exception{
//		SynchSwitch ss = new SynchSwitch();
//		List ips = new ArrayList();
//		ips.add("192.168.13.146");
//		ss.setIpvalues(ips);
//		ss.setMessage("同步消息");
//		ObjectMessage om = session.createObjectMessage(ss);
//		om.setStringProperty(Constants.MESSAGETO, "192.168.13.146");
//		om.setStringProperty(Constants.MESSAGEFROM, "yangxiao");
//		sender.send(om);
//		sender.close();
//		session.close();
//		cnn.close();
	}

}
