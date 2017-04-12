package com.jhw.adm.server.entity.tuopos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;

@Entity
@Table(name = "TOPNODE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "NODE_TYPE", discriminatorType = DiscriminatorType.STRING, length = 2)
public class NodeEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String nodeType;
	private double x;
	private double y;
	private int status = 1;//1:正常；-1:端口，即ping不通；0：周围的设备与该设备连接的端口都端口。
	private boolean synchorized = true;
	private String descs;
	private String parentNode;
	private TopDiagramEntity topDiagramEntity;
	private Set<AreaEntity> areas =new HashSet<AreaEntity>();
	private Set<UserEntity> users =new HashSet<UserEntity>();
	private boolean modifyNode=false;
	
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

	@Column(name = "NODE_TYPE", insertable = false, updatable = false)
	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSynchorized() {
		return synchorized;
	}

	public void setSynchorized(boolean synchorized) {
		this.synchorized = synchorized;
	}
	@ManyToOne
	public TopDiagramEntity getTopDiagramEntity() {
		return topDiagramEntity;
	}
	public void setTopDiagramEntity(TopDiagramEntity topDiagramEntity) {
		this.topDiagramEntity = topDiagramEntity;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean isModifyNode() {
		return modifyNode;
	}

	public void setModifyNode(boolean modifyNode) {
		this.modifyNode = modifyNode;
	}

	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	@ManyToMany(fetch=FetchType.EAGER)
	public Set<AreaEntity> getAreas() {
		return areas;
	}

	public void setAreas(Set<AreaEntity> areas) {
		this.areas = areas;
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	public Set<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Set<UserEntity> users) {
		this.users = users;
	}
    
}
