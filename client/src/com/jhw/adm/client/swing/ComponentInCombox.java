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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.plaf.metal.MetalComboBoxButton;


public class ComponentInCombox extends JPanel implements Serializable{
	private final JTextField textField = new JTextField();
	private final JButton button = new JButton();
	private final CheckPopup popup = new CheckPopup();
	private final JComboBox combox = new JComboBox();
	
//	private final List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
	
	private int width = 100;
	private final int heigh = 20;
	
	private boolean isShow = false;//默认不显示popup
	
	private final StringBuffer buffer = new StringBuffer();
	
	private ComponentComboxModel dataModel = new ComponentComboxModel();
	
	public ComponentInCombox(){
//		button.setIcon(UIManager.getIcon("JComboBox.icon"));
		
		//得到combox中按钮的图片
		for (int i = 0 ; i < combox.getComponentCount(); i++){
			if (combox.getComponent(i) instanceof JButton){
				MetalComboBoxButton buttons = (MetalComboBoxButton)combox.getComponent(i);
				button.setIcon(buttons.getComboIcon());
			}
		}
		
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
				showPopup();
			}
	    });
		textField.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				showPopup();
			}
		});
	}
	
	public void setWidth(int width){
		this.width = width;
		textField.setPreferredSize(new Dimension(this.width,combox.getPreferredSize().height));
		button.setPreferredSize(new Dimension(heigh,combox.getPreferredSize().height));
	}
	
	public void setModel(ComponentComboxModel model){
		this.dataModel = model;
	}
	
	public ComponentComboxModel getModel(){
		return this.dataModel;
	}
	
	public void addItem(Object object,Class classes){
		Object objectClass = null;
		try {
			objectClass = classes.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		if (objectClass instanceof JComponent){
			JComponent component = null;
			if (objectClass instanceof JCheckBox){
				component = new JCheckBox(object.toString());
				((JCheckBox)component).addItemListener(new CheckBoxItemListener());
			}
			else if(objectClass instanceof JTextField){
				component = new JTextField();
			}
			else if(objectClass instanceof JButton){
				component = new JButton(object.toString());
				((JButton)component).addActionListener(new BtnActionListener());
			}
			dataModel.addItem(component);
		}
	}
	
	public void removeItemAll(){
		textField.setText("");
		for (int i = 0 ; i < dataModel.getSize(); i++){
			((JCheckBox)dataModel.getItem(i)).setSelected(false);
		}
		dataModel.removeAllItem();
	}
	
	public void setSelected(int index,boolean isSelect){
		for (int i = 0 ; i < dataModel.getSize(); i++){
			if (index == i){
				((JCheckBox)dataModel.getItem(i)).setSelected(isSelect);
			}
		}
	}
	
	public void clearSelected(){
		for (int i = 0 ; i < dataModel.getSize(); i++){
			((JCheckBox)dataModel.getItem(i)).setSelected(false);
		}
	}
	
	public boolean getSelected(int index){
		boolean isSelect = false;
		for (int i = 0 ; i < dataModel.getSize(); i++){
			if (index == i){
				isSelect = ((JCheckBox)dataModel.getItem(i)).isSelected();
			}
		}
		return isSelect;
	}
	
	public String getText(){
		return textField.getText().trim();
	}
	
	private void showPopup() {
		setPopup();
		if(!isShow){
			isShow = true;
			popup.show(isShow);
			popup.show(ComponentInCombox.this, textField.getX(), textField.getY()+27);
			
		}
		else{
			isShow = false;
			popup.show(isShow);
		}
    }
	private void setPopup(){
	    popup.removeAll();

		int size = dataModel.getSize();
		if (size <1){
			return;
		}
		JPanel panel = new JPanel(new GridLayout(size,1));
		for (int i = 0 ; i < size; i++){
			if (dataModel.getItem(i) instanceof JComponent){
				JComponent component = (JComponent)dataModel.getItem(i);
				panel.add(component);
				if (component instanceof JCheckBox){
					((JCheckBox)component).addItemListener(new CheckBoxItemListener());
				}
				else if(component instanceof JTextField){
					component = new JTextField();
				}
				else if(component instanceof JButton){
					((JButton)component).addActionListener(new BtnActionListener());
				}
			}
		}
	    JPanel container = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    container.add(panel);
		
	    container.setPreferredSize(new Dimension(width+button.getPreferredSize().width-2,container.getPreferredSize().height));
	    popup.add(container);
	}
	
	public String getString(){
		return buffer.toString();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		button.setEnabled(enabled);
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
	class BtnActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	class CheckBoxItemListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			buffer.delete(0, buffer.toString().length());
			for(int i = 0 ; i < dataModel.getSize(); i++){
				if (dataModel.getItem(i) instanceof JCheckBox){
					if (((JCheckBox)dataModel.getItem(i)).isSelected()){
						buffer.append(((JCheckBox)dataModel.getItem(i)).getText()+",");
					}
				}
			}
			
			int len = buffer.toString().length();
			String str = "";
			if(len > 0){
				str = buffer.toString().substring(0, buffer.toString().length()-1);
			}
			textField.setText(str);
		}
	}
}
