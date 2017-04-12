package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.EmailConfigEntity;

@Component(ConfigureSysServerAddrView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureSysServerAddrView extends ViewPart{
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureSysServerAddrView";
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureSysServerAddrView.class);
	
	private final JPanel centerPnl = new JPanel();
	private final JPanel emailPnl = new JPanel();
	private final JLabel emailServerLbl = new JLabel();
	private final JTextField emailServerFld = new JTextField();
	
	private final JLabel popServerLbl = new JLabel();
	private final JTextField popServerFld = new JTextField();
	
	private final JLabel popUserLbl = new JLabel();
	private final JTextField popUserFld = new JTextField();
	
	private final JLabel pwdLbl = new JLabel();
	private final JPasswordField pwdFld = new JPasswordField("",20);
	
	private final JPanel smsPnl = new JPanel();

	private final JLabel serialLbl = new JLabel();
	private final JComboBox serialComboBox = new JComboBox();
	private final JRadioButton smsModemBtn = new JRadioButton();
	
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private final static String RANGE_VALUE = "(1-36个字符)";
	
	private final static String PASSWORD_LENGTH = "6-16个字符";
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private EmailConfigEntity emailConfigEntity = null;
	private List<EmailConfigEntity> emailConfigEntityList = null;
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	//从数据库取值
	@SuppressWarnings("unchecked")
	private void queryData()
	{
		emailConfigEntityList = (List<EmailConfigEntity>) remoteServer.getService().findAll(EmailConfigEntity.class);
		if(emailConfigEntityList.size() == 0){
			return;
		}
		emailConfigEntity = emailConfigEntityList.get(0);
		setValue();
		
	}
	private void setValue()
	{
		clear();
		emailServerFld.setText(emailConfigEntity.getEmailServer());
		popUserFld.setText(emailConfigEntity.getAccounts());
		pwdFld.setText(emailConfigEntity.getPassword());
		serialComboBox.setSelectedItem(emailConfigEntity.getSerialPortName());
	}
	private void clear()
	{
		emailServerFld.setText("");
		popUserFld.setText("");
		pwdFld.setText("");
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
		initEmailPnl();
		initSmsPnl();
		centerPnl.setLayout(new GridBagLayout());
		centerPnl.add(emailPnl,new GridBagConstraints(0,0,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		centerPnl.add(smsPnl,new GridBagConstraints(0,1,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		centerPnl.add(new JPanel(),new GridBagConstraints(0,2,1,1,1.0,1.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
	}
	
	private void initEmailPnl(){
		emailPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel middlePnl = new JPanel(new GridBagLayout());
		middlePnl.add(emailServerLbl,new GridBagConstraints(0,0,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		middlePnl.add(emailServerFld,new GridBagConstraints(1,0,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		middlePnl.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		middlePnl.add(popUserLbl,new GridBagConstraints(0,2,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		middlePnl.add(popUserFld,new GridBagConstraints(1,2,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		middlePnl.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		middlePnl.add(pwdLbl,new GridBagConstraints(0,3,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		middlePnl.add(pwdFld,new GridBagConstraints(1,3,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		middlePnl.add(new StarLabel("(" + PASSWORD_LENGTH + ")"),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		emailPnl.add(middlePnl);
		emailPnl.setBorder(BorderFactory.createTitledBorder("邮件服务器配置"));
	}
	
	private void initSmsPnl(){
		smsPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		serialComboBox.setPreferredSize(new Dimension(100, serialComboBox.getPreferredSize().height));
		panel.add(serialLbl,new GridBagConstraints(0,0,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));		
		panel.add(serialComboBox,new GridBagConstraints(1,0,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		initSerialPort();
		smsPnl.add(panel);
		smsPnl.setBorder(BorderFactory.createTitledBorder("短信Moderm配置"));
	}
	
	private void initSerialPort(){
		List<String> serialPortList = remoteServer.getNmsService().queryAllSerialPort();
		if(serialPortList.size() == 0){
			return;
		}
		for(String serialPortName : serialPortList){
			serialComboBox.addItem(serialPortName);
		}
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
		emailServerLbl.setText("发送邮件服务器(SMTP)");
		popServerLbl.setText("POP3服务器");
		popUserLbl.setText("账户");
		pwdLbl.setText("密码");
		
		serialLbl.setText("串口");
		smsModemBtn.setText("短信Modem");
		
		emailServerFld.setColumns(25);
		
		emailServerFld.setDocument(new TextFieldPlainDocument(emailServerFld));
		popServerFld.setDocument(new TextFieldPlainDocument(popServerFld));
		popUserFld.setDocument(new TextFieldPlainDocument(popUserFld));
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存告警服务器配置",role=Constants.MANAGERCODE)
	public void save()
	{
		if(!isValids())
		{
			return;
		}
		if(null == emailConfigEntity)//用于第一次
		{
			emailConfigEntity = new EmailConfigEntity();
		}
		//构造需要下发的entity
		emailConfigEntity.setEmailServer(emailServerFld.getText().trim());
		emailConfigEntity.setAccounts(popUserFld.getText().trim());
		emailConfigEntity.setPassword(String.valueOf(pwdFld.getPassword()));
		if(null != serialComboBox.getSelectedItem() && !"".equals(serialComboBox.getSelectedItem().toString())){
			emailConfigEntity.setSerialPortName(serialComboBox.getSelectedItem().toString());			
		}
		
		Task task = new RequestTask(emailConfigEntity);
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		private EmailConfigEntity emailConfigEntity = null;
		public RequestTask(EmailConfigEntity emailConfigEntity){
			this.emailConfigEntity = emailConfigEntity;
		}
		
		@Override
		public void run() {
			try{
				if (null == emailConfigEntity.getId()) {
					remoteServer.getService().saveEntity(emailConfigEntity);
				} else {
					remoteServer.getService().updateEntity(emailConfigEntity);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存告警服务器配置异常");
				queryData();
				LOG.error("ConfigureSysServerAddrView.save() error", e);
			}
			strategy.showNormalMessage("保存告警服务器配置成功");
			queryData();
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	public boolean isValids()
	{
		boolean isValid = true;
		if(null == emailServerFld.getText() || "".equals(emailServerFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "邮件服务器不能为空，请输入邮件服务器", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(emailServerFld.getText().trim().length() < 1 || emailServerFld.getText().trim().length() > 36){
			JOptionPane.showMessageDialog(this, "邮件服务器错误，范围为：1-36个字符", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
//		else if(emailServerFld.getText().trim().length() < 5)
//		{
//			JOptionPane.showMessageDialog(this, "邮件服务器格式错误，请输入邮件服务器", "提示", JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
		else if(!"smtp.".equals(emailServerFld.getText().trim().substring(0, 5).toLowerCase())){
			JOptionPane.showMessageDialog(this, "邮件服务器格式错误，请输入邮件服务器", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == popUserFld.getText() || "".equals(popUserFld.getText())){
			JOptionPane.showMessageDialog(this, "账户不能为空，请输入账户", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(popUserFld.getText().trim().length() < 1 || popUserFld.getText().trim().length() > 36){
			JOptionPane.showMessageDialog(this, "账户格式错误，范围为：1-36个字符", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(!getEmail(popUserFld.getText().trim()))
		{
			JOptionPane.showMessageDialog(this, "账户格式错误，请输入账户", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == pwdFld.getText() || "".equals(String.valueOf(pwdFld.getPassword())))
		{
			JOptionPane.showMessageDialog(this, "密码不能为空，请输入密码", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if((String.valueOf(pwdFld.getPassword())).length() > 16 
				|| (String.valueOf(pwdFld.getPassword())).length() <6)
		{
			JOptionPane.showMessageDialog(this, "密码长度范围为" + PASSWORD_LENGTH + "，请重新输入新密码", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	private boolean getEmail(String line){
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(line);
        return m.find();
    }
}
