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
	 * 归并后告警历史信息
	 * 是按照ipValue(告警来源)和warningType(告警类型)进行归并
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String ipValue;    //ip地址
	private String nodeName;   //告警来源
	private int warningEvent;  //告警事件
	private int warningLevel;  //告警级别
	private int warningCategory;//告警类别
	private Date firstTime;    //首次告警时间
	private Date lastTime;     //最近告警时间
	private String content;    //告警内容
	private int warningCount;  //告警频次
	private Date lastConfirmTime;  //最近的确认时间

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
	 * ip地址
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	/**
	 * 告警来源
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
	 * @return
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	/**
	 * 告警级别
	 * @return
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	public int getWarningEvent() {
		return warningEvent;
	}

	/**
	 * 描述
	 * @return
	 */
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	/**
	 * 告警内容
	 * @return
	 */
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 首次告警时间
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
	 * 最近告警时间
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
	 * 告警频次
	 * @return
	 */
	public int getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}

	/**
	 * 最近的确认时间
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
	 * 告警类别
	 * @return
	 */
	public int getWarningCategory() {
		return warningCategory;
	}

	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}
	
}
