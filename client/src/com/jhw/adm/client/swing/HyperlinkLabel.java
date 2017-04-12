package com.jhw.adm.client.swing;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.*;

import javax.swing.*;

public class HyperlinkLabel  extends JLabel implements   MouseListener   {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private   boolean   isEntered   =   false;   
	   
	public   HyperlinkLabel()   {   
		super();
	}
     public   HyperlinkLabel(String   str)   {   
         super(str);   
         this.addMouseListener(this);   
     } 
     public HyperlinkLabel(String text, int horizontalAlignment) {
    	 super(text,horizontalAlignment);
    	 this.addMouseListener(this);   
     }
   
     protected   void   paintBorder(Graphics   g)   {   
         int   w   =   this.getSize().width;   
         int   h   =   this.getSize().height;   
         if(isEntered){   
        	 g.drawLine(0,   h-1,   w-1,   h-1);   
         }   
     }

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		isEntered   =   true;   
		  this.repaint();   
		  this.setCursor(new   Cursor(Cursor.HAND_CURSOR));   
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		isEntered   =   false;   
		  this.repaint();   

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}   
   
       

}
