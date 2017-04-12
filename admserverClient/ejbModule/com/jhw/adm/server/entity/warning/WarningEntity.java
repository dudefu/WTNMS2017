package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class WarningEntity implements Serializable {
	/**
	 * 保存每一条告警的详细信息
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nodeName;       //上报节点的名称
	private String ipValue;        //上报设备的IP
	private int warningEvent;       //分为冷启动,热启动,linkdown,linkup等一共9种情况
	private int currentStatus;     //当前状态,未确认，已确认(0,1)

	private Date createDate;       //告警上报的时间
	private int warningLevel;      //分为紧急,严重,通知,普通
	private int warningCategory;   //告警类别,分为设备告警,端口告警,性能告警,网管告警,协议告警
	private int portNo;            //上报设备的端口号
	private int portType;          //端口类型,有GE,FE和PON口
	private int deviceType;        //二层和三层交换机,OLT,ONU
	private Long nodeId;           //通过ipValue查询到的节点ID
	private Long linkId;           //通过ipValue和portNo查询到的连线的ID
	
	private String confirmUserName;    //确认用户名称
	private Date confirmTime;      //每条告警的确认时间
	
	private Long warningHistoryID; //告警历史ID
	
	private Long subnetId;   //所属子网ID
	private String subnetName;   //所属子网名称

	private int slotNum;// 插槽（针对olt）
	private int onuSequenceNo;// 告警onu的编号
	private String warnOnuMac;// 告警onu的mac地址
	
	private String descs;   //内容
	
	private String comment; //注释
	
//	private WarningOperater opreater; //
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 上报节点的名称
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * 告警事件
	 * 分为冷启动,热启动,linkdown,linkup等一共9种情况
	 * @return
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * 当前状态,未确认，已确认(0,1)
	 * @return
	 */
	public int getCurrentStatus() {
		return currentStatus;
	}

	public int getWarningEvent() {
		return warningEvent;
	}

	

	/**
	 * 上报设备的IP
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	/**
	 * 告警内容
	 * @return
	 */
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	/**
	 * 告警上报的时间
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 分为紧急,严重,通知,普通
	 * @return
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	/**
	 * 上报设备的端口号
	 * @return
	 */
	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	/**
	 * 通过ipValue和portNo查询到的连线的ID
	 * @return
	 */
	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	/**
	 * 端口类型,有GE,FE和PON口
	 * @return
	 */
	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	/**
	 * 二层和三层交换机,OLT,ONU
	 * @return
	 */
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 *  插槽（针对olt）
	 * @return
	 */
	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	/**
	 * 告警onu的编号
	 * @return
	 */
	public int getOnuSequenceNo() {
		return onuSequenceNo;
	}

	public void setOnuSequenceNo(int onuSequenceNo) {
		this.onuSequenceNo = onuSequenceNo;
	}

	/**
	 * 告警onu的mac地址
	 * @return
	 */
	public String getWarnOnuMac() {
		return warnOnuMac;
	}

	public void setWarnOnuMac(String warnOnuMac) {
		this.warnOnuMac = warnOnuMac;
	}

	/**
	 * 通过ipValue查询到的节点ID
	 * @return
	 */
	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * 确认用户名称
	 * @return
	 */
	public String getConfirmUserName() {
		return confirmUserName;
	}

	public void setConfirmUserName(String confirmUserName) {
		this.confirmUserName = confirmUserName;
	}

	/**
	 * 告警历史ID
	 * @return
	 */
	public Long getWarningHistoryID() {
		return warningHistoryID;
	}

	public void setWarningHistoryID(Long warningHistoryID) {
		this.warningHistoryID = warningHistoryID;
	}

	/**
	 * 每条告警的确认时间
	 * @return
	 */
	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	/**
	 * 告警类别,分为设备告警,端口告警,性能告警,网管告警等
	 * @return
	 */
	public int getWarningCategory() {
		return warningCategory;
	}

	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}

	/**
	 * 所属子网ID
	 * @return
	 */
	public Long getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(Long subnetId) {
		this.subnetId = subnetId;
	}

	/**
	 * 所属子网名称
	 * @return
	 */
	public String getSubnetName() {
		return subnetName;
	}

	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}

	/**
	 * 注释
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	
}
