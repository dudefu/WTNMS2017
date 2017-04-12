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
 * OLT DBA配置
 * @author Snow
 *
 */
@Entity
@Table
public class OLTDBA implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Long id;
    private int dbaMoble;//dba模式
    private int dbaAlgorithm;//dba算法
    private int dbaCycleTime;//dba周期时间
    private int dbafrequency;//dba发现频率
    private int dbaFindTime;//dba发现时间
    private OLTEntity oltEntity;
    private boolean syschorized = true;
	private String descs;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getDbaMoble() {
		return dbaMoble;
	}
	public void setDbaMoble(int dbaMoble) {
		this.dbaMoble = dbaMoble;
	}
	public int getDbaAlgorithm() {
		return dbaAlgorithm;
	}
	public void setDbaAlgorithm(int dbaAlgorithm) {
		this.dbaAlgorithm = dbaAlgorithm;
	}
	public int getDbaCycleTime() {
		return dbaCycleTime;
	}
	public void setDbaCycleTime(int dbaCycleTime) {
		this.dbaCycleTime = dbaCycleTime;
	}
	public int getDbafrequency() {
		return dbafrequency;
	}
	public void setDbafrequency(int dbafrequency) {
		this.dbafrequency = dbafrequency;
	}
	public int getDbaFindTime() {
		return dbaFindTime;
	}
	public void setDbaFindTime(int dbaFindTime) {
		this.dbaFindTime = dbaFindTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@ManyToOne
	@JoinColumn(name="oltID")
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
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
    
    
}
