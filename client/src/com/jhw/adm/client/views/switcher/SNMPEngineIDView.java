package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.MessageConstant;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.SNMPEngineIDViewModel;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.MessageReceiveProcess;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SNMPEngineIDView.ID)
@Scope(Scopes.DESKTOP)
public class SNMPEngineIDView extends ViewPart implements MessageReceiveInter{
	public static final String ID = "snmpEngineIDView";

	private JLabel idLbl = new JLabel();
	
	private JTextField idFld = new JTextField();
	
	private JLabel rangeLbl = new JLabel("(16~64��ʮ�������ַ�)");
	
	//�¶˵İ�ť���
	private JPanel bottomPnl = new JPanel();
	private JButton saveBtn;
	private JButton closeBtn;
	private JButton synBtn;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ActionMap actionMap  = null;
	
	private ButtonFactory buttonFactory;
	
	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(SNMPEngineIDViewModel.ID)
	private SNMPEngineIDViewModel viewModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(MessageReceiveProcess.ID)
	private MessageReceiveProcess messageReceiveProcess;
	
	@PostConstruct
	protected void initialize(){
		init();
		
		queryData();
	}

	protected void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		idLbl.setText("����ID");
		rangeLbl.setText("(16~64��ʮ�������ַ�)");
		idFld.setColumns(40);
		//Amend 2010.06.08
		idFld.setDocument(new TextFieldPlainDocument(idFld, 64));
		
		JPanel configPnl = new JPanel(new GridBagLayout());
		
		configPnl.add(idLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(60,20,0,0),0,0));
		configPnl.add(idFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(60,60,0,0),0,0));
		configPnl.add(rangeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(60,0,0,0),0,0));
		configPnl.add(new StarLabel(),new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(60,5,0,0),0,0));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.add(configPnl);
		
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		synBtn = buttonFactory.createButton(UPLOAD);
		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		
		//ע�᷵�ص��첽��Ϣ
		messageReceiveProcess.registerReceiveMessage(this);
	}
	
	private void queryData(){
		switchNodeEntity =
			(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		if (null == switchNodeEntity){
			return ;
		}
		
//		String where = " where entity.switchNode=?";
//		Object[] parms = {switchNodeEntity};
//		List<SNMPHost> snmpHostList = (List<SNMPHost>)remoteServer.getService().findAll(SNMPHost.class, where, parms);
//		if (null == snmpHostList || snmpHostList.size() < 1){
//			return;
//		}
//		
//		setValue(snmpHostList);
	}
	
	/**
	 * �������
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="����SNMP����ID������Ϣ",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}
		
		messageReceiveProcess.openMessageDialog(this,"����");
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="����ͬ��SNMP����ID������Ϣ",role=Constants.MANAGERCODE)
	public void upSynchronize(){
		messageReceiveProcess.openMessageDialog(this,"ͬ��");
	}
	
	/**
	 * �豸����������¼�
	 * @author Administrator
	 */
	private PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	public JButton getSaveButton(){
		return this.saveBtn;
	}
	
	/**
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	public void receive(Object object){
		String message = "";
		if(MessageConstant.SUCCESS.equals((String)object)){
			message = "�����ɹ�";
		}
		else if(MessageConstant.FAILURE.equals((String)object)){
			message = "����ʧ��";
		}
		
		messageReceiveProcess.setMessage(message);
		//.......������..........
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageReceiveProcess.dispose();
	}
	
	//Amend 2010.06.04
	public boolean isValids()
	{
		boolean isValid = true;
		
		if(null == idFld.getText() || "".equals(idFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "����ID���󣬷�Χ�ǣ�16-64��ʮ�������ַ�", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((idFld.getText().trim().length() < 16) || (idFld.getText().trim().length() > 64))
		{
			JOptionPane.showMessageDialog(this, "����ID���󣬷�Χ�ǣ�16-64��ʮ�������ַ�", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(!isOXChar(idFld.getText().trim()))
		{
			JOptionPane.showMessageDialog(this, "����ID���󣬷�Χ�ǣ�16-64��ʮ�������ַ�", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
	public boolean isOXChar(String oldString)
	{
		for(int i = 0;i < oldString.length();i++)
		{
			String c = "" + oldString.charAt(i);
			boolean b = c.matches("[0-9a-fA-F]+");
			if(b)
			{
				continue;
			}else
			{
				return false;
			}
		}
		return true;
	}
}
