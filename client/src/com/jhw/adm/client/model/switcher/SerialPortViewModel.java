package com.jhw.adm.client.model.switcher;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.util.SwitchSerialPort;


@Component(SerialPortViewModel.ID)
public class SerialPortViewModel extends ViewModel{
	public static final String ID = "serialPortViewModel";
	
	private List<SwitchSerialPort> serialPortList = null;
	
	private SwitchSerialPort switchSerialPort = null;
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public List<SwitchSerialPort> getSerialPortList() {
		return serialPortList;
	}

	public void setSerialPortList(List<SwitchSerialPort> serialPortList) {
		this.serialPortList = serialPortList;
	}

	public SwitchSerialPort getSwitchSerialPort(int port) {
		int portCount = serialPortList.size();
		for (int i = 0; i< portCount; i++){
			switchSerialPort = serialPortList.get(i);
			int portNo = switchSerialPort.getPortNo();
			if (port == portNo){
				break;
			}
		}
		return switchSerialPort;
	}
}
