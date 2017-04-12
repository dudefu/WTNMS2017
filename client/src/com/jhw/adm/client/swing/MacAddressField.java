package com.jhw.adm.client.swing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;

import com.jhw.adm.client.views.SpringUtilities;

/**
 * <p>Title: </p>
 * <p>Description:IpAddressField   mac地址格式的输入框
 * </p>
 * <p>Copyright: Copyright (c) </p>
 * <p>Company: </p>
 * @author 
 * @version 1.0
 */

public class MacAddressField extends JTextField implements Serializable {
	private static final long serialVersionUID = 1L;
	private JTextField mac1 = new JTextField();
	private JTextField mac2 = new JTextField();
	private JTextField mac3 = new JTextField();
	private JTextField mac4 = new JTextField();
	private JTextField mac5 = new JTextField();
	private JTextField mac6 = new JTextField();
    
    private final MacAddressDocument mac1Document;
    private final MacAddressDocument mac2Document;
    private final MacAddressDocument mac3Document;
    private final MacAddressDocument mac4Document;
    private final MacAddressDocument mac5Document;
    private final MacAddressDocument mac6Document;
    
    public final static int EMRULE = 0; //破折号 "-"
    public final static int COLON = 1; //冒号":"
    
    //默认为破折号
    private static char style='-';
        
    private static final String ILLEGAL_MAC_VALUE = "00-00-00-00-00-00";

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
                if (txt.getCaretPosition() == txt.getText().length()) {
                    txt.firePropertyChange(RIGHT_TRAN, 0, 1);
                }
            }else if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE){//回格键(Backspace),输入值长度为0时焦点自动向左跳转
            	if(txt.getText().length() == 0){
            		txt.firePropertyChange(LEFT_TRAN, 0, 1);
            	}
            }
            else if(e.getKeyChar() == style){//dot处理,输入值长度大于0小于2时，敲击"-"键转焦点向右跳
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
                	if (leftComponent instanceof JTextField){
                		int len = ((JTextField)leftComponent).getText().length();
                    	((JTextField)leftComponent).select(len,len);
                	}
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
    
    //输入值达到两位位后焦点自动向右跳转
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
				if(currentComponent instanceof JTextField){
					JTextField currentField = (JTextField)currentComponent;
					if(currentField.getText().length() >= 2){
						if(null != rightComponent){
							rightComponent.requestFocus();
						}
					}
				}
			}
		}
    }

    /**
     * 构造器
     * 默认样式为破折号"-"
     */
    public MacAddressField() {
    	this(StringUtils.EMPTY,style);
    }
    
    public MacAddressField(int style) {
    	this(StringUtils.EMPTY,style);
    }

    public MacAddressField(String strMacAddress,int value) {
    	this.setStyle(value);
//        setPreferredSize(new Dimension(250, 25));
    	this.setLayout(new SpringLayout());
//        this.setLayout(new GridLayout(1, 7, 0, 0));
    	mac1.setHorizontalAlignment(CENTER);
    	mac2.setHorizontalAlignment(CENTER);
    	mac3.setHorizontalAlignment(CENTER);
    	mac4.setHorizontalAlignment(CENTER);
    	mac5.setHorizontalAlignment(CENTER);
    	mac6.setHorizontalAlignment(CENTER);
    	mac1.setBorder(null);
    	mac2.setBorder(null);
    	mac3.setBorder(null);
    	mac4.setBorder(null);
    	mac5.setBorder(null);
    	mac6.setBorder(null);
        this.add(createDot(""));
        this.add(mac1);
        this.add(createDot(String.valueOf(style)));
        this.add(mac2);
        this.add(createDot(String.valueOf(style)));
        this.add(mac3);
        this.add(createDot(String.valueOf(style)));
        this.add(mac4);
        this.add(createDot(String.valueOf(style)));
        this.add(mac5);
        this.add(createDot(String.valueOf(style)));
        this.add(mac6);
        this.add(createDot(""));
        SpringUtilities.makeCompactGrid(this, 1, 13, 0, 0, 0, 0);
        this.setFocusable(false);
        setMacAddress(strMacAddress);
        LeftRightKeyListener lrKeyl = new LeftRightKeyListener();
        mac1.addKeyListener(lrKeyl);
        mac2.addKeyListener(lrKeyl);
        mac3.addKeyListener(lrKeyl);
        mac4.addKeyListener(lrKeyl);
        mac5.addKeyListener(lrKeyl);
        mac6.addKeyListener(lrKeyl);
        mac1.addPropertyChangeListener(new LRTransListener(null, mac2));
        mac2.addPropertyChangeListener(new LRTransListener(mac1, mac3));
        mac3.addPropertyChangeListener(new LRTransListener(mac2, mac4));
        mac4.addPropertyChangeListener(new LRTransListener(mac3, mac5));
        mac5.addPropertyChangeListener(new LRTransListener(mac4, mac6));
        mac6.addPropertyChangeListener(new LRTransListener(mac5, null));
          
        mac1Document = new MacAddressDocument();
        mac2Document = new MacAddressDocument();
        mac3Document = new MacAddressDocument();
        mac4Document = new MacAddressDocument();
        mac5Document = new MacAddressDocument();
        mac6Document = new MacAddressDocument();
        
        mac1.setDocument(mac1Document);
        mac1Document.addDocumentListener(new RightDocumentListener(mac1, mac2));
          
        mac2.setDocument(mac2Document);
        mac2Document.addDocumentListener(new RightDocumentListener(mac2, mac3));
        
        mac3.setDocument(mac3Document);
        mac3Document.addDocumentListener(new RightDocumentListener(mac3, mac4));
        
        mac4.setDocument(mac4Document);
        mac4Document.addDocumentListener(new RightDocumentListener(mac4, mac5));
        
        mac5.setDocument(mac5Document);
        mac5Document.addDocumentListener(new RightDocumentListener(mac5, mac6));
        
        mac6.setDocument(mac6Document);
    }

    private JLabel createDot(String dot) {
        JLabel lb = new JLabel(dot);
        lb.setOpaque(false);
        
        return lb;
    }

    /**
     * 设置Mac地址
     * @param setMacAddress String
     */
    public void setMacAddress(String strMacAddress) {
        if (strMacAddress != null && strMacAddress.equals("")) {
        	mac1.setText("");
        	mac2.setText("");
        	mac3.setText("");
        	mac4.setText("");
        	mac5.setText("");
        	mac6.setText("");
        }
        else {
            if (strMacAddress == null || (strMacAddress.indexOf("") == -1)
                || (strMacAddress.split("\\" + String.valueOf(style)).length != 6)) {
                return;
            }
            String strMac[] = strMacAddress.split("\\" + String.valueOf(style));
            mac1.setText(strMac[0]);
            mac2.setText(strMac[1]);
            mac3.setText(strMac[2]);
            mac4.setText(strMac[3]);
            mac5.setText(strMac[4]);
            mac6.setText(strMac[5]);
        }
    }
    
    @Override
	public void setText(String t) {
    	super.setText("");
    	setMacAddress(t);
    }

    /**
     * 取得Mac地址
     * @return String
     */
    public String getMacAddress() {
        String strMac1 = mac1.getText().trim();
        String strMac2 = mac2.getText().trim();
        String strMac3 = mac3.getText().trim();
        String strMac4 = mac4.getText().trim();
        String strMac5 = mac5.getText().trim();
        String strMac6 = mac6.getText().trim();
        
        String macAddr = (strMac1.equals("") ? "0" : strMac1) + String.valueOf(style) +
        	(strMac2.equals("") ? "0" : strMac2) + String.valueOf(style) +
        	(strMac3.equals("") ? "0" : strMac3) + String.valueOf(style) +
        	(strMac4.equals("") ? "0" : strMac4) + String.valueOf(style) +
        	(strMac5.equals("") ? "0" : strMac5) + String.valueOf(style) +
        	(strMac6.equals("") ? "0" : strMac6);
        return macAddr;
//        String regex ="^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
//        Pattern p=Pattern.compile(regex);
//		Matcher m=p.matcher(macAddr);
//		if (m.matches()){
//			return macAddr;
//		}
//		else{
//			return macAddr = "";
//		}
    }

    @Override
	public String getText() {
        return getMacAddress();
    }
    
    @Override
	public void setEnabled(boolean b) {
    	super.setEnabled(b);
    	mac1.setEnabled(b);
    	mac2.setEnabled(b);
    	mac3.setEnabled(b);
    	mac4.setEnabled(b);
    	mac5.setEnabled(b);
    	mac6.setEnabled(b);
    }
    
    @Override
	public void setEditable(boolean b) {
    	super.setEditable(b);
    	if (mac1 == null) return;
    	mac1.setEditable(b);
    	mac2.setEditable(b);
    	mac3.setEditable(b);
    	mac4.setEditable(b);
    	mac5.setEditable(b);
    	mac6.setEditable(b);
    	
    	if (b) {
    		mac1.setDocument(mac1Document);
    		mac2.setDocument(mac2Document);            
    		mac3.setDocument(mac3Document);
    		mac4.setDocument(mac4Document);
    		mac5.setDocument(mac5Document);
    		mac6.setDocument(mac6Document);
    	} else {
    		mac1.setDocument(new JTextField(mac1.getText()).getDocument());
    		mac2.setDocument(new JTextField(mac2.getText()).getDocument());
    		mac3.setDocument(new JTextField(mac3.getText()).getDocument());
    		mac4.setDocument(new JTextField(mac4.getText()).getDocument());
    		mac5.setDocument(new JTextField(mac5.getText()).getDocument());
    		mac6.setDocument(new JTextField(mac6.getText()).getDocument());
    	}
    }
    
    /**
     * 有两种样式，一种是破折号"-"，另一种是冒号":"
     * @param style
     */
    private void setStyle(int style){
    	switch(style){
    		case EMRULE:
    			this.style = '-';
    			break;
    		case COLON:
    			this.style = ':';
    			break;
    		default:
    			this.style = '-';
    			break;
    	}
    }
    
    public static void main(String[] ar){
    	String aa = "00-56-FA-89-85-75";
    	
    	final MacAddressField macFld = new MacAddressField(EMRULE);
    	macFld.setColumns(15);
    	macFld.setText(aa);
    	
    	
    	JButton btn = new JButton("aaa");
    	btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.print(macFld.getMacAddress());
			}
    	});
    	JPanel panel = new JPanel();
    	
    	JTextField field = new JTextField();
    	field.setColumns(20);
    	panel.add(macFld);
    	panel.add(btn);
//    	panel.add(field);
    	
    	

    	panel.updateUI();
    	
    	JFrame frame = new JFrame("Mac地址输入框");
    	frame.setLayout(new FlowLayout());
    	frame.getContentPane().add(panel);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(300, 200);
    	frame.setVisible(true);
    }
}