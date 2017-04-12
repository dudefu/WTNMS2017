package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.FrontEndManagementModel;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;

@Component(FrontEndManagementView.ID)
@Scope(Scopes.DESKTOP)
public class FrontEndManagementView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle(localizationManager.getString("FrontEndExplorer"));
		setLayout(new BorderLayout());

		fepTable = new JTable();

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(BorderFactory.createTitledBorder("前置机列表"));
		tablePanel.add(fepTable.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(fepTable, BorderLayout.CENTER);

		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(tablePanel, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		add(scrollTablePanel, BorderLayout.CENTER);

		JPanel detail = new JPanel();
		createDetail(detail);
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				scrollTablePanel, detail);
		splitPanel.setResizeWeight(0.18);

		add(splitPanel, BorderLayout.CENTER);

		setViewSize(800, 600);

		queryData();

		fepTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						fepSelected();
					}
				});

		tablePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fepTable.clearSelection();
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
//		equipmentModel.changeSelected(null);
		frontEndInfolView.close();
	}

	private void fepSelected() {
		int row = fepTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		FEPTopoNodeEntity selecedFepNode = fepViewModel.getFep(row);
		frontEndInfolView.select(selecedFepNode);
//		equipmentModel.changeSelected(selecedFepNode);
	}

	private void createDetail(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.add(frontEndInfolView, BorderLayout.CENTER);

		setCloseButton(frontEndInfolView.getCloseButton());
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		fepEntityList = (List<FEPTopoNodeEntity>) remoteServer.getService()
				.findAll(FEPTopoNodeEntity.class);
		fepViewModel.getFEPTableModel().setColumnName(fepColumnNames);
		fepViewModel.getFEPTableModel().setDataList(fepEntityList);
		fepTable.setModel(fepViewModel.getFEPTableModel());
	}

	public JTable getFEPTable() {
		return fepTable;
	}

	private JTable fepTable = null;
	private final String[] fepColumnNames = { "编号", "IP" };

	private List<FEPTopoNodeEntity> fepEntityList = null;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = FrontEndManagementModel.ID)
	private FrontEndManagementModel fepViewModel;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = FrontEndInfoView.ID)
	private FrontEndInfoView frontEndInfolView;
	
	@Resource(name = LocalizationManager.ID)
	private LocalizationManager localizationManager;


	private static final Logger LOG = LoggerFactory
			.getLogger(FrontEndManagementView.class);
	private static final long serialVersionUID = 1L;
	public static final String ID = "frontEndManagementView";
}