package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.UUID;

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
import com.jhw.adm.client.draw.SwitcherFigure;
import com.jhw.adm.client.model.switcher.SwitcherModelNumber;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

public class SwitcherEdit extends EquipmentEdit {
	
	public SwitcherEdit() {
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		alarmImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ALARM));
		switcherModelNumber = ClientUtils.getSpringBean(SwitcherModelNumber.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}

	@Override
	public SwitcherFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SWITCHER));
		SwitcherFigure figure = new SwitcherFigure(bufferedImage);
		figure.setText(StringUtils.EMPTY);
		setFigure(figure);
		figure.setEdit(this);
		
		return figure;
	}

	@Override
	public SwitchTopoNodeEntity createModel() {
		SwitchTopoNodeEntity switcherNode = new SwitchTopoNodeEntity();
		switcherNode.setGuid(UUID.randomUUID().toString());
		switcherNode.setStatus(Constants.NORMAL);
		SwitchNodeEntity switcher = new SwitchNodeEntity();
		switcher.setType(NetworkConstants.SWITCHER);
		switcher.setSyschorized(false);
		switcher.setPorts(new HashSet<SwitchPortEntity>());
		
		SwitchBaseInfo baseInfo = new SwitchBaseInfo();
		SwitchBaseConfig baseConfig = new SwitchBaseConfig();
		baseConfig.setIpValue(StringUtils.EMPTY);
		switcher.setBaseConfig(baseConfig);
		switcher.setBaseInfo(baseInfo);
		switcherNode.setNodeEntity(switcher);
		this.setModel(switcherNode);
		return switcherNode;
	}

	@Override
	public SwitcherFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		int status = Constants.NORMAL;
		double x = 0;
		double y = 0;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SWITCHER));
		if (figureModel instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity) figureModel;
			SwitchNodeEntity switcher = remoteServer.getService().getSwitchByIp(switcherNode.getIpValue());
	    	if(switcher != null){
	    		if (switcher.getBaseConfig() == null || switcher.getBaseInfo() == null) {
	    			LOG.error("SwitchNodeEntity({}).(BaseConfig || BaseInfo) can not be null", switcher.getId());
	    		} else {
	    			int modelNumber = switcher.getDeviceModel();
	    			String imageName = switcherModelNumber.getImageName(modelNumber);
	    			bufferedImage = Images.toBufferedImage(imageRegistry.getImage(imageName));
	    		}
	    	}
	    	
	    	text = getNodeText(switcherNode);
	    	status = switcherNode.getStatus();
	    	x = switcherNode.getX();
	    	y = switcherNode.getY();
		}
		final SwitcherFigure figure = new SwitcherFigure(bufferedImage);
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
		if (figureModel instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switchTopoNode = (SwitchTopoNodeEntity) figureModel;
			switchTopoNode.setModifyNode(true);
			switchTopoNode.setX(figure.getBounds().x);
			switchTopoNode.setY(figure.getBounds().y);
		}
	}

	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity) figureModel;
	    	SwitchNodeEntity switcher = NodeUtils.getNodeEntity(switcherNode).getNodeEntity();
	    	if (switcher.getBaseConfig() == null || switcher.getBaseInfo() == null) {
	    		LOG.error("SwitchNodeEntity({}).(BaseConfig || BaseInfo) can not be null", switcher.getId());
	    	} else {
	    		int modelNumber = switcher.getDeviceModel();

				String imageName = switcherModelNumber.getImageName(modelNumber);
				BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(imageName));
				figure.willChange();
				figure.setBufferedImage(bufferedImage);
				figure.setText(getNodeText(switcherNode));
				chageStatus(switcherNode.getStatus());
				figure.changed();
	    	}
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
		
	private final BufferedImage alarmImage;
	private final SwitcherModelNumber switcherModelNumber;
	private final ImageRegistry imageRegistry;
	private final RemoteServer remoteServer;
	private static final Logger LOG = LoggerFactory.getLogger(SwitcherEdit.class);
}