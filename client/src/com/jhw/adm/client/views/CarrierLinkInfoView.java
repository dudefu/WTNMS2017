package com.jhw.adm.client.views;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.carrier.CarrierCategory;
import com.jhw.adm.client.model.carrier.CarrierPort;
import com.jhw.adm.client.model.carrier.CarrierPortCategory;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(CarrierLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class CarrierLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "carrierLinkInfoView";
	
	@Resource(name=CarrierPortCategory.ID)
	private CarrierPortCategory carrierPortCategory;
	
	@Resource(name=CarrierCategory.ID)
	private CarrierCategory carrierCategory;
	
	@Resource(name=CarrierPort.ID)
	private CarrierPort carrierPort;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private JTextField startNameFld;
	private JTextField startCategoryFld;
	private JComboBox startPortFld;
	private JTextField startPortCategoryFld;
	
	private JTextField endNameFld;
	private JTextField endCategoryFld;
	private JComboBox endPortFld;
	private JTextField endPortCategoryFld;
	
	private final JPanel startParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	private final JPanel endParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	private CarrierRouteEntity startRout = null;
	private CarrierRouteEntity endRout = null;
	
	private ListComboBoxModel portBoxModel;
	
	@Override
	public void buildPanel(boolean isStart) {
		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		
		JPanel container = new JPanel(new SpringLayout());
		
		portBoxModel = new ListComboBoxModel(carrierPort.toList());;
		
		if(isStart){
			container.setBorder(BorderFactory.createTitledBorder("起始设备"));
			startParent.add(container);
			
			container.add(new JLabel("设备ID"));
			startNameFld = new JTextField(25);
			startNameFld.setEditable(false);
			container.add(startNameFld);
			
			container.add(new JLabel("设备类型"));
			startCategoryFld = new JTextField(25);
			startCategoryFld.setEditable(false);
			container.add(startCategoryFld);
					
			container.add(new JLabel("端口"));
			startPortFld = new JComboBox();
			startPortFld.setEditable(false);
			startPortFld.setModel(portBoxModel);
			container.add(startPortFld);
			
//			container.add(new JLabel("端口类型"));
//			startPortCategoryFld = new JTextField(20);
//			startPortCategoryFld.setEditable(false);
//			container.add(startPortCategoryFld);
		}else{
			container.setBorder(BorderFactory.createTitledBorder("末端设备"));
			endParent.add(container);
			
			container.add(new JLabel("设备ID"));
			endNameFld = new JTextField(25);
			endNameFld.setEditable(false);
			container.add(endNameFld);
			
			container.add(new JLabel("设备类型"));
			endCategoryFld = new JTextField(25);
			endCategoryFld.setEditable(false);
			container.add(endCategoryFld);
					
			container.add(new JLabel("端口"));
			endPortFld = new JComboBox();
			endPortFld.setEditable(false);
			endPortFld.setModel(portBoxModel);
			container.add(endPortFld);
			
//			container.add(new JLabel("端口类型"));
//			endPortCategoryFld = new JTextField(20);
//			endPortCategoryFld.setEditable(false);
//			container.add(endPortCategoryFld);
		}
		SpringUtilities.makeCompactGrid(container, 3, 2, 6, 6, 30, 6);
	}
	
	public JPanel getStartPanel(){
		return startParent;
	}

	public JPanel getEndPanel(){
		return endParent;
	}
	
	public int getStartRoutePort(){
		return ((StringInteger)startPortFld.getSelectedItem()).getValue();
	}
	
	public void saveRoutePort(){
		this.startRout.setPort(((StringInteger)startPortFld.getSelectedItem()).getValue());
		this.endRout.setPort(((StringInteger)endPortFld.getSelectedItem()).getValue());
		
		List<CarrierRouteEntity> routeList = new ArrayList<CarrierRouteEntity>();
		routeList.add(this.startRout);
		routeList.add(this.endRout);
		remoteServer.getService().updateEntities(routeList);
	}
	
	private void setPortCombox(CarrierEntity carrierEntity){
		if (carrierEntity.getMarking() == Constants.SINGLECHANNEL){
			List<StringInteger> lists = carrierPort.toList();
			
			Iterator iterator = lists.iterator();
			while(iterator.hasNext()){
				StringInteger stringInteger = (StringInteger)iterator.next();
				if (stringInteger.getKey().equalsIgnoreCase("Acable2")){
					iterator.remove();
				}
			}
			portBoxModel = new ListComboBoxModel(lists);
		}
	}
	
	private NodeEntity nodeEntity;
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {

		if(isStart){
			nodeEntity = linkNode.getNode1();
		}else{
			nodeEntity = linkNode.getNode2();
		}
		
		CarrierEntity carrierEntity = remoteServer.getService().getCarrierByCode(
						((CarrierTopNodeEntity) nodeEntity).getCarrierCode());
		if (carrierEntity != null) {
//			setPortCombox(carrierEntity);
			if(isStart){
				startNameFld.setText(Integer.toString(carrierEntity.getCarrierCode()));
				startCategoryFld.setText(carrierCategory.get(carrierEntity.getType()).getKey());
				
				CarrierEntity nextCarrier = null;
				if(linkNode.getNode2() instanceof CarrierTopNodeEntity){
					nextCarrier = remoteServer.getService().getCarrierByCode(
							((CarrierTopNodeEntity)(linkNode.getNode2())).getCarrierCode());
				}
				
				if (nextCarrier != null) {
					CarrierRouteEntity nextRoute = null;
					for (CarrierRouteEntity route : carrierEntity.getRoutes()) {
						if (route.getCarrierCode() == nextCarrier.getCarrierCode()) {
							nextRoute = route;
							this.startRout = route;
						}
					}
					if (nextRoute != null) {
//						startPortFld.setSelectedItem(carrierPort.get((nextRoute.getPort())).getKey());
						startPortFld.setSelectedItem(carrierPort.get((nextRoute.getPort())));
//						startPortCategoryFld.setText(carrierPortCategory.get(
//								nextRoute.getPort()).getKey());
					}
				}
			}else{
				endNameFld.setText(Integer.toString(carrierEntity.getCarrierCode()));
				endCategoryFld.setText(carrierCategory.get(carrierEntity.getType()).getKey());
				CarrierEntity nextCarrier = null;
				if(linkNode.getNode1() instanceof CarrierTopNodeEntity){
					nextCarrier = remoteServer.getService().getCarrierByCode(
							((CarrierTopNodeEntity)(linkNode.getNode1())).getCarrierCode());
				}
				if (nextCarrier != null) {
					CarrierRouteEntity nextRoute = null;
					for (CarrierRouteEntity route : carrierEntity.getRoutes()) {
						if (route.getCarrierCode() == nextCarrier.getCarrierCode()) {
							nextRoute = route;
							this.endRout = route;
						}
					}
					if (nextRoute != null) {
						endPortFld.setSelectedItem(carrierPort.get((nextRoute.getPort())));
//						endPortCategoryFld.setText(carrierPortCategory.get(
//								nextRoute.getPort()).getKey());
					}
				}
			}
		}
	}
}
