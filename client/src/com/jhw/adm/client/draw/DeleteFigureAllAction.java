package com.jhw.adm.client.draw;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.gui.EditableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

public class DeleteFigureAllAction extends AbstractAction {
	
    public DeleteFigureAllAction() {
        super(ID);
        remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
        equipmentModel = ClientUtils.getSpringBean(EquipmentModel.ID);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean existsSubnet = false;
        if (drawingView != null && drawingView.isEnabled() && canDelete()) {
            if (drawingView instanceof org.jhotdraw.gui.EditableComponent) {
            	for (Figure figure : drawingView.getSelectedFigures()) {
            		if (figure instanceof NodeFigure) {
            			NodeFigure equipmentFigure = (NodeFigure)figure;
            			Object object = equipmentFigure.getEdit().getModel();
            			if(object instanceof NodeEntity){
            				NodeEntity nodeEntity = (NodeEntity)equipmentFigure.getEdit().getModel();
            				if(null != nodeEntity.getId()){
        						if(isBlankIP(nodeEntity)){
        							remoteServer.getNmsService().deleteAllNode(nodeEntity);
        							NodeUtils.saveDeleteLog("删除节点" + NodeUtils.getNodeText(nodeEntity));
        							LOG.info("删除节点" + NodeUtils.getNodeText(nodeEntity));
        						}
        						if(nodeEntity instanceof SubNetTopoNodeEntity){
        							existsSubnet = true;
        						}
            				}
            			}else if(object instanceof LinkEntity) {
            				LinkEntity linkEntity = (LinkEntity)equipmentFigure.getEdit().getModel();
            				if(null != linkEntity.getId()){
            					remoteServer.getNmsService().deleteLinkEntity(linkEntity);
            					NodeUtils.saveDeleteLog("删除连线");
            					LOG.info("删除连线");
            				}
            			}
            		}
            	}
            	((EditableComponent) drawingView).delete();
            	if(existsSubnet == true){
//					JOptionPane.showMessageDialog(ClientUtils.getRootFrame(),
//							"删除子网后该子网内的节点、连线及子网将被释放", ClientUtils.getAppName(),
//							JOptionPane.NO_OPTION);
					equipmentModel.requireRefresh();
            	}
            } else {
            	Toolkit.getDefaultToolkit().beep();
            }
        }
	}
	
	private boolean isBlankIP(NodeEntity nodeEntity){
		if(nodeEntity instanceof SwitchTopoNodeEntity){
			String deleteIp = ((SwitchTopoNodeEntity)nodeEntity).getIpValue();
			if(StringUtils.isBlank(deleteIp)){
				return false;
			}
			for(NodeEntity entity : equipmentModel.getDiagram().getNodes()){
				if(entity instanceof SwitchTopoNodeEntity){//完全删除交换机
					if(deleteIp.equals(((SwitchTopoNodeEntity)entity).getIpValue())){
						equipmentModel.removeNode(entity);
						break;
					}
				}
			}
		}
		if(nodeEntity instanceof VirtualNodeEntity){
			String deleteIp = ((VirtualNodeEntity)nodeEntity).getIpValue();
			if(StringUtils.isBlank(deleteIp)){
				return false;
			}
			for(NodeEntity entity : equipmentModel.getDiagram().getNodes()){
				if(entity instanceof VirtualNodeEntity){//完全删除虚拟网元
					if(deleteIp.equals(((VirtualNodeEntity)entity).getIpValue())){
						equipmentModel.removeNode(entity);
						break;
					}
				}
			}
		}
		return true;
	}
	
	private boolean canDelete() {
		return deletionStrategy == null ? false : deletionStrategy.canDelete();
	}
	
    public DeletionStrategy getDeletionStrategy() {
		return deletionStrategy;
	}

	public void setDeletionStrategy(DeletionStrategy deletionStrategy) {
		this.deletionStrategy = deletionStrategy;
	}

    public DrawingView getDrawingView() {
		return drawingView;
	}

	public void setDrawingView(DrawingView drawingView) {
		this.drawingView = drawingView;
        if (drawingView != null) {
            propertyHandler = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("enabled")) {
                        setEnabled((Boolean) evt.getNewValue());
                    }
                }
            };
            drawingView.addPropertyChangeListener(new WeakPropertyChangeListener(propertyHandler));
        }
	}
	
	private DeletionStrategy deletionStrategy;
	private DrawingView drawingView;
	private PropertyChangeListener propertyHandler;
	private RemoteServer remoteServer;
	private EquipmentModel equipmentModel;
	private static final Logger LOG = LoggerFactory.getLogger(DeleteFigureAllAction.class);
	private static final long serialVersionUID = 8527094665854311244L;
    public final static String ID = "edit.delete";
}