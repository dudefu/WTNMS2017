package com.jhw.adm.client.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class ButtonTabComponent extends JPanel {
	private static final long serialVersionUID = -1L;
	private final JButton tabButton;

	public ButtonTabComponent(final JTabbedPane pane) {
		super(new BorderLayout(5, 0));
		if (pane == null) {
			throw new NullPointerException("TabbedPane is null");
		}
		setOpaque(false);
		JLabel label = new JLabel() {
			private static final long serialVersionUID = -1L;
			@Override
			public String getText() {
				int i = pane.indexOfTabComponent(ButtonTabComponent.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}
				return null;
			}
		};
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		tabButton = new TabButton();
		add(label, BorderLayout.CENTER);
		add(tabButton, BorderLayout.EAST);
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}
	
	public JButton getTabButton() {
		return tabButton;
	}

	private class TabButton extends JButton {
		public TabButton() {
			int size = 17;
			setPreferredSize(new Dimension(size, size));
			setToolTipText("πÿ±’±Í«©");
			setUI(new BasicButtonUI());
			setContentAreaFilled(false);
			setFocusable(false);
			setBorder(BorderFactory.createEtchedBorder());
			setBorderPainted(false);
			addMouseListener(buttonMouseListener);
			setRolloverEnabled(true);
		}

		@Override
		public void updateUI() {
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			Stroke stroke = g2.getStroke();
			if (!getModel().isPressed()) {
				g2.translate(-1, -1);
			}
			g2.setStroke(new BasicStroke(2));
			g.setColor(Color.BLACK);
			if (getModel().isRollover()) {
				g.setColor(Color.RED);
			}
			int delta = 6;
			g.drawLine(delta, delta, getWidth() - delta - 1, getHeight()
					- delta - 1);
			g.drawLine(getWidth() - delta - 1, delta, delta, getHeight()
					- delta - 1);
			if (!getModel().isPressed()) {
				g.translate(1, 1);
			}
			g2.setStroke(stroke);
		}
		private static final long serialVersionUID = -1L;
	}

	private final static MouseListener buttonMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};
}
