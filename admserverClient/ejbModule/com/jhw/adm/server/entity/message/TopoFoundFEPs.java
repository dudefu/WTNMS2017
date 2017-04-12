package com.jhw.adm.server.entity.message;

import java.io.Serializable;
import java.util.List;

public class TopoFoundFEPs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long refreshDiagramId;
	private List fepCodes;
	String message;

	public List getFepCodes() {
		return fepCodes;
	}

	public void setFepCodes(List fepCodes) {
		this.fepCodes = fepCodes;
	}

	public Long getRefreshDiagramId() {
		return refreshDiagramId;
	}

	public void setRefreshDiagramId(Long refreshDiagramId) {
		this.refreshDiagramId = refreshDiagramId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
