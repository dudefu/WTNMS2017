package com.jhw.adm.server.test;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;
import java.sql.*;

class DataBackup implements ActionListener
{     JFrame f = null;
    JLabel label = null;
    JTextArea textarea = null;
    JFileChooser fileChooser = null;
    @Resource(mappedName = "java:/adm2000DS")
	DataSource myDb;
    public DataBackup()
    {

        f = new JFrame("FileChooser Example");
        Container contentPane = f.getContentPane();
        textarea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textarea);
        scrollPane.setPreferredSize(new Dimension(350,300));


        JPanel panel = new JPanel();
        JButton b1 = new JButton("恢复数据");
        b1.addActionListener(this);
        JButton b2 = new JButton("备份数据");
        b2.addActionListener(this);
        panel.add(b1);
        panel.add(b2);

        label = new JLabel(" ",JLabel.CENTER);

        contentPane.add(label,BorderLayout.NORTH);
        contentPane.add(scrollPane,BorderLayout.CENTER);
        contentPane.add(panel,BorderLayout.SOUTH);

        f.pack();
        f.setVisible(true);

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
     //windows效果
     try
     {
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
     }
     catch(Exception e1)
     {
      System.out.println("Look and Feel Exception");
      System.exit(0);
     }
        File file = null;
        int result;
        fileChooser = new JFileChooser("d:\\");
        fileChooser.addChoosableFileFilter(new JAVAFileFilter("bak"));

        //恢复数据库操作
        if (e.getActionCommand().equals("恢复数据"))
        {
            fileChooser.setApproveButtonText("确定");
            fileChooser.setDialogTitle("打开文件");
            result = fileChooser.showOpenDialog(f);

            textarea.setText("");

            if (result == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();
            }
            else if(result == JFileChooser.CANCEL_OPTION)
            {
            }
            /***************执行事件*******************/
            //在这里写恢复数据库事件

            try
                {
                   Class.forName("oracle.jdbc.driver.OracleDriver");
                }
                catch(ClassNotFoundException error)//驱动加载失败
                {
                 System.err.println("驱动加载失败");
                }
                
                //连接到数据库
                Connection conStudent=null;
				

                try
                {
                	conStudent = DriverManager.getConnection("jdbc:oracle:thin:@192.168.12.193:1521:admserver","admserver","123456");

                 Statement cmdStudent = conStudent.createStatement();

                 cmdStudent.execute("Restore Database HomeMIS from Disk='"+file.getPath()+"'");
                 if(conStudent != null)
                 {//关闭数据库连接
                    cmdStudent.close();
                    conStudent.close();
                 }
                }
                catch(SQLException sqlerr)
                {
                 System.out.println("Error:"+sqlerr.toString());
                }


        }

        //备份数据库操作
        if (e.getActionCommand().equals("备份数据"))
        {
            result = fileChooser.showSaveDialog(f);
            file = null;
            String fileName;

            if (result == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();


                String fileName1 = file.getName();
                String filePath = file.getPath();
                int index = fileName1.lastIndexOf('.');
                if (index > 0)
                {
                 String extension = fileName1.substring(index+1).toLowerCase();

                    if(!extension.equals("bak"))
                    {
                     filePath = filePath + ".bak";
                    }
                }
                if (index < 0)
                {
                 filePath = filePath + ".bak";
                }
                /***************执行事件*******************/
                 //在这里写备份数据库事件
                 //装入JDBC驱动
                try
                {
                   Class.forName("oracle.jdbc.driver.OracleDriver");
                }
                catch(ClassNotFoundException error)//驱动加载失败
                {
                 System.err.println("驱动加载失败");
                }
                //连接到数据库
                Connection conStudent=null;
				

                try
                {
                 conStudent = DriverManager.getConnection("jdbc:oracle:thin:@192.168.12.193:1521:admserver","admserver","123456");
                 Statement cmdStudent = conStudent.createStatement();
                 cmdStudent.execute("Backup Database HomeMIS To Disk='"+filePath+"'");
                 if(conStudent != null)
                 {//关闭数据库连接
                    cmdStudent.close();
                    conStudent.close();
                 }
                }
                catch(SQLException sqlerr)
                {
                 System.out.println("Error:"+sqlerr.toString());
                }
				}

            
            else if(result == JFileChooser.CANCEL_OPTION)
            {
            }

        }
    }
    public static void main(String[] args)
    {
        new DataBackup();
    }

} //过滤文件
class JAVAFileFilter extends FileFilter
{     String ext;

    public JAVAFileFilter(String ext)
    {
        this.ext = ext;
    }

    public boolean accept(File file)
    {
        if (file.isDirectory())
            return true;

        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');

        if (index > 0 && index < fileName.length()-1) {
            String extension = fileName.substring(index+1).toLowerCase();
            if (extension.equals(ext))
                return true;
        }
        return false;
    }

    public String getDescription(){
        if (ext.equals("bak"))
            return "Data Bakeup File (*.bak)";
        return "";
    }

    
} 
