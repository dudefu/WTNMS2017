package com.jhw.adm.client.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;


public class CheckInCombox extends JPanel implements Serializable{
	private final JTextField textField = new JTextField();
	private final JButton button = new JButton();
	private final CheckPopup popup = new CheckPopup();
	private final JComboBox combox = new JComboBox();
	
	private final List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
	
	private int width = 100;
	private final int heigh = 20;
	
	private boolean isShow = false;//ƒ¨»œ≤ªœ‘ æpopup
	
	private final StringBuffer buffer = new StringBuffer();
	
	public CheckInCombox(){
		button.setIcon(UIManager.getIcon("ComboBox.icon"));
		
		textField.setEditable(false);
		textField.setBackground(Color.WHITE);
		button.setFocusable(false);
		
		this.setLayout(new GridBagLayout());
		this.add(textField,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(button,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				showPopup(e);
			}
	    });
	}
	
	public void setWidth(int width){
		this.width = width;
		textField.setPreferredSize(new Dimension(this.width,combox.getPreferredSize().height));
		button.setPreferredSize(new Dimension(heigh,combox.getPreferredSize().height));
	}
	
	public void addItem(final JCheckBox checkBox){
		checkBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				buffer.delete(0, buffer.toString().length());
				for(int i = 0 ; i < checkBoxList.size(); i++){
					if (checkBoxList.get(i).isSelected()){
						buffer.append(checkBoxList.get(i).getText()+",");
					}
				}
				
				int len = buffer.toString().length();
				String str = "";
				if(len > 0){
					str = buffer.toString().substring(0, buffer.toString().length()-1);
				}
				textField.setText(str);
			}
			
		});
		checkBoxList.add(checkBox);
	}
	
	public void addItem(Object object){
		JCheckBox checkBox = new JCheckBox(object.toString());
		checkBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				buffer.delete(0, buffer.toString().length());
				for(int i = 0 ; i < checkBoxList.size(); i++){
					if (checkBoxList.get(i).isSelected()){
						buffer.append(checkBoxList.get(i).getText()+",");
					}
				}
				
				int len = buffer.toString().length();
				String str = "";
				if(len > 0){
					str = buffer.toString().substring(0, buffer.toString().length()-1);
				}
				textField.setText(str);
			}
			
		});
		checkBoxList.add(checkBox);
	}
	
	public void removeItemAll(){
		textField.setText("");
		for (int i = 0 ; i < checkBoxList.size(); i++){
			checkBoxList.get(i).setSelected(false);
		}
		checkBoxList.clear();
	}
	
	public void setSelected(int index,boolean isSelect){
		for (int i = 0 ; i < checkBoxList.size(); i++){
			if (index == i){
				checkBoxList.get(i).setSelected(isSelect);
			}
		}
	}
	
	public void clearSelected(){
		for (int i = 0 ; i < checkBoxList.size(); i++){
			checkBoxList.get(i).setSelected(false);
		}
	}
	
	public boolean getSelected(int index){
		boolean isSelect = false;
		for (int i = 0 ; i < checkBoxList.size(); i++){
			if (index == i){
				isSelect = checkBoxList.get(i).isSelected();
			}
		}
		
		return isSelect;
	}
	
	public String getText(){
		return textField.getText().trim();
	}
	
	private void showPopup(ActionEvent e) {
		setPopup();
		if(!isShow){
			isShow = true;
			popup.show(isShow);
			popup.show(CheckInCombox.this, textField.getX(), textField.getY()+24);
			
		}
		else{
			isShow = false;
			popup.show(isShow);
		}

    }
	private void setPopup(){
	    popup.removeAll();
//		JCheckBox aa = new JCheckBox("aa");
//	    JCheckBox bb = new JCheckBox("bb");
//	    JPanel panel = new JPanel(new GridBagLayout());
//		panel.add(aa,new GridBagConstraints(0,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,50),0,0));
//		panel.add(bb,new GridBagConstraints(0,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,50),0,0));
		int size = checkBoxList.size();
		if (size <1){
			return;
		}
		JPanel panel = new JPanel(new GridLayout(size,1));
		for (int i = 0 ; i < size; i++){
			panel.add(checkBoxList.get(i));
		}
	    JPanel container = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    container.add(panel);
		
	    container.setPreferredSize(new Dimension(width+button.getPreferredSize().width-2,container.getPreferredSize().height));
	    popup.add(container);
	}
	
	
	class CheckPopup extends  JPopupMenu{
		@Override
		protected void firePropertyChange(String propertyName,Object oldValue, Object newValue){
			if(propertyName.equals("visible")){
				if(oldValue.equals(Boolean.FALSE) && newValue.equals(Boolean.TRUE)){ //SHOW
				try{
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				} 
				else if(oldValue.equals(Boolean.TRUE) && newValue.equals(Boolean.FALSE)){ //HIDE
					isShow = false;
				}
			}
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
		@Override
		public void firePopupMenuCanceled()
		{
			super.firePopupMenuCanceled();
		}
	}
	
	public String getString(){
		return buffer.toString();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}

    public static void main(String[] ar){
    	JFrame frame = new JFrame();
    	JPanel p = new JPanel();
    	CheckInCombox panel = new CheckInCombox();
    	panel.setWidth(100);
    	panel.addItem(new JCheckBox("aa"));
    	panel.addItem(new JCheckBox("bb"));
    	
    	p.add(panel);
    	frame.getContentPane().add(p);
    	frame.setSize(300,200);
    	frame.setVisible(true);
    }
}