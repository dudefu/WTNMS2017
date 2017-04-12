package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.DiagramDecorator;
import com.jhw.adm.client.model.EquipmentCellRenderer;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;

@Component(CarrierExplorerView.ID)
@Scope(Scopes.PROTOTYPE)
public class CarrierExplorerView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle("ÔØ²¨»úä¯ÀÀÆ÷");
		setViewSize(240, 640);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("ÔØ²¨»úä¯ÀÀ"));

		EquipmentCellRenderer cellRenderer = new EquipmentCellRenderer();
		tree = new JTree(equipmentModel.getCarrierTreeModel());
		tree.setCellRenderer(cellRenderer);

		tree.addTreeSelectionListener(new TreeSelectListener());
		
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scrollTreeView = new JScrollPane(tree);
		add(scrollTreeView, BorderLayout.CENTER);
		
		JPanel nodeNoInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		String message = "";
		if(!StringUtils.isBlank(equipmentModel.getCarrierMessage())){
			message =equipmentModel.getCarrierMessage();
		}else {
			message = ClientUI.getDesktopWindow().DEFAULT_MESSAGE;
		}
		JLabel messageLabel = new JLabel(message);
		nodeNoInfoPanel.add(messageLabel);
		add(nodeNoInfoPanel, BorderLayout.PAGE_END);
		
		addTreeSelectionPath(equipmentModel.getLastSelected());
//		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, 
//				new PropertyChangeListener() {
//				public void propertyChange(PropertyChangeEvent evt) {
//					Object selected = evt.getNewValue();
//					addTreeSelectionPath(selected);
//			}
//		});
	}
	
	private void addTreeSelectionPath(Object selected) {
		if (selected instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity)selected;
			for (int row = 0; row < tree.getRowCount(); row++) {
				Object obj = tree.getPathForRow(row).getLastPathComponent();
				if (obj instanceof DiagramDecorator.Node) {
					DiagramDecorator.Node diagramNode = (DiagramDecorator.Node)obj;
					if (carrierNode.equals(diagramNode.getEntity())) {
						tree.addSelectionPath(tree.getPathForRow(row));
					}
				}
			}
		}
	}
	
	private class TreeSelectListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (tree.getLastSelectedPathComponent() instanceof DiagramDecorator.Node) {
				DiagramDecorator.Node diagramNode = (DiagramDecorator.Node)tree.getLastSelectedPathComponent();
				equipmentModel.changeSelected(diagramNode.getEntity());
			}
			if (tree.getLastSelectedPathComponent() instanceof DiagramDecorator) {
				DiagramDecorator diagram = (DiagramDecorator)tree.getLastSelectedPathComponent();
				equipmentModel.changeSelected(diagram.getEntity());
			}
		}		
	}

	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	private JTree tree;
	public static final String ID = "carrierExplorerView";
	private static final long serialVersionUID = 1L;
}