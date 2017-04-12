package com.jhw.adm.client.draw;

import static org.jhotdraw.draw.AttributeKeys.COMPOSITE_ALIGNMENT;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TEXT_ALIGNMENT;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.AttributeKeys.Alignment;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.layouter.HorizontalLayouter;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.locator.RelativeLocator;

public class EquipmentFigure extends GraphicalCompositeFigure implements NodeFigure {

	public EquipmentFigure(BufferedImage equipmentImage) {
		this();
		setBufferedImage(equipmentImage);
	}
	
	public EquipmentFigure(int x, int y, BufferedImage equipmentImage) {
		this();
		setBufferedImage(equipmentImage);
	}
	
    public EquipmentFigure() {

        setLayouter(new VerticalLayouter());
        set(COMPOSITE_ALIGNMENT, Alignment.CENTER);
        
        final Figure presentationFigure = new NullRectangleFigure();
        presentationFigure.set(STROKE_COLOR, null);
        setPresentationFigure(presentationFigure);

        textFigure = new TextFigure(DEF_TEXT);
        textFigure.setEditable(false);
        textFigure.set(AttributeKeys.FONT_FACE, TEXT_FONT);
        textFigure.set(STROKE_COLOR, null);
        textFigure.set(FILL_COLOR, null);
        textFigure.set(TEXT_ALIGNMENT, Alignment.CENTER);

        imageFigure = new ImageFigure();
        imageFigure.set(STROKE_COLOR, null);
        imageFigure.set(FILL_COLOR, null);
        
        GraphicalCompositeFigure alarmComposite = new GraphicalCompositeFigure();
        alarmComposite.setLayouter(new HorizontalLayouter());
        alarmComposite.set(COMPOSITE_ALIGNMENT, Alignment.TRAILING);

        statusFigure = new NullableImageFigure();
        statusFigure.set(STROKE_COLOR, null);
        statusFigure.set(FILL_COLOR, null);

        alarmComposite.add(imageFigure);
        alarmComposite.add(statusFigure);
        alarmComposite.layout();
        //
        add(alarmComposite);
        add(textFigure);
    }
    
    public void setBufferedImage(BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            imageFigure.setBounds(new Point2D.Double(0, 0),
                    new Point2D.Double(bufferedImage.getWidth(),
                    bufferedImage.getHeight()));
        }
        imageFigure.setBufferedImage(bufferedImage);
    }
    
    public void setStatusImage(BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            statusFigure.setBounds(new Point2D.Double(0, 0),
                    new Point2D.Double(bufferedImage.getWidth(),
                    bufferedImage.getHeight()));
        }
        statusFigure.setBufferedImage(bufferedImage);
    }
    
    public void setText(String text) {
        textFigure.setText(text);
    }
    
    private final TextFigure textFigure;
    private final ImageFigure imageFigure;
    private final ImageFigure statusFigure;

	private static final Font TEXT_FONT = new Font("宋体", Font.PLAIN, 12);
    
	private Action doubleClickAction;
	private LinkedList<Connector> connectors;
	private List<Action> popupActions;
	private EquipmentEdit nodeEdit;
	
	private static final long serialVersionUID = -4342334460258204611L;
	
	private void EquipmentChanged(FigureEvent e) {
		if (getEdit() == null) {
		} else {
			getEdit().updateModel();
		}
	}

	private final FigureListener figureListener = new FigureAdapter() {
		@Override
		public void figureChanged(FigureEvent e) {
			EquipmentChanged(e);
		}
	};
    
    private static final String DEF_TEXT = "NULL";

	@Override
	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	@Override
	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	@Override
	public EquipmentEdit getEdit() {
		return nodeEdit;
	}

	public void setEdit(EquipmentEdit edit) {
		this.nodeEdit = edit;
		addFigureListener(figureListener);
	}
	//
  
	@Override
	public Collection<Connector> getConnectors(ConnectionFigure prototype) {
		connectors = new LinkedList<Connector>();
		connectors.add(new LocatorConnector(this, RelativeLocator.center()));
		return Collections.unmodifiableList(connectors);
	}

	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		List<Handle> handles = new LinkedList<Handle>();
		switch (detailLevel) {
			case -1:
				BoundsOutlineHandle boundsOutlineHandle = new BoundsOutlineHandle(this, false, true);
				handles.add(boundsOutlineHandle);            	
				break;
			case 0:
				ConstrainedMoveHandle.addMoveHandles(this, handles);
				break;
		}
		return handles;
	}
  
	@Override
	public boolean handleMouseClick(Point2D.Double p, MouseEvent evt,
			DrawingView view) {
		Action action = getDoubleClickAction();
		String DefaulActionCommand = "DoubleClick";
		if (action == null) {
			return false;
		}

		Object actionCommandValue = action.getValue(Action.ACTION_COMMAND_KEY);
		String actionCommand = actionCommandValue == null ? DefaulActionCommand
				: actionCommandValue.toString();
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				actionCommand, EventQueue.getMostRecentEventTime(), evt
						.getModifiersEx());
		action.actionPerformed(event);
		return true;
	}
	
	/**
	 * 图形弹出菜单的Action集合
	 */
	@Override
	public Collection<Action> getActions(Point2D.Double p) {
		return Collections.unmodifiableList(popupActions);
	}
	
	@Override
	public void setActions(List<Action> actions) {
		popupActions = actions;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (nodeEdit == null) {
			return null;
		} else {
			return nodeEdit.getAdapter(adapterClass);
		}
	}	

    
    private class NullableImageFigure extends ImageFigure {

		private static final long serialVersionUID = -8764734432618730610L;

		@Override
        protected void drawImage(Graphics2D g) {
            BufferedImage image = getBufferedImage();
            Rectangle2D.Double rectangle = getBounds();
            if (image != null) {
                g.drawImage(image, (int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height, null);
            }
        }
    }
    
    private class NullRectangleFigure extends RectangleFigure {

		private static final long serialVersionUID = 7084456400308249647L;

		@Override
        protected void drawFill(Graphics2D g) {
        }

        @Override
        protected void drawStroke(Graphics2D g) {
        }
    }
}
