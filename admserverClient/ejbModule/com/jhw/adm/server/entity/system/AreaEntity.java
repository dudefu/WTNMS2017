package com.jhw.adm.server.entity.system;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.jhw.adm.server.entity.nets.IPSegment;

/**
 * µØÇø
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "AreaEntity")
public class AreaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private AreaEntity superArea;
	private String descs;
    private Set<IPSegment> ips =new  HashSet<IPSegment>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@ManyToOne
	public AreaEntity getSuperArea() {
		return superArea;
	}

	public void setSuperArea(AreaEntity superArea) {
		this.superArea = superArea;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	
    @OneToMany(cascade={CascadeType.ALL})
	public Set<IPSegment> getIps() {
		return ips;
	}
	public void setIps(Set<IPSegment> ips) {
		this.ips = ips;
	}
	
	
}
