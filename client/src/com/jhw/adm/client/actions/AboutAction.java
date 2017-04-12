package com.jhw.adm.client.actions;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.swing.ImagePanel;
import com.jhw.adm.client.util.ClientUtils;

@Component(AboutAction.ID)
public class AboutAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		AboutDialog aboutDialog = new AboutDialog();
		ClientUtils.centerDialog(aboutDialog);
		aboutDialog.setVisible(true);
	}

	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	private static final long serialVersionUID = -1L;

	public static final String ID = "aboutAction";

	public class AboutDialog extends JDialog {

		public AboutDialog() {
			super(ClientUtils.getRootFrame(), String.format("¹ØÓÚ%s", ClientUtils.getAppName()), true);
			setResizable(false);
			setLayout(new BorderLayout());
			Image image = imageRegistry.getImageIcon(ApplicationConstants.SPLASH)
					.getImage();
			ImagePanel drawing = new ImagePanel(image);
			setSize(image.getWidth(this) + 10, image.getHeight(this) + 30);
			add(drawing, BorderLayout.CENTER);
		}

		private static final long serialVersionUID = 1L;
	}
}