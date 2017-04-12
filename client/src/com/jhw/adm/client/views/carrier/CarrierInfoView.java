package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.CONFIG;
import static com.jhw.adm.client.core.ActionConstants.RESTART;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.TEST;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.PLCMarking;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.carrier.CarrierCategory;
import com.jhw.adm.client.swing.MessageOfCarrierConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CarrierInfoView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierInfoView extends ViewPart {
	public static final String ID = "carrierInfoView";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CarrierInfoView.class);
	
	private final JPanel centerPnl = new JPanel();
	
	private final JLabel deviceIdLbl = new JLabel(); //�豸ID
	private final NumberField deviceIdFld = new NumberField();
	
	private final JLabel deviceTypeLbl = new JLabel();  //�豸����
	private final JComboBox deviceTypeCombox = new JComboBox();
	//modifier:wuzhongwei  date:2010.12.15 
	private final JLabel plcMarkingLbl = new JLabel(); //�豸�ͺ�
	private final JComboBox plcMarkingCombox = new JComboBox();
	private JButton configBtn = new JButton();
	
	private final JLabel addrLbl = new JLabel();  //����λ��
	private final JTextField addrFld = new JTextField();
	
	private final JLabel longitudeLbl = new JLabel();  //����
	private final JTextField longitudeFld = new JTextField();
	
	private final JLabel latitudeLbl = new JLabel();  //γ��
	private final JTextField latitudeFld = new JTextField();
	
	private final JLabel areaLbl = new JLabel();  //����
	private final JComboBox areaCombox = new JComboBox();
	
	private final JLabel fedCodeLbl = new JLabel();//ǰ�û����
	private final JComboBox fedCodeCombox = new JComboBox();
	
	private JLabel descriptionLbl = new JLabel();
	private JTextField descriptionFld = new JTextField();
	
	//Amend 2010.06.18
	private final JLabel statusTitleLbl = new JLabel();  //״̬
	private final JLabel statusLbl = new JLabel();
	
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn = new JButton();
	private JButton testBtn = new JButton();
	private JButton restartBtn = new JButton();
	private JButton closeBtn = null;
	
	private CarrierEntity carrierEntity = null;
	private CarrierTopNodeEntity carrierNode;
	private MessageSender messageSender;
	
	private final static int ONLINE = 1;
	private final static int OFFLINE = 0;
	private final static int WAITING = 2;
	private int carrierStatus = 0; //0:online   1:offline   2:waiting
	
	private boolean status;
	private ButtonFactory buttonFactory;
	private final MessageOfCarrierConfigProcessorStrategy messageOfCarrierProcessorStrategy = new MessageOfCarrierConfigProcessorStrategy();
	
	@Resource(name = PLCMarking.ID)
	private PLCMarking plcMarking;
	
	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
		
	@Resource(name = CarrierCategory.ID)
	private CarrierCategory carrierCategory;
	
	@Resource(name = MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private MessageProcessorAdapter messageProcessor;
	
	@PostConstruct
	protected void initialize() {
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this); 
		init();
		queryData();
		messageProcessor = new MessageProcessorAdapter() {
			@Override
			public void process(TextMessage message) {
				try {
					int carrierCode = NumberUtils.toInt(message.getStringProperty(Constants.MESSAGEFROM));// �ز������
					if (carrierCode == carrierEntity.getCarrierCode()) {
						String result = message.getStringProperty(Constants.MESSAGERES);// �Ƿ����ߵĽ��
						if ("S".equals(result)) {
							status = true;
						} else {
							status = false;
						}
						statusLbl.setIcon(reverseIntToIcon(status));
					 }
				} catch (JMSException e) {
					LOG.error("get carrierCode error", e);
				}
			}
		};
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERMONITORREP, messageProcessor);
	}
	
	private void init(){
		
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl, BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(480, 400);
		setResource();
	}
	
	private void initCenterPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		longitudeFld.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				keyTypeControl(arg0);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		latitudeFld.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				keyTypeControl(e);
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		panel.add(deviceIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		panel.add(deviceIdFld,new GridBagConstraints(1,0,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		    
		panel.add(deviceTypeLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(deviceTypeCombox,new GridBagConstraints(1,1,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		//modifier:wuzhongwei  date:2010.12.15 
		configBtn = buttonFactory.createButton(CONFIG);
		panel.add(plcMarkingLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(plcMarkingCombox,new GridBagConstraints(1,2,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		panel.add(configBtn,new GridBagConstraints(4,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		
		panel.add(addrLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(addrFld,new GridBagConstraints(1,3,7,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		
		panel.add(longitudeLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(longitudeFld,new GridBagConstraints(1,4,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		panel.add(latitudeLbl,new GridBagConstraints(4,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,10,0),0,0));
		panel.add(latitudeFld,new GridBagConstraints(5,4,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,10,0),0,0));
		
		panel.add(areaLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(areaCombox,new GridBagConstraints(1,5,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		
		panel.add(fedCodeLbl,new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(fedCodeCombox,new GridBagConstraints(1,6,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		
		panel.add(descriptionLbl,new GridBagConstraints(0,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(descriptionFld,new GridBagConstraints(1,7,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		
		panel.add(statusTitleLbl,new GridBagConstraints(0,8,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(statusLbl,new GridBagConstraints(1,8,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		
		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(panel);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		testBtn = buttonFactory.createButton(TEST);
		restartBtn = buttonFactory.createButton(RESTART);
		closeBtn = buttonFactory.createCloseButton();
		
		bottomPnl.add(saveBtn);
		bottomPnl.add(testBtn);
		bottomPnl.add(restartBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		setTitle("�ز���������Ϣ");
		centerPnl.setBorder(BorderFactory.createTitledBorder("�ز���������Ϣ"));
		deviceIdLbl.setText("�豸ID"); 
		deviceTypeLbl.setText("�豸����");  
		plcMarkingLbl.setText("�豸�ͺ�");
		addrLbl.setText("����λ��");
		longitudeLbl.setText("����");
		latitudeLbl.setText("γ��");
		areaLbl.setText("����");  
		fedCodeLbl.setText("ǰ�û����");
		statusTitleLbl.setText("״̬");
		descriptionLbl.setText("�ڵ�����");
		
		plcMarkingCombox.setModel(new ListComboBoxModel(plcMarking.toList()));
		deviceIdFld.setColumns(25);
		longitudeFld.setColumns(20);
		latitudeFld.setColumns(20);
		addrFld.setDocument(new TextFieldPlainDocument(addrFld,true));
		setInitializeValue();
		setFedCombox();
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, carrierChangeListener);
	}
	
	@SuppressWarnings("unchecked")
	private void setFedCombox(){
		fedCodeCombox.removeAllItems();
//		Iterator nodeEntitys = equipmentModel.getDiagram().getNodes().iterator();
//		while(nodeEntitys.hasNext()){
//			Object nodeEntity = nodeEntitys.next();
//			if(nodeEntity instanceof FEPTopoNodeEntity){
//				FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity)nodeEntity;
//				FEPEntity fepEntity = NodeUtils.getNodeEntity(fepTopoNodeEntity).getFepEntity();
//				if(fepEntity != null){
//					FEPEntityObject fepEntityObject = new FEPEntityObject(fepTopoNodeEntity.getFepEntity());
//					fedCodeCombox.addItem(fepEntityObject);
//				}
//			}
//		}
		List<FEPTopoNodeEntity> fepTopoNodeEntities = (List<FEPTopoNodeEntity>) remoteServer
				.getService().findAll(FEPTopoNodeEntity.class);
		for(FEPTopoNodeEntity fepTopoNodeEntity : fepTopoNodeEntities){
			FEPEntity fepEntity = NodeUtils.getNodeEntity(fepTopoNodeEntity).getFepEntity();
			if(fepEntity != null){
				FEPEntityObject fepEntityObject = new FEPEntityObject(fepTopoNodeEntity.getFepEntity());
				fedCodeCombox.addItem(fepEntityObject);
			}
		}
	}
	
	private void queryData(){
		carrierNode = (CarrierTopNodeEntity)adapterManager.getAdapter(
							equipmentModel.getLastSelected(), CarrierTopNodeEntity.class);
		
		if(null == carrierNode){
			return;
		}
		
		carrierEntity = NodeUtils.getNodeEntity(carrierNode).getNodeEntity();
//		carrierEntity = remoteServer.getService().getCarrierByCode(carrierNode.getCarrierCode());
//		if(carrierEntity == null){
//			carrierEntity = carrierNode.getNodeEntity();
//		}
		setValue();
	}
	
	private void setInitializeValue(){
		ListComboBoxModel carrierBoxModel = new ListComboBoxModel(carrierCategory.toList());
		deviceTypeCombox.setModel(carrierBoxModel);
		
		//�����ݿ��ѯ���еĵ���
		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		if (areaEntityList != null){
			areaCombox.removeAllItems();
			for (int i = 0 ; i < areaEntityList.size(); i++){
				AreaEntityObject object = new AreaEntityObject(areaEntityList.get(i));
				areaCombox.addItem(object);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(){
		
		//�õ��ز����ĸ�����ֵ
		String code = String.valueOf(carrierEntity.getCarrierCode()); //�豸ID
		int type = carrierEntity.getType();//�豸����
		int marking = carrierEntity.getMarking();// �豸�ͺ�   //modifier:wuzhongwei  date:2010.12.15 
		String addr ="";//����λ��
		String longitude = "";//����
		String latitude = "";//γ��
		AreaEntity areaEntity = null;
		
		if (null != carrierEntity.getAddress()){
			addr = carrierEntity.getAddress().getAddress();//����λ��
			longitude = carrierEntity.getAddress().getLongitude(); //����
			latitude = carrierEntity.getAddress().getLatitude();//γ��
			areaEntity = carrierEntity.getAddress().getArea(); //����
		}
		status = BooleanUtils.toBoolean(carrierNode.getStatus()); //״̬
		
		deviceIdFld.setText(code);
		deviceTypeCombox.setSelectedItem(carrierCategory.get(type));
		plcMarkingCombox.setSelectedItem(plcMarking.get(marking));
		addrFld.setText(addr);
		longitudeFld.setText(longitude);
		latitudeFld.setText(latitude);
		descriptionFld.setText(carrierNode.getName());
//		fedCodeCombox.setSelectedItem(carrierEntity.getFepCode());
		
		if(!StringUtils.isBlank(carrierEntity.getFepCode())){
			for(int i= 0 ; i < fedCodeCombox.getItemCount(); i++ ){
				String fepCode = ((FEPEntityObject)fedCodeCombox.getItemAt(i)).getFepEntity().getCode();
				if (carrierEntity.getFepCode().equals(fepCode)){
					fedCodeCombox.setSelectedIndex(i);
				}
			}
		}else{
			fedCodeCombox.setSelectedItem(null);
		}
		
		if (null == areaEntity){
			areaCombox.setSelectedItem(new AreaEntityObject(areaEntity));
		}else{
			for(int i= 0 ; i < areaCombox.getItemCount(); i++ ){
				long id = ((AreaEntityObject)areaCombox.getItemAt(i)).getAreaEntity().getId();
				long ids = areaEntity.getId();
				if (id == ids ){
					areaCombox.setSelectedIndex(i);
				}
			}
		}

		statusLbl.setIcon(reverseIntToIcon(status));
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				updateUI();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="�����ز���������Ϣ",role=Constants.MANAGERCODE)
	public void save(){
		if (!isValids()){
			return;
		}
		
		this.carrierNode.setCarrierCode(NumberUtils.toInt(this.deviceIdFld.getText().trim()));
		this.carrierNode.setType(((StringInteger)deviceTypeCombox.getSelectedItem()).getValue());
		this.carrierNode.setName(StringUtils.abbreviate(descriptionFld.getText().trim(),Constant.NODE_NAME_LENGTH));
		
		this.carrierEntity.setCarrierCode(NumberUtils.toInt(this.deviceIdFld.getText().trim()));//�豸ID
		this.carrierEntity.setType(((StringInteger)deviceTypeCombox.getSelectedItem()).getValue());//�豸����
		
		int marking = ((StringInteger)plcMarkingCombox.getSelectedItem()).getValue();//�豸�ͺ�
		this.carrierEntity.setMarking(marking);
		this.carrierNode.setCarrierType(marking + 300);//��CarrierTopNodeEntity�ж�����carrierType,301Ϊ��ͨ��,302Ϊ˫ͨ��
		
		if (null == this.carrierEntity.getAddress()){
			AddressEntity addressEntity = new AddressEntity();
			this.carrierEntity.setAddress(addressEntity);
		}
		
		this.carrierEntity.getAddress().setAddress(this.addrFld.getText().toString());//����λ��
		this.carrierEntity.getAddress().setLongitude(getNewString(this.longitudeFld.getText().toString().trim()));//����
		this.carrierEntity.getAddress().setLatitude(getNewString(this.latitudeFld.getText().toString().trim()));//γ��
		this.carrierEntity.getAddress().setArea(((AreaEntityObject)areaCombox.getSelectedItem()).getAreaEntity()); //����
		if(fedCodeCombox.getSelectedItem() != null){
			this.carrierEntity.setFepCode(((FEPEntityObject)fedCodeCombox.getSelectedItem()).toString());
		}
		
		if(ONLINE == carrierStatus || OFFLINE == carrierStatus){
			this.carrierNode.setStatus(carrierStatus);
		}
		
		// ����ʵ�塢��ַ������Ա
		if (carrierEntity.getId() == null) {
			carrierEntity = (CarrierEntity)remoteServer.getService().saveEntity(carrierEntity);
		} else {
			carrierEntity = (CarrierEntity)remoteServer.getService().updateEntity(carrierEntity);
		}
		if (carrierNode.getId() == null) {
			carrierNode = (CarrierTopNodeEntity)remoteServer.getService().saveEntity(carrierNode);
		} else {
			carrierNode = (CarrierTopNodeEntity)remoteServer.getService().updateEntity(carrierNode);
		}
		carrierNode.setNodeEntity(this.carrierEntity);
		
		showSaveMessageDialog();
		equipmentModel.fireEquipmentUpdated(carrierNode);
		messageOfCarrierProcessorStrategy.showNormalMessage("S");
	}
	
	private void showSaveMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("����", this);
	}
	
	//modifier:wuzhongwei  date:2010.12.15 begin
	/**
	 * �����ز������ͺţ���ͨ����˫ͨ����
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=CONFIG, desc="�����ز����ͺ�",role=Constants.MANAGERCODE)
	public void config(){
		if (null == carrierEntity){
			JOptionPane.showMessageDialog(this, "��ѡ���ز���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		final int code = NumberUtils.toInt(this.deviceIdFld.getText().trim());
		
		if (code < 2) {
			JOptionPane.showMessageDialog(this, "�豸ID������� 1��������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		CarrierEntity existCarrier = remoteServer.getService().getCarrierByCode(code);
		if ((null != existCarrier)
				&& (!ObjectUtils.equals(existCarrier.getId(), this.carrierEntity.getId()))) {
			
			String where = " where entity.carrierCode=?";
			Object[] parms = {code};
			List<CarrierTopNodeEntity> carrierList = (List<CarrierTopNodeEntity>)
				remoteServer.getService().findAll(CarrierTopNodeEntity.class, where, parms);
			
			if (carrierList != null && carrierList.size() > 0) {
				JOptionPane.showMessageDialog(this, "�豸ID�����ظ����룬������豸ID", "��ʾ", JOptionPane.NO_OPTION);
				return;
			} else {
				carrierEntity = existCarrier;
			}
		}
		
		if (null == carrierEntity.getId()){
			JOptionPane.showMessageDialog(this, "���ȱ����ز�������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		if (listOfCarrier == null) {
			JOptionPane.showMessageDialog(this, "��������һ̨�����ز���","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (listOfCarrier.size() > 1) {
			JOptionPane.showMessageDialog(this, "�����ز���ֻ����һ̨","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}
		
		int marking = ((StringInteger)plcMarkingCombox.getSelectedItem()).getValue();//�豸�ͺ�
		carrierEntity.setMarking(marking);
		
		showMarkingMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSettingCarrier(MessageNoConstants.CARRIERMARKINGCONFIG,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), carrierEntity, result);
		} catch (JMSException e) {
			messageOfCarrierProcessorStrategy.showErrorMessage("��������쳣");
			LOG.error("updateAndSettingCarrier error", e);
		}

		if (result == Constants.SYN_SERVER){
			messageOfCarrierProcessorStrategy.showNormalMessage("S");
		}
		
//		messageSender.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				ObjectMessage message = session.createObjectMessage();
//				message.setIntProperty(Constants.MESSAGETYPE,
//						MessageNoConstants.CARRIERMARKINGCONFIG);
//				message.setObject(carrierEntity);
//				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
//				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
//				return message;
//			}
//		});
//		new Thread(new Runnable(){
//			public void run(){
//				save();
//			}
//		}).start();
	}
	
	private void showMarkingMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("�����豸����", this);
	}//modifier:wuzhongwei  date:2010.12.15 end
	
	@ViewAction(name=TEST, icon=ButtonConstants.TEST, desc="�����ز���",role=Constants.MANAGERCODE)
	public void test(){
		if (null == carrierEntity){
			JOptionPane.showMessageDialog(this, "��ѡ���ز���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		final int code = NumberUtils.toInt(this.deviceIdFld.getText().trim());
		
		if (code < 2) {
			JOptionPane.showMessageDialog(this, "�豸ID������� 1��������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		CarrierEntity existCarrier = remoteServer.getService().getCarrierByCode(code);
		if ((null != existCarrier)
				&& (!ObjectUtils.equals(existCarrier.getId(), this.carrierEntity.getId()))) {
			
			String where = " where entity.carrierCode=?";
			Object[] parms = {code};
			List<CarrierTopNodeEntity> carrierList = (List<CarrierTopNodeEntity>)
				remoteServer.getService().findAll(CarrierTopNodeEntity.class, where, parms);
			
			if (carrierList != null && carrierList.size() > 0) {
				JOptionPane.showMessageDialog(this, "�豸ID�����ظ����룬������豸ID", "��ʾ", JOptionPane.NO_OPTION);
				return;
			} else {
				carrierEntity = existCarrier;
			}
		}
		
		if (null == carrierEntity.getId()){
			JOptionPane.showMessageDialog(this, "���ȱ����ز�������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		if (listOfCarrier == null) {
			JOptionPane.showMessageDialog(this, "��������һ̨�����ز���","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (listOfCarrier.size() > 1) {
			JOptionPane.showMessageDialog(this, "�����ز���ֻ����һ̨","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		carrierStatus = WAITING;
		statusLbl.setIcon(imageRegistry.getImageIcon(NetworkConstants.WAITING));//ͼ��Ϊ��ѯ״̬
		
		showTestMessageDialog();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERTEST);
				message.setObject(carrierEntity);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
	}
	
	private void showTestMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("����", this);
	}
	
	@ViewAction(name=RESTART, icon=ButtonConstants.RESTART, desc="�����ز���",role=Constants.MANAGERCODE)
	public void restart(){
//		if (!isValids()){
//			return;
//		}
		if (null == carrierEntity){
			JOptionPane.showMessageDialog(this, "��ѡ���ز���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		final int code = NumberUtils.toInt(this.deviceIdFld.getText().trim());
		
		if (code < 2) {
			JOptionPane.showMessageDialog(this, "�豸ID������� 1��������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		CarrierEntity existCarrier = remoteServer.getService().getCarrierByCode(code);
		if ((null != existCarrier)
				&& (!ObjectUtils.equals(existCarrier.getId(), this.carrierEntity.getId()))) {
			
			String where = " where entity.carrierCode=?";
			Object[] parms = {code};
			List<CarrierTopNodeEntity> carrierList = (List<CarrierTopNodeEntity>)
				remoteServer.getService().findAll(CarrierTopNodeEntity.class, where, parms);
			
			if (carrierList != null && carrierList.size() > 0) {
				JOptionPane.showMessageDialog(this, "�豸ID�����ظ����룬������豸ID", "��ʾ", JOptionPane.NO_OPTION);
				return;
			} else {
				carrierEntity = existCarrier;
			}
		}
		
		if (null == carrierEntity.getId()){
			JOptionPane.showMessageDialog(this, "���ȱ����ز�������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		if (listOfCarrier == null) {
			JOptionPane.showMessageDialog(this, "��������һ̨�����ز���","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (listOfCarrier.size() > 1) {
			JOptionPane.showMessageDialog(this, "�����ز���ֻ����һ̨","��ʾ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "ȷ����Ҫ�����ز�����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
//		carrierStatus = OFFLINE;
//		statusLbl.setIcon(null);//ͼ��Ϊ����״̬
		statusLbl.setIcon(reverseIntToIcon(false));
		
		showRestartMessageDialog();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERRESTART);
				message.setObject(carrierEntity);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
		messageOfCarrierProcessorStrategy.showNormalMessage("S");
	}
	
	private void showRestartMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("������������", this);
	}
	
	@SuppressWarnings("unchecked")
	public boolean isValids(){
		boolean bool = true;
		if (null == carrierEntity){
			JOptionPane.showMessageDialog(this, "��ѡ���ز���","��ʾ",JOptionPane.NO_OPTION);
			return false;
		}
		final int code = NumberUtils.toInt(this.deviceIdFld.getText().trim());
		
		if (code < 2) {
			JOptionPane.showMessageDialog(this, "�豸ID������� 1��������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			return false;
		}
		CarrierEntity existCarrier = remoteServer.getService().getCarrierByCode(code);
		if ((null != existCarrier)
				&& (!ObjectUtils.equals(existCarrier.getId(), this.carrierEntity.getId()))) {
			
			String where = " where entity.carrierCode=?";
			Object[] parms = {code};
			List<CarrierTopNodeEntity> carrierList = (List<CarrierTopNodeEntity>)
				remoteServer.getService().findAll(CarrierTopNodeEntity.class, where, parms);
			
			if (carrierList != null && carrierList.size() > 0) {
				JOptionPane.showMessageDialog(this, "�豸ID�����ظ����룬������豸ID", "��ʾ", JOptionPane.NO_OPTION);
				return false;
			} else {
				carrierEntity = existCarrier;
			}
		}

		if(plcMarkingCombox.getSelectedItem() == null){
			JOptionPane.showMessageDialog(this, "�������豸�ͺ�", "��ʾ", JOptionPane.NO_OPTION);
			return false;
		}
		
		String longitude = longitudeFld.getText().trim();
		String latitude = latitudeFld.getText().trim();
		
		if(longitude.indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "���ȱ��������ֿ�ͷ������������","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		if(latitude.indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "γ�ȱ��������ֿ�ͷ������������","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}

		if(longitude.length() > 36 || latitude.length() > 36){
			JOptionPane.showMessageDialog(this, "��γ�ȳ��Ȳ��ܳ���36���ַ�������������","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		if(!StringUtils.isBlank(longitude)){
			if(!NumberUtils.isNumber(longitude)){
				JOptionPane.showMessageDialog(this, "����ֵ���ܰ������ֺ͵�ţ�����������","��ʾ",JOptionPane.NO_OPTION);
				bool = false;
				return bool;
			}
		}

		if(!StringUtils.isBlank(latitude)){
			if(!NumberUtils.isNumber(latitude)){
				JOptionPane.showMessageDialog(this, "γ��ֵ���ܰ������ֺ͵�ţ�����������","��ʾ",JOptionPane.NO_OPTION);
				bool = false;
				return bool;
			}
		}
		
		if(NumberUtils.toDouble(longitude) > 180){
			JOptionPane.showMessageDialog(this, "����ֵ��ΧΪ��0-180������������","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		if(NumberUtils.toDouble(latitude) > 90){
			JOptionPane.showMessageDialog(this, "γ��ֵ��ΧΪ��0-90������������","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		return bool;
	}
	
	//Amend 2010.06.08
	public boolean judgeDeviceID(Vector v){
		boolean isSame = false;
		if (carrierNode.getNodeEntity().getCarrierCode() == NumberUtils
				.toInt(deviceIdFld.getText().trim()))//device id unmodified 
		{
			isSame = false;
			return isSame;
		}else//device id modified 
		{
			for(int i = 0;i < v.size();i++)
			{
				if(NumberUtils.toInt(deviceIdFld.getText().trim()) == (Integer)v.get(i))
				{
					isSame = true;
					return isSame;
				}
			}
		}
		return isSame;
	}
	public boolean isValids(Vector carrierCode){
		boolean isValid = true;
		if(null == deviceIdFld.getText() || "".equals(deviceIdFld.getText())){
			JOptionPane.showMessageDialog(this, "�豸ID����Ϊ�գ��������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		else if(NumberUtils.toInt(deviceIdFld.getText()) < 2)
		{
			JOptionPane.showMessageDialog(this, "�豸ID�������1���������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		//compare the new device id with the old device ids
		if(judgeDeviceID(carrierCode))
		{
			JOptionPane.showMessageDialog(this, "�豸ID�����ظ����룬������豸ID", "��ʾ", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		if(null == deviceTypeCombox.getSelectedItem() || "".equals(deviceTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�豸���Ͳ���Ϊ�գ���ѡ���豸����", "��ʾ", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		if(longitudeFld.getText().trim().indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "���ȱ��������ֿ�ͷ������������","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(latitudeFld.getText().trim().indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "γ�ȱ��������ֿ�ͷ������������","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		if(longitudeFld.getText().trim().length() > 36 || latitudeFld.getText().trim().length() > 36){
			JOptionPane.showMessageDialog(this, "��γ�ȳ��Ȳ��ܳ���36���ַ�������������","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(!StringUtils.isBlank(longitudeFld.getText().trim())){
			if(!NumberUtils.isNumber(longitudeFld.getText().trim())){
				JOptionPane.showMessageDialog(this, "����ֵ���ܰ������ֺ͵�ţ�����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}

		if(!StringUtils.isBlank(latitudeFld.getText().trim())){
			if(!NumberUtils.isNumber(latitudeFld.getText().trim())){
				JOptionPane.showMessageDialog(this, "γ��ֵ���ܰ������ֺ͵�ţ�����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if(null == areaCombox.getSelectedItem() || "".equals(areaCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "��������Ϊ�գ���ѡ�����", "��ʾ", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		return isValid;
	}
	//Amend 2010.06.08
	public void keyTypeControl(KeyEvent e)
	{
		char c = e.getKeyChar();
		if (c != '.' &&(c < '0' || c > '9'))
		{
			e.consume();
		}
	}
	public String getNewString(String oldString)
	{
		String newString = "";
		
		if(oldString.indexOf(".") < 0)//no dot
		{
			return oldString;
		}
		
		String headString = oldString.substring(0, oldString.indexOf(".") + 1);
		String tailString = oldString.substring(oldString.indexOf(".") + 1, oldString.length());
		
		if(tailString.length() == 0)//dot is the last char
		{
			newString = headString.substring(0, oldString.indexOf("."));
			return newString;
		}
		
		if(tailString.length() <= 6)
		{
			newString = oldString;
			return newString;
		}else
		{
			newString =  headString + tailString.substring(0, 6);
			return newString;
		}
	}
	
	private Icon reverseIntToIcon(boolean bool){
		Icon icon = null;
		if (bool){
			carrierStatus = ONLINE;
			icon = imageRegistry.getImageIcon(NetworkConstants.ONLINE);
		}else if (!bool){
			carrierStatus = OFFLINE;
			icon = imageRegistry.getImageIcon(NetworkConstants.OFFLINE);
		}
		
		return icon;
	}
	
	public void setStatusIcon(String result){
		if(statusLbl != null){
			 if ("S".equals(result)){
				 status = true;
			 }else if("F".equals(result)){
				 status = false;
			 }
			 statusLbl.setIcon(reverseIntToIcon(status));
		 }
	}
	
	@Override
	public void dispose() {
		super.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERMONITORREP, messageProcessor);
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, carrierChangeListener);
	}
	
	private final PropertyChangeListener carrierChangeListener = new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof CarrierTopNodeEntity){
				queryData();
			}
		}
	};
	
	private void clear(){
		carrierEntity = null;
		deviceIdFld.setText(StringUtils.EMPTY);
//		deviceTypeCombox.setModel(new ListComboBoxModel(null));
		addrFld.setText(StringUtils.EMPTY);
		longitudeFld.setText(StringUtils.EMPTY);
		latitudeFld.setText(StringUtils.EMPTY);
//		areaCombox.removeAllItems();
	}
	
	private class FEPEntityObject{
		FEPEntity fepEntity = null;
		public FEPEntityObject(FEPEntity fepEntity){
			this.fepEntity = fepEntity;
		}
		
		@Override
		public String toString(){
			return this.fepEntity.getCode();
		}
		public FEPEntity getFepEntity() {
			return fepEntity;
		}
	}
	
	class AreaEntityObject {
		AreaEntity areaEntity = null;
		public AreaEntityObject(AreaEntity areaEntity){
			this.areaEntity = areaEntity;
		}
		
		@Override
		public String toString(){
			return this.areaEntity.getName();
		}

		public AreaEntity getAreaEntity() {
			return areaEntity;
		}
	}
}