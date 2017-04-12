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
import com.jhw.adm.server.entity.epon.EponSplitter;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;

public class BeamsplitterEdit extends EquipmentEdit {
	
	public BeamsplitterEdit() {
		this.imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
	}
	
	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.BEAMSPLITTER));
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
		setFigure(figure);
		updateAttributes();
		figure.setEdit(this);
		
		return figure;
	}
	
	@Override
	public EquipmentFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		double x = 0;
		double y = 0;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.BEAMSPLITTER));
		if (figureModel instanceof Epon_S_TopNodeEntity) {
			Epon_S_TopNodeEntity splitterNode = (Epon_S_TopNodeEntity) figureModel;
			text = getNodeText(splitterNode);
	    	x = splitterNode.getX();
	    	y = splitterNode.getY();
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
		figure.setEdit(this);
		return figure;
	}
	
	@Override
	public Epon_S_TopNodeEntity createModel() {
		Epon_S_TopNodeEntity splitterNode = new Epon_S_TopNodeEntity();
		splitterNode.setGuid(UUID.randomUUID().toString());
		EponSplitter splitter = new EponSplitter();
		splitter.setSyschorized(false);
		splitterNode.setEponSplitter(splitter);
		AddressEntity address = new AddressEntity();
		splitter.setAddressEntity(address);
		this.setModel(splitterNode);
		return splitterNode;
	}
	
	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof Epon_S_TopNodeEntity) {
			Epon_S_TopNodeEntity splitterNode = (Epon_S_TopNodeEntity)figureModel;
			figure.willChange();
			splitterNode.setModifyNode(true);
			figure.setText(getNodeText(splitterNode));
			figure.fireAreaInvalidated();
			figure.changed();
		}
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof Epon_S_TopNodeEntity) {
			Epon_S_TopNodeEntity splitterNode = (Epon_S_TopNodeEntity)figureModel;
			splitterNode.setModifyNode(true);
			splitterNode.setX(figure.getBounds().x);
			splitterNode.setY(figure.getBounds().y);
		}
	}
	
	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(BeamsplitterEdit.class);
}