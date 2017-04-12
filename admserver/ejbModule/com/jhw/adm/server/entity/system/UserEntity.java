package com.jhw.adm.server.entity.system;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.tuopos.NodeEntity;

@Entity
@Table(name = "UserEntity")
public class UserEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String userName;
	private String password;
	private RoleEntity role;
	private List<AreaEntity> areas;
	private PersonInfo personInfo;
	private String clientIp;
	private String careLevel;// 多种级别的时候，用逗号","隔开，如：1,2
	private String warningStyle;// 短信、邮件 多种方式的时候，用","隔开，如："S,E"
	private String descs;
	private Set<NodeEntity> nodes =new HashSet<NodeEntity>();
	private long currentTime = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	public List<AreaEntity> getAreas() {
		return areas;
	}

	public void setAreas(List<AreaEntity> areas) {
		this.areas = areas;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "personID")
	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

	@ManyToOne
	@JoinColumn(name = "RoleID")
	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getCareLevel() {
		return careLevel;
	}

	public void setCareLevel(String careLevel) {
		this.careLevel = careLevel;
	}

	public String getWarningStyle() {
		return warningStyle;
	}

	public void setWarningStyle(String warningStyle) {
		this.warningStyle = warningStyle;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	@OneToMany(cascade={CascadeType.MERGE,CascadeType.PERSIST},fetch=FetchType.LAZY)
	public Set<NodeEntity> getNodes() {
		return nodes;
	}

	public void setNodes(Set<NodeEntity> nodes) {
		this.nodes = nodes;
	}

	

}
