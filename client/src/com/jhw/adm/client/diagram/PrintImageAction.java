package com.jhw.adm.client.diagram;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.jhotdraw.draw.Figure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.draw.NetworkDrawingView;

public class PrintImageAction extends AbstractAction {

	public PrintImageAction(NetworkDrawingView drawingView) {
		this.drawingView = drawingView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		Paper paper = new Paper();
		//A4 
//		paper.setSize(594.936, 841.536);
		paper.setSize(595, 842);
        paper.setImageableArea(0, 0, 595, 842);
        //打印模式为纵打的话，x=595,y=842
		
		PageFormat pageFormat = new PageFormat();
		//横打 or 纵打
//		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setOrientation(PageFormat.LANDSCAPE);
		pageFormat.setPaper(paper);
		//重新设置打印方向
//		pageFormat = printerJob.pageDialog(pageFormat);

        Book book = new Book();
        Printable printableView = new Printable() {
            @Override  
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            	if (pageIndex > 0) {   
                    return Printable.NO_SUCH_PAGE;   
                } else { 
                	Graphics2D g2d = (Graphics2D) graphics; 
                	g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());   
                    double d = pageFormat.getImageableWidth() / drawingView.getWidth();   
                    g2d.scale(d, d);
	            	drawingView.fillBackground(g2d);
	            	List<Figure> figures = drawingView.getDrawing().getChildren();
	                for (Figure f : figures) {
	                    f.draw(g2d);
	                }
	                return Printable.PAGE_EXISTS;
                }
            }   
        };   
        book.append(printableView, pageFormat);
        printerJob.setPageable(book);   

        PrintRequestAttributeSet printAttrSet = new HashPrintRequestAttributeSet();   
        printAttrSet.add(MediaSizeName.ISO_A4);   
        printAttrSet.add(OrientationRequested.LANDSCAPE);   
        
        boolean doPrint = printerJob.printDialog(printAttrSet);
//        boolean doPrint = printerJob.printDialog();

        if (doPrint) {   
            try {   
            	printerJob.print();   
            } catch (PrinterException ex) {
            	LOG.error("Print diagram error", ex);
            }   
        }
	}

	public void setImageIcon(ImageIcon imageIcon) {
		putValue(Action.SMALL_ICON, imageIcon);
	}


	private NetworkDrawingView drawingView;
	private static final Logger LOG = LoggerFactory
			.getLogger(PrintImageAction.class);
	private static final long serialVersionUID = -1941542292801300438L;
}