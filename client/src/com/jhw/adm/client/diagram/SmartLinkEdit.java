package com.jhw.adm.client.diagram;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.START_DECORATION;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.ConnectPolicy;
import com.jhw.adm.client.draw.LabeledLinkEdit;
import com.jhw.adm.client.draw.LabeledLinkFigure;
import com.jhw.adm.client.draw.LinearLiner;
import com.jhw.adm.client.draw.NodeFigure;
import com.jhw.adm.client.draw.PowerLiner;
import com.jhw.adm.client.draw.SerialLineDecoration;
import com.jhw.adm.client.model.LinkCategory;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

public class SmartLinkEdit extends LabeledLinkEdit {
	
	public SmartLinkEdit() {
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);
		createConnectPolicies();
	}

	@Override
	public LabeledLinkFigure createFigure() {
		LabeledLinkFigure figure = new LabeledLinkFigure();
		setFigure(figure);
		figure.setEdit(this);
		
		return figure;
	}
	
	@Override
	public LinkEntity createModel() {
		LinkEntity linkNode = new LinkEntity();
		linkNode.setLineType(LinkCategory.ETHERNET);
		linkNode.setSynchorized(true);
		linkNode.setGuid(UUID.randomUUID().toString());
		setModel(linkNode);
		return linkNode;
	}
	
	@Override
	public LabeledLinkFigure restoreFigure(Object figureModel) {
		final LabeledLinkFigure figure = new LabeledLinkFigure();
//		LinkEntity linkNode = toModel(entity);
		setModel(figureModel);
		if (getModel() == null) {
			LOG.error("实体不能为空");
		} else {
			LinkEntity linkNode = toModel(getModel());
			int category = linkNode.getLineType();
			if (category == 0) {
				LOG.error(String.format("link[%s].lineType is 0", linkNode.getId()));
			}
			changeStyle(figure, category);
			changeStatus(figure, linkNode.getStatus());
		}
		setFigure(figure);
		setModel(figureModel);
		figure.setEdit(this);
		return figure;
	}
	
	@Override
	public void updateAttributes() {
		LabeledLinkFigure figure = getFigure();
		LinkEntity linkNode = toModel(getModel());
		if (linkNode == null) {
			LOG.error("实体不能为空");
		} else {			
			int category = linkNode.getLineType();
			int status = linkNode.getStatus();
			figure.willChange();
			linkNode.setModifyLink(true);
			changeStyle(figure, category);
			changeStatus(figure, status);
			figure.changed();
			figure.fireAreaInvalidated();
			figure.fireFigureChanged();
			figure.invalidate();
			figure.validate();
		}
	}

	private void changeStatus(final LabeledLinkFigure figure, final int status) {
		switch (status) {
			case Constants.L_CONNECT:
				figure.linkUp(); break;
			case Constants.L_UNCONNECT:
				figure.linkDown(); break;
			case Constants.L_BLOCK:
				figure.block(); break;
		}
	}
	
	@Override
	public int getStatus() {
		return getModel() == null ? Constants.L_CONNECT : toModel(getModel()).getStatus();
	}
	
	@Override
	public String toString() {
		return getModel() == null ? super.toString() : toModel(getModel()).getId().toString();
	}
	
	@Override
	public int hashCode() {
		return getModel() == null ? super.hashCode() : toModel(getModel()).getGuid().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof SmartLinkEdit) {
			SmartLinkEdit that = (SmartLinkEdit)obj;
			if (getModel() != null && that.getModel() != null) {
				result = toModel(getModel()).getGuid().equals(that.toModel(that.getModel()).getGuid());
			}
		}
		
		return result;
	}
	
	private void changeStyle(LabeledLinkFigure figure, int category) {
		figure.setLiner(new LinearLiner());
		figure.set(START_DECORATION, null);
		figure.set(END_DECORATION, null);
		figure.set(AttributeKeys.STROKE_DASHES, null);
		figure.setFddiImage(null);
		LOG.debug(String.format("连线类型为%s", category));
		
		if (category == LinkCategory.ETHERNET) {
		}
		if (category == LinkCategory.FDDI) {
			figure.setFddiImage(Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.FDDI_DECORATION)));
		}
		if (category == LinkCategory.POWER_LINE) {
			figure.setLiner(new PowerLiner());
		}
		if (category == LinkCategory.WIRELESS) {
			figure.set(AttributeKeys.STROKE_WIDTH, 2d);
			figure.set(AttributeKeys.STROKE_DASHES, new double[]{4d});
		}
		if (category == LinkCategory.SERIAL_LINE) {
			figure.set(START_DECORATION, new SerialLineDecoration());
			figure.set(END_DECORATION, new SerialLineDecoration());
		}
	}
	
	@Override
	public void updateModel() {
		LabeledLinkFigure figure = getFigure();
		LinkEntity linkNode = toModel(getModel());
		if (linkNode == null) {
			LOG.error("实体不能为空");
		} else {
			NodeEntity startNode = null;
			NodeEntity endNode = null;
			if (figure.getStartFigure() instanceof NodeFigure) {
				NodeFigure startFigure = figure.getStartFigure();
				startNode = (NodeEntity)startFigure.getEdit().getModel();
				if (startNode instanceof SubNetTopoNodeEntity) {
					LOG.debug("子网节点不作更新");
				} else {
					linkNode.setNode1(startNode);
				}
			}
			if (figure.getEndFigure() instanceof NodeFigure) {
				NodeFigure endFigure = figure.getEndFigure();
				endNode = (NodeEntity)endFigure.getEdit().getModel();
				if (endNode instanceof SubNetTopoNodeEntity) {
					LOG.debug("子网节点不作更新");
				} else {
					linkNode.setNode2(endNode);
				}
			}
		}
	}

	@Override
	public boolean handleConnected() {
		LabeledLinkFigure figure = getFigure();
		NodeFigure startFigure = figure.getStartFigure();
		NodeFigure endFigure = figure.getEndFigure();
		boolean can = false;
		
		for (ConnectPolicy connectPolicy : connectPolicies) {
			if (startFigure == null || endFigure == null) {
				continue;
			}
			
			if (startFigure.getEdit() == null || endFigure.getEdit() == null) {
				continue;
			}
			if (connectPolicy.canConnect(startFigure, endFigure)) {
				can = true; 
				connectPolicy.connected(figure);
				break;
			}
		}
		
		return can;
	}
	
	@Override
	public boolean canConnect(NodeFigure startFigure, NodeFigure endFigure) {
		boolean can = false;
		
		for (ConnectPolicy connectPolicy : connectPolicies) {
			if (startFigure == null || endFigure == null) {
				continue;
			}
			
			if (startFigure.getEdit() == null || endFigure.getEdit() == null) {
				continue;
			}
			
			if (connectPolicy.canConnect(startFigure, endFigure)) {
				can = true;
				break;
			}
		}
		
		return can;
	}
	
	private void createConnectPolicies() {
		connectPolicies = new ConnectPolicyFactory().createConnectPolicies();
	}

	@Override
	public LabeledLinkFigure getFigure() {
		return figure;
	}

	@Override
	public void setFigure(LabeledLinkFigure figure) {
		this.figure = figure;
	}
	
//	@Override
//	public LinkEntity getModel() {
//		return model;
//	}
	
	public LinkEntity toModel(Object entity) {
		if (entity instanceof LinkEntity) {
			return (LinkEntity)entity;
		} else {
			LOG.error("实体必需是LinkEntity");
			return null;
		}
	}

//	@Override
//	public void setModel(Object entity) {
//		model = toModel(entity);
//	}
	
	@Override
	public void showAlarm(final long value) {
		//traffic monitoring
		if (stopwatch == null) {
			stopwatch = new Stopwatch();
			stopwatch.addObserver(stopwatchObserver);
			new Thread(stopwatch).start();
		} else {
			stopwatch.reset();
		}
		if (stopwatch.isTimeout()) {
			stopwatch = new Stopwatch();
			stopwatch.addObserver(stopwatchObserver);
			new Thread(stopwatch).start();
		}

		invokeShowAlarm(value);
	}
	
	private void invokeShowAlarm(final long value) {
		if (SwingUtilities.isEventDispatchThread()) {
			LinkEntity linkNode = toModel(getModel());
			linkNode.setStatus(Constants.TRAFFIC);
			figure.willChange();
			figure.addFlux(value);
			figure.changed();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					invokeShowAlarm(value);
				}
			});
		}
	}

	@Override
	public void closeAlarm() {
		stopwatch.stop();
		if (SwingUtilities.isEventDispatchThread()) {
			LinkEntity linkNode = toModel(getModel());
			linkNode.setStatus(Constants.L_CONNECT);
			figure.willChange();
			figure.addFlux(0);
			figure.changed();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					closeAlarm();
				}
			});
		}
	}

	private final Observer stopwatchObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			if (stopwatch.isTimeout()) {
				closeAlarm();
				LOG.debug("timeout");
				stopwatch.deleteObserver(stopwatchObserver);
			}
		}
	};

	private List<ConnectPolicy> connectPolicies;
	private Stopwatch stopwatch;
	private LabeledLinkFigure figure;
	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(SmartLinkEdit.class);
	
	private class Stopwatch extends Observable implements Runnable {
		
		@Override
		public void run() {
			while (true) {
				try {
					while (second < max) {
						second++;
						Thread.sleep(interval);
					}
					timeout = true;
					setChanged();
					notifyObservers(timeout);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
			        break;
				}
			}
		}
		
		public void reset() {
			second = 1;
		}
		
		public void stop() {
			timeout = true;
			Thread.currentThread().interrupt();
		}

		public boolean isTimeout() {
			return timeout;
		}

		private boolean timeout;
		private int second;
		private final int max = 10;
		private static final int interval = 1000;
	}
}