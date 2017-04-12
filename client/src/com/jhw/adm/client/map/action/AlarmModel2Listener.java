package com.jhw.adm.client.map.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import com.jhw.adm.client.map.GISView;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.warning.SimpleWarning;

public class AlarmModel2Listener implements PropertyChangeListener {
	private GISView view;

	public AlarmModel2Listener(GISView view) {
		this.view = view;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		NodeEntity node = (NodeEntity) evt.getNewValue();
		
	}

}
