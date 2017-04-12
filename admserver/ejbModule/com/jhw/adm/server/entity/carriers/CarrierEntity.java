package com.jhw.adm.server.entity.carriers;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.util.AddressEntity;

/**
 * 载波机
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "Carrier")
public class CarrierEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int carrierCode;
	private int type;
	private int waveBand1;
	private int waveBand2;
	private AddressEntity address;
	private Set<CarrierPortEntity> ports;
	private String descs;
	private AssociationEntity association;
	private Set<CarrierRouteEntity> routes;
	private int version;
	private int marking;//1:单通道载波机  2:双通道载波机
	private String guid;
	private String fepCode;
	private int timeout1;
	private int timeout2;
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	@OneToMany(mappedBy = "carrier", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<CarrierPortEntity> getPorts() {
		return ports;
	}
	public void setPorts(Set<CarrierPortEntity> ports) {
		this.ports = ports;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getCarrierCode() {
		return carrierCode;
	}
	public void setCarrierCode(int carrierCode) {
		this.carrierCode = carrierCode;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name="addressID")
	public AddressEntity getAddress() {
		return address;
	}
	public void setAddress(AddressEntity address) {
		this.address = address;
	}
	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getFepCode() {
		return fepCode;
	}

	public void setFepCode(String fepCode) {
		this.fepCode = fepCode;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "associationID")
	public AssociationEntity getAssociation() {
		return association;
	}
	public void setAssociation(AssociationEntity association) {
		this.association = association;
	}
	@OneToMany(cascade = { CascadeType.PERSIST,CascadeType.MERGE }, fetch = FetchType.EAGER,mappedBy="carrier")
	public Set<CarrierRouteEntity> getRoutes() {
		return routes;
	}

	public void setRoutes(Set<CarrierRouteEntity> routes) {
		this.routes = routes;
	}
	public int getWaveBand1() {
		return waveBand1;
	}
	public void setWaveBand1(int waveBand1) {
		this.waveBand1 = waveBand1;
	}
	public int getWaveBand2() {
		return waveBand2;
	}
	public void setWaveBand2(int waveBand2) {
		this.waveBand2 = waveBand2;
	}
	public int getMarking() {
		return marking;
	}
	public void setMarking(int marking) {
		this.marking = marking;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getTimeout1() {
		return timeout1;
	}
	public void setTimeout1(int timeout1) {
		this.timeout1 = timeout1;
	}
	public int getTimeout2() {
		return timeout2;
	}
	public void setTimeout2(int timeout2) {
		this.timeout2 = timeout2;
	}
	
	

}
