package com.jhw.adm.client.map.action;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.SwingUtilities;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.controls.IMapControlEvents2Adapter;
import com.esri.arcgis.controls.IMapControlEvents2OnMouseDownEvent;
import com.esri.arcgis.controls.IMapControlEvents2OnMouseMoveEvent;
import com.esri.arcgis.display.IDisplayTransformation;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.interop.AutomationException;
import com.jhw.adm.client.map.MapStatuBar;

public class StatuLineListener extends IMapControlEvents2Adapter {
	private static final long serialVersionUID = 7648289538387942335L;
	private MapStatuBar statusbar;
	private DecimalFormat format;
	private MapBean mapBean;

	public MapBean getMapBean() {
		return mapBean;
	}

	public void setMapBean(MapBean mapBean) {
		this.mapBean = mapBean;
	}

	public StatuLineListener(MapStatuBar statusbar) {
		format = new DecimalFormat();
		format.setMaximumFractionDigits(6);
		this.statusbar = statusbar;
	}

	@Override
	public void onMouseMove(IMapControlEvents2OnMouseMoveEvent event)
			throws IOException, AutomationException {
		if (statusbar != null) {
			final double x = event.getMapX();
			final double y = event.getMapY();
			final String sx = format.format(x);
			final String sy = format.format(y);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					statusbar.setXY(sx, sy);
				}
			});
		}
	}

	@Override
	public void onMouseDown(IMapControlEvents2OnMouseDownEvent event)
			throws IOException, AutomationException {
		double dx = event.getMapX();
		double dy = event.getMapY();
		IDisplayTransformation displayTransformation = null;
		try {
			displayTransformation = mapBean.getActiveView().getScreenDisplay()
					.getDisplayTransformation();
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPoint point = new Point();
		point.setX(dx);
		point.setY(dy);
		int xs[] = new int[1];
		int ys[] = new int[1];

		displayTransformation.fromMapPoint(point, xs, ys);
		int x = xs[0];
		int y = ys[0];
//		System.out.println(x + "    xy============:  " + y);
//		IPoint p1 = displayTransformation.toMapPoint(x, y);
//		double xx = p1.getX();
//		double yy = p1.getY();
//		System.out.println(xx + "    xyxy===========:  " + yy);
		

		int xmin = x - 20;
		int xmax = x + 20;

		int ymin = y - 10;
		int ymax = y + 10;

		IPoint p1 = displayTransformation.toMapPoint(xmin, ymax);
		IPoint p2 = displayTransformation.toMapPoint(xmax, ymax);
		IPoint p3 = displayTransformation.toMapPoint(xmin, ymin);
		IPoint p4 = displayTransformation.toMapPoint(xmax, ymin);
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(6);
		System.out.println(format.format(p1.getX()) + "    p1:  "
				+ format.format(p1.getY()));
		System.out.println(p2.getX() + "    p2:  " + p2.getY());
		System.out.println(p3.getX() + "    p3:  " + p3.getY());
		System.out.println(p4.getX() + "    p4:  " + p4.getY());
	}
}