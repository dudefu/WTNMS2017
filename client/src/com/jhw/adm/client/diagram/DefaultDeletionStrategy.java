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
 * ɾ�����˽ڵ�Ĳ���
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
									ClientUtils.getRootFrame(), "�Զ����ֵĽ���������ɾ��", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
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
									ClientUtils.getRootFrame(), "�Զ����ֵ����㽻��������ɾ��", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
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
									ClientUtils.getRootFrame(), "�Զ����ֵ�OLT����ɾ��", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
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
									ClientUtils.getRootFrame(), "�Զ����ֵ�ONU����ɾ��", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
				
				if (equipmentFigure.getEdit().getModel() instanceof Epon_S_TopNodeEntity) {
					Epon_S_TopNodeEntity splitterNode = (Epon_S_TopNodeEntity)equipmentFigure.getEdit().getModel();
					if (splitterNode.getEponSplitter().isSyschorized()) {
						can = false;
						JOptionPane.showMessageDialog(
								ClientUtils.getRootFrame(), "�Զ����ֵķֹ�������ɾ��", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
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
								ClientUtils.getRootFrame(), "�Զ����ֵ��豸���߲���ɾ��", 
								ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
						break;
					}
				}
			}
		}

		/*
		 * ɾ�������������ڵĽڵ㽫���ͷ�
		 */
		// �������������ǿ���ɾ������ʾ�û�ȷ��
		if (can && drawingView.getSelectedFigures().size() > 0) {
			
			int selectedFigureStatus = getSelectedFigureStatus();
			int option = JOptionPane.CLOSED_OPTION;
			if(NODE_ONLY == selectedFigureStatus){
				option = JOptionPane.showConfirmDialog(ClientUtils.getRootFrame(), 
						"ȷ���Ƿ�ɾ����ѡ�ڵ㣿", 
						ClientUtils.getAppName(), 
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE);
			}else if(LINK_ONLY == selectedFigureStatus){
				option = JOptionPane.showConfirmDialog(ClientUtils.getRootFrame(), 
						"ȷ���Ƿ�ɾ����ѡ���ߣ�", 
						ClientUtils.getAppName(), 
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE);
			}else if(NODE_AND_LINK == selectedFigureStatus){
				option = JOptionPane.showConfirmDialog(ClientUtils.getRootFrame(), 
						"ȷ���Ƿ�ɾ����ѡ�ڵ������ߣ�", 
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
	
	private final int NODE_ONLY = 1;//ȫΪ�豸�ڵ�
	private final int LINK_ONLY = 2;//ȫΪ���߽ڵ�
	private final int NODE_AND_LINK = 3;//�豸�ڵ���豸����
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
				// �����ǰ�û�������Ϊɾ��
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