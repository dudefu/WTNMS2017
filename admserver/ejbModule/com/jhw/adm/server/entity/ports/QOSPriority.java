package com.jhw.adm.server.entity.ports;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;

import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

@Entity
@Table(name = "QOSPriority")
public class QOSPriority  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private List<Priority802D1P> priorityEOTs;
	private List<PriorityDSCP> priorityDSCPs;
	private List<PriorityTOS> priorityTOSs;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
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

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@IndexColumn(name="EOTID")
	public List<Priority802D1P> getPriorityEOTs() {
		return priorityEOTs;
	}

	public void setPriorityEOTs(List<Priority802D1P> priorityEOTs) {
		this.priorityEOTs = priorityEOTs;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@IndexColumn(name="DSCPID")
	public List<PriorityDSCP> getPriorityDSCPs() {
		return priorityDSCPs;
	}

	public void setPriorityDSCPs(List<PriorityDSCP> priorityDSCPs) {
		this.priorityDSCPs = priorityDSCPs;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@IndexColumn(name="TOSID")
	public List<PriorityTOS> getPriorityTOSs() {
		return priorityTOSs;
	}

	public void setPriorityTOSs(List<PriorityTOS> priorityTOSs) {
		this.priorityTOSs = priorityTOSs;
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

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
