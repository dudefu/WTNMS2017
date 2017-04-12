package com.jhw.adm.server.entity.util;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警历史bean,
 * 用于客户端查询告警历史时传给服务端所填入的筛选的条件
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
	 * 首次发生时间的开始
	 * @param startFirstTime
	 */
	public void setStartFirstTime(Date startFirstTime) {
		this.startFirstTime = startFirstTime;
	}
	public Date getStartFirstTime() {
		return startFirstTime;
	}
	
	/**
	 * 首次发生时间的结束
	 * @param endFirstTime
	 */
	public void setEndFirstTime(Date endFirstTime) {
		this.endFirstTime = endFirstTime;
	}
	public Date getEndFirstTime() {
		return endFirstTime;
	}
	
	/**
	 * 最近发生时间的开始
	 * @param startLastTime
	 */
	public void setStartLastTime(Date startLastTime) {
		this.startLastTime = startLastTime;
	}
	public Date getStartLastTime() {
		return startLastTime;
	}
	
	/**
	 * 最近发生时间的结束
	 * @param endLastTime
	 */
	public void setEndLastTime(Date endLastTime) {
		this.endLastTime = endLastTime;
	}
	public Date getEndLastTime() {
		return endLastTime;
	}
	
	/**
	 * 最后确认时间的开始
	 * @param startLastConfirmTime
	 */
	public void setStartLastConfirmTime(Date startLastConfirmTime) {
		this.startLastConfirmTime = startLastConfirmTime;
	}
	public Date getStartLastConfirmTime() {
		return startLastConfirmTime;
	}
	
	/**
	 * 最后确认时间的结束
	 * @param endLastConfirmTime
	 */
	public void setEndLastConfirmTime(Date endLastConfirmTime) {
		this.endLastConfirmTime = endLastConfirmTime;
	}
	public Date getEndLastConfirmTime() {
		return endLastConfirmTime;
	}
	
	/**
	 * 简单查询时的查询条件,分为本周,本月,本季度,本年
	 * @param simpleConfirmTimeStr
	 */
	public void setSimpleConfirmTimeStr(String simpleConfirmTimeStr) {
		this.simpleConfirmTimeStr = simpleConfirmTimeStr;
	}
	public String getSimpleConfirmTimeStr() {
		return simpleConfirmTimeStr;
	}
	
	/**
	 * 告警来源
	 * @param warningSouce
	 */
	public void setWarningSouce(String warningSouce) {
		this.warningSouce = warningSouce;
	}
	public String getWarningSouce() {
		return warningSouce;
	}
	
	/**
	 * 每页的最大行数
	 * @return
	 */
	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
	public int getMaxPageSize() {
		return maxPageSize;
	}
	
	/**
	 * 告警事件
	 * @param warningEvent
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}
	public int getWarningEvent() {
		return warningEvent;
	}
	
	/**
	 * 开始页的值
	 * @return
	 */
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	public int getStartPage() {
		return startPage;
	}
	
	/**
	 * 频次
	 * @return
	 */
	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}
	public int getWarningCount() {
		return warningCount;
	}
}
