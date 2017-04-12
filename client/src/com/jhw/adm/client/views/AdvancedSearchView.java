package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;


@Component(AdvancedSearchView.ID)
public class AdvancedSearchView extends ViewPart {
	public static final String ID = "advancedSearchView";

	@PostConstruct
	protected void initialize() {
		setTitle("�豸����");
		setLayout(new BorderLayout());
		
		//�������
		String[] columnNames = { "�豸", "����", "����", "λ��" };
		String[][] data = new String[][] {
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"19", "19", "�����ز���", "������" },
				{"21", "���Ի�", "�ն��ز���", "������" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" },
				{"192.168.15.13", "16", "IETH802", "�޺�" }};
		JPanel toolPanel = new JPanel(new GridLayout(1, 1));
		
		//**************************
		//��ѯ���
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		queryPanel.setBorder(BorderFactory.createTitledBorder("��ѯ����"));
		
		JComboBox equipmentBox = new JComboBox(new String[] {
				"",
				"192.168.10.11", "192.168.10.12", "192.168.10.13",
				"192.168.10.11", "192.168.10.12", "192.168.10.13",
				"192.168.10.11", "192.168.10.12", "192.168.10.13",
				"192.168.10.11", "192.168.10.12", "192.168.10.13"});
		equipmentBox.setEditable(true);
		
		queryPanel.add(new JLabel("�豸"));
		queryPanel.add(equipmentBox);
//		equipmentBox.setPreferredSize(new Dimension(100, equipmentBox.getPreferredSize().height));
		
		queryPanel.add(new JLabel("����"));
		queryPanel.add(new JTextField("16", 20));

		
		JButton queryBtn = new JButton();
		queryBtn.setText("��ѯ");
		queryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				AdvancedSearchView.this.getcon
				System.out.println(AdvancedSearchView.this.getParent().getParent().getParent().getParent().getParent());
				System.out.println(AdvancedSearchView.this.getParent().getParent().getParent().getParent().getParent().getBounds().x);
				System.out.println(AdvancedSearchView.this.getParent().getParent().getParent().getParent().getParent().getBounds().y);
				System.out.println(AdvancedSearchView.this.getParent().getParent().getParent().getParent().getParent().getLocation().x);
				System.out.println(AdvancedSearchView.this.getParent().getParent().getParent().getParent().getParent().getLocation().y);
			}
		});
		
		queryPanel.add(queryBtn);	
		toolPanel.add(queryPanel);
		
		JXTable table = new JXTable(data, columnNames);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		table.setHighlighters(HighlighterFactory.createAlternateStriping(2));
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);	
		
		JPanel panel = new JPanel(new BorderLayout(1, 2));
		panel.add(tabelPanel,BorderLayout.CENTER);
		
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.setBorder(BorderFactory.createTitledBorder("��ѯ���"));
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		//***************
		
		//��ҳ����
		JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		JButton firstBtn = new JButton("��ҳ", imageRegistry.getImageIcon(ButtonConstants.FIRST));
		JButton previousBtn = new JButton("��һҳ", imageRegistry.getImageIcon(ButtonConstants.PREVIOUS));
		JButton nextBtn = new JButton("��һҳ", imageRegistry.getImageIcon(ButtonConstants.NEXT));
		JButton lastBtn = new JButton("βҳ", imageRegistry.getImageIcon(ButtonConstants.LAST));
		
		
		JComboBox pageCountBox = new JComboBox();
		
		for (int i = 1; i < 20; i++) {
			pageCountBox.addItem(i);
		}
		
		paginationPanel.add(firstBtn);
		paginationPanel.add(previousBtn);
		paginationPanel.add(nextBtn);
		paginationPanel.add(lastBtn);
		paginationPanel.add(pageCountBox);

		add(toolPanel,BorderLayout.NORTH);
		add(scrollTablePanel, BorderLayout.CENTER);
		add(paginationPanel,BorderLayout.SOUTH);
	}

	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	private static final long serialVersionUID = 1L;
}