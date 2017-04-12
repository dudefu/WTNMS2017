package com.jhw.adm.client.swing;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

/*
 * Õº–Œ√Ê∞Â
 */
public class ImagePanel extends JPanel {

	public ImagePanel(Image image) {
		this.image = image;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		java.awt.Composite oldComposite = g2d.getComposite();
		int compositeRule = AlphaComposite.SRC_OVER;
		AlphaComposite alphaComposite = AlphaComposite.getInstance(compositeRule, 0.85f);
		g2d.setComposite(alphaComposite);
		g2d.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null),
				0, 0, image.getWidth(null), image.getHeight(null), this);
		g2d.setComposite(oldComposite);
	}
	
	private Image image;
	
	private static final long serialVersionUID = 1L;
}