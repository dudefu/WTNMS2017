package com.jhw.adm.client.draw;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LightEdit implements NodeEdit<LightFigure> {
	
	public LightEdit(int category, double width, double height) {
		this.category = category;
		this.width = width;
		this.height = height;
		executorService = Executors.newSingleThreadExecutor();
	}

	@Override
	public LightFigure createFigure() {
		return null;
	}

	@Override
	public Object createModel() {
		return null;
	}

	@Override
	public LightFigure getFigure() {
		return figure;
	}

	@Override
	public Object getModel() {
		return model;
	}

	@Override
	public LightFigure restoreFigure(Object model) {
		
		if (category == RECTANGLE_LIGHT) {
			figure = new RectangleLightFigure(width, height);
		}
		if (category == ELLIPSE_LIGHT) {
			
			figure = new EllipseLightFigure(width, height);
		}
		figure.changeColor(Color.GREEN);
		setFigure(figure);
		setModel(model);
		figure.setEdit(this);
		return figure;
	}

	@Override
	public void updateAttributes() {
	}

	@Override
	public void updateModel() {
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void setFigure(LightFigure figure) {
		this.figure = figure;
	}
	
	public void changeColor(Color color) {
		figure.willChange();
		figure.changeColor(color);
		figure.changed();
	}
	
	/**
	 * …¡À∏6√Î
	 */
	public void flash() {
		Repeater repeater = new Repeater();
		executorService.execute(repeater);
	}

	private void repeat() {
		figure.willChange();
		turnOn = !turnOn;
		if (turnOn) {
			figure.turnOn();
		} else {
			figure.turnOff();
		}
		figure.changed();
	}
	
	/**
	 * ø™µ∆
	 */
	public void turnOn() {
		figure.willChange();
		figure.turnOn();
		figure.changed();
	}
	
	/**
	 * πÿµ∆
	 */
	public void turnOff() {
		figure.willChange();
		figure.turnOff();
		figure.changed();
	}
	
	private final ExecutorService executorService;
	private boolean turnOn;
	private final int category;
	private final double width;
	private final double height;
	private Object model;
	private LightFigure figure;
	public static final int RECTANGLE_LIGHT = 1;
	public static final int ELLIPSE_LIGHT = 2;
	
	private class Repeater extends Thread {
		
		public Repeater() {
		}
		
		@Override
		public void run() {
			try {
				while (true) {
//				while (second < max) {
//					second++;
					repeat();
					Thread.sleep(interval);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		private static final int max = 20;
		private static final int interval = 300;
		private int second;
	}
}