package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.CopyDataType;

@Component(AutoBakupParamView.ID)
@Scope(Scopes.DESKTOP)
public class AutoBakupParamView extends ViewPart{

	private static final long serialVersionUID = 1L;
	public static final String ID = "autoBakupParamView";
	
	private JButton saveBtn;
	private JButton closeBtn;
	private JComboBox strategyBox;
	private ButtonFactory buttonFactory;
	
	private CopyDataType copyDataType;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private MessageOfSwitchConfigProcessorStrategy messageOfSwitchConfigProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	@PostConstruct
	public void initialize(){
		
		setTitle("自动备份参数");
		setSize(400, 300);
		setLayout(new BorderLayout());
		
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel parent = new JPanel();
		createContents(parent);
		add(parent, BorderLayout.CENTER);
		queryData();
	}
	
	@SuppressWarnings("unchecked")
	private void queryData() {
		
		List<CopyDataType> copyDataTypeList = (List<CopyDataType>) remoteServer.getService().findAll(CopyDataType.class);
		if(copyDataTypeList.size() == 0){
			copyDataType = new CopyDataType();
			return;
		}
		copyDataType = copyDataTypeList.get(0);
		setValue(copyDataType);
	}

	private void setValue(CopyDataType copyDataType) {
		
		if(copyDataType.getCopyType() == 0){
			strategyBox.setSelectedItem("无");
		}else if(copyDataType.getCopyType() == 1){
			strategyBox.setSelectedItem("每天");
		}else if(copyDataType.getCopyType() == 2){
			strategyBox.setSelectedItem("每周");
		}else if(copyDataType.getCopyType() == 3){
			strategyBox.setSelectedItem("每月");
		}
	}

	public void createContents(JPanel parent){
		parent.setLayout(new BorderLayout());
		
		JPanel paramPanel = new JPanel();
		createBackupContents(paramPanel);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		buttonPanel.add(saveBtn);
		buttonPanel.add(closeBtn);

		parent.add(paramPanel, BorderLayout.CENTER);
		parent.add(buttonPanel, BorderLayout.PAGE_END);
	}
	//无：0；每天：1；每周：2；每月：3——copyDataType
	private void createBackupContents(JPanel parent) {
		parent.setLayout(new BorderLayout());

		strategyBox = new JComboBox(new String[] { 
				"无", "每天", "每周", "每月"});
		strategyBox.setPreferredSize(new Dimension(150, strategyBox.getPreferredSize().height));
		
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel descriptionPanel = new JPanel(new SpringLayout());
		wrapper.add(descriptionPanel);
		
		descriptionPanel.add(new JLabel("1. 选择“无”，系统将不会自动备份"));
		descriptionPanel.add(new JLabel("2. 选择“每天”，系统将在每天0点自动备份"));
		descriptionPanel.add(new JLabel("3. 选择“每周”，系统将在每周最后一天0点自动备份"));
		descriptionPanel.add(new JLabel("4. 选择“每月”，系统将在每月最后一天0点自动备份"));
		descriptionPanel.add(strategyBox);

		SpringUtilities.makeCompactGrid(descriptionPanel, 5, 1, 6, 6, 6, 6);
		
		parent.add(wrapper, BorderLayout.CENTER);
	}
	
	@ViewAction(name=SAVE,icon=ButtonConstants.SAVE,desc="保存自动备份参数", role=Constants.MANAGERCODE)
	public void save(){
		
		String selectedParam = strategyBox.getSelectedItem().toString();
		
		copyDataType.setCopyType(getType(selectedParam));
		copyDataType.setCreateDate(new Date());
		copyDataType.setUserName(clientModel.getCurrentUser().getUserName());
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("保存", this);
		remoteServer.getService().updateEntity(copyDataType);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
	}
	
	public int getType(String param){
		
		if("无".equals(param)){
			return 0;
		}else if("每天".equals(param)){
			return 1;
		}else if("每周".equals(param)){
			return 2;
		}else if("每月".equals(param)){
			return 3;
		}
		return 0;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
}