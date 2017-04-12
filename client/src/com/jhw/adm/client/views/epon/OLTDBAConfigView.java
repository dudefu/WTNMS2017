package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTDBA;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(OLTDBAConfigView.ID)
@Scope(Scopes.DESKTOP)
public class OLTDBAConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTDBAConfigView";
	
	private final JPanel centerPnl = new JPanel();
	
	private final JLabel dbaModeLbl = new JLabel();
	private final JComboBox dbaModeBox = new JComboBox();
	
	private final JLabel dbaAlgorithmLbl = new JLabel();
	private final JComboBox dbaAlgorithmBox = new JComboBox();
	
	private final JLabel dbaCycleLbl = new JLabel();
	private final NumberField dbaCycleFld = new NumberField();
	
	private final JLabel dbaFreqLbl = new JLabel();
	private final NumberField dbaFreqFld = new NumberField();
	
	private final JLabel dbaTimeLbl = new JLabel();
	private final NumberField dbaTimeFld = new NumberField();
	
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn;
	private JButton closeBtn ;
	
	private OLTDBA oltDBA;
	private OLTEntity oltNodeEntity = null;
	private ButtonFactory buttonFactory;
		
	private final static String[] MODE = {"Hardware DBA"};
	private final static String[] ALGORITHM= {"NONWORKCONSERV"};
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private static final Logger LOG = LoggerFactory.getLogger(OLTDBAConfigView.class);

	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initCenterPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(dbaModeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		panel.add(dbaModeBox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		panel.add(dbaAlgorithmLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(dbaAlgorithmBox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		
		panel.add(dbaCycleLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(dbaCycleFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		
		panel.add(dbaFreqLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(dbaFreqFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		
		panel.add(dbaTimeLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(dbaTimeFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		
		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(panel);
		centerPnl.setBorder(BorderFactory.createTitledBorder("DBA配置"));
	}
	
	private void initBottomPnl(){
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
	}
	
	private void setResource(){
		dbaModeLbl.setText("DBA模式");
		dbaAlgorithmLbl.setText("DBA算法");
		dbaCycleLbl.setText("DBA周期时间");
		dbaFreqLbl.setText("DBA发现频率");
		dbaTimeLbl.setText("DBA发现时间");
		dbaCycleFld.setColumns(25);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	private void queryData(){
		oltNodeEntity = (OLTEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTEntity.class);
		
		if (null == oltNodeEntity){
			return ;
		}
		
		setCombox();
		
		String where = " where entity.oltEntity=?";
		Object[] parms = {oltNodeEntity};
		
		List<OLTDBA> oltDBAList = (List<OLTDBA>)remoteServer.getService().findAll(OLTDBA.class,where,parms);
		if (null == oltDBAList || oltDBAList.size() < 1){
			return;
		}
		oltDBA = oltDBAList.get(0);
		
		setValue();
	}
	
	private void setCombox(){
		dbaModeBox.removeAllItems();
		dbaAlgorithmBox.removeAllItems();
		for (String str:MODE){
			dbaModeBox.addItem(str);
		}
		for (String str:ALGORITHM){
			dbaAlgorithmBox.addItem(str);
		}
	}
	
	private void setValue(){
		String mode = reverseModeValue(oltDBA.getDbaMoble());
		String algorithm = reverseAlgorithmValue(oltDBA.getDbaAlgorithm());
		int cycleTime = oltDBA.getDbaCycleTime();
		int frequency = oltDBA.getDbafrequency();
		int findTime = oltDBA.getDbaFindTime();	
		
		dbaModeBox.setSelectedItem(mode);
		dbaAlgorithmBox.setSelectedItem(algorithm);
		dbaCycleFld.setText(cycleTime + "");
		dbaFreqFld.setText(frequency + "");
		dbaTimeFld.setText(findTime + "");
	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存OLT DBA配置",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		if (null == oltDBA){
			oltDBA = new OLTDBA();
		}
		List<Serializable> oltDBAList = new ArrayList<Serializable>();
		
		int mode = reverseModeValue(dbaModeBox.getSelectedItem().toString());
		int algorithm = reverseAlgorithmValue(dbaAlgorithmBox.getSelectedItem().toString());
		int cycleTime = NumberUtils.toInt(dbaCycleFld.getText());
		int frequency = NumberUtils.toInt(dbaFreqFld.getText());
		int findTime = NumberUtils.toInt(dbaTimeFld.getText());	
		
		oltDBA.setDbaMoble(mode);
		oltDBA.setDbaAlgorithm(algorithm);
		oltDBA.setDbaCycleTime(cycleTime);
		oltDBA.setDbafrequency(frequency);
		oltDBA.setDbaFindTime(findTime);
		oltDBA.setOltEntity(oltNodeEntity);
		oltDBAList.add(oltDBA);
		
		String ipValue = oltNodeEntity.getIpValue();
		
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue,
					MessageNoConstants.DBACONFIG, oltDBAList,
					clientModel.getCurrentUser().getUserName(),
					clientModel.getLocalAddress(), Constants.DEV_OLT, result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("OLTDBAConfigView's save() is failure:{}", e);
		}
		
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}
	
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	private String reverseModeValue(int value){
		String str = "";
		if (value == 1){
			str = "Hardware DBA";
		}
		return str;
	}
	private int reverseModeValue(String str){
		int value = 0;
		if (str.equals("Hardware DBA")){
			value = 1;
		}
		return value;
	}
	
	private String reverseAlgorithmValue(int value){
		String str = "";
		if (value == 1){
			str = "NONWORKCONSERV";
		}
		return str;
	}
	private int reverseAlgorithmValue(String str){
		int value = 0;
		if (str.equals("NONWORKCONSERV")){
			value = 1;
		}
		return value;
	}
	
	/**
	 * 设备浏览器的时间监听
	 * 当选择不同的设备时，根据switchNodeEntity,重新查询数据库
	 * @author Administrator
	 */
	private final PropertyChangeListener eponNodeChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof EponTopoEntity){
				queryData();
			}
		}
	};
	
	private void clear(){
		dbaModeBox.setSelectedItem("");
		dbaAlgorithmBox.setSelectedItem("");
		dbaCycleFld.setText("");
		dbaFreqFld.setText("");
		dbaTimeFld.setText("");	
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	public boolean isValids(){
		boolean isValid = true;
		
		if (null == this.oltNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择EPON设备","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == dbaCycleFld.getText().trim() || "".equals(dbaCycleFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "DBA周期时间错误","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == dbaFreqFld.getText().trim() || "".equals(dbaFreqFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "DBA发现频率错误","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == dbaTimeFld.getText().trim() || "".equals(dbaTimeFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "DBA发现时间错误","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
}
