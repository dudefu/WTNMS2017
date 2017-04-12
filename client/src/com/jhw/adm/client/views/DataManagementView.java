package com.jhw.adm.client.views;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;

@Component(DataManagementView.ID)
@Scope(Scopes.DESKTOP)
public class DataManagementView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "dataManagementView";
	
	private JTabbedPane tabbedPane;
	
//	@Resource(name=DataImportExportView.ID)
//	private DataImportExportView dataImportExportView;
	
//	@Resource(name=AutoBakupParamView.ID)
//	private AutoBakupParamView autoBakupParamView;
	
	@Resource(name=DataCleanView.ID)
	private DataCleanView dataCleanView;
	
	@PostConstruct
	protected void initialize() {
		
		setTitle("���ݹ�����ͼ");
		setViewSize(450, 300);
		setLayout(new BorderLayout());	

		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(SwingConstants.TOP);
		
		setCloseButton(dataCleanView.getCloseButton());
//		setCloseButton(autoBakupParamView.getCloseButton());
		
//		tabbedPane.addTab("�Զ����ݲ���", autoBakupParamView);
		tabbedPane.addTab("��������", dataCleanView);
//		tabbedPane.addTab("���ݵ��뵼��", dataImportExportView);
		
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	@Override
	public void dispose() {
		super.dispose();
//		dataImportExportView.close();
//		autoBakupParamView.close();
		dataCleanView.close();
	}
}