package com.jhw.adm.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;

import com.jhw.adm.client.model.LoginDialogModel;
import com.jhw.adm.client.model.ServerInfo;
import com.jhw.adm.client.model.LoginDialogModel.ServerTableModel;

/**
 * 服务器管理对话框
 */
public class ManageServerDialog extends JDialog {
	
	public ManageServerDialog(Dialog owner) {
		super(owner, true);
		setSize(640, 480);
		setTitle("服务器列表");
		centerDialog();
		setLayout(new BorderLayout());
		JPanel addingPanel = new JPanel();
		createAddingPanel(addingPanel);
		
		JPanel listPanel = new JPanel();
		createListPanel(listPanel);
		
		add(addingPanel, BorderLayout.PAGE_START);
		add(listPanel, BorderLayout.CENTER);
	}
	
	private void createAddingPanel(JPanel parent) {
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
        integerFormat.setMinimumFractionDigits(0);
        integerFormat.setParseIntegerOnly(true);
        integerFormat.setGroupingUsed(false);
        		
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		parent.add(new JLabel("名称"));
		final JTextField nameField = new JTextField(25);
		parent.add(nameField);
		
		parent.add(new JLabel("地址"));
		final JTextField addressField = new JTextField();
		addressField.setColumns(20);
		addressField.setEditable(true);
		parent.add(addressField);
		
		parent.add(new JLabel("端口"));
		final JFormattedTextField portField = new JFormattedTextField(integerFormat);
		portField.setColumns(15);
		parent.add(portField);
		
		JButton addButton = new JButton("添 加");
		addButton.setBackground(new Color(61, 151, 203));
		parent.add(addButton);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (StringUtils.isBlank(nameField.getText())) {
					JOptionPane.showMessageDialog(ManageServerDialog.this, "名称不能为空",
							"错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (StringUtils.isBlank(addressField.getText())) {
					JOptionPane.showMessageDialog(ManageServerDialog.this, "地址不能为空",
							"错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (StringUtils.isBlank(portField.getText())) {
					JOptionPane.showMessageDialog(ManageServerDialog.this, "端口不能为空",
							"错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				ServerInfo config = new ServerInfo();
				config.setName(nameField.getText());
				config.setAddress(addressField.getText());
				config.setPort(Integer.parseInt(portField.getText()));
				config.setTimeout(100);
				
				configModel.addServer(config);
			}
		});
		
		JButton deleteButton = new JButton("删 除");
		deleteButton.setBackground(new Color(61, 151, 203));
		parent.add(deleteButton);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() > -1) {
					configModel.removeConfig(table.getSelectedRow());
				}
			}
		});
	}
	
	private void createListPanel(JPanel parent) {
		parent.setLayout(new GridLayout(1, 1, 20, 20));
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(false);
		table.getTableHeader().setReorderingAllowed(false);
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		parent.add(scrollTablePanel);
	}
	
	public void fillContents(LoginDialogModel configModel) {
		this.configModel = configModel;
		tableModel = configModel.getTabelModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnModel());
		tableModel.fireTableDataChanged();
	}
	
	protected void centerDialog() {
		Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(x, y);
	}

    private JXTable table;
	private ServerTableModel tableModel;
	private LoginDialogModel configModel;
	
	private static final long serialVersionUID = 7173545202423390472L;
}