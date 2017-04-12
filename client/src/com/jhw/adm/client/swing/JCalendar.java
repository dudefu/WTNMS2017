package com.jhw.adm.client.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jhw.adm.client.views.SpringUtilities;

public class JCalendar extends JPanel{
	private JTextField field = null;
	private CalendarPanel datePnl = null;
	private JPanel container = null;
	private JDialog dateDlg = null;
	private boolean isShow = false;
	private JDialog dlg = new JDialog();
	private JCalendar jCalendar = null;
	
	public JCalendar(){
		JButton btn = new JButton();
		
		jCalendar = this;
		
		ImageIcon icon = new ImageIcon("D:/workspace/ADM2010/res/images/BD14565_.GIF");
		btn.setIcon(icon);
		btn.setPreferredSize(new Dimension(20,30));
		
		field = new JTextField();
		field.setPreferredSize(new Dimension(130, 30));
		field.setMinimumSize(new Dimension(130, 30));
		field.setMaximumSize(new Dimension(130, 30));
//		field.setEditable(false);
		
		datePnl = new CalendarPanel();
		dateDlg = new JDialog();
		dateDlg.getContentPane().add(datePnl);
		dateDlg.setUndecorated(true);
		dateDlg.pack();
		
		
		datePnl.addDateChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                Date selectedDate = (Date)e.getSource();
                SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
                field.setText(f.format(selectedDate));
            }
        });
		container = new JPanel();
//		container.setLayout(new SpringLayout());
//		container.add(field);
//		container.add(btn);
//		SpringUtilities.makeCompactGrid(container, 1, 2, 0, 0, 0, 0);
		
		container.setLayout(new GridBagLayout());
		container.add(btn,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,-20,0,0),0,0));
		container.add(field,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		
		
		this.setLayout(new BorderLayout());
		this.add(container);
		
		dateDlg.setVisible(false);
		isShow = false;
		
		
//		dlg.getContentPane().add(this);
//		dlg.setUndecorated(true);
//	    dlg.pack();
//	    dlg.setLocation(200, 300);
//		
//		dlg.setVisible(true);
		
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (isShow){
					dateDlg.setVisible(false);
					isShow = false;
				}
				else{
					int a = jCalendar.getX();
					int ba = jCalendar.getY();
					
					dateDlg.setLocation((int)jCalendar.getLocation().getX(), (int)jCalendar.getLocation().getY()+30);
					dateDlg.setVisible(true);
					isShow = true;
				}
				
			}
		});
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run(){
				container.updateUI();
			}
		});
	}
	
	public String getDateText(){
		return field.getText().trim();
	}
	
	public static void main(String[] ar){
		new JCalendar();
	}

}
