package com.jhw.adm.server.entity.nets;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fepentity")
public class FEPEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private String fepName;
	private String loginName;
	private String loginPassword;
	private String ipValue;
	private String directSwitchIp;
	private List<IPSegment> segment;
	private StatusEntity status;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getFepName() {
		return fepName;
	}

	public void setFepName(String fepName) {
		this.fepName = fepName;
	}

	@OneToMany(mappedBy="fepEntity",fetch=FetchType.EAGER,cascade={CascadeType.ALL})
	public List<IPSegment> getSegment() {
		return segment;
	}

	public void setSegment(List<IPSegment> segment) {
		this.segment = segment;
	}

	@OneToOne(cascade={CascadeType.ALL})
	public StatusEntity getStatus() {
		return status;
	}

	public void setStatus(StatusEntity status) {
		this.status = status;
	}

	public String getDirectSwitchIp() {
		return directSwitchIp;
	}

	public void setDirectSwitchIp(String directSwitchIp) {
		this.directSwitchIp = directSwitchIp;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
