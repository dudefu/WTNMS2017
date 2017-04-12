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
import java.net.MalformedURLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.core.WarningAudioPlayer;
import com.jhw.adm.client.model.ClientConfig;
import com.jhw.adm.client.model.ClientConfigRepository;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.util.Constants;

@Component(WarningAudioConfigureView.ID)
@Scope(Scopes.DESKTOP)
public class WarningAudioConfigureView extends ViewPart {

	public static final String ID = "warningAudioConfigureView";
	private static final long serialVersionUID = 1L;

	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	@Resource(name=WarningAudioPlayer.ID)
	private WarningAudioPlayer warningAudioPlayer;

	@Resource(name=ClientConfigRepository.ID)
	private ClientConfigRepository clientConfigRepository;

	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	public String file_path = "";
	public static int repeatNumber = 1;

	public File selectedFile = null;

	private JCheckBox defaultCheckBox = new JCheckBox();
	private NumberField repeatNumFld = new NumberField(5, 0, 1, 65535, true);
	private JTextField noteFileFld = new JTextField();

	private ButtonFactory buttonFactory = null;
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	private JButton notePlayButton = null;

	@PostConstruct
	public void initialize() {
		buttonFactory = actionManager.getButtonFactory(this);
		this.setLayout(new BorderLayout());

		JPanel content = new JPanel();
		this.createContents(content);
		this.add(content, BorderLayout.CENTER);
		this.setTitle("告警声音配置");
		this.setSize(480, 500);
		setValue();
	}

	public void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());

		JPanel fileSelectedContainer = new JPanel(new GridBagLayout());
		fileSelectedContainer.setBorder(BorderFactory.createTitledBorder("声音文件"));

		noteFileFld.setEditable(false);
		noteFileFld.setBackground(Color.WHITE);
		JButton fileChooserButton = new JButton("选择文件");
		notePlayButton = new JButton("试听", imageRegistry.getImageIcon(ButtonConstants.START));
		fileSelectedContainer.add(new JLabel("告警声音"), new GridBagConstraints(0,
				0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 3), 0, 0));
		fileSelectedContainer.add(noteFileFld, new GridBagConstraints(1, 0, 4,
				1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 3), 240, 0));
		fileSelectedContainer.add(fileChooserButton, new GridBagConstraints(6,
				0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 3), 0, 0));
		fileSelectedContainer.add(notePlayButton, new GridBagConstraints(7, 0,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 0), 0, 0));

		JPanel infoWrapper = new JPanel(new BorderLayout());
		infoWrapper.setBorder(BorderFactory.createTitledBorder("播放次数设置"));
		JPanel jPanel = new JPanel(new BorderLayout());

		JPanel infoContainer = new JPanel(new GridBagLayout());
		infoContainer.add(new JLabel("播放次数"), new GridBagConstraints(0, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 10), 0, 0));
		infoContainer.add(repeatNumFld, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(5, 10, 10, 0), 0, 0));
		infoContainer.add(new StarLabel("(1-65535)"), new GridBagConstraints(2,
				0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		infoContainer.add(defaultCheckBox, new GridBagConstraints(0, 1, 2,
				1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 10), 0, 0));
		defaultCheckBox.setText("默认声音告警");
		defaultCheckBox.setSelected(true);
		jPanel.add(infoContainer, BorderLayout.WEST);
		infoWrapper.add(jPanel, BorderLayout.NORTH);

		JPanel buttomPnl = new JPanel();
		createButtonPanel(buttomPnl);

		parent.add(fileSelectedContainer, BorderLayout.PAGE_START);
		parent.add(infoWrapper, BorderLayout.CENTER);
		parent.add(buttomPnl, BorderLayout.SOUTH);

		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileChooserAction();
			}
		});

		notePlayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				play();
			}
		});

	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		parent.add(saveBtn);
		parent.add(closeBtn);
	}

	private JFileChooser fileChooser;

	private void fileChooserAction() {
		FileFilter fileFilter = new FileNameExtensionFilter(".wav", "wav");

		if(!StringUtils.isBlank(file_path)){
			fileChooser = new JFileChooser(file_path.substring(0, file_path.lastIndexOf("\\")));
		}
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		fileChooser.addChoosableFileFilter(fileFilter);
		int returnVal = fileChooser.showOpenDialog(WarningAudioConfigureView.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// 被选择的文件
			selectedFile = fileChooser.getSelectedFile();
			// 文件路径
			String path = selectedFile.getAbsolutePath();
			if (path.toLowerCase().endsWith(".wav")) {
				noteFileFld.setText(path);
				file_path = path;
			} else {
				JOptionPane.showMessageDialog(this, "文件格式不为wav格式，请重新选择", "提示",JOptionPane.NO_OPTION);
				noteFileFld.setText(file_path);
				return;
			}
		}
	}

	public void setValue() {
		if (clientModel.getClientConfig().getWavFileName() != null) {
			file_path = clientModel.getClientConfig().getWavFileName();
			noteFileFld.setText(file_path);
		}
		if (clientModel.getClientConfig().getRePeatNum() != 0) {
			repeatNumber = clientModel.getClientConfig().getRePeatNum();
			repeatNumFld.setText("" + repeatNumber);
		}
		defaultCheckBox.setSelected(clientModel.getClientConfig().isDefaultWarningAudio());
	}

	// save the file_path to client configure file
	@ViewAction(name = SAVE, icon = ButtonConstants.SAVE, desc = "保存告警声音配置信息", role = Constants.MANAGERCODE)
	public void save() {
		if (!isValids()) {
			return;
		}
		if (!isExistFile()) {
			return;
		}
		
		String repeatNum = repeatNumFld.getText().trim();
		if (StringUtils.isBlank(repeatNum) || 0 == NumberUtils.toInt(repeatNum)) {
			JOptionPane.showMessageDialog(this, "播放次数必须大于0，请重新输入", "提示",JOptionPane.NO_OPTION);
			repeatNumFld.setText(Integer.toString(clientModel.getClientConfig().getRePeatNum()));
			repeatNumFld.requestFocus();
			return;
		}
		ClientConfig clientConfig = clientModel.getClientConfig();
		clientConfig.setWavFileName(file_path);
		clientConfig.setRePeatNum(NumberUtils.toInt(repeatNum));
		clientConfig.setDefaultWarningAudio(defaultCheckBox.isSelected());

		Task task = new RequestTask(clientConfig);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private ClientConfig clientConfig;
		public RequestTask(ClientConfig clientConfig){
			this.clientConfig = clientConfig;
		}
		
		@Override
		public void run() {
			clientConfigRepository.saveConfig(clientConfig);
			noteFileFld.setText(clientModel.getClientConfig().getWavFileName());
			repeatNumFld.setText("" + clientModel.getClientConfig().getRePeatNum());
			strategy.showNormalMessage("保存告警声音配置成功");
		}
		
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy("保存", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"保存",false);
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

	private boolean isValids() {
		boolean isValid = true;

//		if (null == noteFileFld || "".equals(noteFileFld.getText())) {
//			JOptionPane.showMessageDialog(this, "请选择提示告警声音文件", "提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
		if (StringUtils.isBlank(repeatNumFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "播放次数错误，范围为：1-65535", "提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		return isValid;
	}

	private boolean isExistFile() {
		boolean isExist = true;

		selectedFile = new File(file_path);
		if (!selectedFile.exists()) {
			JOptionPane.showMessageDialog(this, "提示告警声音文件不存在,请重新选择", "提示",
					JOptionPane.NO_OPTION);
			isExist = false;
			return isExist;
		}
		return isExist;
	}

	@SuppressWarnings("static-access")
	public void play() {
		if (!warningAudioPlayer.playStatus) {
			warningAudioPlayer.playStatus = true;
			Thread t = new Thread(new RePlayThread());
			t.start();
			notePlayButton.setText("停止");
			notePlayButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.STOP));
		} else if (warningAudioPlayer.playStatus) {
			warningAudioPlayer.playStatus = false;
			warningAudioPlayer.stop();
			notePlayButton.setText("试听");
			notePlayButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.START));
		}
	}

	public JButton getCloseButton() {
		return this.closeBtn;
	}

	private class RePlayThread implements Runnable {
		private String fileURL = "";

		public RePlayThread() {
			//
		}


		@Override
		public void run() {
			fileURL = noteFileFld.getText().trim();
			File file = new File(fileURL);
			if (file.exists()) {
				try {
					warningAudioPlayer.play(1, file.toURI().toURL(), false);
				} catch (MalformedURLException e) {
					JOptionPane.showMessageDialog(null, "试听出错，文件不存在", "提示",JOptionPane.NO_OPTION);
					warningAudioPlayer.playStatus = false;
				}
			} else {
				JOptionPane.showMessageDialog(null, "试听出错，文件不存在", "提示",JOptionPane.NO_OPTION);
				warningAudioPlayer.playStatus = false;
			}
			notePlayButton.setText("试听");
			notePlayButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.START));
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		warningAudioPlayer.stop();
		warningAudioPlayer.playStatus = false;
		notePlayButton.setText("试听");
		notePlayButton.setIcon(imageRegistry
				.getImageIcon(ButtonConstants.START));
		notePlayButton.setEnabled(true);
	}

}
