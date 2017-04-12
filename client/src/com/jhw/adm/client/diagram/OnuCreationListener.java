package com.jhw.adm.client.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jhotdraw.draw.DrawingEditor;

import com.jhw.adm.client.draw.EquipmentCreationTool;

public class OnuCreationListener implements ItemListener, ActionListener {

    public OnuCreationListener(EquipmentCreationTool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
    }
    
    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
        	OnuEdit onuEdit = new OnuEdit();
        	onuEdit.createModel();
    		tool.installEdit(onuEdit);
			editor.setTool(tool);
        }
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
    	OnuEdit onuEdit = new OnuEdit();
    	onuEdit.createModel();
		tool.installEdit(onuEdit);
		editor.setTool(tool);
	}
    
    private EquipmentCreationTool tool;
    private DrawingEditor editor;
}