package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.TrapConfigViewModel;
import com.jhw.adm.client.swing.CheckInCombox;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningType;

@Component(TrapConfigView.ID)
@Scope(Scopes.DESKTOP)
public class TrapConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "trapConfigView";
	
	private final JPanel centerPnl = new JPanel(); 
	
	private final JPanel noticePnl = new JPanel();
	private final JLabel coldStartLbl = new JLabel();
	private final JComboBox coldStartCombox = new JComboBox();
	private final CheckInCombox coldStartChkCombox = new CheckInCombox(); 
	
	private final JLabel hotStartLbl = new JLabel();
	private final JComboBox hotStartCombox = new JComboBox();
	private final CheckInCombox hotStartChkCombox = new CheckInCombox(); 
	
	private final JLabel linkupLbl = new JLabel();
	private final JComboBox linkupCombox = new JComboBox();
	private final CheckInCombox linkupChkCombox = new CheckInCombox();
	
	private final static String[] NOTICE_LEVEL = {"普通","通知"}; 
	
	private final JPanel warningPnl = new JPanel();
	private final JLabel linkdownLbl = new JLabel();
	private final JComboBox linkdownCombox = new JComboBox();
	private final CheckInCombox linkdownChkCombox = new CheckInCombox();
	
	private final JLabel flowLbl = new JLabel();
	private final JComboBox flowCombox = new JComboBox();
	private final CheckInCombox flowChkCombox = new CheckInCombox();
	
	private final JLabel authFailLbl = new JLabel();
	private final JComboBox authFailCombox = new JComboBox();
	private final CheckInCombox authFailChkCombox = new CheckInCombox();
	
	private final JLabel egpLbl = new JLabel();
	private final JComboBox egpCombox = new JComboBox();
	private final CheckInCombox egpChkCombox = new CheckInCombox();
	
	private final static String[] WARNING_LEVEL = {"严重","紧急"}; 
	
	
	//0:冷启动  1:热启动  2:端口断开  3:端口连通 4:认证失败  5:EGP邻居故障 6:端口流量
	private final String[] labelStr = {"冷启动","热启动","端口断开","端口连通","认证失败","EGP邻居故障","端口流量"};

	private final static String[] LEVEL = {"普通","通知","严重","紧急"}; 

	private final static String[] PATTERN = {"消息","声音"}; 
	
	//
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn ;
	private JButton closeBtn ;
	
	/**
	 * 判断是否有初始值，如果有为true，否则为false。默认为false
	 * 当为false时，保存应该用save下发(保存)，当为true时，保存应该用update下发(修改)
	 */
	private boolean isInitialize = false;
	
	private ButtonFactory buttonFactory;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(TrapConfigViewModel.ID)
	private TrapConfigViewModel viewModel;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel; 
	
	private MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();

	@PostConstruct
	protected void initialize() {
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
		this.setViewSize(600, 450);
		
		setResource();
	}
	
	private void initCenterPnl(){
		noticePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel1 = new JPanel(new GridBagLayout());
		panel1.add(coldStartLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,12,50),0,0));
		panel1.add(coldStartCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,69,12,50),0,0));
		panel1.add(coldStartChkCombox,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,12,0),0,0));
		
		panel1.add(hotStartLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,12,50),0,0));
		panel1.add(hotStartCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,69,12,50),0,0));
		panel1.add(hotStartChkCombox,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,12,0),0,0));
		
		panel1.add(linkupLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,50),0,0));
		panel1.add(linkupCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,69,10,50),0,0));
		panel1.add(linkupChkCombox,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,10,0),0,0));
		noticePnl.add(panel1);
		noticePnl.setBorder(BorderFactory.createTitledBorder("通知"));
		

		warningPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.add(linkdownLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,12,50),0,0));
		panel2.add(linkdownCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,12,50),0,0));
		panel2.add(linkdownChkCombox,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,12,0),0,0));
		
		panel2.add(flowLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,12,50),0,0));
		panel2.add(flowCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,12,50),0,0));
		panel2.add(flowChkCombox,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,12,0),0,0));
		
		panel2.add(authFailLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,12,50),0,0));
		panel2.add(authFailCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,12,50),0,0));
		panel2.add(authFailChkCombox,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,12,0),0,0));
		
		panel2.add(egpLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,50),0,0));
		panel2.add(egpCombox,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,10,50),0,0));
		panel2.add(egpChkCombox,new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,50,10,0),0,0));
		warningPnl.add(panel2);
		warningPnl.setBorder(BorderFactory.createTitledBorder("告警"));
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(noticePnl,BorderLayout.NORTH);
		centerPnl.add(warningPnl,BorderLayout.CENTER);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		this.setTitle("Trap事件配置");

		coldStartLbl.setText("冷启动");
		hotStartLbl.setText("热启动");
		linkupLbl.setText("端口连通");
		linkdownLbl.setText("端口断开");
		flowLbl.setText("端口流量");
		authFailLbl.setText("认证失败");
		egpLbl.setText("EGP邻居故障");
		
		coldStartCombox.setPreferredSize(new Dimension(100,coldStartCombox.getPreferredSize().height));
		hotStartCombox.setPreferredSize(new Dimension(100,hotStartCombox.getPreferredSize().height));
		linkupCombox.setPreferredSize(new Dimension(100,linkupCombox.getPreferredSize().height));
		linkdownCombox.setPreferredSize(new Dimension(100,linkdownCombox.getPreferredSize().height));
		flowCombox.setPreferredSize(new Dimension(100,flowCombox.getPreferredSize().height));
		authFailCombox.setPreferredSize(new Dimension(100,authFailCombox.getPreferredSize().height));
		egpCombox.setPreferredSize(new Dimension(100,egpCombox.getPreferredSize().height));
		coldStartChkCombox.setWidth(100);
		hotStartChkCombox.setWidth(100);
		linkupChkCombox.setWidth(100);
		linkdownChkCombox.setWidth(100);
		flowChkCombox.setWidth(100);
		authFailChkCombox.setWidth(100);
		egpChkCombox.setWidth(100);
		
		for(int i = 0 ; i < NOTICE_LEVEL.length ; i++){
			coldStartCombox.addItem(NOTICE_LEVEL[i]);
			hotStartCombox.addItem(NOTICE_LEVEL[i]);
			linkupCombox.addItem(NOTICE_LEVEL[i]);
			
			coldStartCombox.setSelectedItem(null);
			hotStartCombox.setSelectedItem(null);
			linkupCombox.setSelectedItem(null);
		}
		
		for(int i = 0 ; i < WARNING_LEVEL.length ; i++){
			linkdownCombox.addItem(WARNING_LEVEL[i]);
			flowCombox.addItem(WARNING_LEVEL[i]);
			authFailCombox.addItem(WARNING_LEVEL[i]);
			egpCombox.addItem(WARNING_LEVEL[i]);
			
			linkdownCombox.setSelectedItem(null);
			flowCombox.setSelectedItem(null);
			authFailCombox.setSelectedItem(null);
			egpCombox.setSelectedItem(null);
		}
		
		for (int k = 0 ; k < PATTERN.length; k++){
			coldStartChkCombox.addItem(PATTERN[k]);
			hotStartChkCombox.addItem(PATTERN[k]);
			linkupChkCombox.addItem(PATTERN[k]);
			linkdownChkCombox.addItem(PATTERN[k]);
			flowChkCombox.addItem(PATTERN[k]);
			authFailChkCombox.addItem(PATTERN[k]);
			egpChkCombox.addItem(PATTERN[k]);
		}
		
		authFailCombox.setEnabled(false);
		egpCombox.setEnabled(false);
		authFailChkCombox.setEnabled(false);
		egpChkCombox.setEnabled(false);
	}
	
	private void queryData(){
		List<WarningType> warningTypeList = (List<WarningType>)remoteServer.getService().findAll(WarningType.class);
		if (null == warningTypeList){
			return;
		}
		setValue(warningTypeList);
		alarmModel.setWarningTypes(warningTypeList);
	}
	
	/**
	 * 设置控件的值
	 * @param warningTypeList
	 */
	private void setValue(List<WarningType> warningTypeList){
		viewModel.setWarningTypeList(warningTypeList);
		
		int count = warningTypeList.size();
		if (count < 1){
			isInitialize = false;
		}
		else{
			isInitialize = true;
		}
		
		for (int i = 0 ; i < count; i++){
			WarningType warningType = warningTypeList.get(i); 
			
			int type = warningType.getWarningType();
			int level = warningType.getWarningLevel();
			String style = warningType.getWarningStyle();
			switch(type){
				case Constants.COLDSTART:
					setComponentValue(coldStartCombox,coldStartChkCombox,level,style);
					break;
				case Constants.WARMSTART:
					setComponentValue(hotStartCombox,hotStartChkCombox,level,style);
					break;
				case Constants.LINKDOWN:
					setComponentValue(linkdownCombox,linkdownChkCombox,level,style);
					break;
				case Constants.LINKUP:
					setComponentValue(linkupCombox,linkupChkCombox,level,style);
					break;
				case Constants.REMONTHING:	
					setComponentValue(flowCombox,flowChkCombox,level,style);
					break;
			}
		}
	}
	
	private void setComponentValue(JComboBox combox,CheckInCombox checkCombox,int level,String style){
		combox.setSelectedItem(reverseIntToStringLevel(level));
		checkCombox.clearSelected();
		if (null == style || "".equals(style)){
			return;
		}
		String[] styleStr = style.split(",");
		for (int k = 0 ; k < styleStr.length; k++){
			checkCombox.setSelected(reverseIntToStringStyle(styleStr[k]), true);
		}
	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存Trap事件配置",role=Constants.MANAGERCODE)
	public void save(){
		
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
		if (isInitialize){
			List<WarningType> dataList = viewModel.getWarningTypeList();
			int count = dataList.size();

			for (int i = 0 ; i < count; i++){
				int type = dataList.get(i).getWarningType();
				int level = 0 ;
				String style = "";
				switch(type){
					case Constants.COLDSTART: 
						level = reverseStringToIntLevel(coldStartCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = coldStartChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.WARMSTART:
						level = reverseStringToIntLevel(hotStartCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = hotStartChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.LINKDOWN:
						level = reverseStringToIntLevel(linkdownCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = linkdownChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.LINKUP:
						level = reverseStringToIntLevel(linkupCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = linkupChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.REMONTHING:
						level = reverseStringToIntLevel(flowCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = flowChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
				}

				dataList.get(i).setWarningLevel(level);
				dataList.get(i).setWarningStyle(style);
			}
			
			remoteServer.getService().updateEntities(dataList);
		}
		else{
			List<WarningType> dataList = new ArrayList<WarningType>();
			int count = labelStr.length;

			for (int i = 0 ; i <= count; i++){
				if (4 == i || 5 == i || 6 == i){
					continue;
				}
				int type = i;
				int level = 0;
				String style = "";
				switch(type){
					case Constants.COLDSTART:
						level = reverseStringToIntLevel(coldStartCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = coldStartChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.WARMSTART:
						level = reverseStringToIntLevel(hotStartCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = hotStartChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.LINKDOWN:
						level = reverseStringToIntLevel(linkdownCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = linkdownChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.LINKUP:
						level = reverseStringToIntLevel(linkupCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = linkupChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
					case Constants.REMONTHING:
						level = reverseStringToIntLevel(flowCombox.getSelectedItem().toString());
						for (int k = 0 ; k < PATTERN.length; k++){
							boolean isSelected = flowChkCombox.getSelected(k);
							if (isSelected){
								style = style + reverseIntToStringStyle(k) + ",";
							}
						}
						if (style.length() > 0){
							style = style.substring(0,style.length()-1);
						}
						break;
				}

				WarningType warningType = new WarningType();
				warningType.setWarningType(type);
				warningType.setWarningLevel(level);
				warningType.setWarningStyle(style);
				
				dataList.add(warningType);
			}
			remoteServer.getService().saveEntities(dataList);
		}
		messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		queryData();
	}
	
	private String reverseIntToStringLevel(int level){
		String str = "";
		switch(level){
			case Constants.URGENCY:
				str = "紧急";
				break;
			case Constants.SERIOUS:
				str = "严重";
				break;
			case Constants.INFORM:
				str = "通知";
				break;
			case Constants.GENERAL:
				str = "普通";
				break;
		}
		
		return str;
	}
	
	private int reverseStringToIntLevel(String str){
		int level = -1;
		if ("紧急".equals(str)){
			level = Constants.URGENCY;
		}
		else if ("严重".equals(str)){
			level = Constants.SERIOUS;
		}
		else if("通知".equals(str)){
			level = Constants.INFORM;
		}
		else if("普通".equals(str)){
			level = Constants.GENERAL;
		}
		
		return level;
	}
	
	private String reverseIntToStringStyle(int style){
		String str = "";
		switch(style){
			case 0:
				str = Constants.MESSAGE;
				break;
			case 1:
				str = Constants.VOICE;
				break;
		}
		
		return str;
	}
	private int reverseIntToStringStyle(String str){
		int style = -1;
		if (Constants.MESSAGE.equals(str)){
			style = 0;
		}
		else if (Constants.VOICE.equals(str)){
			style = 1;
		}
		
		return style;
	}
}
