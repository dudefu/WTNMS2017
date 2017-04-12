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
public class WarningHistoryEntity implements Serializable{

	/**
	 * �鲢��澯��ʷ��Ϣ
	 * �ǰ���ipValue(�澯��Դ)��warningType(�澯����)���й鲢
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String ipValue;    //ip��ַ
	private String nodeName;   //�澯��Դ
	private int warningEvent;  //�澯�¼�
	private int warningLevel;  //�澯����
	private int warningCategory;//�澯���
	private Date firstTime;    //�״θ澯ʱ��
	private Date lastTime;     //����澯ʱ��
	private String content;    //�澯����
	private int warningCount;  //�澯Ƶ��
	private Date lastConfirmTime;  //�����ȷ��ʱ��

	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * ip��ַ
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	/**
	 * �澯��Դ
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
	 * @return
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	/**
	 * �澯����
	 * @return
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	public int getWarningEvent() {
		return warningEvent;
	}

	/**
	 * ����
	 * @return
	 */
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	/**
	 * �澯����
	 * @return
	 */
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * �״θ澯ʱ��
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	/**
	 * ����澯ʱ��
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	/**
	 * �澯Ƶ��
	 * @return
	 */
	public int getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}

	/**
	 * �����ȷ��ʱ��
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastConfirmTime() {
		return lastConfirmTime;
	}

	public void setLastConfirmTime(Date lastConfirmTime) {
		this.lastConfirmTime = lastConfirmTime;
	}

	/**
	 * �澯���
	 * @return
	 */
	public int getWarningCategory() {
		return warningCategory;
	}

	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}
	
}
