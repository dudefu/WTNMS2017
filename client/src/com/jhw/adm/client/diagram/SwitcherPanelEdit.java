package com.jhw.adm.client.diagram;

import java.awt.Image;

import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.PanelEdit;
import com.jhw.adm.client.draw.PanelFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

public class SwitcherPanelEdit extends PanelEdit {
	
	public SwitcherPanelEdit() {
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);
	}
	
	@Override
	public PanelFigure restoreFigure(Object figureModel) {
		Image panelImage = imageRegistry.getImageIcon(NetworkConstants.IETH802_E)
		.getImage();
		if (figureModel instanceof SwitchNodeEntity) {
	    	SwitchNodeEntity switcher = (SwitchNodeEntity)figureModel;
	    	if (switcher.getBaseConfig() == null || switcher.getBaseInfo() == null) {
	    		LOG.error("SwitchNodeEntity({}).(BaseConfig || BaseInfo) can not be null", switcher.getId());
	    	} else {
				String category = switcher.getType();
				
				// TODO: remove hard code...
				if (category.equals("IETH804")) {
					panelImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008_E).getImage();
				}
	    	}
		}
	    	
	    PanelFigure panelFigure = new PanelFigure(Images.toBufferedImage(panelImage));
	    setFigure(panelFigure);
		setModel(figureModel);
		panelFigure.setEdit(this);
		
		return panelFigure;
	}

	private ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(SwitcherPanelEdit.class);
}
