package com.jhw.adm.client.draw;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.liner.Liner;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

/**
 * ֱ�߻�����
 */
public class LinearLiner implements Liner {

	private static final long serialVersionUID = 1L;
	private double shoulderSize;

    public LinearLiner() {
        this(20);
    }

    public LinearLiner(double slantSize) {
        this.shoulderSize = slantSize;
    }

    public Collection<Handle> createHandles(BezierPath path) {
        return null;
    }

    public void lineout(ConnectionFigure figure) {
        BezierPath path = ((LineConnectionFigure) figure).getBezierPath();
        Connector start = figure.getStartConnector();
        Connector end = figure.getEndConnector();
        if (start == null || end == null || path == null) {
            return;
        }

        // Special treatment if the connection connects the same figure
        if (figure.getStartFigure() == figure.getEndFigure()) {
            // Ensure path has exactly 4 nodes
            while (path.size() < 4) {
                path.add(1, new BezierPath.Node(0, 0));
            }
            while (path.size() > 4) {
                path.remove(1);
            }
            Point2D.Double sp = start.findStart(figure);
            Point2D.Double ep = end.findEnd(figure);
            Rectangle2D.Double sb = start.getBounds();
            Rectangle2D.Double eb = end.getBounds();
            int soutcode = sb.outcode(sp);
            if (soutcode == 0) {
                soutcode = Geom.outcode(sb, eb);
            }
            int eoutcode = eb.outcode(ep);
            if (eoutcode == 0) {
                eoutcode = Geom.outcode(sb, eb);
            }

            path.get(0).moveTo(sp);
            path.get(path.size() - 1).moveTo(ep);


            switch (soutcode) {
                case Geom.OUT_TOP:
                    eoutcode = Geom.OUT_LEFT;
                    break;
                case Geom.OUT_RIGHT:
                    eoutcode = Geom.OUT_TOP;
                    break;
                case Geom.OUT_BOTTOM:
                    eoutcode = Geom.OUT_RIGHT;
                    break;
                case Geom.OUT_LEFT:
                    eoutcode = Geom.OUT_BOTTOM;
                    break;
                default:
                    eoutcode = Geom.OUT_TOP;
                    soutcode = Geom.OUT_RIGHT;
                    break;
            }
            //path.get(0).moveTo(sp.x + shoulderSize, sp.y);
            path.get(0).mask = BezierPath.C2_MASK;
            if ((soutcode & Geom.OUT_RIGHT) != 0) {
                path.get(0).x[2] = sp.x + shoulderSize;
                path.get(0).y[2] = sp.y;
            } else if ((soutcode & Geom.OUT_LEFT) != 0) {
                path.get(0).x[2] = sp.x - shoulderSize;
                path.get(0).y[2] = sp.y;
            } else if ((soutcode & Geom.OUT_BOTTOM) != 0) {
                path.get(0).x[2] = sp.x;
                path.get(0).y[2] = sp.y + shoulderSize;
            } else {
                path.get(0).x[2] = sp.x;
                path.get(0).y[2] = sp.y - shoulderSize;
            }
            path.get(1).mask = BezierPath.C2_MASK;
            path.get(1).moveTo(sp.x+shoulderSize, (sp.y+ep.y)/2);
                path.get(1).x[2] = sp.x + shoulderSize;
                path.get(1).y[2] = ep.y - shoulderSize;
            path.get(2).mask = BezierPath.C1_MASK;
            path.get(2).moveTo((sp.x+ep.x)/2, ep.y-shoulderSize);
                path.get(2).x[1] = sp.x + shoulderSize;
                path.get(2).y[1] = ep.y - shoulderSize;
            path.get(3).mask = BezierPath.C1_MASK;
            if ((eoutcode & Geom.OUT_RIGHT) != 0) {
                path.get(3).x[1] = ep.x + shoulderSize;
                path.get(3).y[1] = ep.y;
            } else if ((eoutcode & Geom.OUT_LEFT) != 0) {
                path.get(3).x[1] = ep.x - shoulderSize;
                path.get(3).y[1] = ep.y;
            } else if ((eoutcode & Geom.OUT_BOTTOM) != 0) {
                path.get(3).x[1] = ep.x;
                path.get(3).y[1] = ep.y + shoulderSize;
            } else {
                path.get(3).x[1] = ep.x;
                path.get(3).y[1] = ep.y - shoulderSize;
            }
        } else {
            Point2D.Double sp = start.findStart(figure);
            Point2D.Double ep = end.findEnd(figure);

            path.clear();
        	// һ��ֱ��
            path.add(new BezierPath.Node(sp.x, sp.y));
            path.add(new BezierPath.Node(ep.x, ep.y));
        }

        path.invalidatePath();
    }

    public void read(DOMInput in) {
    }

    public void write(DOMOutput out) {
    }

    public Liner clone() {
        try {
            return (Liner) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        }
    }
}