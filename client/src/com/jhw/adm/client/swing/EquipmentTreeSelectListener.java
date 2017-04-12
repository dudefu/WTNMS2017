package com.jhw.adm.client.swing;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class EquipmentTreeSelectListener  implements TreeSelectionListener{

    
    public EquipmentTreeSelectListener( ){

    }
    
    @Override
	public void valueChanged(TreeSelectionEvent e) {

    	// 凡是树选择的处理都涉及到树路径的处理：
    	TreePath path = e.getPath();
    	Object[] nodes = path.getPath();
    	
    	//实现对拓扑的操作
    	//.....................待续
	}
}
