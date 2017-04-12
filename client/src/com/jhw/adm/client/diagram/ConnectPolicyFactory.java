package com.jhw.adm.client.diagram;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jhw.adm.client.draw.ConnectPolicy;
import com.jhw.adm.client.draw.LinkFigure;
import com.jhw.adm.client.draw.NodeFigure;
import com.jhw.adm.client.model.LinkCategory;
import com.jhw.adm.client.model.carrier.CarrierPort;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

/**
 * 连接策略工厂
 */
public class ConnectPolicyFactory {

	public List<ConnectPolicy> createConnectPolicies() {
		List<ConnectPolicy> connectPolicies = new LinkedList<ConnectPolicy>();
		
		// 前置机与两层交换机
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof FEPTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeEntity) ||
					(startModel instanceof SwitchTopoNodeEntity &&
					endModel instanceof FEPTopoNodeEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.WIRELESS);
			}
		});
		
		// 前置机与三层交换机
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof SwitchTopoNodeLevel3 &&
					endModel instanceof FEPTopoNodeEntity) ||
					(startModel instanceof FEPTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeLevel3)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.WIRELESS);
			}
		});
		
		// 前置机与载波机
		connectPolicies.add(new FepAndCarrierPolicy());
		
		// 前置机与OLT
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof EponTopoEntity &&
					endModel instanceof FEPTopoNodeEntity) ||
					(startModel instanceof FEPTopoNodeEntity &&
					endModel instanceof EponTopoEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.WIRELESS);
			}
		});
		
		// 两层交换机与两层交换机
		connectPolicies.add(new SwitcherAndSwitcherPolicy());
		
		// 两层交换机与三层交换机
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof SwitchTopoNodeLevel3 &&
					endModel instanceof SwitchTopoNodeEntity) ||
					(startModel instanceof SwitchTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeLevel3)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		
		// 两层交换机与OLT
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof EponTopoEntity &&
					endModel instanceof SwitchTopoNodeEntity) ||
					(startModel instanceof SwitchTopoNodeEntity &&
					endModel instanceof EponTopoEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		// 两层交换机与ONU
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof ONUTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeEntity) ||
					(startModel instanceof SwitchTopoNodeEntity &&
					endModel instanceof ONUTopoNodeEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		
		// 两层交换机与载波机
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof CarrierTopNodeEntity &&
					endModel instanceof SwitchTopoNodeEntity) ||
					(startModel instanceof SwitchTopoNodeEntity &&
					endModel instanceof CarrierTopNodeEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.SERIAL_LINE);
			}
		});
		
		// 两层交换机与GPRS
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof GPRSTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeEntity) ||
					(startModel instanceof SwitchTopoNodeEntity &&
					endModel instanceof GPRSTopoNodeEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.WIRELESS);
			}
		});
		// 三层交换机与OLT
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof EponTopoEntity &&
					endModel instanceof SwitchTopoNodeLevel3) ||
					(startModel instanceof SwitchTopoNodeLevel3 &&
					endModel instanceof EponTopoEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.SERIAL_LINE);
			}
		});	
		// 三层交换机与三层交换机
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if (startModel instanceof SwitchTopoNodeLevel3 &&
					endModel instanceof SwitchTopoNodeLevel3) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		// 三层交换机与ONU
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof ONUTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeLevel3) ||
					(startModel instanceof SwitchTopoNodeLevel3 &&
					endModel instanceof ONUTopoNodeEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		
		// 三层交换机与GPRS
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof GPRSTopoNodeEntity &&
					endModel instanceof SwitchTopoNodeLevel3) ||
					(startModel instanceof SwitchTopoNodeLevel3 &&
					endModel instanceof GPRSTopoNodeEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.WIRELESS);
			}
		});
		// OLT与OLT
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if ((startModel instanceof EponTopoEntity &&
					endModel instanceof EponTopoEntity) ||
					(startModel instanceof FEPTopoNodeEntity &&
					endModel instanceof EponTopoEntity)) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		// OLT与分光器
		connectPolicies.add(new OltAndSplitterPolicy());
		// 分光器与ONU
		connectPolicies.add(new OnuAndSplitterPolicy());
		// ONU与ONU
		connectPolicies.add(new ConnectPolicy() {
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();				
				if (startModel instanceof Epon_S_TopNodeEntity &&
					endModel instanceof Epon_S_TopNodeEntity) {
					return true;
				} else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		// 载波机与载波机
		connectPolicies.add(new CarrierAndCarrierPolicy());
		
		connectPolicies.add(new ConnectPolicy(){
			@Override
			public boolean canConnect(NodeFigure start,
					NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();
				if (startModel instanceof VirtualNodeEntity
						|| endModel instanceof VirtualNodeEntity) {
					return true;
				}else {
					return false;
				}
			}
			@Override
			public void connected(LinkFigure figure) {
				LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
				linkNode.setLineType(LinkCategory.ETHERNET);
			}
		});
		
		connectPolicies.add(new ConnectPolicy() {
			
			@Override
			public void connected(LinkFigure linkFigure) {
				//
			}
			
			@Override
			public boolean canConnect(NodeFigure start, NodeFigure end) {
				Object startModel = start.getEdit().getModel();
				Object endModel = end.getEdit().getModel();
				if (startModel instanceof CommentTopoNodeEntity
						|| endModel instanceof CommentTopoNodeEntity) {
					return false;
				}
				return false;
			}
		});
		
		return connectPolicies;
	}
	private class OnuAndSplitterPolicy implements ConnectPolicy {
		
		@Override
		public boolean canConnect(NodeFigure start, NodeFigure end) {
			Object startModel = start.getEdit().getModel();
			Object endModel = end.getEdit().getModel();
			if ((startModel instanceof ONUTopoNodeEntity &&
				endModel instanceof Epon_S_TopNodeEntity) ||
				(startModel instanceof Epon_S_TopNodeEntity &&
				endModel instanceof ONUTopoNodeEntity)) {
				return true;
			} else {
				return false;
			}
		}
		@Override
		public void connected(LinkFigure figure) {
			LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
			linkNode.setLineType(LinkCategory.FDDI);
			
			ONUTopoNodeEntity onuNode = null;
			ONUEntity onu = null;
			
			if (linkNode.getNode1() instanceof ONUTopoNodeEntity) {
				onuNode = (ONUTopoNodeEntity)linkNode.getNode1();
			} else {
				onuNode = (ONUTopoNodeEntity)linkNode.getNode2();
			}
			
			onu = NodeUtils.getNodeEntity(onuNode).getOnuEntity();
			
			if (onu.getLldpinfos() == null) {
				onu.setLldpinfos(new HashSet<LLDPInofEntity>());
			}
			
			LLDPInofEntity linkInfo = new LLDPInofEntity();
			linkInfo.setLocalSlot(0);
			linkInfo.setLocalPortNo(0);
			
			onu.getLldpinfos().add(linkInfo);
			linkNode.setLldp(linkInfo);
		}
	}
	
	private class OltAndSplitterPolicy implements ConnectPolicy {
		
		@Override
		public boolean canConnect(NodeFigure start, NodeFigure end) {
			Object startModel = start.getEdit().getModel();
			Object endModel = end.getEdit().getModel();				
			if ((startModel instanceof EponTopoEntity &&
				endModel instanceof Epon_S_TopNodeEntity) ||
				(startModel instanceof Epon_S_TopNodeEntity &&
				endModel instanceof EponTopoEntity)) {
				return true;
			} else {
				return false;
			}
		}
		@Override
		public void connected(LinkFigure figure) {
			LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
			linkNode.setLineType(LinkCategory.FDDI);
			
			EponTopoEntity oltNode = null;
			OLTEntity olt = null;
			
			if (linkNode.getNode1() instanceof EponTopoEntity) {
				oltNode = (EponTopoEntity)linkNode.getNode1();
			} else {
				oltNode = (EponTopoEntity)linkNode.getNode2();
			}
			
//			olt = remoteServer.getService().getOLTByIpValue(oltNode.getIpValue());
			olt = NodeUtils.getNodeEntity(oltNode).getOltEntity();
			
			if (olt.getLldpinfos() == null) {
				olt.setLldpinfos(new HashSet<LLDPInofEntity>());
			}
			
			LLDPInofEntity linkInfo = new LLDPInofEntity();
			linkInfo.setLocalIP(olt.getIpValue());
			linkInfo.setLocalSlot(olt.getSlotNum());
			linkInfo.setLocalPortNo(0);
			
			olt.getLldpinfos().add(linkInfo);
			linkNode.setLldp(linkInfo);
		}
	}
	
	private class SwitcherAndSwitcherPolicy implements ConnectPolicy {
		
		@Override
		public boolean canConnect(NodeFigure start, NodeFigure end) {
			Object startModel = start.getEdit().getModel();
			Object endModel = end.getEdit().getModel();
			if (startModel instanceof SwitchTopoNodeEntity &&
				endModel instanceof SwitchTopoNodeEntity) {
				return true;
			} else {
				return false;
			}
		}
		@Override
		public void connected(LinkFigure figure) {
			LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
			linkNode.setLineType(LinkCategory.ETHERNET);
			SwitchTopoNodeEntity startSwitcherNode = (SwitchTopoNodeEntity)linkNode.getNode1();
			SwitchTopoNodeEntity endSwitcherNode = (SwitchTopoNodeEntity)linkNode.getNode2();
			
			SwitchNodeEntity nodeEentity = NodeUtils.getNodeEntity(startSwitcherNode).getNodeEntity();
			
	    	LLDPInofEntity lldp = new LLDPInofEntity();
	    	lldp.setSyschorized(false);
	    	String localAddress = startSwitcherNode.getIpValue();
	    	int localPort = 1;
	    	String remoteAddress = endSwitcherNode.getIpValue();
	    	int remotePort = 1;
	    	lldp.setLocalIP(localAddress);
	    	lldp.setLocalPortNo(localPort);
	    	lldp.setRemoteIP(remoteAddress);
	    	lldp.setRemotePortNo(remotePort);
	    	
	    	if (nodeEentity.getLldpinfos() == null) {
	        	Set<LLDPInofEntity> setOfLLDP = new HashSet<LLDPInofEntity>();
	        	nodeEentity.setLldpinfos(setOfLLDP);
	    	}
	    	nodeEentity.getLldpinfos().add(lldp);
	    	linkNode.setLldp(lldp);
	    	linkNode.setLineType(LinkCategory.ETHERNET);
		}
	}
	
	private class CarrierAndCarrierPolicy implements ConnectPolicy {
		
		@Override
		public boolean canConnect(NodeFigure start, NodeFigure end) {
			Object startModel = start.getEdit().getModel();
			Object endModel = end.getEdit().getModel();				
			if (startModel instanceof CarrierTopNodeEntity &&
				endModel instanceof CarrierTopNodeEntity) {
				return true;
			} else {
				return false;
			}
		}
	    
	    /**
	     * 载波机相连默认使用电力线，通讯端口Acable(5)
	     * <br>
	     * 如果使用串口线，通讯端口DEV1(1)
	     */
		@Override
		public void connected(LinkFigure figure) {
			LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
			linkNode.setLineType(LinkCategory.POWER_LINE);
			CarrierTopNodeEntity startCarrierNode = (CarrierTopNodeEntity)linkNode.getNode1();
    		CarrierTopNodeEntity endCarrierNode = (CarrierTopNodeEntity)linkNode.getNode2();
			
			int startCode = startCarrierNode.getCarrierCode();
			int endCode = endCarrierNode.getCarrierCode();
			
			CarrierEntity startEntity = NodeUtils.getNodeEntity(startCarrierNode).getNodeEntity();
			CarrierEntity endEntity = NodeUtils.getNodeEntity(endCarrierNode).getNodeEntity();
			
			if (startEntity.getRoutes() == null) {
				startEntity.setRoutes(new HashSet<CarrierRouteEntity>());
			}
			if (endEntity.getRoutes() == null) {
				endEntity.setRoutes(new HashSet<CarrierRouteEntity>());
			}
			
			CarrierRouteEntity start2EndRoute = new CarrierRouteEntity();
			start2EndRoute.setCarrierCode(endCode);
			start2EndRoute.setPort(CarrierPort.DEFAULT_POWER_PORT);
			start2EndRoute.setCarrier(startEntity);			
			startEntity.getRoutes().add(start2EndRoute);
			
			CarrierRouteEntity end2StartRoute = new CarrierRouteEntity();
			end2StartRoute.setCarrierCode(startCode);
			end2StartRoute.setPort(CarrierPort.DEFAULT_POWER_PORT);
			end2StartRoute.setCarrier(endEntity);			
			endEntity.getRoutes().add(end2StartRoute);
			
			String frontEndCode = startEntity.getFepCode();
			if (StringUtils.isBlank(frontEndCode)) {
				frontEndCode = endEntity.getFepCode();
				startEntity.setFepCode(frontEndCode);
			} else {
				endEntity.setFepCode(frontEndCode);
			}
			linkNode.setCarrierRoute(start2EndRoute);
		}
	}
	
	private class FepAndCarrierPolicy implements ConnectPolicy {

		@Override
		public boolean canConnect(NodeFigure start, NodeFigure end) {
			Object startModel = start.getEdit().getModel();
			Object endModel = end.getEdit().getModel();				
			if ((startModel instanceof CarrierTopNodeEntity &&
				endModel instanceof FEPTopoNodeEntity) ||
				(startModel instanceof FEPTopoNodeEntity &&
				endModel instanceof CarrierTopNodeEntity)) {
				return true;
			} else {
				return false;
			}
		}
		@Override
		public void connected(LinkFigure figure) {
			LinkEntity linkNode = (LinkEntity)figure.getEdit().getModel();
			linkNode.setLineType(LinkCategory.SERIAL_LINE);
			NodeEntity startNode = linkNode.getNode1();
			NodeEntity endNode = linkNode.getNode2();
			
			FEPTopoNodeEntity frontEndNode = null;
			CarrierTopNodeEntity carrierNode = null;
			if (startNode instanceof FEPTopoNodeEntity &&
				endNode instanceof CarrierTopNodeEntity) {
				frontEndNode = (FEPTopoNodeEntity)startNode;
				carrierNode = (CarrierTopNodeEntity)endNode;
			}
			if (endNode instanceof FEPTopoNodeEntity &&
				startNode instanceof CarrierTopNodeEntity) {
				frontEndNode = (FEPTopoNodeEntity)endNode;
				carrierNode = (CarrierTopNodeEntity)startNode;
			}
			
			if (frontEndNode != null && carrierNode != null) {
				CarrierEntity carrierEntity = NodeUtils.getNodeEntity(carrierNode).getNodeEntity();
				carrierEntity.setFepCode(frontEndNode.getCode());
			}
		}
	}
}