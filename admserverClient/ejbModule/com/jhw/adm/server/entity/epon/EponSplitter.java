package com.jhw.adm.server.entity.epon;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.jhw.adm.server.entity.util.AddressEntity;
/**
 * ·Ö¹âÆ÷
 * @author Snow
 *
 */
@Entity
public class EponSplitter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private Set<EponSplitterPort> splitterPorts;
	private AddressEntity addressEntity;
	private boolean syschorized=true;
	private String descs;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	public Set<EponSplitterPort> getSplitterPorts() {
		return splitterPorts;
	}
	public void setSplitterPorts(Set<EponSplitterPort> splitterPorts) {
		this.splitterPorts = splitterPorts;
	}
	public boolean isSyschorized() {
		return syschorized;
	}
	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	@OneToOne(cascade=CascadeType.ALL)
	public AddressEntity getAddressEntity() {
		return addressEntity;
	}
	public void setAddressEntity(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}
	
    
}
