package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.util.ClientUtils;

/**
 * Ä£Ì¬µÄÊÓÍ¼ÈÝÆ÷
 */
@Component(ModalContainer.ID)
public class ModalContainer implements ViewContainer {
	
	@Override
	public void addView(ViewPart viewPart) {
		this.viewPart = viewPart;
	}
	
	private ViewPart viewPart;
	private ModalDialog dialog;
	private static final long serialVersionUID = 8597171039510211067L;
	private static final Logger LOG = LoggerFactory.getLogger(ModalContainer.class);
	public static final String ID = "modalContainer";

	@Override
	public void open() {
		if (viewPart == null) {
		} else {
			dialog = new ModalDialog(ClientUtils.getRootFrame(), viewPart.getTitle());
			dialog.setIconImage(ClientUtils.getRootFrame().getIconImage());
			dialog.setResizable(false);
			dialog.setLayout(new BorderLayout());
			dialog.add(viewPart, BorderLayout.CENTER);
			dialog.setSize(viewPart.getViewWidth(), viewPart.getViewHeight());
			final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					dialog.setTitle((String) evt.getNewValue());
				}
			};
			viewPart.addPropertyChangeListener(ViewPart.PROP_TITLE, propertyChangeListener);
			
			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					if (viewPart != null) {
						viewPart.removePropertyChangeListener(propertyChangeListener);
						viewPart.close();
						viewPart = null;
						LOG.info("dialog.closed...");
					}
				}
			});
			
			for (JButton closeButton : viewPart.getCloseButtons()) {
				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (viewPart != null) {
							viewPart.close();
							viewPart = null;
							LOG.info("dialog.closed...");
						}
						dialog.close();
						
					}									
				});
			}
			
			Dimension screenSize = dialog.getToolkit().getScreenSize();
			Dimension size = dialog.getSize();
			screenSize.height = screenSize.height / 2;
			screenSize.width = screenSize.width / 2;
			size.height = size.height / 2;
			size.width = size.width / 2;
			int y = screenSize.height - size.height;
			if (y > 180) {
				y -= 108;
			}
			int x = screenSize.width - size.width;
			dialog.setLocation(x, y);							
			dialog.setVisible(true);
		}
	}

	@Override
	public void close() {
//		if (dialog != null) {
//			dialog.close();
//		}
//		if (viewPart != null) {
//			viewPart.close();
//		}
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public int getPlacement() {
		return 0;
	}

	@Override
	public int getViewHeight() {
		return 0;
	}

	@Override
	public int getViewWidth() {
		return 0;
	}

	@Override
	public void setViewHeight(int viewHeight) {		
	}

	@Override
	public void setViewWidth(int viewWidth) {
	}


	@Override
	public String getViewTitle() {
		return StringUtils.EMPTY;
	}

	@Override
	public void setViewTitle(String title) {
	}

	@Override
	public boolean isModal() {
		return true;
	}
	
	private class ModalDialog extends JDialog {
		
		public ModalDialog(Frame owner, String title) {
			super(owner, title, true);
		}

		@Override
		protected void processWindowEvent(WindowEvent e) {
			super.processWindowEvent(e);

			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				close();
			}
		}
		
		public void close() {
			dispose();
		}
		
		private static final long serialVersionUID = -1309188815514639463L;
	}
}