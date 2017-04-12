package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * rstp��������Э�飩����
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "STPSysConfig")
public class STPSysConfig  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private long pre;// ���ȼ�
	private int maxOldTime;// 6-200s
	private int transferDelay;// ת����ʱ
	private String protocolVersion;// ת����ʱ
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
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

	public long getPre() {
		return pre;
	}

	public void setPre(long pre) {
		this.pre = pre;
	}

	public int getMaxOldTime() {
		return maxOldTime;
	}

	public void setMaxOldTime(int maxOldTime) {
		this.maxOldTime = maxOldTime;
	}

	public int getTransferDelay() {
		return transferDelay;
	}

	public void setTransferDelay(int transferDelay) {
		this.transferDelay = transferDelay;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	@OneToOne
	@JoinColumn(name="SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}
	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

}
