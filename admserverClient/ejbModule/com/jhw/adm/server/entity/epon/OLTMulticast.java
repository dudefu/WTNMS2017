package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * OLT组播配置
 * 
 * @author Snow
 * 
 */
@Entity
@Table
public class OLTMulticast implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean DLF_Drop;// 报文风暴抑制功能
	private int routeAge;
	private int v1_IGMPNum;// V1 IGMP包个数
	private int v2_IGMPNum;
	private int v3_IGMPNum;
	private int join_IGMPNum;
	private int leaves_IGMPNum;
	private int comm_IGMPNum;// 通用查询IGMP包个数
	private int special_IGMPNum;// 特殊查询IGMP包个数
	private int mcstStatus;
	private int mcstModle;
	private int igmpProxyStatus;// IGMP代理状态
	private String igmp_ByIp;// IGMP查询者IP地址
	private int maxRespondTime;// 最大查询响应时间秒
	private int intervalsTime;// 最后成员查询间隔
	private int queryCount;// 最后成员查询数量
	private OLTEntity oltEntity;
	private boolean syschorized = true;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isDLF_Drop() {
		return DLF_Drop;
	}

	public void setDLF_Drop(boolean dLFDrop) {
		DLF_Drop = dLFDrop;
	}

	public int getRouteAge() {
		return routeAge;
	}

	public void setRouteAge(int routeAge) {
		this.routeAge = routeAge;
	}

	public int getV1_IGMPNum() {
		return v1_IGMPNum;
	}

	public void setV1_IGMPNum(int v1IGMPNum) {
		v1_IGMPNum = v1IGMPNum;
	}

	public int getV2_IGMPNum() {
		return v2_IGMPNum;
	}

	public void setV2_IGMPNum(int v2IGMPNum) {
		v2_IGMPNum = v2IGMPNum;
	}

	public int getV3_IGMPNum() {
		return v3_IGMPNum;
	}

	public void setV3_IGMPNum(int v3IGMPNum) {
		v3_IGMPNum = v3IGMPNum;
	}

	public int getJoin_IGMPNum() {
		return join_IGMPNum;
	}

	public void setJoin_IGMPNum(int joinIGMPNum) {
		join_IGMPNum = joinIGMPNum;
	}

	public int getLeaves_IGMPNum() {
		return leaves_IGMPNum;
	}

	public void setLeaves_IGMPNum(int leavesIGMPNum) {
		leaves_IGMPNum = leavesIGMPNum;
	}

	public int getComm_IGMPNum() {
		return comm_IGMPNum;
	}

	public void setComm_IGMPNum(int commIGMPNum) {
		comm_IGMPNum = commIGMPNum;
	}

	public int getSpecial_IGMPNum() {
		return special_IGMPNum;
	}

	public void setSpecial_IGMPNum(int specialIGMPNum) {
		special_IGMPNum = specialIGMPNum;
	}

	public int getMcstStatus() {
		return mcstStatus;
	}

	public void setMcstStatus(int mcstStatus) {
		this.mcstStatus = mcstStatus;
	}

	public int getMcstModle() {
		return mcstModle;
	}

	public void setMcstModle(int mcstModle) {
		this.mcstModle = mcstModle;
	}

	public int getIgmpProxyStatus() {
		return igmpProxyStatus;
	}

	public void setIgmpProxyStatus(int igmpProxyStatus) {
		this.igmpProxyStatus = igmpProxyStatus;
	}

	public String getIgmp_ByIp() {
		return igmp_ByIp;
	}

	public void setIgmp_ByIp(String igmpByIp) {
		igmp_ByIp = igmpByIp;
	}

	public int getMaxRespondTime() {
		return maxRespondTime;
	}

	public void setMaxRespondTime(int maxRespondTime) {
		this.maxRespondTime = maxRespondTime;
	}

	public int getIntervalsTime() {
		return intervalsTime;
	}

	public void setIntervalsTime(int intervalsTime) {
		this.intervalsTime = intervalsTime;
	}

	public int getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	@ManyToOne
	@JoinColumn(name = "oltID")
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
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

}
