package com.jhw.adm.client.map.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.controls.ControlsMapZoomInTool;

public class MapZoomInAction extends AbstractAction {
	MapBean bean;

	public MapZoomInAction(MapBean bean) {
		super();
		this.bean = bean;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ControlsMapZoomInTool tool = new ControlsMapZoomInTool();
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
