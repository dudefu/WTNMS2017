package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.OK;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.LinkCategory;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.swing.JCloseButton;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

/** 拓扑连线详细信息视图 */
@Component(LinkDetailView.ID)
@Scope(Scopes.DESKTOP)
public class LinkDetailView extends ViewPart {
	
	@PostConstruct
	protected void initialize() {
		setTitle("连线详细信息");
		buttonFactory = actionManager.getButtonFactory(this);
		linkNode = (LinkEntity) adapterManager.getAdapter(equipmentModel
				.getLastSelected(), LinkEntity.class);
		createContents(this);
		fillContents();
		setViewSize(300, startPanel.getPreferredSize().height + endPanel.getPreferredSize().height
				+ linkPanel.getPreferredSize().height + 30);
	}
	
	protected void fillContents() {
		
		if (linkNode != null) {

			LOG.debug(String.format("填充连线信息-Start:%s; End:%s", 
					startNode.getClass().getName(), endNode.getClass().getCanonicalName()));
			
			if (startNode instanceof FEPTopoNodeEntity) {// 前置机
				frontEndLinkInfoView.fillNode(linkNode, true);
			} else if (startNode instanceof SwitchTopoNodeEntity) {//二层交换机
				switcherLinkInfoView.fillNode(linkNode, true);
			} else if (startNode instanceof GPRSTopoNodeEntity) {//GPRS
				gprsLinkInfoView.fillNode(linkNode, true);
			} else if (startNode instanceof CarrierTopNodeEntity) {//载波机
				carrierLinkInfoView.fillNode(linkNode, true);
			} else if(startNode instanceof SwitchTopoNodeLevel3){//三层交换机
				switcherThreeLinkInfoView.fillNode(linkNode, true);
			} else if(startNode instanceof EponTopoEntity){//OLT
				oltLinkInfoView.fillNode(linkNode, true);
			} else if(startNode instanceof ONUTopoNodeEntity){//ONU
				onuLinkInfoView.fillNode(linkNode, true);
			} else if(startNode instanceof Epon_S_TopNodeEntity){//OPTICAL SPLITTER
				opticalSplitterLinkInfoView.fillNode(linkNode, true);
			} else if(startNode instanceof SubNetTopoNodeEntity){//子网
				subnetLinkInfoView.fillNode(linkNode, true);
			} else if(startNode instanceof VirtualNodeEntity){
				virtualElementLinkInfoView.fillNode(linkNode, true);
			}
			
			if (endNode instanceof SwitchTopoNodeEntity) {//二层交换机
				switcherLinkInfoView.fillNode(linkNode, false);
			} else if (endNode instanceof GPRSTopoNodeEntity) {//GPRS
				gprsLinkInfoView.fillNode(linkNode, false);
			} else if (endNode instanceof CarrierTopNodeEntity) {//载波机
				carrierLinkInfoView.fillNode(linkNode, false);
			} else if(endNode instanceof FEPTopoNodeEntity){//前置机
				frontEndLinkInfoView.fillNode(linkNode, false);
			} else if(endNode instanceof SwitchTopoNodeLevel3){//三层交换机
				switcherThreeLinkInfoView.fillNode(linkNode, false);
			} else if(endNode instanceof EponTopoEntity){//OLT
				oltLinkInfoView.fillNode(linkNode, false);
			} else if(endNode instanceof ONUTopoNodeEntity){//ONU
				onuLinkInfoView.fillNode(linkNode, false);
			} else if(endNode instanceof Epon_S_TopNodeEntity){//OPTICAL SPLITTER
				opticalSplitterLinkInfoView.fillNode(linkNode, false);
			} else if(endNode instanceof SubNetTopoNodeEntity){//子网
				subnetLinkInfoView.fillNode(linkNode, false);
			}  else if(endNode instanceof VirtualNodeEntity){
				virtualElementLinkInfoView.fillNode(linkNode, false);
			}
			
			if (startNode instanceof SwitchTopoNodeEntity &&
				endNode instanceof SwitchTopoNodeEntity) {
				switcherLinkInfoView.setEditable();
			}

			fillLinkNode(linkNode);
		} 
	}
	
	protected void fillLinkNode(LinkEntity linkNode) {
//		if(startNode instanceof SubNetTopoNodeEntity ||
//				endNode instanceof SubNetTopoNodeEntity) {
//			linkCategoryBox.setEnabled(false);
//		}
		linkCategoryBox.setSelectedItem(linkCategory.get(linkNode.getLineType()));
	}
	private JPanel centerPanel;
	private JPanel linkPanel;
	protected void createContents(JPanel parent) {
		setLayout(new BorderLayout());
		centerPanel = new JPanel();
		createCenter(centerPanel);
		add(centerPanel, BorderLayout.CENTER);
		linkPanel = new JPanel();
		createLinkPanel(linkPanel);
		add(linkPanel, BorderLayout.PAGE_END);
	}
	
	private void createLinkPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		linkPanel.setBorder(BorderFactory.createTitledBorder("连线信息"));
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		parent.add(linkPanel, BorderLayout.CENTER);
		parent.add(toolPanel, BorderLayout.PAGE_END);
		JPanel linkContainer = new JPanel(new SpringLayout());
		linkPanel.add(linkContainer);
		linkCategoryBox = new JComboBox();
		
		if(startNode instanceof SubNetTopoNodeEntity || endNode instanceof SubNetTopoNodeEntity){
			List<StringInteger> list = new ArrayList<StringInteger>(1);
			list.add(linkCategory.get(this.linkNode.getLineType()));
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(list);
			linkCategoryBox.setModel(linkCategoryBoxModel);
		}else if(startNode instanceof GPRSTopoNodeEntity || endNode instanceof GPRSTopoNodeEntity){//无线(GPRS)
			List<StringInteger> list = new ArrayList<StringInteger>(1);
			list.add(linkCategory.get(3));
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(Collections.unmodifiableList(list));
			linkCategoryBox.setModel(linkCategoryBoxModel);
		}else if(startNode instanceof CarrierTopNodeEntity || endNode instanceof CarrierTopNodeEntity){//载波机
			List<StringInteger> list = null;
			if(startNode instanceof CarrierTopNodeEntity && endNode instanceof CarrierTopNodeEntity){//电力线 & 串口线
				list = new ArrayList<StringInteger>(2);
				list.add(linkCategory.get(2));
				list.add(linkCategory.get(4));
			}else{//串口线
				list = new ArrayList<StringInteger>(1);
				list.add(linkCategory.get(4));
			}
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(Collections.unmodifiableList(list));
			linkCategoryBox.setModel(linkCategoryBoxModel);
		} else if (startNode instanceof SwitchTopoNodeEntity
				|| endNode instanceof SwitchTopoNodeEntity
				|| startNode instanceof SwitchTopoNodeLevel3
				|| endNode instanceof SwitchTopoNodeLevel3) {// 二层交换机 or 三层交换机
			List<StringInteger> list = null;
			if ((startNode instanceof SwitchTopoNodeEntity && endNode instanceof SwitchTopoNodeEntity)
					|| (startNode instanceof SwitchTopoNodeLevel3 && endNode instanceof SwitchTopoNodeLevel3)
					|| (startNode instanceof SwitchTopoNodeEntity && endNode instanceof VirtualNodeEntity)
					|| (startNode instanceof VirtualNodeEntity && endNode instanceof SwitchTopoNodeEntity)) {// 网线，光纤
				list = new ArrayList<StringInteger>(2);
				list.add(linkCategory.get(1));
				list.add(linkCategory.get(5));
			}else{//网线
				list = new ArrayList<StringInteger>(1);
				list.add(linkCategory.get(1));
			}
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(Collections.unmodifiableList(list));
			linkCategoryBox.setModel(linkCategoryBoxModel);
		} else if(startNode instanceof Epon_S_TopNodeEntity || endNode instanceof Epon_S_TopNodeEntity){//分光器
			List<StringInteger> list = new ArrayList<StringInteger>(1);
			list.add(linkCategory.get(5));
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(Collections.unmodifiableList(list));
			linkCategoryBox.setModel(linkCategoryBoxModel);
		} else if(startNode instanceof EponTopoEntity || endNode instanceof EponTopoEntity){//OLT
			List<StringInteger> list = new ArrayList<StringInteger>(1);
			list.add(linkCategory.get(LinkCategory.ETHERNET));
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(Collections.unmodifiableList(list));
			linkCategoryBox.setModel(linkCategoryBoxModel);
		}else if(startNode instanceof VirtualNodeEntity || endNode instanceof VirtualNodeEntity){
			List<StringInteger> list = new ArrayList<StringInteger>(2);
			list.add(linkCategory.get(1));
			list.add(linkCategory.get(5));
			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(Collections.unmodifiableList(list));
			linkCategoryBox = new JComboBox(linkCategoryBoxModel);
		}
		linkCategoryBox.setPreferredSize(new Dimension(100, linkCategoryBox.getPreferredSize().height));
		linkCategoryBox.setEditable(false);
		linkContainer.add(new JLabel("类型"));
		linkContainer.add(linkCategoryBox);

		SpringUtilities.makeCompactGrid(linkContainer, 1, 2, 6, 6, 10, 6);
		JButton okButton = buttonFactory.createButton(OK);
		closeButton = buttonFactory.createCloseButton();
		setCloseButton(closeButton);
		toolPanel.add(okButton);
		toolPanel.add(closeButton);
	}
	
	@ViewAction(icon=ButtonConstants.SAVE,desc="保存连线详细信息",role=Constants.MANAGERCODE)
	public void ok() {
		int selectedCategory = ((StringInteger)linkCategoryBox.getSelectedItem()).getValue();
		linkNode.setLineType(selectedCategory);

		if (startNode instanceof SwitchTopoNodeEntity &&
			endNode instanceof SwitchTopoNodeEntity) {
			linkNode.getLldp().setLocalPortNo(Integer.parseInt(switcherLinkInfoView.getNewStartPort()));
			linkNode.getLldp().setRemotePortNo(Integer.parseInt(switcherLinkInfoView.getNewEndPort()));
		} else if (startNode instanceof CarrierTopNodeEntity
				&& endNode instanceof CarrierTopNodeEntity) {
			carrierLinkInfoView.saveRoutePort();
//			int portNo = carrierLinkInfoView.getStartRoutePort();
//			linkNode.getCarrierRoute().setPort(portNo);
		}
		
		equipmentModel.fireEquipmentUpdated(linkNode);
		closeButton.fireActionEvent();
	}
	
	private JPanel startPanel;
	private JPanel endPanel;
	private void createCenter(JPanel parent) {
//		parent.setLayout(new GridLayout(2, 0, 5, 0));
		parent.setLayout(new BorderLayout());
		
		startNode = linkNode.getNode1();
		endNode = linkNode.getNode2();
		
		LOG.debug(String.format("构建连线信息面板-Start:%s; End:%s", 
				startNode.getClass().getName(), endNode.getClass().getCanonicalName()));
		
		if (startNode instanceof FEPTopoNodeEntity) {// 前置机
			frontEndLinkInfoView.buildPanel(true);
			startPanel = frontEndLinkInfoView.getNodePanel();
		} else if (startNode instanceof SwitchTopoNodeEntity) {//交换机
			switcherLinkInfoView.buildPanel(true);
			startPanel = switcherLinkInfoView.getStartPanel();
		} else if (startNode instanceof GPRSTopoNodeEntity) {//GPRS
			gprsLinkInfoView.buildPanel(true);
			startPanel = gprsLinkInfoView.getNodePanel();
		} else if (startNode instanceof CarrierTopNodeEntity) {//载波机
			carrierLinkInfoView.buildPanel(true);
			startPanel = carrierLinkInfoView.getStartPanel();
		}else if(startNode instanceof ONUTopoNodeEntity){//ONU
			onuLinkInfoView.buildPanel(true);
			startPanel = onuLinkInfoView.getNodePanel();
		}else if(startNode instanceof EponTopoEntity){//OLT
			oltLinkInfoView.buildPanel(true);
			startPanel = oltLinkInfoView.getStartPanel();
		}else if(startNode instanceof Epon_S_TopNodeEntity){//分光器
			opticalSplitterLinkInfoView.buildPanel(true);
			startPanel = opticalSplitterLinkInfoView.getNodePanel();
		}else if(startNode instanceof SwitchTopoNodeLevel3){//三层交换机
			switcherThreeLinkInfoView.buildPanel(true);
			startPanel = switcherThreeLinkInfoView.getStartPanel();
		}else if(startNode instanceof SubNetTopoNodeEntity){//子网
			subnetLinkInfoView.buildPanel(true);
			startPanel = subnetLinkInfoView.getNodePanel();
		}else if(startNode instanceof VirtualNodeEntity){//虚拟网元
			virtualElementLinkInfoView.buildPanel(true);
			startPanel = virtualElementLinkInfoView.getStartParent();
		}
		
		if (endNode instanceof SwitchTopoNodeEntity) {//交换机
			switcherLinkInfoView.buildPanel(false);
			endPanel = switcherLinkInfoView.getEndPanel();
		} else if (endNode instanceof GPRSTopoNodeEntity) {//GPRS
			gprsLinkInfoView.buildPanel(false);
			endPanel = gprsLinkInfoView.getNodePanel();
		} else if (endNode instanceof CarrierTopNodeEntity) {//载波机
			carrierLinkInfoView.buildPanel(false);
			endPanel = carrierLinkInfoView.getEndPanel();
		} else if(endNode instanceof FEPTopoNodeEntity){//前置机
			frontEndLinkInfoView.buildPanel(false);
			endPanel = frontEndLinkInfoView.getNodePanel();
		}else if(endNode instanceof ONUTopoNodeEntity){//ONU
			onuLinkInfoView.buildPanel(false);
			endPanel = onuLinkInfoView.getNodePanel();
		}else if(endNode instanceof EponTopoEntity){//OLT
			oltLinkInfoView.buildPanel(false);
			endPanel = oltLinkInfoView.getEndPanel();
		}else if(endNode instanceof Epon_S_TopNodeEntity){//分光器
			opticalSplitterLinkInfoView.buildPanel(false);
			endPanel = opticalSplitterLinkInfoView.getNodePanel();
		}else if(endNode instanceof SwitchTopoNodeLevel3){//三层交换机
			switcherThreeLinkInfoView.buildPanel(false);
			endPanel = switcherThreeLinkInfoView.getEndPanel();
		}else if(endNode instanceof SubNetTopoNodeEntity){//子网
			subnetLinkInfoView.buildPanel(false);
			endPanel = subnetLinkInfoView.getNodePanel();
		}else if(endNode instanceof VirtualNodeEntity){//虚拟网元
			virtualElementLinkInfoView.buildPanel(false);
			endPanel = virtualElementLinkInfoView.getEndParent();
		}
		
		parent.add(startPanel,BorderLayout.NORTH);
		parent.add(endPanel,BorderLayout.SOUTH);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	private JCloseButton closeButton;
	private ButtonFactory buttonFactory;
	
	@Resource(name=FrontEndLinkInfoView.ID)
	private FrontEndLinkInfoView frontEndLinkInfoView;
	
	@Resource(name=SwitcherLinkInfoView.ID)
	private SwitcherLinkInfoView switcherLinkInfoView;
	
	@Resource(name=CarrierLinkInfoView.ID)
	private CarrierLinkInfoView carrierLinkInfoView;
	
	@Resource(name=GPRSLinkInfoView.ID)
	private GPRSLinkInfoView gprsLinkInfoView;
	
	@Resource(name=ONULinkInfoView.ID)
	private ONULinkInfoView onuLinkInfoView;
	
	@Resource(name=OLTLinkInfoView.ID)
	private OLTLinkInfoView oltLinkInfoView;
	
	@Resource(name=OpticalSplitterLinkInfoView.ID)
	private OpticalSplitterLinkInfoView opticalSplitterLinkInfoView;
	
	@Resource(name=SwitcherThreeLinkInfoView.ID)
	private SwitcherThreeLinkInfoView switcherThreeLinkInfoView;
	
	@Resource(name=SubnetLinkInfoView.ID)
	private SubnetLinkInfoView subnetLinkInfoView;
	
	@Resource(name=VirtualElementLinkInfoView.ID)
	private VirtualElementLinkInfoView virtualElementLinkInfoView;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=LinkCategory.ID)
	private LinkCategory linkCategory;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	private LinkEntity linkNode;
	private JComboBox linkCategoryBox;
	
	private NodeEntity startNode;
	private NodeEntity endNode;
	
	private static final long serialVersionUID = -7404359366467597532L;
	public static final String ID = "linkDetailView";

	private static final Logger LOG = LoggerFactory.getLogger(LinkDetailView.class);
}