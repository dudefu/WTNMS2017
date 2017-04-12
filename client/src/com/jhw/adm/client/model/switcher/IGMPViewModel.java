package com.jhw.adm.client.model.switcher;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;

@Component(IGMPViewModel.ID)
public class IGMPViewModel extends ViewModel{
	public static final String ID = "igmpViewModel";
	
	@Autowired
	@Qualifier(IGMPTableModel.ID)
	private IGMPTableModel tableModel;
	
	
	private IGMPEntity igmpEntity;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public IGMPEntity getIgmpEntity() {
		return igmpEntity;
	}

	public void setIgmpEntity(IGMPEntity igmpEntity) {
		this.igmpEntity = igmpEntity;
	}
	
	public void setIgmpVsiList(List<Igmp_vsi> list){
		tableModel.setIgmpVsiData(list);
		tableModel.fireTableDataChanged();
	}
	
	public List<Igmp_vsi> getIgmpVsiData() {
		return tableModel.getIgmpVsiData();
	}

	public IGMPTableModel getTableModel() {
		return tableModel;
	}


	public void setTableModel(IGMPTableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	public void setColumnNames(String[] columnNames) {
		tableModel.setColumnNames(columnNames);
	}
}
