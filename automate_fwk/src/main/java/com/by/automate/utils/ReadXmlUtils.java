package com.by.automate.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ReadXmlUtils {
	/**
	 * Get value with element for the attribute.
	 * 
	 * @param element
	 * @param attribute
	 * @return
	 */
	public static String getAttributeValue(Element element, String attribute) {
		Attribute att = element.attribute(attribute);
		return att.getText();
	}

	public static Element getCurrentNode(Element node, String childNodeStr,
			String value) {
		try {
			int result = node.selectNodes(
					".//" + childNodeStr + "[@name='" + value + "']").size();
			if (result > 1) {
				throw new Exception("There are two identical page names [name="
						+ node.attribute("name").getText() + "].");
			}
			return node = (Element) node.selectNodes(
					".//" + childNodeStr + "[@name='" + value + "']").get(0);
		} catch (Exception e) {
			throw new IndexOutOfBoundsException("Find node "
					+ node.attribute("name").getText() + " attribute " + value
					+ " is error.");
		}

	}

	public static String getAttributeValue(Element node, String childNodeStr,
			String value, String attribute) {
		try {
			node = getCurrentNode(node, childNodeStr, value);
			return getAttributeValue(node, attribute);
		} catch (NullPointerException e) {
			throw new NullPointerException("Get element ["
					+ node.attribute("name").getText()
					+ "] is not found with attribute[" + attribute + "]");
		}
	}

	public static Element getSubElement(Document doc, String tagName) {
		return (Element) doc.selectSingleNode("//" + tagName);
	}

	public static Document merge(Document doc, Document docs, String xmlFile) {

		Element elSub1 = getSubElement(doc, "pages");
		Element elSub2 = getSubElement(docs, "pages");
		elSub1.appendContent(elSub2);
		writeXML(doc, xmlFile);

		return doc;
	}

	public static void writeXML(Document doc, String fileName) {
		try {

			OutputFormat format = OutputFormat.createPrettyPrint();
			// format.setEncoding("UTF-8");

			XMLWriter writer = new XMLWriter(new FileWriter(fileName), format);
			writer.write(doc);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static List<Element> findElement(Element element, String className){
		return  element.selectNodes("//class[@name='" + className + "']");
	}


	/**
	 * Create A Document
	 */
	public static Document createXML() {
		
		return DocumentHelper.createDocument();
	}
	
	public static Element createRootElement(Document document, String element){
		Element root = DocumentHelper.createElement(element);
		document.setRootElement(root);
		return root;
	}

	public static Element addAttribute(Element element, String arg0, String arg1) {

		return element.addAttribute(arg0, arg1);
	}

	public static Element addElement(Element parent, String chlidElementName) {

		return parent.addElement(chlidElementName);
	}

	public static Element addText(Element currentElement, String text) {

		return currentElement.addText(text);
	}

	public static void writeXml(Document document , String xmlName) {
		try {
            OutputFormat format = OutputFormat.createPrettyPrint();  
            format.setIndentSize(4);  
            format.setNewlines(true);  
            format.setTrimText(true);  
            format.setPadText(true);  
			XMLWriter xmlWrite = new XMLWriter(new FileOutputStream(xmlName), format);
			xmlWrite.write(document);
			xmlWrite.close();

		} catch (IOException e) {
		}
	}
	
	 public static Document getDocument(String XmlPath) {

	        try {
	            File file = new File(XmlPath);
	            if (!file.exists()) {
	                if (!file.exists()) {
	                    throw new RuntimeException("Test data set file: [" + file + "] "
	                            + "could not be found, please make sure you set property correctly.");
	                }
	            }
	            SAXReader reader = new SAXReader();
	            return reader.read(new FileInputStream(XmlPath));
	        } catch (DocumentException e) {
	            e.printStackTrace();
	        } catch (FileNotFoundException e) {
	            System.out.println(XmlPath + " can not found , please check config of the sut file.");
	        }

	        return null;
	    }


}
