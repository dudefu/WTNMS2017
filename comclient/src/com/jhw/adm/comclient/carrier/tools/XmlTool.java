
package com.jhw.adm.comclient.carrier.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * This class contains XmlManage realization for XML tool.
 * <p>Copyright Falconet Software(c) 2004</p>
 * @author liuxw
 * @version 1.0
 */
public class XmlTool
{
  private Document document=null;
  private org.xml.sax.XMLFilter xmlFilter=null;
  private String xmlEncoding="GB2312";
  //private String xmlEncoding="ISO-8859-1";

  /**
   * set XML Filter
   * @param xmlFilter Define the XML filter
   */
  public void setXMLFilter(org.xml.sax.XMLFilter xmlFilter)
  {
    this.xmlFilter=xmlFilter;

  }

  /**
   * read XML from Stream
   * @param inputStream input stream interface
   * @return true or false.
   */
  public boolean read(InputStream inputStream) throws JDOMException
  {
      SAXBuilder builder = null;
      builder = new SAXBuilder();
      if (xmlFilter!=null)
        builder.setXMLFilter(xmlFilter);

      builder.setExpandEntities(true);
      if (inputStream==null)
        return false;
      try
      {
        document = builder.build(inputStream);
      }catch(java.io.IOException e)
      {
        return false;
      }
      xmlFilter=null;
      return true;
  }

  /**
   * read XML from file
   * @param strFile xml file name
   * @return true or false.
   */
  public boolean read(String strFile) throws JDOMException
  {
    SAXBuilder builder = null;
    builder = new SAXBuilder();
    if (xmlFilter!=null)
      builder.setXMLFilter(xmlFilter);
    builder.setExpandEntities(true);
    if (document!=null)
      document=null;

    try
    {
      document = builder.build(strFile);
    }catch(java.io.IOException e)
    {
      return false;
    }
    xmlFilter=null;
    return true;

  }

  /**
   * write XML to stream
   * @param outputStream Xml output stream interface
   */
  public void write(OutputStream outputStream) throws IOException
  {
      XMLOutputter outputter = new XMLOutputter();
      Format format=Format.getPrettyFormat();
      format.setEncoding(xmlEncoding);
      outputter.setFormat(format);
      if (document!=null)
        outputter.output(document, outputStream);
  }

  /**
   * write XML to file
   * @param strFile Xml file name
   */
  public void write(String strFile) throws IOException
  {
    FileOutputStream fileWriter=new FileOutputStream(strFile);
    XMLOutputter outputter = new XMLOutputter();
    Format format=Format.getPrettyFormat();
    format.setEncoding(xmlEncoding);
    outputter.setFormat(format);
    outputter.output(document, fileWriter);
    fileWriter.close();
  }

  /**
   * Set encoding type for xml
   * @param encoding Encoding type.
   */
  public void setXmlEncoding(String encoding)
  {
    xmlEncoding=encoding;
  }

  /**
   * Create XML document in memory
   * @param strRoot Root name in document
   */
  public void createDocument(String strRoot)
  {
    if (document!=null)
      document=null;

    Element root=new Element(strRoot);
    document=new Document(root);
  }

  /**
   * Get root element for current document.
   * @return
   */
  public Element getRootElement()
  {
    if (document!=null)
    {
      return document.getRootElement();
    }
    else
      return null;
  }

  /**
   *
   * @param element
   */
  public void setRootElement(Element element)
  {
    if (document!=null)
    {
      document.setRootElement(element);
    }
  }

}