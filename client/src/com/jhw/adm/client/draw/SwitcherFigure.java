package com.jhw.adm.client.draw;

import java.awt.image.BufferedImage;

import com.jhw.adm.client.diagram.SwitcherEdit;

/**
 * ½»»»»úÍ¼ÐÎ
 */
public class SwitcherFigure extends EquipmentFigure {
	
	public SwitcherFigure() {
		this(null);
	}

	public SwitcherFigure(BufferedImage image) {
		super(0, 0, image);
	}

	@Override
	public SwitcherEdit getEdit() {
		return (SwitcherEdit)super.getEdit();
	}
	
	private static final long serialVersionUID = -5145160583856555633L;
}