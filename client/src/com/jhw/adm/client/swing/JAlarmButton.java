package com.jhw.adm.client.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public final class JAlarmButton extends JComponent implements MouseInputListener {

	private static final long serialVersionUID = -3161569034974080790L;

	/**
     * ͨ��״̬�µı���ͼƬ
     */
    private Icon backgroundImage;
    
    /**
     * ͨ��״̬�µ���ѳߴ�
     */
    private Dimension preferredSize;
    
    /**
     * ��������Ϸ�ʱ�ı���ͼƬ
     */
    private Icon rolloverBackgroundImage;
    
    /**
     * ��������Ϸ�ʱ����ѳߴ�
     */
    private Dimension rolloverPreferredSize;
    
    /**
     * ��ť������ʱ�ı���ͼƬ
     */
    private Icon pressedBackgroundImage;
    
    /**
     * ��ť������ʱ����ѳߴ�
     */
    private Dimension pressedPreferredSize;
    
    /**
     * ��ť����ֹʱ�ı���ͼƬ
     */
    private Icon disabledBackgroundImage;
    
    /**
     * ��ť����ֹʱ����ѳߴ�
     */
    private Dimension disabledPreferredSize;
    
    /**
     * ��ǰ��ť����ѳߴ�
     */
    private volatile Dimension currentSize;
    
    /**
     * ͨ��״̬�°�ť��ͼ��
     */
    private Icon icon;
    
    /**
     * ��ť����ֹʱ��ͼ��
     */
    private Icon disabledIcon;
    
    /**
     * ��ťͼ����ֵ�λ��
     */
    private IconOrientation iconOrientation = IconOrientation.WEST;
    
    /**
     * ��ť��Ĭ�ϳߴ�
     */
    private static final Dimension DEFAULT_SIZE = new Dimension(100, 25);
    
    /**
     * ˮƽƫ����
     */
    private int horizontalOffset = DEFAULT_HORIZONTAL_OFFSET;
    /**
     * Ĭ�ϵ�ˮƽƫ����
     */
    private static final int DEFAULT_HORIZONTAL_OFFSET = 2;
    
    /**
     * ��ֱƫ����
     */
    private int verticalOffset = DEFALUT_VERTICAL_OFFSET;
    
    /**
     * Ĭ�ϵĴ�ֱƫ����
     */
    private static final int DEFALUT_VERTICAL_OFFSET = 1;
    
    /**
     * ��ʾ�ڰ�ť�ϵ�����
     */
    private String text;
    
    /**
     * ��ť�ı�������
     */
    private Font font;
    
    /**
     * ��ť�ı���Ĭ������
     */
    private static final Font DEFAULT_FONT = new Font("����", Font.PLAIN, 12);
    
    /**
     * ��ť����ֹʱ�����ֵ���ɫ
     */
    private Color disabledForeground;
    
    /**
     * Ĭ�ϵİ�ť����ֹʱ�ı�����ɫ
     */
    private final Color DEFAULT_DISABLED_FOREGROUND = new Color(192, 192, 192);
    
    /**
     * ��ť��״̬
     */
    private volatile Status status = Status.DEFAULT;
    
    /**
     * ��ť����ɫ͸����
     */
    private volatile float alpha = 1.0f;
    
    private static HashMap< RenderingHints.Key, Object> renderingHints;
    
    /**
     * ��ťִ�еĶ���
     */
    private Action action;
    
    static {
        renderingHints = new HashMap();
//        renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
//                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        renderingHints.put(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//        renderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
//                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//        renderingHints.put(RenderingHints.KEY_DITHERING,
//                RenderingHints.VALUE_DITHER_DISABLE);
//        renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS,
//                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC );
//        renderingHints.put(RenderingHints.KEY_STROKE_CONTROL,
//                RenderingHints.VALUE_STROKE_PURE);
//        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
//                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ////
        renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        renderingHints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        renderingHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        renderingHints.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
	/** ����һ��ImageButton��ť��ʵ�� */
    public JAlarmButton() {
        this(Color.BLACK);
    }
    
    public JAlarmButton(Color defaultColor){
    	setDefault_color(defaultColor);
    	currentSize = DEFAULT_SIZE;
    	
//    	try {
//			UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel ");
//			SwingUtilities.updateComponentTreeUI(this); 
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		} 

        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    /**
     * ���ó���״̬�°�ť�ı��������ø÷�����Ӱ�쵽��ť�ڳ���״̬�µ���ѳߴ磬
     * �ɴ����ͼ�������Ӧ��ͼ��ߴ����
     * @param backgroundImage ����״̬�±���ͼ�����
     * @throws java.lang.IllegalArgumentException �������Ĳ���Ϊnull
     */
    public void setBackgroundImage(Icon backgroundImage) throws IllegalArgumentException {
        if (backgroundImage == null) {
            throw new IllegalArgumentException("backgroundImage can't be null");
        }
        this.backgroundImage = backgroundImage;
        preferredSize = new Dimension(backgroundImage.getIconWidth(),
                backgroundImage.getIconHeight());
        currentSize = preferredSize;
    }
    
    /**
     * ���ñ�����״̬�°�ť�ı��������ø÷�����Ӱ�쵽��ť�ڽ���״̬�µ���ѳߴ磬
     * �ɴ����ͼ�������Ӧ��ͼ��ߴ����
     * @param disabledBackgroundImage ����״̬�±���ͼ�����
     * @throws java.lang.IllegalArgumentException �������Ĳ���Ϊnull
     */
    public void setDisabledBackgroundImage(Icon disabledBackgroundImage) throws IllegalArgumentException {
        if (disabledBackgroundImage == null) {
            throw new IllegalArgumentException(
                    "disabledBackgroundImage can't be null");
        }
        this.disabledBackgroundImage = disabledBackgroundImage;
        disabledPreferredSize = new Dimension(disabledBackgroundImage
                .getIconWidth(), disabledBackgroundImage.getIconHeight());
    }
    
    /**
     * �����ڱ�����״̬ʱ��ť�ı��������ø÷�����Ӱ�쵽��ť�ڱ�����״̬ʱ����ѳߴ磬
     * �ɴ����ͼ�������Ӧ��ͼ��ߴ����
     * @param pressedBackgroundImage ������ʱ�ı���ͼ�����
     * @throws java.lang.IllegalArgumentException �������Ĳ���Ϊnull
     */
    public void setPressedBackgroundImage(Icon pressedBackgroundImage)throws IllegalArgumentException {
        if (pressedBackgroundImage == null) {
            throw new IllegalArgumentException(
                    "pressedBackgroundImage can't be null");
        }
        this.pressedBackgroundImage = pressedBackgroundImage;
        pressedPreferredSize = new Dimension(pressedBackgroundImage
                .getIconWidth(), pressedBackgroundImage.getIconHeight());
    }
    /**
     * �������ָ�������Ϸ�ʱ��ť�ı��������ø÷�����Ӱ�쵽��ť�����ָ�������Ϸ�
     * ʱ����ѳߴ磬�ɴ����ͼ�������Ӧ��ͼ��ߴ����
     * @param rolloverBackgroundImage ���ָ�������Ϸ�ʱ�ı���ͼ�����
     * @throws java.lang.IllegalArgumentException �������Ĳ���Ϊnull
     */
    public void setRolloverBackgroundImage(Icon rolloverBackgroundImage) throws IllegalArgumentException {
        if (rolloverBackgroundImage == null) {
            throw new IllegalArgumentException(
                    "rolloverBackgroundImage can't be null");
        }
        this.rolloverBackgroundImage = rolloverBackgroundImage;
        rolloverPreferredSize = new Dimension(rolloverBackgroundImage
                .getIconWidth(), rolloverBackgroundImage.getIconHeight());
    }
    
    /**
     * ����ˮƽƫ������
     * @param horizontalOffset ˮƽƫ����
     * @throws java.lang.IllegalArgumentException �������С��0���׳����쳣
     */
    public void setHorizontalOffset(int horizontalOffset) throws IllegalArgumentException{
        if(horizontalOffset < 0) {
            throw new IllegalArgumentException("horizontalOffset must >=0");
        }
        this.horizontalOffset = horizontalOffset;
    }
    
    /**
     * ���ô�ֱƫ����
     * @param verticalOffset ��ֱƫ����
     * @throws java.lang.IllegalArgumentException �������С��0���׳����쳣
     */
    public void setVerticalOffset(int verticalOffset) throws IllegalArgumentException{
        if(verticalOffset < 0) {
            throw new IllegalArgumentException("verticalOffset must >=0");
        }
        this.verticalOffset = verticalOffset;
    }
    
    /**
     * ���ð�ť��ͼ��
     * @param icon ͼ�����
     * @throws java.lang.IllegalArgumentException �������Ĳ���Ϊnull���׳����쳣
     */
    public synchronized void setIcon(Icon icon) throws IllegalArgumentException {
        if(icon == null) {
            throw new IllegalArgumentException(
                    "icon can't be null");
        }
        this.icon = icon;
    }
    
    /**
     * ���ð�ť�ڱ�����ʱ��ʾ��ͼ��
     * @param disabledIcon ͼ�����
     * @throws java.lang.IllegalArgumentException �������Ĳ���Ϊnull���׳����쳣
     */
    public synchronized void setDisabledIcon(Icon disabledIcon) throws IllegalArgumentException {
        if(disabledIcon == null) {
            throw new IllegalArgumentException(
                    "disabledIcon can't be null");
        }
        this.disabledIcon = disabledIcon;
    }
    
    public void setIconOrientation(IconOrientation iconOrientation) throws IllegalArgumentException{
        if(iconOrientation == null) {
            throw new IllegalArgumentException(
                    "iconOrientation can't be null");
        }
        this.iconOrientation = iconOrientation;
    }
    
    public void setDisabledForeground(Color disabledForeground) {
        this.disabledForeground = disabledForeground;
    }
    
    /**
     * ���ð�ť��͸����
     * @param alpha ͸���ȡ���Χ��0.0f~1.0f֮�䡣0.0fΪ��ȫ͸����1.0fΪ��ȫ��ʾ
     * @throws java.lang.IllegalArgumentException �������0.0f~1.0f֮����׳����쳣
     */
    public synchronized void setAlpha(float alpha)throws IllegalArgumentException{
        if (alpha < 0f || alpha > 1.0f) {
            throw new IllegalArgumentException(
                    "alpha value must between 0.0 and 1.0");
        }
        this.alpha = alpha;
        repaint();
    }
    
    /**
     * ���ð�ť��ʾ���ı�
     * @param text ��ʾ���ı��ַ���
     */
    public synchronized  void setText(String text) {
        if (text != null) {
            this.text = text;
            repaint();
        }
    }
    
    /**
     * ���ذ�ť�ĵ�ǰ���壬���֮ǰû���������壬�򷵻�Ĭ������
     * @return ��ť�ĵ�ǰ����
     */
    @Override
	public Font getFont() {
        if(font == null) {
            return DEFAULT_FONT;
        }
        return font;
    }
    
    /**
     * ���ð�ť�ĵ�ǰ����
     * @param font ����ʵ��
     */
    @Override
	public void setFont(Font font) {
        this.font = font;
        super.setFont(font);
    }
    /**
     * ָ�������ť�Ķ���
     * @param action ��ť�Ķ���
     */
    public void setAction(Action action) {
        this.action = action;
    }
    
    /**
     * ����JComponent.setEnabled(boolean enabled)�������Ƿ�ť���û򱻽�ֹ
     * @param enabled �����ť������Ϊtrue������Ϊfalse
     * @see java.awt.Component#isEnabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (isEnabled() != enabled) {
            if (disabledPreferredSize != null
                    && !disabledPreferredSize.equals(currentSize)) {
                currentSize = disabledPreferredSize;
                revalidate();
            }
        }
    }
    
    /**
     * ���ư�ť�߿�
     * @param g ͼ��������
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	@Override
    protected void paintBorder(Graphics g) {
        
    }
    /**
     * ���������
     * @param g ͼ��������
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	@Override
    protected void paintChildren(Graphics g) {
        
    }
    /**
     * ���ư�ť
     * @param g ͼ��������
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	@Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.addRenderingHints(renderingHints);
        if(alpha < 1.0f) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    alpha));
        }
        drawBorder(g);
        drawBackgroundImage(g2d);
        drawIcon(g2d);
        drawFill(g2d, this.iconWidth, this.iconHeight, this.offsetX, this.offsetY);
        drawText(g2d);
        g2d.dispose();
    }
    
    /**
     * ʵ�����λ�ڰ�ť�Ϸ��Լ�����ťʱ��ťЧ���Ļ���
     */
    private void drawBorder(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g.create();
    	RoundRectangle2D roundRectangle2D = new RoundRectangle2D.Float();
    	if(status == Status.ROLLOVER){//���λ�ڰ�ť�Ϸ�
    		roundRectangle2D.setRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
    		
    		Color background = this.getBackground();
    		g2d.setColor(background);
    		g2d.fill(roundRectangle2D);
    		g2d.setColor(background);
    		g2d.draw(roundRectangle2D);
    	}
    	if(status == Status.PRESSED){//press
    		roundRectangle2D.setRoundRect(1, 1, getWidth() - 1, getHeight() - 1, 5, 5);
    		Color background = this.getBackground();
//    		int red = background.getRed();
//    		int green = background.getGreen();
//    		int blue = background.getBlue();
//    		System.out.println("red=" + red + " green=" + green + " blue=" + blue);
			Color newBackground = new Color(background.getRed() - 20, background
					.getGreen() - 20, background.getBlue() - 20);
    		g2d.setColor(newBackground);
    		g2d.fill(roundRectangle2D);
    		g2d.setColor(newBackground);
    		g2d.draw(roundRectangle2D);
    	}
	}
    
    /*
     * For fill Rectangle
     */
    private int iconWidth;
    private int iconHeight;
    private int offsetX;
    private int offsetY;
    
    private void drawIcon(final Graphics2D g2d) {
        Icon currentIcon = isEnabled() ? icon : disabledIcon;
        if(currentIcon == null) {
        	this.iconWidth = 20;
        	this.iconHeight = 15;
        	this.offsetX = horizontalOffset;
        	this.offsetY = getHeight() / 2 - iconHeight / 2;
            return;
        }
        iconWidth = currentIcon.getIconWidth();
        iconHeight = currentIcon.getIconHeight();
        int offsetX = 0;
        int offsetY = 0;
        switch (iconOrientation) {
            case WEST:
                offsetX = horizontalOffset;
                offsetY = getHeight() / 2 - iconHeight / 2;
                break;
            case EAST:
                offsetX = getWidth() - horizontalOffset - iconWidth;
                offsetY = getHeight() / 2 - iconHeight / 2;
                break;
            case NORTH:
                offsetX = getWidth() / 2 - iconWidth / 2;
                offsetY = verticalOffset;
                break;
            case SOUTH:
                offsetX = getWidth() / 2 - iconWidth / 2;
                offsetY = getHeight() - verticalOffset - iconHeight;
                break;
            case NORTH_WEST:
                offsetX = horizontalOffset;
                offsetY = verticalOffset;
                break;
            case NORTH_EAST:
                offsetX = getWidth() - horizontalOffset - iconWidth;
                offsetY = verticalOffset;
                break;
            case SOUTH_WEST:
                offsetX = horizontalOffset;
                offsetY = getHeight() - verticalOffset - iconHeight;
                break;
            case SOUTH_EAST:
                offsetX = getWidth() - horizontalOffset - iconWidth;
                offsetY = getHeight() - verticalOffset - iconHeight;
                break;
            case CENTER:
                offsetX = getWidth() / 2 - iconWidth / 2;
                offsetY = getHeight() / 2 - iconHeight / 2;
                break;
        }
//        g2d.setColor(default_color);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        if(status == Status.PRESSED){
        	currentIcon.paintIcon(this,g2d,offsetX + 1,offsetY + 1);
        }else {
        	currentIcon.paintIcon(this,g2d,offsetX,offsetY);
        }
//        drawFill(g2d, w, h, offsetX, offsetY);
    }
    
	private void drawFill(final Graphics2D g2d, int width, int height, int offsetX,int offsetY) {
		RoundRectangle2D roundRectangle2d = new RoundRectangle2D.Float();
//      rectangle2d.setRect(offsetX, offsetY, width, height);
		if(status == Status.PRESSED){
			roundRectangle2d.setRoundRect(offsetX + 1, offsetY + 1, width, height, 5, 5);
		}else{
			roundRectangle2d.setRoundRect(offsetX, offsetY, width, height, 5, 5);
		}
        
        Color oldColor = getDefault_color();
        if(null == getDraw_color()){
        	g2d.setColor(oldColor);
        	g2d.fill(roundRectangle2d);
        }else {
        	g2d.setColor(getDraw_color());
        	g2d.fill(roundRectangle2d);
        	g2d.setColor(oldColor);
        }
	}
    
    private void drawBackgroundImage(final Graphics2D g2d) {
        if (!isEnabled()) {
            if (disabledBackgroundImage != null) {
                disabledBackgroundImage.paintIcon(this, g2d, 0, 0);
            }
            return;
        }
        switch (status) {
            case ROLLOVER:
                if (rolloverBackgroundImage != null) {
                    rolloverBackgroundImage.paintIcon(this, g2d, 0, 0);
                } else if (backgroundImage != null) {
                    backgroundImage.paintIcon(this, g2d, 0, 0);
                }
                break;
            case PRESSED:
                if (pressedBackgroundImage != null) {
                    pressedBackgroundImage.paintIcon(this, g2d, 1, 1);
                } else if (backgroundImage != null) {
                    backgroundImage.paintIcon(this, g2d, 1, 1);
                }
                break;
            case PRESSED_EXIT:
            default:
                if (backgroundImage != null) {
                    backgroundImage.paintIcon(this, g2d, 0, 0);
                }
                break;
        }
    }
    
    private void drawText(final Graphics2D g2d) {
        if(text == null || text.isEmpty()) {
            return;
        }
        Font font = getFont();
        FontMetrics fm = getFontMetrics(font);
        TextLayout textLayout = new TextLayout(text, font, g2d
                .getFontRenderContext());
//        AffineTransform affineTransform = AffineTransform
//                .getTranslateInstance(
//                (getWidth() - fm.stringWidth(text)) / 2,
//                getHeight() / 2 + fm.getHeight() / 4);
        AffineTransform affineTransform = null;
        if(status == Status.PRESSED){
        	affineTransform = AffineTransform.getTranslateInstance(
        			horizontalOffset * 2 + this.iconWidth + 1, getHeight() / 2 + fm.getHeight() / 4 + verticalOffset * 2  + 1);
        }else{
        	affineTransform = AffineTransform.getTranslateInstance(
        			horizontalOffset * 2 + this.iconWidth , getHeight() / 2 + fm.getHeight() / 4 + verticalOffset * 2);
        }
//		System.err.println("text.length is ..."
//						+ fm.stringWidth(text)
//						+ " And total.length is ..."
//						+ (horizontalOffset * 2 + this.iconWidth + fm.stringWidth(text)));
        Shape textShape = textLayout.getOutline(affineTransform);
        if(isEnabled()) {
            g2d.setPaint(getForeground());
        } else {
            g2d.setPaint(disabledForeground != null ? disabledForeground
                    : DEFAULT_DISABLED_FOREGROUND);
        }
        g2d.fill(textShape);
//        g2d.draw(textShape);
    }
    
    /**
     * ��굥���¼��Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mouseClicked(MouseEvent e) {
        
    }
    
    @Override
	protected void printBorder(Graphics g) {
    	super.paintBorder(g);
    }
    
    /**
     * ��ť����ʱ�Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mousePressed(MouseEvent e) {
        if (!isEnabled()) {
            return;
        } else if(!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        status = Status.PRESSED;
        repaint();
        if (pressedPreferredSize != null
                && !pressedPreferredSize.equals(currentSize)) {
            currentSize = pressedPreferredSize;
            revalidate();
        }
    }
    
    /**
     * ���̧��ʱ�Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mouseReleased(MouseEvent e) {
        if (!isEnabled()) {
            return;
        } else if(!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (e.getX() > 0 && e.getY() > 0 && e.getX() < getPreferredSize().width
                && e.getY() < getPreferredSize().height) {
            status = Status.ROLLOVER;
            if (rolloverPreferredSize != null
                    && !rolloverPreferredSize.equals(currentSize)) {
                currentSize = rolloverPreferredSize;
            }
            try {
                doAction(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            status = Status.DEFAULT;
            if (preferredSize != null && !preferredSize.equals(currentSize)) {
                currentSize = preferredSize;
            }
        }
        repaint();
        revalidate();
    }
    
    /**
     * ������ʱ�Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mouseEntered(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        status = (status == Status.PRESSED_EXIT) ? Status.PRESSED
                : Status.ROLLOVER;
        repaint();
        if (rolloverPreferredSize != null
                && !rolloverPreferredSize.equals(currentSize)) {
            currentSize = rolloverPreferredSize;
            revalidate();
        }
    }
    
    /**
     * ����뿪ʱ�Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mouseExited(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        status = (status == Status.PRESSED) ? Status.PRESSED_EXIT
                : Status.DEFAULT;
        repaint();
        if (preferredSize != null && !preferredSize.equals(currentSize)) {
            currentSize = preferredSize;
            revalidate();
        }
    }
    
    /**
     * �����קʱ�Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mouseDragged(MouseEvent e) {
    }
    
    /**
     * ����ڰ�ť�Ϸ��ƶ�ʱ�Ĵ���
     * @param e ����¼�
     * @deprecated �˷���ֻ���ڲ������ã��ⲿ��Ҫ��ʽ����
     */
    @Deprecated
	public void mouseMoved(MouseEvent e) {
    }
    
    /**
     * ִ�а�ť�Ķ���
     * @param e ����¼�
     * @throws java.lang.Exception ���ִ�е�ҵ������׳����쳣
     */
    private void doAction(MouseEvent e) throws Exception{
        if (!isEnabled()) {
            return;
        }
        if (action != null) {
            ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), "", e
                    .getWhen(), e.getModifiers());
            action.actionPerformed(ae);
        }
    }
    
    /**
     * ���ص�ǰ���״̬����ѳߴ硣���ֵ�ǲ��̶��ģ����������԰�ť�Ĳ�ͬ״̬���ֵ����ʱ
     * �ı䣬һ����������ֻ�����ֹ����������á�
     * @return ��ǰ���״̬����ѳߴ�
     */
    @Override
    public Dimension getPreferredSize() {
        return currentSize;
    }
    
    /**
     * ��ť����״̬��ö��
     *
     * @author ��һͯ
     *
     */
    private enum Status {
        /**
         * Ĭ��״̬
         */
        DEFAULT,
        /**
         * ����ƶ�����ť֮��
         */
        ROLLOVER,
        /**
         * �ڰ�ť�ϰ���������
         */
        PRESSED,
        /**
         * �ڰ�ť�ϰ�����������������ƿ���ť����
         */
        PRESSED_EXIT;
    }
    
    /**
     * ͼ���ڰ�ť�ϳ���λ�õ�ö�٣��˰�ťֻ�������һ��ͼ�꣬��Ҳ���ϴ������ť�Ĺ淶��
     * @author ��һͯ
     */
    public enum IconOrientation {
        /**
         * ͼ��λ�ڰ�ť������
         */
        WEST,
        /**
         * ͼ��λ�ڰ�ť������
         */
        NORTH,
        /**
         * ͼ��λ�ڰ�ť������
         */
        EAST,
        /**
         * ͼ��λ�ڰ�ť������
         */
        SOUTH,
        /**
         * ͼ��λ�ڰ�ť�����Ϸ�
         */
        NORTH_WEST,
        /**
         * ͼ��λ�ڰ�ť�����·�
         */
        SOUTH_WEST,
        /**
         * ͼ��λ�ڰ�ť�����Ϸ�
         */
        NORTH_EAST,
        /**
         * ͼ��λ�ڰ�ť�����·�
         */
        SOUTH_EAST,
        /**
         * ͼ��λ�ڰ�ť������
         */
        CENTER;
    }
    
    /*
     * custom some attributes for admClient
     * 
     */
    
    private PropertyChangeListener textListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setText((String)evt.getNewValue());
		}
	};
	private PropertyChangeListener colorListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setDraw_color((Color) evt.getNewValue());
		}
	};
	private PropertyChangeListener toolTipListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setToolTipText((String)evt.getNewValue());
		}
	};
    private JAlarmButtonModel model;
    public void setModel(JAlarmButtonModel model){
    	this.model = model;
    	
    	model.addPropertyChangeListener(model.ALARM_TEXT, textListener);
    	model.addPropertyChangeListener(model.ALARM_COLOR, colorListener);
    }
    
    private Color default_color = null;
    
    private Color getDefault_color() {
		return default_color;
	}
	private void setDefault_color(Color defaultColor) {
		default_color = defaultColor;
	}

	private Color draw_color = null;
	
	public Color getDraw_color() {
		return draw_color;
	}
	public void setDraw_color(Color drawColor) {
		draw_color = drawColor;
		repaint();
	}
	
	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(text);
	}
}