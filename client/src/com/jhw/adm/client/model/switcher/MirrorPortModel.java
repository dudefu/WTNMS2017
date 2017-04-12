package com.jhw.adm.client.model.switcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JCheckBox;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.switchs.MirrorEntity;

@Component(MirrorPortModel.ID)
public class MirrorPortModel extends ViewModel{
	public static final String ID = "mirrorPortModel";
	
	private MirrorEntity mirrorEntity = null;
	
	private boolean applied = false; //是否启用
	
	private String portMirror = ""; //端口镜像
	
	private String inBit = ""; //入口比率
	
	private String outBit = ""; //出口比率
	
	private String inPorts = null; //入口方向端口

	private String outPorts = null; //出口方向端口
	
	private String outScanMac = ""; //出方向监控MAC

	private String inScanMac = ""; //入方向监控MAC
	
	private String outScanMode = ""; //出方向监控模式
	
	private String inScanMode = ""; //入方向监控模式
	
	private int portCount = 0 ; //端口总数
	
	private List<JCheckBox> inChkBoxList = new ArrayList<JCheckBox>();
	
	private List<JCheckBox> outChkBoxList = new ArrayList<JCheckBox>();
	
	
	@PostConstruct
	protected void initialize() {
		
	}
	

	public MirrorEntity getMirrorEntity() {
		mirrorEntity.setApplied(this.isApplied());
		mirrorEntity.setPortNo(reverseString(this.getPortMirror()));
		mirrorEntity.setInbit(Integer.parseInt(this.getInBit()));
		mirrorEntity.setOutbit(Integer.parseInt(this.getOutBit()));
		
		mirrorEntity.setInports(this.getInPorts());
		mirrorEntity.setOutports(this.getOutPorts());

		mirrorEntity.setInscanMac(this.getInScanMac());
		mirrorEntity.setOutscanMac(this.getOutScanMac());
		
		mirrorEntity.setScanInMode(this.getInScanMode());
		mirrorEntity.setScanOutMode(this.getOutScanMode());
		
		return mirrorEntity;
	}

	public void setMirrorEntity(MirrorEntity mirrorEntity) {
		this.mirrorEntity = mirrorEntity;
		this.setApplied(mirrorEntity.isApplied());
		
		int portNo = mirrorEntity.getPortNo();
		if (0 == portNo){
			this.setPortMirror("NULL");
		}
		else{
			this.setPortMirror("端口 " + mirrorEntity.getPortNo());
		}
		
		this.setInBit(String.valueOf(mirrorEntity.getInbit()));
		this.setOutBit(String.valueOf(mirrorEntity.getOutbit()));
		
		String inportStr = mirrorEntity.getInports();
		if (null == inportStr || "".equals(inportStr)){
			this.setInPorts(null);
		}
		else{
			this.setInPorts(inportStr);
		}
		
		String outportStr = mirrorEntity.getOutports();
		if (null == outportStr || "".equals(outportStr)){
			this.setOutPorts(null);
		}
		else{
			this.setOutPorts(outportStr);
		}
		
		this.setOutScanMac(mirrorEntity.getOutscanMac());
		this.setInScanMac(mirrorEntity.getInscanMac());
		
		this.setOutScanMode(mirrorEntity.getScanOutMode());
		this.setInScanMode(mirrorEntity.getScanInMode());
	}
	
	public int getPortCount() {
		return portCount;
	}

	public void setPortCount(int portCount) {
		this.portCount = portCount;
	}
	
	public boolean isApplied() {
		return applied;
	}


	public void setApplied(boolean applied) {
		this.applied = applied;
	}


	public String getPortMirror() {
		return portMirror;
	}


	public void setPortMirror(String portMirror) {
		this.portMirror = portMirror;
	}


	public String getInBit() {
		return inBit;
	}


	public void setInBit(String inBit) {
		this.inBit = inBit;
	}


	public String getOutBit() {
		return outBit;
	}


	public void setOutBit(String outBit) {
		this.outBit = outBit;
	}


	public String getInPorts() {
		return inPorts;
	}


	public void setInPorts(String inPorts) {
		this.inPorts = inPorts;
	}


	public String getOutPorts() {
		return outPorts;
	}


	public void setOutPorts(String outPorts) {
		this.outPorts = outPorts;
	}


	public String getOutScanMac() {
		return outScanMac;
	}


	public void setOutScanMac(String outScanMac) {
		this.outScanMac = outScanMac;
	}


	public String getInScanMac() {
		return inScanMac;
	}


	public void setInScanMac(String inScanMac) {
		this.inScanMac = inScanMac;
	}


	public String getOutScanMode() {
		return outScanMode;
	}


	public void setOutScanMode(String outScanMode) {
		this.outScanMode = outScanMode;
	}


	public String getInScanMode() {
		return inScanMode;
	}


	public void setInScanMode(String inScanMode) {
		this.inScanMode = inScanMode;
	}
	
	public List<JCheckBox> getInChkBoxList() {
		return inChkBoxList;
	}

	public void setInChkBoxList(int i ,JCheckBox checkBox) {
		this.inChkBoxList.add(i, checkBox);
	}

	public List<JCheckBox> getOutChkBoxList() {
		return outChkBoxList;
	}

	public void setOutChkBoxList(int i ,JCheckBox checkBox) {
		this.outChkBoxList.add(i,checkBox);
	}
	
	public List getPortStrList(){
		List<String> portStrList = new ArrayList<String>();
		for(int i = 0 ; i < portCount + 1; i++){
			if (0 == i){
				portStrList.add(i, "NULL");
			}
			else{
				portStrList.add(i,"端口 " + i);
			}
		}
		return portStrList;
	}
	
	
	private int reverseString(String str){
		boolean isNum = StringUtils.isNumeric(str);
		if (isNum){
			return NumberUtils.toInt(str);
		}
		return 0;
	}
}
