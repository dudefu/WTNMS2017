package com.jhw.adm.client.map.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import com.esri.arcgis.carto.IElement;
import com.esri.arcgis.carto.IGraphicsContainer;
import com.esri.arcgis.carto.IGraphicsContainerSelect;
import com.esri.arcgis.carto.PngPictureElement;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.IEnvelopeGEN;
import com.esri.arcgis.interop.AutomationException;
import com.jhw.adm.client.map.GISView;
import com.jhw.adm.server.entity.warning.SimpleWarning;

public class AlarmModelListener implements PropertyChangeListener {
	private GISView view;

	public AlarmModelListener(GISView view) {
		this.view = view;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		List<SimpleWarning> warnings = (List<SimpleWarning>) evt.getNewValue();
		for(SimpleWarning warning:warnings){
			String ipvalue = warning.getIpValue();
			int level = warning.getWarningLevel();
		}
	}

	private void SelectName(String ipValue) throws AutomationException, IOException {
		IGraphicsContainer graphicsContainer = view.getMapBean().getActiveView().getGraphicsContainer();
//		view.getD
		IGraphicsContainerSelect graphicsContainerSelect = (IGraphicsContainerSelect)graphicsContainer;
		
		IElement pElement;
		try {
			graphicsContainer.reset();
			pElement = graphicsContainer.next();
			PngPictureElement element;
			while (pElement != null) {
				if (pElement instanceof PngPictureElement) {
					element = (PngPictureElement) pElement;
					String ename = element.getName();
					if (ename.equals(ipValue)) {
						graphicsContainerSelect.selectElement(pElement); // …Ë÷√—°‘Ò◊¥Ã¨
//						IEnvelopeGEN envelope = (IEnvelopeGEN) displayTransformation
//								.getVisibleBounds();
//						envelope.centerAt(pElement.getGeometry().getEnvelope()
//								.getLowerLeft());
//						displayTransformation
//								.setVisibleBounds((IEnvelope) envelope);
						break;
					}
				}
				pElement = graphicsContainer.next();
			}
		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
