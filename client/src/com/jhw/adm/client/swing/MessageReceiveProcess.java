package com.jhw.adm.client.swing;

import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

@Component(MessageReceiveProcess.ID)
@Scope(Scopes.PROTOTYPE)
public class MessageReceiveProcess {
	public static final String ID = "messageReceiveProcess";
	
	private ViewPart viewPart = null;
	
	private String operateInfo = "";
	
//	private MessageReceiveInter receiveInter = null;
	
	private Vector<MessageReceiveInter> receiveInters = new Vector<MessageReceiveInter>();
	
	private MessagePromptDialog messageDlg = null;
	
	private MessageSender messageSender;	
	
	private MessageProcessorAdapter messageProcessor;
	
	private MessageProcessorAdapter messageFepProcessor;
	
	@Autowired
	@Qualifier(MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@PostConstruct
	protected void initialize(){
		receiveInters.removeAllElements();
		receiveMessage();
	}
	
	/**
	 * 当每个视图打开时注册接收消息
	 * @param receiveInter
	 */
	public void registerReceiveMessage(MessageReceiveInter receiveInter){
		this.receiveInters.addElement(receiveInter);
	}
	
	private void receiveMessage(){
		messageSender = remoteServer.getMessageSender();
		messageProcessor = new MessageProcessorAdapter() {
			@Override
			public void process(ObjectMessage message) {
				try {
					ParmRep object = (ParmRep)message.getObject();
					object.getParmIds();
					boolean result = object.isSuccess();
					if (result){
						for(MessageReceiveInter receiveInter : receiveInters){
							receiveInter.receive("操作成功");
						}
					}
					else{
						for(MessageReceiveInter receiveInter : receiveInters){
							receiveInter.receive("操作失败");
						}
					}
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageProcessor);
		
		messageFepProcessor = new MessageProcessorAdapter() {
			@Override
			public void process(ObjectMessage message) {
				try {
					ParmRep object = (ParmRep)message.getObject();
					object.getParmIds();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			public void process(TextMessage message) {
				for(final MessageReceiveInter receiveInter : receiveInters){
					try {
						receiveInter.receive(message.getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		};
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepProcessor);
	}
	
	public void dispose() {
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepProcessor);
	}
	
	public void openMessageDialog(ViewPart viewPart ,String operateInfo){
		this.viewPart = viewPart;
		this.operateInfo = operateInfo;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				showMessageDialog();
			}
		});
	}
	
	private void showMessageDialog(){
		messageDlg = new MessagePromptDialog(this.viewPart,operateInfo,imageRegistry,remoteServer);
		messageDlg.setVisible(true);
	}
	
	public void setMessage(String message){
		if (null != messageDlg){
			messageDlg.setMessage(message);
		}
	}
}
