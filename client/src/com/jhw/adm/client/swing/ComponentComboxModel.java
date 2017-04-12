package com.jhw.adm.client.swing;

import java.util.ArrayList;
import java.util.List;

public class ComponentComboxModel {
	private List<Object> itemList = new ArrayList<Object>();
	  
	public int getSize(){
		return itemList.size(); 
	}

	public void addItem(Object obj ){
		itemList.add(obj);
	}

	public void removeItem(Object obj ){
		if (itemList.contains(obj)){
			itemList.remove(obj);
		}
	}

	public void insertItemAt(Object obj, int index){
		try{
			itemList.set(index, obj);
		}
		catch(Exception e){
		}
	}

	public void removeItemAt( int index ){
		try{
			itemList.remove(index);
		}
		catch(Exception e){
		}
	}
	
	public void removeAllItem(){
		itemList.clear();
	}
	
	public void setSelectedItem(Object anItem){
		  
	}

	public Object getItem(int index){
		return itemList.get(index);
	}
}
