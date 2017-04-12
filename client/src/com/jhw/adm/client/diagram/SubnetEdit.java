package com.jhw.adm.client.diagram;


import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

/**
 * 子网控制类
 */
public class SubnetEdit extends EquipmentEdit {
	
	public SubnetEdit() {
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		alarmImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ALARM));
	}

	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SUBNET));
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
		setFigure(figure);
		updateAttributes();
		figure.setEdit(this);
		
		return figure;
	}

	@Override
	public Object createModel() {
		SubNetTopoNodeEntity subnetNode = new SubNetTopoNodeEntity();
		subnetNode.setGuid(UUID.randomUUID().toString());
		this.setModel(subnetNode);
		return subnetNode;
	}

	@Override
	public EquipmentFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		int status = Constants.NORMAL;
		double x = 0;
		double y = 0;
		SubNetTopoNodeEntity subnetNode = null;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SUBNET));
		if (figureModel instanceof SubNetTopoNodeEntity) {
			subnetNode = (SubNetTopoNodeEntity) figureModel;
			text = subnetNode.getName();
	    	x = subnetNode.getX();
	    	y = subnetNode.getY();
	    	status = subnetNode.getStatus();
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
		chageStatus(status);
		return figure;
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof SubNetTopoNodeEntity) {
			SubNetTopoNodeEntity subnetNode = (SubNetTopoNodeEntity) figureModel;
			subnetNode.setModifyNode(true);
			subnetNode.setX(figure.getBounds().x);
			subnetNode.setY(figure.getBounds().y);
		}
	}

	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof SubNetTopoNodeEntity) {
			SubNetTopoNodeEntity subnetNode = (SubNetTopoNodeEntity) figureModel;
			BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SUBNET));
			chageStatus(subnetNode.getStatus());			
			figure.willChange();
			figure.setBufferedImage(bufferedImage);
			figure.setText(subnetNode.getName());
			figure.changed();
		}
	}
	
	private void chageStatus(int status) {
		switch (status) {
			case Constants.NORMAL:
				normal();break;
			case Constants.HASWARNING:
				alarm();break;
				
		}
	}
	
	/**
	 * 交换机故障告警
	 */
	public void alarm() {
		if (SwingUtilities.isEventDispatchThread()) {
			EquipmentFigure figure = getFigure();
			figure.willChange();
			figure.setStatusImage(alarmImage);
			figure.changed();
			figure.fireAreaInvalidated();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					alarm();
				}
			});
		}
	}
	
	/**
	 * 交换机故障恢复
	 */
	public void normal() {
		if (SwingUtilities.isEventDispatchThread()) {
			EquipmentFigure figure = getFigure();
			figure.willChange();
			figure.setStatusImage(null);
			figure.changed();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					normal();
				}
			});
		}
	}

	private final BufferedImage alarmImage;
	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(SubnetEdit.class);
}