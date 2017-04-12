package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

/**
 * 交换机端口
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SwitchPort")
public class SwitchPortEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private int portNO;// 端口号
	private int type;// 类型:广口、电口
	private boolean connected;
	private boolean worked;
	private String configMode;// 速度双工：自适应/100兆全双工/100兆半双工/10兆全双工/10兆半双工
	private String currentMode;// 速度双工：自适应/100兆全双工/100兆半双工/10兆全双工/10兆半双工
	private boolean flowControl;// 流控
	private String abandonSetting;// 丢弃：none,all,untag
	private SwitchNodeEntity switchNode;
	private boolean syschorized = true;
	private String descs;
	private int issuedTag=0;//1：设备側  0：网管侧
	
	private String Vendorname;
	private String VendorPN ;
	private String Vendorrev ;
	private String VendorSN ;
	private String Datecode ;
	private String BRNominal ;
	private String Wavelength ;
	private String TransMedia ;
	private String LengthSM ;
	private String Temperature ;
	private String Voltage ;
	private String TxBias ;
	private String TxPower ;
	private String RxPower ;
	private String TemperatureHighAlarm ;
	private String TemperatureLowAlarm ;
	private String TemperatureHighWarning ;
	private String TemperatureLowWarning ;
	private String VoltageHighAlarm ;
	private String VoltageLowAlarm ;
	private String VoltageHighWarning ;
	private String VoltageLowWarning ;
	private String TxBiasHighAlarm ;
	private String TxBiasLowAlarm ;
	private String TxBiasHighWarning ;
	private String TxBiasLowWarning ;
	private String TxPowerHighAlarm ;
	private String TxPowerLowAlarm ;
	private String TxPowerHighWarning ;
	private String TxPowerLowWarning ;
	private String RxPowerHighAlarm ;
	private String RxPowerLowAlarm ;
	private String RxPowerHighWarning ;
	private String RxPowerLowWarning ;
	
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	/**
	 * LLDPPort 1：设备側  0：网管侧
	 */
	private int llDP_IssuedTag=0;

	public int getLlDP_IssuedTag() {
		return llDP_IssuedTag;
	}

	public void setLlDP_IssuedTag(int llDPIssuedTag) {
		llDP_IssuedTag = llDPIssuedTag;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isWorked() {
		return worked;
	}

	public void setWorked(boolean worked) {
		this.worked = worked;
	}

	public String getConfigMode() {
		return configMode;
	}

	public void setConfigMode(String configMode) {
		this.configMode = configMode;
	}

	public String getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(String currentMode) {
		this.currentMode = currentMode;
	}

	public boolean isFlowControl() {
		return flowControl;
	}

	public void setFlowControl(boolean flowControl) {
		this.flowControl = flowControl;
	}

	public String getAbandonSetting() {
		return abandonSetting;
	}

	public void setAbandonSetting(String abandonSetting) {
		this.abandonSetting = abandonSetting;
	}

	

	@ManyToOne
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

	public int getPortNO() {
		return portNO;
	}

	public void setPortNO(int portNO) {
		this.portNO = portNO;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	
	public String getVendorname() {
		return Vendorname;
	}
	public void setVendorname(String vendorname) {
		Vendorname = vendorname;
	}
	public String getVendorPN() {
		return VendorPN;
	}
	public void setVendorPN(String vendorPN) {
		VendorPN = vendorPN;
	}
	public String getVendorrev() {
		return Vendorrev;
	}
	public void setVendorrev(String vendorrev) {
		Vendorrev = vendorrev;
	}
	public String getVendorSN() {
		return VendorSN;
	}
	public void setVendorSN(String vendorSN) {
		VendorSN = vendorSN;
	}
	public String getDatecode() {
		return Datecode;
	}
	public void setDatecode(String datecode) {
		Datecode = datecode;
	}
	public String getBRNominal() {
		return BRNominal;
	}
	public void setBRNominal(String bRNominal) {
		BRNominal = bRNominal;
	}
	public String getWavelength() {
		return Wavelength;
	}
	public void setWavelength(String wavelength) {
		Wavelength = wavelength;
	}
	public String getTransMedia() {
		return TransMedia;
	}
	public void setTransMedia(String transMedia) {
		TransMedia = transMedia;
	}
	public String getLengthSM() {
		return LengthSM;
	}
	public void setLengthSM(String lengthSM) {
		LengthSM = lengthSM;
	}
	public String getTemperature() {
		return Temperature;
	}
	public void setTemperature(String temperature) {
		Temperature = temperature;
	}
	public String getVoltage() {
		return Voltage;
	}
	public void setVoltage(String voltage) {
		Voltage = voltage;
	}
	public String getTxBias() {
		return TxBias;
	}
	public void setTxBias(String txBias) {
		TxBias = txBias;
	}
	public String getTxPower() {
		return TxPower;
	}
	public void setTxPower(String txPower) {
		TxPower = txPower;
	}
	public String getRxPower() {
		return RxPower;
	}
	public void setRxPower(String rxPower) {
		RxPower = rxPower;
	}
	public String getTemperatureHighAlarm() {
		return TemperatureHighAlarm;
	}
	public void setTemperatureHighAlarm(String temperatureHighAlarm) {
		TemperatureHighAlarm = temperatureHighAlarm;
	}
	public String getTemperatureLowAlarm() {
		return TemperatureLowAlarm;
	}
	public void setTemperatureLowAlarm(String temperatureLowAlarm) {
		TemperatureLowAlarm = temperatureLowAlarm;
	}
	public String getTemperatureHighWarning() {
		return TemperatureHighWarning;
	}
	public void setTemperatureHighWarning(String temperatureHighWarning) {
		TemperatureHighWarning = temperatureHighWarning;
	}
	public String getTemperatureLowWarning() {
		return TemperatureLowWarning;
	}
	public void setTemperatureLowWarning(String temperatureLowWarning) {
		TemperatureLowWarning = temperatureLowWarning;
	}
	public String getVoltageHighAlarm() {
		return VoltageHighAlarm;
	}
	public void setVoltageHighAlarm(String voltageHighAlarm) {
		VoltageHighAlarm = voltageHighAlarm;
	}
	public String getVoltageLowAlarm() {
		return VoltageLowAlarm;
	}
	public void setVoltageLowAlarm(String voltageLowAlarm) {
		VoltageLowAlarm = voltageLowAlarm;
	}
	public String getVoltageHighWarning() {
		return VoltageHighWarning;
	}
	public void setVoltageHighWarning(String voltageHighWarning) {
		VoltageHighWarning = voltageHighWarning;
	}
	public String getVoltageLowWarning() {
		return VoltageLowWarning;
	}
	public void setVoltageLowWarning(String voltageLowWarning) {
		VoltageLowWarning = voltageLowWarning;
	}
	public String getTxBiasHighAlarm() {
		return TxBiasHighAlarm;
	}
	public void setTxBiasHighAlarm(String txBiasHighAlarm) {
		TxBiasHighAlarm = txBiasHighAlarm;
	}
	public String getTxBiasLowAlarm() {
		return TxBiasLowAlarm;
	}
	public void setTxBiasLowAlarm(String txBiasLowAlarm) {
		TxBiasLowAlarm = txBiasLowAlarm;
	}
	public String getTxBiasHighWarning() {
		return TxBiasHighWarning;
	}
	public void setTxBiasHighWarning(String txBiasHighWarning) {
		TxBiasHighWarning = txBiasHighWarning;
	}
	public String getTxBiasLowWarning() {
		return TxBiasLowWarning;
	}
	public void setTxBiasLowWarning(String txBiasLowWarning) {
		TxBiasLowWarning = txBiasLowWarning;
	}
	public String getTxPowerHighAlarm() {
		return TxPowerHighAlarm;
	}
	public void setTxPowerHighAlarm(String txPowerHighAlarm) {
		TxPowerHighAlarm = txPowerHighAlarm;
	}
	public String getTxPowerLowAlarm() {
		return TxPowerLowAlarm;
	}
	public void setTxPowerLowAlarm(String txPowerLowAlarm) {
		TxPowerLowAlarm = txPowerLowAlarm;
	}
	public String getTxPowerHighWarning() {
		return TxPowerHighWarning;
	}
	public void setTxPowerHighWarning(String txPowerHighWarning) {
		TxPowerHighWarning = txPowerHighWarning;
	}
	public String getTxPowerLowWarning() {
		return TxPowerLowWarning;
	}
	public void setTxPowerLowWarning(String txPowerLowWarning) {
		TxPowerLowWarning = txPowerLowWarning;
	}
	public String getRxPowerHighAlarm() {
		return RxPowerHighAlarm;
	}
	public void setRxPowerHighAlarm(String rxPowerHighAlarm) {
		RxPowerHighAlarm = rxPowerHighAlarm;
	}
	public String getRxPowerLowAlarm() {
		return RxPowerLowAlarm;
	}
	public void setRxPowerLowAlarm(String rxPowerLowAlarm) {
		RxPowerLowAlarm = rxPowerLowAlarm;
	}
	public String getRxPowerHighWarning() {
		return RxPowerHighWarning;
	}
	public void setRxPowerHighWarning(String rxPowerHighWarning) {
		RxPowerHighWarning = rxPowerHighWarning;
	}
	public String getRxPowerLowWarning() {
		return RxPowerLowWarning;
	}
	public void setRxPowerLowWarning(String rxPowerLowWarning) {
		RxPowerLowWarning = rxPowerLowWarning;
	}

}
