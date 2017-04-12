package com.jhw.adm.client.model;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class ListComboBoxModel extends AbstractListModel implements
		ComboBoxModel {

	public ListComboBoxModel(List<?> theList) {
		this.elements = theList;
	}

	public Object getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object newValue) {
		selectedItem = newValue;
	}

	public int getSize() {
		return elements == null ? 0 : elements.size();
	}

	public Object getElementAt(int i) {
		return elements == null ? null : elements.get(i);
	}

	private Object selectedItem;
	private List<?> elements;
	private static final long serialVersionUID = 4165175252800873609L;
}