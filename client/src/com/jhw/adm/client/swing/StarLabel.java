package com.jhw.adm.client.swing;

import java.awt.Color;

import javax.swing.JLabel;

public class StarLabel extends JLabel{
	
	private static final long serialVersionUID = 1L;
	private static final String STAR_LABEL = "*";
	public StarLabel(){
		super();
		this.setText(STAR_LABEL);
		this.setForeground(Color.RED);
	}
	
	public StarLabel(String text){
		super();
		this.setText(STAR_LABEL);
		this.setForeground(Color.RED);
		this.setText(text + STAR_LABEL);
	}
}
