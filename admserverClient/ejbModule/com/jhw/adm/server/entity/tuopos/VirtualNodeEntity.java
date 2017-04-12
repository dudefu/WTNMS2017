package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "virtualNode")
@DiscriminatorValue(value = "VI")
public class VirtualNodeEntity extends NodeEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipValue;
	private String imagePath;
	private int type;
	private String guid;
	private boolean localImage;
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public boolean isLocalImage() {
		return localImage;
	}
	public void setLocalImage(boolean localImage) {
		this.localImage = localImage;
	}
	
}
