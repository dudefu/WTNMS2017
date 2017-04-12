package com.jhw.adm.client.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(LoginDialogModel.ID)
public class LoginDialogModel {

	public LoginDialogModel() {
		comboModel = new ServerComboBoxModel();
		tabelModel = new ServerTableModel();
//		clientConfig = new ClientConfig();
	}

//	public void loadConfig() {
//		File fin = new File(CONFIG_FILE);
//		FileInputStream fis = null;
//		XMLDecoder decoder = null;
//		try {
//			fis = new FileInputStream(fin);
//			decoder = new XMLDecoder(fis);
//			clientConfig = (ClientConfig) decoder.readObject();
//		} catch (Exception ex) {
//			LOG.error("load config error", ex);
//		} finally {
//			if (decoder != null) {
//				decoder.close();
//			}
//			if (fis != null) {
//				try {
//					fis.close();
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//	}
//
//	public void saveConfig() {
//		File fo = new File(CONFIG_FILE);
//		FileOutputStream fos = null;
//		XMLEncoder encoder = null;
//		try {
//			fos = new FileOutputStream(fo);
//			encoder = new XMLEncoder(fos);
//			clientConfig.setMapFileName("c:\\33234\\32424\\fa.mxg");
//			encoder.writeObject(clientConfig);
//			encoder.flush();
//		} catch (IOException ex) {
//			LOG.error("save config error", ex);
//		} finally {
//			if (encoder != null) {
//				encoder.close();
//			}
//			if (fos != null) {
//				try {
//					fos.close();
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//	}
	
	public String getLastUser() {
		return clientConfig.getLastUser();
	}

	public void setLastUser(String lastUser) {
		clientConfig.setLastUser(lastUser);
	}
	
	public void addServer(ServerInfo config) {
		clientConfig.addServer(config);
		tabelModel.fireTableDataChanged();
	}

	public void removeConfig(int index) {
		if (index < clientConfig.getServerCount()) {
			clientConfig.removeServer(index);
			tabelModel.fireTableDataChanged();
		}
	}

	public ServerComboBoxModel getComboModel() {
		return comboModel;
	}

	public ServerTableModel getTabelModel() {
		return tabelModel;
	}

	public ServerInfo getSelectedServer() {
		return (ServerInfo) comboModel.getSelectedItem();
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	private ClientConfig clientConfig;
	private ServerTableModel tabelModel;
	private ServerComboBoxModel comboModel;
	private static final Logger LOG = LoggerFactory.getLogger(LoginDialogModel.class);
	public static final String CONFIG_FILE = "conf/client.config.xml";
	public static final String ID = "serverConfigModel";

	private class ServerComboBoxModel extends AbstractListModel implements
			ComboBoxModel {

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		@Override
		public void setSelectedItem(Object newValue) {
			selectedItem = newValue;
		}

		@Override
		public int getSize() {
			return clientConfig.getServerCount();
		}

		@Override
		public Object getElementAt(int i) {
			return clientConfig.getServer(i);
		}

		private Object selectedItem;
		private static final long serialVersionUID = -1046984489473149247L;
	}

	public class ServerTableModel extends AbstractTableModel {

		public ServerTableModel() {
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModel();
			TableColumnExt nameColumn = new TableColumnExt(modelIndex++, 120);
			nameColumn.setIdentifier("名称");
			nameColumn.setHeaderValue("名称");
			columnModel.addColumn(nameColumn);

			TableColumnExt addressColumn = new TableColumnExt(modelIndex++, 150);
			addressColumn.setIdentifier("地址");
			addressColumn.setHeaderValue("地址");
			columnModel.addColumn(addressColumn);

			TableColumnExt portColumn = new TableColumnExt(modelIndex++, 80);
			portColumn.setIdentifier("端口");
			portColumn.setHeaderValue("端口");
			columnModel.addColumn(portColumn);
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return clientConfig.getServerCount();
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumn(col).getHeaderValue().toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < clientConfig.getServerCount()) {
				ServerInfo server = clientConfig.getServer(row);
				switch (col) {
				case 0:
					value = server.getName();
					break;
				case 1:
					value = server.getAddress();
					break;
				case 2:
					value = server.getPort();
					break;
				default:
					break;
				}
			}

			return value;
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public ServerInfo getValue(int row) {
			ServerInfo value = null;
			if (row < clientConfig.getServerCount()) {
				value = clientConfig.getServer(row);
			}

			return value;
		}

		private TableColumnModel columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}