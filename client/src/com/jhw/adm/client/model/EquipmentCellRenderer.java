package com.jhw.adm.client.model;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import sun.swing.DefaultLookup;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.util.ClientUtils;

/**
 * 设备单元格渲染器
 */
public class EquipmentCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, sel, expanded,
				leaf, row, hasFocus);
		this.hasFocus = hasFocus;
		setText(stringValue);
		
		if (value instanceof DiagramDecorator){			
			setText(((DiagramDecorator)value).getText());
		} else if (value instanceof DiagramDecorator.Node){
			setText(((DiagramDecorator.Node)value).getText());
		}
		Color fg = null;

		JTree.DropLocation dropLocation = tree.getDropLocation();
		if (dropLocation != null && dropLocation.getChildIndex() == -1
				&& tree.getRowForPath(dropLocation.getPath()) == row) {

			Color col = DefaultLookup.getColor(this, ui,
					"Tree.dropCellForeground");
			if (col != null) {
				fg = col;
			} else {
				fg = getTextSelectionColor();
			}

		} else if (sel) {
			fg = getTextSelectionColor();
		} else {
			fg = getTextNonSelectionColor();
		}

		setForeground(fg);

		Icon icon = getNodeIcon(value);
		if (!tree.isEnabled()) {
			setEnabled(false);
			LookAndFeel laf = UIManager.getLookAndFeel();
			Icon disabledIcon = laf.getDisabledIcon(tree, icon);
			if (disabledIcon != null) {
				icon = disabledIcon;
			}
			setDisabledIcon(icon);
		} else {
			setEnabled(true);
			setIcon(icon);
		}
		setComponentOrientation(tree.getComponentOrientation());

		selected = sel;

		return this;
	}
	
	private Icon getNodeIcon(Object value) {
		Icon icon = null;
		ImageRegistry imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);
		if (value instanceof DiagramDecorator) {				
			icon = imageRegistry.getImageIcon(MainMenuConstants.SUBNET);
		} else if (value instanceof DiagramDecorator.Node){
			DiagramDecorator.Node diagramNode = (DiagramDecorator.Node)value;
			if (diagramNode.getCategory() == DiagramDecorator.SWITCHER_CATEGORY) {
				icon = imageRegistry.getImageIcon(MainMenuConstants.SWITCHER_TYPE);
			}
			if (diagramNode.getCategory() == DiagramDecorator.CARRIER_CATEGORY) {
				icon = imageRegistry.getImageIcon(MainMenuConstants.CARRIER);
			}
			if (diagramNode.getCategory() == DiagramDecorator.FRONT_END_CATEGORY) {
				icon = imageRegistry.getImageIcon(MainMenuConstants.FRONT_END);
			}
			if (diagramNode.getCategory() == DiagramDecorator.GPRS_CATEGORY) {
				icon = imageRegistry.getImageIcon(MainMenuConstants.GPRS);
			}
			if (diagramNode.getCategory() == DiagramDecorator.EPON_CATEGORY) {				
				icon = imageRegistry.getImageIcon(MainMenuConstants.OLT);
			}
			if (diagramNode.getCategory() == DiagramDecorator.ONU_CATEGORY) {				
				icon = imageRegistry.getImageIcon(MainMenuConstants.OLT);
			}
			if (diagramNode.getCategory() == DiagramDecorator.SUBNET_CATEGORY) {				
				icon = imageRegistry.getImageIcon(MainMenuConstants.SUBNET);
			}
		}
		
		return icon;
	}

	private static final long serialVersionUID = 1L;
}