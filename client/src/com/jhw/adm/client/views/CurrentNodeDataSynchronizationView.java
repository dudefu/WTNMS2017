package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.DataSynchronizeStrategy;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CurrentNodeDataSynchronizationView.ID)
@Scope(Scopes.DESKTOP)
public class CurrentNodeDataSynchronizationView extends ViewPart {
	public static final String ID = "currentNodeDataSynchronizationView";

	public static final String noteLabel = "1.如果设备侧和网管侧数据不一致，网管侧数据会删除";
	public static final String promptLabel = "2.上载过程可能需要几分钟的时间，请耐心等待";
	
	private final JPanel informationPnl = new JPanel();
	private final JPanel operationPnl = new JPanel();
	private JButton closeBtn = null;
	private JButton synBtn = null;
	
	private JTextField currentNodeIPFld;
	private JTextField currentNodeTypeFld;

	private SwitchTopoNodeEntity switchTopoNodeEntity;
	private SwitchNodeEntity switchNodeEntity;
	
	private static final long serialVersionUID = 1L;
	
	private ButtonFactory buttonFactory;

	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;

	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	private MessageSender messageSender;	
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	@PostConstruct
	protected void initialize() {
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this); 
		init();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}	

	private void init() {
		this.setTitle("数据上载");
		
		initCenterPnl();
		initBottomPnl();

		this.setLayout(new BorderLayout());
		this.add(informationPnl, BorderLayout.CENTER);
		this.add(operationPnl, BorderLayout.SOUTH);

		this.setViewSize(480, 360);
		
		queryData();
	}

	private void initCenterPnl() {
		JPanel infoContainer = new JPanel(new GridBagLayout());
		
		currentNodeIPFld = new JTextField();
		currentNodeTypeFld = new JTextField();
		
		currentNodeIPFld.setColumns(35);
		currentNodeTypeFld.setColumns(35);
		
		currentNodeIPFld.setEditable(false);
		currentNodeTypeFld.setEditable(false);
		
		currentNodeIPFld.setBackground(Color.WHITE);
		currentNodeTypeFld.setBackground(Color.WHITE);
		
		infoContainer.add(new JLabel("交换机IP"), new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,3),0,0));
		infoContainer.add(currentNodeIPFld, new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,3),0,0));
		
		infoContainer.add(new JLabel("交换机型号"), new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,3),0,0));
		infoContainer.add(currentNodeTypeFld, new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,3),0,0));
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(BorderFactory.createTitledBorder("设备信息"));
		JPanel infoWrapper = new JPanel(new BorderLayout());
		infoWrapper.add(infoContainer, BorderLayout.WEST);
		infoPanel.add(infoWrapper, BorderLayout.NORTH);
		
		JPanel noticePanel = new JPanel(new BorderLayout());
		noticePanel.setBorder(BorderFactory.createTitledBorder("重要提示"));
		JLabel noteLbl = new JLabel(noteLabel);
		JLabel promptLbl = new JLabel(promptLabel);
		noteLbl.setForeground(Color.RED);
		promptLbl.setForeground(Color.RED);
		
		JPanel noticeWarpper = new JPanel(new BorderLayout());
		JPanel noticeContainer = new JPanel(new GridBagLayout());
		noticeContainer.add(noteLbl, new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,3),0,0));
		noticeContainer.add(promptLbl, new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,3),0,0));

		noticeWarpper.add(noticeContainer,BorderLayout.WEST);
		noticePanel.add(noticeWarpper, BorderLayout.NORTH);
		
		informationPnl.setLayout(new BorderLayout(5,10));
		informationPnl.add(noticePanel, BorderLayout.PAGE_START);
		informationPnl.add(infoPanel, BorderLayout.CENTER);
	}

	private void initBottomPnl() {
		operationPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		synBtn = buttonFactory.createButton(UPLOAD);
		closeBtn = buttonFactory.createCloseButton();
		operationPnl.add(synBtn);
		operationPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		setCompontEanble(true);
	}

	private void setCompontEanble(boolean enable) {
		synBtn.setEnabled(enable);
	}
	
	private void queryData() {
		switchTopoNodeEntity = (SwitchTopoNodeEntity) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),SwitchTopoNodeEntity.class);
		
		if(null != switchTopoNodeEntity){
			switchNodeEntity = NodeUtils.getNodeEntity(switchTopoNodeEntity).getNodeEntity();
		}
		
		if(null != switchNodeEntity){
			currentNodeTypeFld.setText(switchNodeEntity.getType());
			if(null != switchNodeEntity.getBaseConfig()){
				currentNodeIPFld.setText(switchNodeEntity.getBaseConfig().getIpValue());	
			}
		}
	}

	@ViewAction(name = UPLOAD, icon = ButtonConstants.SYNCHRONIZE,desc="上载当前交换机数据",role=Constants.MANAGERCODE)
	public void upload() {
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		String message = "准备上载参数，你确定吗？";
		int result = JOptionPane.showConfirmDialog(this, message, "提示", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (0 != result){
			return;
		}
		
		Task task = new RequestTask(getSwitchList());
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private HashSet<SynchDevice> deviceList = null;
		public RequestTask(HashSet<SynchDevice> deviceList){
			this.deviceList = deviceList;
		}
		
		@Override
		public void run() {
			messageSender.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage message = session.createObjectMessage();
					message.setIntProperty(Constants.MESSAGETYPE,
							MessageNoConstants.SYNCHDEVICE);
					message.setObject(deviceList);
					message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
					message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());	
					return message;
				}
			});
		}
	}
	
	private JProgressBarModel progressBarModel;
	private DataSynchronizeStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new DataSynchronizeStrategy("上载", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"上载",true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private HashSet<SynchDevice> getSwitchList(){//暂时处理二层交换机
		HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		String ip = "";
		int deviceModel = 0;
		
		if(null != switchTopoNodeEntity){
			ip = NodeUtils.getNodeEntity(switchTopoNodeEntity).getNodeEntity().getBaseConfig().getIpValue();
			deviceModel = NodeUtils.getNodeEntity(switchTopoNodeEntity).getNodeEntity().getDeviceModel();
		}
		synchDevice.setIpvalue(ip);
		synchDevice.setModelNumber(deviceModel);
		synDeviceList.add(synchDevice);
		return synDeviceList;
	}
}
