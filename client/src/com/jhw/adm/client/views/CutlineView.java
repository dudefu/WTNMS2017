package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.CutlineModel;

@Component(CutlineView.ID)
@Scope(Scopes.DESKTOP)
public class CutlineView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "cutlineView";
	private final String STATIC_IMAGE = "常规图例";
	private final String SWITCH_IMAGE = "设备图例";
	private final String CUSTOM_IMAGE = "虚拟网元图例";
	private final int PANEL_WIDTH = 55;
	
	private JPanel cutlinePanel = new JPanel();
	private JPanel staticCutlinePanel = new JPanel();//静态图例面板
//	private JPanel switchCutlinePanel = new JPanel();//设备图例面板
	private JPanel customPanel = new JPanel();//自定义图例面板
	private JScrollPane scrollPane = new JScrollPane();
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=CutlineModel.ID)
	private CutlineModel cutlineModel;
	
	@PostConstruct
	protected void initialize(){
		cutlineModel.setCustomImageInfo();
		createCutlinePanel();
		
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		this.setTitle("图例查看器");
		this.setViewSize(this.PANEL_WIDTH, scrollPane.getPreferredSize().height + 30);
	}

	@SuppressWarnings("static-access")
	private void createCutlinePanel() {
		cutlinePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		staticCutlinePanel = createCutline(cutlineModel.getStaticImageNames(), cutlineModel.getStaticImageDescriptions(), staticCutlinePanel, this.STATIC_IMAGE);
//		switchCutlinePanel = createCutline(cutlineModel.getEquipmentImageNames(),cutlineModel.getEquipmentImageDescriptions(),switchCutlinePanel,this.SWITCH_IMAGE);
//		createCustomPanel();
		
		JPanel tempPanel = new JPanel(new GridBagLayout());
		tempPanel.add(staticCutlinePanel,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
//		tempPanel.add(switchCutlinePanel,new GridBagConstraints(0,1,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		if(null != customPanel){
			tempPanel.add(customPanel,new GridBagConstraints(0,2,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		}
		cutlinePanel.add(tempPanel);
		scrollPane.getViewport().add(cutlinePanel);
	}

	private void createCustomPanel() {
		
		if(cutlineModel.getCUSTOM_IMAGE_NAMES().size() == 0){
			customPanel = null;
		}
		
		customPanel = createCutline(cutlineModel.getCUSTOM_IMAGE_NAMES(), cutlineModel.getCUSTOM_IMAGE_DESCRIPTIONS(), customPanel, CUSTOM_IMAGE);
	}

	/**
	 * 创建图例样式
	 */
	private JPanel createCutline(String[] imageName,String[] imageDescription,JPanel parent,String borderTitle) {
		if(null == imageName || 0 == imageName.length){
			return null;
		}
		parent.setLayout(new GridBagLayout());
		parent.setBorder(BorderFactory.createTitledBorder(borderTitle));
		
		for(int i = 0 ; i < imageName.length ; i++){
			parent.add(createIamgeLabel(imageName[i]), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 25), 0,0));
			parent.add(new JLabel(imageDescription[i]), new GridBagConstraints(1, i, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 25), 0,0));
		}
		return parent;
	}
	
	private JPanel createCutline(List<byte[]> imageName, List<String> imageDescription, JPanel parent, String borderTitle) {
		int size = imageName.size();
		if(size == 0){
			return null;
		}
		parent.setLayout(new GridBagLayout());
		parent.setBorder(BorderFactory.createTitledBorder(borderTitle));

		for(int i = 0 ; i < size ; i++){
			parent.add(createIamgeLabel(imageName.get(i)), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 25), 0,0));
			parent.add(new JLabel(imageDescription.get(i)), new GridBagConstraints(1, i, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 25), 0,0));
		}
		
		return parent;
	}
	
	/**
	 * 创建一个带图样的label
	 * @param iamgeName
	 * @return JLabel
	 */
	private JLabel createIamgeLabel(String iamgeName){
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(getIamgeIcon(iamgeName));
		
		return imageLabel;
	}
	
	private ImageIcon getIamgeIcon(String imageName){
		if(StringUtils.isBlank(imageName)){
			return null;
		}
		
		return imageRegistry.getImageIcon(imageName);
	}
	
	private JLabel createIamgeLabel(byte[] imageName){
		JLabel imageLabel = new JLabel();
		if(null != imageName){
			imageLabel.setIcon(new ImageIcon(imageName));
		}
		
		return imageLabel;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
}
