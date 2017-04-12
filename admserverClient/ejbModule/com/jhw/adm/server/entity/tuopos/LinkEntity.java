package com.jhw.adm.server.entity.tuopos;


import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;


@Entity
@Table(name = "TOPLINK")
public class LinkEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String guid;
	private int lineType;
	private String node1Guid;
	private String node2Guid;
	private NodeEntity node1;
	private NodeEntity node2;
	private LLDPInofEntity lldp;//
	private CarrierRouteEntity carrierRoute;//表示载波机之间的连线或者
	private boolean synchorized;
	private int status = 1;// －1，表示断开；0，表示阻塞；1表示连接正常
	private int ringID;// 对应环的id
	private String descs;
    private TopDiagramEntity topDiagramEntity;
    private boolean modifyLink=false;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getLineType() {
		return lineType;
	}

	public void setLineType(int lineType) {
		this.lineType = lineType;
	}

	@ManyToOne
	@JoinColumn(name = "startNodeId")
	public NodeEntity getNode1() {
		return node1;
	}

	public void setNode1(NodeEntity node1) {
		this.node1 = node1;
	}

	@ManyToOne
	@JoinColumn(name = "endNodeId")
	public NodeEntity getNode2() {
		return node2;
	}

	public void setNode2(NodeEntity node2) {
		this.node2 = node2;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@OneToOne
	@JoinColumn(name = "lldp")
	public LLDPInofEntity getLldp() {
		return lldp;
	}

	public void setLldp(LLDPInofEntity lldp) {
		this.lldp = lldp;
	}
	@OneToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "carrierRoute")
	public CarrierRouteEntity getCarrierRoute() {
		return carrierRoute;
	}
	public void setCarrierRoute(CarrierRouteEntity carrierRoute) {
		this.carrierRoute = carrierRoute;
	}
	public boolean isSynchorized() {
		return synchorized;
	}

	public void setSynchorized(boolean synchorized) {
		this.synchorized = synchorized;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRingID() {
		return ringID;
	}

	public void setRingID(int ringID) {
		this.ringID = ringID;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
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

	public boolean isModifyLink() {
		return modifyLink;
	}

	public void setModifyLink(boolean modifyLink) {
		this.modifyLink = modifyLink;
	}

	public String getNode1Guid() {
		return node1Guid;
	}

	public void setNode1Guid(String node1Guid) {
		this.node1Guid = node1Guid;
	}

	public String getNode2Guid() {
		return node2Guid;
	}

	public void setNode2Guid(String node2Guid) {
		this.node2Guid = node2Guid;
 	}
   
	
	
}
