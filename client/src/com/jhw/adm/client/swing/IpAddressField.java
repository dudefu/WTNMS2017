package com.jhw.adm.client.swing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;

import com.jhw.adm.client.views.SpringUtilities;

/**
 * <p>Title: </p>
 * <p>Description:IpAddressField   IP地址格式的输入框
 * </p>
 * <p>Copyright: Copyright (c) </p>
 * <p>Company: </p>
 * @author 
 * @version 1.0
 */

public class IpAddressField extends JTextField implements Serializable {
	private static final long serialVersionUID = 1L;
	NumberField ip1 = new NumberField(3, 0, 0, 255, true);
	NumberField ip2 = new NumberField(3, 0, 0, 255, true);
	NumberField ip3 = new NumberField(3, 0, 0, 255, true);
    NumberField ip4 = new NumberField(3, 0, 1, 254, true);
    
    private final NumberDocument ip1Document;
    private final NumberDocument ip2Document;
    private final NumberDocument ip3Document;
    private final NumberDocument ip4Document;
        
    private static final String ILLEGAL_IP_VALUE = "0.0.0.0";

    private class LeftRightKeyListener extends KeyAdapter {
    	
        public static final String LEFT_TRAN = "LEFT_TRAN";
        public static final String RIGHT_TRAN = "RIGHT_TRAN";
        @Override
		public void keyPressed(KeyEvent e) {
            JTextComponent txt = (JTextComponent) e.getComponent();
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {//方向键—向左
                if (txt.getCaretPosition() == 0) {
                    txt.firePropertyChange(LEFT_TRAN, 0, 1);
                }
            }else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {//方向键—向右
//                     || e.getKeyChar() == '.'
                if (txt.getCaretPosition() == txt.getText().length()) {
                    txt.firePropertyChange(RIGHT_TRAN, 0, 1);
                }
            }else if(e.getKeyChar() == '.'){//dot处理,输入值长度大于0小于3时，敲击"."键转焦点向右跳
            	if(txt.getText().length() > 0){
            		if(!StringUtils.isBlank(txt.getSelectedText())){
            			//
            		}else{
            			if(txt.getSelectionStart() == 0){
            				//
            			}else{
            				txt.firePropertyChange(RIGHT_TRAN, 0, 1);
            			}
            		}
            	}
            }else if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE){//回格键(Backspace),输入值长度为0时焦点自动向左跳转
            	if(txt.getText().length() == 0){
            		txt.firePropertyChange(LEFT_TRAN, 0, 1);
            	}
            }
        }
    };
    private class LRTransListener implements PropertyChangeListener {
        private final Component leftComponent, rightComponent;
        public LRTransListener(Component leftComponent,
                               Component rightComponent) {
            this.leftComponent = leftComponent;
            this.rightComponent = rightComponent;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == LeftRightKeyListener.LEFT_TRAN) {
                if (leftComponent != null) {
                    leftComponent.requestFocus();
                }
            }
            else if (evt.getPropertyName() == LeftRightKeyListener.RIGHT_TRAN) {
                if (rightComponent != null) {
                    rightComponent.requestFocus();
                }
            }
        }
    }
    
    //输入值达到三位后焦点自动向右跳转
    private class RightDocumentListener implements DocumentListener{
    	private final Component currentComponent;
    	private final Component rightComponent;
    	public RightDocumentListener(Component currentComponent,Component rightComponent){
    		this.currentComponent = currentComponent;
    		this.rightComponent = rightComponent;
    	}

		@Override
		public void changedUpdate(DocumentEvent arg0) {}
		@Override
		public void removeUpdate(DocumentEvent arg0) {}
		@Override
		public void insertUpdate(DocumentEvent arg0) {
//			System.out.println("insertUpdate");
			if(null != currentComponent){
				if(currentComponent instanceof NumberField){
					NumberField currentField = (NumberField)currentComponent;
					if(currentField.getText().length() >= 3){
						if(null != rightComponent){
							rightComponent.requestFocus();
						}
					}
				}
			}
		}
    }

    public IpAddressField() {
    	this(StringUtils.EMPTY);
    }

    public IpAddressField(String strIpAddress) {
//        setPreferredSize(new Dimension(250, 25));
    	this.setLayout(new SpringLayout());
//        this.setLayout(new GridLayout(1, 7, 0, 0));
    	ip1.setHorizontalAlignment(CENTER);
    	ip2.setHorizontalAlignment(CENTER);
    	ip3.setHorizontalAlignment(CENTER);
    	ip4.setHorizontalAlignment(CENTER);
        ip1.setBorder(null);
        ip2.setBorder(null);
        ip3.setBorder(null);
        ip4.setBorder(null);
        this.add(createDot(""));
        this.add(ip1);
        this.add(createDot("."));
        this.add(ip2);
        this.add(createDot("."));
        this.add(ip3);
        this.add(createDot("."));
        this.add(ip4);
        this.add(createDot(""));
        SpringUtilities.makeCompactGrid(this, 1, 9, 0, 0, 0, 0);
        this.setFocusable(false);
        setIpAddress(strIpAddress);
        LeftRightKeyListener lrKeyl = new LeftRightKeyListener();
        ip1.addKeyListener(lrKeyl);
        ip2.addKeyListener(lrKeyl);
        ip3.addKeyListener(lrKeyl);
        ip4.addKeyListener(lrKeyl);
        ip1.addPropertyChangeListener(new LRTransListener(null, ip2));
        ip2.addPropertyChangeListener(new LRTransListener(ip1, ip3));
        ip3.addPropertyChangeListener(new LRTransListener(ip2, ip4));
        ip4.addPropertyChangeListener(new LRTransListener(ip3, null));
          
        ip1Document = new NumberDocument(3, 0, 0, 255);
        ip2Document = new NumberDocument(3, 0, 0, 255);
        ip3Document = new NumberDocument(3, 0, 0, 255);
        ip4Document = new NumberDocument(3, 0, 0, 255);
        
        ip1.setDocument(ip1Document);
        ip1Document.addDocumentListener(new RightDocumentListener(ip1, ip2));
          
        ip2.setDocument(ip2Document);
        ip2Document.addDocumentListener(new RightDocumentListener(ip2, ip3));
        
        ip3.setDocument(ip3Document);
        ip3Document.addDocumentListener(new RightDocumentListener(ip3, ip4));
        
        ip4.setDocument(ip4Document);
    }

    private JLabel createDot(String dot) {
        JLabel lb = new JLabel(dot);
        lb.setOpaque(false);
        
        return lb;
    }

    /**
     * 设置IP地址
     * @param strIpAddress String
     */
    public void setIpAddress(String strIpAddress) {
        if (strIpAddress != null && strIpAddress.equals("")) {
            ip1.setText("");
            ip2.setText("");
            ip3.setText("");
            ip4.setText("");
        }
        else {
            if (strIpAddress == null || (strIpAddress.indexOf(".") == -1)
                || (strIpAddress.split("\\.").length != 4)) {
                return;
            }
            String strIp[] = strIpAddress.split("\\.");
            ip1.setText(strIp[0]);
            ip2.setText(strIp[1]);
            ip3.setText(strIp[2]);
            ip4.setText(strIp[3]);
        }
    }
    
    @Override
	public void setText(String t) {
    	super.setText("");
    	setIpAddress(t);
    }

    /**
     * 取得IP地址
     * @return String
     */
    public String getIpAddress() {
        String strIp1 = ip1.getText().trim();
        String strIp2 = ip2.getText().trim();
        String strIp3 = ip3.getText().trim();
        String strIp4 = ip4.getText().trim();
        
        String ipAddr = (strIp1.equals("") ? "0" : strIp1) + "." +
        	(strIp2.equals("") ? "0" : strIp2) + "." +
        	(strIp3.equals("") ? "0" : strIp3) + "." +
        	(strIp4.equals("") ? "0" : strIp4);
        
//        if(ILLEGAL_IP_VALUE.equals(ipAddr)){
//        	return ipAddr;
//        }
        
        String regex ="^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";
        Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(ipAddr);
		if (m.matches()){
			return ipAddr;
		}
		else{
			return ipAddr = "";
		}
    }

    @Override
	public String getText() {
        return getIpAddress();
    }
    
    @Override
	public void setEnabled(boolean b) {
    	super.setEnabled(b);
    	ip1.setEnabled(b);
    	ip2.setEnabled(b);
    	ip3.setEnabled(b);
    	ip4.setEnabled(b);
    }
    
    @Override
	public void setEditable(boolean b) {
    	super.setEditable(b);
    	if (ip1 == null) return;
    	ip1.setEditable(b);
    	ip2.setEditable(b);
    	ip3.setEditable(b);
    	ip4.setEditable(b);
    	
    	if (b) {
    		ip1.setDocument(ip1Document);
            ip2.setDocument(ip2Document);            
            ip3.setDocument(ip3Document);
            ip4.setDocument(ip4Document);
    	} else {
    		ip1.setDocument(new JTextField(ip1.getText()).getDocument());
        	ip2.setDocument(new JTextField(ip2.getText()).getDocument());
        	ip3.setDocument(new JTextField(ip3.getText()).getDocument());
        	ip4.setDocument(new JTextField(ip4.getText()).getDocument());
    	}
    }
    
    public static void main(String[] ar){
    	IpAddressField ipFld = new IpAddressField();
    	ipFld.setColumns(20);
    	JFrame frame = new JFrame("IP地址输入框");
    	frame.setLayout(new FlowLayout());
    	frame.getContentPane().add(ipFld);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(300, 200);
    	frame.setVisible(true);
    }
}