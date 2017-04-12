package com.jhw.adm.client.diagram;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.draw.NetworkDrawingView;
import com.jhw.adm.client.util.ClientUtils;

public class ExportImageAction extends AbstractAction {
	
	public ExportImageAction(NetworkDrawingView drawingView) {
		this.outputFormat = new PNGOutputFormat();
		this.drawingView = drawingView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (drawingView != null && outputFormat != null) {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.setFileFilter(outputFormat.getFileFilter());
			int returnVal = fileChooser.showSaveDialog(ClientUtils.getRootFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				try {
					outputFormat.export(selectedFile, drawingView);
					JOptionPane.showMessageDialog(ClientUtils.getRootFrame(),
							"导出图片成功", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException ex) {
					LOG.error("Export image error", ex);
					JOptionPane.showMessageDialog(ClientUtils.getRootFrame(),
							"导出图片失败", "温馨提示", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}
	}

	public void setImageIcon(ImageIcon imageIcon) {
		putValue(Action.SMALL_ICON, imageIcon);
	}

	private PNGOutputFormat outputFormat;
	private NetworkDrawingView drawingView;
	private static final Logger LOG = LoggerFactory.getLogger(ExportImageAction.class);
	private static final long serialVersionUID = -4572492205555282099L;
}