package com.jhw.adm.client.map;

import java.awt.MenuItem;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JToolBar;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.beans.toolbar.ToolItem;
import com.esri.arcgis.beans.toolbar.ToolbarBean;
import com.esri.arcgis.controls.ControlsClearSelectionCommand;
import com.esri.arcgis.controls.ControlsMapFullExtentCommand;
import com.esri.arcgis.controls.ControlsMapMeasureTool;
import com.esri.arcgis.controls.ControlsMapPanTool;
import com.esri.arcgis.controls.ControlsMapZoomInTool;
import com.esri.arcgis.controls.ControlsMapZoomOutTool;
import com.esri.arcgis.controls.ControlsMapZoomToLastExtentBackCommand;
import com.esri.arcgis.controls.ControlsMapZoomToLastExtentForwardCommand;
import com.esri.arcgis.systemUI.esriCommandStyles;

public class MapToolBar {
	private Map<ToolItem, Action> actions = new HashMap<ToolItem, Action>();
	private Map<MenuItem, Action> menuitems = new HashMap<MenuItem, Action>();

	private MapBean mapBean;
	private JToolBar toolBar;
	private ToolbarBean aetoolbar;
	private static MapToolBar mapToolBar;

	public static MapToolBar getInstance() {
		if (mapToolBar == null) {
			mapToolBar = new MapToolBar();
		}
		return mapToolBar;
	}

	public MapBean getMapBean() {
		return mapBean;
	}

	public void setMapBean(MapBean mapBean) {
		this.mapBean = mapBean;
	}

	public JToolBar initToolBar() throws UnknownHostException, IOException {
		toolBar = new JToolBar();
		aetoolbar = new com.esri.arcgis.beans.toolbar.ToolbarBean();

		ControlsMapZoomInTool cmZoomInTool = new ControlsMapZoomInTool();
		ControlsMapZoomOutTool cmZoomOutTool = new ControlsMapZoomOutTool();
		ControlsMapPanTool cmPanTool = new ControlsMapPanTool();

		aetoolbar.addItem(cmZoomInTool, 0, -1, true, 0,
				esriCommandStyles.esriCommandStyleIconOnly); // ZoomIn
		aetoolbar.addItem(cmZoomOutTool, 0, -1, false, 0,
				esriCommandStyles.esriCommandStyleIconOnly); // ZoomOut
		aetoolbar.addItem(cmPanTool, 0, -1, false, 0,
				esriCommandStyles.esriCommandStyleIconOnly); // Pan
		aetoolbar.addItem(new ControlsMapFullExtentCommand(), 0, -1, false, 10,
				esriCommandStyles.esriCommandStyleIconOnly); // FullExtent
		aetoolbar.addItem(new ControlsMapZoomToLastExtentBackCommand(), 0, -1,
				false, 20, esriCommandStyles.esriCommandStyleIconOnly);
		aetoolbar.addItem(new ControlsMapZoomToLastExtentForwardCommand(), 0,
				-1, false, 10, esriCommandStyles.esriCommandStyleIconOnly);
		aetoolbar.addItem(new ControlsMapMeasureTool(), 0, -1, true, 20,
				esriCommandStyles.esriCommandStyleIconOnly);

		aetoolbar.addItem(new ControlsClearSelectionCommand(), 0, -1, true, 20,
				esriCommandStyles.esriCommandStyleIconOnly);
		aetoolbar.setBuddyControl(mapBean);
		toolBar.add(aetoolbar);
		return toolBar;
	}

}
