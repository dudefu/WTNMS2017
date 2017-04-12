package com.jhw.adm.client.map.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.carto.IActiveView;
import com.esri.arcgis.carto.IElement;
import com.esri.arcgis.carto.IGraphicsContainer;
import com.esri.arcgis.carto.IGraphicsContainerSelect;
import com.esri.arcgis.carto.IMarkerElement;
import com.esri.arcgis.carto.LineElement;
import com.esri.arcgis.carto.MarkerElement;
import com.esri.arcgis.carto.PngPictureElement;
import com.esri.arcgis.carto.RectangleElement;
import com.esri.arcgis.carto.TextElement;
import com.esri.arcgis.carto.esriViewDrawPhase;
import com.esri.arcgis.controls.IMapControlEvents2Adapter;
import com.esri.arcgis.controls.IMapControlEvents2OnAfterDrawEvent;
import com.esri.arcgis.controls.IMapControlEvents2OnDoubleClickEvent;
import com.esri.arcgis.controls.IMapControlEvents2OnMouseDownEvent;
import com.esri.arcgis.controls.IMapControlEvents2OnMouseMoveEvent;
import com.esri.arcgis.display.CartographicLineSymbol;
import com.esri.arcgis.display.IDisplayTransformation;
import com.esri.arcgis.display.ILineProperties;
import com.esri.arcgis.display.ILineSymbol;
import com.esri.arcgis.display.ITemplate;
import com.esri.arcgis.display.ITextSymbol;
import com.esri.arcgis.display.PictureMarkerSymbol;
import com.esri.arcgis.display.RgbColor;
import com.esri.arcgis.display.SimpleLineSymbol;
import com.esri.arcgis.display.Template;
import com.esri.arcgis.display.TextSymbol;
import com.esri.arcgis.display.esriIPictureType;
import com.esri.arcgis.display.esriLineCapStyle;
import com.esri.arcgis.display.esriLineJoinStyle;
import com.esri.arcgis.geometry.BezierCurve;
import com.esri.arcgis.geometry.Envelope;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.support.ms.stdole.StdFont;
import com.jhw.adm.client.actions.ShowViewAction;
import com.jhw.adm.client.map.GISView;
import com.jhw.adm.client.map.MapStatuBar;
import com.jhw.adm.client.views.ConfigureGroupView;
import com.jhw.adm.client.views.carrier.CarrierInfoView;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.epon.EponSplitter;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;

public class MapControlListener extends IMapControlEvents2Adapter {
	private MapBean mapBean = null;
	private IDisplayTransformation displayTransformation = null;
	private MapStatuBar statusbar;
	IGraphicsContainer graphicsContainer = null;
	private final DecimalFormat format;
	String imagePath = "res/network/";
	private final GISView view;
	private IActiveView activeView;

	public MapControlListener(GISView view) {
		this.view = view;
		this.mapBean = view.getMapBean();
		format = new DecimalFormat();
		format.setMaximumFractionDigits(6);
	}

	@Override
	public void onMouseDown(IMapControlEvents2OnMouseDownEvent event)throws IOException {
		double xc = event.getX();
		double yc = event.getY();
		IElement selected = view.getSelectedElement();
		selectElementByPoint(xc, yc);
		IElement newselected = view.getSelectedElement();
		if (selected != newselected) {
			activeView.partialRefresh(esriViewDrawPhase.esriViewGraphics,
					newselected, activeView.getExtent());
		}
		 if (view.getSelectedElement() != null) {
		 activeView.refresh();
		 }

	}

	@Override
	public void onMouseMove(IMapControlEvents2OnMouseMoveEvent event)
			throws IOException, AutomationException {
		if (view.getStatubar() != null) {
			final double x = event.getMapX();
			final double y = event.getMapY();
			final String sx = format.format(x);
			final String sy = format.format(y);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					view.getStatubar().setXY(sx, sy);
				}
			});
		}
	}

	@Override
	public void onDoubleClick(IMapControlEvents2OnDoubleClickEvent event)
			throws IOException, AutomationException {
		int xc = event.getX();
		int yc = event.getY();
		selectElementByPoint(xc, yc);
		IElement element = view.getSelectedElement();
		if (element != null) {
			activeView.refresh();
		}
		if (element != null && element instanceof PngPictureElement) {
			activeView.refresh();
			PngPictureElement pe = (PngPictureElement) element;
			Object obj = pe.getCustomProperty();
			if (obj != null) {
				view.getEquipmentModel().changeSelected(obj);
				ShowViewAction emulationAction = new ShowViewAction();
				if (obj instanceof CarrierTopNodeEntity) {
					emulationAction.setViewId(CarrierInfoView.ID);
				}
				emulationAction.setGroupId(ConfigureGroupView.ID);
				String DefaulActionCommand = "DoubleClick";
				ActionEvent actionEvent = new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, DefaulActionCommand);
				emulationAction.actionPerformed(actionEvent);
			}
		}
	}

	@Override
	public void onAfterDraw(IMapControlEvents2OnAfterDrawEvent theEvent) {
		try {
			displayTransformation = mapBean.getActiveView().getScreenDisplay()
					.getDisplayTransformation();
			activeView = mapBean.getActiveView();
			graphicsContainer = activeView.getGraphicsContainer();
			graphicsContainer.deleteAllElements();
		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			double scale = displayTransformation.getScaleRatio();
			System.out.println("=====================================:  "
					+ mapBean.getMapScale());
		} catch (AutomationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		buildImage();
		try {
			selectedElement(view.getSelectedElement());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// NodeEntity node = view.getSelectedNode();
		// if(node!=null){
		// centerNode(node);
		// view.setSelectedNode(null);
		// }
	}

	public void buildImage() {
		TopDiagramEntity diagram = view.getEquipmentModel().getDiagram();
		Set<NodeEntity> nodes = diagram.getNodes();
		Set<LinkEntity> lines = diagram.getLines();
		List<IElement> elements = createSwitcher3(nodes);
		for (IElement element : elements) {
			drawNode(element);
		}
		for (NodeEntity node : nodes) {
			try {
				IElement element = createNode(node);
				if (element != null) {
					drawNode(element);
				}
			} catch (AutomationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (LinkEntity link : lines) {
			link.setLineType(Constants.LINE_FIBERO);
			IElement element = createLine(link);
			if (element != null) {
				drawNode(element);
				try {
					Polyline line = (Polyline) element.getGeometry();
					IElement statuElement = getLineStatus(line, link
							.getStatus());
					if (statuElement != null) {
						drawNode(statuElement);
					}
				} catch (AutomationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private List<IElement> createSwitcher3(Set<NodeEntity> nodes) {
		List<IElement> elements = new ArrayList<IElement>();
		HashMap<AddressEntity, List<String>> datas = new HashMap<AddressEntity, List<String>>();
		for (NodeEntity node : nodes) {
			if (node instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 node3 = (SwitchTopoNodeLevel3) node;
				String ipvalue = node3.getIpValue();
				if (ipvalue != null) {
					AddressEntity address = view.getRemoteServer().getService()
							.queryAddressBySwitcher3(ipvalue);
					if (address == null) {
						continue;
					}
					if (address.getLongitude() == null
							|| address.getLongitude().equals("")) {
						continue;
					}
					if (address.getLatitude() == null
							|| address.getLatitude().equals("")) {
						continue;
					}
					if (datas.size() == 0) {
						List<String> ips = new ArrayList<String>();
						ips.add(ipvalue);
						datas.put(address, ips);
					} else {
						Set<AddressEntity> addresses = datas.keySet();
						boolean same = true;
						for (AddressEntity addr : addresses) {
							String x = addr.getLongitude();
							String y = addr.getLatitude();
							if ((x.equals(address.getLongitude()))
									&& (y.equals(address.getLatitude()))) {
								datas.get(addr).add(ipvalue);
								same = true;
								break;
							} else {
								same = false;
							}
						}
						if (!same) {
							List<String> ips = new ArrayList<String>();
							ips.add(ipvalue);
							datas.put(address, ips);
						}
					}

				}
			}
		}

		Set<AddressEntity> keys = datas.keySet();
		for (AddressEntity key : keys) {
			List<String> ips = datas.get(key);
			String longitude = key.getLongitude();
			String latitude = key.getLatitude();
			IElement element = null;
			try {
				element = createSwitcher3Node(longitude, latitude);
			} catch (AutomationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (element != null) {
				elements.add(element);
				view.getAddressIpsMap().put(key, ips);
			}
		}
		return elements;
	}

	private void drawNode(IElement element) {
		try {
			graphicsContainer.addElement(element, 0);
		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addTextElement(IPoint point, String txt) throws IOException,
			AutomationException {
		ITextSymbol sym = new TextSymbol();
		sym.setText(txt);
		StdFont font = new StdFont();
		font.setName("Helvetica");
		sym.setFont(font);
		if (graphicsContainer != null) {
			if (point != null) {
				if (!point.isEmpty()) {
					if (sym != null) {
						TextElement textElement = new TextElement();
						int[] xs = new int[1];
						int[] ys = new int[1];
						displayTransformation.fromMapPoint(point, xs, ys);
						int x = xs[0];
						int y = ys[0];
						x = x + 15;
						y = y + 12;
						IPoint p = displayTransformation.toMapPoint(x, y);
						textElement.setGeometry(p);
						textElement.setSymbol(sym);
						textElement.setText(sym.getText());
						graphicsContainer.addElement(textElement, 0);
					}
				}
			}
		}
	}

	private IElement createLine(LinkEntity link) {
		LineElement lineElement = null;
		int lineType = link.getLineType();
		int status = link.getStatus();
		ILineSymbol symbol = null;

		NodeEntity node1 = link.getNode1();
		NodeEntity node2 = link.getNode2();
		IPoint p1 = null;
		IPoint p2 = null;
		try {
			if (node1 instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 node3 = (SwitchTopoNodeLevel3) node1;
				Set<AddressEntity> addresses = view.getAddressIpsMap().keySet();
				for (AddressEntity address : addresses) {
					List<String> ips = view.getAddressIpsMap().get(address);
					for (String ip : ips) {
						String nip = node3.getIpValue();
						if (ip.equals(nip)) {
							p1 = getPointByAddress(address);
						}
					}
				}
			} else {
				AddressEntity address = null;
				if (node1 instanceof SwitchTopoNodeEntity) {
					SwitchTopoNodeEntity switchertopo = (SwitchTopoNodeEntity) node1;
					address = view.getKeyAddressMap().get(
							switchertopo.getGuid());
				} else if (node1 instanceof CarrierTopNodeEntity) {
					CarrierTopNodeEntity carriertopo = (CarrierTopNodeEntity) node1;
					address = view.getKeyAddressMap()
							.get(carriertopo.getGuid());
				} else if (node1 instanceof GPRSTopoNodeEntity) {
					GPRSTopoNodeEntity gprsnode = (GPRSTopoNodeEntity) node1;
					address = view.getKeyAddressMap().get(gprsnode.getGuid());
				} else if (node1 instanceof EponTopoEntity) {
					EponTopoEntity oltnode = (EponTopoEntity) node1;
					address = view.getKeyAddressMap().get(oltnode.getGuid());
				} else if (node1 instanceof Epon_S_TopNodeEntity) {
					Epon_S_TopNodeEntity eponsnode = (Epon_S_TopNodeEntity) node1;
					address = view.getKeyAddressMap().get(eponsnode.getGuid());
				} else if (node1 instanceof ONUTopoNodeEntity) {
					ONUTopoNodeEntity onunode = (ONUTopoNodeEntity) node1;
					address = view.getKeyAddressMap().get(onunode.getGuid());
				}
				if (address != null) {
					p1 = getPointByAddress(address);
				}
			}

			if (node2 instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 node3 = (SwitchTopoNodeLevel3) node2;
				Set<AddressEntity> addresses = view.getAddressIpsMap().keySet();
				for (AddressEntity address : addresses) {
					List<String> ips = view.getAddressIpsMap().get(address);
					for (String ip : ips) {
						String nip = node3.getIpValue();
						if (ip.equals(nip)) {
							p2 = getPointByAddress(address);
						}
					}
				}
			} else {
				AddressEntity address = null;
				if (node2 instanceof SwitchTopoNodeEntity) {
					SwitchTopoNodeEntity switchertopo = (SwitchTopoNodeEntity) node2;
					address = view.getKeyAddressMap().get(
							switchertopo.getGuid());
				} else if (node2 instanceof CarrierTopNodeEntity) {
					CarrierTopNodeEntity carriertopo = (CarrierTopNodeEntity) node2;
					address = view.getKeyAddressMap()
							.get(carriertopo.getGuid());
				} else if (node2 instanceof GPRSTopoNodeEntity) {
					GPRSTopoNodeEntity gprsnode = (GPRSTopoNodeEntity) node2;
					address = view.getKeyAddressMap().get(gprsnode.getGuid());
				} else if (node2 instanceof EponTopoEntity) {
					EponTopoEntity oltnode = (EponTopoEntity) node1;
					address = view.getKeyAddressMap().get(oltnode.getGuid());
				} else if (node2 instanceof Epon_S_TopNodeEntity) {
					Epon_S_TopNodeEntity eponsnode = (Epon_S_TopNodeEntity) node2;
					address = view.getKeyAddressMap().get(eponsnode.getGuid());
				} else if (node2 instanceof ONUTopoNodeEntity) {
					ONUTopoNodeEntity onunode = (ONUTopoNodeEntity) node2;
					address = view.getKeyAddressMap().get(onunode.getGuid());
				}
				if (address != null) {
					p2 = getPointByAddress(address);
				}
			}

			if (p1 == null || p2 == null) {
				return null;
			}
		} catch (AutomationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (lineType == Constants.LINE_COPPER) {

		} else if (lineType == Constants.LINE_FIBERO) {
			try {
				symbol = new SimpleLineSymbol();
				symbol.setWidth(2);
			} catch (IOException e) {
				e.printStackTrace(); // never happened
			}
		} else if (lineType == Constants.LINE_WIRE) {
			try {
				int xs1[] = new int[1];
				int ys1[] = new int[1];
				int xs2[] = new int[1];
				int ys2[] = new int[1];
				displayTransformation.fromMapPoint(p1, xs1, ys1);
				displayTransformation.fromMapPoint(p2, xs2, ys2);
				int xm = Math.abs(xs1[0] - xs2[0]);
				int ym = Math.abs(ys1[0] - ys2[0]);
				if (xs1[0] > xs2[0]) {
					xm = xs2[0] + xm;
				} else {
					xm = xs1[0] + xm;
				}
				if (ys1[0] > ys2[0]) {
					ym = ys2[0] + ym;
				} else {
					ym = ys1[0] + ym + 6;
				}
				IPoint pm = displayTransformation.toMapPoint(xm, ym);

				BezierCurve bc = new BezierCurve();
				bc.putCoord(0, p1);
				bc.putCoord(1, pm);
				bc.putCoord(2, p2);
				lineElement = new LineElement();
				lineElement.setGeometry(bc);
				return lineElement;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (lineType == Constants.LINE_WIRELESS) {
			try {
				CartographicLineSymbol cartSym = new CartographicLineSymbol();
				cartSym.setWidth(2);
				cartSym.setCap(esriLineCapStyle.esriLCSRound);
				cartSym.setJoin(esriLineJoinStyle.esriLJSRound);
				//
				ITemplate template = new Template();
				template.addPatternElement(1, 5);
				template.setInterval(1); // symbolSize * 3
				ILineProperties lineProperties = cartSym;
				lineProperties.setTemplateByRef(template);
				//
				symbol = cartSym;
			} catch (IOException e) {
				e.printStackTrace(); // never happened
			}
		}

		try {
			Polyline polyline2d = new Polyline();
			polyline2d.setFromPoint(p1);
			polyline2d.setToPoint(p2);
			lineElement = new LineElement();
			if (status == 2) {
				RgbColor color = null;
				try {
					color = new RgbColor();
					color.setRed(0x0000FF);
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				symbol.setColor(color);
			}
			lineElement.setSymbol(symbol);
			lineElement.setGeometry(polyline2d);

		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lineElement;
	}

	private IMarkerElement create(Polyline line, int status)
			throws AutomationException, IOException {

		IPoint pf = line.getFromPoint();
		IPoint pt = line.getToPoint();
		IPoint pm = getMidPoint(pf, pt);
		Envelope envelope = new Envelope();
		IMarkerElement element = new MarkerElement();
		if (pm != null) {
			int xs[] = new int[1];
			int ys[] = new int[1];
			displayTransformation.fromMapPoint(pm, xs, ys);
			int x = xs[0];
			int y = ys[0];
			int xmin = x - 8;
			int xmax = x + 8;
			int ymin = y - 8;
			int ymax = y + 8;

			IPoint p1 = displayTransformation.toMapPoint(xmin, ymax);
			IPoint p2 = displayTransformation.toMapPoint(xmax, ymax);
			IPoint p3 = displayTransformation.toMapPoint(xmin, ymin);
			IPoint p4 = displayTransformation.toMapPoint(xmax, ymin);

			envelope.setLowerLeft(p1);
			envelope.setLowerRight(p2);
			envelope.setUpperLeft(p3);
			envelope.setUpperRight(p4);
		}

		PictureMarkerSymbol pms = new PictureMarkerSymbol();

		pms.createMarkerSymbolFromFile(esriIPictureType.esriIPictureEMF,
				"E:\\admclient\\res\\network\\link-block.png");
		element.setSymbol(pms);
		pms.draw(envelope);

		return element;

	}

	private IElement getLineStatus(Polyline line, int status)
			throws AutomationException, IOException {
		IPoint pf = line.getFromPoint();
		IPoint pt = line.getToPoint();
		IPoint pm = getMidPoint(pf, pt);
		Envelope envelope = new Envelope();
		PngPictureElement pe = new PngPictureElement();
		if (pm != null) {
			int xs[] = new int[1];
			int ys[] = new int[1];
			displayTransformation.fromMapPoint(pm, xs, ys);
			int x = xs[0];
			int y = ys[0];
			int xmin = x - 8;
			int xmax = x + 8;
			int ymin = y - 8;
			int ymax = y + 8;

			IPoint p1 = displayTransformation.toMapPoint(xmin, ymax);
			IPoint p2 = displayTransformation.toMapPoint(xmax, ymax);
			IPoint p3 = displayTransformation.toMapPoint(xmin, ymin);
			IPoint p4 = displayTransformation.toMapPoint(xmax, ymin);

			envelope.setLowerLeft(p1);
			envelope.setLowerRight(p2);
			envelope.setUpperLeft(p3);
			envelope.setUpperRight(p4);
		}
		if (status == 0) {
			pe.importPictureFromFile(imagePath + "link-block.png");
			pe.setGeometry(envelope);
			return pe;
		} else if (status == -1) {
			pe.importPictureFromFile(imagePath + "disconnect.png");
			pe.setGeometry(envelope);

			return pe;
		}
		return null;
	}

	private IPoint getPointByAddress(AddressEntity address)
			throws AutomationException, IOException {
		String longitude = null;
		String latitude = null;
		IPoint point = new Point();
		if (address != null) {
			longitude = address.getLongitude();
			latitude = address.getLatitude();
		} else {
			return null;
		}
		if (longitude == null || latitude == null) {
			return null;
		}
		point.setX(Double.parseDouble(longitude));
		point.setY(Double.parseDouble(latitude));
		return point;
	}

	private IPoint getPointByNode(NodeEntity node) throws AutomationException,
			IOException {
		IPoint point = new Point();
		String longitude = null;
		String latitude = null;
		AddressEntity address = null;
		String key = null;
		CommonServiceBeanRemote commonService = view.getRemoteServer()
				.getService();
		if (node instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switchertopo = (SwitchTopoNodeEntity) node;
			String ipvalue = switchertopo.getIpValue();
			address = commonService.queryAddressBySwitcher2(ipvalue);
			key = switchertopo.getGuid();
		} else if (node instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carriertopo = (CarrierTopNodeEntity) node;
			int code = carriertopo.getCarrierCode();
			address = commonService.queryAddressByCarrier(code);
			key = carriertopo.getGuid();
		} else if (node instanceof GPRSTopoNodeEntity) {
			GPRSTopoNodeEntity gprsnode = (GPRSTopoNodeEntity) node;
			String userId = gprsnode.getUserId();
			address = commonService.queryAddressByGPRS(userId);
			key = gprsnode.getGuid();
		} else if (node instanceof EponTopoEntity) {
			EponTopoEntity oltnode = (EponTopoEntity) node;
			String ipvalue = oltnode.getIpValue();
			address = commonService.queryAddressByOLT(ipvalue);
			key = oltnode.getGuid();
		} else if (node instanceof Epon_S_TopNodeEntity) {
			Epon_S_TopNodeEntity eponsnode = (Epon_S_TopNodeEntity) node;
			EponSplitter esp = eponsnode.getEponSplitter();
			address = esp.getAddressEntity();
			key = eponsnode.getGuid();
		} else if (node instanceof ONUTopoNodeEntity) {
			ONUTopoNodeEntity onunode = (ONUTopoNodeEntity) node;
			String mac = onunode.getMacValue();
			address = commonService.queryAddressByONU(mac);
			key = onunode.getGuid();
		}

		if (address != null) {
			longitude = address.getLongitude();
			latitude = address.getLatitude();
		} else {
			return null;
		}
		if (longitude == null || latitude == null) {
			return null;
		}
		point.setX(Double.parseDouble(longitude));
		point.setY(Double.parseDouble(latitude));
		view.getKeyAddressMap().put(key, address);
		return point;
	}

	private IPoint getMidPoint(IPoint p1, IPoint p2) {
		IPoint point = null;
		try {
			int[] xs1 = new int[1];
			int[] ys1 = new int[1];
			displayTransformation.fromMapPoint(p1, xs1, ys1);
			int[] xs2 = new int[1];
			int[] ys2 = new int[1];
			displayTransformation.fromMapPoint(p2, xs2, ys2);
			int x1 = xs1[0];
			int x2 = xs2[0];
			int y1 = ys1[0];
			int y2 = ys2[0];
			int xm = 0;
			int ym = 0;
			if (x1 > x2) {
				xm = Math.abs(x1 - x2) / 2 + x2;
			} else {
				xm = Math.abs(x1 - x2) / 2 + x1;
			}
			if (y1 > y2) {
				ym = Math.abs(y1 - y2) / 2 + y2;
			} else {
				ym = Math.abs(y1 - y2) / 2 + y1;
			}

			point = displayTransformation.toMapPoint(xm, ym);
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return point;
	}

	private IElement createNode(NodeEntity node) throws AutomationException,
			IOException {
		String pictureName = null;
		String name = node.getName();
		String txt = null;
		String key = null;
		int ws = 0, hs = 0;
		if (node instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switchertopo = (SwitchTopoNodeEntity) node;
			key = switchertopo.getIpValue();
			txt = key;
			pictureName = "switcher2map";
			name = switchertopo.getIpValue();
			ws = 18;
			hs = 6;
		} else if (node instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carriertopo = (CarrierTopNodeEntity) node;
			key = carriertopo.getCarrierCode() + "";

			name = key + "";
			txt = "载波机:" + key;
			pictureName = "carrier";
			ws = 18;
			hs = 6;
		} else if (node instanceof GPRSTopoNodeEntity) {
			GPRSTopoNodeEntity gprsnode = (GPRSTopoNodeEntity) node;
			String userId = gprsnode.getUserId();
			name = userId;
			txt = "GPRS:" + userId;
			pictureName = "gprs2map";
			ws = 7;
			hs = 19;
		} else if (node instanceof EponTopoEntity) {
			EponTopoEntity oltnode = (EponTopoEntity) node;
			name = oltnode.getDeviceType();
			txt = "OLT:" + oltnode.getIpValue();
			ws = 32;
			hs = 8;
			pictureName = "olt2map";
		} else if (node instanceof Epon_S_TopNodeEntity) {
			name = "分光器";
			txt = "分光器";
			ws = 18;
			hs = 5;
			pictureName = "spliter2map";
		} else if (node instanceof ONUTopoNodeEntity) {
			ONUTopoNodeEntity onunode = (ONUTopoNodeEntity) node;
			name = "ONU";
			txt = onunode.getSequenceNo() + "";
			ws = 18;
			hs = 5;
			pictureName = "onu2map";
		}

		IPoint point = getPointByNode(node);
		if (point == null) {
			return null;
		}

		Envelope envelope = new Envelope();
		String imagePath = "res/network/";

		int xs[] = new int[1];
		int ys[] = new int[1];

		displayTransformation.fromMapPoint(point, xs, ys);
		int x = xs[0];
		int y = ys[0];

		int xmin = x - ws;
		int xmax = x + ws;
		int ymin = y - hs;
		int ymax = y + hs;

		IPoint p1 = displayTransformation.toMapPoint(xmin, ymax);
		IPoint p2 = displayTransformation.toMapPoint(xmax, ymax);
		IPoint p3 = displayTransformation.toMapPoint(xmin, ymin);
		IPoint p4 = displayTransformation.toMapPoint(xmax, ymin);

		envelope.setLowerLeft(p1);
		envelope.setLowerRight(p2);
		envelope.setUpperLeft(p3);
		envelope.setUpperRight(p4);

		PngPictureElement pe = new PngPictureElement();
		pe.setCustomProperty(node);
		pe.setSavePictureInDocument(true);
		pe.importPictureFromFile(imagePath + pictureName + ".png");
		pe.setName(name);

		pe.setGeometry(envelope);
		addTextElement(p1, txt);
		return pe;
	}

	private IElement createSwitcher3Node(String longitude, String latitude)
			throws AutomationException, IOException {
		PngPictureElement pe = new PngPictureElement();
		pe.setSavePictureInDocument(true);
		String imagePath = "res/network/";
		pe.importPictureFromFile(imagePath + "subnet.png");
		Envelope envelope = new Envelope();

		IPoint point = new Point();
		String txt = "三层网络";
		point.setX(Double.parseDouble(longitude));
		point.setY(Double.parseDouble(latitude));

		int xs[] = new int[1];
		int ys[] = new int[1];

		displayTransformation.fromMapPoint(point, xs, ys);
		int x = xs[0];
		int y = ys[0];

		int xmin = x - 20;
		int xmax = x + 20;
		int ymin = y - 15;
		int ymax = y + 15;

		IPoint p1 = displayTransformation.toMapPoint(xmin, ymax);
		IPoint p2 = displayTransformation.toMapPoint(xmax, ymax);
		IPoint p3 = displayTransformation.toMapPoint(xmin, ymin);
		IPoint p4 = displayTransformation.toMapPoint(xmax, ymin);

		envelope.setLowerLeft(p1);
		envelope.setLowerRight(p2);
		envelope.setUpperLeft(p3);
		envelope.setUpperRight(p4);
		pe.setGeometry(envelope);
		addTextElement(p1, txt);
		return pe;
	}

	public String getNameByNode(NodeEntity node) {
		String name = node.getName();
		if (node instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switchertopo = (SwitchTopoNodeEntity) node;
			SwitchNodeEntity switcher = switchertopo.getNodeEntity();
			name = switcher.getBaseConfig().getIpValue();
		} else if (node instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carriertopo = (CarrierTopNodeEntity) node;
			CarrierEntity carrier = carriertopo.getNodeEntity();
			name = carrier.getCarrierCode() + "";
		} else if (node instanceof SwitchTopoNodeLevel3) {
			SwitchTopoNodeLevel3 switchertopo3 = (SwitchTopoNodeLevel3) node;
			SwitchLayer3 switcher3 = switchertopo3.getSwitchLayer3();
			name = switchertopo3.getIpValue();
		}
		return name;
	}

	// ==========================================================================================================
	public MapStatuBar getStatusbar() {
		return statusbar;
	}

	@SuppressWarnings("deprecation")
	private void centerNode(NodeEntity node) {
		IGraphicsContainerSelect graphicsContainerSelect = (IGraphicsContainerSelect) graphicsContainer;
		IElement element;
		IPoint pPoint;
		try {
			graphicsContainer.reset();
			pPoint = new Point();
			element = graphicsContainer.next();
			PngPictureElement pElement;
			while (element != null) {
				if (element instanceof PngPictureElement) {
					pElement = (PngPictureElement) element;
					Object obj = pElement.getCustomProperty();
					if (node == obj) {
						IEnvelope env = pElement.getGeometry().getEnvelope();
						mapBean.centerAt(env.getLowerLeft());
						selectedElement(pElement);
						graphicsContainerSelect.selectElement(pElement);
					}
					break;
				}
				element = graphicsContainer.next();
			}
		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点选中节点
	 * 
	 * @param xc
	 * @param yc
	 * @throws IOException
	 */
	private void selectElementByPoint(double xc, double yc) throws IOException {
		IDisplayTransformation displayTransformation = this.mapBean
				.getActiveView().getScreenDisplay().getDisplayTransformation();
		IGraphicsContainer graphicsContainer = this.mapBean.getActiveView()
				.getGraphicsContainer();
		graphicsContainer.reset();

		IElement element1 = graphicsContainer.next();
		while (element1 != null) {
			if (element1 instanceof RectangleElement) {
				graphicsContainer.deleteElement(element1);
				break;
			}
			element1 = graphicsContainer.next();
		}
		graphicsContainer.reset();
		IElement element = graphicsContainer.next();
		view.setSelectedElement(null);
		while (element != null) {
			if (element instanceof PngPictureElement) {
				PngPictureElement pElement = (PngPictureElement) (element);
				IEnvelope env = pElement.getGeometry().getEnvelope();
				int xmi[] = new int[1];
				int ymi[] = new int[1];
				int xmx[] = new int[1];
				int ymx[] = new int[1];

				IPoint tp1 = env.getEnvelope().getUpperLeft();
				IPoint tp2 = env.getEnvelope().getLowerRight();

				displayTransformation.fromMapPoint(tp1, xmi, ymi);
				displayTransformation.fromMapPoint(tp2, xmx, ymx);
				int xmin = xmi[0] - 2;
				int ymin = ymi[0] - 2;
				int xmax = xmx[0] + 2;
				int ymax = ymx[0] + 2;

				if ((xc >= xmin && xc <= xmax) && (yc >= ymin && yc <= ymax)) {
					view.setSelectedElement(pElement);
					break;
				}
			}
			element = graphicsContainer.next();
		}
	}

	/**
	 * 把一个指定节点设为选中状态
	 * 
	 * @param element
	 * @throws IOException
	 */
	private void selectedElement(IElement element) throws IOException {
		if (element instanceof PngPictureElement) {
			PngPictureElement pElement = (PngPictureElement) (element);
			IEnvelope env = pElement.getGeometry().getEnvelope();
			int xmi[] = new int[1];
			int ymi[] = new int[1];
			int xmx[] = new int[1];
			int ymx[] = new int[1];
			IPoint tp1 = env.getEnvelope().getUpperLeft();
			IPoint tp2 = env.getEnvelope().getLowerRight();
			displayTransformation.fromMapPoint(tp1, xmi, ymi);
			displayTransformation.fromMapPoint(tp2, xmx, ymx);
			int xmin = xmi[0] - 2;
			int ymin = ymi[0] - 2;
			int xmax = xmx[0] + 2;
			int ymax = ymx[0] + 2;
			Envelope envelope = new Envelope();
			envelope.setLowerLeft(displayTransformation.toMapPoint(xmin, ymax));
			envelope
					.setLowerRight(displayTransformation.toMapPoint(xmax, ymax));
			envelope.setUpperLeft(displayTransformation.toMapPoint(xmin, ymin));
			envelope
					.setUpperRight(displayTransformation.toMapPoint(xmax, ymin));

			view.getHolowElement().setGeometry(envelope);

			graphicsContainer.addElement(view.getHolowElement(), 0);

		}
	}

}
