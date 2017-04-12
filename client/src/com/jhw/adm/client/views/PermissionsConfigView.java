package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(PermissionsConfigView.ID)
@Scope(Scopes.DESKTOP)
public class PermissionsConfigView  extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "permissionsConfigView";
	
	//右边的面板
	private JPanel rightPanel = new JPanel();
	
	private JLabel managerLbl = new JLabel();
	private JComboBox manageCombox = new JComboBox();
	private JLabel warningLevelLbl = new JLabel();
	private JPanel warningLevelPnl = new JPanel();
	
	
	private JCheckBox generalChk = new JCheckBox();
	private JCheckBox noticeChk = new JCheckBox();
	private JCheckBox seriousChk = new JCheckBox();
	private JCheckBox urgentChk = new JCheckBox();
	
	private JLabel warningModeLbl = new JLabel();
	private JPanel warningModePnl = new JPanel();
	private JCheckBox emailChk = new JCheckBox();
	private JCheckBox smsChk = new JCheckBox();

	
	private MessageOfSwitchConfigProcessorStrategy messageOfSwitchConfigProcessorStrategy  = new MessageOfSwitchConfigProcessorStrategy();
	
	private JButton saveBtn;
	
	private JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;
	
	private List<UserEntity> userEntityList = null;

	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@PostConstruct
	public void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initRightPnl();

		this.setLayout(new BorderLayout());
		this.add(rightPanel,BorderLayout.CENTER);
		
		this.setViewSize(640, 480);
		
		setResource();
	}
	
	private void initRightPnl(){
		//管理员配置
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel topMiddlePnl = new JPanel(new GridBagLayout());
		topMiddlePnl.add(managerLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,10,0),0,0));//
		topMiddlePnl.add(manageCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,40,10,0),0,0));//
		topPnl.add(topMiddlePnl);
		
		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel centerMiddlePnl = new JPanel(new GridBagLayout());
		centerMiddlePnl.setBorder(BorderFactory.createTitledBorder("告警配置"));
		centerMiddlePnl.add(warningLevelLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,5,0),0,0));//
		centerMiddlePnl.add(warningLevelPnl,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,25,5,0),0,0));//
		centerMiddlePnl.add(warningModeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));//
		centerMiddlePnl.add(warningModePnl,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,20,5,0),0,0));//
		centerPnl.add(centerMiddlePnl);
		
		warningLevelPnl.setLayout(new SpringLayout());
		warningLevelPnl.add(generalChk);
		warningLevelPnl.add(noticeChk);
		warningLevelPnl.add(seriousChk);
		warningLevelPnl.add(urgentChk);
		SpringUtilities.makeCompactGrid(warningLevelPnl, 1,4, 16, 16, 30, 10);
		
		JPanel middlePnl = new JPanel(new SpringLayout());
		middlePnl.add(emailChk);
		middlePnl.add(smsChk);
		SpringUtilities.makeCompactGrid(middlePnl, 1,2, 16, 6, 30, 16);

		warningModePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		warningModePnl.add(middlePnl);
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(topPnl,BorderLayout.NORTH);
		panel1.add(centerPnl,BorderLayout.CENTER);
//		panel1.setBorder(BorderFactory.createTitledBorder("管理员配置"));
		
		JPanel userPnl = new JPanel(new BorderLayout());
		userPnl.add(panel1,BorderLayout.SOUTH);
		
		JPanel middleCenterPnl = new JPanel(new BorderLayout());
		middleCenterPnl.add(userPnl,BorderLayout.NORTH);
		middleCenterPnl.setBorder(BorderFactory.createTitledBorder("管理员配置"));
		
		//
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(middleCenterPnl,BorderLayout.CENTER);
		rightPanel.add(bottomPnl,BorderLayout.SOUTH);
	}
	
	private void setResource(){
		this.setTitle("区域管理");
		manageCombox.setPreferredSize(new Dimension(160,manageCombox.getPreferredSize().height));
		managerLbl.setText("管理员");
		warningLevelLbl.setText("等级");
		warningModeLbl.setText("方式");
		
		generalChk.setText("普通");
		noticeChk.setText("通知");
		seriousChk.setText("严重");
		urgentChk.setText("紧急");
		emailChk.setText("电子邮件");
		smsChk.setText("短信");
		
		manageCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				setWarningInfo(e);
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		//向数据库查询所有的地区
		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		//向数据库查询所有的用户
		userEntityList = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		
		//根据查询到的用户信息构造下拉框
		setManageCombox(userEntityList);
	}
	
	/**
	 * 通过向数据库读取数据，设置manageCombox
	 */
	private void setManageCombox(List<UserEntity> userEntityList){
		if(null == userEntityList){
			return;
		}
		
		manageCombox.removeAllItems();
		int count = userEntityList.size();
		
		for (int i = 0 ;i < count; i++){
			manageCombox.addItem(new UserEntityObject(userEntityList.get(i)));
		}
	}
	
	/**
	 * 根据下拉框所选中的UserEntity显示告警配置信息
	 * @param e
	 */
	private void setWarningInfo(ItemEvent e){
		setCheckBoxDisable();
		
		UserEntity entity = null;
		JComboBox combox = (JComboBox)e.getSource();
		if (combox.getSelectedItem() instanceof UserEntityObject){
			UserEntityObject object = (UserEntityObject)combox.getSelectedItem();
			entity = object.getEntity();
		}
		
		if (null == entity){
			return;
		}
		
		//通过entity查询得到告警等级
		String levelStr = entity.getCareLevel();
		if (null != levelStr){
			String[] levels = levelStr.split(",");
			for (int i = 0 ; i < levels.length; i++){
				if (StringUtils.isNumeric(levels[i])){
					switch(NumberUtils.toInt(levels[i])){
						case Constants.URGENCY:
							urgentChk.setSelected(true);
							break;
						case Constants.SERIOUS:
							seriousChk.setSelected(true);
							break;
						case Constants.INFORM:
							noticeChk.setSelected(true);
							break;
						case Constants.GENERAL:
							generalChk.setSelected(true);
							break;
					}
				}
			}
		}
		
		//通过entity查询得到告警方式
		String styleStr = entity.getWarningStyle();
		if (null != styleStr){
			String[] styles = styleStr.split(",");
			for (int j = 0 ; j < styles.length; j++){
				if (Constants.SMS.equals(styles[j])){
					smsChk.setSelected(true);
				}
				else if (Constants.EMAIL.equals(styles[j])){
					emailChk.setSelected(true);
				}
			}
		}
	}
	
	private void setCheckBoxDisable(){
		urgentChk.setSelected(false);
		seriousChk.setSelected(false);
		noticeChk.setSelected(false);
		generalChk.setSelected(false);
		
		emailChk.setSelected(false);
		smsChk.setSelected(false);
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, text=SAVE, desc="保存区域管理员权限",role=Constants.MANAGERCODE)
	public void save(){
		
		//得到告警等级
		String levels = "";
		if (urgentChk.isSelected()){
			levels =levels + Constants.URGENCY + ",";
		}
		if (seriousChk.isSelected()){
			levels =levels + Constants.SERIOUS + ",";
		}
		if (noticeChk.isSelected()){
			levels =levels + Constants.INFORM + ",";
		}
		if (generalChk.isSelected()){
			levels =levels + Constants.GENERAL + ",";
		}
		
		if (levels.length() > 0){
			levels = levels.substring(0,levels.length()-1);
		}
		
		//得到告警方式
		String styles = "";
		if (emailChk.isSelected()){
			styles = styles + Constants.EMAIL + ",";
		}
		if (smsChk.isSelected()){
			styles = styles + Constants.SMS + ",";
		}
		if (styles.length() > 0){
			styles = styles.substring(0,styles.length() -1);
		}
		
		//得到
		UserEntity entity = null;
		if (manageCombox.getSelectedItem() instanceof UserEntityObject){
			UserEntityObject object = (UserEntityObject)manageCombox.getSelectedItem();
			entity = object.getEntity();
		}else{
			JOptionPane.showMessageDialog(this, "请选择管理员","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		entity.setCareLevel(levels);
		entity.setWarningStyle(styles);
		

		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("保存", this);
		remoteServer.getService().updateEntity(entity);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
	}
	
	
	class UserEntityObject implements Serializable{
		private UserEntity entity = null;
		
		public UserEntityObject(UserEntity entity){
			this.entity = entity;
		}
		
		/**
		 * 重写toString方法
		 */
		public String toString(){
			return this.entity.getUserName() + "|" +  this.entity.getRole().getRoleName();
		}

		public UserEntity getEntity() {
			return entity;
		}
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
}
