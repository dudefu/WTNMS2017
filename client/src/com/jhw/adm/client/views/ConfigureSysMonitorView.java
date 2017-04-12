package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import com.jhw.adm.client.model.MonitorParamModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.util.Constants;

@Component(ConfigureSysMonitorView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureSysMonitorView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureSysMonitorView";
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureSysMonitorView.class);
	
	private JPanel topPnl = new JPanel();
	private JPanel heartPnl = new JPanel();
	private JLabel heartLbl = new JLabel();
	private NumberField heartFld = new NumberField(5,0,1,65535,true);
	private JLabel timeoutLbl = new JLabel();
	private NumberField timeoutFld = new NumberField(5,0,1,65535,true);
	
	private JPanel centerPnl = new JPanel();
	
	private JPanel topoPnl = new JPanel();
	private JLabel topoTimeoutLbl = new JLabel();
	private NumberField topoTimeoutFld = new NumberField(5,0,1,65535,true);
	
	private JPanel synPnl = new JPanel();
	private JLabel synTimeoutLbl = new JLabel();
	private NumberField synTimeoutFld = new NumberField(5,0,1,65535,true);
	
	private JPanel jmsPnl = new JPanel();
	private JLabel jmsTimeoutLbl = new JLabel();
	private NumberField jmsTimeoutFld = new NumberField(5,0,1,65535,true);
	
	private final static String RANGE_VALUE = "(1-65535)";
	
	private JPanel bottomPnl = new JPanel();
	private JButton closeBtn= null;
	
	private TimeConfig timeConfig;
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@Resource(name=MonitorParamModel.ID)
	private MonitorParamModel monitorParamModel;
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this); 
		init();
		queryData();
	}
	
	private void init(){
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initTopPnl(){
//		heartPnl.setLayout(new SpringLayout());
//		heartPnl.add(heartLbl);
//		heartPnl.add(heartFld);
//		heartPnl.add(timeoutLbl);
//		heartPnl.add(timeoutFld);
//
//		SpringUtilities.makeCompactGrid(heartPnl, 2, 2, 6, 6, 55, 15);
		
		heartPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(heartLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(heartFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,58,5,0),0,0));
		panel.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		panel.add(timeoutLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(timeoutFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,58,5,0),0,0));
		panel.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		heartPnl.add(panel);
		
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(heartPnl);
	}
	
	private void initCenterPnl(){
		topoPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel1 = new JPanel(new GridBagLayout());
		panel1.add(topoTimeoutLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,4,10,0),0,0));
		panel1.add(topoTimeoutFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel1.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		topoPnl.add(panel1);

		synPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.add(synTimeoutLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,4,5,0),0,0));
		panel2.add(synTimeoutFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,5,0),0,0));
		panel2.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,5,0),0,0));
		synPnl.add(panel2);
		
		jmsPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel3 = new JPanel(new GridBagLayout());
		panel3.add(jmsTimeoutLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,4,10,0),0,0));
		panel3.add(jmsTimeoutFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,58,10,0),0,0));
		panel3.add(new StarLabel(RANGE_VALUE),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		jmsPnl.add(panel3);
		
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(topoPnl,BorderLayout.NORTH);
		centerPnl.add(synPnl,BorderLayout.CENTER);
		centerPnl.add(jmsPnl,BorderLayout.SOUTH);
	}
	
	private void initBottomPnl(){
		JButton saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);	
	}
	
	private void setResource(){
		topPnl.setBorder(BorderFactory.createTitledBorder("心跳"));
		heartLbl.setText("心跳频率(秒)");
		timeoutLbl.setText("网络延时(秒)");
		
		topoPnl.setBorder(BorderFactory.createTitledBorder("拓扑发现"));
		topoTimeoutLbl.setText("单个设备超时时间(秒)");
		
		synPnl.setBorder(BorderFactory.createTitledBorder("设备同步"));
		synTimeoutLbl.setText("单个设备超时时间(秒)");

		jmsPnl.setBorder(BorderFactory.createTitledBorder("参数配置"));
		jmsTimeoutLbl.setText("超时时间(秒)");
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		 List<TimeConfig> list = (List<TimeConfig>)remoteServer.getService().findAll(TimeConfig.class);
		
		if (null == list || list.size() < 1){
			return;
		}
		
		setValue(list);
	}
	
	/**
	 * 设置控件中的值
	 * @param list
	 */
	private void setValue(List<TimeConfig> list){
		//保存到model里
		monitorParamModel.setTimeConfig(list);
		
		timeConfig = monitorParamModel.getTimeConfig();
		int heart = timeConfig.getHeartbeatMaxTime();
		int heartDelay = timeConfig.getHearbeatdelayMaxTime();
		int topo = timeConfig.getTuopoMaxTime();
		int syn = timeConfig.getSynchoizeMaxTime();
		int deviceTime = timeConfig.getParmConfigMaxTime();
		if (0 == heart){
			heartFld.setText("");
		}
		else{
			heartFld.setText(heart + "");
		}
		if(0 >= heartDelay){
			timeoutFld.setText("");
		}
		else{
			timeoutFld.setText(heartDelay + "");
		}
		if (0 >= topo){
			topoTimeoutFld.setText("");
		}
		else{
			topoTimeoutFld.setText(topo + "");
		}
		if (0 >= syn){
			synTimeoutFld.setText("");
		}
		else{
			synTimeoutFld.setText(syn + "");
		}
		if (0 >= deviceTime){
			jmsTimeoutFld.setText("");
		}
		else{
			jmsTimeoutFld.setText(deviceTime + "");
		}
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存监测参数",role=Constants.MANAGERCODE)
	public void save(){
		if (!isValids()){
			return;
		}
		
		int heart = NumberUtils.toInt(heartFld.getText()) ;
		int heartDelay = NumberUtils.toInt(timeoutFld.getText()) ;
		int topo = NumberUtils.toInt(topoTimeoutFld.getText());
		int syn = NumberUtils.toInt(synTimeoutFld.getText());
		int deviceTime = NumberUtils.toInt(jmsTimeoutFld.getText());
		
		if (timeConfig == null){
			timeConfig = new TimeConfig();
		}

		timeConfig.setHeartbeatMaxTime(heart);
		timeConfig.setHearbeatdelayMaxTime(heartDelay);
		timeConfig.setTuopoMaxTime(topo);
		timeConfig.setSynchoizeMaxTime(syn);
		timeConfig.setParmConfigMaxTime(deviceTime);
		
		Task task  = new RequestTask(timeConfig);
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		private TimeConfig timeConfig = null;
		public RequestTask(TimeConfig timeConfig){
			this.timeConfig = timeConfig;
		}
		
		@Override
		public void run() {
			try{
				if(this.timeConfig.getId() == null){
					timeConfig = (TimeConfig) remoteServer.getService().saveEntity(timeConfig);
				}else{
					timeConfig = (TimeConfig) remoteServer.getService().updateEntity(timeConfig);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存监测参数异常");
				queryData();
				LOG.error("ConfigureSysMonitorView.save() error", e);
			}
			strategy.showNormalMessage("保存监测参数成功");
			queryData();
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
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	public boolean isValids(){
		boolean isValid = true;
		
		if(null == heartFld.getText() || "".equals(heartFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "心跳频率不能为空","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == timeoutFld.getText() || "".equals(timeoutFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "网络延时不能为空","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (null == topoTimeoutFld.getText() || "".equals(topoTimeoutFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "拓扑发现超时时间不能为空","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (null == synTimeoutFld.getText() || "".equals(synTimeoutFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "设备同步超时时间不能为空","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (null == jmsTimeoutFld.getText() || "".equals(jmsTimeoutFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "参数配置超时时间不能为空","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
}
