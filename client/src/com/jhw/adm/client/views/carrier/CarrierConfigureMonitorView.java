package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.carriers.MonitorConfigEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(CarrierConfigureMonitorView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierConfigureMonitorView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle("载波机定时监控");
		setViewSize(450, 320);
		setLayout(new BorderLayout());

		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		buttonFactory = actionManager.getButtonFactory(this);
		JButton saveButton = buttonFactory.createButton(SAVE);
		JButton closeButton = buttonFactory.createCloseButton();
		toolPanel.add(saveButton);
		toolPanel.add(closeButton);
		setCloseButton(closeButton);

		JPanel content = new JPanel();
		createContents(content);
		add(toolPanel, BorderLayout.PAGE_END);
		add(content, BorderLayout.CENTER);
		content.setBorder(BorderFactory.createTitledBorder("载波机定时监控"));
		queryData();
	}

	private void createContents(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel container = new JPanel(new GridBagLayout());
		parent.add(container);

		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);

		listenIntervalField = new JFormattedTextField(integerFormat);
		sendIntervalField = new JFormattedTextField(integerFormat);
		timeoutField = new JFormattedTextField(integerFormat);
		listenIntervalField.setColumns(20);
		sendIntervalField.setColumns(20);
		timeoutField.setColumns(20);
		listenIntervalField.setValue(new Integer(5));
		sendIntervalField.setValue(new Integer(5));
		timeoutField.setValue(new Integer(5));

		container.add(new JLabel("监测频率（分钟）"), new GridBagConstraints(0, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));
		container.add(listenIntervalField, new GridBagConstraints(1, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 30, 10, 0), 0, 0));
		container.add(new JLabel("建议1-5（分钟）"), new GridBagConstraints(2, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));

		container.add(new JLabel("发包间隔（毫秒）"), new GridBagConstraints(0, 1, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));
		container.add(sendIntervalField, new GridBagConstraints(1, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 30, 10, 0), 0, 0));
		container.add(new JLabel("建议500-1000（毫秒）"), new GridBagConstraints(2,
				1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));

		container.add(new JLabel("命令超时（秒）"), new GridBagConstraints(0, 2, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));
		container.add(timeoutField, new GridBagConstraints(1, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 30, 10, 0), 0, 0));
		container.add(new JLabel("建议2-10（秒）"), new GridBagConstraints(2, 2, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		List<MonitorConfigEntity> monitorConfigEntityList = (List<MonitorConfigEntity>) remoteServer
				.getService().findAll(MonitorConfigEntity.class);
		if (null == monitorConfigEntityList) {
			return;
		}
		setValue(monitorConfigEntityList);
	}

	private void setValue(List<MonitorConfigEntity> monitorConfigEntityList) {
		if (monitorConfigEntityList.size() < 1) {
			return;
		}
		monitorEntity = monitorConfigEntityList.get(0);
		if (null == monitorEntity) {
			return;
		}

		listenIntervalField.setText(String
				.valueOf(monitorEntity.getFrequence()));// 监测频率
		sendIntervalField.setText(String.valueOf(monitorEntity.getDistance()));// 发包间隔
		timeoutField.setText(String.valueOf(monitorEntity.getOuttime()));// 命令超时
	}

	@ViewAction(name = SAVE, icon = ButtonConstants.SAVE, desc = "保存载波机定时监控信息", role = Constants.MANAGERCODE)
	public void save() {
		int frequence = NumberUtils.toInt(listenIntervalField.getText().trim());
		int distance = NumberUtils.toInt(sendIntervalField.getText().trim());
		int timeout = NumberUtils.toInt(timeoutField.getText().trim());

		if (null == monitorEntity) {
			monitorEntity = new MonitorConfigEntity();
		}
		monitorEntity.setFrequence(frequence);
		monitorEntity.setDistance(distance);
		monitorEntity.setOuttime(timeout);
		
		Task task = new RequestTask(monitorEntity);
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		private MonitorConfigEntity monitorEntity = null;
		public RequestTask(MonitorConfigEntity monitorEntity){
			this.monitorEntity = monitorEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().updateEntity(monitorEntity);
			}catch(Exception e){
				strategy.showErrorMessage("保存载波机定时监控信息异常");
				LOG.error("CarrierConfigureMonitorView.save() error", e);
			}
			strategy.showNormalMessage("保存载波机定时监控信息成功");
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
	
	private ButtonFactory buttonFactory;
	private MonitorConfigEntity monitorEntity;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;
	
	private static final Logger LOG = LoggerFactory.getLogger(CarrierConfigureMonitorView.class);
	private JFormattedTextField listenIntervalField;
	private JFormattedTextField sendIntervalField;
	private JFormattedTextField timeoutField;
	private static final long serialVersionUID = 1L;
	public static final String ID = "carrierConfigureMonitorView";
}