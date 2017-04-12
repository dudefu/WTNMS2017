package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SysLogHostToDev")
public class SysLogHostToDevEntity  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String ipValue;    //�豸IP��ַ
	
	private int hostID;        //����ID
	
	private int hostMode;      //ģʽ    0:disable  1:enable
	
	private SysLogHostEntity sysLogHostEntity;  //syslog��Ϣ
	
	private int issuedTag=0;//1���豸��  0�����ܲ�
	
	private String descs;
	
	private boolean syschorized=true;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * �豸IP��ַ
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	/**
	 * syslog��Ϣ
	 * @return
	 */
	@ManyToOne(cascade={CascadeType.MERGE},fetch=FetchType.EAGER)
	public SysLogHostEntity getSysLogHostEntity() {
		return sysLogHostEntity;
	}
	public void setSysLogHostEntity(SysLogHostEntity sysLogHostEntity) {
		this.sysLogHostEntity = sysLogHostEntity;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}
	
	/**
	 * ����ID
	 * һ���豸���ֻ����4������ID
	 * @return
	 */
	public int getHostID() {
		return hostID;
	}
	public void setHostID(int hostID) {
		this.hostID = hostID;
	}
	
	public int getHostMode() {
		return hostMode;
	}
	public void setHostMode(int hostMode) {
		this.hostMode = hostMode;
	}
}
