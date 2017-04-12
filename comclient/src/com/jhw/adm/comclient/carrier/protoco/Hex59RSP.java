package com.jhw.adm.comclient.carrier.protoco;

import java.util.ArrayList;
import java.util.List;

public class Hex59RSP extends DataPacket {

	public Hex59RSP() {
		listOfRoute = new ArrayList<Route>();
	}

	public void addRoute(Route route) {
		listOfRoute.add(route);
	}

	public int getRouteVersion() {
		return routeVersion;
	}

	public void setRouteVersion(int routeVersion) {
		this.routeVersion = routeVersion;
	}

	public int getRouteCount() {
		return listOfRoute.size();
	}

	public List<Route> getRouters() {
		return listOfRoute;
	}

	private List<Route> listOfRoute;
	private int routeVersion;
}