package com.jhw.adm.client.map.tools;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.carto.IActiveView;
import com.esri.arcgis.carto.IElement;
import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.IGraphicsContainer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.PngPictureElement;
import com.esri.arcgis.display.IDisplayTransformation;
import com.esri.arcgis.display.IFillSymbol;
import com.esri.arcgis.display.ILineSymbol;
import com.esri.arcgis.display.IRgbColor;
import com.esri.arcgis.display.IScreenDisplay;
import com.esri.arcgis.display.ISimpleMarkerSymbol;
import com.esri.arcgis.display.ISymbol;
import com.esri.arcgis.display.ISymbolProxy;
import com.esri.arcgis.display.RgbColor;
import com.esri.arcgis.display.SimpleFillSymbol;
import com.esri.arcgis.display.SimpleLineSymbol;
import com.esri.arcgis.display.SimpleMarkerSymbol;
import com.esri.arcgis.display.esriRasterOpCode;
import com.esri.arcgis.display.esriSimpleMarkerStyle;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geometry.Envelope;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.interop.AutomationException;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;

public class MapTools {
	private static MapTools mapTools;

	public static MapTools getInstance() {
		if (mapTools == null) {
			mapTools = new MapTools();
		}
		return mapTools;
	}

	public void centerElement(MapBean mapBean, String nodename) {
		PngPictureElement pngElement = null;
		try {
			IGraphicsContainer graphicsContainer = mapBean.getActiveView()
					.getGraphicsContainer();
			graphicsContainer.reset();
			IElement element = graphicsContainer.next();
			while (element != null) {
				if (element instanceof PngPictureElement) {
					pngElement = (PngPictureElement) element;
					String name = pngElement.getName();
					if (nodename.equals(name)) {
						break;
					}
				}
				element = graphicsContainer.next();
			}
			IEnvelope env = (IEnvelope) pngElement.getGeometry();
			IPoint point = env.getUpperLeft();
			mapBean.getMapControl().centerAt(point);
			mapBean.getMapControl().flashShape(point, 2, 200, env);
		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void zoomToPosition(MapBean mapBean, IGeometry geometry)
			throws AutomationException, IOException {
		if (geometry.getGeometryType() == esriGeometryType.esriGeometryPoint) {
			IPoint point = (IPoint) geometry;
			mapBean.centerAt(point);
			mapBean.getMapControl().setMapScale(2500);
		} else if (geometry.getGeometryType() == esriGeometryType.esriGeometryPolyline) {
			IEnvelope envelope = geometry.getEnvelope();
			envelope.expand(
					convertPixesToMapUnits(mapBean.getActiveView(), 20),
					convertPixesToMapUnits(mapBean.getActiveView(), 20), false);
			mapBean.getActiveView().setExtent(envelope);
		} else if (geometry.getGeometryType() == esriGeometryType.esriGeometryPolygon) {
			IEnvelope envelope = geometry.getEnvelope();
			envelope.expand(
					convertPixesToMapUnits(mapBean.getActiveView(), 20),
					convertPixesToMapUnits(mapBean.getActiveView(), 20), false);
			mapBean.getActiveView().setExtent(envelope);
		}
		mapBean.getActiveView().refresh();

	}

	private double convertPixesToMapUnits(IActiveView activeView,
			double pixelUnits) throws AutomationException, IOException {

		IDisplayTransformation dispTrans = null;
		IScreenDisplay screenDisplay = null;

		double dblrealWorldDisplayExtent = 0.0;
		int ipixelExtent = 0;
		double dblsizeOfOnePixel = 0.0;

		screenDisplay = activeView.getScreenDisplay();
		dispTrans = screenDisplay.getDisplayTransformation();
		ipixelExtent = dispTrans.getDeviceFrame().right
				- dispTrans.getDeviceFrame().left;
		IEnvelope env = dispTrans.getVisibleBounds();// Error

		dblrealWorldDisplayExtent = env.getWidth();
		dblsizeOfOnePixel = dblrealWorldDisplayExtent / ipixelExtent;

		return pixelUnits * dblsizeOfOnePixel;

	}

	/**
	 * 被选中是高亮显示
	 * 
	 * @param featureLayer
	 * @param featureCursor
	 * @throws AutomationException
	 * @throws IOException
	 */
	public void DisplayResultFeature(IFeatureLayer featureLayer,
			IFeatureCursor featureCursor) throws AutomationException,
			IOException {
		IFeatureSelection featureSelection = (IFeatureSelection) featureLayer;
		featureSelection.clear();
		IFeature feature = featureCursor.nextFeature();
		while ((feature = featureCursor.nextFeature()) != null) {
			featureSelection.add(feature);
			featureSelection.selectionChanged();
		}
	}

	/**
	 * 将WGS84坐标转为BJ54坐标
	 * 
	 * @param L
	 * @param B
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public IPoint wgs84Tobj54(double L, double B) throws UnknownHostException,
			IOException {
		double a = 6378140.0;// ????80,a???????????
		double f = 1.0 / 298.257; // ????80,f?????
		double dk = 3.0 / 180.0 * Math.PI;// ?????????3??????
		int dh = (int) (((L - 1.5 * Math.PI / 180.0) / dk) + 1);
		double L0 = dk * dh;// L0???????????????,??λ????
		double AA, BB, CC, DD, EE, X, e, e1;
		double b1, dl, n1, t, N;

		// ????????????? X
		b1 = (1 - f) * a;
		e = Math.sqrt((a * a - b1 * b1) / a / a);
		AA = 1 + 3.0 / 4.0 * Math.pow(e, 2.0) + 45.0 / 64.0 * Math.pow(e, 4.0)
				+ 175.0 / 256.0 * Math.pow(e, 6.0);
		AA = AA + 11025.0 / 16384.0 * Math.pow(e, 8.0);
		BB = 3.0 / 4.0 * Math.pow(e, 2.0) + 15.0 / 16.0 * Math.pow(e, 4.0)
				+ 525.0 / 512.0 * Math.pow(e, 6.0);
		BB = BB + 2205.0 / 2048.0 * Math.pow(e, 8.0);
		CC = 15.0 / 64.0 * Math.pow(e, 4.0) + 105.0 / 256.0 * Math.pow(e, 6.0)
				+ 2205.0 / 4096.0 * Math.pow(e, 8.0);
		DD = 35.0 / 512.0 * Math.pow(e, 6.0) + 315.0 / 2048.0
				* Math.pow(e, 8.0);
		EE = 315.0 / 16384.0 * Math.pow(e, 8.0);
		X = a
				* (1 - Math.pow(e, 2.0))
				* (AA * B - BB / 2 * Math.sin(2 * B) + CC / 4 * Math.sin(4 * B)
						- DD / 6 * Math.sin(6 * B) + EE / 8 * Math.sin(8 * B));
		dl = L - L0;
		e1 = Math.sqrt((a * a - b1 * b1) / b1 / b1);
		n1 = e1 * Math.cos(B);
		t = Math.tan(B);
		N = a / Math.sqrt(1 - e * e * Math.pow(Math.sin(B), 2.0));
		double PX = X;
		PX = PX + N / 2 * Math.sin(B) * Math.cos(B) * Math.pow(dl, 2.0);
		PX = PX + N / 24 * Math.sin(B) * Math.pow(Math.cos(B), 3.0)
				* (5 - t * t + 9 * n1 * n1 + 4 * Math.pow(n1, 4.0))
				* Math.pow(dl, 4.0);
		PX = PX + N / 720 * Math.sin(B) * Math.pow(Math.cos(B), 5.0)
				* (61 - 58 * Math.pow(t, 2.0) + Math.pow(t, 4.0))
				* Math.pow(dl, 6.0);
		double PY = N * Math.cos(B) * dl;
		PY = PY + N / 6 * Math.pow(Math.cos(B), 3.0) * (1 - t * t + n1 * n1)
				* Math.pow(dl, 3.0);
		PY = PY
				+ N
				/ 120
				* Math.pow(Math.cos(B), 5.0)
				* (5 - 18 * t * t + Math.pow(t, 4.0) + 14 * n1 * n1 - 58 * n1
						* n1 * t * t) * Math.pow(dl, 5.0);
		IPoint pt = new Point();
		pt.setX(PY);
		pt.setY(PX);
		return pt;
	}

	// 已知坐标，创建点
	public IPoint editOperate_CreateNewPoint(IMap map, double x, double y)
			throws UnknownHostException, IOException {

		IPoint newPoint = new Point();

		newPoint.setX(x);

		newPoint.setY(y);

		newPoint.setSpatialReferenceByRef(map.getSpatialReference());

		return newPoint;
	}

	private ISymbol getSymbol(IGeometry geometry) throws UnknownHostException,
			IOException {

		ISymbol symbol = null;

		switch (geometry.getGeometryType()) {

		case esriGeometryType.esriGeometryPoint:

			ISimpleMarkerSymbol simpleMarkerSymbol = null;

			IRgbColor rgbColor = null;

			rgbColor = new RgbColor();
			rgbColor.setRed(0);
			rgbColor.setGreen(255);
			rgbColor.setBlue(0);

			simpleMarkerSymbol = new SimpleMarkerSymbol();
			simpleMarkerSymbol.setOutline(true);
			simpleMarkerSymbol.setOutlineColor(rgbColor);
			simpleMarkerSymbol.setStyle(esriSimpleMarkerStyle.esriSMSCircle);
			simpleMarkerSymbol.setSize(18);

			rgbColor = new RgbColor();
			rgbColor.setRed(0);
			rgbColor.setGreen(255);
			rgbColor.setBlue(0);

			simpleMarkerSymbol.setColor(rgbColor);

			symbol = new ISymbolProxy(simpleMarkerSymbol);
			symbol.setROP2(esriRasterOpCode.esriROPNotXOrPen);

			break;

		case esriGeometryType.esriGeometryPolyline:

			break;

		case esriGeometryType.esriGeometryEnvelope:
			IEnvelope envelope = new Envelope();
			
			break;

		case esriGeometryType.esriGeometryPolygon:

			IFillSymbol pFillSymbol = new SimpleFillSymbol();
			ISymbol pSymbol = new ISymbolProxy(pFillSymbol);
			pSymbol.setROP2(esriRasterOpCode.esriROPXOrPen);

			IRgbColor pRGBColor = new RgbColor();
			pRGBColor.setUseWindowsDithering(false);
			pRGBColor.setRed(0);
			pRGBColor.setGreen(255);
			pRGBColor.setBlue(0);
			pFillSymbol.setColor(pRGBColor);

			ILineSymbol pLineSymbol = new SimpleLineSymbol();
			pRGBColor = new RgbColor();
			pRGBColor.setRed(0);
			pRGBColor.setGreen(255);
			pRGBColor.setBlue(0);
			pLineSymbol.setColor(pRGBColor);
			pLineSymbol.setWidth(0.1);

			pFillSymbol.setOutline(pLineSymbol);

			symbol = (ISymbol) pFillSymbol;

			break;

		}

		return symbol;
	}
	
	private List<SwitchNodeEntity> buildTestData() {
		List datas = new ArrayList();

		SwitchNodeEntity entity1 = new SwitchNodeEntity();
		AddressEntity address1 = new AddressEntity();
		address1.setLongitude("113.88914");
		address1.setLatitude("22.451632");
		entity1.setAddress(address1);
		SwitchBaseInfo bi1 = new SwitchBaseInfo();
		bi1.setDeviceName("IETH802");
		entity1.setBaseInfo(bi1);

		SwitchNodeEntity entity2 = new SwitchNodeEntity();
		AddressEntity address2 = new AddressEntity();
		address2.setLongitude("113.952178");
		address2.setLatitude("22.639818");
		entity2.setAddress(address2);
		SwitchBaseInfo bi2 = new SwitchBaseInfo();
		bi2.setDeviceName("IETH802");
		entity2.setBaseInfo(bi2);

		SwitchNodeEntity entity3 = new SwitchNodeEntity();
		AddressEntity address3 = new AddressEntity();
		address3.setLongitude("113.846588");
		address3.setLatitude("22.789289");
		entity3.setAddress(address3);
		SwitchBaseInfo bi3 = new SwitchBaseInfo();
		bi3.setDeviceName("IETH802");
		entity3.setBaseInfo(bi3);

		SwitchNodeEntity entity4 = new SwitchNodeEntity();
		AddressEntity address4 = new AddressEntity();
		address4.setLongitude("114.098906");
		address4.setLatitude("22.717982");
		entity4.setAddress(address4);
		SwitchBaseInfo bi4 = new SwitchBaseInfo();
		bi4.setDeviceName("IETH802");
		entity4.setBaseInfo(bi4);

		SwitchNodeEntity entity5 = new SwitchNodeEntity();
		AddressEntity address5 = new AddressEntity();
		address5.setLongitude("114.215466");
		address5.setLatitude("22.587709");
		entity5.setAddress(address5);
		SwitchBaseInfo bi5 = new SwitchBaseInfo();
		bi5.setDeviceName("IETH802");
		entity5.setBaseInfo(bi5);

		datas.add(entity1);
		datas.add(entity2);
		datas.add(entity3);
		datas.add(entity4);
		datas.add(entity5);
		return datas;
	}

}
