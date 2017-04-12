package com.jhw.adm.client.diagram;

import javax.swing.JOptionPane;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.draw.CommentAreaFigure;
import com.jhw.adm.client.draw.DeletionStrategy;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.draw.LabeledLinkFigure;
import com.jhw.adm.client.draw.NodeFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;

/**
 * 删除拓扑节点的策略
 */
public class DefaultDeletionStrategy implements DeletionStrategy {
	
	public DefaultDeletionStrategy(DrawingView drawingView) {
		this.drawingView = drawingView;
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	@Override
	public boolean canDelete() {		
		boolean can = true;
		for (Figure figure : drawingView.getSelectedFigures()) {
			if (figure instanceof NodeFigure) {
				NodeFigure equipmentFigure = (NodeFigure)figure;
				if (equipmentFigure.getEdit().getModel() instanceof SwitchTopoNodeEntity) {
					SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)equipmentFigure.getEdit().getModel();
					SwitchNodeEntity nodeEntity = remoteServer.getService().getSwitchByIp(switcherNode.getIpValue());
					if(nodeEntity != null){
						if (nodeEntity.isSyschorized()) {
							can = false;
							JOptionPane.showMessageDialog(
									ClientUtils.getRootFrame(), "自动发现的交换机不能删除", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
				
				if (equipmentFigure.getEdit().getModel() instanceof SwitchTopoNodeLevel3) {
					SwitchTopoNodeLevel3 switcherLayer3Node = (SwitchTopoNodeLevel3)equipmentFigure.getEdit().getModel();
					SwitchLayer3 switcherLayer3 = remoteServer.getService().getSwitcher3ByIP(switcherLayer3Node.getIpValue());
					if(switcherLayer3 != null){
						if (switcherLayer3.isSyschorized()) {
							can = false;
							JOptionPane.showMessageDialog(
									ClientUtils.getRootFrame(), "自动发现的三层交换机不能删除", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
				
				if (equipmentFigure.getEdit().getModel() instanceof EponTopoEntity) {
					EponTopoEntity oltNode = (EponTopoEntity)equipmentFigure.getEdit().getModel();
					OLTEntity oltEntity = remoteServer.getService().getOLTByIpValue(oltNode.getIpValue());
					if(oltEntity != null){
						if (oltEntity.isSyschorized()) {
							can = false;
							JOptionPane.showMessageDialog(
									ClientUtils.getRootFrame(), "自动发现的OLT不能删除", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
				
				if (equipmentFigure.getEdit().getModel() instanceof ONUTopoNodeEntity) {
					ONUTopoNodeEntity onuNode = (ONUTopoNodeEntity)equipmentFigure.getEdit().getModel();
					ONUEntity onuEntity = remoteServer.getService().getOnuByMacValue(onuNode.getMacValue());
					if(onuEntity != null){
						if (onuEntity.isSyschorized()) {
							can = false;
							JOptionPane.showMessageDialog(
									ClientUtils.getRootFrame(), "自动发现的ONU不能删除", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
				
				if (equipmentFigure.getEdit().getModel() instanceof Epon_S_TopNodeEntity) {
					Epon_S_TopNodeEntity splitterNode = (Epon_S_TopNodeEntity)equipmentFigure.getEdit().getModel();
					if (splitterNode.getEponSplitter().isSyschorized()) {
						can = false;
						JOptionPane.showMessageDialog(
								ClientUtils.getRootFrame(), "自动发现的分光器不能删除", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
						break;
					}
				}
			}
			if (figure instanceof LabeledLinkFigure) {
				LabeledLinkFigure linkFigure = (LabeledLinkFigure)figure;
				if (linkFigure.getEdit().getModel() instanceof LinkEntity) {
					LinkEntity linkNode = (LinkEntity)linkFigure.getEdit().getModel();

					if (linkNode.getLldp() != null && linkNode.getLldp().isSyschorized()) {
						can = false;
						JOptionPane.showMessageDialog(
								ClientUtils.getRootFrame(), "自动发现的设备连线不能删除", 
								ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
						break;
					}
				}
			}
		}

		/*
		 * 删除子网后子网内的节点将被释放
		 */
		// 如果上面的条件是可以删除，提示用户确认
		if (can && drawingView.getSelectedFigures().size() > 0) {
			
			int selectedFigureStatus = getSelectedFigureStatus();
			int option = JOptionPane.CLOSED_OPTION;
			if(NODE_ONLY == selectedFigureStatus){
				option = JOptionPane.showConfirmDialog(ClientUtils.getRootFrame(), 
						"确定是否删除所选节点？", 
						ClientUtils.getAppName(), 
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE);
			}else if(LINK_ONLY == selectedFigureStatus){
				option = JOptionPane.showConfirmDialog(ClientUtils.getRootFrame(), 
						"确定是否删除所选连线？", 
						ClientUtils.getAppName(), 
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE);
			}else if(NODE_AND_LINK == selectedFigureStatus){
				option = JOptionPane.showConfirmDialog(ClientUtils.getRootFrame(), 
						"确定是否删除所选节点与连线？", 
						ClientUtils.getAppName(), 
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE);
			}
			if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
				can = false;
			} else if (option == JOptionPane.OK_OPTION) {
				deleteFepNode();
			}
		}
		
		return can;
	}
	
	private final int NODE_ONLY = 1;//全为设备节点
	private final int LINK_ONLY = 2;//全为连线节点
	private final int NODE_AND_LINK = 3;//设备节点和设备连线
	private int getSelectedFigureStatus(){
		int nodeCount = 0;
		int linkCount = 0;
		if (drawingView.getSelectedFigures().size() > 0) {
			for (Figure figure : drawingView.getSelectedFigures()) {
				if((figure instanceof EquipmentFigure) || (figure instanceof CommentAreaFigure)){
					nodeCount += 1;
				}else if(figure instanceof LabeledLinkFigure){
					linkCount += 1;
				}
			}
		}
		if(nodeCount > 0 && linkCount > 0){
			return NODE_AND_LINK;
		}else if(nodeCount > 0 && linkCount == 0){
			return NODE_ONLY;
		}else if(nodeCount == 0 && linkCount > 0) {
			return LINK_ONLY;
		}
		return -1;
	}
	
	private void deleteFepNode() {
		for (Figure figure : drawingView.getSelectedFigures()) {
			if (figure instanceof NodeFigure) {
				NodeFigure equipmentFigure = (NodeFigure)figure;
				// 如果是前置机，则标记为删除
				if (equipmentFigure.getEdit().getModel() instanceof FEPTopoNodeEntity) {
					FEPTopoNodeEntity fepNode = (FEPTopoNodeEntity)equipmentFigure.getEdit().getModel();
					fepNode.setSynchorized(false);
				}
			}
		}
	}
	
	private final RemoteServer remoteServer;
	private final DrawingView drawingView;
}