package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.Constants;

@Component(ConfigureSnmpHostView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureSnmpHostView extends ViewPart {
	
	@PostConstruct
	protected void initialize() {
		setViewSize(480, 320);
		setLayout(new BorderLayout());

		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		createOptionPanel(optionPanel);
		JPanel hostPanel = new JPanel();
		hostPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		createHostPanel(hostPanel);
		JPanel actionPanel = new JPanel();
		createActionPanel(actionPanel);
		add(optionPanel,BorderLayout.PAGE_START);
		add(hostPanel,BorderLayout.CENTER);
		add(actionPanel,BorderLayout.PAGE_END);
	}
	
	private void createOptionPanel(JPanel parent) {
		JPanel container = new JPanel();
		parent.add(container);
		GridLayout gridLayout = new GridLayout(3, 2);
		container.setLayout(gridLayout);
		container.add(new JLabel("启用SNMP 只读(public)共同体"));
		container.add(new JCheckBox());
		container.add(new JLabel("启用SNMP 读写(private)共同体"));
		container.add(new JCheckBox());
		container.add(new JLabel("配置SNMP服务器地址"));
		container.add(new JCheckBox());
	}
	
	private void createHostPanel(JPanel parent) {
		JPanel container = new JPanel();
		parent.add(container);
		GridLayout gridLayout = new GridLayout(5, 2);
		container.setLayout(gridLayout);
		container.add(new JLabel("SNMP 服务器地址1"));
		container.add(new JTextField(25));
		container.add(new JLabel("SNMP 服务器地址2"));
		container.add(new JTextField(25));
		container.add(new JLabel("SNMP 服务器地址3"));
		container.add(new JTextField(25));
		container.add(new JLabel("SNMP 服务器地址4"));
		container.add(new JTextField(25));
		container.add(new JLabel("SNMP 服务器地址5"));
		container.add(new JTextField(25));
	}
	
	private void createActionPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton saveBtn = buttonFactory.createButton(SAVE);
		JButton closeBtn = buttonFactory.createCloseButton();
		parent.add(saveBtn);
		parent.add(closeBtn);
		setCloseButton(closeBtn);
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存SNMP主机",role=Constants.MANAGERCODE)
	public void save() {
		//
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;

	private ButtonFactory buttonFactory;
	private static final long serialVersionUID = -7336113588566125810L;
	public static final String ID = "configureSnmpHostView";
}