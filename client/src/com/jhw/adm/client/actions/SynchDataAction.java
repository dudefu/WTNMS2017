package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.Action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.JBossMessageSender;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(SynchDataAction.ID)
public class SynchDataAction extends DesktopAction{
	public static final String ID = "synchDataAction";
	
	@Autowired
	@Qualifier(JBossMessageSender.ID)
	private MessageSender messageSender;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@PostConstruct
	protected void initialize() {
		putValue(Action.NAME, "数据上载");
		putValue(Action.SMALL_ICON, getImageRegistry().getImageIcon(ButtonConstants.QUERY));
		putValue(Action.SHORT_DESCRIPTION, "数据上载");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SYNCHDEVICE);
				message.setObject(getSwitchList());
				message.setStringProperty(Constants.MESSAGEFROM, "wuzhongwei");
				return message;
			}
		});
	}
	
	private ArrayList getSwitchList(){
		SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
		String ipValue = switchTopoNodeEntity.getIpValue();
		
		ArrayList switchList = new ArrayList();
		switchList.add(ipValue);
		return null;
	}
}
