package com.jhw.adm.client.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jhotdraw.draw.DrawingEditor;

import com.jhw.adm.client.draw.EquipmentCreationTool;

public class BeamsplitterCreationListener implements ItemListener, ActionListener {

    public BeamsplitterCreationListener(EquipmentCreationTool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
    }
    
    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
        	BeamsplitterEdit splitterEdit = new BeamsplitterEdit();
        	splitterEdit.createModel();
    		tool.installEdit(splitterEdit);
			editor.setTool(tool);
        }
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
    	BeamsplitterEdit splitterEdit = new BeamsplitterEdit();
    	splitterEdit.createModel();
		tool.installEdit(splitterEdit);
		editor.setTool(tool);
	}
    
    private EquipmentCreationTool tool;
    private DrawingEditor editor;
}