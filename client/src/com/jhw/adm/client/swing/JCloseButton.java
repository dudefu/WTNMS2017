package com.jhw.adm.client.swing;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * 关闭按钮，可以主动触发事件 - fireActionEvent()。
 */
public class JCloseButton extends JButton {
	
	public JCloseButton() {
        this(null, null);
    }
	
	public JCloseButton(String text, Icon icon) {
		super(text, icon);
	}
	
	public void fireActionEvent() {
		ActionEvent event = new ActionEvent(JCloseButton.this,
				ActionEvent.ACTION_PERFORMED, "ACTION_PERFORMED");
		super.fireActionPerformed(event);
	}

	private static final long serialVersionUID = -1627825044222786854L;
}
