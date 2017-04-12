package com.jhw.adm.client.diagram;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ApplicationException;
import com.jhw.adm.client.core.Pair;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchRingInfo;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.RingEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.Constants;

/**
 * �������ڵ�������<br>
 * ���ݷ��ֻ����Ľ��������������˽ڵ�
 */
public class SwitcherNodeGenerator {

	public SwitcherNodeGenerator(EquipmentRepository equipmentRepository) {
		this.equipmentRepository = equipmentRepository;
	}
	
	public void generate(TopDiagramEntity topDiagramEntity, String fepCode) {
		List<SwitchLayer3> listOfLayer3Switcher = equipmentRepository.findLayer3Switchers();
		//List<SwitchNodeEntity> listOfSwitcher = equipmentRepository.findSwitchers();
		List<SwitchNodeEntity> listOfSwitcher = equipmentRepository.findSwitchers(fepCode);
		
		int maxWidth = ClientUtils.getMaxBounds().width;
		lastPoint = new Point(maxWidth / 4, 10);
				
		createLayer3SwitcherNode(listOfLayer3Switcher);
		
		calcLayer3SwitcherCoordinates(createdLayer3SwitcherNodeMap.values());
				
		createRingNode(listOfSwitcher);
		
		createSwitcherNode(listOfSwitcher);
		
		calcSwitcherCoordinates(createdSwitcherNodeMap.values());
		
		for (NodeEntity createdNode : createdLayer3SwitcherNodeMap.values()) {
			topDiagramEntity.getNodes().add(createdNode);
			createdNode.setTopDiagramEntity(topDiagramEntity);
		}
		for (NodeEntity createdNode : createdSwitcherNodeMap.values()) {
			topDiagramEntity.getNodes().add(createdNode);
			createdNode.setTopDiagramEntity(topDiagramEntity);
		}
		for (NodeEntity createdNode : createdRingNodeMap.values()) {
			topDiagramEntity.getNodes().add(createdNode);
			createdNode.setTopDiagramEntity(topDiagramEntity);
		}
	}
	
	/* �������㽻�������˽ڵ� */
	private void createSwitcherNode(List<SwitchNodeEntity> listOfSwitcher) {
		createdSwitcherNodeMap = new HashMap<String, SwitchTopoNodeEntity>();
		LOG.info("��ʼ�������㽻�������˽ڵ�====================>");
		
		for (SwitchNodeEntity switcher : listOfSwitcher) {
			
			if (switcher.getBaseConfig() == null) {
				throw new ApplicationException("��������ʧ�ܣ�BaseConfigΪnull");
			}
			
			if (!switcher.isSyschorized()) {
				LOG.info(String.format("������[%s][%s]�Ѿ�����־Ϊ[ɾ��]��������ӵ���������", 
						switcher.getId(), switcher.getBaseConfig().getIpValue()));
				continue;
			}
			
			SwitchTopoNodeEntity switcherNode = new SwitchTopoNodeEntity();	
			switcherNode.setStatus(Constants.NORMAL);
			switcherNode.setNodeEntity(switcher);
			switcherNode.setIpValue(switcher.getBaseConfig().getIpValue());
			switcherNode.setName(switcher.getBaseConfig().getIpValue());
			switcherNode.setGuid(UUID.randomUUID().toString());
			LOG.info(String.format("��ӽ�����[%s][%s]", 
					switcher.getId(), switcher.getBaseConfig().getIpValue()));
			createdSwitcherNodeMap.put(switcher.getBaseConfig().getIpValue(), switcherNode);
		}

		LOG.info("<====================������㽻�������˽ڵ�[size: {}]������", createdSwitcherNodeMap.values().size());
	}
	
	/**
	 * �������㽻��������
	 */
	private void calcSwitcherCoordinates(Collection<SwitchTopoNodeEntity> createdSwitcherCollection) {
		int count = createdSwitcherCollection.size();
		if (count == 0) return;
		
		for (RingEntity ringNode : createdRingNodeMap.values()) {
			int ringNo = ringNode.getRingNo();
			List<SwitchNodeEntity> listOfSwitcher = ringAndSwitcherMap.get(ringNo);
			
			count = listOfSwitcher.size();
			
			double radiusX = 100 + count * 10;
	    	double radiusY = 75 + count * 10;
	    	
	    	double ringX = lastPoint.x - radiusX;
			double ringY = lastPoint.y;
	    	
	    	int index = 0;
	    	
			for (SwitchNodeEntity switcher : listOfSwitcher) {
				SwitchTopoNodeEntity switcherNode = 
					createdSwitcherNodeMap.get(switcher.getBaseConfig().getIpValue());
				
	    		double angle = 2d*Math.PI/count*index;
	        	double tox = radiusX * Math.sin(angle);
	        	double toy = radiusY * Math.cos(angle);
				
	        	switcherNode.setX(tox + ringX + radiusX);
	        	switcherNode.setY(toy + ringY + radiusY);
	    		index++;
			}
	    	
	    	lastPoint.y += (radiusY * 2.6);
		}
		
		List<SwitchTopoNodeEntity> lastSwticherNodes = new ArrayList<SwitchTopoNodeEntity>();
		for (SwitchTopoNodeEntity switcherNode : createdSwitcherNodeMap.values()) {
			if (switcherNode.getNodeEntity().getRings() != null &&
				switcherNode.getNodeEntity().getRings().size() > 0) {
				continue;
			}
			lastSwticherNodes.add(switcherNode);			
		}

		count = lastSwticherNodes.size();
		double radiusX = 100 + count * 10;
    	double radiusY = 75 + count * 10;
    	
		for (int index = 0; index < count; index++) {			
	    	
	    	double ringX = lastPoint.x - radiusX;
			double ringY = lastPoint.y;
	    	
			SwitchTopoNodeEntity switcherNode = lastSwticherNodes.get(index);
			
    		double angle = 2d*Math.PI/count*index;
        	double tox = radiusX * Math.sin(angle);
        	double toy = radiusY * Math.cos(angle);
			
        	switcherNode.setX(tox + ringX + radiusX);
        	switcherNode.setY(toy + ringY + radiusY);
		}
    	lastPoint.y += (radiusY * 2.6);
	}
	
	/* �������㽻�������˽ڵ� */
	private void createLayer3SwitcherNode(List<SwitchLayer3> listOfLayer3Switcher) {
		LOG.info("��ʼ�������㽻�������˽ڵ�[size: {}]====================>", listOfLayer3Switcher.size());
		createdLayer3SwitcherNodeMap = 
			new HashMap<String, SwitchTopoNodeLevel3>();
		
		for (SwitchLayer3 layer3Switcher : listOfLayer3Switcher) {
			
			if (!layer3Switcher.isSyschorized()) {
				LOG.info(String.format("���㽻����[%s][%s]�Ѿ�����־Ϊ[ɾ��]��������ӵ���������", 
						layer3Switcher.getId(), layer3Switcher.getIpValue()));
				continue;
			}
						
			SwitchTopoNodeLevel3 layer3SwitcherNode = new SwitchTopoNodeLevel3();			
			layer3SwitcherNode.setSwitchLayer3(layer3Switcher);
			layer3SwitcherNode.setIpValue(layer3Switcher.getIpValue());
			layer3SwitcherNode.setName(layer3Switcher.getIpValue());
			layer3SwitcherNode.setGuid(UUID.randomUUID().toString());
			LOG.info(String.format("������㽻����[%s][%s]", 
					layer3Switcher.getId(), layer3Switcher.getIpValue()));
			createdLayer3SwitcherNodeMap.put(layer3Switcher.getIpValue(), layer3SwitcherNode);
		}
		LOG.info("<====================������㽻�������˽ڵ������");
	}
	
	/**
	 * �������㽻��������
	 */
	private void calcLayer3SwitcherCoordinates(Collection<SwitchTopoNodeLevel3> createdLayer3SwitcherCollection) {
		int count = createdLayer3SwitcherCollection.size();
		if (count == 0) return;
		
    	double radiusX = 100 + count * 10;
    	double radiusY = 75 + count * 10;
    	
    	double ringX = lastPoint.x - radiusX;
		double ringY = lastPoint.y;
    	
    	int index = 0;

    	for (SwitchTopoNodeLevel3 layer3SwitcherNode : createdLayer3SwitcherCollection) {
    		double angle = 2d*Math.PI/count*index;
        	double tox = radiusX * Math.sin(angle);
        	double toy = radiusY * Math.cos(angle);
			
			layer3SwitcherNode.setX(tox + ringX + radiusX);
			layer3SwitcherNode.setY(toy + ringY + radiusY);
    		index++;
    	}
    	
    	lastPoint.y += (radiusY * 2.6);
	}
	
	/* �������㽻�����Ļ� */
	private void createRingNode(List<SwitchNodeEntity> listOfSwitcher) {
		LOG.info("��ʼ�������㽻�����Ļ�====================>");
		ringAndSwitcherMap = new HashMap<Integer, List<SwitchNodeEntity>>();
		Map<Integer, Pair<String, Integer>> blockMap = new HashMap<Integer, Pair<String, Integer>>();
		for (SwitchNodeEntity switcher : listOfSwitcher) {	
			
			if (!switcher.isSyschorized()) {
				LOG.info(String.format("������[%s][%s]�Ѿ�����־Ϊ[ɾ��]��������ӵ�������", 
						switcher.getId(), switcher.getBaseConfig().getIpValue()));
				continue;
			}
			
			if (switcher.getBaseConfig() == null) {
				LOG.error("��������ʧ�ܣ���������BaseConfigΪnull");
				throw new ApplicationException("��������ʧ�ܣ���������BaseConfigΪnull");
			}
			
			if (switcher.getRings() == null || switcher.getRings().size() == 0) {
				LOG.info(String.format("������[%s][%s]û�д��ڻ�����", 
						switcher.getId(), switcher.getBaseConfig().getIpValue()));
				continue;
			}
			
			for (SwitchRingInfo ring : switcher.getRings()) {
				int ringNo = ring.getRingID();				
				
				LOG.info(String.format("������[%s][%s]�˿�[%s]���ڻ�[%s]����", 
						switcher.getId(), switcher.getBaseConfig().getIpValue(), ring.getPortNo(), ringNo));
								
				if (ringAndSwitcherMap.containsKey(ringNo)) {
					if (ringAndSwitcherMap.get(ringNo).contains(switcher)) {
						LOG.info(String.format("������[%s][%s]�Ѿ����ڻ�[%s]���棬�����ظ����", 
								switcher.getId(), switcher.getBaseConfig().getIpValue(), ringNo));
					} else {
						ringAndSwitcherMap.get(ringNo).add(switcher);
					}
				} else {
					List<SwitchNodeEntity> swticherNodes = new ArrayList<SwitchNodeEntity>();					
					swticherNodes.add(switcher);
					ringAndSwitcherMap.put(ringNo, swticherNodes);
				}

				if (!ring.isForwarding()) {
					LOG.info(String.format("������[%s]���ڻ�[%s]����Ķ˿�[%s]��ͨ", 
							switcher.getBaseConfig().getIpValue(), ringNo, ring.getPortNo()));
					
					if (!blockMap.containsKey(ringNo)) {
						blockMap.put(ringNo, 
								new Pair<String, Integer>(switcher.getBaseConfig().getIpValue(), ring.getPortNo()));
					}
				}
			}
		}
		
		LOG.info("������{}�����㽻�����Ļ�", ringAndSwitcherMap.values().size());
		createdRingNodeMap = new HashMap<Integer, RingEntity>(ringAndSwitcherMap.values().size());
		for (int ringNo : ringAndSwitcherMap.keySet()) {
			RingEntity ringNode = new RingEntity();
			ringNode.setRingNo(ringNo);
			createdRingNodeMap.put(ringNo, ringNode);
			
			if (blockMap.containsKey(ringNo)) {
				Pair<String, Integer> pair = blockMap.get(ringNo);
				ringNode.setIp_Value(pair.getHead());
				ringNode.setPortNo(pair.getTail());
			}			
		}
		LOG.info("<====================������㽻������������");
	}
	
	public SwitchTopoNodeLevel3 findLayer3Switcher(String address) {
		SwitchTopoNodeLevel3 found = null;
		
		if (createdLayer3SwitcherNodeMap.containsKey(address)) {
			found = createdLayer3SwitcherNodeMap.get(address);
		}
		
		return found;
	}
	
	public SwitchTopoNodeEntity findSwitcher(String address) {
		SwitchTopoNodeEntity found = null;
		
		if (createdSwitcherNodeMap.containsKey(address)) {
			found = createdSwitcherNodeMap.get(address);
		}
		
		return found;
	}
	
	public Collection<SwitchTopoNodeLevel3> getAllLayer3Switcher() {
		return createdLayer3SwitcherNodeMap.values();
	}
	
	public Collection<SwitchTopoNodeEntity> getAllSwitcher() {
		return createdSwitcherNodeMap.values();
	}
	
	private Point lastPoint;
	private Map<String, SwitchTopoNodeLevel3> createdLayer3SwitcherNodeMap;			
	private Map<Integer, RingEntity> createdRingNodeMap;
	private Map<Integer, List<SwitchNodeEntity>> ringAndSwitcherMap;
	private Map<String, SwitchTopoNodeEntity> createdSwitcherNodeMap;
	private final EquipmentRepository equipmentRepository;
	private static final Logger LOG = LoggerFactory.getLogger(SwitcherNodeGenerator.class);
}