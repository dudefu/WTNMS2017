package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.EXPORT;
import static com.jhw.adm.client.core.ActionConstants.IMPORT;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.util.Constants;

@Component(DataImportExportView.ID)
@Scope(Scopes.DESKTOP)
public class DataImportExportView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "dataImportExportView";
	
	private JButton importBtn;
	private JButton exportBtn;
	
	private JTextField importFileFld;
	private JTextField exportFileFld;
	
	private File selectedFile;
	
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@PostConstruct
	public void initialize(){
		
		setTitle("���ݵ��뵼��");
		setSize(400,300);
		setLayout(new BorderLayout());
		
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel parent = new JPanel();
		createContents(parent);
		add(parent, BorderLayout.CENTER);
	}
	
	public void createContents(JPanel parent){
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel container = new JPanel(new SpringLayout());
		parent.add(container);
		
		importFileFld = new JTextField();
		exportFileFld = new JTextField();
		
		importFileFld.setColumns(45);
		exportFileFld.setColumns(45);
		
		importFileFld.setEditable(false);
		exportFileFld.setEditable(false);
		
		importBtn = buttonFactory.createButton(IMPORT);
		exportBtn = buttonFactory.createButton(EXPORT);
		
		//����
		container.add(new JLabel("�����ļ�"));
		container.add(importFileFld);
		JButton importChooserButton = new JButton("ѡ��"); 
		container.add(importChooserButton);
		importChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooserAction("import");
			}
		});
		container.add(new JLabel(""));
		container.add(new JLabel(""));
		container.add(importBtn);
		
		//����
		container.add(new JLabel("�����ļ�"));
		container.add(exportFileFld);
		JButton exportChooserButton = new JButton("ѡ��"); 
		container.add(exportChooserButton);
		exportChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooserAction("export");
			}
		});
		
		container.add(new JLabel(""));
		container.add(new JLabel(""));
		container.add(exportBtn);
		
		SpringUtilities.makeCompactGrid(container, 4, 3, 6, 6, 6, 6);
	}
	
	public void fileChooserAction(String operationType){

		FileFilter fileFilter = new CsvFileFilter("csv");
		
		JFileChooser fileChooser = new CsvFileChooser();
		fileChooser.addChoosableFileFilter(fileFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int returnVal = fileChooser.showOpenDialog(DataImportExportView.this);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			//��ѡ����ļ�
			selectedFile = fileChooser.getSelectedFile();
			//�ļ�·��
			String path = selectedFile.getAbsolutePath();
			
			if("import".equals(operationType)){
				importFileFld.setText(path);
			}else if("export".equals(operationType)){
				exportFileFld.setText(path);	
			}
		}
	}
	
	//����
	@ViewAction(name=IMPORT,icon=ButtonConstants.IMPORT,desc="����������־��Ϣ",role=Constants.MANAGERCODE)
	public void importFlie(){
		
		if(null == importFileFld.getText() || "".equals(importFileFld.getText())){
			JOptionPane.showMessageDialog(this, "��ѡ���ļ�", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
	}
	//����
	@ViewAction(name=EXPORT,icon=ButtonConstants.EXPORT,desc="�����ļ�",role=Constants.MANAGERCODE)
	public void export() {
		
		if(null == exportFileFld.getText() || "".equals(exportFileFld.getText())){
			JOptionPane.showMessageDialog(this, "��ѡ���ļ�", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
	}
	
	private class CsvFileChooser extends JFileChooser{
		
		private static final long serialVersionUID = 1L;

		public CsvFileChooser(){
			super();
		}
		
		@Override
		public void approveSelection(){
			
			File file = new File(getSelectedFile().getPath());
			if(file.exists()){
				super.approveSelection();
			}else{
				JOptionPane.showMessageDialog(null, "�ļ������ڣ�������ѡ��", "��ʾ", JOptionPane.NO_OPTION);
			}
		}
	}
	
	private class CsvFileFilter extends FileFilter{
		
		String fileExtension;
		
		public CsvFileFilter(String ext){
			this.fileExtension = ext;
		}
		
		@Override
		public boolean accept(File file){
			if(file.isDirectory()){
				return true;
			}
			
			String fileName = file.getName();
			int index = fileName.indexOf(".");
			
			if(index > 1 && index < fileName.length() - 1){
				String extension = fileName.substring(index + 1).toLowerCase();
				if(fileExtension.equals(extension)){
					return true;
				}
			}
			return false;
		}

		@Override
		public String getDescription() {
			if(fileExtension.equals("csv")){
				return "csv�ļ�(*.csv)";
			}
			return "";
		}
	}
}
