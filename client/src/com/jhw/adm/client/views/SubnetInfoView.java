package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SubnetInfoView.ID)
@Scope(Scopes.DESKTOP)
public class SubnetInfoView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "subnetInfoView";
	
	private final JPanel centerPnl = new JPanel();
	
	private final JLabel subnetNameLbl = new JLabel();
	private final JTextField subnetNameFld = new JTextField();
	
	private final JPanel bottomPnl = new JPanel();
	private JButton okBtn;
	private JButton closeBtn;
	
	private ButtonFactory buttonFactory;
	private SubNetTopoNodeEntity subNetTopoNodeEntity = null;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@PostConstruct
	protected void initialize() {
		buttonFactory = actionManager.getButtonFactory(this);
		init();
		initializeValue();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeValue(){
		subNetTopoNodeEntity = (SubNetTopoNodeEntity) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),SubNetTopoNodeEntity.class);
		
		if(subNetTopoNodeEntity == null){
			return;
		}
		subnetNameFld.setText(subNetTopoNodeEntity.getName());
	}
	
	private void init(){
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(540, 440);
		this.setTitle("子网信息");
	}
	
	private void initCenterPnl(){
		JPanel namePanel = new JPanel();
		namePanel.setBorder(BorderFactory.createTitledBorder("子网名称"));
		namePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		subnetNameLbl.setText("子网名称");
		subnetNameFld.setColumns(25);
		
		namePanel.add(subnetNameLbl);
		namePanel.add(subnetNameFld);
		namePanel.add(new StarLabel());
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(namePanel,BorderLayout.NORTH);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okBtn = buttonFactory.createButton(ActionConstants.SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.add(okBtn);
		bottomPnl.add(closeBtn);
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存子网属性设置",role=Constants.MANAGERCODE)
	public void save(){
		if(StringUtils.isBlank(subnetNameFld.getText())){
			JOptionPane.showMessageDialog(this, "子网名称不能为空，请输入子网名称", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		subNetTopoNodeEntity.setName(subnetNameFld.getText());
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
		equipmentModel.fireEquipmentUpdated(subNetTopoNodeEntity);
		messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
}