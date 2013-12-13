package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;



public class Controller {
	public static final String XML = "http://www.w3.org/2001/XMLSchema";
	
	private File xmlfile;
	private IDatabaseInserter inserter;
	private File schemafile;
	Map<String, List<String>> tmp;
	HashMap<String, List<String>> results;
	
	/**
	 * Constructor
	 * @param xmlfile
	 * @param inserter
	 * @param schemafile
	 */
	public Controller(File xmlfile, IDatabaseInserter inserter, File schemafile)
	{
		System.out.println("Controller");
		results = initResultMap();
		this.xmlfile = xmlfile;
		this.inserter = inserter;
		this.schemafile = schemafile;
		System.out.println("successfully controller initiation");
	}
	/**
	 * start of validation and insert
	 * @return
	 */
	public Map<String, List<String>> execute()
	{
		if(!validate()) return results;
		insert();
		return results;
	}
	/**
	 * validates the uploaded xml file
	 * @return
	 */
	private boolean validate()
	{	
		try 
		{		
			Schema schema = SchemaFactory.newInstance(XML).newSchema(schemafile);
			Validator validator = schema.newValidator();
			MyErrorHandler errHandler = new MyErrorHandler();
			validator.setErrorHandler(errHandler);
			validator.validate(new StreamSource(xmlfile));
			if(!errHandler.hasErrors())
			{
				return true;
			} 
			else 
			{
				results.get(Errors.NOT_VALID).add("XML is not valid!");
				results.get(Errors.NOT_VALID).add(errHandler.getErrors());
			}
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
			results.get(Errors.NOT_WELLFORMED).add("XML is not wellformed!");
			results.get(Errors.NOT_WELLFORMED).add(e.toString());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			results.get(Errors.NOT_WELLFORMED).add("XML is not wellformed!");
			results.get(Errors.NOT_WELLFORMED).add(e.toString());
		}
		return false;
	}

	/**
	 * starts the import of the data to the database 
	 */
	private void insert()
	{
	
	Map<String, List<String>> tmp = inserter.insertIntoDatabase(xmlfile);
	
	if(!tmp.get(Errors.ERRORS).isEmpty())
	{
	    results.put(Errors.ERRORS, tmp.get(Errors.ERRORS));
	} 
	else 
	{
	    results.put(Errors.NEW_PRODUCTS, tmp.get(Errors.NEW_PRODUCTS));
	    results.put(Errors.UPDATED_PRODUCTS, tmp.get(Errors.UPDATED_PRODUCTS));
	}
	}
	
	/**
	 * initializes the resulting map of validateAndImport() method
	 * @return map, containing errors and lists of products
	 */
	private HashMap<String, List<String>> initResultMap() {
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		result.put(Errors.ERRORS, new ArrayList<String>());
		result.put(Errors.UPDATED_PRODUCTS, new ArrayList<String>());
		result.put(Errors.NEW_PRODUCTS, new ArrayList<String>());
		result.put(Errors.NOT_VALID, new ArrayList<String>());
		result.put(Errors.NOT_WELLFORMED, new ArrayList<String>());
		return result;
	}
}
