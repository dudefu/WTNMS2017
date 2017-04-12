package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(ModifyPasswordView.ID)
@Scope(Scopes.DESKTOP)
public class ModifyPasswordView extends ViewPart {
	public static final String ID = "modifyPasswordView";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ModifyPasswordView.class);
	
	private ButtonFactory buttonFactory;
	
	private JTextField userNameFld = new JTextField();
	private JPasswordField oldPwdFld = new JPasswordField("", 20);
	private JPasswordField newPwdFld = new JPasswordField("", 20);
	private JPasswordField nextNewPwdFld = new JPasswordField("", 20);
	
	private static final String PASSWORD_LENGTH = "6-16个字符";
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@PostConstruct
	protected void initialize() {
		setTitle("修改密码");
		setViewSize(400, 280);
		setLayout(new BorderLayout());
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		buttonFactory = actionManager.getButtonFactory(this); 
		JButton closeButton = buttonFactory.createCloseButton();
		toolPanel.add(buttonFactory.createButton(SAVE));
		toolPanel.add(closeButton);
		setCloseButton(closeButton);

		add(toolPanel, BorderLayout.PAGE_END);
		JPanel detail = new JPanel();
		createDetail(detail);

		add(detail, BorderLayout.CENTER);
		setValue();
	}
	
	private void createDetail(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel container = new JPanel(new GridBagLayout());
		parent.add(container);
		userNameFld.setEditable(false);
		userNameFld.setBackground(Color.WHITE);
		
		container.add(new JLabel("用户"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(userNameFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,5,0),0,0));
		
		container.add(new JLabel("旧密码"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(oldPwdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,5,0),0,0));
		container.add(new StarLabel(),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));

		container.add(new JLabel("新密码"),new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(newPwdFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,5,0),0,0));
		container.add(new StarLabel("(" + PASSWORD_LENGTH + ")"),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));

		container.add(new JLabel("确认密码"),new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(nextNewPwdFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,5,0),0,0));
		container.add(new StarLabel("(" + PASSWORD_LENGTH + ")"),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		
		parent.setBorder(BorderFactory.createTitledBorder("修改密码"));
	}
	
	private void setValue(){
		userNameFld.setText(clientModel.getCurrentUser().getUserName());
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存修改后的密码",role=Constants.MANAGERCODE)
	public void save(){
		if (!isValids()){
			return;
		}
		clientModel.getCurrentUser().setPassword(String.valueOf(newPwdFld.getPassword()));
		
		Task task = new RequestTask(clientModel.getCurrentUser());
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{

		private UserEntity userEntity;
		public RequestTask(UserEntity userEntity){
			this.userEntity = userEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getNmsService().updateUserPwd(userEntity);
//				remoteServer.getService().updateEntity(this.userEntity);
			}catch(Exception e){
				strategy.showErrorMessage("保存密码异常");
				LOG.error("ModifyPasswordView.updateEntity() error", e);
			}
			strategy.showNormalMessage("保存密码成功");
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
	
	private boolean isValids(){
		String currentPwd = clientModel.getCurrentUser().getPassword();

		String oldPwd = String.valueOf(oldPwdFld.getPassword());
		String newPwd = String.valueOf(newPwdFld.getPassword());
		String nextNexPwd = String.valueOf(nextNewPwdFld.getPassword());
		if (null == oldPwd || "".equals(oldPwd)){
			JOptionPane.showMessageDialog(this, "旧密码不能为空，请输入旧密码","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if (!currentPwd.equals(oldPwd)){
			JOptionPane.showMessageDialog(this, "输入旧密码错误","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if(null == newPwd || "".equals(newPwd)){
			JOptionPane.showMessageDialog(this, "新密码不能为空，请输入新密码","提示",JOptionPane.NO_OPTION);
			return false;
		}else if(newPwd.length() < 6 || newPwd.length() > 16){
			JOptionPane.showMessageDialog(this, "密码长度范围为" + PASSWORD_LENGTH + "，请重新输入新密码","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if(null == nextNexPwd || "".equals(nextNexPwd)){
			JOptionPane.showMessageDialog(this, "确认密码不能为空，请输入确认密码","提示",JOptionPane.NO_OPTION);
			return false;
		}else if(nextNexPwd.length() < 6 || nextNexPwd.length() > 16){
			JOptionPane.showMessageDialog(this, "密码长度范围为" + PASSWORD_LENGTH + "，请重新输入确认密码","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if(!newPwd.equals(nextNexPwd)){
			JOptionPane.showMessageDialog(this, "两次输入的密码不相同","提示",JOptionPane.NO_OPTION);
			return false;
		}
		
		String regex = "[a-zA-Z0-9]{1,}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher =pattern.matcher(newPwd);
		Matcher matcher2 = pattern.matcher(nextNexPwd);
		if((!matcher.matches()) || (!matcher2.matches())){
			JOptionPane.showMessageDialog(this, "新密码或确认密码只能输入数字和字母，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		return true;
	}
}