package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientConfigRepository;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.ComponentTitledBorder;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.util.Constants;

@Component(ConfigureSysGisView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureSysGisView extends ViewPart{
	private static final long serialVersionUID = 1L;

	public static final String ID = "configureSysGisView";
	
	private JPanel centerPnl = new JPanel();
	
	private JPanel serverPnl = new JPanel();
	private JRadioButton serverRadBtn = new JRadioButton();
	private JLabel serverAddrLbl = new JLabel();
	private IpAddressField serverAddrFld = new IpAddressField();
	private JLabel serverPortLbl = new JLabel();
	private NumberField serverPortFld = new NumberField();
	
	private JPanel filePnl = new JPanel();
	private JRadioButton fileRadBtn = new JRadioButton();
	private JTextField fileChooserFld = new JTextField();
	private JButton fileChooserBtn = new JButton();
	
	
	private JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private JFileChooser fileChooser  = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureSysGisView.class);

	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=ClientConfigRepository.ID)
	private ClientConfigRepository clientConfigRepository;
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this); 
		init();
		queryData();
	}
	
	private void init(){
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initCenterPnl(){
		ButtonGroup group = new ButtonGroup ();
		group.add(serverRadBtn);
		group.add(fileRadBtn);
		
		JPanel middlePnl = new JPanel(new GridBagLayout());
		middlePnl.add(serverAddrLbl,new GridBagConstraints(0,0,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		middlePnl.add(serverAddrFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,200),0,0));
		middlePnl.add(serverPortLbl,new GridBagConstraints(0,1,1,1,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		middlePnl.add(serverPortFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,200),0,0));
		serverPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		serverPnl.add(middlePnl);
		
		ComponentTitledBorder serverBorder = new ComponentTitledBorder(serverRadBtn,serverPnl,BorderFactory.createTitledBorder(""));
		serverPnl.setBorder(serverBorder);

		
		filePnl.setLayout(new GridBagLayout());
		filePnl.add(fileChooserFld,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		filePnl.add(fileChooserBtn,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,5),0,0));
		ComponentTitledBorder fileBorder = new ComponentTitledBorder(fileRadBtn,filePnl,BorderFactory.createTitledBorder(""));
		filePnl.setBorder(fileBorder);
		
		JPanel container = new JPanel(new GridBagLayout());
		container.add(serverPnl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		container.add(filePnl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(container);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
	}
	
	private void setResource(){
		serverRadBtn.setText("服务器设置");
		fileRadBtn.setText("文件设置");
		
		serverRadBtn.setActionCommand("server");
		fileRadBtn.setActionCommand("file");
		
		serverAddrLbl.setText("服务器地址");
		serverPortLbl.setText("服务器端口");
		
		serverRadBtn.setEnabled(false);
		fileRadBtn.setSelected(true);
		fileChooserFld.setEditable(false);
		setEnable(false);
		fileChooserFld.setBackground(Color.WHITE);
		
		fileChooserBtn.setText("选择文件");
		
		fileChooserFld.setColumns(60);
		
		fileChooserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = fileChooserFld.getText();
				if (null == path || "".equals(path.trim())){
					fileChooser = new JFileChooser();
				}
				else{
					fileChooser = new JFileChooser(path.substring(0,path.lastIndexOf("\\")));
				}
				
				fileChooser.setFileFilter(new MXDFileFilter());
				
				int returnVal = fileChooser.showOpenDialog(ConfigureSysGisView.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getPath();
					fileChooserFld.setText(filePath);
				}
			}
		});
		
		ActionButtonListener actionListener = new ActionButtonListener();
		serverRadBtn.addActionListener(actionListener);
		fileRadBtn.addActionListener(actionListener);
	}
	
	class ActionButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JRadioButton button = (JRadioButton)e.getSource();
			if (button.getActionCommand().equals("server")){
				setEnable(true);
			}
			else{
				setEnable(false);
			}
		}
	}
	
	private void setEnable(boolean bool){
		serverAddrFld.setEnabled(bool);
		serverPortFld.setEnabled(bool);
		
		fileChooserFld.setEnabled(!bool);
		fileChooserBtn.setEnabled(!bool);
		
		serverAddrFld.setBackground(Color.WHITE);
		serverPortFld.setBackground(Color.WHITE);
		fileChooserFld.setBackground(Color.WHITE);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				updateUI();
			}
		});
	}
	
	private void queryData(){
		String path = clientModel.getClientConfig().getMapFileName();
		fileChooserFld.setText(path);
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存GIS文件路径",role=Constants.MANAGERCODE)
	public void save(){
		String mapFileName = fileChooserFld.getText().trim();
		clientModel.getClientConfig().setMapFileName(mapFileName);
		
		Task task = new RequestTask();
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		public RequestTask(){
			//
		}
		
		@Override
		public void run() {
			clientConfigRepository.saveConfig(clientModel.getClientConfig());
			strategy.showNormalMessage("保存GIS文件路径成功");
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
	
	class MXDFileFilter extends FileFilter{
		 @Override 
	     public boolean accept(File f) {
			 if (f.getName().endsWith("MXD") || f.getName().endsWith("mxd") || f.isDirectory()) {
				return true;
			} 
			 return false; 
	     } 
	     @Override 
	     public String getDescription() { 
	    	 // TODO Auto-generated method stub 
	    	 return "(*.MXD)"; 
	     } 
	}
}
