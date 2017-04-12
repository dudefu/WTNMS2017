package com.jhw.adm.client.draw;

import org.jhotdraw.draw.AttributeKey;

public class NetworkKeys {
	/**
	 * true,不能拖拽
	 * false,可以拖拽
	 */
	public final static AttributeKey<Boolean> UNDRAGABLE = new AttributeKey<Boolean>(
			"undragable", Boolean.class, null, true, null);
}