package com.jhw.adm.client.map.action;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.controls.ControlsMapZoomOutTool;

/**
 * ·Å´óµØÍ¼
 * 
 * @author ÑîÏö
 */
public class MapZoomOutAction extends AbstractAction {
	private MapBean bean;

	public MapZoomOutAction(MapBean bean) {
		super();
		this.bean = bean;
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		try {
			ControlsMapZoomOutTool tool = new ControlsMapZoomOutTool();
			tool.onCreate(bean.getObject());
			tool.onClick();
			bean.setCurrentToolByRef(tool);
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException exx) {
			exx.printStackTrace();
		}
	}
}
