package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

@Component(SerialPortDetailView.ID)
@Scope(Scopes.DESKTOP)
public class SerialPortDetailView extends ViewPart{

	private static final long serialVersionUID = 1L;
	public static final String ID = "serialPortDetailView";
	
	//串口设置面板
	private JPanel serialConfigPnl = new JPanel();
	private JLabel serialLbl = new JLabel();
	private JTextField serialFld = new JTextField();
	
	
	private JLabel serialNameLbl = new JLabel();
	private JTextField serialNameFld = new JTextField();
	
	private JLabel baudLbl = new JLabel();
	private JTextField baudFld = new JTextField();
	
	private JLabel fluidicsLbl = new JLabel();
	private JCheckBox checkBox = new JCheckBox();
	
	private JLabel dataBitLbl = new JLabel();
	private JTextField dataBitFld = new JTextField();
	
	private JLabel parityLbl = new JLabel();
	private JTextField parityFld = new JTextField();
	
	private JLabel stopBitLbl = new JLabel();
	private JTextField stopBitFld = new JTextField();
	
	//串口模式面板
	private JLabel modeLbl = new JLabel();
	private JTextField modeFld = new JTextField();
	
	//TCP Client设置面板
	private JPanel tcpClientPnl = new JPanel();
	private JLabel tcpClientRemotePortLbl = new JLabel();
	private NumberField tcpClientRemotePortFld = new NumberField(5,0,0,65535,true);
	
	private JLabel tcpClientRemoteIpLbl = new JLabel();
	private IpAddressField tcpClientRemoteIpFld = new IpAddressField();
	
	//TCP Server设置面板
	private JPanel   tcpServerPnl = new JPanel();
	private JLabel tcpServerLocalPortLbl = new JLabel();
	private NumberField tcpServerLocalPortFld = new NumberField(5,0,0,65535,true);
	
	//UDP Client设置面板
	private JPanel udpClientPnl = new JPanel();
	private JLabel udpClientRemotePortLbl = new JLabel();
	private NumberField udpClientRemotePortFld = new NumberField(5,0,0,65535,true);
	
	private JLabel udpClientRemoteIpLbl = new JLabel();
	private IpAddressField udpClientRemoteIpFld = new IpAddressField();
	
	//UDP Server设置面板
	private JPanel udpServerPnl = new JPanel();
	private JLabel udpServerLocalPortLbl = new JLabel();
	private NumberField udpServerLocalPortFld = new NumberField(5,0,0,65535,true);
	
	private SwitchSerialPort switchSerialPort;
	private CardLayout cardLayout;
	private JPanel dynamicalPanel;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@PostConstruct
	protected void initialize(){
		init();
		switchSerialPort = (SwitchSerialPort) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchSerialPort.class);
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedLinstener);
	}

	private void init(){
		initSerialConfigPnl();
		
		cardLayout = new CardLayout();
		dynamicalPanel = new JPanel(cardLayout);
		
		dynamicalPanel.add(getTcpClientPnl(), "1");
		dynamicalPanel.add(getTcpServerPnl(), "0");
		dynamicalPanel.add(getUdpClientPnl(), "3");
		dynamicalPanel.add(getUdpServerPnl(), "2");
		
		this.setLayout(new BorderLayout());
		this.add(serialConfigPnl,BorderLayout.PAGE_START);
		this.add(dynamicalPanel,BorderLayout.CENTER);
		this.setViewSize(640, 570);
		setResource();
	}
	
	private void initSerialConfigPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(serialLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		panel.add(serialFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		panel.add(serialNameLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,150,5,5),0,0));
		panel.add(serialNameFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		panel.add(baudLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(baudFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(fluidicsLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,150,5,5),0,0));
		panel.add(checkBox,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		
		panel.add(dataBitLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(dataBitFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(parityLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,150,5,5),0,0));
		panel.add(parityFld,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,50),0,0));
		
		panel.add(stopBitLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(stopBitFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(modeLbl,new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,150,5,5),0,0));
		panel.add(modeFld,new GridBagConstraints(3,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		serialConfigPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		serialConfigPnl.add(panel);
	}
	
	private JPanel getTcpClientPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		tcpClientRemotePortLbl.setText("远端端口");
		tcpClientRemoteIpLbl.setText("远端IP");
		
		tcpClientRemotePortFld.setColumns(20);
		tcpClientRemoteIpFld.setColumns(20);
		tcpClientRemotePortFld.setEditable(false);
		tcpClientRemoteIpFld.setEditable(false);
		
		panel.add(tcpClientRemotePortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(tcpClientRemotePortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(tcpClientRemoteIpLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(tcpClientRemoteIpFld,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		tcpClientPnl.setBorder(BorderFactory.createTitledBorder("串口其他信息"));
		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(panel,BorderLayout.WEST);
		tcpClientPnl.setLayout(new BorderLayout());
		tcpClientPnl.add(newPanel,BorderLayout.NORTH);
		
		return tcpClientPnl;
	}
	
	private JPanel getTcpServerPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		tcpServerLocalPortLbl.setText("本地端口");
		tcpServerLocalPortFld.setColumns(20);
		tcpServerLocalPortFld.setEditable(false);
		
		panel.add(tcpServerLocalPortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(tcpServerLocalPortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		tcpServerPnl.setBorder(BorderFactory.createTitledBorder("串口其他信息"));
		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(panel,BorderLayout.WEST);
		tcpServerPnl.setLayout(new BorderLayout());
		tcpServerPnl.add(newPanel,BorderLayout.NORTH);
		
		return tcpServerPnl;
	}
	
	private JPanel getUdpClientPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		udpClientRemotePortLbl.setText("远端端口");
		udpClientRemoteIpLbl.setText("远端IP");
		
		udpClientRemoteIpFld.setColumns(20);
		udpClientRemotePortFld.setColumns(20);
		udpClientRemoteIpFld.setEditable(false);
		udpClientRemotePortFld.setEditable(false);
		
		panel.add(udpClientRemotePortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(udpClientRemotePortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(udpClientRemoteIpLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(udpClientRemoteIpFld,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		udpClientPnl.setBorder(BorderFactory.createTitledBorder("串口其他信息"));
		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(panel,BorderLayout.WEST);
		udpClientPnl.setLayout(new BorderLayout());
		udpClientPnl.add(newPanel,BorderLayout.NORTH);
		
		return udpClientPnl;
	}
	
	private JPanel getUdpServerPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		udpServerLocalPortLbl.setText("本地端口");
		udpServerLocalPortFld.setColumns(20);
		udpServerLocalPortFld.setEditable(false);
		
		panel.add(udpServerLocalPortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(udpServerLocalPortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		udpServerPnl.setBorder(BorderFactory.createTitledBorder("串口其他信息"));
		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(panel,BorderLayout.WEST);
		udpServerPnl.setLayout(new BorderLayout());
		udpServerPnl.add(newPanel,BorderLayout.NORTH);
		
		return udpServerPnl;
	}
	
	private void setResource(){
		this.setTitle("串口详细信息");
		serialConfigPnl.setBorder(BorderFactory.createTitledBorder("串口基本信息"));
		
		serialLbl.setText("串口");
		serialNameLbl.setText("串口名");
		baudLbl.setText("波特率");
		fluidicsLbl.setText("流控");
		dataBitLbl.setText("数据位");
		parityLbl.setText("校验位");
		stopBitLbl.setText("停止位");
		modeLbl.setText("模式");
		
		serialFld.setColumns(20);
		serialNameFld.setColumns(20);
		baudFld.setColumns(20);
		dataBitFld.setColumns(20);
		parityFld.setColumns(20);
		stopBitFld.setColumns(20);
		modeFld.setColumns(20);
		
		serialFld.setEditable(false);
		serialNameFld.setEditable(false);
		baudFld.setEditable(false);
		dataBitFld.setEditable(false);
		parityFld.setEditable(false);
		stopBitFld.setEditable(false);
		modeFld.setEditable(false);
		
		checkBox.setEnabled(false);
	}
	
	private void setValue(){
		
		if(null != switchSerialPort){
			int mode = switchSerialPort.getSerialMode();
			cardLayout.show(dynamicalPanel, "" + mode);
		}
		
		if(null != switchSerialPort){
			serialFld.setText("" + switchSerialPort.getPortNo());
			serialNameFld.setText(switchSerialPort.getPortName());
			baudFld.setText("" + switchSerialPort.getBaudRate());
			dataBitFld.setText("" + switchSerialPort.getDatabit());
			parityFld.setText(reverseCheckToString(switchSerialPort.getCheckbit()));
			stopBitFld.setText("" + switchSerialPort.getStopbit());
			modeFld.setText(reverseIntToString(switchSerialPort.getSerialMode()));
			
			if(switchSerialPort.getSerialMode() == 1){
				tcpClientRemotePortFld.setText(switchSerialPort.getTcpclientRemotePort());
				tcpClientRemoteIpFld.setText(switchSerialPort.getTcpclientRemoteIP());
			}else if(switchSerialPort.getSerialMode() == 0){
				tcpServerLocalPortFld.setText(switchSerialPort.getTcpserverLocalPort());
			}else if(switchSerialPort.getSerialMode() == 3){
				udpClientRemoteIpFld.setText(switchSerialPort.getUdpclientRemoteIp());
				udpClientRemotePortFld.setText(switchSerialPort.getUdpclientRemotePort());
			}else if(switchSerialPort.getSerialMode() == 2){
				udpServerLocalPortFld.setText(switchSerialPort.getUdpserverLocalPort());				
			}
		}
	}
	
	private PropertyChangeListener SelectionChangedLinstener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switchSerialPort = (SwitchSerialPort) adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchSerialPort.class);
			setValue();
		}		
	};
	
	private String reverseIntToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "tcpserver";
				break;
			case 1:
				str = "tcpclient";
				break;
			case 2:
				str = "udpserver";
				break;
			case 3:
				str = "udpclient";
				break;
		}
		
		return str;
	}
	
	private String reverseCheckToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "none";
				break;
			case 1:
				str = "odd";
				break;
			case 2:
				str = "even";
				break;
		}

		return str;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedLinstener);
	}
}
