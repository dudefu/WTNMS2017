package com.jhw.adm.server.entity.tuopos;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.jhw.adm.server.entity.nets.FEPEntity;
@Entity
@Table(name = "topofepnode")
@DiscriminatorValue(value = "FP")
public class FEPTopoNodeEntity extends NodeEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FEPEntity fepEntity;
	private String guid;
	private String ipValue;
	private String code;

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	@Transient
	public FEPEntity getFepEntity() {
		return fepEntity;
	}

	public void setFepEntity(FEPEntity fepEntity) {
		this.fepEntity = fepEntity;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
