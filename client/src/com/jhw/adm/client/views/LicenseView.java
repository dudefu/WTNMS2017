package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.server.entity.util.License;

@Component(LicenseView.ID)
@Scope(Scopes.DESKTOP)
public class LicenseView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "licenseView";
	
	private static final String[] VERSION = {"评估版本","注册版本","开发版本"};
	
	private final JPanel infoPanel = new JPanel();
	
	private final JTextField contractFld = new JTextField();
	private final JTextField versionFld = new JTextField();
	private final JTextField clientCountFld = new JTextField();
	private final JTextField switcherCountFld = new JTextField();
	private final JTextField layer3SwitcherCountFld = new JTextField();
	private final JTextField expirationDateFld = new JTextField();
	private final JTextArea descriptionFld = new JTextArea(3,1);
	
	private ButtonFactory buttonFactory;
	private final JPanel buttonPanel = new JPanel();
	private JButton closeBtn = new JButton();
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@PostConstruct
	protected void initialize(){
		
		buttonFactory = actionManager.getButtonFactory(this);
		
		initializeComponent();
		initializeButtonPanel();
		setComponentValue();
		
		this.setTitle("授权信息");
		this.setViewSize(400, 350);
		this.setLayout(new BorderLayout());
		this.add(infoPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	private void initializeComponent(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("合同号"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(contractFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("授权版本类型"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(versionFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));		
		
		panel.add(new JLabel("客户端数量"),new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(clientCountFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("二层交换机数量"),new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(switcherCountFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("三层交换机数量"),new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(layer3SwitcherCountFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("有效日期"),new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(expirationDateFld,new GridBagConstraints(1,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("授权描述"),new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(descriptionFld,new GridBagConstraints(1,6,5,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		infoPanel.add(panel);
		setPreferredSize();
		setEditabled(false);
		descriptionFld.setLineWrap(true);
		descriptionFld.setBorder(new JTextField().getBorder());
	}
	
	private void setPreferredSize(){
		contractFld.setPreferredSize(new Dimension(270,contractFld.getPreferredSize().height));
		versionFld.setPreferredSize(new Dimension(270,versionFld.getPreferredSize().height));
		clientCountFld.setPreferredSize(new Dimension(270,clientCountFld.getPreferredSize().height));
		switcherCountFld.setPreferredSize(new Dimension(270,switcherCountFld.getPreferredSize().height));
		layer3SwitcherCountFld.setPreferredSize(new Dimension(270,layer3SwitcherCountFld.getPreferredSize().height));
		expirationDateFld.setPreferredSize(new Dimension(270,expirationDateFld.getPreferredSize().height));
		descriptionFld.setPreferredSize(new Dimension(270,descriptionFld.getPreferredSize().height));
	}
	
	private void setEditabled(boolean editabled){
		contractFld.setEditable(editabled);
		versionFld.setEditable(editabled);
		clientCountFld.setEditable(editabled);
		switcherCountFld.setEditable(editabled);
		layer3SwitcherCountFld.setEditable(editabled);
		expirationDateFld.setEditable(editabled);
		descriptionFld.setEditable(editabled);
	}
	
	private void initializeButtonPanel(){
		buttonPanel.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(closeBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		buttonPanel.add(newPanel,BorderLayout.EAST);
	}
	
	private void setComponentValue(){
		License license = clientModel.getLicense();
		
		contractFld.setText(license.getContractNumber());
		descriptionFld.setText(license.getDescription());
		versionFld.setText(VERSION[license.getVersion()]);
		
		if(!license.getClientCount().isRestriction()){
			clientCountFld.setText("未限制");
		}else{
			clientCountFld.setText(license.getClientCount().getValue().toString());
		}
		
		if(!license.getSwitcherCount().isRestriction()){
			switcherCountFld.setText("未限制");
		}else{
			switcherCountFld.setText(license.getSwitcherCount().getValue().toString());
		}
		
		if(!license.getLayer3SwitcherCount().isRestriction()){
			layer3SwitcherCountFld.setText("未限制");
		}else{
			layer3SwitcherCountFld.setText(license.getLayer3SwitcherCount().getValue().toString());
		}
		
		if(!license.getExpirationDate().isRestriction()){
			expirationDateFld.setText("未限制");
		}else{
			expirationDateFld.setText(dateFormatter.format(license.getExpirationDate().getValue()));
		}
		
	}
	
}
