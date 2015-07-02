package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 *
 */
public class ConfigManager {

	static ConfigManager manager;
	static ArrayList<OntoConfig> ontoConfigArr;
	SAXParser parser;

	public static ConfigManager getInstance() {

		if (manager == null) {
			manager = new ConfigManager();
			ontoConfigArr = new ArrayList<OntoConfig>();
		}
		return manager;
	}

	public ArrayList<OntoConfig> loadConfig () throws ParserConfigurationException, SAXException, IOException {
		String dir = System.getProperty("user.dir");
		File configFile = new File(dir + "/Ontologies.xml");
		if(configFile.isFile()==false){
			System.out.println("Please especify the configuration file");
			System.exit(0);
		}
		Document document = null;
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(configFile);
		doc.getDocumentElement().normalize();
		 
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	 
		NodeList nList = doc.getElementsByTagName("Ontology");
	 
		System.out.println("----------------------------" + nList.getLength());
	 
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) nNode;
				OntoConfig ontoConfigInst = new OntoConfig(e.getElementsByTagName("name").item(0).getTextContent(), e.getElementsByTagName("url").item(0).getTextContent());
				ontoConfigArr.add(ontoConfigInst);
			}
		}

		return ontoConfigArr;
	}

}
