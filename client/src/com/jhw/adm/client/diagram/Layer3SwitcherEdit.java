package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;

public class Layer3SwitcherEdit extends EquipmentEdit {
	
	public Layer3SwitcherEdit() {
		this.imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);
	}
	
	@Override
	public SwitchTopoNodeLevel3 createModel() {
		SwitchTopoNodeLevel3 layer3SwitcherNode = new SwitchTopoNodeLevel3();
		layer3SwitcherNode.setGuid(UUID.randomUUID().toString());
		SwitchLayer3 layer3Switcher = new SwitchLayer3();
		layer3SwitcherNode.setGuid(UUID.randomUUID().toString());		
		layer3SwitcherNode.setSwitchLayer3(layer3Switcher);
		this.setModel(layer3SwitcherNode);
		return layer3SwitcherNode;
	}
	
	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.LEVEL_3_SWITCHER));
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
		figure.setText("Èý²ã½»»»»ú");
		setFigure(figure);
		figure.setEdit(this);
		
		return figure;
	}
		
	@Override
	public EquipmentFigure restoreFigure(Object figureModel) {
		double x = 0;
		double y = 0;
		String text = StringUtils.EMPTY;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.LEVEL_3_SWITCHER));
		if (figureModel instanceof SwitchTopoNodeLevel3) {
			SwitchTopoNodeLevel3 layer3SwitcherNode = (SwitchTopoNodeLevel3) figureModel;
	    	x = layer3SwitcherNode.getX();
	    	y = layer3SwitcherNode.getY();
	    	text = getNodeText(layer3SwitcherNode);
		}
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
		figure.willChange();
		figure.setText(text);
		figure.changed();

		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		figure.transform(at);
		
		setFigure(figure);
		setModel(figureModel);
//		updateAttributes(figureModel);
		figure.setEdit(this);
		return figure;
	}

	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof SwitchTopoNodeLevel3) {
			SwitchTopoNodeLevel3 layer3SwitcherNode = (SwitchTopoNodeLevel3) figureModel;
			layer3SwitcherNode.setModifyNode(true);
			figure.willChange();
			figure.setText(getNodeText(layer3SwitcherNode));
			figure.fireAreaInvalidated();
			figure.changed();
		}
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof SwitchTopoNodeLevel3) {
			SwitchTopoNodeLevel3 layer3SwitcherNode = (SwitchTopoNodeLevel3) figureModel;
			layer3SwitcherNode.setModifyNode(true);
			layer3SwitcherNode.setX(figure.getBounds().x);
			layer3SwitcherNode.setY(figure.getBounds().y);
		}
	}

	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(Layer3SwitcherEdit.class);
}