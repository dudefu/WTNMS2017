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
	 * ����ÿһ���澯����ϸ��Ϣ
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nodeName;       //�ϱ��ڵ������
	private String ipValue;        //�ϱ��豸��IP
	private int warningEvent;       //��Ϊ������,������,linkdown,linkup��һ��9�����
	private int currentStatus;     //��ǰ״̬,δȷ�ϣ���ȷ��(0,1)

	private Date createDate;       //�澯�ϱ���ʱ��
	private int warningLevel;      //��Ϊ����,����,֪ͨ,��ͨ
	private int warningCategory;   //�澯���,��Ϊ�豸�澯,�˿ڸ澯,���ܸ澯,���ܸ澯,Э��澯
	private int portNo;            //�ϱ��豸�Ķ˿ں�
	private int portType;          //�˿�����,��GE,FE��PON��
	private int deviceType;        //��������㽻����,OLT,ONU
	private Long nodeId;           //ͨ��ipValue��ѯ���Ľڵ�ID
	private Long linkId;           //ͨ��ipValue��portNo��ѯ�������ߵ�ID
	
	private String confirmUserName;    //ȷ���û�����
	private Date confirmTime;      //ÿ���澯��ȷ��ʱ��
	
	private Long warningHistoryID; //�澯��ʷID
	
	private Long subnetId;   //��������ID
	private String subnetName;   //������������

	private int slotNum;// ��ۣ����olt��
	private int onuSequenceNo;// �澯onu�ı��
	private String warnOnuMac;// �澯onu��mac��ַ
	
	private String descs;   //����
	
	private String comment; //ע��
	
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
	 * �ϱ��ڵ������
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * �澯�¼�
	 * ��Ϊ������,������,linkdown,linkup��һ��9�����
	 * @return
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * ��ǰ״̬,δȷ�ϣ���ȷ��(0,1)
	 * @return
	 */
	public int getCurrentStatus() {
		return currentStatus;
	}

	public int getWarningEvent() {
		return warningEvent;
	}

	

	/**
	 * �ϱ��豸��IP
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	/**
	 * �澯����
	 * @return
	 */
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	/**
	 * �澯�ϱ���ʱ��
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
	 * ��Ϊ����,����,֪ͨ,��ͨ
	 * @return
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	/**
	 * �ϱ��豸�Ķ˿ں�
	 * @return
	 */
	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	/**
	 * ͨ��ipValue��portNo��ѯ�������ߵ�ID
	 * @return
	 */
	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	/**
	 * �˿�����,��GE,FE��PON��
	 * @return
	 */
	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	/**
	 * ��������㽻����,OLT,ONU
	 * @return
	 */
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 *  ��ۣ����olt��
	 * @return
	 */
	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	/**
	 * �澯onu�ı��
	 * @return
	 */
	public int getOnuSequenceNo() {
		return onuSequenceNo;
	}

	public void setOnuSequenceNo(int onuSequenceNo) {
		this.onuSequenceNo = onuSequenceNo;
	}

	/**
	 * �澯onu��mac��ַ
	 * @return
	 */
	public String getWarnOnuMac() {
		return warnOnuMac;
	}

	public void setWarnOnuMac(String warnOnuMac) {
		this.warnOnuMac = warnOnuMac;
	}

	/**
	 * ͨ��ipValue��ѯ���Ľڵ�ID
	 * @return
	 */
	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * ȷ���û�����
	 * @return
	 */
	public String getConfirmUserName() {
		return confirmUserName;
	}

	public void setConfirmUserName(String confirmUserName) {
		this.confirmUserName = confirmUserName;
	}

	/**
	 * �澯��ʷID
	 * @return
	 */
	public Long getWarningHistoryID() {
		return warningHistoryID;
	}

	public void setWarningHistoryID(Long warningHistoryID) {
		this.warningHistoryID = warningHistoryID;
	}

	/**
	 * ÿ���澯��ȷ��ʱ��
	 * @return
	 */
	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	/**
	 * �澯���,��Ϊ�豸�澯,�˿ڸ澯,���ܸ澯,���ܸ澯��
	 * @return
	 */
	public int getWarningCategory() {
		return warningCategory;
	}

	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}

	/**
	 * ��������ID
	 * @return
	 */
	public Long getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(Long subnetId) {
		this.subnetId = subnetId;
	}

	/**
	 * ������������
	 * @return
	 */
	public String getSubnetName() {
		return subnetName;
	}

	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}

	/**
	 * ע��
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	
}
