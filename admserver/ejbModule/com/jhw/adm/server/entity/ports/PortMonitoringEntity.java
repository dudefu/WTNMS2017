package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * 端口监控数据
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "PortMonitoring")
public class PortMonitoringEntity  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private long inbyte;
	private long inunicastbyte;
	private long inununicastbyte;
	private long inabandonbyte;
	private long inerrbyte;
	private long inunknownbyte;

	private long outbyte;
	private long outunicastbyte;
	private long outununicastbyte;
	private long outabandonbyte;
	private long outerrbyte;
	private long outunknownbyte;

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

	public long getInbyte() {
		return inbyte;
	}

	public void setInbyte(long inbyte) {
		this.inbyte = inbyte;
	}

	public long getInunicastbyte() {
		return inunicastbyte;
	}

	public void setInunicastbyte(long inunicastbyte) {
		this.inunicastbyte = inunicastbyte;
	}

	public long getInununicastbyte() {
		return inununicastbyte;
	}

	public void setInununicastbyte(long inununicastbyte) {
		this.inununicastbyte = inununicastbyte;
	}

	public long getInabandonbyte() {
		return inabandonbyte;
	}

	public void setInabandonbyte(long inabandonbyte) {
		this.inabandonbyte = inabandonbyte;
	}

	public long getInerrbyte() {
		return inerrbyte;
	}

	public void setInerrbyte(long inerrbyte) {
		this.inerrbyte = inerrbyte;
	}

	public long getInunknownbyte() {
		return inunknownbyte;
	}

	public void setInunknownbyte(long inunknownbyte) {
		this.inunknownbyte = inunknownbyte;
	}

	public long getOutbyte() {
		return outbyte;
	}

	public void setOutbyte(long outbyte) {
		this.outbyte = outbyte;
	}

	public long getOutunicastbyte() {
		return outunicastbyte;
	}

	public void setOutunicastbyte(long outunicastbyte) {
		this.outunicastbyte = outunicastbyte;
	}

	public long getOutununicastbyte() {
		return outununicastbyte;
	}

	public void setOutununicastbyte(long outununicastbyte) {
		this.outununicastbyte = outununicastbyte;
	}

	public long getOutabandonbyte() {
		return outabandonbyte;
	}

	public void setOutabandonbyte(long outabandonbyte) {
		this.outabandonbyte = outabandonbyte;
	}

	public long getOuterrbyte() {
		return outerrbyte;
	}

	public void setOuterrbyte(long outerrbyte) {
		this.outerrbyte = outerrbyte;
	}

	public long getOutunknownbyte() {
		return outunknownbyte;
	}

	public void setOutunknownbyte(long outunknownbyte) {
		this.outunknownbyte = outunknownbyte;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
