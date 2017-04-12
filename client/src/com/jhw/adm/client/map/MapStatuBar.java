/**
MapStatubar.java
 *¹¦ÄÜ£º 
ÉÏÎç11:13:42
 *@authorAdministrator 
 *@company
 *@version
 */
package com.jhw.adm.client.map;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Administrator
 * 
 */
public class MapStatuBar extends JPanel {
	JLabel label;
	JLabel label_3;

	public MapStatuBar() {
		super();
		setLayout(new GridLayout(1, 0));

		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 0));
		add(panel);

		final JLabel xLabel = new JLabel();
		xLabel.setText("X:");
		panel.add(xLabel);

		label = new JLabel();
		label.setText("0.0");
		panel.add(label);

		final JLabel yLabel = new JLabel();
		yLabel.setText("Y:");
		panel.add(yLabel);

		label_3 = new JLabel();
		label_3.setText("0.0");
		panel.add(label_3);

		final JPanel panel_1 = new JPanel();
		add(panel_1);
	}

	public void setXY(String x, String y) {
		label.setText(x);
		label_3.setText(y);
	}

}
