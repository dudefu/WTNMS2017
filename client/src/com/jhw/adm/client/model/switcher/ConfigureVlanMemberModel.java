package com.jhw.adm.client.model.switcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPortConfig;

@Component(ConfigureVlanMemberModel.ID)
public class ConfigureVlanMemberModel implements Serializable{
	public static final String ID = "configureVlanMemberModel";
	
	private VlanConfig vlanConfig = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureVlanMemberModel.class);
	
	private static final char TAG = 'T';
	private static final char UNTAG = 'U';
	
	
	@Autowired
	@Qualifier(VlanMemberTableModel.ID)
	private VlanMemberTableModel vlanTableModel;
	
	
	@Autowired
	@Qualifier(VlanMemberPortModel.ID)
	private VlanMemberPortModel vlanPortModel;
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public VlanMemberTableModel getVlanTableModel(){
		return vlanTableModel;
	}
	
	public VlanMemberPortModel getVlanPortModel(){
		return vlanPortModel;
	}
	
	public void setPortColumnName(String[] columnName){
		vlanPortModel.setColumnName(columnName);
	}
	
	public void setVlanColumnName(String[] columnName){
		vlanTableModel.setColumnName(columnName);
	}
	
	public void setVlanDataList(VlanConfig data){
		if (null == data){
			vlanTableModel.setDataList(null);
			vlanTableModel.fireTableDataChanged();
			return;
		}
		//所有行的数据
		List dataSum = new ArrayList(); 
		
		this.vlanConfig = data;
		try{
			Set<VlanEntity> vlanEntityList = vlanConfig.getVlanEntitys();
			Iterator<VlanEntity> vlanEntityIterator = vlanEntityList.iterator();
			while(vlanEntityIterator.hasNext()){
				VlanEntity vlanEntity = vlanEntityIterator.next();
				String vlanID = vlanEntity.getVlanID() + "";
				String vlanName = "vlan名称";
				String tagStr= "";
				String untagStr = "";
				String tagValue="";
				String untagValue="";

				Set<VlanPortConfig> portConfigList = vlanEntity.getPortConfig();
				Iterator iterator = portConfigList.iterator();
				while(iterator.hasNext()){
					VlanPortConfig portConfig = (VlanPortConfig)iterator.next();
					int vlanPortID = portConfig.getPortNo();
					char portTagChar = portConfig.getPortTag();
					
//					String portTag = Integer.parseInt(Character.toString(portTagChar));

					if (portTagChar == TAG){
						tagStr = tagStr + vlanPortID+",";
					}else if (portTagChar == UNTAG){
						untagStr = untagStr + vlanPortID+",";
					}
				}
				
				if (tagStr.length() > 0){
					tagValue = tagStr.substring(0,tagStr.length()-1);
				}
				if (untagStr.length() >0){
					untagValue = untagStr.substring(0,untagStr.length()-1);
				}
				
				List rowData = new ArrayList();
				rowData.add(0, vlanID);
				rowData.add(1,vlanName);
				rowData.add(2, tagValue);
				rowData.add(3,untagValue);
				rowData.add(4,vlanEntity);
				dataSum.add(rowData);
			}
			
//			Set<VlanPort> vlanPortList = vlanConfig.getVlanPorts();
//			Iterator<VlanPort> vlanPortIterator = vlanPortList.iterator();
//			while(vlanPortIterator.hasNext()){
//				VlanPort vlanPort = vlanPortIterator.next();
//				int port = vlanPort.getPortNO();
//				for (int k = 0 ; k < dataSum.size(); k++){
//					int portID = new Integer(((ArrayList)dataSum.get(k)).get(2).toString());
//					if (port == portID){
//						int priority = vlanPort.getPriority();
//						int pvid = vlanPort.getPvid();
//						((ArrayList)dataSum.get(k)).add(4, priority);
//						((ArrayList)dataSum.get(k)).add(5, pvid);
//					}
//				}
//			}
		}
		catch(Exception e){
			LOG.error("setDataList is Failure:{}",e);
			return;
		}	
		vlanTableModel.setDataList(dataSum);
		vlanTableModel.fireTableDataChanged();
	}
	
	public void setPortDataList(List<List> list){
		vlanPortModel.setDataList(list);
		vlanPortModel.fireTableDataChanged();
	}
	
	public void setSelectVlanToPort(int row){
		vlanPortModel.clearPortStatus();
		String tagValue = vlanTableModel.getValueAt(row, 2).toString();
		String untagValue = vlanTableModel.getValueAt(row, 3).toString();
		
		String[] tagValues = tagValue.split(",");
		String[] untagValues = untagValue.split(",");
		
		for (int i = 0 ; i < tagValues.length ; i++){
			if (null == tagValues[i] || tagValues[i].equals("")){
				break;
			}
			int iTagValue = Integer.parseInt(tagValues[i]);
			Object objectPort = vlanPortModel.getValueAt(iTagValue-1, 0);
			if (objectPort instanceof JCheckBox){
				vlanPortModel.setSelectedValueAt(objectPort,true,iTagValue-1,0);
			}
			
			Object objectTag = vlanPortModel.getValueAt(iTagValue-1, 1);
			if (objectTag instanceof JRadioButton){
				vlanPortModel.setSelectedValueAt(objectTag,true,iTagValue-1,1);
			}
		}
		
		for (int i = 0 ; i < untagValues.length ; i++){
			if (null == untagValues[i] || untagValues[i].equals("")){
				break;
			}
			int iUntagValue = Integer.parseInt(untagValues[i]);
			Object objectPort = vlanPortModel.getValueAt(iUntagValue-1, 0);
			if (objectPort instanceof JCheckBox){
				vlanPortModel.setSelectedValueAt(objectPort,true,iUntagValue-1,0);
			}
			
			Object objectUntag = vlanPortModel.getValueAt(iUntagValue-1, 2);
			if (objectUntag instanceof JRadioButton){
				vlanPortModel.setSelectedValueAt(objectUntag,true,iUntagValue-1,2);
			}
		}
	}
	
	public VlanConfig getVlanConfig(){
		VlanConfig vlanConfig = new VlanConfig();
		
		Set<VlanEntity> vlanEntityList = new LinkedHashSet<VlanEntity>();
		
		List<List> dataList = vlanTableModel.getDataList();
		for(int i = 0 ; i < dataList.size(); i++){
			VlanEntity vlanEntity = new VlanEntity();
			
			List rowData = dataList.get(i);
			String vlanId = rowData.get(0).toString();
			
			String[] tagPort = rowData.get(2).toString().split(",");
			String[] untagPort = rowData.get(3).toString().split(",");
			Set<VlanPortConfig> vlanPortConfigList = new HashSet<VlanPortConfig>();
			for(int j = 0 ; j < tagPort.length; j++){
				VlanPortConfig vlanPortConfig = new VlanPortConfig();
				if (null == tagPort[j] || tagPort[j].equals("")){
					break;
				}
				vlanPortConfig.setPortNo(Integer.parseInt(tagPort[j]));
				vlanPortConfig.setPortTag(TAG);
				
				vlanPortConfigList.add(vlanPortConfig);
			}
			for(int j = 0 ; j < untagPort.length; j++){
				VlanPortConfig vlanPortConfig = new VlanPortConfig();
				if (null == untagPort[j] || untagPort[j].equals("")){
					break;
				}
				vlanPortConfig.setPortNo(Integer.parseInt(untagPort[j]));
				vlanPortConfig.setPortTag(UNTAG);
				
				vlanPortConfigList.add(vlanPortConfig);
			}
			
			vlanEntity.setVlanID(NumberUtils.toInt(vlanId));
			vlanEntity.setPortConfig(vlanPortConfigList);
			vlanEntityList.add(vlanEntity);
		}
		vlanConfig.setVlanEntitys(vlanEntityList);

		return vlanConfig;
	}
	
	
	
	
	public void clearSelectPortStatus(Object object){
		vlanPortModel.clearSelected(object);
	}
	
	public void setDefaultPortSelected(Object object){
		vlanPortModel.setDefaultPortSelected(object);
	}
	
	public void fireTableDataChanged(){
		vlanTableModel.fireTableDataChanged();
	}
	
	
	
	
	public Vector getTagPort(){
		return vlanPortModel.getTagPort();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
