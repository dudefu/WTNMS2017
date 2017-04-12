/**
 * 
 */
package com.jhw.adm.server.entity.warning;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**性能监控报表
 * @author 左军勇
 * @时间 2010-8-4
 */
@Entity
public class PerformanceSheet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date createDate;
	private String device;//设备
	private int portNo;
	private String parameter;//参数
	private int numValue;//数值
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public int getNumValue() {
		return numValue;
	}
	public void setNumValue(int numValue) {
		this.numValue = numValue;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
    
}
