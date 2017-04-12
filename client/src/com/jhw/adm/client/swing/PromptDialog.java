package com.jhw.adm.client.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.server.entity.util.Constants;

public class PromptDialog {
	
	public PromptDialog(){
	}
	
	private int showConfirmDialog( ){
		OptionDialog dialog = new OptionDialog();
		
		return dialog.getSelectValue();
	}
	
	public static int showPromptDialog(Component viewPart,String message,ImageRegistry imageRegistry){
		OptionDialog dialog = new OptionDialog(viewPart,message,imageRegistry);
		
		return dialog.getSelectValue();
	}
	
	public static void main(String[] ar){
		int a = new PromptDialog().showConfirmDialog();
		System.err.println(a);
	}
}

class OptionDialog extends JDialog{
	private JPanel mainPanel = new JPanel();
	private JLabel iconLbl = new JLabel();
	private JLabel titleLbl = new JLabel();
	
	private JCheckBox deviceSideChkBox = new JCheckBox("设备侧");
	private JCheckBox netWorkSideChkBox = new JCheckBox("网管侧");
	
	private JButton okBtn = new JButton("确定");
	private JButton cancelBtn = new JButton("取消");
	
	private int selectValue;
	
	private Component viewPart = null;
	private String message = "";
	
	public OptionDialog(){
		super(ClientUtils.getRootFrame());
		super.setTitle("提示");
		init();
	}
	
	public OptionDialog(Component viewPart,String message,ImageRegistry imageRegistry){
		super(ClientUtils.getRootFrame());
		super.setTitle("提示");
//		this.setIconImage(imageRegistry.getImage(ApplicationConstants.LOGO));
//		this.setIconImage(null);
		this.viewPart = viewPart;
		this.message = message;
		init();
	}
	
	private void init(){
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel topContainer = new JPanel(new GridBagLayout());
		Icon icon = (Icon)UIManager.get("OptionPane.informationIcon", this.getLocale());
		iconLbl.setIcon(icon);
		
		titleLbl.setHorizontalAlignment(JLabel.CENTER);
		titleLbl.setText(this.message);
		
		topContainer.add(iconLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(20,10,0,0),0,0));
		topContainer.add(titleLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(20,15,0,0),0,0));
		topPnl.add(topContainer);
		

		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		centerPnl.setBorder(BorderFactory.createEtchedBorder());
		JPanel panel = new JPanel(new SpringLayout());
		panel.add(deviceSideChkBox);
		panel.add(netWorkSideChkBox);
		SpringUtilities.makeCompactGrid(panel, 1, 2, 6, 10, 10, 6);
		centerPnl.add(panel);
		
		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(okBtn);
		bottomPnl.add(cancelBtn);
		
		deviceSideChkBox.setSelected(true);
		netWorkSideChkBox.setSelected(true);
		
		okBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectValue = getSelectItem();
				if (0 == selectValue){
					return;
				}
				dispose();
			}
			
		});
		
		cancelBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
			
		});
		
		netWorkSideChkBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				netWorkSideChkBox.setSelected(true);
			}
		});
		
		JPanel middlePnl = new JPanel(new BorderLayout());
		middlePnl.add(centerPnl,BorderLayout.CENTER);
		middlePnl.add(bottomPnl,BorderLayout.SOUTH);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPnl,BorderLayout.CENTER);
		mainPanel.add(middlePnl,BorderLayout.SOUTH);
		
		this.getContentPane().add(mainPanel);
		this.setModal(true);
		this.setSize(new Dimension(320,200));
		this.setLocationRelativeTo(viewPart);
		this.setResizable(false);
		
		this.setVisible(true);
	}
	
	public int getSelectItem(){
		int value = 0 ; 
		if (netWorkSideChkBox.isSelected()){
			value = Constants.SYN_SERVER;
		}
		if (deviceSideChkBox.isSelected()){
			value = Constants.SYN_DEV;
		}
		if (netWorkSideChkBox.isSelected() && deviceSideChkBox.isSelected()){
			value = Constants.SYN_ALL;
		}
		
		return value;
	}
	
	public int getSelectValue(){
		return this.selectValue;
	}

}
