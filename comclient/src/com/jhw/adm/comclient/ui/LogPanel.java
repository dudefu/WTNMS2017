package com.jhw.adm.comclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.spi.LoggingEvent;

public class LogPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField frontEndCodeFld = new JTextField();
	private JTextField frontEndIPFld = new JTextField();
	private JTextField gprsPortFld = new JTextField();
	private JTextField topoIPFld = new JTextField();
	private JTextField threeLayerFld = new JTextField();

	private JLabel codeStarLabel = new JLabel("*");
	private JLabel ipStarLabel = new JLabel("*");
	private JLabel portStarLabel = new JLabel("*");
	private JLabel topoStarLabel = new JLabel("*");
	private JLabel layerStarLabel = new JLabel("*");

	private JButton saveBtn = null;
	private JButton closeBtn = null;

	private static JTextArea textArea = new JTextArea();

	public JComponent init() {

		Font font = new Font("宋体", Font.PLAIN, 12);
		setLayout(new BorderLayout());
		// textArea = new JTextArea();
		textArea.setFont(font);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(textArea, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		add(scrollTablePanel, BorderLayout.CENTER);

		return this;
	}

	public static void addLog(LoggingEvent event) {

		// final String log = new Date(event.timeStamp) + " " + event.getLevel()
		// + " " + "[" + event.getLoggerName() + "] - "
		// + event.getMessage() + '\n';
		final String log = new Date(event.timeStamp) + " " + event.getLevel()
				+ " " + "[" + "com.wintoptec.adm.comclient.jms.MessageLog"
				+ "] - " + event.getMessage() + '\n';

		if (SwingUtilities.isEventDispatchThread()) {
			// textArea.insert(log, 0);
			textArea.append(log);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// textArea.insert(log, 0);
					textArea.append(log);
				}
			});
		}

		// int lineCount = textArea.getLineCount();
		// System.out
		// .println(textArea.getDocument().getLength() + " " + lineCount+"
		// "+textArea.getl);
		// if (lineCount >= 2) {
		//
		// try {
		// textArea.getDocument().remove(lineCount, 1);
		// } catch (BadLocationException e) {
		// e.printStackTrace();
		// }
		// }
		if (textArea.getLineCount() > 98) {
			try {
				// for (int j = 0; j < 1; j++) {
				String tmp = textArea.getText();
				int index = tmp.indexOf("\n");
				textArea.getDocument().remove(0, index + 1);
				// }
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @deprecated
	 * @param parent
	 */
	public void createContents(JPanel parent) {

		parent.setLayout(new BorderLayout());

		JPanel inforContainer = new JPanel(new BorderLayout());
		JPanel paramPanel = new JPanel(new GridBagLayout());

		inforContainer.setBorder(BorderFactory.createTitledBorder("参数配置"));

		frontEndCodeFld.setBackground(Color.WHITE);
		frontEndIPFld.setBackground(Color.WHITE);
		gprsPortFld.setBackground(Color.WHITE);
		topoIPFld.setBackground(Color.WHITE);
		threeLayerFld.setBackground(Color.WHITE);

		codeStarLabel.setForeground(Color.RED);
		ipStarLabel.setForeground(Color.RED);
		portStarLabel.setForeground(Color.RED);
		topoStarLabel.setForeground(Color.RED);
		layerStarLabel.setForeground(Color.RED);

		paramPanel.add(new JLabel("编号"), new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		paramPanel.add(frontEndCodeFld, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 3), 120, 0));
		paramPanel.add(codeStarLabel, new GridBagConstraints(2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 0), 0, 0));

		paramPanel.add(new JLabel("前置机IP"), new GridBagConstraints(0, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		paramPanel.add(frontEndIPFld, new GridBagConstraints(1, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 3), 120, 0));
		paramPanel.add(ipStarLabel, new GridBagConstraints(2, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 0), 0, 0));

		paramPanel.add(new JLabel("GPRS端口号"), new GridBagConstraints(0, 2, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		paramPanel.add(gprsPortFld, new GridBagConstraints(1, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 3), 120, 0));
		paramPanel.add(portStarLabel, new GridBagConstraints(2, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 0), 0, 0));

		paramPanel.add(new JLabel("拓扑IP"), new GridBagConstraints(0, 3, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		paramPanel.add(topoIPFld, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 3), 120, 0));
		paramPanel.add(topoStarLabel, new GridBagConstraints(2, 3, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 0), 0, 0));

		paramPanel.add(new JLabel("Three_Layer"), new GridBagConstraints(0, 4,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
		paramPanel.add(threeLayerFld, new GridBagConstraints(1, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 3), 120, 0));
		paramPanel.add(layerStarLabel, new GridBagConstraints(2, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 0), 0, 0));

		JPanel jPanel = new JPanel(new BorderLayout());
		jPanel.add(paramPanel, BorderLayout.WEST);
		inforContainer.add(jPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = new JButton("保存");
		closeBtn = new JButton("关闭");

		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		closeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		buttonPanel.add(saveBtn);
		buttonPanel.add(closeBtn);

		parent.add(inforContainer, BorderLayout.CENTER);
		parent.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void save() {

		if (!isValids()) {
			return;
		}

	}

	public void close() {

	}

	private boolean isValids() {

		boolean isValid = true;

		if (null == frontEndCodeFld.getText()
				|| "".equals(frontEndCodeFld.getText())) {
			JOptionPane.showMessageDialog(this, "前置机编号不能为空，请输入前置机编号", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == frontEndIPFld.getText()
				|| "".equals(frontEndIPFld.getText())) {
			JOptionPane.showMessageDialog(this, "前置机IP不能为空，请输入前置机IP", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == gprsPortFld.getText() || "".equals(gprsPortFld.getText())) {
			JOptionPane.showMessageDialog(this, "GPRS端口号不能为空，请输入GPRS端口号", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == topoIPFld.getText() || "".equals(topoIPFld.getText())) {
			JOptionPane.showMessageDialog(this, "拓扑IP不能为空，请输入拓扑IP", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == threeLayerFld.getText()
				|| "".equals(threeLayerFld.getText())) {
			JOptionPane.showMessageDialog(this,
					"THREE_LAYER_COMMUNITY不能为空，请输入THREE_LAYER_COMMUNITY", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}
}
