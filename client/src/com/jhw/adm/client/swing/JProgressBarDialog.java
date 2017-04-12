package com.jhw.adm.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;

import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.switcher.Task;

public class JProgressBarDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String TITLE = "提示";
	private static final String DEFAULT_OPERATION = "下载";
	private static final String DEFAULT_COLSE = "关闭";
	
	private JLabel subjectLabel;
	private JProgressBar progressBar;
	private JLabel detailLabel;
	private JButton closeBtn;
	private JProgressBarModel model;
	private MessageProcessorStrategy strategy;
	private int min = 0;
	private int max = 0;
	private String operation = StringUtils.EMPTY;
	private boolean determine = false;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
		subjectLabel.setText(operation);
	}
	
	public JProgressBarModel getModel(){
		return model;
	}
	
	/*
	 *设定进度条对话框采用的数据模型，用于更新对话框中控件的显示数据。 
	 */
	public void setModel(JProgressBarModel model){
		this.model = model;
		
		this.model.addPropertyChangeListener(JProgressBarModel.DETERMINE_PROPERTY_NAME,determineListener);
		this.model.addPropertyChangeListener(JProgressBarModel.DETAIL_PROPERTY_NAME,detailListener);
		this.model.addPropertyChangeListener(JProgressBarModel.PROGRESS_PROPERTY_NAME, progressListener);
		this.model.addPropertyChangeListener(JProgressBarModel.ENABLED_PROPERTY_NAME, enabledListener);
	}
	
	public MessageProcessorStrategy getStrategy() {
		return strategy;
	}
	
	/*
	 *设定进度条对话框采用的原始数据处理策略，用于告诉数据模型类数据发生了变化。 
	 */
	public void setStrategy(MessageProcessorStrategy strategy) {
		this.strategy = strategy;
	}
	
	public JProgressBarDialog(String title,int min,int max,Component viewPart,String operation,boolean determine){
		this.min = min;
		this.max = max;
		this.operation = operation;
		this.determine = determine;
		
		this.setModal(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setIconImage(ClientUtils.getRootFrame().getIconImage());
		this.setTitle(title);
		this.setSize(300, 160);
		this.createDialog(this);
		this.setLocationRelativeTo(viewPart);
	}
	
	public JProgressBarDialog(Component viewPart){
		this(TITLE,0,1,viewPart,DEFAULT_OPERATION,false);
	}
	
	private final PropertyChangeListener determineListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setDetermine(((Boolean) evt.getNewValue()));
		}
	};

	public void setDetermine(final boolean determine){
		if(SwingUtilities.isEventDispatchThread()){
			this.progressBar.setIndeterminate(determine);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					setDetermine(determine);
				}
			});
		}
	}
	
	private final PropertyChangeListener detailListener = new PropertyChangeListener(){
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setDetailLabel((String) evt.getNewValue());
		}
	};
	
	private void setDetailLabel(final String newValue){
		if(SwingUtilities.isEventDispatchThread()){
			String[] infos = newValue.split("\\|");
			if(infos[1].equals(model.NORMAL)){
				detailLabel.setForeground(new Color(35,175,98));
			}else{
				detailLabel.setForeground(new Color(231,88,20));
			}
			detailLabel.setText(infos[0]);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setDetailLabel(newValue);
				}
			});
		}
	}
	
	private final PropertyChangeListener progressListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setValue((Integer)evt.getNewValue());
		}
	};
	
	private void setValue(final int newValue){
		if(SwingUtilities.isEventDispatchThread()){
			progressBar.setValue(newValue);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setValue(newValue);
				}
			});
		}
	}
	
	private final PropertyChangeListener enabledListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setEnabled((Boolean) evt.getNewValue());
		}
	};
	
	private void createDialog(JDialog parent){
	
		GridBagLayout dialogLayout = new GridBagLayout();
		parent.setLayout(dialogLayout);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(8, 8, 8, 8);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		
		JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
		
		parent.add(contentPanel, constraints);
		
		JPanel subjectPanel = new JPanel();
		contentPanel.add(subjectPanel, BorderLayout.PAGE_START);
		createSubjectPanel(subjectPanel);
		
		JPanel progressBarPanel = new JPanel();
		contentPanel.add(progressBarPanel, BorderLayout.CENTER);
		createProgressBarPanel(progressBarPanel);
		
		JPanel closePanel = new JPanel();
		contentPanel.add(closePanel, BorderLayout.SOUTH);
		createClosePanel(closePanel);
	}
	
	private void createClosePanel(JPanel parent){
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		closeBtn = new JButton();
		closeBtn.setAction(new CloseAction("关闭"));
		closeBtn.setEnabled(false);
		parent.add(closeBtn);
	}
	
	//button status
	public void setEnabled(final Boolean enabled){
		if(SwingUtilities.isEventDispatchThread()){
			this.closeBtn.setEnabled(enabled);
			closeBtn.requestFocus();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setEnabled(enabled);
				}
			});
		}
	}
	
	public void run(final Task task){
		final Runnable taskThread = new Runnable() {
			@Override
			public void run() {
				task.run();
			}
		};

		final Runnable openThread = new Runnable() {
			@Override
			public void run() {
				openDialog();
			}
		};
		ExecutorService executorService = Executors.newSingleThreadExecutor(); 
		executorService.execute(openThread);
		executorService.execute(taskThread);
	}
	
	private void openDialog(){
		if(SwingUtilities.isEventDispatchThread()){
			this.setVisible(true);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					openDialog();
				}
			});
		}
	}
	
	private void closeDialog(){
		this.model.removePropertyChangeListener(JProgressBarModel.DETERMINE_PROPERTY_NAME,determineListener);
		this.model.removePropertyChangeListener(JProgressBarModel.DETAIL_PROPERTY_NAME,detailListener);
		this.model.removePropertyChangeListener(JProgressBarModel.PROGRESS_PROPERTY_NAME, progressListener);
		this.model.removePropertyChangeListener(JProgressBarModel.ENABLED_PROPERTY_NAME, enabledListener);
		this.dispose();
	}
	
	private void createProgressBarPanel(JPanel parent){
		parent.setLayout(new BorderLayout(5,5));
		progressBar = new JProgressBar(this.min, this.max);
//		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(this.determine);
		progressBar.setValue(0);
		parent.add(progressBar, BorderLayout.PAGE_START);
		detailLabel = new JLabel(StringUtils.EMPTY);
		parent.add(detailLabel, BorderLayout.CENTER);
	}
	
	private void createSubjectPanel(JPanel parent){
		parent.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		subjectLabel = new JLabel(this.operation, UIManager.getIcon("OptionPane.informationIcon"), SwingConstants.LEADING); 
		parent.add(subjectLabel);
	}
	
	private class CloseAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		public CloseAction(String name){
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			close();
		}
		
		private void close(){
			if(SwingUtilities.isEventDispatchThread()){
				closeDialog();
			}else{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						close();
					}
				});
			}
		}
	}
}
