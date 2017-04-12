package com.jhw.adm.client.diagram;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.Tuple;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.epon.EponSplitter;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
//import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;

/**
 * EPON节点生成器<br>
 * 根据发现回来的OLT/ONU，生成拓扑节点
 */
public class EponNodeGenerator {

	public EponNodeGenerator(EquipmentRepository equipmentRepository) {
		this.equipmentRepository = equipmentRepository;
	}
	
	public void generate(TopDiagramEntity topDiagramEntity, String fepCode) {
		int maxWidth = ClientUtils.getMaxBounds().width;
		lastPoint = new Point(maxWidth / 2, 10);
		
		List<OLTEntity>  listOfOlt = equipmentRepository.findOlts();
		List<ONUEntity>  listOfOnu = equipmentRepository.findOnus();
						
		createOltNode(listOfOlt);
		createOnuNode(listOfOnu);

		for (NodeEntity createdNode : createdOltNodeMap.values()) {
			topDiagramEntity.getNodes().add(createdNode);
			createdNode.setTopDiagramEntity(topDiagramEntity);
		}
		for (NodeEntity createdNode : createdOnuNodeMap.values()) {
			topDiagramEntity.getNodes().add(createdNode);
			createdNode.setTopDiagramEntity(topDiagramEntity);
		}
		for (NodeEntity createdNode : createdSplitterNodeMap.values()) {
			topDiagramEntity.getNodes().add(createdNode);
			createdNode.setTopDiagramEntity(topDiagramEntity);
		}
	}
	
	/* 生成OLT节点 */
	private void createOltNode(List<OLTEntity> listOfOlt) {
		LOG.info("开始生成OLT拓扑节点====================>");
		createdOltNodeMap = new HashMap<String, EponTopoEntity>();
		int lastX = lastPoint.x;
		for (OLTEntity olt : listOfOlt) {			
			if (!olt.isSyschorized()) {
				LOG.info(String.format("OLT[%s][%s]已经被标志为[删除]，不会添加到拓扑里面", 
						olt.getId(), olt.getIpValue()));
				continue;
			}
			
			EponTopoEntity oltNode = new EponTopoEntity();
			oltNode.setGuid(UUID.randomUUID().toString());
			oltNode.setOltEntity(olt);
			oltNode.setIpValue(olt.getIpValue());
			oltNode.setName(olt.getIpValue());
			oltNode.setX(lastPoint.x);
			oltNode.setY(lastPoint.y);
			createdOltNodeMap.put(olt.getIpValue(), oltNode);
			lastPoint.x += 120;
			LOG.info(String.format("添加OLT[%s][%s]", olt.getId(), olt.getIpValue()));			
		}
		lastPoint.x = lastX;
		lastPoint.y += 100;

		LOG.info("<====================完成OLT拓扑节点[size: {}]的生成", createdOltNodeMap.values().size());
	}
	
	/* 生成ONU拓扑节点 */
	private void createOnuNode(List<ONUEntity> listOfOnu) {
		LOG.info("开始生成ONU拓扑节点====================>");
		createdOnuNodeMap = new HashMap<String, ONUTopoNodeEntity>();
		createdSplitterNodeMap = new HashMap<Tuple<String, Integer, Integer>, Epon_S_TopNodeEntity>();

		for (ONUEntity onu : listOfOnu) {
			if (!onu.isSyschorized()) {
				LOG.info(String.format("ONU[%s][%s]已经被标志为[删除]，不会添加到拓扑里面", 
						onu.getId(), onu.getMacValue()));
				continue;
			}
			
			ONUTopoNodeEntity onuNode = new ONUTopoNodeEntity();
			onuNode.setGuid(UUID.randomUUID().toString());
			onuNode.setOnuEntity(onu);
			onuNode.setName(Integer.toString(onu.getSequenceNo()));
			onuNode.setMacValue(onu.getMacValue());
			createdOnuNodeMap.put(onu.getMacValue(), onuNode);
			LOG.info(String.format("添加ONU[%s]", onu.getMacValue()));
		}
		
		LOG.info("<====================完成ONU拓扑节点[size: {}]的生成", createdOnuNodeMap.values().size());
		createSplitterNode();
	}
	
	/* 生成分光器拓扑节点 */
	private void createSplitterNode() {
		LOG.info("开始生成分光器拓扑节点====================>");
		int lastX = lastPoint.x;
		for (ONUTopoNodeEntity onuNode : createdOnuNodeMap.values()) {
			ONUEntity onu = onuNode.getOnuEntity();
			
			for (LLDPInofEntity linkInfo : onu.getLldpinfos()) {
				String address = linkInfo.getRemoteIP();
				int slotNo = linkInfo.getRemoteSlot();
				int portNo = linkInfo.getRemotePortNo();
				Tuple<String, Integer, Integer> splitterKey = 
					new Tuple<String, Integer, Integer>(address, slotNo, portNo);
				
				if (createdSplitterNodeMap.containsKey(splitterKey)) {
					LOG.info(String.format("分光器[%s:%s-%s]已经存在，不会重复添加", address, slotNo, portNo));
				} else {
					EponSplitter splitter = new EponSplitter();
					Epon_S_TopNodeEntity splitterNode = new Epon_S_TopNodeEntity();
					splitterNode.setName(String.format("%s/%s", slotNo, portNo));
					EponTopoEntity oltNode = createdOltNodeMap.get(address);
					splitterNode.setOltGuid(oltNode.getGuid());
					splitterNode.setGuid(UUID.randomUUID().toString());
					splitterNode.setEponSplitter(splitter);
					splitterNode.setX(lastPoint.x);
					splitterNode.setY(lastPoint.y);
					createdSplitterNodeMap.put(splitterKey, splitterNode);
					lastPoint.x += 100;
					LOG.info(String.format("添加分光器[%s:%s-%s]", address, slotNo, portNo));	
				}
			}
		}
		
		lastPoint.x = lastX;
		lastPoint.y += 100;
		
		for (ONUTopoNodeEntity onuNode : createdOnuNodeMap.values()) {
			onuNode.setX(lastPoint.x);
			onuNode.setY(lastPoint.y);
			lastPoint.x += 60;
		}
		LOG.info("<====================完成分光器拓扑节点[size: {}]的生成", createdSplitterNodeMap.values().size());
	}
	
	public EponTopoEntity findOlt(String address) {
		EponTopoEntity found = null;
		
		if (createdOltNodeMap.containsKey(address)) {
			found = createdOltNodeMap.get(address);
		}
		
		return found;
	}
	
	public Epon_S_TopNodeEntity findSplitter(Tuple<String, Integer, Integer> tuple) {
		Epon_S_TopNodeEntity found = null;
		
		if (createdSplitterNodeMap.containsKey(tuple)) {
			found = createdSplitterNodeMap.get(tuple);
		}
		
		return found;
	}
	
	public Collection<EponTopoEntity> getAllOlt() {
		return createdOltNodeMap.values();
	}
	
	public Collection<Epon_S_TopNodeEntity> getAllSplitter() {
		return createdSplitterNodeMap.values();
	}
	
	public Set<Tuple<String, Integer, Integer>> getAllSplitterKey() {
		return createdSplitterNodeMap.keySet();
	}
	
	public Collection<ONUTopoNodeEntity> getAllOnu() {
		return createdOnuNodeMap.values();
	}

	private Point lastPoint;
	private Map<String, EponTopoEntity> createdOltNodeMap;
	private Map<String, ONUTopoNodeEntity> createdOnuNodeMap;
	private Map<Tuple<String, Integer, Integer>, Epon_S_TopNodeEntity> createdSplitterNodeMap;
	private final EquipmentRepository equipmentRepository;
	private static final Logger LOG = LoggerFactory.getLogger(EponNodeGenerator.class);
}