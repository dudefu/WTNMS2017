package com.jhw.adm.client.diagram;

import java.util.Map;

import org.jhotdraw.draw.Drawing;

import com.jhw.adm.client.draw.LightEdit;
import com.jhw.adm.client.draw.PartFigure;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

public interface EmluatorBuilder {
	void buildPorts(Drawing drawing, SwitchNodeEntity switchNodeEntity);
	void buildLights(Drawing drawing, SwitchNodeEntity switchNodeEntity);
	Map<Integer, PartFigure> getPartMap();
	Map<Integer, LightEdit> getDataSingalMap();
	Map<Integer, LightEdit> getWorkSingalMap();
}