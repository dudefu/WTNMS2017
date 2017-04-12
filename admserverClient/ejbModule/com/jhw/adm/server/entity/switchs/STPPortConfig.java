package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "PortRSTP")
public class STPPortConfig  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private boolean stpModed;// 是否启用stp
	private boolean edgePorted;// 是否是边缘端口
	private int lujingkaixiao;// 路径开销
	private int prelevel;// 优先级:0-240
	private int p2pPort;// 0:自动；1：是；2：否
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	private int issuedTag=0;//1：设备側  0：网管侧
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

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public boolean isStpModed() {
		return stpModed;
	}

	public void setStpModed(boolean stpModed) {
		this.stpModed = stpModed;
	}

	public boolean isEdgePorted() {
		return edgePorted;
	}

	public void setEdgePorted(boolean edgePorted) {
		this.edgePorted = edgePorted;
	}

	public int getLujingkaixiao() {
		return lujingkaixiao;
	}

	public void setLujingkaixiao(int lujingkaixiao) {
		this.lujingkaixiao = lujingkaixiao;
	}

	public int getPrelevel() {
		return prelevel;
	}

	public void setPrelevel(int prelevel) {
		this.prelevel = prelevel;
	}

	public int getP2pPort() {
		return p2pPort;
	}

	public void setP2pPort(int p2pPort) {
		this.p2pPort = p2pPort;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

}
