package com.jhw.adm.client.views;

import java.util.EventListener;

public interface PartListener extends EventListener {
	void partOpened(PartEvent event);
	void partClosed(PartEvent event);
}