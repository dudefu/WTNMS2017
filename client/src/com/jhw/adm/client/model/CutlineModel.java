package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.server.entity.util.VirtualType;

/**
 * 
 * model for CutlineView
 * @author Administrator
 *
 */
@Component(CutlineModel.ID)
public class CutlineModel {

	public static final String ID = "cutlineModel";
	private static final String[] STATIC_IMAGE_NAMES = {NetworkConstants.NORMAL_LINE,NetworkConstants.DISCONNECT_LINE,NetworkConstants.LINK_BLOCK_LINE,NetworkConstants.OPTICAL_FIBER,NetworkConstants.SUBNET,NetworkConstants.FRONT_END};
	private static final String[] STATIC_IMAGE_DESCRIPTIONS = {"网线","端口断开","端口堵塞","光纤","子网","前置机"};
	private static final String[] EQUIPMENT_IMAGE_NAMES = {
			NetworkConstants.LEVEL_3_SWITCHER, NetworkConstants.EPON8506,
			NetworkConstants.IEL3010_HV, NetworkConstants.ONU,
			NetworkConstants.BEAMSPLITTER,
			NetworkConstants.IETH802, NetworkConstants.IETH804,
			NetworkConstants.IETH8008, NetworkConstants.IETH8008U,
			NetworkConstants.IETH9028, NetworkConstants.IETH9307,
			NetworkConstants.SWITCHER };
	private static final String[] EQUIPMENT_IMAGE_DESCRIPTIONS = { 
			"IETH3424", "IEL8006",
			"IEL3010", "ONU",
			"分光器",
			"IETH802", "IETH804-H", 
			"IETH8008", "IETH8008-U", 
			"IETH9028", "IETH9307",
			"非网管型" };
	private List<byte[]> CUSTOM_IMAGE_NAMES = null;
	private List<String> CUSTOM_IMAGE_DESCRIPTIONS = null;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@SuppressWarnings("unchecked")
	public void setCustomImageInfo(){
		CUSTOM_IMAGE_NAMES = new ArrayList<byte[]>();
		CUSTOM_IMAGE_DESCRIPTIONS = new ArrayList<String>();
		
		List< VirtualType> virtualTypeList = (List<VirtualType>) remoteServer
				.getService().findAll(VirtualType.class);
		
		if(virtualTypeList.size() == 0){
			return;
		}
		
		for(VirtualType virtualType : virtualTypeList){
			CUSTOM_IMAGE_NAMES.add(virtualType.getBytes());
			CUSTOM_IMAGE_DESCRIPTIONS.add(virtualType.getType());
		}
	}

	public List<byte[]> getCUSTOM_IMAGE_NAMES() {
		return CUSTOM_IMAGE_NAMES;
	}

	public List<String> getCUSTOM_IMAGE_DESCRIPTIONS() {
		return CUSTOM_IMAGE_DESCRIPTIONS;
	}

	public static String[] getStaticImageNames() {
		return STATIC_IMAGE_NAMES;
	}

	public static String[] getStaticImageDescriptions() {
		return STATIC_IMAGE_DESCRIPTIONS;
	}
	
	public static String[] getEquipmentImageNames() {
		return EQUIPMENT_IMAGE_NAMES;
	}

	public static String[] getEquipmentImageDescriptions() {
		return EQUIPMENT_IMAGE_DESCRIPTIONS;
	}

	@PostConstruct
	protected void initialize(){
		//do nothing
	}
	
}
