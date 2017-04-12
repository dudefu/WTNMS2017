package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.jms.JMSException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTMulticast;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(OLTMulticastView.ID)
@Scope(Scopes.DESKTOP)
public class OLTMulticastView extends ViewPart {
	private static final long serialVersionUID = 3577733145522011109L;

	public static final String ID = "OLTMulticastView";

	private final JPanel centerPnl = new JPanel();

	private final JLabel dropLbl = new JLabel();
	private final JTextField dropFld = new JTextField();

	private final JLabel v1Lbl = new JLabel();
	private final JTextField v1Fld = new JTextField();

	private final JLabel v2Lbl = new JLabel();
	private final JTextField v2Fld = new JTextField();

	private final JLabel v3Lbl = new JLabel();
	private final JTextField v3Fld = new JTextField();

	private final JLabel joinLbl = new JLabel();
	private final JTextField joinFld = new JTextField();

	private final JLabel leavesLbl = new JLabel();
	private final JTextField leavesFld = new JTextField();

	private final JLabel generalLbl = new JLabel();
	private final JTextField generalFld = new JTextField();

	private final JLabel specialLbl = new JLabel();
	private final NumberField specialFld = new NumberField();

	private final JLabel routeAgeLbl = new JLabel();
	private final JTextField routeAgeFld = new JTextField();

	private final JLabel mcstStatusLbl = new JLabel();
	private final JComboBox mcstStatusBox = new JComboBox();

	private final JLabel mcstModeLbl = new JLabel();
	private final JComboBox mcstModeBox = new JComboBox();

	private final JLabel proxyLbl = new JLabel();
	private final JComboBox proxyBox = new JComboBox();

	private final JLabel ipAddrLbl = new JLabel();
	private final IpAddressField ipAddrFld = new IpAddressField();

	private final JLabel maxTimeLbl = new JLabel();
	private final NumberField maxTimeFld = new NumberField();

	private final JLabel intervalLbl = new JLabel();
	private final NumberField intervalFld = new NumberField();

	private final JLabel numberLbl = new JLabel();
	private final NumberField numberFld = new NumberField();

	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn;
	private JButton closeBtn;

	private ButtonFactory buttonFactory;

	private OLTEntity selectedOlt = null;

	private OLTMulticast oltMulticast;

	private static final String[] STATUS = { "disable", "enable" };
	private static final String[] MCSTMODE = { "igmp-snooping enable",
			"igmp querier" };

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;
	
	private MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private static final Logger LOG = LoggerFactory.getLogger(OLTMulticastView.class);

	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}

	private void init() {
		buttonFactory = actionManager.getButtonFactory(this);
		initCenterPnl();
		initBottomPnl();

		this.setLayout(new BorderLayout());
		this.add(centerPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);

		setResource();
	}

	private void initCenterPnl() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(dropLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 0), 0, 0));
		panel.add(dropFld, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 20, 0, 0), 0, 0));
		panel.add(routeAgeLbl, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 30, 0, 0), 0, 0));
		panel.add(routeAgeFld, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 20, 0, 0), 0, 0));

		panel.add(v1Lbl, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(v1Fld, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(mcstStatusLbl, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(mcstStatusBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		panel.add(v2Lbl, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(v2Fld, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(mcstModeLbl, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(mcstModeBox, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		panel.add(v3Lbl, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(v3Fld, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(proxyLbl, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(proxyBox, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		panel.add(joinLbl, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(joinFld, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(ipAddrLbl, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(ipAddrFld, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		panel.add(leavesLbl, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(leavesFld, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(maxTimeLbl, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(maxTimeFld, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		panel.add(generalLbl, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(generalFld, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(intervalLbl, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(intervalFld, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		panel.add(specialLbl, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 5, 0, 0), 0, 0));
		panel.add(specialFld, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));
		panel.add(numberLbl, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 30, 0, 0), 0, 0));
		panel.add(numberFld, new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(15, 20, 0, 0), 0, 0));

		centerPnl.setBorder(BorderFactory.createTitledBorder("组播配置"));
		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(panel);
	}

	private void initBottomPnl() {
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);

		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
	}

	private void setResource() {
		this.setTitle("OLT组播配置");

		dropLbl.setText("DLF Drop");
		v1Lbl.setText("V1 IGMP包个数");
		v2Lbl.setText("V2 IGMP包个数");
		v3Lbl.setText("V3 IGMP包个数");
		joinLbl.setText("Join IGMP包个数");
		leavesLbl.setText("Leaves IGMP包个数");
		generalLbl.setText("通用查询IGMP包个数");
		specialLbl.setText("特殊查询IGMP包个数");
		routeAgeLbl.setText("Route Age");
		mcstStatusLbl.setText("MCST状态");
		mcstModeLbl.setText("MCST模式");
		proxyLbl.setText("IGMP代理状态");
		ipAddrLbl.setText("IGMP查询者IP地址");
	 	maxTimeLbl.setText("最大查询响应时间(秒)");
		intervalLbl.setText("最后成员查询间隔(秒)");
		numberLbl.setText("最后成员查询数量");

		for (int i = 0; i < STATUS.length; i++) {
			mcstStatusBox.addItem(STATUS[i]);
		}
		for (int i = 0; i < MCSTMODE.length; i++) {
			mcstModeBox.addItem(MCSTMODE[i]);
		}
		for (int i = 0; i < STATUS.length; i++) {
			proxyBox.addItem(STATUS[i]);
		}

		dropFld.setColumns(20);
		routeAgeFld.setColumns(20);

		dropFld.setEditable(false);
		v1Fld.setEditable(false);
		v2Fld.setEditable(false);
		v3Fld.setEditable(false);
		joinFld.setEditable(false);
		leavesFld.setEditable(false);
		generalFld.setEditable(false);
		specialFld.setEditable(false);

		dropFld.setBackground(Color.WHITE);
		v1Fld.setBackground(Color.WHITE);
		v2Fld.setBackground(Color.WHITE);
		v3Fld.setBackground(Color.WHITE);
		joinFld.setBackground(Color.WHITE);
		leavesFld.setBackground(Color.WHITE);
		generalFld.setBackground(Color.WHITE);
		specialFld.setBackground(Color.WHITE);

		this.setViewSize(600, 480);

		// 增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		selectedOlt = (OLTEntity) adapterManager.getAdapter(equipmentModel
				.getLastSelected(), OLTEntity.class);

//		if (null == selectedOlt) {
//			return;
//		}

		String where = " where entity.oltEntity=?";
		Object[] parms = { selectedOlt };

		List<OLTMulticast> oltMulticastList = (List<OLTMulticast>) remoteServer
				.getService().findAll(OLTMulticast.class, where, parms);
		if (null == oltMulticastList || oltMulticastList.size() < 1) {
			return;
		}
		oltMulticast = oltMulticastList.get(0);
		setValue();

	}

	private void setValue() {
		String drop = reverseValue(oltMulticast.isDLF_Drop());
		String igmpV1 = String.valueOf(oltMulticast.getV1_IGMPNum());
		String igmpV2 = String.valueOf(oltMulticast.getV2_IGMPNum());
		String igmpV3 = String.valueOf(oltMulticast.getV3_IGMPNum());
		String join = String.valueOf(oltMulticast.getJoin_IGMPNum());
		String leaves = String.valueOf(oltMulticast.getLeaves_IGMPNum());
		String common = String.valueOf(oltMulticast.getComm_IGMPNum());
		String specia = String.valueOf(oltMulticast.getSpecial_IGMPNum());

		String routeAge = String.valueOf(oltMulticast.getRouteAge());
		String mcstStatus = reverseValue(oltMulticast.getMcstStatus());
		String mcstMode = reverseModeValue(oltMulticast.getMcstModle());
		String proxy = reverseValue(oltMulticast.getIgmpProxyStatus());
		String ipvalue = oltMulticast.getIgmp_ByIp();
		String maxTime = String.valueOf(oltMulticast.getMaxRespondTime());
		String intervalsTime = String.valueOf(oltMulticast.getIntervalsTime());
		String queryCount = String.valueOf(oltMulticast.getQueryCount());

		dropFld.setText(drop);
		v1Fld.setText(igmpV1);
		v2Fld.setText(igmpV2);
		v3Fld.setText(igmpV3);
		joinFld.setText(join);
		leavesFld.setText(leaves);
		generalFld.setText(common);
		specialFld.setText(specia);
		routeAgeFld.setText(routeAge);
		mcstStatusBox.setSelectedItem(mcstStatus);
		mcstModeBox.setSelectedItem(mcstMode);
		proxyBox.setSelectedItem(proxy);
		ipAddrFld.setText(ipvalue);
		maxTimeFld.setText(maxTime);
		intervalFld.setText(intervalsTime);
		numberFld.setText(queryCount);
	}

	/**
	 * 保存按钮事件
	 */
	@ViewAction(name = SAVE, icon = ButtonConstants.SAVE, desc = "保存OLT组播", role = Constants.MANAGERCODE)
	public void save() {
		if (!isValids()) {
			return;
		}
		boolean drop = reverseValue(dropFld.getText());
		int igmpV1 = NumberUtils.toInt(v1Fld.getText());
		int igmpV2 = NumberUtils.toInt(v2Fld.getText());
		int igmpV3 = NumberUtils.toInt(v3Fld.getText());
		int join = NumberUtils.toInt(joinFld.getText());
		int leaves = NumberUtils.toInt(leavesFld.getText());
		int common = NumberUtils.toInt(generalFld.getText());
		int specia = NumberUtils.toInt(specialFld.getText());

		int routeAge = NumberUtils.toInt(routeAgeFld.getText());
		int mcstStatus = reverseStatusValue(mcstStatusBox.getSelectedItem()
				.toString());
		int mcstMode = reverseModeValue(mcstModeBox.getSelectedItem()
				.toString());
		int proxy = reverseStatusValue(proxyBox.getSelectedItem().toString());
		String ipvalue = ipAddrFld.getText();
		int maxTime = NumberUtils.toInt(maxTimeFld.getText());
		int intervalsTime = NumberUtils.toInt(intervalFld.getText());
		int queryCount = NumberUtils.toInt(numberFld.getText());
		
		int sync = PromptDialog.showPromptDialog(this, "请选择保存的方式",
				imageRegistry);
		if (sync == 0) {
			return;
		}
		
		String address = selectedOlt.getIpValue();
		String from = clientModel.getCurrentUser().getUserName();
		String localAddress = clientModel.getLocalAddress();
		int messageNo = MessageNoConstants.OLTVLANADD;
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(oltMulticast);
		
		if (null == oltMulticast) {
			oltMulticast = new OLTMulticast();
			oltMulticast.setDLF_Drop(drop);
			oltMulticast.setV1_IGMPNum(igmpV1);
			oltMulticast.setV2_IGMPNum(igmpV2);
			oltMulticast.setV3_IGMPNum(igmpV3);
			oltMulticast.setJoin_IGMPNum(join);
			oltMulticast.setLeaves_IGMPNum(leaves);
			oltMulticast.setComm_IGMPNum(common);
			oltMulticast.setSpecial_IGMPNum(specia);
			oltMulticast.setRouteAge(routeAge);
			oltMulticast.setMcstStatus(mcstStatus);
			oltMulticast.setMcstModle(mcstMode);
			oltMulticast.setIgmpProxyStatus(proxy);
			oltMulticast.setIgmp_ByIp(ipvalue);
			oltMulticast.setMaxRespondTime(maxTime);
			oltMulticast.setIntervalsTime(intervalsTime);
			oltMulticast.setQueryCount(queryCount);
			oltMulticast.setOltEntity(selectedOlt);

			messageOfSaveProcessorStrategy.showInitializeDialog("保存组播", this);
			try {
				remoteServer.getAdmService().saveAndSettingByIP(address,
						messageNo, list, from, localAddress, Constants.DEV_OLT, sync);
			} catch (JMSException e) {
				LOG.error("保存组播失败", e);
				messageOfSaveProcessorStrategy.showErrorMessage("保存组播失败");
			}
		} else {
			oltMulticast.setDLF_Drop(drop);
			oltMulticast.setV1_IGMPNum(igmpV1);
			oltMulticast.setV2_IGMPNum(igmpV2);
			oltMulticast.setV3_IGMPNum(igmpV3);
			oltMulticast.setJoin_IGMPNum(join);
			oltMulticast.setLeaves_IGMPNum(leaves);
			oltMulticast.setComm_IGMPNum(common);
			oltMulticast.setSpecial_IGMPNum(specia);
			oltMulticast.setRouteAge(routeAge);
			oltMulticast.setMcstStatus(mcstStatus);
			oltMulticast.setMcstModle(mcstMode);
			oltMulticast.setIgmpProxyStatus(proxy);
			oltMulticast.setIgmp_ByIp(ipvalue);
			oltMulticast.setMaxRespondTime(maxTime);
			oltMulticast.setIntervalsTime(intervalsTime);
			oltMulticast.setQueryCount(queryCount);
			oltMulticast.setOltEntity(selectedOlt);

			messageOfSaveProcessorStrategy.showInitializeDialog("保存组播", this);
			try {
				remoteServer.getAdmService().updateAndSettingByIp(address,
						messageNo, list, from, localAddress, Constants.DEV_OLT, sync);
			} catch (JMSException e) {
				LOG.error("保存组播失败", e);
				messageOfSaveProcessorStrategy.showErrorMessage("保存组播失败");
			}
		}
		
		if (sync == Constants.SYN_SERVER) {
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}

	private void clear() {
		dropFld.setText(StringUtils.EMPTY);
		v1Fld.setText(StringUtils.EMPTY);
		v2Fld.setText(StringUtils.EMPTY);
		v3Fld.setText(StringUtils.EMPTY);
		joinFld.setText(StringUtils.EMPTY);
		leavesFld.setText(StringUtils.EMPTY);
		generalFld.setText(StringUtils.EMPTY);
		specialFld.setText(StringUtils.EMPTY);
		routeAgeFld.setText(StringUtils.EMPTY);
		mcstStatusBox.setSelectedItem(StringUtils.EMPTY);
		mcstModeBox.setSelectedItem(StringUtils.EMPTY);
		proxyBox.setSelectedItem(StringUtils.EMPTY);
		ipAddrFld.setText(StringUtils.EMPTY);
		maxTimeFld.setText(StringUtils.EMPTY);
		intervalFld.setText(StringUtils.EMPTY);
		numberFld.setText(StringUtils.EMPTY);
	}

	/**
	 * 设备浏览器的时间监听 当选择不同的设备时，根据switchNodeEntity,重新查询数据库
	 */
	private final PropertyChangeListener eponNodeChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof EponTopoEntity) {
				queryData();
			} else {
				clear();
				selectedOlt = null;
			}
		}
	};

	private boolean reverseValue(String str) {
		boolean bool = false;
		if (str.equals("on")) {
			bool = true;
		} else {
			bool = false;
		}
		return bool;
	}

	private String reverseValue(boolean bool) {
		String str = StringUtils.EMPTY;
		if (bool) {
			str = "on";
		} else {
			str = "off";
		}
		return str;
	}

	private String reverseValue(int value) {
		String str = StringUtils.EMPTY;
		if (value == 0) {
			str = "enable";
		} else {
			str = "disable";
		}
		return str;
	}

	private int reverseStatusValue(String str) {
		int value = 0;
		if (str.equals("enable")) {
			value = 0;
		} else {
			value = 1;
		}
		return value;
	}

	private String reverseModeValue(int value) {
		String str = StringUtils.EMPTY;
		if (value == 0) {
			str = "igmp-snooping enable";
		} else {
			str = "igmp querier";
		}
		return str;
	}

	private int reverseModeValue(String str) {
		int value = 0;
		if (str.equals("igmp-snooping enable")) {
			value = 0;
		} else {
			value = 1;
		}
		return value;
	}

	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	public boolean isValids() {
		boolean isValid = true;

		if (null == this.selectedOlt) {
			JOptionPane.showMessageDialog(this, "请选择EPON设备", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		if (null == routeAgeFld.getText().trim()
				|| "".equals(routeAgeFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "Route Age错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		if (null == ipAddrFld.getText().trim()
				|| "".equals(ipAddrFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "IGMP查询者IP地址错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(ClientUtils.isIllegal(ipAddrFld.getText())){
			JOptionPane.showMessageDialog(this, "IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if (null == maxTimeFld.getText().trim()
				|| "".equals(maxTimeFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "最大查询响应时间错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == intervalFld.getText().trim()
				|| "".equals(intervalFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "最大成员查询间隔错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == numberFld.getText().trim()
				|| "".equals(numberFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "最后成员查询数量错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",
				imageRegistry);
		if (result == 0) {
			isValid = false;
			return isValid;
		}
		return isValid;
	}
}