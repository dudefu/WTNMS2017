package com.jhw.adm.comclient.carrier.protoco;

import java.util.ArrayList;
import java.util.List;

public class Hex3BREQ extends DataPacket {

	public Hex3BREQ() {
		listOfRoute = new ArrayList<Route>();
	}
	
	public void addRoute(Route route) {
		listOfRoute.add(route);
	}
	
	public int getRouterCount() {
		return listOfRoute.size();
	}

	public int getBodyLen() {
		return getRouterCount() * ROUTER_LEN + COUNT_LEN;
	}
	
	public List<Route> getRoutes() {
		return listOfRoute;
	}
	
	private List<Route> listOfRoute;
	
	public static final int ROUTER_LEN = 4;
	public static final int COUNT_LEN = 1;
}