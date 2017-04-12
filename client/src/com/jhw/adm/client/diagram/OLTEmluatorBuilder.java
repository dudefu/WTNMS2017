package com.jhw.adm.client.diagram;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.util.Images;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.draw.LightEdit;
import com.jhw.adm.client.draw.LightFigure;
import com.jhw.adm.client.draw.PanelFigure;
import com.jhw.adm.client.draw.PartFigure;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;

@Component(OLTEmluatorBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class OLTEmluatorBuilder implements EponEmluatorBuilder {

	public static final String ID = "OLTEmluatorBuilder";
	public static final String EMULATION_TYPE = "OLT";
	
	private final Map<Integer, PartFigure> figurePortIndex;
	private final Map<Integer, LightEdit> dataSignalMap;
	private final Map<Integer, LightEdit> workSignalMap;
	private final Map<Integer, LightEdit> upDataSignalMap;
	private final Map<Integer, LightEdit> downDataSignalMap;
	private final double offset = 5;
	
	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	public OLTEmluatorBuilder(){
		figurePortIndex = new HashMap<Integer, PartFigure>();
		dataSignalMap = new HashMap<Integer, LightEdit>();
		workSignalMap = new HashMap<Integer, LightEdit>();
		upDataSignalMap = new HashMap<Integer, LightEdit>();
		downDataSignalMap = new HashMap<Integer, LightEdit>();
	}
	
	@Override
	public void buildLights(Drawing drawing, OLTEntity eponEntity) {

		Map<Integer, Point> locationDataMap = new HashMap<Integer, Point>();
		locationDataMap.put(1, new Point(555,102));//g1
		locationDataMap.put(2, new Point(555,88));//g2
		locationDataMap.put(3, new Point(555,74));//g3
		locationDataMap.put(4, new Point(555,60));//g4
		
		Map<Integer, Point> locationUpDataMap = new HashMap<Integer, Point>();
		locationUpDataMap.put(1, new Point(60,62));//eth
		
		Map<Integer, Point> locationDownDataMap = new HashMap<Integer, Point>();
		locationDownDataMap.put(1, new Point(360,102));//epon act
		
		Map<Integer, Point> locationWorkMap = new HashMap<Integer, Point>();
		locationWorkMap.put(1, new Point(360,88));//epon link
		locationWorkMap.put(2, new Point(60,78));//sys,电源灯亮，该灯闪
		locationWorkMap.put(3, new Point(60,94));//pwr
		
		List<OLTPort> ponPortList = new ArrayList<OLTPort>();//pon口
		List<OLTPort> fPowerPortList = new ArrayList<OLTPort>();//F类电口,ETH
		List<OLTPort> gPowerPortList = new ArrayList<OLTPort>();//G类电口
		if(null == eponEntity){
			eponEntity.setPorts(new HashSet<OLTPort>());
		}
	
		for(OLTPort oltPort : eponEntity.getPorts()){
			if (oltPort.getPortType().toUpperCase().equals("GigaEthernet".toUpperCase())) {//G类电口
				gPowerPortList.add(oltPort);
			}else if(oltPort.getPortType().toUpperCase().equals("FastEthernet".toUpperCase())) {//F类电口,ETH
				fPowerPortList.add(oltPort);
			}else if (oltPort.getPortType().toUpperCase().equals("EPON".toUpperCase())) {//pon口
				if(ponPortList.size() == 0){
					ponPortList.add(oltPort);
				}
			}
		}
		
		drawLight(ponPortList,locationDownDataMap,downDataSignalMap,drawing);
		drawLight(fPowerPortList,locationUpDataMap,upDataSignalMap,drawing);
		drawLight(gPowerPortList,locationDataMap,dataSignalMap,drawing);
		
		for(int i = 1;i < 4;i++){//work signal
			OLTPort portEntity = new OLTPort();
			AffineTransform at = new AffineTransform();
			LightEdit workEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
			LightFigure workFigure = workEdit.restoreFigure(portEntity);
			Point dataPoint = locationWorkMap.get(i);
			at.translate(dataPoint.x + offset, dataPoint.y + offset);
			workFigure.transform(at);
			drawing.add(workFigure);
			workSignalMap.put(i, workEdit);
//			workEdit.turnOn();
		}
	}

	private void drawLight(List<OLTPort> list, Map<Integer, Point> locationMap,
			Map<Integer, LightEdit> dataMap, Drawing drawing) {
		for(OLTPort portEntity : list){//data signal
			AffineTransform at = new AffineTransform();
			LightEdit dataEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
			LightFigure dataFigure = dataEdit.restoreFigure(portEntity);
			Point dataPoint = locationMap.get(portEntity.getProtNo());
			at.translate(dataPoint.x + offset, dataPoint.y + offset);
			dataFigure.transform(at);
			drawing.add(dataFigure);
			dataMap.put(portEntity.getProtNo(), dataEdit);
//			dataEdit.flash();
		}
	}
	
	@Override
	public void buildPorts(Drawing drawing, OLTEntity eponEntity) {

		Image backgroundImage = imageRegistry.getImageIcon(NetworkConstants.OLT_E).getImage();

		Image fddiImage = imageRegistry.getImageIcon(NetworkConstants.OLT_FDDI).getImage();
		Image g13Image = imageRegistry.getImageIcon(NetworkConstants.OLT_G13).getImage();
		Image g24Image = imageRegistry.getImageIcon(NetworkConstants.OLT_G24).getImage();

		AffineTransform at = new AffineTransform();
		PanelFigure emulationalFigure = new PanelFigure(Images
				.toBufferedImage(backgroundImage));
		drawing.add(emulationalFigure);
		at = new AffineTransform();
		at.translate(offset, offset);
		emulationalFigure.transform(at);
		
		List<OLTPort> ponPortList = new ArrayList<OLTPort>();//pon口
		List<OLTPort> fPowerPortList = new ArrayList<OLTPort>();//F类电口,ETH
		List<OLTPort> gPowerPortList = new ArrayList<OLTPort>();//G类电口
		List<OLTPort> gFddiPortList = new ArrayList<OLTPort>();//G类网口
		
		if(eponEntity == null){
			eponEntity.setPorts(new HashSet<OLTPort>());
		}
	
		for(OLTPort oltPort : eponEntity.getPorts()){
			if (oltPort.getPortType().toUpperCase().equals("GigaEthernet".toUpperCase())) {//G类电口
				gPowerPortList.add(oltPort);
			}else if(oltPort.getPortType().toUpperCase().equals("FastEthernet".toUpperCase())){//F类电口,ETH
				fPowerPortList.add(oltPort);
			}else if (oltPort.getPortType().toUpperCase().equals("EPON".toUpperCase())) {//pon口
				if(ponPortList.size() == 0){
					ponPortList.add(oltPort);
				}
			}else {//G类网口
				gFddiPortList.add(oltPort);
			}
		}
		// G类网口
		Map<Integer, Point> locationGFMap = new HashMap<Integer, Point>();
		locationGFMap.put(1, new Point(372,88));//g1
		locationGFMap.put(2, new Point(400,88));//g2
		locationGFMap.put(3, new Point(428,88));//g3
		locationGFMap.put(4, new Point(456,88));//g4
		// G类电口
		Map<Integer, Point> locationGPMap = new HashMap<Integer, Point>();
		locationGPMap.put(1, new Point(486,86));//g1
		locationGPMap.put(2, new Point(486,53));//g2
		locationGPMap.put(3, new Point(519,86));//g3
		locationGPMap.put(4, new Point(519,53));//g4
		//pon口
		Map<Integer, Point> locationPonMap = new HashMap<Integer, Point>();
		locationPonMap.put(1, new Point(332,88));//pon
		//F类电口
		Map<Integer, Point> locationFPMap = new HashMap<Integer, Point>();
		locationFPMap.put(1, new Point(76,53));//ETH
		
		// G类电口
		for(OLTPort portEntity : gPowerPortList){
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			if(portEntity.getProtNo() == 2 || portEntity.getProtNo() == 4){
				partFigure.setBounds(new Rectangle2D.Double(0, 0, g24Image
						.getWidth(null), g24Image.getHeight(null)));
				partFigure.setBufferedImage(Images.toBufferedImage(g24Image));
			}else if(portEntity.getProtNo() == 1 || portEntity.getProtNo() == 3){
				partFigure.setBounds(new Rectangle2D.Double(0, 0, g13Image
						.getWidth(null), g13Image.getHeight(null)));
				partFigure.setBufferedImage(Images.toBufferedImage(g13Image));
			}
			
			at = new AffineTransform();
			Point point = locationGPMap.get(portEntity.getProtNo());
			at.translate(point.x + offset, point.y + offset);
			partFigure.transform(at);
			emulationalFigure.add(partFigure);
			figurePortIndex.put(portEntity.getProtNo(), partFigure);
		}
		//F类电口
		drawPort(fPowerPortList, g24Image, locationFPMap, emulationalFigure);
		// G类网口
		drawPort(gFddiPortList, fddiImage, locationGFMap, emulationalFigure);
		//pon口
		drawPort(ponPortList, fddiImage, locationPonMap, emulationalFigure);
	}
	
	private void drawPort(List<OLTPort> list, Image image,
			Map<Integer, Point> locationMap, PanelFigure figure) {
		if(list.size() == 0){
			OLTPort portEntity = new OLTPort();
			for(int i = 1;i < 5;i++){
				PartEdit partEdit = new PartEdit();
				PartFigure partFigure = partEdit.restoreFigure(portEntity);
				partFigure.setBounds(new Rectangle2D.Double(0, 0, image
						.getWidth(null), image.getHeight(null)));
				partFigure.setBufferedImage(Images.toBufferedImage(image));
				AffineTransform at = new AffineTransform();
				Point point = locationMap.get(i);
				at.translate(point.x + offset, point.y + offset);
				partFigure.transform(at);
				figure.add(partFigure);
				figurePortIndex.put(i, partFigure);
			}
		}else{
			for(OLTPort portEntity : list){
				PartEdit partEdit = new PartEdit();
				PartFigure partFigure = partEdit.restoreFigure(portEntity);
				partFigure.setBounds(new Rectangle2D.Double(0, 0, image
						.getWidth(null), image.getHeight(null)));
				partFigure.setBufferedImage(Images.toBufferedImage(image));
				AffineTransform at = new AffineTransform();
				Point point = locationMap.get(portEntity.getProtNo());
				at.translate(point.x + offset, point.y + offset);
				partFigure.transform(at);
				figure.add(partFigure);
				figurePortIndex.put(portEntity.getProtNo(), partFigure);
			}
		}
	}

	@Override
	public Map<Integer, LightEdit> getDataSingalMap() {
		return dataSignalMap;
	}

	@Override
	public Map<Integer, PartFigure> getPartMap() {
		return figurePortIndex;
	}

	@Override
	public Map<Integer, LightEdit> getWorkSingalMap() {
		return workSignalMap;
	}

	@Override
	public Map<Integer, LightEdit> getDownDataSingalMap() {//下PON
		return downDataSignalMap;
	}

	@Override
	public Map<Integer, LightEdit> getUpDataSingalMap() {//上ETH
		return upDataSignalMap;
	}
}
