package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WarningType implements Serializable {
	/**
	 * �澯�¼�(������,������,�˿ڶϿ�,�˿�����,����,Ping��ͨ,Ping�ɹ�,ǰ�û��Ͽ�,ǰ�û�����,syslog)
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int warningType;
	private int warningLevel;
	private String warningStyle;//��������Ϣ.���ַ�ʽ��ʱ����","����
	private String desccs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getWarningType() {
		return warningType;
	}

	public void setWarningType(int warningType) {
		this.warningType = warningType;
	}

	public int getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	public String getWarningStyle() {
		return warningStyle;
	}

	public void setWarningStyle(String warningStyle) {
		this.warningStyle = warningStyle;
	}

	public String getDesccs() {
		return desccs;
	}

	public void setDesccs(String desccs) {
		this.desccs = desccs;
	}

}
