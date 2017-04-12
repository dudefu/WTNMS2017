package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.CONFIRM;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmTypeCategory;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.WarningConfirmStrategy;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningEntity;

@Component(SingleWarningConfirmView.ID)
@Scope(Scopes.DESKTOP)
public class SingleWarningConfirmView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "singleWarningConfirmView";
	private static final Logger LOG = LoggerFactory.getLogger(SingleWarningConfirmView.class);

	private JTextField createTimeField = new JTextField();
	private JTextField equipmentField = new JTextField();
	private JTextField subnetNameField = new JTextField();
	private JTextArea contentArea = new JTextArea(3,1);
	private JTextField levelField = new JTextField();
	private JTextField typeField = new JTextField();
	private JTextField confirmTimeField = new JTextField();
	private JTextField confirmUserNameField = new JTextField();
	private JTextArea commentArea = new JTextArea(3,1);
	private JScrollPane scrollPane = null;
	private JPanel detailPanel = new JPanel();
	
	private JButton confirmButton = new JButton();
	private JButton closeButton = new JButton();
	private JPanel buttonPanel = new JPanel();
	private ButtonFactory buttonFactory = null;
	
	private WarningEntity singleAlarm = null;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=AlarmTypeCategory.ID)
	private AlarmTypeCategory alarmTypeCategory;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		
		createScrollPane();
		createDetailPanel(detailPanel);
		createButtonPanel(buttonPanel);
		initializeComponentValue();
		
		this.setLayout(new BorderLayout());
		this.add(detailPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.PAGE_END);
		this.setTitle("告警详细信息");
		this.setViewSize(500, 450);
	}

	private void createScrollPane() {
		scrollPane = new JScrollPane(commentArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		commentArea.setCaretPosition(0);
//		scrollPane.getViewport().add(commentArea);
	}

	@SuppressWarnings("unchecked")
	private void initializeComponentValue() {
		setComponentEditable();
		singleAlarm = alarmModel.getSingleConfirmAlarm();
		singleAlarm = remoteServer.getService().findById(singleAlarm.getId(), WarningEntity.class);
		if(null == singleAlarm){
			return;
		}
		
		createTimeField.setText(dateFormatter.format(singleAlarm.getCreateDate()));
		equipmentField.setText(singleAlarm.getNodeName());
		subnetNameField.setText(singleAlarm.getSubnetName());
		contentArea.setText(singleAlarm.getDescs());
		levelField.setText(alarmSeverity.get(singleAlarm.getWarningLevel()).getKey());
		typeField.setText(alarmTypeCategory.get(singleAlarm.getWarningCategory()).getKey());
		if(singleAlarm.getCurrentStatus() == Constants.CONFIRM){
			confirmUserNameField.setText(singleAlarm.getConfirmUserName());
			confirmTimeField.setText(dateFormatter.format(singleAlarm.getConfirmTime()));
			commentArea.setText(singleAlarm.getComment());
			commentArea.setEditable(false);
		}
	}

	private void setComponentEditable() {
		createTimeField.setEditable(false);
		equipmentField.setEditable(false);
		contentArea.setEditable(false);
		levelField.setEditable(false);
		typeField.setEditable(false);
		typeField.setEditable(false);
		confirmTimeField.setEditable(false);
		confirmUserNameField.setEditable(false);
		subnetNameField.setEditable(false);
	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		confirmButton = buttonFactory.createButton(CONFIRM);
		closeButton = buttonFactory.createCloseButton();
		parent.add(confirmButton);
		parent.add(closeButton);
		this.setCloseButton(closeButton);
	}

	private void createDetailPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));

		createTimeField.setColumns(50);
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("发生时间"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(createTimeField,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("设备名称"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(equipmentField,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("所属子网"),new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(subnetNameField,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("内容"),new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(contentArea,new GridBagConstraints(1,3,5,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("级别"),new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(levelField,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("类型"),new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(typeField,new GridBagConstraints(1,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("确认时间"),new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(confirmTimeField,new GridBagConstraints(1,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("确认人员"),new GridBagConstraints(0,7,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(confirmUserNameField,new GridBagConstraints(1,7,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

		panel.add(new JLabel("描述"),new GridBagConstraints(0,8,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(scrollPane,new GridBagConstraints(1,8,1,3,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		parent.add(panel);
		
		Border border = new JTextField().getBorder();
		contentArea.setBorder(border);
		contentArea.setLineWrap(true);
//		commentArea.setBorder(border);
		commentArea.setLineWrap(true);
	}
	
	@ViewAction(name=CONFIRM, icon=ButtonConstants.SAVE, desc="确认单个告警信息",role=Constants.MANAGERCODE)
	public void confirm(){
		if(null == singleAlarm){
			return;
		}
		
		if(Constants.CONFIRM == singleAlarm.getCurrentStatus()){
			JOptionPane.showMessageDialog(this, "该告警信息已被确认，不允许重复确认", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		String comment = commentArea.getText().trim();
		if(comment.length() > 255){
			JOptionPane.showMessageDialog(this, "描述长度不允许超过255个字符，请重新输入", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int backSelected = JOptionPane.showConfirmDialog(this, "你确定确认该告警信息", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(backSelected != JOptionPane.OK_OPTION){
			return;
		}
		singleAlarm.setComment(comment);
		List<WarningEntity> confirmList = new ArrayList<WarningEntity>();
		confirmList.add(singleAlarm);
		
		Task task = new RequestTask(confirmList);
		showMessageDialog(task);
	}

	private class RequestTask implements Task{

		private List<WarningEntity> list = null;
		public RequestTask(List<WarningEntity> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			confirmedWarning = new ArrayList<WarningEntity>();
			unConfirmedWarning = new ArrayList<WarningEntity>();
			alarmModel.updateSelectedWarningAttributes(list, unConfirmedWarning, confirmedWarning);
			try{
				remoteServer.getNmsService().confirmWarningInfo(unConfirmedWarning);
			}catch(Exception e){
				strategy.showErrorMessage("确认单个告警异常");
				LOG.error("Error occur when confirming common warning(s)", e);
			}
			strategy.showNormalMessage(confirmedWarning.size(), unConfirmedWarning.size());
			alarmModel.confirmAlarm(list);
			initializeComponentValue();
		}
	}
	
	private List<WarningEntity> confirmedWarning = null;	
	private List<WarningEntity> unConfirmedWarning = null;
	private JProgressBarModel progressBarModel;
	private WarningConfirmStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new WarningConfirmStrategy("确认单个告警", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"确认单个告警",true);
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
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
}
