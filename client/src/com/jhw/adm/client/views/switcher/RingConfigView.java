package com.jhw.adm.client.views.switcher;

/*
 * ��������
 */

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.BACK;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.RingConfigTableModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.views.SwitcherExplorerView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.RingBean;
import com.jhw.adm.server.entity.util.RingInfo;

@Component(RingConfigView.ID)
@Scope(Scopes.DESKTOP)
public class RingConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ringConfigView";
	private static final Logger LOG = LoggerFactory.getLogger(RingConfigView.ID);
	
	private final JPanel ringTypePnl = new JPanel();
	private final JLabel ringIdLbl = new JLabel();
	private final JTextField ringIdFld = new JTextField();
	private final JLabel ringTypeLbl = new JLabel();
	private final JTextField ringTypeFld = new JTextField();
	private final JLabel modelLbl = new JLabel();
	private final JCheckBox modelChk = new JCheckBox();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	private final JPanel configPnl = new JPanel();
	private final JLabel firstPortMemberLbl = new JLabel();
	private final JComboBox firstportMemberCombox = new JComboBox();
	
	private final JLabel secondPortMemberLbl = new JLabel();
	private final JComboBox secondPortMemberCombox = new JComboBox();
	
	private final JLabel sysTypeLbl = new JLabel();
	private final JComboBox sysTypeCombox = new JComboBox();
	
	private final JLabel firstPortMemberTypeLbl = new JLabel();
	private final JComboBox firstportMemberTypeCombox = new JComboBox();
	
	private final JLabel secondPortMemberTypeLbl = new JLabel();
	private final JComboBox secondportMemberTypeCombox = new JComboBox();
	
	private final JPanel tablePnl = new JPanel();
	private final CommonTable table = new CommonTable();
	
	//�¶˵İ�ť���
	private final JPanel bottomPnl = new JPanel();
	private JButton downloadBtn;
	private JButton backBtn;
	private JButton closeBtn = null;
	
	private JButton addBtn;
	private JButton deleteBtn;
	private JButton synBtn;
	
	private CardLayout cardLayout;
	private JPanel cardPnl;
	
	private RingBean ringBean;
	
	private RingManagementView ringMngView;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private final String[] systemType = {"Trans","Master","Assistant","Edge"};
	
	private final String[] portMemberType = {"None","Master","Subsidiary","Edgeport"};
	
	private static final String[] COLUMN_NAME = {"�豸IP","�˿�","ϵͳ����"}; 
	private ButtonFactory buttonFactory;
	
	private static final int DYNAMIC = 1;
	
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(RingConfigTableModel.ID)
	private RingConfigTableModel ringConfigTableModel;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=SwitcherExplorerView.ID)
	private ViewPart explorerView;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				switchNodeEntity = (SwitchNodeEntity) adapterManager
						.getAdapter(equipmentModel.getLastSelected(),SwitchNodeEntity.class);
				switchSelectAction();
			}
			else{
				firstportMemberCombox.removeAllItems();
				secondPortMemberCombox.removeAllItems();
				sysTypeCombox.removeAllItems();
				firstportMemberTypeCombox.removeAllItems();
				secondportMemberTypeCombox.removeAllItems();
			}
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			List<RingBean> ringBeanList = remoteServer.getNmsService().queryRingConfig();
			if (ringBeanList != null && ringBeanList.size() > 0){
				String selectRingId = ringIdFld.getText();
				for (RingBean ringBean : ringBeanList){
					String ringId = String.valueOf(ringBean.getRingID());
					String status = dataStatus.get(ringBean.getIssuedTag()).getKey();
					if (selectRingId.equalsIgnoreCase(ringId.trim())){
						statusFld.setText(status);
					}
				}
			}		
			ringMngView.refreshRingTableView();
		}
	};
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//ǰ�û�������
		@Override
		public void process(TextMessage message) {
			List<RingBean> ringBeanList = remoteServer.getNmsService().queryRingConfig();
			if (ringBeanList != null && ringBeanList.size() > 0){
				String selectRingId = ringIdFld.getText();
				for (RingBean ringBean : ringBeanList){
					String ringId = String.valueOf(ringBean.getRingID());
					String status = dataStatus.get(ringBean.getIssuedTag()).getKey();
					if (selectRingId.equalsIgnoreCase(ringId.trim())){
						statusFld.setText(status);
					}
				}
			}		
			ringMngView.refreshRingTableView();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		init();	
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initConfigPnl();
		initTablePnl();
		initBottomPnl();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(configPnl,BorderLayout.NORTH);
		panel.add(tablePnl,BorderLayout.CENTER);
		panel.add(bottomPnl,BorderLayout.SOUTH);
		
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				explorerView, panel);
		splitPanel.setDividerLocation(210);
		
		this.setLayout(new BorderLayout());
		this.add(splitPanel,BorderLayout.CENTER);
		
		setResource();
	}
	
	private void initTablePnl(){
		table.adjustColumnPreferredWidths();
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(table);

		tablePnl.setLayout(new BorderLayout());
		tablePnl.add(scrollPnl,BorderLayout.CENTER);
		
		ringConfigTableModel.setColumnName(COLUMN_NAME);
		table.setModel(ringConfigTableModel);
		
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(ringConfigTableModel);
		table.setRowSorter(sorter);
		sorter.toggleSortOrder(0);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
//		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		backBtn = buttonFactory.createButton(BACK);
		closeBtn = buttonFactory.createCloseButton();
//		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(backBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void initConfigPnl(){
		ringTypePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel middlePnl = new JPanel(new GridBagLayout());
		middlePnl.add(ringIdLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		middlePnl.add(ringIdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		middlePnl.add(ringTypeLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,5,0),0,0));
		middlePnl.add(ringTypeFld,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		middlePnl.add(modelLbl,new GridBagConstraints(4,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		middlePnl.add(modelChk,new GridBagConstraints(5,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		middlePnl.add(statusLbl,new GridBagConstraints(6,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,5,0),0,0));
		middlePnl.add(statusFld,new GridBagConstraints(7,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		
		ringTypePnl.add(middlePnl);
		ringTypePnl.setBorder(BorderFactory.createTitledBorder("Ring��Ϣ"));
		
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(firstPortMemberLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(firstportMemberCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(secondPortMemberLbl,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,5,0),0,0));
		panel.add(secondPortMemberCombox,new GridBagConstraints(4,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		
		panel.add(sysTypeLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(sysTypeCombox,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(firstPortMemberTypeLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(firstportMemberTypeCombox,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		panel.add(secondPortMemberTypeLbl,new GridBagConstraints(3,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		panel.add(secondportMemberTypeCombox,new GridBagConstraints(4,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));

		
		JPanel ringConfigPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		ringConfigPnl.add(panel);
		ringConfigPnl.setBorder(BorderFactory.createTitledBorder("�˿�����"));
		
		
		JPanel btnPnl = new JPanel(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		synBtn = buttonFactory.createButton(UPLOAD);
		addBtn = buttonFactory.createButton(APPEND);
		deleteBtn = buttonFactory.createButton(DELETE);
		newPanel.add(addBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
		newPanel.add(deleteBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
		newPanel.add(synBtn,new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, Constant.BUTTONINTERVAL, 10, 3), 0, 0));
		
		btnPnl.add(newPanel, BorderLayout.EAST);
		
		configPnl.setLayout(new BorderLayout());
		configPnl.add(ringTypePnl,BorderLayout.NORTH);
		configPnl.add(ringConfigPnl,BorderLayout.CENTER);
		configPnl.add(btnPnl,BorderLayout.SOUTH);
	}

	private void setResource(){
		synBtn.setVisible(false);
		
		ringIdFld.setColumns(15);
		ringTypeFld.setColumns(15);
		ringIdFld.setEditable(false);
		ringTypeFld.setEditable(false);
		ringIdFld.setBackground(Color.WHITE);
		ringTypeFld.setBackground(Color.WHITE);
		
		modelChk.setEnabled(false);
		modelChk.setText("Enable");
		modelChk.setHorizontalTextPosition(AbstractButton.LEADING);
		
		firstportMemberTypeCombox.setEnabled(false);
		secondportMemberTypeCombox.setEnabled(false);
		secondportMemberTypeCombox.setEnabled(false);
		
		firstportMemberCombox.setPreferredSize(new Dimension(80,firstportMemberCombox.getPreferredSize().height));
		secondPortMemberCombox.setPreferredSize(new Dimension(80,secondPortMemberCombox.getPreferredSize().height));
		sysTypeCombox.setPreferredSize(new Dimension(80,sysTypeCombox.getPreferredSize().height));
		firstportMemberTypeCombox.setPreferredSize(new Dimension(80,firstportMemberTypeCombox.getPreferredSize().height));
		secondportMemberTypeCombox.setPreferredSize(new Dimension(80,secondportMemberTypeCombox.getPreferredSize().height));
		
		
		ringTypeLbl.setText("��������");
		ringIdLbl.setText("Ring ID");
		statusLbl.setText("״̬");
		firstPortMemberLbl.setText("�˿ڳ�Ա1");
		secondPortMemberLbl.setText("�˿ڳ�Ա2");
		sysTypeLbl.setText("ϵͳ����");
		firstPortMemberTypeLbl.setText("�˿ڳ�Ա����1");
		secondPortMemberTypeLbl.setText("�˿ڳ�Ա����2");
		statusFld.setEditable(false);
		statusFld.setColumns(15);
		statusFld.setBackground(Color.WHITE);
		
		addListener();
	}
	
	private void addListener(){
		sysTypeCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				int index = sysTypeCombox.getSelectedIndex();
				if (index == 0){
					firstportMemberTypeCombox.setEnabled(false);
					secondportMemberTypeCombox.setEnabled(false);
				}
				else{
					firstportMemberTypeCombox.setEnabled(true);
					secondportMemberTypeCombox.setEnabled(true);
				}
			}
		});
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * ��������ѡ���¼�
	 */
	private void switchSelectAction(){
		if (null == switchNodeEntity){
			return ;
		}
		
		//ͨ��switchNodeEntity��ѯ�˿ڵ�����
		int portCount = switchNodeEntity.getPorts().size();
		firstportMemberCombox.removeAllItems();
		secondPortMemberCombox.removeAllItems();
		for (int i = 0 ; i < portCount; i++){
			firstportMemberCombox.addItem("Port"+(i+1));
			secondPortMemberCombox.addItem("Port"+(i+1));
		}
		
		int systemTypeLen = systemType.length;
		int portMemberTypeLen = portMemberType.length;
		sysTypeCombox.removeAllItems();
		firstportMemberTypeCombox.removeAllItems();
		secondportMemberTypeCombox.removeAllItems();
		
		for(int j = 0 ; j < systemTypeLen; j++){
			sysTypeCombox.addItem(systemType[j]);
		}
		
		if(null != ringBean){
			if(this.DYNAMIC == ringBean.getRingType()){
				sysTypeCombox.setEnabled(false);
			}else {
				sysTypeCombox.setEnabled(true);
			}
		}
		
		for(int k =0 ; k < portMemberTypeLen; k++){
			firstportMemberTypeCombox.addItem(portMemberType[k]);
			secondportMemberTypeCombox.addItem(portMemberType[k]);
		}
	}
	
	public void setCardLayout(CardLayout cardLayout,JPanel cardPnl,RingManagementView ringMngView){
		this.cardLayout = cardLayout;
		this.cardPnl = cardPnl;
		this.ringMngView = ringMngView;
	}
	
	/**
	 * ����ring�˿ڱ���ֵ
	 * @param selectVlanBean
	 * @param switchVlanPortInfoList
	 */
	@SuppressWarnings("unchecked")
	public void setValue(RingBean selectRingBean,List<RingInfo> ringInfoList){
		if (null == selectRingBean){
			ringConfigTableModel.setDataList(null);
			ringConfigTableModel.fireTableDataChanged();
			return;
		}
		ringBean = selectRingBean;
		switchSelectAction();
		
		String ringID = String.valueOf(selectRingBean.getRingID());
		String ringType = reverseRingType(selectRingBean.getRingType());
		boolean mode = selectRingBean.isRingEnable();
		String status = dataStatus.get(selectRingBean.getIssuedTag()).getKey();
		
		ringIdFld.setText(ringID);
		ringTypeFld.setText(ringType);
		modelChk.setSelected(mode);
		statusFld.setText(status);
		
		if (null == ringInfoList){
			ringConfigTableModel.setDataList(null);
			ringConfigTableModel.fireTableDataChanged();
			return;
		}
		List<List> dataList = new ArrayList<List>();
		for(RingInfo ringInfo : ringInfoList){
			String ipValue = ringInfo.getIp();
			String ports = ringInfo.getPorts();
			String sysType = ringInfo.getSysType();
			
			List rowList = new ArrayList();
			rowList.add(ipValue);
			rowList.add(ports);
			rowList.add(sysType);
			rowList.add(ringInfo);
			dataList.add(rowList);
		}
		
		ringConfigTableModel.setDataList(dataList);
		ringConfigTableModel.fireTableDataChanged();
	}
	
	/**
	 * ��������
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND,desc="�����˿�GH_RING����",role=Constants.MANAGERCODE)
	public void append(){
		if(!isValids()){
			return;
		}
		RingInfo ringInfo = new RingInfo();
		String ipValue = switchNodeEntity.getBaseConfig().getIpValue();
		
		int port1 = NumberUtils.toInt(firstportMemberCombox.getSelectedItem().toString().substring(4));
		int port2 = NumberUtils.toInt(secondPortMemberCombox.getSelectedItem().toString().substring(4));
		String ports = port1 + "," + port2;
		
		String port1Type = firstportMemberTypeCombox.getSelectedItem().toString().toLowerCase();
		String port2Type = secondportMemberTypeCombox.getSelectedItem().toString().toLowerCase();
		
		String sysType = sysTypeCombox.getSelectedItem().toString().toLowerCase();
		
		ringInfo.setIp(ipValue);
		ringInfo.setPort1(port1);
		ringInfo.setPort2(port2);
		ringInfo.setPorts(ports);
		ringInfo.setPort1Type(port1Type);
		ringInfo.setPort2Type(port2Type);
		ringInfo.setSysType(sysType);
		
		List rowList = new ArrayList();
		rowList.add(ipValue);
		rowList.add(ports);
		rowList.add(sysType);
		rowList.add(ringInfo);
		
		ringConfigTableModel.insertRow(rowList);
	}
	
	/**
	 * ɾ������
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="ɾ���˿�GH_RING����",role=Constants.MANAGERCODE)
	public void delete(){
		if (table.getSelectedRowCount() < 1){
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		int[] selectRows = new int[table.getSelectedRowCount()];
		for(int i = 0 ; i < table.getSelectedRows().length; i++){
			int modelRow = table.convertRowIndexToModel(table.getSelectedRows()[i]);
			selectRows[i] = modelRow;
		}
		ringConfigTableModel.removeRow(selectRows);
	}
	
	/**
	 * ���ز���
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="���ض˿�GH_RING����",role=Constants.MANAGERCODE)
	public void download(){
		int result = PromptDialog.showPromptDialog(this, "��ѡ�����صķ�ʽ",imageRegistry);
		if (result == 0){
			return ;
		}
		
		List<RingInfo> ringInfoList = new ArrayList<RingInfo>();
		for (int i = 0 ; i < table.getRowCount(); i++){
			int row = table.convertRowIndexToModel(i);
			RingInfo ringInfo = (RingInfo)ringConfigTableModel.getValueAt(row,ringConfigTableModel.getColumnCount());
			ringInfoList.add(ringInfo);
		}
		
		Task task = new DownLoadRequestTask(ringInfoList, result);
		showConfigureMessageDialog(task, "����");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<RingInfo> list = null;
		private int result = 0;
		public DownLoadRequestTask(List<RingInfo> list, int result){
			this.list = list;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getNmsService().saveRing(list,clientModel.getLocalAddress()
						,ringBean,result,clientModel.getCurrentUser().getUserName());
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("���ض˿�GH_RING�쳣");
				statusFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
				ringMngView.refreshRingTableView();
				LOG.error("VlanConfigView.DownLoadRequestTask error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				statusFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
				ringMngView.refreshRingTableView();
			}
		}
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private JProgressBarDialog dialog;
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(paramConfigStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfigureMessageDialog(task, operation);
				}
			});
		}
	}

	/**
	 * ���ز���
	 */
	@ViewAction(name=BACK, icon=ButtonConstants.RETURN,desc="��������RING ID��ͼ", role=Constants.MANAGERCODE)
	public void back(){
		if (null == cardLayout){
			return;
		}
		cardLayout.show(cardPnl, VlanManagementView.LIST_CARD);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				ringMngView.refreshAddRingView();
			}
		});
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	private int reverseRingType(String str){
		int value = 0;
		if (str.equals("��̬����")){
			value = 0;
		}
		else {
			value = 1;
		}
		return value;
	}
	
	private String reverseRingType(int value){
		String str = "";
		if (value == 0){
			str = "��̬����";
		}
		else{
			str = "��̬����";
		}
		return str;
	}
	
	public boolean isValids(){
		boolean isValid = true;
		
		String ipValue = switchNodeEntity.getBaseConfig().getIpValue();
		for (int i = 0 ; i < table.getRowCount(); i++){
			String deviceIp = (String)ringConfigTableModel.getValueAt(i, 0);
			if (ipValue.equals(deviceIp)){
				JOptionPane.showMessageDialog(this, "�б����Ѿ�����ͬ���豸�ˣ�����ɾ����������.","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if(null == firstportMemberCombox.getSelectedItem() || "".equals(firstportMemberCombox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա1����Ϊ�գ���ѡ��˿ڳ�Ա","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == secondPortMemberCombox.getSelectedItem() || "".equals(secondPortMemberCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա2����Ϊ�գ���ѡ��˿ڳ�Ա","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == sysTypeCombox.getSelectedItem() || "".equals(sysTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "ϵͳ���Ͳ���Ϊ�գ���ѡ��ϵͳ����","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == firstportMemberTypeCombox.getSelectedItem() || "".equals(firstportMemberTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա����1����Ϊ�գ���ѡ��˿ڳ�Ա����","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == secondportMemberTypeCombox.getSelectedItem() || "".equals(secondportMemberTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա����2����Ϊ�գ���ѡ��˿ڳ�Ա����","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String firstPort = firstportMemberCombox.getSelectedItem().toString();
		String secondPort = secondPortMemberCombox.getSelectedItem().toString();
		if (firstPort.equalsIgnoreCase(secondPort)){
			JOptionPane.showMessageDialog(this, "��ѡ����ȷ�Ķ˿ڳ�Ա������ѡ��������ͬ�Ķ˿�","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		String sysType = sysTypeCombox.getSelectedItem().toString();
		String firstPortType = firstportMemberTypeCombox.getSelectedItem().toString();
		String secondPortType = secondportMemberTypeCombox.getSelectedItem().toString();
		if (sysType.equals("Master")){
			if (firstPortType.equals(secondPortType)){
				JOptionPane.showMessageDialog(this, "�˿���������ΪMasterʱ����Ա�˿ڱ���һ��ΪMaster��һ��ΪSubsidiary"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			if (!((firstPortType.equals("Master") || firstPortType.equals("Subsidiary")) 
					&& (secondPortType.equals("Master") || secondPortType.equals("Subsidiary")))){
				JOptionPane.showMessageDialog(this, "�˿���������ΪMasterʱ����Ա�˿ڱ���һ��ΪMaster��һ��ΪSubsidiary"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if (sysType.equals("Assistant") || sysType.equals("Edge")){
			if (firstPortType.equals(secondPortType)){
				JOptionPane.showMessageDialog(this, "�˿���������ΪAssistant��Edgeʱ����Ա�˿ڱ���һ��ΪEdgeport��һ��ΪNone"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			if (!((firstPortType.equals("Edgeport") || firstPortType.equals("None")) 
					&& (secondPortType.equals("Edgeport") || secondPortType.equals("None")))){
				JOptionPane.showMessageDialog(this, "�˿���������ΪAssistant��Edgeʱ����Ա�˿ڱ���һ��ΪEdgeport��һ��ΪNone"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if(!isRingType()){
			isValid = false;
			return isValid;
		}

		return isValid;
	}
	
	private boolean isRingType(){
		boolean  bool = true;
		
		String newIpValue = switchNodeEntity.getBaseConfig().getIpValue();
		int newRingType = reverseRingType(ringTypeFld.getText());
		String port1Str = firstportMemberCombox.getSelectedItem().toString();
		String port2Str = secondPortMemberCombox.getSelectedItem().toString();
		int newPort1 =  NumberUtils.toInt(port1Str.substring(4));
		int newPort2 =  NumberUtils.toInt(port2Str.substring(4));
		
		List<RingInfo> allRingInfoList = remoteServer.getNmsService().queryAllRingInfo();
		for(RingInfo ringInfo : allRingInfoList){
			String oldIpValue = ringInfo.getIp();
			int oldRingType = ringInfo.getRingType();
			int oldPort1 = ringInfo.getPort1();
			int oldPort2 = ringInfo.getPort2();
			if (newIpValue.equals(oldIpValue)){
				if (newRingType != oldRingType){
					JOptionPane.showMessageDialog(this, "ѡ��Ļ������ͺʹ��豸�Ļ������Ͳ�ͬ������ɾ��ԭ����Ring"
							,"��ʾ",JOptionPane.NO_OPTION);
					bool = false;
					break;
				}
				if (((newPort1 == oldPort1)&& (newPort2 == oldPort2)) || ((newPort1 == oldPort2)&& (newPort2 == oldPort1))){
					JOptionPane.showMessageDialog(this, "�˿ڱ�Rstp������Ringռ�ã���ѡ�������˿�"
							,"��ʾ",JOptionPane.NO_OPTION);
					bool = false;
					break;
				}
			}
			
		}

		return bool;
	}
}