package com.jhw.adm.server.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.SpeedEntity;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TestBean1;
import com.jhw.adm.server.entity.util.TestBean2;
import com.jhw.adm.server.entity.util.TestBean3;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;
import com.jhw.adm.server.entity.warning.WarningType;

public class Tools {
	private static String time = null;
	public static SwitchNodeEntity buildSwitch1() {
		SwitchNodeEntity node6 = new SwitchNodeEntity();
		SwitchBaseInfo info6 = new SwitchBaseInfo();
		SwitchBaseConfig config6 = new SwitchBaseConfig();
		AddressEntity area6 = new AddressEntity();
		info6.setAddress("广州市南山区科技园威新软件园");
		info6.setBootromVersion("v1.0");
		info6.setCompany("深圳金宏威实业发展有限责任公司");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		time = sdf.format(date);
		info6.setCurrentTime(time);
		info6.setDeviceName("工业交换机23");
		info6.setMacValue("ab23421s142");
		config6.setIpValue("192.168.13.146");
		config6.setDhcpAyylied(true);
		config6.setNetGate("192.168.12.254");
		config6.setFirstDNS("202.96.134.198");
		area6.setAddress("深圳市");
		area6.setLatitude("1235.12598");
		area6.setLongitude("231.0127");
		node6.setBaseInfo(info6);
		node6.setBaseConfig(config6);
		node6.setAddress(area6);
		return node6;
	}

	public static HashMap synchSwitch(String ipvalue) {
		HashMap map = new HashMap();
		SwitchNodeEntity entity = buildSwitch1();
		List switchs = new ArrayList();
		switchs.add(entity);
		List vcs = new ArrayList();
		VlanConfig vc = buildVlan();
		vcs.add(vc);

		List qosscs = new ArrayList();
		QOSSpeedConfig qossc = new QOSSpeedConfig();
		SpeedEntity se1 = new SpeedEntity();
		SpeedEntity se2 = new SpeedEntity();
		se1.setApplied(true);
		se1.setDescs("se1");
		se1.setFlowType(0);
		se1.setUnit("bit");

		se2.setApplied(true);
		se2.setDescs("se2");
		se2.setFlowType(0);
		se2.setUnit("bit");

		return map;

	}

	private List<SwitchNodeEntity> buildSwitchs() {
		List<SwitchNodeEntity> datas = new ArrayList<SwitchNodeEntity>();
		SwitchNodeEntity node1 = new SwitchNodeEntity();
		SwitchBaseInfo info1 = new SwitchBaseInfo();
		SwitchBaseConfig config1 = new SwitchBaseConfig();
		AddressEntity area1 = new AddressEntity();

		info1.setAddress("广州市某某地方小区");
		info1.setBootromVersion("v1.0");
		info1.setCompany("深圳金宏威实业发展有限责任公司");
		info1.setCurrentTime(time);
		info1.setDeviceName("工业交换机1");
		info1.setMacValue("ab3d255ce22");
		info1.setSwitchNode(node1);
		config1.setIpValue("192.168.1.2");
		config1.setDhcpAyylied(true);
		config1.setNetGate("192.168.1.1");
		config1.setFirstDNS("202.96.134.133");
		config1.setSwitchNode(node1);
		area1.setAddress("广州市");
		area1.setLatitude("1235.1254798");
		area1.setLongitude("231.0124587");

		node1.setBaseInfo(info1);
		node1.setBaseConfig(config1);
		node1.setAddress(area1);
		datas.add(node1);

		SwitchNodeEntity node2 = new SwitchNodeEntity();
		SwitchBaseInfo info2 = new SwitchBaseInfo();
		SwitchBaseConfig config2 = new SwitchBaseConfig();
		AddressEntity area2 = new AddressEntity();
		info2.setAddress("广州市某某地方小区2");
		info2.setBootromVersion("v1.0");
		info2.setCompany("深圳金宏威实业发展有限责任公司");
		info2.setCurrentTime(time);
		info2.setDeviceName("工业交换机2");
		info2.setMacValue("ab3d22fc112");
		info2.setSwitchNode(node2);
		config2.setIpValue("192.168.1.2");
		config2.setDhcpAyylied(true);
		config2.setNetGate("192.168.1.1");
		config2.setFirstDNS("202.96.134.133");
		config2.setSwitchNode(node2);
		area2.setAddress("广州市");
		area2.setLatitude("1235.1254798");
		area2.setLongitude("231.0124587");
		node2.setBaseInfo(info2);
		node2.setBaseConfig(config2);
		node2.setAddress(area2);
		datas.add(node2);

		SwitchNodeEntity node3 = new SwitchNodeEntity();
		SwitchBaseInfo info3 = new SwitchBaseInfo();
		SwitchBaseConfig config3 = new SwitchBaseConfig();
		AddressEntity area3 = new AddressEntity();

		info3.setAddress("广州市南山区西丽");
		info3.setBootromVersion("v1.0");
		info3.setCompany("深圳金宏威实业发展有限责任公司");
		info3.setCurrentTime(time);
		info3.setDeviceName("工业交换机3");
		info3.setMacValue("ab3d2223142");
		info3.setSwitchNode(node3);
		config3.setIpValue("192.168.2.2");
		config3.setDhcpAyylied(true);
		config3.setNetGate("192.168.2.1");
		config3.setFirstDNS("202.96.134.199");
		config3.setSwitchNode(node3);
		area3.setAddress("深圳市");
		area3.setLatitude("1235.1254798");
		area3.setLongitude("231.0124587");
		node3.setBaseInfo(info3);
		node3.setBaseConfig(config3);
		node3.setAddress(area3);
		datas.add(node3);

		SwitchNodeEntity node4 = new SwitchNodeEntity();
		SwitchBaseInfo info4 = new SwitchBaseInfo();
		SwitchBaseConfig config4 = new SwitchBaseConfig();
		AddressEntity area4 = new AddressEntity();

		info4.setAddress("广州市南山区科技园");
		info4.setBootromVersion("v1.0");
		info4.setCompany("深圳金宏威实业发展有限责任公司");
		info4.setCurrentTime(time);
		info4.setDeviceName("工业交换机4");
		info4.setMacValue("ab3dhds142");
		info4.setSwitchNode(node4);
		config4.setIpValue("192.168.2.3");
		config4.setDhcpAyylied(true);
		config4.setNetGate("192.168.2.1");
		config4.setFirstDNS("202.96.134.133");
		config4.setSwitchNode(node4);
		area4.setAddress("深圳市");
		area4.setLatitude("1235.1254798");
		area4.setLongitude("231.0124587");
		node4.setBaseInfo(info4);
		node4.setBaseConfig(config4);
		node4.setAddress(area4);
		datas.add(node4);

		SwitchNodeEntity node5 = new SwitchNodeEntity();
		SwitchBaseInfo info5 = new SwitchBaseInfo();
		SwitchBaseConfig config5 = new SwitchBaseConfig();
		AddressEntity area5 = new AddressEntity();

		info5.setAddress("广州市南山区科技园");
		info5.setBootromVersion("v1.0");
		info5.setCompany("深圳金宏威实业发展有限责任公司");
		info5.setCurrentTime(time);
		info5.setDeviceName("工业交换机5");
		info5.setMacValue("ab3dh21s142");
		info5.setSwitchNode(node5);
		config5.setIpValue("192.168.3.2");
		config5.setDhcpAyylied(true);
		config5.setNetGate("192.168.3.1");
		config5.setFirstDNS("202.96.134.133");
		config5.setSwitchNode(node5);
		area5.setAddress("深圳市");
		area5.setLatitude("1235.1254798");
		area5.setLongitude("231.0124587");
		node5.setBaseInfo(info5);
		node5.setBaseConfig(config5);
		node5.setAddress(area5);
		datas.add(node5);

		SwitchNodeEntity node6 = new SwitchNodeEntity();
		SwitchBaseInfo info6 = new SwitchBaseInfo();
		SwitchBaseConfig config6 = new SwitchBaseConfig();
		AddressEntity area6 = new AddressEntity();

		info6.setAddress("广州市南山区科技园");
		info6.setBootromVersion("v1.0");
		info6.setCompany("深圳金宏威实业发展有限责任公司");
		info6.setCurrentTime(time);
		info6.setDeviceName("工业交换机6");
		info6.setMacValue("ab23421s142");
		info6.setSwitchNode(node6);
		config6.setIpValue("192.168.3.3");
		config6.setDhcpAyylied(true);
		config6.setNetGate("192.168.3.1");
		config6.setFirstDNS("202.96.134.133");
		config6.setSwitchNode(node6);
		area6.setAddress("深圳市");
		area6.setLatitude("1235.12598");
		area6.setLongitude("231.0127");
		node6.setBaseInfo(info6);
		node6.setBaseConfig(config6);
		node6.setAddress(area6);
		datas.add(node6);

		return datas;
	}

	public static VlanConfig buildVlan() {
		VlanPortConfig vpc1 = new VlanPortConfig();
		VlanPortConfig vpc2 = new VlanPortConfig();
		VlanPortConfig vpc3 = new VlanPortConfig();
		VlanPortConfig vpc4 = new VlanPortConfig();
		VlanEntity ve1 = new VlanEntity();
		VlanEntity ve2 = new VlanEntity();
		VlanPort vp1 = new VlanPort();
		VlanPort vp2 = new VlanPort();
		VlanConfig vc = new VlanConfig();

		vpc1.setPortNo(1);
		vpc1.setPortTag('U');
		vpc1.setDescs("1");
		vpc2.setPortNo(2);
		vpc2.setPortTag('U');
		vpc2.setDescs("2");
		vpc3.setPortNo(3);
		vpc3.setPortTag('T');
		vpc3.setDescs("3");
		vpc4.setPortNo(4);
		vpc4.setPortTag('T');
		vpc4.setDescs("4");

		List vpcs1 = new ArrayList();
		vpcs1.add(vpc1);
		vpcs1.add(vpc2);

		List vpcs2 = new ArrayList();
		vpcs1.add(vpc3);
		vpcs1.add(vpc4);

//		ve1.setPortConfig(vpcs1);
//		ve1.setVlanID("1");
//		ve1.setDescs("ve1");
//		ve1.setVlanConfig(vc);
//		ve2.setPortConfig(vpcs2);
//		ve2.setVlanID("2");
//		ve2.setDescs("ve2");
//		ve2.setVlanConfig(vc);

		Set ves = new HashSet();
		ves.add(ve1);
		ves.add(ve2);

		vp1.setPortNO(1);
		vp1.setPriority(0);
		vp1.setPvid(1);
		vp1.setDescs("vp1");
		vp1.setVlanConfig(vc);

		vp2.setPortNO(2);
		vp2.setPriority(1);
		vp2.setPvid(2);
		vp2.setDescs("vp2");
		vp2.setVlanConfig(vc);

		Set vps = new HashSet();
		vps.add(vp1);
		vps.add(vp2);

		// vc.setSwitchNode(entity);
		vc.setVlanEntitys(ves);
		vc.setVlanPorts(vps);
		vc.setDescs("test vlan");

		return vc;

	}

	public static TestBean1 buildTests() {
		TestBean1 tb1 = new TestBean1();
		TestBean2 tb2 = new TestBean2();
		TestBean2 tb3 = new TestBean2();
		TestBean2 tb4 = new TestBean2();

		TestBean3 tb5 = new TestBean3();
		TestBean3 tb6 = new TestBean3();

		tb2.setBean1(tb1);
		tb3.setBean1(tb1);
		tb4.setBean1(tb1);
		Set datas = new HashSet();
		datas.add(tb2);
		datas.add(tb3);
		datas.add(tb4);
		tb1.setBeans2(datas);
		Set datas3 = new HashSet();
		tb5.setBean1(tb1);
		tb6.setBean1(tb1);
		datas3.add(tb5);
		datas3.add(tb6);
		tb1.setBeans3(datas3);
		return tb1;
	}

	public static TopDiagramEntity buildDiagram() {
		TopDiagramEntity diagram = new TopDiagramEntity();
		Set snes = new HashSet();
		SwitchTopoNodeEntity sne1 = new SwitchTopoNodeEntity();
		SwitchTopoNodeEntity sne2 = new SwitchTopoNodeEntity();
		SwitchTopoNodeEntity sne3 = new SwitchTopoNodeEntity();
		snes.add(sne1);
		snes.add(sne2);
		snes.add(sne3);
		diagram.setNodes(snes);
		Set lines = new HashSet();
		LinkEntity line1 = new LinkEntity();
		LinkEntity line2 = new LinkEntity();
		LinkEntity line3 = new LinkEntity();
		lines.add(line1);
		lines.add(line2);
		lines.add(line3);
		diagram.setLines(lines);
		return diagram;
	}

	public static WarningType buildWarningType() {
		WarningType wt = new WarningType();
		wt.setWarningType(Constants.COLDSTART);
		wt.setWarningLevel(Constants.URGENCY);
		wt.setWarningStyle("xyz");
		return wt;
	}

	public static TrapWarningEntity buildWarning() {

		TrapWarningEntity entity = new TrapWarningEntity();
		entity.setIpValue("192.168.12.156");
		entity.setWarningEvent(Constants.COLDSTART);
//		WarningNewStatus newstatus = new WarningNewStatus();
//		newstatus.setBeginTime(new Date());
//		entity.setNewStatus(newstatus);
		entity.setDescs("告警测试");
		entity.setCurrentStatus(Constants.COLDSTART);
		entity.setWarningCode("test code");
		return entity;
	}
}
