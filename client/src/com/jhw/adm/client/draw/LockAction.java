package com.jhw.adm.client.draw;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.jhotdraw.draw.DrawingView;

public class LockAction extends AbstractAction {
	
	public LockAction(DrawingView drawingView) {
		this.drawingView = drawingView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		drawingView.setEnabled(!drawingView.isEnabled());
		
		
		if (drawingView.isEnabled()) {
			putValue(Action.SMALL_ICON, getUnlockImageIcon());
		} else {
			putValue(Action.SMALL_ICON, getLockImageIcon());
		}
	}

	public ImageIcon getLockImageIcon() {
		return lockImageIcon;
	}

	public void setLockImageIcon(ImageIcon lockImageIcon) {
		this.lockImageIcon = lockImageIcon;
	}

	public ImageIcon getUnlockImageIcon() {
		return unlockImageIcon;
	}

	public void setUnlockImageIcon(ImageIcon unlockImageIcon) {
		this.unlockImageIcon = unlockImageIcon;
	}

	private ImageIcon lockImageIcon;
	private ImageIcon unlockImageIcon;
	private DrawingView drawingView;
	private static final long serialVersionUID = -1L;
}