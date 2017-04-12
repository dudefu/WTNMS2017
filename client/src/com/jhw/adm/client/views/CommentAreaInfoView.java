package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.OK;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(CommentAreaInfoView.ID)
@Scope(Scopes.DESKTOP)
public class CommentAreaInfoView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "commentAreaInfoView";
	private static final Logger LOG = LoggerFactory.getLogger(CommentAreaInfoView.class);
	
	private final JPanel commentPanel = new JPanel();
	private JTextArea commentArea = null;
	
	private final JPanel buttonPanel = new JPanel();
	private JButton downloadButton = null;
	private JButton closeButton = null;
	private ButtonFactory buttonFactory = null;
	
	private CommentTopoNodeEntity commentTopoNodeEntity;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	public CommentTopoNodeEntity getCommentTopoNodeEntity() {
		return commentTopoNodeEntity;
	}
	public void setCommentTopoNodeEntity(CommentTopoNodeEntity commentTopoNodeEntity) {
		this.commentTopoNodeEntity = commentTopoNodeEntity;
	}
	
	PropertyChangeListener commentListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue() instanceof CommentTopoNodeEntity){
				initializeData();
			}
		}
	};

	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);

		createCommentPanel(commentPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout(8, 8));
		this.add(commentPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setViewSize(400, 300);
		this.setTitle("注释信息");
		
		initializeData();
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, commentListener);
	}
	
	private void initializeData() {
		CommentTopoNodeEntity topoNodeEntity = (CommentTopoNodeEntity) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),CommentTopoNodeEntity.class);
		setCommentTopoNodeEntity(topoNodeEntity);
		
		if(null == getCommentTopoNodeEntity()){
			return;
		}
		
		commentArea.setText(topoNodeEntity.getComment());
	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		downloadButton = buttonFactory.createButton(OK);
		closeButton = buttonFactory.createCloseButton();
		this.setCloseButton(closeButton);
		
		parent.add(downloadButton);
		parent.add(closeButton);
	}

	private void createCommentPanel(JPanel parent) {
		parent.setLayout(new BorderLayout(18, 18));
		
		commentArea = new JTextArea(3, 1);
		commentArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(commentArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		commentArea.setCaretPosition(0);

		parent.add(scrollPane, BorderLayout.CENTER);
	}
	
	private final String regEndSpace = "[　 ]*$";//去掉字符串尾部的空格(全角/半角)
	@ViewAction(name=OK, icon=ButtonConstants.SAVE, desc="保存注释信息",role=Constants.MANAGERCODE)
	public void ok() {
		CommentTopoNodeEntity nodeEntity = this.getCommentTopoNodeEntity();
		if(null == nodeEntity){
			nodeEntity = new CommentTopoNodeEntity();
		}
		
		String comment = commentArea.getText().replaceAll(regEndSpace, "");
		if(comment.length() > 50){
			JOptionPane.showMessageDialog(this, "注释内容长度不允许大于50个字符，请重新输入", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		nodeEntity.setComment(comment);
		
		Task task = new RequestTask(nodeEntity);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private CommentTopoNodeEntity topoNodeEntity = null;
		public RequestTask(CommentTopoNodeEntity commentTopoNodeEntity){
			this.topoNodeEntity = commentTopoNodeEntity;
		}
		
		@Override
		public void run() {
			try{
				if(null == topoNodeEntity.getId()){
					topoNodeEntity = (CommentTopoNodeEntity) remoteServer.getService().saveEntity(topoNodeEntity);
				}else{
					topoNodeEntity = (CommentTopoNodeEntity) remoteServer.getService().updateEntity(topoNodeEntity);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存注释信息异常");
				LOG.error("CommentAreaInfoView.updateEntity() error", e);
			}
			setCommentTopoNodeEntity(topoNodeEntity);
			equipmentModel.fireEquipmentUpdated(topoNodeEntity);
			strategy.showNormalMessage("保存注释信息成功");
		}
		
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy("保存注释信息", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"保存注释信息",true);
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
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, commentListener);
	}
	
}
