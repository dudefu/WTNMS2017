package com.jhw.adm.server.entity.tuopos;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TOPDIAGRAM")
public class TopDiagramEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private Set<NodeEntity> nodes;
	private Set<LinkEntity> lines;
	private long lastTime;
	private String descs;

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

	@OneToMany( cascade = { CascadeType.PERSIST,CascadeType.MERGE },mappedBy="topDiagramEntity")
	public Set<NodeEntity> getNodes() {
		return nodes;
	}

	public void setNodes(Set<NodeEntity> nodes) {
		this.nodes = nodes;
	}

	@OneToMany(cascade = { CascadeType.PERSIST,CascadeType.MERGE },mappedBy="topDiagramEntity")
	public Set<LinkEntity> getLines() {
		return lines;
	}

	public void setLines(Set<LinkEntity> lines) {
		this.lines = lines;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

}
