package com.jhw.adm.client.model;

import java.util.Date;

import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.NoteEntity;
import com.jhw.adm.server.entity.warning.RmonEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;

/**
 * 告警信息包装器
 */
public final class AlarmMessage {
	
	private AlarmMessage(){}
	
	public static AlarmMessage wrap(WarningEntity warning) {
		AlarmMessage alarm = new AlarmMessage();
		alarm.setLinkId(warning.getLinkId());
		alarm.setStatus(Constants.L_UNCONNECT);
		alarm.setWrapped(warning);
		alarm.setCreateDate(warning.getCreateDate());
		alarm.setIpValue(warning.getIpValue());
		alarm.setPortNo(warning.getPortNo());
		alarm.setWarningType(warning.getWarningEvent());
		alarm.setDescs(warning.getDescs());
		return alarm;
	}
	
	public static AlarmMessage wrap(NoteEntity note) {
		AlarmMessage alarm = new AlarmMessage();
		alarm.setLinkId(note.getTargetDiagramId());
		alarm.setStatus(Constants.L_CONNECT);
		alarm.setWrapped(note);
		alarm.setCreateDate(note.getCreateDate());
		alarm.setIpValue(note.getIpValue());
		alarm.setPortNo(note.getPortNo());
		alarm.setWarningType(note.getNoteType());
		alarm.setDescs(note.getDescs());
		return alarm;
	}
	
	public static AlarmMessage wrap(RmonEntity remon) {
		AlarmMessage alarm = new AlarmMessage();
		alarm.setLinkId(remon.getLinkId());	
		alarm.setWrapped(remon);
		alarm.setCreateDate(remon.getCreateDate());
		alarm.setIpValue(remon.getIpValue());
		alarm.setPortNo(remon.getPortNo());
		alarm.setWarningType(remon.getWarningType());
		alarm.setDescs(remon.getDescs());
		return alarm;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLinkId() {
		return linkId;
	}
	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}
	public int getWarningType() {
		return warningType;
	}
	public void setWarningType(int warningType) {
		this.warningType = warningType;
	}
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Object getWrapped() {
		return wrapped;
	}
	public void setWrapped(Object wrapped) {
		this.wrapped = wrapped;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	private Long id;
	private Long linkId;
	private int status;
	private int warningType;
	private String ipValue;
	private int portNo;
	private String descs;
	private Date createDate;
	private Object wrapped;
}