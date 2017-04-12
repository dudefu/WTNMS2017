package com.jhw.adm.client.views;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(FormGroupView.ID)
public class FormGroupView extends TabbedGroupView {

	@PostConstruct
	protected void initialize() {
		setTitle("±Ìµ• ”Õº");
		setViewSize(800, 600);
	}

	@Override
	public int getPlacement() {
		return TOP_CENTER;
	}

	private static final long serialVersionUID = -1L;
	public static final String ID = "formGroupView";
}