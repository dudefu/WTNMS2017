package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.LOADMIB;
import static com.jhw.adm.client.core.ActionConstants.REMOVEMIB;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//import pt.ipb.agentapi.mibs.MibException;
//import pt.ipb.agentapi.mibs.MibModule;
//import pt.ipb.agentapi.mibs.MibNode;
//import pt.ipb.agentapi.mibs.MibOps;
//import pt.ipb.agentapi.mibs.MibTC;
//import pt.ipb.agentapi.mibs.MibTrap;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.util.Constants;

@Component(MibBrowserView.ID)
public class MibBrowserView{
	private static final long serialVersionUID = 1L;
	public static final String ID = "mibBrowserView";

	private JToolBar jToolBar1 = new JToolBar();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JLabel statusBar=new JLabel();
	private JSplitPane jSplitPane1 = new JSplitPane();
	private JSplitPane jSplitPane2 = new JSplitPane();
	private JScrollPane scrollText1;
	private JTree mibTree;
//	private MibOps ops;
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel netModel;
	private java.io.File currentDir;
	private JButton addMibButton,removeMibButton,closeMibButton;
	private OidNode currentNode;

	private BorderLayout borderLayout2 = new BorderLayout();
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private JPanel jPanel4 = new JPanel();
	private JPanel jPanel5 = new JPanel();
	private BorderLayout borderLayout3 = new BorderLayout();
	private JLabel jLabel1 = new JLabel();
	private JTextArea oidDescription = new JTextArea();
	private JPanel jPanel6 = new JPanel();
	private JLabel jLabel7 = new JLabel();
	private BorderLayout borderLayout4 = new BorderLayout();
	private JTextArea resultText = new JTextArea();
	private JPanel jPanel7 = new JPanel();
	private JPanel jPanel8 = new JPanel();
	private JPanel jPanel10 = new JPanel();
	private GridLayout gridLayout3 = new GridLayout();
	private GridLayout gridLayout4 = new GridLayout();
	private JPanel jPanel9 = new JPanel();
	private JPanel jPanel11 = new JPanel();
	private JPanel jPanel12 = new JPanel();
	private JPanel jPanel14 = new JPanel();
	private JPanel jPanel13 = new JPanel();
	private JLabel jLabel2 = new JLabel();
	private BorderLayout borderLayout5 = new BorderLayout();
	private BorderLayout borderLayout6 = new BorderLayout();
	private JLabel jLabel3 = new JLabel();
	private JTextField jOidStatus = new JTextField();
	private JTextField jOidSyn = new JTextField();
	private BorderLayout borderLayout7 = new BorderLayout();
	private JLabel jLabel4 = new JLabel();
	private JTextField jOidAccess = new JTextField();
	private BorderLayout borderLayout8 = new BorderLayout();
	private JLabel jLabel5 = new JLabel();
	private JTextField jOidRef = new JTextField();
	private BorderLayout borderLayout9 = new BorderLayout();
	private JLabel jLabel6 = new JLabel();
	private JTextField jOidIndex = new JTextField();
	private BorderLayout borderLayout10 = new BorderLayout();
	private JLabel jLabel8 = new JLabel();
	private JTextField jOidObject = new JTextField();
	private GridLayout gridLayout5 = new GridLayout();
	private JPanel jPanel15 = new JPanel();
	private JPanel jPanel16 = new JPanel();
	private JPanel jPanel18 = new JPanel();
	private JPanel jPanel19 = new JPanel();
	private GridLayout gridLayout6 = new GridLayout();
	private GridLayout gridLayout7 = new GridLayout();
	private JPanel jPanel20 = new JPanel();
	private JPanel jPanel22 = new JPanel();
	private BorderLayout borderLayout11 = new BorderLayout();
	private JLabel jLabel9 = new JLabel();
	private BorderLayout borderLayout13 = new BorderLayout();
	private JLabel jLabel11 = new JLabel();
	private BorderLayout borderLayout16 = new BorderLayout();
	private JLabel jLabel14 = new JLabel();
	private JTextField oidText = new JTextField();
	private JTextField setValue = new JTextField();
	private JComboBox snmpHost = new JComboBox();
	private JButton jButtonGet = new JButton();
	private JButton jButtonGetNext = new JButton();
	private JButton jButtonSet = new JButton();
	private TitledBorder titledBorder3;
	private TitledBorder titledBorder1;
	private JButton jButtonWalk = new JButton();
	private GridLayout gridLayout8 = new GridLayout();
	private JPanel jPanel17 = new JPanel();
	private BorderLayout borderLayout12 = new BorderLayout();
	private JLabel jLabel10 = new JLabel();
	private JTextField getComm = new JTextField();
	private JPanel jPanel21 = new JPanel();
	private BorderLayout borderLayout14 = new BorderLayout();
	private JLabel jLabel12 = new JLabel();
	private JPasswordField setComm = new JPasswordField();

	private Insets inset = new Insets(1,0,0,1);	 private ImageIcon logo = new ImageIcon(this.getClass().getResource("logo.png"));
	private JMenuItem clearAllItem = new JMenuItem("清空历史");
	private JMenuItem copyItem = new JMenuItem("复制到...");
	private JPopupMenu popup = new JPopupMenu();
	
	private ButtonFactory buttonFactory;
	 
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	 
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	 
	@PostConstruct
	protected void initialize() {
//		try {
//			buttonFactory = actionManager.getButtonFactory(this); 
//			jbInit();
////	    	openDefaultMib();
//		}
//	   catch(Exception e) {
//	     e.printStackTrace();
//	   }
	 }

//	 private void jbInit() throws Exception {
//		this.setTitle("Mib浏览器");
//		titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(Color.gray),"");
//	   
//
//		rootNode=new DefaultMutableTreeNode("Mibs");
//		netModel=new DefaultTreeModel(rootNode);
//		mibTree=new JTree(netModel);
//		statusBar.setText(" ");
//		
//	   	ImageIcon imageOpenNode = imageRegistry.getImageIcon(NetworkConstants.OPEN_MIB);
//	   	ImageIcon imageCloseNode = imageRegistry.getImageIcon(NetworkConstants.CLOSE_MIB);
//	  	ImageIcon imageNode=imageRegistry.getImageIcon(NetworkConstants.LEAF);
//		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
//	   	renderer.setClosedIcon(imageCloseNode);
//	   	renderer.setOpenIcon(imageOpenNode);
//	   	renderer.setLeafIcon(imageNode);
//		mibTree.setCellRenderer(renderer);
//
//
//	   JScrollPane treeScrollerMib = new JScrollPane(mibTree);
//	   addMibButton = buttonFactory.createButton(LOADMIB);
//	   removeMibButton = buttonFactory.createButton(REMOVEMIB);
//	   closeMibButton = buttonFactory.createCloseButton();
//	   this.setCloseButton(closeMibButton);
//
//	   MouseListener ml = new MouseAdapter() {
//	     public void mousePressed(MouseEvent e) {
//	       int selRow = mibTree.getRowForLocation(e.getX(), e.getY());
//	       TreePath selPath = mibTree.getPathForLocation(e.getX(), e.getY());
//	       if(selRow != -1) {
//	         if(e.getClickCount() == 1) {
//	           treeClick(selRow, selPath);
//	           mibTree.expandPath(selPath);
//	         }
//	       }
//	     }
//	   };
//	   mibTree.addMouseListener(ml);
//
//	   jPanel2.setLayout(borderLayout17);
//	   jPanel5.setLayout(borderLayout3);
//	   jLabel1.setPreferredSize(new Dimension(50, 18));
//	   jLabel1.setText("描述：");
//	   oidDescription.setBorder(BorderFactory.createLoweredBevelBorder());
//	   oidDescription.setEditable(false);
//	   oidDescription.setWrapStyleWord(true);
//	   JScrollPane scrollText2=new JScrollPane(oidDescription);
//	   jPanel4.setLayout(borderLayout15);
//	   jLabel7.setToolTipText("");
//	   jLabel7.setText("显示：");
//	   jPanel6.setLayout(borderLayout4);
//	   resultText.setBorder(BorderFactory.createLoweredBevelBorder());
//	   resultText.setEditable(false);
//	   resultText.setWrapStyleWord(true);
//	   resultText.setAutoscrolls(true);
//	   resultText.setFont(new Font("",Font.BOLD,12));
//	   scrollText1=new JScrollPane(resultText);
//	   jPanel8.setLayout(gridLayout4);
//	   jPanel7.setLayout(gridLayout3);
//	   gridLayout3.setColumns(2);
//	   gridLayout3.setHgap(1);
//	   gridLayout3.setRows(2);
//	   gridLayout3.setVgap(1);
//	   gridLayout4.setColumns(1);
//	   gridLayout4.setHgap(1);
//	   gridLayout4.setRows(2);
//	   gridLayout4.setVgap(1);
//	   jLabel2.setPreferredSize(new Dimension(50, 18));
//	   jLabel2.setText("语法：");
//	   jPanel11.setLayout(borderLayout5);
//	   jPanel12.setLayout(borderLayout6);
//	   jLabel3.setPreferredSize(new Dimension(50, 18));
//	   jLabel3.setText("状态：");
//	   jPanel14.setLayout(borderLayout7);
//	   jLabel4.setPreferredSize(new Dimension(50, 18));
//	   jLabel4.setText("访问：");
//	   jPanel13.setLayout(borderLayout8);
//	   jLabel5.setMaximumSize(new Dimension(50, 18));
//	   jLabel5.setPreferredSize(new Dimension(50, 18));
//	   jLabel5.setToolTipText("");
//	   jLabel5.setText("参考：");
//	   jPanel10.setLayout(borderLayout9);
//	   jLabel6.setPreferredSize(new Dimension(50, 18));
//	   jLabel6.setText("索引：");
//	   jPanel9.setLayout(borderLayout10);
//	   jLabel8.setPreferredSize(new Dimension(50, 18));
//	   jLabel8.setText("对象：");
//	   jPanel3.setLayout(gridLayout5);
//	   gridLayout5.setColumns(1);
//	   gridLayout5.setHgap(1);
//	   gridLayout5.setRows(4);
//	   gridLayout5.setVgap(2);
//	   jPanel15.setLayout(gridLayout6);
//	   jPanel16.setLayout(gridLayout7);
//	   gridLayout6.setColumns(2);
//	   gridLayout6.setHgap(5);
//	   gridLayout6.setRows(0);
//	   gridLayout7.setColumns(2);
//	   gridLayout7.setHgap(5);
//	   gridLayout7.setRows(0);
//	   jPanel20.setLayout(borderLayout11);
//	   jLabel9.setPreferredSize(new Dimension(50, 18));
//	   jLabel9.setText("设备：");
//	   jPanel22.setLayout(borderLayout13);
//	   jLabel11.setPreferredSize(new Dimension(50, 18));
//	   jLabel11.setText("数值：");
//	   jPanel18.setLayout(borderLayout16);
//	   jPanel19.setLayout(gridLayout8);
//	   jLabel14.setPreferredSize(new Dimension(50, 18));
//	   jLabel14.setText("OID：");
//	   jButtonGet.setText("Get");
//	   jButtonGet.addActionListener(new java.awt.event.ActionListener() {
//	     public void actionPerformed(ActionEvent e) {
//	       jButtonGet_actionPerformed(e);
//	     }
//	   });
//	   jButtonGetNext.setText("GetNext");
//	   jButtonGetNext.addActionListener(new java.awt.event.ActionListener() {
//	     public void actionPerformed(ActionEvent e) {
//	       jButtonGetNext_actionPerformed(e);
//	     }
//	   });
//	   jButtonSet.setText("Set");
//	   jButtonSet.addActionListener(new java.awt.event.ActionListener() {
//	     public void actionPerformed(ActionEvent e) {
//	       jButtonSet_actionPerformed(e);
//	     }
//	   });
//	   jButtonSet.setEnabled(false);
//
//	   jOidSyn.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jOidSyn.setEditable(false);
//	   jOidStatus.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jOidStatus.setEditable(false);
//	   jOidAccess.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jOidAccess.setEditable(false);
//	   jOidRef.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jOidRef.setEditable(false);
//	   jOidIndex.setEnabled(false);
//	   jOidIndex.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jOidIndex.setEditable(false);
//	   jOidObject.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jOidObject.setEditable(false);
//
//	   borderLayout4.setHgap(2);
//	   borderLayout4.setVgap(2);
//	   borderLayout2.setHgap(5);
//	   borderLayout2.setVgap(5);
//	   snmpHost.setEditable(true);
//
//	   jButtonWalk.setToolTipText("");
//	   jButtonWalk.setText("Walk");
//	   jButtonWalk.addActionListener(new java.awt.event.ActionListener() {
//	     public void actionPerformed(ActionEvent e) {
//	       jButtonWalk_actionPerformed(e);
//	     }
//	   });
//
//
//	   gridLayout8.setColumns(4);
//	   gridLayout8.setHgap(5);
//	   jPanel17.setLayout(borderLayout12);
//	   jLabel10.setPreferredSize(new Dimension(100, 18));
//	   jLabel10.setToolTipText("");
//	   jLabel10.setText("getCommunity：");
//	   jPanel21.setLayout(borderLayout14);
//	   jLabel12.setPreferredSize(new Dimension(100, 18));
//	   jLabel12.setText("setCommunity：");
//	   getComm.setText("public");
//	   setComm.setText("private");
//	   jToolBar1.add(addMibButton);
//	   jToolBar1.add(removeMibButton);
//	   jToolBar1.add(closeMibButton);
//	   jToolBar1.setFloatable(false);
//
//	   jToolBar1.setRollover(true);
//	   jToolBar1.setPreferredSize(new Dimension(280,30));
//	   addMibButton.setMargin(inset);
//	   removeMibButton.setMargin(inset);
//	   closeMibButton.setMargin(inset);
//
//	   jSplitPane1.setBorder(BorderFactory.createLineBorder(Color.gray));
//	   jSplitPane1.setDividerLocation(180);
//	   jSplitPane1.setDividerSize(6);
//	   jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
//	   jSplitPane2.setDividerSize(6);
//	   jSplitPane1.add(treeScrollerMib, JSplitPane.LEFT);
//	   jSplitPane1.add(jSplitPane2, JSplitPane.RIGHT);
//
//	   jPanel2.setPreferredSize(new Dimension(350, 200));
//	   jPanel3.setPreferredSize(new Dimension(350, 92));
//	   jSplitPane2.add(jPanel2, JSplitPane.TOP);
//	   jPanel1.setLayout(new BorderLayout());
//	   jSplitPane2.add(jPanel1,JSplitPane.BOTTOM);
//	   jPanel2.add(jPanel4, BorderLayout.NORTH);
//	   jPanel4.add(jPanel7, BorderLayout.NORTH);
//	   jPanel7.add(jPanel11, null);
//	   jPanel11.add(jLabel2, BorderLayout.WEST);
//	   jPanel11.add(jOidSyn, BorderLayout.CENTER);
//	   jPanel7.add(jPanel12, null);
//	   jPanel12.add(jLabel3, BorderLayout.WEST);
//	   jPanel12.add(jOidStatus, BorderLayout.CENTER);
//	   jPanel7.add(jPanel14, null);
//	   jPanel14.add(jLabel4, BorderLayout.WEST);
//	   jPanel14.add(jOidAccess, BorderLayout.CENTER);
//	   jPanel7.add(jPanel13, null);
//	   jPanel13.add(jLabel5, BorderLayout.WEST);
//	   jPanel13.add(jOidRef, BorderLayout.CENTER);
//	   jPanel4.add(jPanel8, BorderLayout.CENTER);
//	   jPanel8.add(jPanel10, null);
//	   jPanel10.add(jLabel6, BorderLayout.WEST);
//	   jPanel10.add(jOidIndex, BorderLayout.CENTER);
//	   jPanel8.add(jPanel9, null);
//	   jPanel9.add(jLabel8, BorderLayout.WEST);
//	   jPanel9.add(jOidObject, BorderLayout.CENTER);
//	   jPanel2.add(jPanel5, BorderLayout.CENTER);
//	   jPanel5.add(jLabel1, BorderLayout.NORTH);
//	   jPanel5.add(scrollText2, BorderLayout.CENTER);
//	   jPanel1.add(jPanel3, BorderLayout.SOUTH);
//	   jPanel3.add(jPanel15, null);
//	   jPanel15.add(jPanel20, null);
//	   jPanel20.add(jLabel9, BorderLayout.WEST);
//	   jPanel20.add(snmpHost, BorderLayout.CENTER);
//	   jPanel15.add(jPanel17, null);
//	   jPanel17.add(jLabel10, BorderLayout.WEST);
//	   jPanel17.add(getComm, BorderLayout.CENTER);
//	   jPanel3.add(jPanel16, null);
//	   jPanel16.add(jPanel22, null);
//	   jPanel22.add(jLabel11, BorderLayout.WEST);
//	   jPanel22.add(setValue, BorderLayout.CENTER);
//	   jPanel16.add(jPanel21, null);
//	   jPanel21.add(jLabel12, BorderLayout.WEST);
//	   jPanel21.add(setComm, BorderLayout.CENTER);
//	   jPanel3.add(jPanel18, null);
//	   jPanel18.add(jLabel14, BorderLayout.WEST);
//	   jPanel18.add(oidText, BorderLayout.CENTER);
//	   jPanel3.add(jPanel19, null);
//	   jPanel19.add(jButtonGet, null);
//	   jPanel19.add(jButtonGetNext, null);
//	   jPanel19.add(jButtonSet, null);
//	   jPanel19.add(jButtonWalk, null);
//	   jPanel1.add(jPanel6, BorderLayout.CENTER);
//	   jPanel6.add(jLabel7, BorderLayout.NORTH);
//	   jPanel6.add(scrollText1, BorderLayout.CENTER);
//
//	   popup.add(clearAllItem);
//	   clearAllItem.addActionListener(new java.awt.event.ActionListener()
//	    {
//	      public void actionPerformed(ActionEvent e)
//	      {
//	        resultText.setText("");
//	      }
//	    });
//	    popup.addSeparator();
//	    popup.add(copyItem);
//	    copyItem.addActionListener(new java.awt.event.ActionListener()
//	    {
//	         public void actionPerformed(ActionEvent e)
//	         {
//	           resultText.copy();
//	         }
//	    });
//	   resultText.addMouseListener(new MouseAdapter()
//	   {
//	     public void mouseReleased(MouseEvent e)
//	      {
//	        if(e.isPopupTrigger())
//	       {
//	         showPopupMenu(e);
//	       }
//	      }
//	   });
//
//	   this.setLayout(borderLayout1);
//	   this.add(jToolBar1, BorderLayout.NORTH);
//	   this.add(jSplitPane1, BorderLayout.CENTER);
//	   this.add(statusBar, BorderLayout.SOUTH);
//	   
//	   this.setViewSize(800,600);
//
//	 }
//
//	 /**
//	  * 打开缺省MIB文件
//	  */
//	 private void openDefaultMib(){
//		 String defaultPath = "mibs" + File.separator + "RFC1213-MIB";
//		 String defaultPathGH = "mibs" + File.separator + "IETH8000_MGMT_II.mib";
//	
//		 try{
//		     readMibFile(defaultPath);
//		     mibTree.expandRow(0);
//		     readMibFile(defaultPathGH);
//		     mibTree.expandRow(0);
//		 }
//		 catch(Exception exc){
//		     exc.printStackTrace();
//		     JOptionPane.showMessageDialog(this,exc.getMessage());
//		 }
//	 }
//
//	 /**
//	  * Read the mib file with file name.
//	  * @param fileName The name of mib file.
//	  */
//	 public void readMibFile(String fileName) throws FileNotFoundException, MibException
//	 {
//	   this.ops = new MibOps();
//	   String[] path=new String[]{System.getProperty("user.dir")+File.separator+"mibs"};
//	   this.ops.setPath(path);
//	   this.ops.loadMib(fileName);
//
//	   //for(Enumeration e=ops.modules(); e.hasMoreElements(); )
//	   Enumeration e=ops.modules();
//	   if (e.hasMoreElements())
//	   {
//	     MibModule module = (MibModule)e.nextElement();
//
//	     //初始化基本节点
//	     DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(module.
//	         getName());
//
//	     MibNode root = module.getRoot();
//	     if (root != null)
//	     {
//	       DefaultMutableTreeNode nodeNodeInit = getChildren(2, root);
//	       String nodeLabel = ( (OidNode) (nodeNodeInit.getUserObject())).name;
//	       //DefaultMutableTreeNode nodeNode=initMibNodes(nodeNodeInit,nodeLabel);
//
//	       if (nodeNodeInit != null)
//	         moduleNode.add(nodeNodeInit);
//	     }
//
//	     for (Enumeration e2 = module.mibTCs(); e2.hasMoreElements(); )
//	     {
//	       MibTC tc = (MibTC) e2.nextElement();
//	       OidNode oidNode = new OidNode();
//
//	       oidNode.name = tc.getLabel();
//	       oidNode.description = tc.getDescription();
//	       oidNode.reference = tc.getReference();
//	       if (tc.getSyntax() != null)
//	         oidNode.syntax = tc.getSyntax().getDescription();
//	       oidNode.status = tc.getStatus();
//
//	       DefaultMutableTreeNode tcNode = new DefaultMutableTreeNode(oidNode);
//	       moduleNode.add(tcNode);
//	     }
//
//	     for (Enumeration e3 = module.traps(); e3.hasMoreElements(); )
//	     {
//	       MibTrap trap = (MibTrap)e3.nextElement();
//	       OidNode oidNode=new OidNode();
//
//	       oidNode.name=trap.getLabel();
//	       oidNode.description=trap.getDescription();
//	       oidNode.oid=trap.getEnterprise().toString();
//	       oidNode.reference=trap.getReference();
//
//	       DefaultMutableTreeNode trapNode=new DefaultMutableTreeNode(oidNode);
//	       moduleNode.add(trapNode);
//	     }
//	     netModel.insertNodeInto(moduleNode,rootNode,rootNode.getChildCount());
//	   }
//	 }
//
//	 /**
//	  * Get the children of given node.
//	  * @param level The level of node.
//	  * @param node The current node.
//	  */
//	 DefaultMutableTreeNode getChildren(int level, MibNode node)
//	 {
//	   DefaultMutableTreeNode moduleNode;
//
//	   if(node!=null)
//	   {
//	     OidNode oidNode=new OidNode();
//
//	     oidNode.name=node.getLabel();
//	     oidNode.description=node.getDescription();
//	     oidNode.oid=node.getOID().toString();
//	     oidNode.oidStr=node.getOIDString();
//	     oidNode.access=node.getAccessStr();
//	     oidNode.status=node.getStatus();
//
//	     if (node.getSyntax()!=null)
//	       oidNode.syntax=node.getSyntax().getDescription();
//
//	     if (node.isIndex())
//	       oidNode.index="ifIndex";
//
//	     moduleNode=new DefaultMutableTreeNode(oidNode);
//	     for(Enumeration e=node.children(); e.hasMoreElements(); ) {
//	       MibNode child = (MibNode)e.nextElement();
//	       DefaultMutableTreeNode nodeNode=getChildren(level+2, child);
//	       if (nodeNode!=null)
//	         moduleNode.add(nodeNode);
//	     }
//	     return moduleNode;
//	   }
//	   return null;
//	 }
//
//	 DefaultMutableTreeNode initMibNodes(DefaultMutableTreeNode initNode,String label)
//	 {
//	   String name[] = {
//	    "iso", "org", "dod", "internet", "directory", "mgmt", "experimental", "private", "snmpV2",
//	   "enterprises", "mib-2", "snmpDomains", "snmpProxys", "snmpModules"
//	   };
//	   String parent[] = {
//	       "", "iso", "org", "dod", "internet", "internet", "internet", "internet", "internet",
//	       "private", "mgmt", "snmpV2", "snmpV2", "snmpV2"
//	   };
//
//	   String oid[] = {
//	           "0","1","1.3","1.3.6","1.3.6.1","1.3.6.1","1.3.6.1","1.3.6.1","1.3.6.1",
//	      "1.3.6.1.4","1.3.6.1.2","1.3.6.1.6", "1.3.6.1.6", "1.3.6.1.6"
//	   };
//
//	   DefaultMutableTreeNode returnNode=initNode;
//	   while (!label.equalsIgnoreCase("iso"))
//	   {
//	     DefaultMutableTreeNode tempNode=new DefaultMutableTreeNode();
//
//	     for (int i=1;i<name.length;i++)
//	     {
//	       if (label.equalsIgnoreCase(name[i]))
//	       {
//	          OidNode oidNode=new OidNode();
//	          oidNode.name=parent[i];
//	          oidNode.description=parent[i];
//	          String parentOid=((OidNode)(returnNode.getUserObject())).oid;
//	          String parentOidStr=((OidNode)(returnNode.getUserObject())).oidStr;
//	          oidNode.oid=oid[i];
//	          tempNode.setUserObject(oidNode);
//	          tempNode.add(returnNode);
//	          returnNode=tempNode;
//	          label=oidNode.name;
//	          break;
//	       }
//	     }
//	   }
//
//	   return returnNode;
//	 }
//
//
//	 /**
//	  * Respose double click action,will open the selected submap
//	  *
//	  */
//	 protected void treeClick(int selRow, TreePath selPath){
//		 DefaultMutableTreeNode node = (DefaultMutableTreeNode)mibTree.getLastSelectedPathComponent();
//		 if (node==null){
//			 return;
//		 }
//		 Object obInfo=node.getUserObject();
//		 if (obInfo instanceof OidNode){
//			 currentNode=(OidNode)obInfo;
//			 oidText.setText(currentNode.oid);
//			 jOidObject.setText(currentNode.oidStr);
//			 jOidSyn.setText(currentNode.syntax);
//			 jOidStatus.setText(currentNode.status);
//			 jOidIndex.setText(currentNode.index);
//			 jOidAccess.setText(currentNode.access);
//			 jOidRef.setText(currentNode.reference);
//			 oidDescription.setText(currentNode.description);
//
//			 if (currentNode.access.equalsIgnoreCase("read-write"))
//				 jButtonSet.setEnabled(true);
//			 else
//				 jButtonSet.setEnabled(false);
//		 }
//	 }
//
//	 
//	 
//	 //下面方法myTranslate的作用是将StrinBuffer中的回车符号换为 <br> 
//	 private StringBuffer   myTranslate(StringBuffer   sour) 
//	 { 
//	     for(int   i=0;i <sour.length();i++) {
//	         if(sour.substring(i,i+1).equals( "\n")){ 
//	        	 sour.delete(i,i+1); 
//	             sour.insert(i, ""); 
//	         } 
//	         if(sour.substring(i,i+1).equals( "\r")){ 
//	             sour.delete(i,i+1); 
//	             sour.insert(i, ""); 
//	         } 
//	         if(sour.substring(i,i+1).equals("  ")) { 
//	              sour.delete(i,i+1); 
//	              sour.insert(i, ""); 
//	         } 
//	     } 
//	     return   sour;
//	 } 
//	 
//	 @ViewAction(name=LOADMIB, icon=NetworkConstants.ONLINE,desc="加载MIB表", role=Constants.MANAGERCODE)
//	 public void loadMib(){
//			final JFileChooser fileChooser = new JFileChooser();
//			fileChooser.setFileFilter(new MibFileFilter());
//				
//			String filePath = "";
//			int returnVal = fileChooser.showOpenDialog(MibBrowserView.this);
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				filePath = fileChooser.getSelectedFile().getPath();
//			}
//			 
//			if (null != filePath && !"".equals(filePath)){
//				this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//				try{
//					
//					readMibFile(filePath);
//					mibTree.expandRow(0);
//			    }
//				catch(Exception exc){
//			        exc.printStackTrace();
//			        JOptionPane.showMessageDialog(this,exc.getMessage());
//			     }
//			    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//			 } 
//		}
//		
//	 @ViewAction(name=REMOVEMIB, icon=NetworkConstants.OFFLINE,desc="移除MIB表", role=Constants.MANAGERCODE)
//	 public void removeMib(){
//			DefaultMutableTreeNode node = (DefaultMutableTreeNode)mibTree.getLastSelectedPathComponent();
//			if (node==null){
//			    JOptionPane.showMessageDialog(this,"请选择一个Mib!");
//			    return;
//			}
//		
//			if (node.getParent()!=null){
//			    if (node.getParent().equals(rootNode)){
//			    	netModel.removeNodeFromParent(node);
//			    	return;
//			    }
//			}
//		
//			JOptionPane.showMessageDialog(this,"请选择一个Mib!");
//			return;
//		}
//
//	 /**
//	  * 
//	  * @param e
//	  */
//	 private void jButtonGet_actionPerformed(ActionEvent e) {
//	   String IP=(String)snmpHost.getSelectedItem();
//	   if (IP==null)
//	   {
//	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
//	     return;
//	   }
//	   if (IP.equals(""))
//	   {
//	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
//	     return;
//	   }
//	   saveIP(IP);
//
//	   String get=getComm.getText();
//	   String oid=oidText.getText();
//	   if (oid.equals(""))
//	   {
//	     JOptionPane.showMessageDialog(this,"请输入合适的Oid!");
//	     return;
//	   }
//
//	   if(oid.startsWith("."))
//	     oid=oid.substring(1);
//
//	   this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//	   resultText.append("\r\nSnmp Get "+IP+" Oid: "+oid+"\r\n");
//
////	   try
////	   {
////	     MComProxy proxy = MComProxyConnector.lookupMComProxy("Local");
////	     String[] names = new String[]
////	         {
////	         "java.lang.String",
////	         "java.util.HashMap"
////	     };
////	     HashMap environment=new HashMap();
////	     environment.put("SnmpOid",oid);
////	     environment.put("SnmpCommunityGet",get);
////	     Object[] values = new Object[]
////	         {
////	         IP,
////	         environment
////	     };
////	     String re=(String)proxy.invoke("MibBrowser", "snmpGet", values, names);
////	     proxy.close();
////	     resultText.append(re);
////	   }catch (MComProxyException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////	   catch (java.io.IOException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////
////	   resultText.append("\r\n");
////	   this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//	 }
//
//	 /**
//	  * 
//	  * @param e
//	  */
//	 private void jButtonGetNext_actionPerformed(ActionEvent e) {
////	   String IP=(String)snmpHost.getSelectedItem();
////	   if (IP==null)
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
////	     return;
////	   }
////	   if (IP.equals(""))
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
////	     return;
////	   }
////	   saveIP(IP);
////	   String get=getComm.getText();
////	   String oid=oidText.getText();
////	   if (oid.equals(""))
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的Oid!");
////	     return;
////	   }
////	   if(oid.startsWith("."))
////	     oid=oid.substring(1);
////
////	   this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
////	   resultText.append("\r\nSnmp GetNext "+IP+" Oid: "+oid+"\r\n");
////	   try
////	   {
////	     MComProxy proxy = MComProxyConnector.lookupMComProxy("Local");
////	     String[] names = new String[]
////	         {
////	         "java.lang.String",
////	         "java.util.HashMap"
////	     };
////	     HashMap environment=new HashMap();
////	     environment.put("SnmpOid",oid);
////	     environment.put("SnmpCommunityGet",get);
////	     Object[] values = new Object[]
////	         {
////	         IP,
////	         environment
////	     };
////	     String re=(String)proxy.invoke("MibBrowser", "snmpGetNext", values, names);
////	     String[] oids=re.split(":");
////	     if (oids.length>1)
////	       oidText.setText(oids[0].trim());
////
////	     proxy.close();
////	     resultText.append(re);
////	   }catch (MComProxyException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////	   catch (java.io.IOException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////
////	   resultText.append("\r\n");
////	   this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//
//	 }
//
//	 /**
//	  * 
//	  * @param e
//	  */
//	 private void jButtonSet_actionPerformed(ActionEvent e) {
////	   String IP=(String)snmpHost.getSelectedItem();
////	   if (IP==null)
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
////	     return;
////	   }
////	   if (IP.equals(""))
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
////	     return;
////	   }
////
////	   saveIP(IP);
////	   String set=new String(setComm.getPassword());
////	   String oid=oidText.getText();
////	   String value=setValue.getText();
////	   if (oid.equals(""))
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的Oid!");
////	     return;
////	   }
////	   if (currentNode.syntax.equals(SMI_IPADDRESS))
////	   {
////	     value=value.replace('.',':');
////	     String[] ipstr=value.split(":");
////	     if (ipstr.length!=4)
////	     {
////	       JOptionPane.showMessageDialog(this,"请输入合适的IP地址!");
////	       return;
////	     }
////	   }
////
////	   if(oid.startsWith("."))
////	     oid=oid.substring(1);
////
////	   this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
////	   resultText.append("\r\nSnmp Set "+IP+" Oid: "+oid+"\r\n");
////
////	   try
////	   {
////	     MComProxy proxy = MComProxyConnector.lookupMComProxy("Local");
////	     String[] names = new String[]
////	         {
////	         "java.lang.String",
////	         "java.util.HashMap",
////	         "java.lang.String",
////	         "java.lang.String"
////	     };
////	     HashMap environment=new HashMap();
////	     environment.put("SnmpOid",oid);
////	     environment.put("SnmpCommunitySet",set);
////	     Object[] values = new Object[]
////	         {
////	         IP,
////	         environment,
////	         value,
////	         currentNode.syntax
////	     };
////	     String re=(String)proxy.invoke("MibBrowser", "snmpSet", values, names);
////	     proxy.close();
////	     resultText.append(re);
////	   }catch (MComProxyException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////	   catch (java.io.IOException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////
////	   resultText.append("\r\n");
////	   this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//
//	 }
//
//	 /**
//	  * 
//	  * @param e
//	  */
//	 private void jButtonWalk_actionPerformed(ActionEvent e) {
////	   String IP=(String)snmpHost.getSelectedItem();
////	   if (IP==null)
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
////	     return;
////	   }
////	   if (IP.equals(""))
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的设备!");
////	     return;
////	   }
////	   saveIP(IP);
////	   String get=getComm.getText();
////	   String oid=oidText.getText();
////	   if (oid.equals(""))
////	   {
////	     JOptionPane.showMessageDialog(this,"请输入合适的Oid!");
////	     return;
////	   }
////	   if(oid.startsWith("."))
////	     oid=oid.substring(1);
////
////	   this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
////	   resultText.append("\r\nSnmp Walk "+IP+" Oid: "+oid+"\r\n");
////	   try
////	   {
////	     MComProxy proxy = MComProxyConnector.lookupMComProxy("Local");
////	     String[] names = new String[]
////	         {
////	         "java.lang.String",
////	         "java.util.HashMap"
////	     };
////	     HashMap environment=new HashMap();
////	     environment.put("SnmpOid",oid);
////	     environment.put("SnmpCommunityGet",get);
////	     Object[] values = new Object[]
////	         {
////	         IP,
////	         environment
////	     };
////	     ArrayList re=(ArrayList)proxy.invoke("MibBrowser", "snmpWalk", values, names);
////	     proxy.close();
////
////	     Iterator ire=re.iterator();
////	     while(ire.hasNext())
////	     {
////	       resultText.append((String)ire.next());
////	       resultText.append("\r\n");
////	     }
////	   }catch (MComProxyException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////	   catch (java.io.IOException ext)
////	   {
////	     resultText.append(ext.getMessage());
////	   }
////	   this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//
//	 }
//
//	 private void saveIP(String IP){
//	   if (snmpHost.getItemCount()==0){
//		   snmpHost.addItem(IP);
//	   }
//	   else{
//	     for (int i=0;i<snmpHost.getItemCount();i++){
//	       if (snmpHost.getItemAt(i).equals(IP))
//	         break;
//	       if (i==snmpHost.getItemCount()-1)
//	         snmpHost.addItem(IP);
//	     }
//	   }
//	 }
//
//	 private static final String SMI_IPADDRESS = "IpAddress";
//	  BorderLayout borderLayout15 = new BorderLayout();
//	  BorderLayout borderLayout17 = new BorderLayout();
//
//	  void showPopupMenu(MouseEvent e)
//	  {
//	    clearAllItem.setEnabled(false);
//	    copyItem.setEnabled(false);
//
//	    if(resultText.getLineCount() != 1)
//	    {
//	     clearAllItem.setEnabled(true);
//	    }
//	    if(resultText.getSelectedText() != null)
//	    {
//	      copyItem.setEnabled(true);
//	    }
//	    popup.show(e.getComponent(),e.getX(),e.getY());
//	  }
//	  
//	  class MibFileFilter extends FileFilter{
//			 @Override 
//		     public boolean accept(File f) {
//				 if (f.getName().endsWith("MIB") || f.getName().endsWith("mib") 
//						 || f.getName().endsWith("MY") || f.getName().endsWith("my") || f.isDirectory()) 
//					 return true; 
//				 return false; 
//		     } 
//		     @Override 
//		     public String getDescription() { 
//		    	 // TODO Auto-generated method stub 
//		    	 return "(*.MIB)"; 
//		     } 
//		}
//	  
////	  public static void main(String[]a){
////			JHWMibBrowser jhwMibBrowser = new JHWMibBrowser();
////			jhwMibBrowser.startMIB();
////	  }
}

/**
* This class describs structure of oid node.
* @version 1.0
*/
class OidNode
{
	 public String name="";
	 public String oid="";
	 public String oidStr="";
	 public String description="";
	 public String syntax="";
	 public String access="";
	 public String index="";
	 public String reference="";
	 public String status="";
	 public String toString(){
		 return this.name;
	 }
}
