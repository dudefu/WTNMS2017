package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.FrontEndManagementModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JCloseButton;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.nets.StatusEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(FrontEndInfoView.ID)
@Scope(Scopes.DESKTOP)
public class FrontEndInfoView extends ViewPart {
	
	private JTextField codeField = null;
//	private JTextField nameField = null;
	private JPasswordField pwdFld = null;
	private IpAddressField ipField = null;
	
	private IpAddressField directSwitchIpFld = null;
	private IpAddressField begintIPField = null;
	private IpAddressField endIPField = null;
	
	private JLabel descriptionLbl = new JLabel("节点名称");
	private JTextField descriptionFld = new JTextField();
	
	private JButton addIPBtn = null;
	private JButton delIPBtn = null;
	
	private JTable  ipTable = null;
	private final String[] ipColumnName = {"起始IP","终止IP"};
	private static final String IPADD = "ipAdd";
	private static final String IPDEL = "ipDel";
	
	private FEPTopoNodeEntity selectedFrontEndNode;
	private FEPEntity fepEntity = null;
	private final List<IPSegment> delList = new ArrayList<IPSegment>();
	//private static final String INPUT_LENGTH = "1-36个字符";
	private static final String PASSWORD_LENGTH = "6-16个字符";
	
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	private static final Logger LOG = LoggerFactory.getLogger(FrontEndInfoView.class);

	@PostConstruct
	protected void initialize() {
		setTitle("前置机信息");
		buttonFactory = actionManager.getButtonFactory(this); 
		setLayout(new BorderLayout());
		JPanel detail = new JPanel();
		createDetail(detail);
		
		add(detail, BorderLayout.CENTER);
		
		setViewSize(640, 480);
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, lastSelectionChanged);
		selectedFrontEndNode = (FEPTopoNodeEntity)adapterManager.getAdapter(
				equipmentModel.getLastSelected(), FEPTopoNodeEntity.class);
		fillContents();
	}
	
	private final PropertyChangeListener lastSelectionChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			selectedFrontEndNode = (FEPTopoNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), FEPTopoNodeEntity.class);
			fillContents();
		}
	};
	
	public void select(FEPTopoNodeEntity fepNode) {
		selectedFrontEndNode = fepNode;
		fillContents();
	}
	
	private void fillContents() {
		
		if (selectedFrontEndNode == null) {
			return;
		}
		
		delList.clear();
		
		fepEntity = NodeUtils.getNodeEntity(selectedFrontEndNode).getFepEntity();
		if(null != fepEntity){
			codeField.setText(fepEntity.getCode());
//			nameField.setText(fepEntity.getFepName());
			pwdFld.setText(fepEntity.getLoginPassword());
			ipField.setIpAddress(fepEntity.getIpValue());
			directSwitchIpFld.setIpAddress(fepEntity.getDirectSwitchIp());
			fepViewModel.setIPSegmentList(fepEntity.getSegment());
		}
		descriptionFld.setText(selectedFrontEndNode.getName());
	}
	
	private void createDetail(JPanel parent) {
		JPanel container = new JPanel(new GridBagLayout());
		
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);
		
		codeField = new JTextField("",25);
		codeField.setEditable(false);
		codeField.setDocument(new TextFieldPlainDocument(codeField));
		container.add(new JLabel("编号"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(codeField,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
//		container.add(new StarLabel("(" + INPUT_LENGTH + ")"),new GridBagConstraints(2,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
//		nameField = new JTextField("",25);
//		nameField.setDocument(new TextFieldPlainDocument(nameField,true));
//		container.add(new JLabel("名称"),new GridBagConstraints(0,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		container.add(nameField,new GridBagConstraints(1,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
//		container.add(new StarLabel("(" + INPUT_LENGTH + ")"),new GridBagConstraints(2,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		pwdFld = new JPasswordField("", 15);
		container.add(new JLabel("密码"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(pwdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		container.add(new StarLabel("(" + PASSWORD_LENGTH + ")"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		ipField = new IpAddressField();
		container.add(new JLabel("IP"),new GridBagConstraints(0,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(ipField,new GridBagConstraints(1,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		container.add(new StarLabel(),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		directSwitchIpFld = new IpAddressField();
		container.add(new JLabel("起始设备"),new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(directSwitchIpFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		container.add(new StarLabel(),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		container.add(descriptionLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		container.add(descriptionFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
	
		JPanel middlePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		middlePnl.add(container);
		middlePnl.setBorder(BorderFactory.createTitledBorder("基本信息"));

		JPanel scrollIpPnl = getScrollIpPnl();
		
		JPanel bottomPnl = getBottomPnl();

		parent.setLayout(new BorderLayout());
		parent.add(middlePnl,BorderLayout.NORTH);
		parent.add(scrollIpPnl,BorderLayout.CENTER);
		parent.add(bottomPnl,BorderLayout.SOUTH);
	}
	
	private JPanel getScrollIpPnl(){
		JPanel scrollIpPnl = new JPanel(new BorderLayout());
		
		JPanel topPnl = new JPanel(new BorderLayout());
		JPanel topLeftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel topRightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		begintIPField = new IpAddressField();
		endIPField = new IpAddressField();
		begintIPField.setColumns(25);
		endIPField.setColumns(25);
		addIPBtn = buttonFactory.createButton(IPADD);
		delIPBtn = buttonFactory.createButton(IPDEL);
		topLeftPnl.add(new JLabel("起始IP"));
		topLeftPnl.add(begintIPField);
		topLeftPnl.add(new StarLabel());
		topLeftPnl.add(new JLabel("终止IP"));
		topLeftPnl.add(endIPField);
		topLeftPnl.add(new StarLabel());
		
		topRightPnl.add(addIPBtn);
		topRightPnl.add(delIPBtn);
		
		topPnl.add(topLeftPnl,BorderLayout.CENTER);
		topPnl.add(topRightPnl,BorderLayout.EAST);
		

		//下面的起始和终止IP的Table
		ipTable = new JTable();
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(ipTable);
		fepViewModel.getFEPIpTableModel().setColumnName(ipColumnName);
		fepViewModel.getFEPIpTableModel().setDataList(null);
		ipTable.setModel(fepViewModel.getFEPIpTableModel());
		
		scrollIpPnl.add(topPnl,BorderLayout.NORTH);
		scrollIpPnl.add(scrollPnl,BorderLayout.CENTER);
		scrollIpPnl.setBorder(BorderFactory.createTitledBorder("管理网段"));

		return scrollIpPnl;
	}
	
	private JPanel getBottomPnl(){
		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		closeButton = buttonFactory.createCloseButton();
		JButton saveBtnBar = buttonFactory.createButton(SAVE);
		bottomPnl.add(saveBtnBar);
		bottomPnl.add(closeButton);
		setCloseButton(closeButton);
		return bottomPnl;
	}
	
	/**
	 * 增加前置机
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存前置机",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		if(!isIncludeStartIP(directSwitchIpFld.getIpAddress())){
			JOptionPane.showMessageDialog(this, "管理网段中不包含起始设备IP，请配置", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		FEPEntity existEPEntity = getInputFEP(this.fepEntity);
		
		for (IPSegment seg : delList) {
			remoteServer.getService().deleteEntity(seg);
		}
		
		Task task = new RequestTask(existEPEntity);
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{

		private FEPEntity existEPEntity;
		public RequestTask(FEPEntity existEPEntity){
			this.existEPEntity = existEPEntity;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try{
				if (existEPEntity.getId() == null) {
					existEPEntity = (FEPEntity)remoteServer.getService().saveEntity(existEPEntity);
				} else {
					existEPEntity = (FEPEntity)remoteServer.getService().updateEntity(existEPEntity);
				}

				selectedFrontEndNode.setIpValue(existEPEntity.getIpValue());
				selectedFrontEndNode.setCode (existEPEntity.getCode());
				selectedFrontEndNode.setName(StringUtils.abbreviate(descriptionFld.getText().trim(),Constant.NODE_NAME_LENGTH));
				
				selectedFrontEndNode.setFepEntity(existEPEntity);

				if (selectedFrontEndNode.getId() == null) {
					selectedFrontEndNode = (FEPTopoNodeEntity)remoteServer.getService().saveEntity(selectedFrontEndNode);
				} else {
					selectedFrontEndNode = (FEPTopoNodeEntity)remoteServer.getService().updateEntity(selectedFrontEndNode);
				}

			}catch(Exception e){
				strategy.showErrorMessage("保存前置机异常");
				LOG.error("", e);
			}
			selectedFrontEndNode.setFepEntity(existEPEntity);
			delList.clear();
			begintIPField.setText("");
			endIPField.setText("");
			equipmentModel.fireEquipmentUpdated(selectedFrontEndNode);
			fillContents();
			
			List<FEPTopoNodeEntity> fepEntityList = (List<FEPTopoNodeEntity>) remoteServer
					.getService().findAll(FEPTopoNodeEntity.class);
			fepViewModel.getFEPTableModel().setDataList(fepEntityList);
			fepViewModel.getFEPTableModel().fireTableDataChanged();
			strategy.showNormalMessage("保存前置机成功");
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
	public FEPEntity getInputFEP(FEPEntity feps){
		FEPEntity fepEntity = null;
		if (null == feps){
			fepEntity = new FEPEntity();
		}
		else{
			fepEntity = feps;
		}
		fepEntity.setCode(codeField.getText().trim());
//		fepEntity.setFepName(nameField.getText().trim());
		fepEntity.setLoginPassword(String.valueOf(pwdFld.getPassword()).trim());
		fepEntity.setIpValue(ipField.getText().trim());
		fepEntity.setDirectSwitchIp(directSwitchIpFld.getText().trim());
		
		List<IPSegment> ipSegmentList = fepViewModel.getIPSegmentList();
		for(IPSegment se:ipSegmentList){
			se.setFepEntity(fepEntity);
		}
		
		fepEntity.setSegment(ipSegmentList);		
		if(null == fepEntity.getStatus()){
			StatusEntity statusEntity = new StatusEntity();
			fepEntity.setStatus(statusEntity);
		}
		
		return fepEntity;
	}
	
	@ViewAction(name=IPADD, icon=ButtonConstants.APPEND, text=APPEND,desc="添加管理网段",role=Constants.MANAGERCODE)
	public void ipAdd(){
		if (null == begintIPField.getIpAddress()
				|| "".equals(begintIPField.getIpAddress())
				|| null == endIPField.getIpAddress()
				|| "".equals(endIPField.getIpAddress())) {
			JOptionPane.showMessageDialog(this, "IP管理网段中不允许空值，请重新输入IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if(!verifyIP(begintIPField.getIpAddress(),endIPField.getIpAddress())){
			JOptionPane.showMessageDialog(this, "管理网段IP地址错误，请重新输入IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if(isIpSameValue()){
			return;
		}
		
		if(compareToOtherFEP()){
			return;
		}
		
		if(ClientUtils.isIllegal(begintIPField.getIpAddress())){
			JOptionPane.showMessageDialog(this, "不允许添加非法网段,请重新输入IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		IPSegment ipSegment = getInputIPSegment();
		if(null == ipSegment){
			return ;
		}
		fepViewModel.setIPSegment(ipSegment);
	}
	
	private boolean verifyIP(String beginIP,String endIP) {
		boolean isValid = true;
		
		String[] beginIPs = beginIP.split("\\.");
		String[] endIPs = endIP.split("\\.");
		if(beginIPs.length != 4 || endIPs.length != 4){
			isValid = false;
			return isValid;
		}
		
		for(int i = 0;i < 4;i++){
			if(i < 3){
				if(!beginIPs[i].equals(endIPs[i])){
					isValid = false;
					break;
				}
			}else if(i == 3){
				if(Integer.parseInt(beginIPs[i]) > Integer.parseInt(endIPs[i])){
					isValid = false;
					break;
				}
			}
		}
		
		return isValid;
	}

	@ViewAction(name=IPDEL, icon=ButtonConstants.DELETE, text=DELETE,desc="删除管理网段",role=Constants.MANAGERCODE)
	public void ipDel(){
		int[] rows = ipTable.getSelectedRows();
		if ( rows.length < 1){
			JOptionPane.showMessageDialog(this, "请选择管理网段","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		for (int i = 0 ; i < rows.length; i++ ){
			IPSegment ipSegment = fepViewModel.getIPSegment(rows[i]);
			if (ipSegment.getId() != null){
				delList.add(ipSegment);
			}
			else{   //modify:wuzhongwei  date:2010/9/20  
				fepViewModel.removeIPSegment(ipSegment);
			}
		}
		
		for(IPSegment ipSegment : delList){
			fepViewModel.removeIPSegment(ipSegment);
		}
	}
	
	public IPSegment getInputIPSegment(){
		String biginIp = begintIPField.getText();
		String endIp = endIPField.getText();
		
		if (null == biginIp || biginIp.trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入起始IP地址","提示",JOptionPane.NO_OPTION);
			return null;
		}
		else if (null == endIp || endIp.trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入终止IP地址","提示",JOptionPane.NO_OPTION);
			return null;
		}
		
		IPSegment ipSegment = new IPSegment();
		ipSegment.setBeginIp(biginIp.trim());
		ipSegment.setEndIp(endIp.trim());
		return ipSegment;
	}
	
	public String getIPFldValue(){
		return codeField.getText().trim();
	}
	
	public void clear(){
		codeField.setText("");
//		nameField.setText("");
		pwdFld.setText("");
		ipField.setIpAddress("");
		directSwitchIpFld.setIpAddress("");
		begintIPField.setIpAddress("");
		endIPField.setIpAddress("");
//		ipTableModel.setIPSegmentList(null);
		fepViewModel.removeAll();
	}
	
	//******判断输入的值是否合法****
	@SuppressWarnings("unchecked")
	private boolean isValids(){
		if (null == codeField.getText() 
				|| codeField.getText().trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入前置机编号","提示",JOptionPane.NO_OPTION);
			return false;
		}else if(codeField.getText().trim().length() < 1 || codeField.getText().trim().length() > 36){
			JOptionPane.showMessageDialog(this, "前置机编号错误，长度范围为1-36个字符","提示",JOptionPane.NO_OPTION);
			return false;
		}else{
			selectedFrontEndNode.getCode();
			List<FEPEntity> list = (List<FEPEntity>) remoteServer.getService().findAll(FEPEntity.class);
			if (selectedFrontEndNode.getId() == null) {
				for (FEPEntity fepEntity : list) {
					if (codeField.getText().trim().equals(fepEntity.getCode())) {
						JOptionPane.showMessageDialog(this, "前置机编号已存在，请重新输入","提示", JOptionPane.NO_OPTION);
						return false;
					}
				}
			} else {
				for (FEPEntity fepEntity : list) {
					if (!selectedFrontEndNode.getCode().equals(fepEntity.getCode())) {
						if (codeField.getText().trim().equals(fepEntity.getCode())) {
							JOptionPane.showMessageDialog(this,
									"前置机编号已存在，请重新输入", "提示",
									JOptionPane.NO_OPTION);
							return false;
						}
					}
				}
			}
		}
//		if (null == nameField.getText() 
//				|| nameField.getText().trim().equals("")){
//			JOptionPane.showMessageDialog(this, "请输入前置机名称","提示",JOptionPane.NO_OPTION);
//			return false;
//		}else if(nameField.getText().trim().length() < 1 || nameField.getText().trim().length() > 36){
//			JOptionPane.showMessageDialog(this, "前置机名称错误，长度范围为1-36个字符","提示",JOptionPane.NO_OPTION);
//			return false;
//		}
		if (null == pwdFld.getPassword() 
				|| pwdFld.getPassword().toString().trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入前置机密码","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if (null != pwdFld.getPassword() 
				&& (pwdFld.getPassword().length < 6 || pwdFld.getPassword().length > 16)){
			JOptionPane.showMessageDialog(this, "密码长度范围为" + PASSWORD_LENGTH + "，请重新输入新密码","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if (null == ipField.getText()
				|| ipField.getText().trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入完整的IP地址","提示",JOptionPane.NO_OPTION);
			return false;
		}else if(ClientUtils.isIllegal(ipField.getText())){
			JOptionPane.showMessageDialog(this, "IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if (null == directSwitchIpFld.getText()
				|| directSwitchIpFld.getText().trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入完整的起始设备IP地址","提示",JOptionPane.NO_OPTION);
			return false;
		}else if(ClientUtils.isIllegal(directSwitchIpFld.getText())){
			JOptionPane.showMessageDialog(this, "起始设备IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if(null == fepViewModel.getIPSegmentList()){
			JOptionPane.showMessageDialog(this, "请输入管理网段的IP地址","提示",JOptionPane.NO_OPTION);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean isIncludeStartIP(String directSwitchIp){
		boolean isInclude = false;
		
		List<IPSegment> ipSegmentList = fepViewModel.getIPSegmentList();
		for(IPSegment ipSegment : ipSegmentList){
			String oldBeginIp = ipSegment.getBeginIp();
			String oldEndIp = ipSegment.getEndIp();
			
			if (directSwitchIp.substring(0, directSwitchIp.lastIndexOf("."))
					.equals(oldBeginIp.substring(0, oldBeginIp.lastIndexOf(".")))) {// 存在起始设备的网段
				int directSwitchIpValue = NumberUtils.toInt(directSwitchIp
						.substring(directSwitchIp.lastIndexOf(".") + 1, directSwitchIp.length()));
				int oleBeginIpValue = NumberUtils.toInt(oldBeginIp
						.substring(oldBeginIp.lastIndexOf(".") + 1, oldBeginIp.length()));
				int oleEndIpValue = NumberUtils.toInt(oldEndIp
						.substring(oldEndIp.lastIndexOf(".") + 1, oldEndIp.length()));
				if(directSwitchIpValue >= oleBeginIpValue && directSwitchIpValue <= oleEndIpValue){
					isInclude = true;
					break;
				}
			}
		}
		return isInclude;
	}
	
	//本前置机下判断
	private boolean isIpSameValue(){
		
		boolean isSame = false;
		
		List<IPSegment> ipSegmentList = fepViewModel.getIPSegmentList();
		if (null == ipSegmentList || ipSegmentList.size() < 1){
			isSame = false;
			return isSame;
		}
		
		String newBeginIP = begintIPField.getText().trim();//新起始IP
		String newEndIP = endIPField.getText().trim();//新终止IP
		
		for (int i = 0 ; i < ipSegmentList.size() ;i++){
			String oldBeginIp = ipSegmentList.get(i).getBeginIp();
			String oldEndIp = ipSegmentList.get(i).getEndIp();
			
			if(isSubNetworkSegment(oldBeginIp,newBeginIP,oldEndIp,newEndIP)){
				JOptionPane.showMessageDialog(this, "管理网段有重复，请检查后重新输入","提示",JOptionPane.NO_OPTION);
				isSame = true;
				break;
			}
		}
		
		return isSame;
	}
	
	@SuppressWarnings("unchecked")
	private boolean compareToOtherFEP(){
		boolean isInclude = false;
		
		String newBeginIP = begintIPField.getText().trim();//新起始IP
		String newEndIP = endIPField.getText().trim();//新终止IP
		
		List<FEPEntity> fepEntities = (List<FEPEntity>) remoteServer.getService().findAll(FEPEntity.class);
		for(FEPEntity fepEntity : fepEntities){
			if(!fepEntity.getCode().equals(codeField.getText().trim())){
				for(IPSegment ipSegment : fepEntity.getSegment()){
					String oldBeginIp = ipSegment.getBeginIp();
					String oldEndIp = ipSegment.getEndIp();
					
					if(isSubNetworkSegment(oldBeginIp,newBeginIP,oldEndIp,newEndIP)){
						JOptionPane.showMessageDialog(this, "其他前置机管理网段中包含该网段，请检查后重新输入","提示",JOptionPane.NO_OPTION);
						isInclude = true;
						return isInclude;
					}
				}
			}
		}
		
		return isInclude;
	}
	
	//重复网段判断
	private boolean isSubNetworkSegment(String oldBeginIP,String newBeginIP,String oldEndIP,String newEndIP){
		boolean isSubNetworkSegment = false;
		
		if (!oldBeginIP.substring(0, oldBeginIP.lastIndexOf(".")).equals(
				newBeginIP.substring(0, newBeginIP.lastIndexOf(".")))) {//判断ip地址的前三位是否相同
			isSubNetworkSegment = false;
			return isSubNetworkSegment;
		}
		
		int oldBeginForthIP = Integer.parseInt(oldBeginIP.substring(oldBeginIP
				.lastIndexOf(".") + 1, oldBeginIP.length()));
		int newBeginForthIP = Integer.parseInt(newBeginIP.substring(newBeginIP
				.lastIndexOf(".") + 1, newBeginIP.length()));
		
		int oldEndForthIP = Integer.parseInt(oldEndIP.substring(oldEndIP
				.lastIndexOf(".") + 1, oldEndIP.length()));
		int newEndForthIP = Integer.parseInt(newEndIP.substring(newEndIP
				.lastIndexOf(".") + 1, newEndIP.length()));
		
		if ((oldBeginForthIP <= newBeginForthIP && newBeginForthIP <= oldEndForthIP)
				|| (newEndForthIP >= oldBeginForthIP && oldEndForthIP >= newEndForthIP)
				|| (newBeginForthIP <= oldBeginForthIP && newEndForthIP >= oldEndForthIP)
				) {
			isSubNetworkSegment = true;
			return isSubNetworkSegment;
		}

		return isSubNetworkSegment;
	}
	
	//****************************

	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, lastSelectionChanged);
	}
	
	public JCloseButton getCloseButton() {
		return closeButton;
	}
	
	private JCloseButton closeButton;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name = FrontEndManagementModel.ID)
	private FrontEndManagementModel fepViewModel;
	
	private static final String LIST_CARD = "LIST_CARD";
	private static final String DETAIL_CARD = "DETAIL_CARD";
	private static final long serialVersionUID = 1L;
	public static final String ID = "frontEndInfolView";
}