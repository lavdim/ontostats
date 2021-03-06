package owlapi.ontostats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.xml.sax.SAXException;

import util.OntoConfig;
import dnl.utils.text.table.TextTable;

/**
 * Hello world!
 *
 */
public class App {
	private static ArrayList<OntoConfig> ontConfigArr;
		
	public static void main(String[] args) throws IOException {
		try {
			readConfFile();
		} catch (ParserConfigurationException e1) {
			e1.getMessage();
		} catch (SAXException e1) {
			e1.getMessage();
		}
		
		try {
			processStats();
		} catch (OWLOntologyCreationException e) {
			//e.getMessage();
		}
	}
	
	public static void processStats() throws IOException, OWLOntologyCreationException{
		int numberOfOntologies = ontConfigArr.size();

		String[] columnNames = { "Ontology", "Namespace", "Classes", "EquivalentClasses", "Object Properties",
				"Data Properties", "Individuals", "Expressivity", "SubProperties", "Class Disjoint", "ObjPropDomain",
				"ObjPropRange", "DataPropDomain", "DataPropRange", "InverseObjProp", "FunctionalObjProp",
				"FunctionalDataProp", "InverseFuncObtProp", "SymmtricObjProp", "TransitiveObjProp",
				"EquivalentObjProp" };

		int Classes = 0, EquivalentClasses = 0, ObjectProperties = 0, DataProperties = 0, Individuals = 0,
				SubProperties = 0, ClassDisjoint = 0, ObjPropDomain = 0, ObjPropRange = 0, DataPropDomain = 0,
				DataPropRange = 0, InverseObjProp = 0, FunctionalObjProp = 0, InverseFuncObtProp = 0,
				SymmtricObjProp = 0, TransitiveObjProp = 0, EquivalentObjProp = 0, FunctionalDataProperty = 0;
		String Expressivity = "";

		Object[][] data = new Object[numberOfOntologies][columnNames.length];

		int i = 0;
		
		for (int j = 0; j < ontConfigArr.size(); j++) {
			String Ontology = ontConfigArr.get(j).getName();

			Classes = EquivalentClasses = ObjectProperties = DataProperties = Individuals = SubProperties = ClassDisjoint = ObjPropDomain = ObjPropRange = DataPropDomain = DataPropRange = InverseObjProp = FunctionalObjProp = InverseFuncObtProp = SymmtricObjProp = TransitiveObjProp = EquivalentObjProp = FunctionalDataProperty = 0;

			String UrlString = ontConfigArr.get(j).getUrl();
			
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(UrlString));

			Classes = ontology.getClassesInSignature().size();
			EquivalentClasses = ontology.getEquivalentClassesAxioms(null).size();
			Individuals = ontology.getIndividualsInSignature().size();
			ObjectProperties = ontology.getObjectPropertiesInSignature().size();
			DataProperties = ontology.getDataPropertiesInSignature().size();
			ObjPropDomain = ontology.getObjectPropertyDomainAxioms(null).size();

			Set<OWLImportsDeclaration> imports = ontology.getImportsDeclarations();
			if (imports.size() > 0) {
				System.out.println(UrlString +" Direct Imports:");
				int count = 1;
				for (OWLImportsDeclaration imp : imports)
					System.out.println(count + ": " + imp.getIRI().toString());
				count++;
				System.out.println("End Imports");
			}
			
			DLExpressivityChecker expressivityChecker = new DLExpressivityChecker(Collections.singleton(ontology));

			Expressivity = expressivityChecker.getDescriptionLogicName();
			
			for (OWLAxiom ax : ontology.getAxioms()) {

				// if (ax.getAxiomType().toString().equals("SubClassOf"))
				// SubClassOf++;
				if (ax.getAxiomType().toString().equals("DataPropertyRange"))
					DataPropRange++;
				else if (ax.getAxiomType().toString().equals("DataPropertyDomain"))
					DataPropDomain++;
				else if (ax.getAxiomType().toString().equals("ObjectPropertyDomain"))
					ObjPropDomain++;
				else if (ax.getAxiomType().toString().equals("ObjectPropertyRange"))
					ObjPropRange++;
				// else if
				// (ax.getAxiomType().toString().equals("SubDataPropertyOf"))
				// SubDataPropertyOf++;
				else if (ax.getAxiomType().toString().equals("SubObjectPropertyOf"))
					SubProperties++;
				else if (ax.getAxiomType().toString().equals("EquivalentClasses"))
					EquivalentClasses++;
				else if (ax.getAxiomType().toString().equals("InverseFunctionalObjectProperty"))
					InverseFuncObtProp++;
				else if (ax.getAxiomType().toString().equals("DisjointClasses"))
					ClassDisjoint++;
				else if (ax.getAxiomType().toString().equals("InverseObjectProperties"))
					InverseObjProp++;
				else if (ax.getAxiomType().toString().equals("FunctionalObjectProperty"))
					FunctionalObjProp++;
				else if (ax.getAxiomType().toString().equals("TransitiveObjectProperty"))
					TransitiveObjProp++;
				else if (ax.getAxiomType().toString().equals("EquivalentObjectProperties"))
					EquivalentObjProp++;
				else if (ax.getAxiomType().toString().equals("SymmetricObjectProperty"))
					SymmtricObjProp++;
				else if (ax.getAxiomType().toString().equals("FunctionalDataProperty"))
					FunctionalDataProperty++;
			}
			
			data[i] = new Object[] { Ontology, UrlString, Classes, EquivalentClasses, ObjectProperties,
					DataProperties, Individuals, Expressivity, SubProperties, ClassDisjoint, ObjPropDomain,
					ObjPropRange, DataPropDomain, DataPropRange, InverseObjProp, FunctionalObjProp,
					FunctionalDataProperty, InverseFuncObtProp, SymmtricObjProp, TransitiveObjProp,
					EquivalentObjProp };
			i++;
		}
		
		TextTable table = new TextTable(columnNames, data);
		table.setAddRowNumbering(true);
		table.printTable();
		OutputStream output = new FileOutputStream("results.csv");
		
		table.toCsv(output);
	}
	
	/**
	 * Read an XML file with the name and url of the ontologies
	 * @return 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void readConfFile() throws ParserConfigurationException, SAXException, IOException{
		ontConfigArr = util.ConfigManager.getInstance().loadConfig();
	}
}
