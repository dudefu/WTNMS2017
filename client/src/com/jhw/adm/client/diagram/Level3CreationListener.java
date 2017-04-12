package com.jhw.adm.client.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jhotdraw.draw.DrawingEditor;

import com.jhw.adm.client.draw.EquipmentCreationTool;

public class Level3CreationListener implements ItemListener, ActionListener {

    public Level3CreationListener(EquipmentCreationTool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
    }
    
    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
        	Layer3SwitcherEdit level3SwitcherEdit = new Layer3SwitcherEdit();
        	level3SwitcherEdit.createModel();
    		tool.installEdit(level3SwitcherEdit);
			editor.setTool(tool);
        }
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		Layer3SwitcherEdit level3SwitcherEdit = new Layer3SwitcherEdit();
		level3SwitcherEdit.createModel();
		tool.installEdit(level3SwitcherEdit);
		editor.setTool(tool);
	}
    
    private final EquipmentCreationTool tool;
    private final DrawingEditor editor;
}