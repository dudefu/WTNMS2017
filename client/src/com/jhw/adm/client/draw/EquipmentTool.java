package com.jhw.adm.client.draw;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.CreationTool;

public class EquipmentTool extends CreationTool {

	public EquipmentTool(Figure prototype) {
		super(prototype);
		prototypeAttributes = new HashMap<AttributeKey, Object>();
		prototypeAttributes.put(AttributeKeys.FILL_COLOR, null);
		prototypeAttributes.put(AttributeKeys.STROKE_COLOR, null);
	}

    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
    }
    
	private static final long serialVersionUID = 1L;
}