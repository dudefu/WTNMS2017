package com.jhw.adm.client.diagram;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.jhw.adm.client.core.Lockable;

public class LockAction extends AbstractAction {
	
	public LockAction(Lockable lockable) {
		this.lockable = lockable;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		

		lockable.setLocked(!lockable.isLocked());
		
		if (lockable.isLocked()) {
			putValue(Action.SMALL_ICON, getLockImageIcon());
		} else {
			putValue(Action.SMALL_ICON, getUnlockImageIcon());
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
	private final Lockable lockable;
	private static final long serialVersionUID = -1L;
}