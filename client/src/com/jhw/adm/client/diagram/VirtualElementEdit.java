package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.draw.VirtualElementFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.VirtualType;

public class VirtualElementEdit extends EquipmentEdit {

	private static final Logger LOG = LoggerFactory.getLogger(VirtualElementEdit.class);
	private final ImageRegistry imageRegistry;
	private final RemoteServer remoteServer;
	private final BufferedImage alarmImage;
	
	public VirtualElementEdit(){
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		alarmImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ALARM));
	}
	
	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SWITCHER));
		VirtualElementFigure figure = new VirtualElementFigure(bufferedImage);
		figure.setText(StringUtils.EMPTY);
		setFigure(figure);
		figure.setEdit(this);
		
		return figure;
	}

	@Override
	public VirtualNodeEntity createModel() {
		VirtualNodeEntity virtualNode = new VirtualNodeEntity();
		virtualNode.setGuid(UUID.randomUUID().toString());
		virtualNode.setIpValue(StringUtils.EMPTY);
		virtualNode.setName(StringUtils.EMPTY);
		virtualNode.setType(0);
		
		this.setModel(virtualNode);
		return virtualNode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EquipmentFigure restoreFigure(Object figureModel) {
		
		String text = StringUtils.EMPTY;
		int status = Constants.NORMAL;
		double x = 0;
		double y = 0;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SWITCHER));
		if (figureModel instanceof VirtualNodeEntity) {
			VirtualNodeEntity virtualNode = (VirtualNodeEntity) figureModel;
			
			String where = " where id = " + virtualNode.getType();
			List<VirtualType> virtualTypes = (List<VirtualType>)remoteServer.getService().findAll(VirtualType.class, where);
			if(virtualTypes.size() != 0){
				byte[] bytes = virtualTypes.get(0).getBytes();
				bufferedImage = Images.toBufferedImage(new ImageIcon(bytes).getImage());
			}
	    	
	    	text = getNodeText(virtualNode);
	    	status = virtualNode.getStatus();
	    	x = virtualNode.getX();
	    	y = virtualNode.getY();
		}
		VirtualElementFigure figure = new VirtualElementFigure(bufferedImage);
		figure.willChange();
		figure.setText(text);
		figure.changed();

		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		figure.transform(at);
		
		setFigure(figure);
		setModel(figureModel);
		figure.setEdit(this);
		chageStatus(status);
		return figure;
	}

	@Override
	public void updateModel() {		
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof VirtualNodeEntity) {
			VirtualNodeEntity virtualNode = (VirtualNodeEntity) figureModel;
			virtualNode.setModifyNode(true);
			virtualNode.setX(figure.getBounds().x);
			virtualNode.setY(figure.getBounds().y);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof VirtualNodeEntity) {
			VirtualNodeEntity virtualNode = (VirtualNodeEntity) figureModel;
			
			
			String where = " where id = " + virtualNode.getType();
			List<VirtualType> virtualTypes = (List<VirtualType>)remoteServer.getService().findAll(VirtualType.class, where);
			//must get virtual image through virtual type
			byte[] bytes = virtualTypes.get(0).getBytes();

			BufferedImage bufferedImage = Images.toBufferedImage(new ImageIcon(bytes).getImage());
			figure.willChange();
			figure.setBufferedImage(bufferedImage);
			figure.setText(getNodeText(virtualNode));
			figure.fireAreaInvalidated();
			figure.changed();
			chageStatus(virtualNode.getStatus());
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
	 * ½»»»»ú¹ÊÕÏ¸æ¾¯
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
	 * ½»»»»ú¹ÊÕÏ»Ö¸´
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

}
