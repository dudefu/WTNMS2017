package com.jhw.adm.client.draw;

import java.awt.image.BufferedImage;

public class VirtualElementFigure extends EquipmentFigure {

	private static final long serialVersionUID = 3926219718591467444L;

	public VirtualElementFigure(){
		this(null);
	}
	
	public VirtualElementFigure(BufferedImage image){
		super(0, 0, image);
	}
}
