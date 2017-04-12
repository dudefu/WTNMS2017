package com.jhw.adm.client.diagram;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.event.ToolAdapter;
import org.jhotdraw.draw.event.ToolEvent;

import com.jhw.adm.client.actions.ShowViewAction;
import com.jhw.adm.client.draw.EquipmentCreationTool;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ConfigureGroupView;
import com.jhw.adm.client.views.SubnetInfoView;
import com.jhw.adm.client.views.SubnetPropertyAppendView;

public class SubnetCreationListener implements ItemListener, ActionListener {

    public SubnetCreationListener(EquipmentCreationTool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
        tool.addToolListener(new ToolAdapter() {
			@Override
			public void toolDone(ToolEvent event) {				
				ShowViewAction emulationAction = ClientUtils.getSpringBean(ShowViewAction.class, ShowViewAction.ID);
//				emulationAction.setViewId(SubnetInfoView.ID);
				emulationAction.setViewId(SubnetPropertyAppendView.ID);
				emulationAction.setGroupId(ConfigureGroupView.ID);
				
				String DefaulActionCommand = "DoubleClick";
				Object actionCommandValue = emulationAction.getValue(Action.ACTION_COMMAND_KEY);
				String actionCommand = actionCommandValue == null ? DefaulActionCommand
						: actionCommandValue.toString();
				ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
						actionCommand, EventQueue.getMostRecentEventTime(), 1);
				emulationAction.actionPerformed(actionEvent);
			}
        });
    }
    
    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
        	SubnetEdit nodeEdit = new SubnetEdit();
    		nodeEdit.createModel();
			tool.installEdit(nodeEdit);
			editor.setTool(tool);
        }
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		SubnetEdit nodeEdit = new SubnetEdit();
		nodeEdit.createModel();
		tool.installEdit(nodeEdit);
		editor.setTool(tool);
	}

    private final EquipmentCreationTool tool;
    private final DrawingEditor editor;
}