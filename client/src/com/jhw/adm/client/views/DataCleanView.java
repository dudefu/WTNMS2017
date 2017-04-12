package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.BEGIN;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

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
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.util.Constants;

@Component(DataCleanView.ID)
@Scope(Scopes.DESKTOP)
public class DataCleanView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "dataCleanView";
	private static final Logger LOG = LoggerFactory.getLogger(DataCleanView.class);
	
	private JButton startBtn;
	private JButton closeBtn;
	private JComboBox strategyBox;
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	public void initialize(){
		
		setTitle("��������");
		setSize(400, 300);
		setLayout(new BorderLayout());

		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel parent = new JPanel();
		createContents(parent);
		add(parent, BorderLayout.CENTER);
		
	}
	public void createContents(JPanel parent){
		parent.setLayout(new BorderLayout());

		JPanel cleanPanel = new JPanel();
		createCleanContent(cleanPanel);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		startBtn = buttonFactory.createButton(BEGIN);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		buttonPanel.add(startBtn);
		buttonPanel.add(closeBtn);
		
		parent.add(cleanPanel, BorderLayout.CENTER);
		parent.add(buttonPanel, BorderLayout.PAGE_END);
	}
	private void createCleanContent(JPanel parent) {
		parent.setLayout(new BorderLayout());

		strategyBox = new JComboBox(new String[] { 
				"���ܼ������", "�澯�¼�����"});
		strategyBox.setPreferredSize(new Dimension(150, strategyBox.getPreferredSize().height));
		
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel descriptionPanel = new JPanel(new SpringLayout());
		wrapper.add(descriptionPanel);
		
		descriptionPanel.add(new JLabel("1. ѡ�����ܼ�����ݡ���"
				+ String.format("ϵͳ��ɾ������(%S��%S��)֮ǰ�����ܼ������", Calendar
						.getInstance().get(Calendar.YEAR), Calendar
						.getInstance().get(Calendar.MONTH) + 1)));
		descriptionPanel.add(new JLabel("2. ѡ�񡰸澯�¼����ݡ���"
				+ String.format("ϵͳ��ɾ������(%S��%S��)֮ǰ�ĸ澯�¼�����", Calendar
						.getInstance().get(Calendar.YEAR), Calendar
						.getInstance().get(Calendar.MONTH) + 1)));
		
		descriptionPanel.add(strategyBox);
		SpringUtilities.makeCompactGrid(descriptionPanel, 3, 1, 6, 6, 6, 6);
		parent.add(wrapper, BorderLayout.CENTER);
	}

	@ViewAction(name=BEGIN,icon=ButtonConstants.START,desc="ɾ������", role=Constants.MANAGERCODE)
	public void begin(){
		
		int sureDelete = JOptionPane.showConfirmDialog(this, "ȷ��ɾ������", "��ʾ", JOptionPane.OK_CANCEL_OPTION);
		
		if(sureDelete == JOptionPane.OK_OPTION){
			String selectedParam = strategyBox.getSelectedItem().toString();

			Task task = new RequestTask(selectedParam);
			showMessageDialog(task, "ɾ������");
		}
	}
	
	private class RequestTask implements Task{
		
		private String selectedParam = "";
		public RequestTask(String selectedParam){
			this.selectedParam = selectedParam;
		}
		
		@Override
		public void run() {
			try{
				if("���ܼ������".equals(selectedParam)){
					remoteServer.getNmsService().deleteRmonCount();
				}else if("�澯�¼�����".equals(selectedParam)){
					remoteServer.getNmsService().deleteTrapWarningInfo();
				}
			}catch(Exception e){
				strategy.showErrorMessage("ɾ�������쳣");
				LOG.error("DataCleanView.clean() error", e);
			}
			strategy.showNormalMessage("ɾ�����ݳɹ�");
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
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
}