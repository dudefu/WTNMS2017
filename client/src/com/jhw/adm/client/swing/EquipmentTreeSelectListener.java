package com.jhw.adm.client.swing;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class EquipmentTreeSelectListener  implements TreeSelectionListener{

    
    public EquipmentTreeSelectListener( ){

    }
    
    @Override
	public void valueChanged(TreeSelectionEvent e) {

    	// ������ѡ��Ĵ����漰����·���Ĵ���
    	TreePath path = e.getPath();
    	Object[] nodes = path.getPath();
    	
    	//ʵ�ֶ����˵Ĳ���
    	//.....................����
	}
}
