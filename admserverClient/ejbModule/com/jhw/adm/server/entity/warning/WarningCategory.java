package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WarningCategory implements Serializable {
	/**
	 * �澯���(�豸�澯,�˿ڸ澯,Э��澯,���ܸ澯,���ܸ澯)
	 */

	private static final long serialVersionUID = 1L;
	private Long id;
	private int warningCategory;
//	private String warningStyle;//��������Ϣ.���ַ�ʽ��ʱ����","����
	private String desccs;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public int getWarningCategory() {
		return warningCategory;
	}
	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}

//	public String getWarningStyle() {
//		return warningStyle;
//	}
//	public void setWarningStyle(String warningStyle) {
//		this.warningStyle = warningStyle;
//	}
	
	public String getDesccs() {
		return desccs;
	}
	public void setDesccs(String desccs) {
		this.desccs = desccs;
	}
	
	
}
