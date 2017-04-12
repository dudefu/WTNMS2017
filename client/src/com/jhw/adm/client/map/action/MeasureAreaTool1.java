package com.jhw.adm.client.map.action;

import java.io.IOException;


import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.carto.IActiveView;
import com.esri.arcgis.carto.esriViewDrawPhase;
import com.esri.arcgis.controls.HookHelper;
import com.esri.arcgis.controls.IHookHelper;
import com.esri.arcgis.display.BasicRasterPicture;
import com.esri.arcgis.display.IDisplayFeedback;
import com.esri.arcgis.display.IRubberBand;
import com.esri.arcgis.display.RubberPolygon;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IPointCollection;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.ITopologicalOperator;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.interop.AutomationException;
//import com.esri.arcgis.output.BasicRasterPicture;
import com.esri.arcgis.support.ms.stdole.IPicture;
import com.esri.arcgis.systemUI.ICommand;
import com.esri.arcgis.systemUI.ITool;

public class MeasureAreaTool1  implements ICommand,ITool{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private IHookHelper hookHelper = null;
	
	private MapBean mapBean;

	IActiveView activeView;
	
	IDisplayFeedback m_DisplayFeedback;
	
	IPolyline m_polyline ;

	//private IUnitConverter unitConverter; 
	
	public MeasureAreaTool1() throws AutomationException, IOException {

	}

	public int getBitmap() throws IOException, AutomationException {
		BasicRasterPicture brp = new BasicRasterPicture();
		IPicture pic=null;
		try {
			String path = System.getProperty("user.dir"); //$NON-NLS-1$
			pic = brp.loadPicture(path+"\\resource\\images\\measure_2.gif"); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pic.getHandle();
	}

	@Override
	public String getCaption() throws IOException, AutomationException {
		return "";
	}

	@Override
	public String getCategory() throws IOException, AutomationException {
		
		return null;
	}

	@Override
	public int getHelpContextID() throws IOException, AutomationException {
		
		return 0;
	}

	@Override
	public String getHelpFile() throws IOException, AutomationException {
		
		return null;
	}

	@Override
	public String getMessage() throws IOException, AutomationException {
		
		return "";
	}

	@Override
	public String getName() throws IOException, AutomationException {
		
		return "";
	}

	@Override
	public String getTooltip() throws IOException, AutomationException {
		
		return "";
	}

	@Override
	public boolean isChecked() throws IOException, AutomationException {
		
		return false;
	}

	@Override
	public boolean isEnabled() throws IOException, AutomationException {
		return true;
	}

	@Override
	public void onClick() throws IOException, AutomationException {
		

	}

	@Override
	public void onCreate(Object hook) throws IOException, AutomationException {
		try
		{
			hookHelper = new HookHelper();
			hookHelper.setHookByRef(hook);

			if(hookHelper.getActiveView() == null){
				hookHelper = null;
			}
			else{
				activeView = hookHelper.getActiveView();
			}
		}
		catch(AutomationException e)
		{
			e.printStackTrace();
			hookHelper = null;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			hookHelper = null;
		}
	}

	@Override
	public boolean deactivate() throws IOException, AutomationException {
		 
		return true;
	}

	@Override
	public int getCursor() throws IOException, AutomationException {
		
		
		//String strRelativePath="/resource/images/measure.cur";
		
		//IPath path= Platform.getLocation();//得到workspace的路径
		//Image img = com.hailite.reminder.ui.images.Images.getImage(com.hailite.reminder.ui.images.Images.IMAGE_CLOSE_DOWN);
		//org.eclipse.swt.graphics.Cursor cursor = new org.eclipse.swt.graphics.Cursor(Display.getDefault(), img.getImageData(), 0, 0);
		//Display.getDefault().getSystemCursor(0);
		//String strPath=path.toOSString()+ strRelativePath;
		
//		Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
//		
//		return cursor.getType();
		
		return 0;
		
	}

	@Override
	public boolean onContextMenu(int x, int y) throws IOException,
	AutomationException {
		
		return false;
	}

	@Override
	public void onDblClick() throws IOException, AutomationException {
		
	}

	@Override
	public void onKeyDown(int keyCode, int shift) throws IOException,
	AutomationException {
		

	}

	@Override
	public void onKeyUp(int keyCode, int shift) throws IOException,
	AutomationException {
		

	}

	@Override
	public void onMouseDown(int button, int shift, int x, int y)
	throws IOException, AutomationException {

		if(button!=1){return;} 

		IPolygon pPolygon = new Polygon();
		IPointCollection pPointCollection;
		pPointCollection = (IPointCollection) pPolygon;

		IRubberBand pRubberBand = new RubberPolygon();
		IGeometry pRubberBandGeometry;        
		ITopologicalOperator topOperator = null;

		pRubberBandGeometry = pRubberBand.trackNew(activeView.getScreenDisplay(), null);        

		if (pRubberBandGeometry != null) {

			pPointCollection = (IPointCollection) pRubberBandGeometry;
			if (pPointCollection.getPointCount() > 2) {

				topOperator = (ITopologicalOperator) pPointCollection;
				
				if (!topOperator.isSimple()){topOperator.simplify();}

				pPolygon = (IPolygon) pRubberBandGeometry;

			} else 
			{
				return;
			} 
			
			pRubberBand = null;
			
			pRubberBandGeometry = null;
			
		}
	}


	@Override
	public void onMouseMove(int button, int shift, int x, int y)
	throws IOException, AutomationException {
	}

	@Override
	
	public void onMouseUp(int button, int shift, int x, int y)
	throws IOException, AutomationException {
		
		mapBean.refresh(esriViewDrawPhase.esriViewBackground, null, null);
		
	}

	@Override
	public void refresh(int hdc) throws IOException, AutomationException {
		

	}
}

