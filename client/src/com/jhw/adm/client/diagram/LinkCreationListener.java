package com.jhw.adm.client.diagram;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.event.ToolEvent;
import org.jhotdraw.draw.event.ToolListener;

import com.jhw.adm.client.actions.ShowViewAction;
import com.jhw.adm.client.draw.LabeledLinkEdit;
import com.jhw.adm.client.draw.LinkCreationTool;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ConfigureGroupView;
import com.jhw.adm.client.views.LinkDetailView;

public class LinkCreationListener implements ItemListener {

    public LinkCreationListener(LinkCreationTool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
        tool.addToolListener(new ToolListener() {
			@Override
			public void areaInvalidated(ToolEvent event) {
			}
			@Override
			public void boundsInvalidated(ToolEvent event) {
			}
			@Override
			public void toolDone(ToolEvent event) {
				if (labeledLinkEdit.getFigure() == null) return;
				
				if (labeledLinkEdit.handleConnected()) {
					labeledLinkEdit.updateAttributes();
					ShowViewAction emulationAction = ClientUtils.getSpringBean(ShowViewAction.ID);
					emulationAction.setViewId(LinkDetailView.ID);
					emulationAction.setGroupId(ConfigureGroupView.ID);
					
					String DefaulActionCommand = "DoubleClick";
					Object actionCommandValue = emulationAction.getValue(Action.ACTION_COMMAND_KEY);
					String actionCommand = actionCommandValue == null ? DefaulActionCommand
							: actionCommandValue.toString();
					ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
							actionCommand, EventQueue.getMostRecentEventTime(), 1);
					emulationAction.actionPerformed(actionEvent);
				}
			}
			@Override
			public void toolStarted(ToolEvent event) {	
			}
        });
    }

    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
        	labeledLinkEdit = new SmartLinkEdit();
        	labeledLinkEdit.createModel();
    		tool.installEdit(labeledLinkEdit);
			editor.setTool(tool);
        }
    }
    
    private LabeledLinkEdit labeledLinkEdit;
    private final LinkCreationTool tool;
    private final DrawingEditor editor;
}