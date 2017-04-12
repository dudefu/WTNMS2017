package com.jhw.adm.server.entity.util;

import java.io.Serializable;
import java.util.Date;

/**
 * �澯��ʷbean,
 * ���ڿͻ��˲�ѯ�澯��ʷʱ����������������ɸѡ������
 * @author Administrator
 *
 */
public class WarningHistoryBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String simpleConfirmTimeStr;
	
	private String warningSouce;  
	
	private int warningEvent;
	
	private Date startFirstTime;
	
	private Date endFirstTime;
	
	private Date startLastTime;
	private Date endLastTime;
	
	private Date startLastConfirmTime;
	private Date endLastConfirmTime;
	
	private int warningCount;
	
	
	private int maxPageSize;
	private int startPage;
	
	/**
	 * �״η���ʱ��Ŀ�ʼ
	 * @param startFirstTime
	 */
	public void setStartFirstTime(Date startFirstTime) {
		this.startFirstTime = startFirstTime;
	}
	public Date getStartFirstTime() {
		return startFirstTime;
	}
	
	/**
	 * �״η���ʱ��Ľ���
	 * @param endFirstTime
	 */
	public void setEndFirstTime(Date endFirstTime) {
		this.endFirstTime = endFirstTime;
	}
	public Date getEndFirstTime() {
		return endFirstTime;
	}
	
	/**
	 * �������ʱ��Ŀ�ʼ
	 * @param startLastTime
	 */
	public void setStartLastTime(Date startLastTime) {
		this.startLastTime = startLastTime;
	}
	public Date getStartLastTime() {
		return startLastTime;
	}
	
	/**
	 * �������ʱ��Ľ���
	 * @param endLastTime
	 */
	public void setEndLastTime(Date endLastTime) {
		this.endLastTime = endLastTime;
	}
	public Date getEndLastTime() {
		return endLastTime;
	}
	
	/**
	 * ���ȷ��ʱ��Ŀ�ʼ
	 * @param startLastConfirmTime
	 */
	public void setStartLastConfirmTime(Date startLastConfirmTime) {
		this.startLastConfirmTime = startLastConfirmTime;
	}
	public Date getStartLastConfirmTime() {
		return startLastConfirmTime;
	}
	
	/**
	 * ���ȷ��ʱ��Ľ���
	 * @param endLastConfirmTime
	 */
	public void setEndLastConfirmTime(Date endLastConfirmTime) {
		this.endLastConfirmTime = endLastConfirmTime;
	}
	public Date getEndLastConfirmTime() {
		return endLastConfirmTime;
	}
	
	/**
	 * �򵥲�ѯʱ�Ĳ�ѯ����,��Ϊ����,����,������,����
	 * @param simpleConfirmTimeStr
	 */
	public void setSimpleConfirmTimeStr(String simpleConfirmTimeStr) {
		this.simpleConfirmTimeStr = simpleConfirmTimeStr;
	}
	public String getSimpleConfirmTimeStr() {
		return simpleConfirmTimeStr;
	}
	
	/**
	 * �澯��Դ
	 * @param warningSouce
	 */
	public void setWarningSouce(String warningSouce) {
		this.warningSouce = warningSouce;
	}
	public String getWarningSouce() {
		return warningSouce;
	}
	
	/**
	 * ÿҳ���������
	 * @return
	 */
	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
	public int getMaxPageSize() {
		return maxPageSize;
	}
	
	/**
	 * �澯�¼�
	 * @param warningEvent
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}
	public int getWarningEvent() {
		return warningEvent;
	}
	
	/**
	 * ��ʼҳ��ֵ
	 * @return
	 */
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	public int getStartPage() {
		return startPage;
	}
	
	/**
	 * Ƶ��
	 * @return
	 */
	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}
	public int getWarningCount() {
		return warningCount;
	}
}
