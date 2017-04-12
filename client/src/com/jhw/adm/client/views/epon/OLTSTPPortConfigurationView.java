package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.epon.OLTSTPPortConfigModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPortStp;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(OLTSTPPortConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class OLTSTPPortConfigurationView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTSTPPortConfigurationView";
	
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JPanel mainPnl = new JPanel();
	//�϶˵����
	private final JPanel topPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel portPriorityLbl = new JLabel();
	private final JLabel enableLbl = new JLabel();
	private final JLabel statusLbl = new JLabel();
	private final JLabel pathPayLbl = new JLabel();
	private final JLabel desRootLbl = new JLabel();
	private final JLabel bridgePathPayLbl = new JLabel();
	private final JLabel desBridgeLbl = new JLabel();
	private final JLabel desPortLbl = new JLabel();
	private final JLabel forwordDelayLbl = new JLabel();
	
	//�м����
	private final JScrollPane centerScrllPnl = new JScrollPane();
	private final JPanel centerPnl = new JPanel();
	private final JPanel middlePnl = new JPanel();
	
	//���߰�ť���
	private final JPanel toolBtnPnl = new JPanel();
	private JButton saveBtn;
	private JButton synBtn;
	private JButton closeBtn = null;
	
	private int portCount = 0;
	
	private OLTEntity oltNodeEntity = null;
	private ButtonFactory buttonFactory;
	
	private final List<List> componentList = new ArrayList<List>();
	
	private static final String[] PRIORITYLIST = {"0","16","32","48","64","80","96","112"
		,"128","144","160","176","192","208","224","240"};
	
	private static final String[] P2PPORTLIST = {"true","false","auto"};
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(OLTSTPPortConfigModel.ID)
	private OLTSTPPortConfigModel viewModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initTopPnl();
		initCenterPnl();
		initToolBtnPnl();
		
		mainPnl.setLayout(new BorderLayout());
		mainPnl.add(topPnl,BorderLayout.NORTH);
		mainPnl.add(centerScrllPnl,BorderLayout.CENTER);
		scrollPnl.getViewport().add(mainPnl);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(toolBtnPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
		panel.add(portPriorityLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		panel.add(enableLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		panel.add(statusLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		panel.add(pathPayLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
		panel.add(desRootLbl,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,80,0,0),0,0));
		panel.add(bridgePathPayLbl,new GridBagConstraints(6,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,40,0,0),0,0));
		panel.add(desBridgeLbl,new GridBagConstraints(7,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
		panel.add(desPortLbl,new GridBagConstraints(8,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
		panel.add(forwordDelayLbl,new GridBagConstraints(9,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
		
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		middlePnl.setLayout(new GridBagLayout());
		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(middlePnl);
		centerScrllPnl.getViewport().add(centerPnl);
	}
	
	private void initToolBtnPnl(){
		toolBtnPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		saveBtn = buttonFactory.createButton(SAVE);
		synBtn = buttonFactory.createButton(UPLOAD);
		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		toolBtnPnl.add(newPanel,BorderLayout.EAST);
	}
	
	private void setResource(){
		portLbl.setText("�˿�");
		portPriorityLbl.setText("�˿����ȼ�");
		enableLbl.setText("ʹ��");
		statusLbl.setText("״̬");
		pathPayLbl.setText("·������");
		desRootLbl.setText("DesignatedRoot");
		bridgePathPayLbl.setText("����·������");
		desBridgeLbl.setText("DesignatedBridge");
		desPortLbl.setText("ָ�ɶ˿�");
		forwordDelayLbl.setText("ת����ʱ");
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	private void queryData(){
		oltNodeEntity = (OLTEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTEntity.class);
		if (null == oltNodeEntity){
			return ;
		}
		
		//�˿�����
		portCount = oltNodeEntity.getPorts().size();
		//��ʼ���������
		setCenterLayout();
		
		String where = " where entity.oltEntity=?";
		Object[] parms = {oltNodeEntity};
		
		List<OLTPortStp> oltPortStpList = (List<OLTPortStp>)remoteServer.getService().findAll(OLTPortStp.class,where,parms);
		if (null == oltPortStpList || oltPortStpList.size() < 1){
//			initDataBase();
			return;
		}
		
		portCount = oltPortStpList.size();
		setValue(oltPortStpList);
	}
	
	/**
	 * ͨ����ѯ���Ķ˿��б���panel�����пؼ�
	 */
	private void setCenterLayout(){
		componentList.clear();
		middlePnl.removeAll();
		
		for(int i = 0 ; i < portCount ; i++){
			JLabel portLabel = new JLabel();//�˿�
			portLabel.setText((i+1)+"");
			
			JComboBox priorityComBox = new JComboBox(); //�˿����ȼ�
			priorityComBox.setPreferredSize(new Dimension(80,priorityComBox.getPreferredSize().height));
			for(int j = 0 ; j < PRIORITYLIST.length ; j++){
				priorityComBox.addItem(PRIORITYLIST[j]);
			}
			
			JLabel enableLabel = new JLabel();
			JLabel statusLabel = new JLabel();
			NumberField pathPayFld = new NumberField();
			JLabel desRootLabel = new JLabel();
			JLabel bridgePathPayLabel = new JLabel();
			JLabel desBridgeLabel = new JLabel();
			JLabel desPortLable = new JLabel();
			JLabel forwordDelayLabel = new JLabel();


			middlePnl.add(portLabel,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
			middlePnl.add(priorityComBox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,26,0,0),0,0));
			middlePnl.add(enableLabel,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(statusLabel,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
			middlePnl.add(pathPayFld,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,25,0,0),0,0));
			middlePnl.add(desRootLabel,new GridBagConstraints(5,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
			middlePnl.add(bridgePathPayLabel,new GridBagConstraints(6,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,0,0),0,0));
			middlePnl.add(desBridgeLabel,new GridBagConstraints(7,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
			middlePnl.add(desPortLable,new GridBagConstraints(8,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
			middlePnl.add(forwordDelayLabel,new GridBagConstraints(9,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,40,0,0),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portLabel); //�˿�
			rowList.add(1,priorityComBox);//
			rowList.add(2,enableLabel);//
			rowList.add(3,statusLabel);//
			rowList.add(4,pathPayFld);//
			rowList.add(5,desRootLabel);//
			rowList.add(6,bridgePathPayLabel);//
			rowList.add(7,desBridgeLabel);//
			rowList.add(8,desPortLable);//
			rowList.add(9,forwordDelayLabel);//
			
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				middlePnl.revalidate();
			}
		});
	}
	
	private void clear(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				componentList.clear();
				middlePnl.removeAll();
				oltNodeEntity = null;
				middlePnl.revalidate();
			}
		});
	}
	
	private void initDataBase(){
		List<Serializable> dataList = new ArrayList<Serializable>();
		for(int row = 0 ; row < 16; row++){
			OLTPortStp entity = new OLTPortStp();
			int port = row + 1 ;        //�˿�

			entity.setPortNo(port);
			entity.setPriority(128);
			entity.setShiNeng(1);
			entity.setPortStatus(2);
			entity.setRootExpenses(19);
			entity.setDesignatedRoot("80:00:00:e0:0f:ae:fa:c0");
			entity.setN_bridgeRoot("0");
			entity.setDesignatedBridge("80:00:00:e0:0f:ae:fa:c0");
			entity.setAppointPort(32770);
			entity.setForward(0);
//			entity.setEponEntity(eponNodeEntity);
			dataList.add(entity);
		}
		
		remoteServer.getService().saveEntities(dataList);
	}
	
	
	/**
	 * ���ø����ؼ���ֵ
	 */
	private void setValue(List<OLTPortStp> oltPortStpList){
		viewModel.setOltStpPortList(oltPortStpList);
		for(int row = 0 ; row < oltPortStpList.size(); row++){
			int port = row + 1 ;        //�˿�
			OLTPortStp oltPortStp = viewModel.getValueAt(port);
			int priority = oltPortStp.getPriority();
			int shiNeng = oltPortStp.getShiNeng();
			int portStatus = oltPortStp.getPortStatus();
			int rootExpenses = oltPortStp.getRootExpenses();
			String designatedRoot = oltPortStp.getDesignatedRoot();
			String n_bridgeRoot = oltPortStp.getN_bridgeRoot();
			String designatedBridge = oltPortStp.getDesignatedBridge();
			int appointPort = oltPortStp.getAppointPort();//ָ�ɶ˿�
		    int forward = oltPortStp.getForward();

			List rowList = componentList.get(row);
			((JLabel)rowList.get(0)).setText(port+""); //�˿�
			((JComboBox)rowList.get(1)).setSelectedItem(priority + ""); //���ȼ�
			((JLabel)rowList.get(2)).setText(""+shiNeng); //ʹ��
			((JLabel)rowList.get(3)).setText(portStatus+"");//״̬
			((NumberField)rowList.get(4)).setText(rootExpenses + "");//·������
			((JLabel)rowList.get(5)).setText(designatedRoot + "");//
			((JLabel)rowList.get(6)).setText(n_bridgeRoot);//
			((JLabel)rowList.get(7)).setText(designatedBridge);//
			((JLabel)rowList.get(8)).setText(appointPort + "");//
			((JLabel)rowList.get(9)).setText(forward + "");//
		}
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="����OLT STP�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		List<Serializable> dataList = getSTPPortConfigList();
		remoteServer.getService().updateEntity(dataList);

	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="����ͬ��OLT STP�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void upSynchronize(){
	
	}
	
	private List<Serializable> getSTPPortConfigList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < portCount; i++){
			int portNo = i+1;    //�˿�
			OLTPortStp stpPortConfig = viewModel.getValueAt(portNo);
			
			int priority = stpPortConfig.getPriority();//���ȼ�
			int oldPathPay = stpPortConfig.getRootExpenses();//·������

			for (int k = 0 ; k < portCount; k++){
				JLabel portLbl = (JLabel)componentList.get(k).get(0);
				int port = NumberUtils.toInt(portLbl.getText());
				
				JComboBox priorityComBox = (JComboBox)componentList.get(k).get(1);
				int newPreLevel = NumberUtils.toInt(priorityComBox.getSelectedItem().toString());
				
				NumberField payFld = (NumberField)componentList.get(k).get(4);
				int newPay = NumberUtils.toInt(payFld.getText().trim());

				if (portNo == port){
					if ((priority != newPreLevel)
						|| oldPathPay != newPay){
						//˵���˶˿ڵ�ֵ�иı�
						stpPortConfig.setPriority(newPreLevel);
						stpPortConfig.setRootExpenses(newPay);
//						stpPortConfig.setEponEntity(eponNodeEntity);

						list.add(stpPortConfig);
						break;
					}
				}
			}
		}
		
		return list;
	}
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	/**
	 * �豸�������ʱ�����
	 * ��ѡ��ͬ���豸ʱ������switchNodeEntity,���²�ѯ���ݿ�
	 * @author Administrator
	 *
	 */
	private final PropertyChangeListener eponNodeChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof EponTopoEntity){
				queryData();
			}
		}
	};
	
	public boolean isValids(){
		boolean isValid = true;
		
		if (null == this.oltNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ��EPON�豸","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		for(int i = 0 ; i < componentList.size(); i++){
			NumberField pathPayFld = (NumberField)componentList.get(i).get(4);
			if(null == pathPayFld.getText().trim() || "".equals(pathPayFld.getText().trim())){
				JOptionPane.showMessageDialog(this, "·������ֵ����","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		return isValid;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
}
