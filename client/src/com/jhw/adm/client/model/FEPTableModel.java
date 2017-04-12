package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;

@Component(FEPTableModel.ID)
public class FEPTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 6523879291721052443L;
	public static final String ID = "fepTableModel";

	private List<FEPTopoNodeEntity> fepEntityList = null;
	private String[] columnName = null;
	
	private RemoteServer remoteServer;

	@PostConstruct
	protected void initialize() {
		remoteServer = ClientUtils.getSpringBean(RemoteServer.class, RemoteServer.ID);
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}

	public void setDataList(List<FEPTopoNodeEntity> fepEntityList) {
		if (null == fepEntityList) {
			this.fepEntityList = new ArrayList<FEPTopoNodeEntity>();
		} else {
			this.fepEntityList = fepEntityList;
		}
	}

	public int getRowCount() {
		if (null == fepEntityList) {
			return 0;
		}
		return fepEntityList.size();
	}

	public int getColumnCount() {
		return columnName.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnName[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		int count = fepEntityList.size();
		if (count <= rowIndex) {
			return null;
		}

		Object value = null;
		FEPTopoNodeEntity  fepNode = fepEntityList.get(rowIndex);
		FEPEntity fepEntity = remoteServer.getService().getFEPEntityByIP(
				fepEntityList.get(rowIndex).getIpValue());
		if (fepEntity == null) return "NULL";
		fepNode.setFepEntity(fepEntity);
		switch (columnIndex) {
		case 0:
			value = fepEntity.getCode();
			break;
		case 1:
			value = fepEntity.getIpValue();
			break;
		}

		return value;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		int count = fepEntityList.size();
		if (count <= rowIndex) {
			return;
		}

		FEPEntity fepEntity = fepEntityList.get(rowIndex).getFepEntity();
		switch (columnIndex) {
		case 0:
			fepEntity.setCode((String) aValue);
			break;
		case 1:
			fepEntity.setFepName((String) aValue);
			break;
		}
	}

	public void addFEP(FEPTopoNodeEntity fep) {
		fepEntityList.add(fep);
		this.fireTableDataChanged();

	}

	public void updateFEP(int index, FEPTopoNodeEntity fep) {
		fepEntityList.set(index, fep);
		this.fireTableDataChanged();
	}

	public void removeFEP(FEPTopoNodeEntity fep) {
		fepEntityList.remove(fep);
	}

	public FEPTopoNodeEntity getFep(int rowIndex) {
		int count = fepEntityList.size();
		if (count <= rowIndex) {
			return null;
		}

		return fepEntityList.get(rowIndex);
	}

	public List<FEPTopoNodeEntity> getFepList() {
		return fepEntityList;
	}
}
