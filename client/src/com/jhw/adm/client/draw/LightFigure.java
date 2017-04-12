package com.jhw.adm.client.draw;

import java.awt.Color;


public interface LightFigure extends NodeFigure {	
	public void turnOn();
	public void turnOff();
	public void changeColor(Color color);
	public void setEdit(LightEdit edit);
}