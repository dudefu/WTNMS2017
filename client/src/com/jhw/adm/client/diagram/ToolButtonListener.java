package com.jhw.adm.client.diagram;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.tool.Tool;

public class ToolButtonListener implements ItemListener {
    private Tool tool;
    private DrawingEditor editor;

    public ToolButtonListener(Tool t, DrawingEditor editor) {
        this.tool = t;
        this.editor = editor;
    }

    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            editor.setTool(tool);
        }
    }
}
