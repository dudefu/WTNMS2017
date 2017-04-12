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
import com.jhw.adm.client.views.switcher.BaseInfoView;

public class SwitcherCreationListener implements ItemListener, ActionListener {

    public SwitcherCreationListener(EquipmentCreationTool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
        tool.addToolListener(new ToolAdapter() {
			@Override
			public void toolDone(ToolEvent event) {
				ShowViewAction emulationAction = ClientUtils.getSpringBean(ShowViewAction.class, ShowViewAction.ID);
				emulationAction.setViewId(BaseInfoView.ID);
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
    		SwitcherEdit switcherEdit = new SwitcherEdit();
    		switcherEdit.createModel();
    		tool.installEdit(switcherEdit);
			editor.setTool(tool);
        }
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		SwitcherEdit switcherEdit = new SwitcherEdit();
		switcherEdit.createModel();
		tool.installEdit(switcherEdit);
		editor.setTool(tool);
	}

    private EquipmentCreationTool tool;
    private DrawingEditor editor;
}