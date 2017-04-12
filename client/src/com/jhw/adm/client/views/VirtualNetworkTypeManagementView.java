package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.MODIFY;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.VirtualType;

@Component(VirtualNetworkTypeManagementView.ID)
@Scope(Scopes.DESKTOP)
public class VirtualNetworkTypeManagementView extends ViewPart {

	private static final Logger LOG = LoggerFactory.getLogger(VirtualNetworkTypeManagementView.class);
	
	private static final long serialVersionUID = 4640294744540971791L;
	public static final String ID = "virtualNetworkTypeManagementView";
	private final int DEFAULT_STATUS = 0;
	private final int CREATE_STATUS = 1;
	private final int MODIFY_STATUS = 2;
	private int operator_status = 0;
	
	private final JPanel detailPanel = new JPanel();
	private final JPanel typePanel = new JPanel();
	private final JTextField typeField = new JTextField();
	private final JLabel picturePreviewLabel = new JLabel();
//	private JButton fileChooseButton = new JButton();
	private final JXHyperlink fileChooseHyperlink = new JXHyperlink();
	
	private final JToolBar editToolBar = new JToolBar();
	private JButton createTypeButton = null;
	private JButton editTypeButton = null;
	private JButton deleteTypeButton = null;

	private final JPanel buttonPanel = new JPanel();
	private JButton saveButton = null;
	private JButton closeButton = null;
	
	private final JScrollPane scrollPane = new JScrollPane();
	private final JXTable typeTable = new JXTable();
	private final VirtualTypeTableModel tableModel = new VirtualTypeTableModel();
	
	private VirtualType virtualType = null;
	
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	protected void initialize(){
		
		buttonFactory = actionManager.getButtonFactory(this);
		
		createTablePanel(scrollPane);
		createDetailPanel(detailPanel);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(1);
		splitPane.setDividerLocation(150);
		splitPane.setLeftComponent(scrollPane);
		splitPane.setRightComponent(detailPanel);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		this.setLayout(new BorderLayout());
		this.add(splitPane, BorderLayout.CENTER);
		this.setTitle("虚拟网元类型管理");
		this.setViewSize(600, 500);
		
		initializeValue();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeValue() {
		List<VirtualType> list = (List<VirtualType>) remoteServer.getService().findAll(VirtualType.class);
		if(list.size() < 0){
			return;
		}
		setTableValue(list);
	}

	private void setTableValue(final List<VirtualType> list) {
		if(SwingUtilities.isEventDispatchThread()) {
			tableModel.setData(list);
			tableModel.fireTableDataChanged();
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					setTableValue(list);
				}
			});
		}
	}

	/**
	 * create table panel
	 */
	private void createTablePanel(JScrollPane parent) {
		typeTable.setModel(tableModel);
//		typeTable.setSize(200, typeTable.getHeight());
		typeTable.setEditable(false);
		typeTable.setSortable(false);
		typeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setComponentValue(false);
			}
		});
		setTableValue(null);
		
		parent.getViewport().add(typeTable);
	}
	
	protected void setComponentValue(boolean status) {
		int selectedRow = typeTable.getSelectedRow();
		if(selectedRow < 0){
			return;
		}
		virtualType = tableModel.getValue(selectedRow);
		
		setTypeText(virtualType.getType());
//		For test
//		byte[] bytes = { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72,
//				68, 82, 0, 0, 0, 80, 0, 0, 0, 17, 8, 6, 0, 0, 0, 74, 21, 43,
//				100, 0, 0, 1, 20, 73, 68, 65, 84, 120, -38, -19, 88, 11, 14,
//				-125, 48, 8, -83, 86, -67, -1, 57, 118, -125, 93, 96, -105,
//				-14, -73, -68, 38, 24, -58, -104, -61, -22, 22, -46, -108,
//				-124, -56, -85, -56, -70, -41, -54, 83, 67, 8, 97, 109, -102,
//				-90, 122, -122, -125, -69, -128, -32, 118, 127, 84, -49, -16,
//				68, 98, -87, 4, 118, 93, -9, -47, -81, -86, 81, 60, -127, -65,
//				-82, 93, 9, -84, 4, 58, -23, -127, -92, -60, 82, -111, -83, 24,
//				71, 20, -12, 88, -89, 109, -37, -28, 60, -106, 88, -2, -82,
//				-90, -74, -69, 42, -116, 0, -122, -29, 60, -49, 43, -103, 5,
//				47, -53, -110, 38, 65, 19, -16, 86, 103, 28, -57, -19, 28, 98,
//				-119, 97, -108, 15, 46, 52, -37, 27, -33, 8, -28, -114, 73, 28,
//				-63, -44, 6, 60, -42, 33, 66, 121, -82, -122, 57, -127, 124, 1,
//				57, -127, -38, 120, 34, 80, 74, 116, -33, -9, -121, 48, 53, 85,
//				111, 117, -16, 71, -121, 97, 120, 25, -41, 48, -35, -62, -38,
//				102, -30, 109, 65, 29, 47, 89, 68, 104, -89, 124, 51, 78, 96,
//				-42, 45, 44, 87, -49, -78, 106, -106, -99, -29, -95, -50, 95,
//				118, -96, 76, -94, -58, 106, -63, -92, 72, 82, 61, -67, -44,
//				-79, 92, 75, -7, -39, 61, -112, -97, -104, -90, 41, 53, 86, 43,
//				70, 28, 99, 124, 83, 79, 47, 117, -72, -22, 34, 87, 98, -115,
//				-64, 44, 21, 46, -11, 57, -48, 114, -19, -23, -25, -64, -6, 86,
//				113, -30, 77, -92, 126, 15, 60, -9, 61, -16, 9, -43, 113, -84,
//				-111, 8, 63, -5, -1, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96,
//				-126 };
////		for(int i = 0; i < bytes.length; i++){
////			System.err.println("From database : " + bytes[i]);
////		}
//		resetLabelImageIcon(getImageIcon(bytes));
		
		this.imageBytes = virtualType.getBytes();
		resetLabelImageIcon(getImageIcon(this.imageBytes));
		
		setComponentStatus(status);
	}
	
	protected void resetLabelImageIcon(final ImageIcon imageIcon) {
		if(SwingUtilities.isEventDispatchThread()){
			this.picturePreviewLabel.removeAll();
			this.picturePreviewLabel.setIcon(imageIcon);
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					resetLabelImageIcon(imageIcon);
				}
			});
		}
	}

	private ImageIcon getImageIcon(byte[] bytes) {
		return new ImageIcon(bytes);
	}

	private void createDetailPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		createEditToolBar(editToolBar);
		createElementPanel(typePanel);
		createButtonPanel(buttonPanel);
		
		detailPanel.add(editToolBar, BorderLayout.NORTH);
		detailPanel.add(typePanel, BorderLayout.CENTER);
		detailPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * create edit panel
	 * @param parent
	 */
	private void createEditToolBar(JToolBar parent) {
		createTypeButton = buttonFactory.createButton(APPEND);
		editTypeButton = buttonFactory.createButton(MODIFY);
		deleteTypeButton = buttonFactory.createButton(DELETE);
		
		parent.add(createTypeButton);
		parent.add(editTypeButton);
		parent.add(deleteTypeButton);
		parent.setFloatable(false);
	}
	
	/**
	 * @param parent
	 */
	private void createElementPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		typeField.setColumns(25);
//		fileChooseButton.setAction(new FileChooseAction("..."));
		fileChooseHyperlink.setAction(new FileChooseAction("图片选择"));
		fileChooseHyperlink.setUnclickedColor(Color.RED);
		fileChooseHyperlink.setClickedColor(Color.RED);
		setComponentStatus(false);
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(new JLabel("类型"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(0, 3, 5, 0), 0, 0));
		panel.add(typeField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(0, 10, 5, 0), 0, 0));
		
		panel.add(new JLabel("预览"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(0, 3, 5, 0), 0, 0));
		panel.add(picturePreviewLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(0, 10, 5, 0), 0, 0));
		panel.add(fileChooseHyperlink, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(0, 10, 5, 0), 0, 0));
		
		parent.add(panel);
	}

	/**
	 * create button panel
	 * @param parent
	 */
	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		saveButton = buttonFactory.createButton(SAVE);
		closeButton = buttonFactory.createCloseButton();
		this.setCloseButton(closeButton);
		
		parent.add(saveButton);
		parent.add(closeButton);
	}
	
	private void setComponentStatus(final boolean status){
		if(SwingUtilities.isEventDispatchThread()){
			typeField.setEditable(status);
			fileChooseHyperlink.setEnabled(status);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					setComponentStatus(status);
				}
			});
		}
	}
	
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND,desc="新增自定义虚拟网元类型",role=Constants.MANAGERCODE)
	public void append(){
		this.virtualType = null;
		this.imageBytes = null;
		operator_status = CREATE_STATUS;
		setComponentStatus(true);
		setTypeText("");
		resetLabelImageIcon(null);
	}
	
	@ViewAction(name=MODIFY, icon=ButtonConstants.MODIFY,desc="修改自定义虚拟网元类型",role=Constants.MANAGERCODE)
	public void modify(){
		int selectedRow = typeTable.getSelectedRowCount();
		if(0 == selectedRow){
			JOptionPane.showMessageDialog(this, "请选择需要修改的类型", "提示", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		operator_status = MODIFY_STATUS;
		setComponentValue(true);
		setComponentStatus(true);
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="删除自定义虚拟网元类型",role=Constants.MANAGERCODE)
	public void delete(){
//		int selectedRow = typeTable.getSelectedRowCount();
		if (null == this.virtualType) {
			JOptionPane.showMessageDialog(this, "请选择需要删除的类型", "提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		VirtualType virtualType = this.virtualType;
		
//		if (1 == virtualType.getId()) {
//			JOptionPane.showMessageDialog(this, "系统默认网元类型不能删除", "提示",JOptionPane.ERROR_MESSAGE);
//			return;
//		}
		
		String where = " where type = " + virtualType.getId();
		List<VirtualNodeEntity> virtualNodeEntityList = (List<VirtualNodeEntity>)remoteServer.getService().findAll(VirtualNodeEntity.class, where);
		if(virtualNodeEntityList.size() != 0){
			JOptionPane.showMessageDialog(this, "该虚拟网元类型正被使用，不允许删除", "提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "确认删除该条数据？", "提示",JOptionPane.YES_NO_OPTION);
		if ((result == JOptionPane.NO_OPTION) || (result == JOptionPane.CLOSED_OPTION)) {
			return;
		}

		Task task = new DeleteRequestTask(virtualType);
		showMessageDialog(task);
	}
	
	private class DeleteRequestTask implements Task{

		private VirtualType type;
		public DeleteRequestTask(VirtualType virtualType){
			this.type = virtualType;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().deleteEntity(type);
			}catch(Exception e){
				strategy.showErrorMessage("删除自定义虚拟网元类型异常");
				LOG.error("Error occur when deleting virtual type!", e);
			}
			setComponentStatus(false);
			setTypeText("");
			resetLabelImageIcon(null);
			initializeValue();
			operator_status = DEFAULT_STATUS;
			strategy.showNormalMessage("删除自定义虚拟网元类型成功");
		}
		
	}
	
	/*
	 * 去掉字符串首尾部的空格(全角/半角)
	 */
	private final String regStartSpace = "^[　 ]*";
	private final String regEndSpace = "[　 ]*$";
	@SuppressWarnings("unchecked")
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存自定义虚拟网元类型",role=Constants.MANAGERCODE)
	public void save(){
		
		if(null == this.virtualType){
			this.virtualType = new VirtualType();
		}
		
		String type = typeField.getText().replaceAll(regStartSpace, "").replaceAll(regEndSpace, "");
		if((StringUtils.isBlank(type)) || (type.length() < 1 || type.length() > 10)){
			JOptionPane.showMessageDialog(this, "虚拟网元类型长度范围为：1-10字符，请重新输入", "提示", JOptionPane.NO_OPTION);
			return;
		}
//		if((type.length() < 1 || type.length() > 10)){
//			JOptionPane.showMessageDialog(this, "虚拟网元类型长度范围为：1-10字符，请重新输入", "提示", JOptionPane.NO_OPTION);
//			return;
//		}
		Object[] parmas = {type};
		String where = " where entity.type=?";
		List<VirtualType> list = (List<VirtualType>) remoteServer.getService().findAll(VirtualType.class, where,parmas);
		if(null != list && list.size() > 0){
			VirtualType entity = list.get(0);
			if((null != entity) && (!ObjectUtils.equals(this.virtualType.getId(), entity.getId()))){
				JOptionPane.showMessageDialog(this, "虚拟网元类型不能重复，请重新输入", "提示", JOptionPane.NO_OPTION);
				return;
			}
		}
		if(null == this.imageBytes){
			JOptionPane.showMessageDialog(this, "虚拟网元类型图片预览不能为空，请选择图片", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		Task task = new RequestTask(this.virtualType,type);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private final VirtualType virtualType;
		private final String type;
		public RequestTask(VirtualType virtualType,String type){
			this.virtualType = virtualType;
			this.type = type;
		}
		
		@Override
		public void run() {
			try{
				this.virtualType.setType(this.type);
				this.virtualType.setBytes(imageBytes);
				if(operator_status == CREATE_STATUS){
					remoteServer.getService().saveEntity(this.virtualType);
				}else if(operator_status == MODIFY_STATUS){
					remoteServer.getService().updateEntity(this.virtualType);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存自定义虚拟网元类型异常");
				LOG.error("Error occur when saving virtual type!", e);
			}
			setComponentStatus(false);
			setTypeText("");
			resetLabelImageIcon(null);
			initializeValue();
			operator_status = DEFAULT_STATUS;
			strategy.showNormalMessage("保存自定义虚拟网元类型成功");
		}
	}
	
	private void setTypeText(final String text){
		if(SwingUtilities.isEventDispatchThread()){
			typeField.setText(text);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					setTypeText(text);
				}
			});
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy("保存虚拟网元类型", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"保存虚拟网元类型",true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}
	
	private class VirtualTypeTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private final TableColumnModelExt columnModel;
		private List<VirtualType> list = null;
		
		public VirtualTypeTableModel() {
			list = new ArrayList<VirtualType>();
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt typeColumn = new TableColumnExt(modelIndex++, 200);
			typeColumn.setIdentifier("type");
			typeColumn.setHeaderValue("网元类型");
			columnModel.addColumn(typeColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return list.size();
		}
		
		@Override
		public String getColumnName(int col) {
			return columnModel.getColumn(col).getHeaderValue().toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object value = null;
			
			if (rowIndex < list.size()) {
				VirtualType type = list.get(rowIndex);
				switch (columnIndex) {
				case 0:
					value = type.getType();
					break;
				default:
					break;
				}
			}
			return value;
		}
		
		public VirtualType getValue(int row){
			VirtualType value = null;
			if(row < list.size()){
				value = list.get(row);
			}
			return value;
		}
		
		public void setData(List<VirtualType> list) {
			if (list == null) {
				return;
			}
			this.list = list;
		}
	}

	private class FileChooseAction extends AbstractAction{

		private static final long serialVersionUID = 4981419968685828766L;
		private JFileChooser fileChooser = null;
		private final String PATH_PNG = ".png";
		private final String PATH_JPG = ".jpg";
		
		public FileChooseAction(String name){
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			FileFilter fileFilter = new FileNameExtensionFilter("(.png & .jpg)", "png","jpg");
			fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(fileFilter);
			int returnVlaue = fileChooser.showOpenDialog(VirtualNetworkTypeManagementView.this);
			if(JFileChooser.APPROVE_OPTION == returnVlaue){
				File file = fileChooser.getSelectedFile();
				String path = file.getAbsolutePath();
				if((path.toLowerCase().endsWith(PATH_PNG)) || (path.toLowerCase().endsWith(PATH_JPG))){
					uploadAndPreviewNativeImage(path);
				}else{
					JOptionPane.showMessageDialog(VirtualNetworkTypeManagementView.this, "文件格式不为png或jpg格式，请重新选择", "提示", JOptionPane.NO_OPTION);
					return;
				}
			}
		}
	}

	/**
	 * upload native image from user's computer and preview the image 
	 * @param path
	 */
	private byte[] imageBytes = null;
	public void uploadAndPreviewNativeImage(String filePath) {
		imageBytes = getImageBytes(filePath);
//		for(int i = 0; i < imageBytes.length; i++){
//			System.err.println("convert to image : " + imageBytes[i]);
//		}
		
		if(null != imageBytes){
			resetLabelImageIcon(getImageIcon(imageBytes));
		}
	}
	
	private byte[] getImageBytes(String filePath){
		
		File imageFile = new File(filePath);
		if(!imageFile.exists()){
			try {
				imageFile.createNewFile();
			} catch (IOException e) {
				LOG.error("error occur when create new File", e);
			}
		}
		
		if(imageFile.length() > Constant.IMAGE_SIZE){
			JOptionPane.showMessageDialog(this, "图片文件大小不能大于100KB，请重新选择", "提示", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		ImageIcon imageIcon = new ImageIcon(filePath);
		BufferedImage bufferedImage = Images.toBufferedImage(imageIcon.getImage());
		
		if (bufferedImage.getWidth() > Constant.IMAGE_WIDTH_HEIGHT
				|| bufferedImage.getHeight() > Constant.IMAGE_WIDTH_HEIGHT) {
			JOptionPane.showMessageDialog(this, "图片文件宽度和高度不能大于120*120，请重新选择", "提示", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
		
		try {
			boolean backValue = ImageIO.write(bufferedImage, "png", imageOutputStream);
			if(false == backValue){
				return null;
			}
		} catch (IOException e) {
			LOG.error("error occur when writing image", e);
		}finally{
			if(null != imageOutputStream){
				try {
					imageOutputStream.close();
				} catch (IOException e) {
					LOG.error("error occur when closing ByteArrayOutputStream", e);
				}
			}
		}
		
//		For test
//		FileOutputStream fileOutputStream = null;
//		try {
//			fileOutputStream = new FileOutputStream(new File("D:\\ADMClient\\fuck.png"));
//			ImageIO.write(bufferedImage, "PNG", fileOutputStream);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			if(null != fileOutputStream){
//				try {
//					fileOutputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		return imageOutputStream.toByteArray();
	}
	
}
