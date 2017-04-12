package com.jhw.adm.client.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.carto.IElement;
import com.esri.arcgis.carto.IGraphicsContainer;
import com.esri.arcgis.carto.MarkerElement;
import com.esri.arcgis.carto.PngPictureElement;
import com.esri.arcgis.carto.RectangleElement;
import com.esri.arcgis.carto.SelectionEnvironment;
import com.esri.arcgis.display.IColor;
import com.esri.arcgis.display.IDisplayTransformation;
import com.esri.arcgis.display.RgbColor;
import com.esri.arcgis.display.SimpleFillSymbol;
import com.esri.arcgis.display.SimpleMarkerSymbol;
import com.esri.arcgis.display.esriSimpleFillStyle;
import com.esri.arcgis.display.esriSimpleMarkerStyle;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriSRGeoCSType;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.map.action.AlarmModel2Listener;
import com.jhw.adm.client.map.action.AlarmModelListener;
import com.jhw.adm.client.map.action.MapControlListener;
import com.jhw.adm.client.map.action.MapZoomInAction;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.warning.SimpleWarning;

@Component(GISView.ID)
@Scope(Scopes.DESKTOP)
public class GISView extends ViewPart {
	public static final String ID = "gisView";
	private static final long serialVersionUID = 1L;

	private JToolBar toolbar;
	private MapStatuBar statubar;

	private MapBean mapBean;

	IGraphicsContainer graphicsContainer = null;
	IDisplayTransformation displayTransformation = null;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = AlarmModel.ID)
	private AlarmModel alarmModel;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	IElement holowElement = null;

	private IElement selectedElement = null;
	private NodeEntity selectedNode = null;

	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;

	public Map<AddressEntity, List<String>> addressIpsMap = new HashMap<AddressEntity, List<String>>();
	public Map<String, AddressEntity> keyAddressMap = new HashMap<String, AddressEntity>();

	@PostConstruct
	protected void initialize() {
		setSize(800, 600);
		setTitle("地理拓扑图");
		setLayout(new BorderLayout());
		String mapFile = clientModel.getClientConfig().getMapFileName();
		File file = new File(mapFile);
		boolean issettup = testArcGis();
		if (issettup) {
			if (file.exists()) {
				initMapBean(mapFile);
			}
			Object obj = equipmentModel.getLastSelected();
			if (obj != null && obj instanceof NodeEntity) {
				selectedNode = (NodeEntity) obj;
			} else {
				selectedNode = null;
			}
		} else {
			JOptionPane.showMessageDialog(this, "当前系统没有安装ARCENGINE RUNTIME环境","警告",JOptionPane.WARNING_MESSAGE); 
		}

	}

	private boolean testArcGis() {
		boolean issettup = false;
		Process p;
		try {
			p = Runtime.getRuntime().exec("cmd.exe /c tasklist");
			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));

			String arcgis = "ARCGIS.EXE";
			String line = "";
			while ((line = input.readLine()) != null) {
				if (line.length() > 1) {
					CharSequence cs = arcgis.substring(0);
					if (line.contains(cs)) {
						System.out.println(line);
						issettup = true;
						break;
					}
				}
			}
			input.close();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return issettup;
	}

	/**
	 * 初始化地图
	 * 
	 * @param mapfilepath
	 */
	private void initMapBean(String mapfilepath) {
		try {
			EngineInitializer.initializeVisualBeans();
			new AoInitialize()
					.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
			SpatialReferenceEnvironment srf = new SpatialReferenceEnvironment();
			ISpatialReference spRef = srf
					.createSpatialReference(esriSRGeoCSType.esriSRGeoCS_Beijing1954);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		mapBean = new MapBean();
		try {
			mapBean.setShowScrollbars(false);
			mapBean.loadMxFile(mapfilepath, null, null);

		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MapToolBar mtb = MapToolBar.getInstance();
		mtb.setMapBean(mapBean);
		try {
			toolbar = mtb.initToolBar();
			mapBean.setAutoMouseWheel(true);
			IColor pcolor = new RgbColor();
			pcolor.setRGB(Color.blue.getRGB());
			pcolor.setTransparency((byte) 255);
			SelectionEnvironment pSelectionenvironment = new SelectionEnvironment();
			pSelectionenvironment.setDefaultColorByRef(pcolor);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		SimpleFillSymbol simpleFillSymbolHollow;
		try {
			simpleFillSymbolHollow = new SimpleFillSymbol();
			simpleFillSymbolHollow.setStyle(esriSimpleFillStyle.esriSFSHollow);
			RectangleElement rectangleElementHollow = new RectangleElement();
			RgbColor color = new RgbColor();
			color.setRed(0xFF);
			simpleFillSymbolHollow.setColor(color);
			rectangleElementHollow.setSymbol(simpleFillSymbolHollow);
			this.holowElement = rectangleElementHollow;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		statubar = new MapStatuBar();
		this.add(mapBean, BorderLayout.CENTER);
		this.add(toolbar, BorderLayout.NORTH);
		this.add(statubar, BorderLayout.SOUTH);
		initEvent();
	}

	/**
	 * 初始化监听事件
	 */
	private void initEvent() {
		 AlarmModelListener alarmListener = new AlarmModelListener(this);
		 AlarmModel2Listener alarmListener2 = new AlarmModel2Listener(this);
		 alarmModel.addPropertyChangeListener(alarmModel.ALARM_ARRIVAL,
		 alarmListener);
		 equipmentModel.addPropertyChangeListener(
		 EquipmentModel.EQUIPMENT_UPDATED, alarmListener2);
		MapControlListener listener = new MapControlListener(this);
		MapZoomInAction zoomin = new MapZoomInAction(mapBean);
		try {
			mapBean.addIMapControlEvents2Listener(listener);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object getLastSelected() {
		return equipmentModel.getLastSelected();
	}

	public void emptyLastSelected() {
		equipmentModel.clearSelection();
	}

	// =========================================================================================================
	public void setWarning(SimpleWarning sw) throws IOException {
		displayTransformation = mapBean.getActiveView().getScreenDisplay()
				.getDisplayTransformation();
		graphicsContainer = mapBean.getActiveView().getGraphicsContainer();
		graphicsContainer.reset();
		IElement element = graphicsContainer.next();
		while (element != null) {
			if (element instanceof PngPictureElement) {
				PngPictureElement pElement = (PngPictureElement) (element);
				String name = pElement.getName();
				if (name != null) {
					String ipvalue = sw.getIpValue();
					if (name.equals(ipvalue)) {
						IEnvelope env = pElement.getGeometry().getEnvelope();
						int[] xm = new int[1];
						int[] ym = new int[1];

						IPoint tp = env.getEnvelope().getUpperRight();
						displayTransformation.fromMapPoint(tp, xm, ym);
						Point point = new Point();
						point.setX(xm[0]);
						point.setY(ym[0]);
						SimpleMarkerSymbol simpleMarkerSymbol1 = new SimpleMarkerSymbol();
						simpleMarkerSymbol1.setSize(32);
						simpleMarkerSymbol1
								.setStyle(esriSimpleMarkerStyle.esriSMSCircle);
						RgbColor color1 = new RgbColor();
						color1.setRGB(0xFF00);
						simpleMarkerSymbol1.setColor(color1);
						MarkerElement markerElement1 = new MarkerElement();
						markerElement1.setSymbol(simpleMarkerSymbol1);
						markerElement1.setGeometry(point);
						graphicsContainer.addElement(markerElement1, 0);

						break;
					}
				}
			}
			element = graphicsContainer.next();
		}

		// mapBean.getActiveView().refresh();
		// mapBean.getActiveView().partialRefresh(
		// esriViewDrawPhase.esriViewGraphics, null, null);
	}

	// =========================================================================================================
	public EquipmentModel getEquipmentModel() {
		return equipmentModel;
	}

	public AlarmModel getAlarmModel() {
		return alarmModel;
	}

	public MapBean getMapBean() {
		return mapBean;
	}

	public JToolBar getToolbar() {
		return toolbar;
	}

	public MapStatuBar getStatubar() {
		return statubar;
	}

	public NodeEntity getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(NodeEntity selectedNode) {
		this.selectedNode = selectedNode;
	}

	public IElement getSelectedElement() {

		return selectedElement;
	}

	public void setSelectedElement(IElement selectedElement) {
		this.selectedElement = selectedElement;
	}

	public IElement getHolowElement() {
		return holowElement;
	}

	public void setHolowElement(IElement holowElement) {
		this.holowElement = holowElement;
	}

	public RemoteServer getRemoteServer() {
		return remoteServer;
	}

	public void setRemoteServer(RemoteServer remoteServer) {
		this.remoteServer = remoteServer;
	}

	public Map<AddressEntity, List<String>> getAddressIpsMap() {
		return addressIpsMap;
	}

	public void setAddressIpsMap(Map<AddressEntity, List<String>> addressIpsMap) {
		this.addressIpsMap = addressIpsMap;
	}

	public Map<String, AddressEntity> getKeyAddressMap() {
		return keyAddressMap;
	}

	public void setKeyAddressMap(Map<String, AddressEntity> keyAddressMap) {
		this.keyAddressMap = keyAddressMap;
	}

}
