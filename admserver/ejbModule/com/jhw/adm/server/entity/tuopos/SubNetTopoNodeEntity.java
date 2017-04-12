package com.jhw.adm.server.entity.tuopos;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
@Entity
@DiscriminatorValue(value = "SN")
public class SubNetTopoNodeEntity extends NodeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String guid;
	private List<NodeEntity> nodes;
	private List<LinkEntity> lines;

	@Transient
	public List<NodeEntity> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeEntity> nodes) {
		this.nodes = nodes;
	}

	@Transient
	public List<LinkEntity> getLines() {
		return lines;
	}

	public void setLines(List<LinkEntity> lines) {
		this.lines = lines;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

}
