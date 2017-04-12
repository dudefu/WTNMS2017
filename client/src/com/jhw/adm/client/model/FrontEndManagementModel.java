package com.jhw.adm.client.model;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;

@Component(FrontEndManagementModel.ID)
public class FrontEndManagementModel extends ViewModel{
	public static final String ID = "frontEndManagementModel";
	
	@Autowired
	@Qualifier(FEPTableModel.ID)
	private FEPTableModel fepTableModel;
	
	@Autowired
	@Qualifier(FEPIpTableModel.ID)
	private FEPIpTableModel ipTableModel;
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public FEPTableModel  getFEPTableModel(){
		return fepTableModel;
	}
	
	public FEPIpTableModel getFEPIpTableModel(){
		return ipTableModel;
	}
	
	
	
	public List<IPSegment> getSelectIpSegmentList(int rowIndex){
		FEPEntity fepEntity = fepTableModel.getFep(rowIndex).getFepEntity();
		
		return fepEntity.getSegment();
	}
	
	public void addFEP(FEPTopoNodeEntity fep){
		fepTableModel.addFEP(fep);
    	
    }
    
    public void updateFEP(int index ,FEPTopoNodeEntity fep){
    	fepTableModel.updateFEP(index, fep);
    }
    
    public void removeFEP(FEPTopoNodeEntity fep){
    	fepTableModel.removeFEP(fep);
    }
    
    public FEPTopoNodeEntity getFep(int rowIndex){
    	return fepTableModel.getFep(rowIndex);
    }
    
    public List<FEPTopoNodeEntity> getFepList(){
    	return fepTableModel.getFepList();
    }
    

    
    public IPSegment getIPSegment(int index){
    	return ipTableModel.getIPSegment(index);
    }
    
    public List getIPSegmentList(){
    	return ipTableModel.getIPSegmentList();
    }
    
    public void setIPSegment(IPSegment ipSegment){
    	ipTableModel.setIPSegment(ipSegment);
    }
    
    public void setIPSegmentList(List<IPSegment> list){
    	ipTableModel.setIPSegmentList(list);
    }
    
    public void removeAll(){
    	ipTableModel.removeAll();
    }
    
    public void removeIPSegment(IPSegment ipSegment){
    	ipTableModel.removeIPSegment(ipSegment);
    }
    public void removeIPSegment(int index){
    	ipTableModel.removeIPSegment(index);
    }
}
