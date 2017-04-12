package com.jhw.adm.client.draw;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jhotdraw.draw.GridConstrainer;
import org.jhotdraw.draw.TranslationDirection;

public class NetworkConstrainer extends GridConstrainer {
	
	@Override
	public Rectangle2D.Double translateRectangle(Rectangle2D.Double r, TranslationDirection dir) {
        double x = r.x;
        double y = r.y;

        constrainRectangle(r, dir);

        switch (dir) {
            case NORTH:
            case NORTH_WEST:
            case NORTH_EAST:
                if (y == r.y && r.y > 0) {
                    r.y -= height;
                }
                break;
            case SOUTH:
            case SOUTH_WEST:
            case SOUTH_EAST:
                if (y == r.y) {
                    r.y += height;
                }
                break;
        }
        switch (dir) {
            case WEST:
            case NORTH_WEST:
            case SOUTH_WEST:
                if (x == r.x && r.x > 0) {
                    r.x -= width;
                }
                break;
            case EAST:
            case NORTH_EAST:
            case SOUTH_EAST:
                if (x == r.x) {
                    r.x += width;
                }
                break;
        }

        return r;
    }

	@Override
    public Rectangle2D.Double constrainRectangle(Rectangle2D.Double r) {
        Point2D.Double p0 = constrainPoint(new Point2D.Double(r.x, r.y));
        Point2D.Double p1 = constrainPoint(new Point2D.Double(r.x + r.width, r.y + r.height));

        if (Math.abs(p0.x - r.x) < Math.abs(p1.x - r.x - r.width)) {
            r.x = p0.x;
        } else {
            r.x = p1.x - r.width;
        }
        if (Math.abs(p0.y - r.y) < Math.abs(p1.y - r.y - r.height)) {
            r.y = p0.y;
        } else {
            r.y = p1.y - r.height;
        }

        return r;
    }
	
	@Override
	protected Rectangle2D.Double constrainRectangle(Rectangle2D.Double r, TranslationDirection dir) {
        Point2D.Double p0 = new Point2D.Double(r.x, r.y);

        switch (dir) {
            case NORTH:
            case NORTH_WEST:
            case WEST:
                constrainPoint(p0, dir);
                break;
            case EAST:
            case NORTH_EAST:
                p0.x += r.width;
                constrainPoint(p0, dir);
                p0.x -= r.width;
                break;
            case SOUTH:
            case SOUTH_WEST:
                p0.y += r.height;
                constrainPoint(p0, dir);
                p0.y -= r.height;
                break;
            case SOUTH_EAST:
                p0.y += r.height;
                p0.x += r.width;
                constrainPoint(p0, dir);
                p0.y -= r.height;
                p0.x -= r.width;
                break;
        }

        r.x = p0.x;
        r.y = p0.y;

        return r;
    }
	
	@Override
    public Point2D.Double constrainPoint(Point2D.Double p) {
        p.x = Math.round(p.x / width) * width;
        p.y = Math.round(p.y / height) * height;
        
        p.x = p.x > 0 ? p.x : 0;
        p.y = p.y > 0 ? p.y : 0;
        
        return p;
    }
	
	@Override
    protected Point2D.Double constrainPoint(Point2D.Double p, TranslationDirection dir) {
        Point2D.Double p0 = constrainPoint((Point2D.Double) p.clone());

        switch (dir) {
            case NORTH:
            case NORTH_WEST:
            case NORTH_EAST:
                if (p0.y < p.y) {
                    p.y = p0.y;
                } else if (p0.y > p.y) {
                    p.y = p0.y - height;
                }
                break;
            case SOUTH:
            case SOUTH_WEST:
            case SOUTH_EAST:
                if (p0.y < p.y) {
                    p.y = p0.y + height;
                } else if (p0.y > p.y) {
                    p.y = p0.y;
                }
                break;
        }
        switch (dir) {
            case WEST:
            case NORTH_WEST:
            case SOUTH_WEST:
                if (p0.x < p.x) {
                    p.x = p0.x;
                } else if (p0.x > p.x) {
                    p.x = p0.x - width;
                }
                break;
            case EAST:
            case NORTH_EAST:
            case SOUTH_EAST:
                if (p0.x < p.x) {
                    p.x = p0.x + width;
                } else if (p0.x > p.x) {
                    p.x = p0.x;
                }
                break;
        }

        return p;
    }
	
	private static final long serialVersionUID = 1L;
	private double height = 5;
	private double width = 5;
}