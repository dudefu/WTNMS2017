package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

/**
 * ��̬�˿ھۺ�����/lacp
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "LACPConfig")
public class LACPConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private boolean applied;// �Ƿ�����
	private String applyKey;// ��Կ
	private int applyRole;// ��ɫ������/����
	private boolean syschorized=true;
	private SwitchNodeEntity switchNode;
	private String descs;
    
	
	
	private int issuedTag=0;//1���豸��  0�����ܲ�
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public String getApplyKey() {
		return applyKey;
	}

	public void setApplyKey(String applyKey) {
		this.applyKey = applyKey;
	}

	public int getApplyRole() {
		return applyRole;
	}

	public void setApplyRole(int applyRole) {
		this.applyRole = applyRole;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
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

}
