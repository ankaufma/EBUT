package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCountry;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOPurchasePrice;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSalesPrice;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSupplier;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.CountryBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.PriceBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.SupplierBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa._BaseBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.vo.Country;


public class DOMDatabaseInserter implements IDatabaseInserter
{
	/**
	 * Constructor
	 */
	public DOMDatabaseInserter (){
		super();
	}
	
	/**
	 * saves a document to the database
	 * @param dom the document which will be saved
	 * @return map, containing error messages, new and updated product lists
	 */
	public Map<String, List<String>> insertIntoDatabase(File xmlfile)
	{
        Document dom = null;
		try {
			//Document Builder Factory initialization
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			System.out.println("Factory successful created");
			//Create Document Builder
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        System.out.println("Document Builder created");
	        //Start Parsing
	        dom = builder.parse(xmlfile);
	        System.out.println("Dom successfully parsed");
	        System.out.println("XML File exists? answer: "+xmlfile.exists());
		} 
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//map with updated and new products
		//start initialize Result Map
		HashMap<String, List<String>> resultMap = initResultMap();
		System.out.println("Result Map created");
		// lists for the output
		ArrayList<String> updatedProducts = new ArrayList<String>();
		ArrayList<String> newProducts = new ArrayList<String>();
		//get dom root element
		Element root = dom.getDocumentElement();
		System.out.println("Root: "+ root.getNodeValue());
		
		// Supplier
		//get supplier from dom root
		BOSupplier supplier = getSupplier(root);
		
		if (supplier == null){
			resultMap.get(Errors.ERRORS).add("No Valid Supplier found!");
			return resultMap;
		}
		
		// routine to find all articles
		NodeList articles = root.getElementsByTagName("ARTICLE");
		for (int i = 0; i < articles.getLength(); i++) {
			Element article = (Element) articles.item(i);
			//get the supported elements by tag name
			BOProduct product = new BOProduct();
			
			NodeList descriptionShortList = article.getElementsByTagName("DESCRIPTION_SHORT");
			product.setShortDescription(descriptionShortList.item(0).getFirstChild().getNodeValue());
			if(article.getElementsByTagName("DESCRIPTION_LONG").getLength() > 0) {
				NodeList descriptionLongList = article.getElementsByTagName("DESCRIPTION_LONG");
				product.setLongDescription(descriptionLongList.item(0).getFirstChild().getNodeValue());
			}
			if(article.getElementsByTagName("MANUFACTURER_NAME").getLength() > 0) {
				NodeList manu = article.getElementsByTagName("MANUFACTURER_NAME");
				product.setManufacturer(manu.item(0).getFirstChild().getNodeValue());
			}
			NodeList aidList = article.getElementsByTagName("SUPPLIER_AID");
			product.setOrderNumberCustomer(aidList.item(0).getFirstChild().getNodeValue());
			product.setSupplier(supplier);
			product.setOrderNumberSupplier(aidList.item(0).getFirstChild().getNodeValue());
			
			ProductBOA pb = ProductBOA.getInstance();
			//find all products
			List<BOProduct> listeBOProduct = pb.findAll();
			
			boolean neu = true;
			if (listeBOProduct != null) {
				for (BOProduct iterator : listeBOProduct) {
					if (iterator.getOrderNumberCustomer().equals(product.getOrderNumberCustomer())) {
						pb.delete(iterator);
						neu = false;
					}
				}
			}
			// if there are new products
			if(neu) {
				//save it to db
				pb.saveOrUpdate(product);
				//calculate the sales price with 100% gain
				newPrice(article, product);
				//add short description
				newProducts.add(product.getShortDescription());
			} else {
				//if the product still exists --> update the product on db and calculate the sales price
				ProductBOA.getInstance().delete(product);
				_BaseBOA.getInstance().commit();
				pb.saveOrUpdate(product);
				newPrice(article, product);
				updatedProducts.add(product.getShortDescription());
			}
		}
		resultMap.put(Errors.NEW_PRODUCTS, newProducts);
		resultMap.put(Errors.UPDATED_PRODUCTS, updatedProducts);
		// return the resulat map with errors, new products and updated products
		return resultMap;
	}

	/**
	 * searches the specified supplier out of the database
	 * @param root node of the document
	 * @return supplier, or null if none was found
	 */
	private BOSupplier getSupplier(final Element root) {
		BOSupplier supplier = null;
		System.out.println("NodeList Suppliers");
		NodeList suppliers = root.getElementsByTagName("SUPPLIER_NAME");
		
		System.out.println("NodeList: "+suppliers.item(0));
	    Element supplierElement = (Element) suppliers.item(0);
		String supplierName = supplierElement.getFirstChild().getNodeValue();
		System.out.println("Supplier Name: "+supplierName);
		SupplierBOA sboa = SupplierBOA.getInstance();

		List<BOSupplier> listeBOSupplier = sboa.findByCompanyName(supplierName);
		if (!listeBOSupplier.isEmpty()) {
			   supplier = listeBOSupplier.get(0);
		}
		return supplier;
	}


	/**
	 * saves prices for an article
	 * @param article element of the document
	 * @param bp the product of which prices will be saved
	 */
	private void newPrice(Element article, BOProduct bp) {
		NodeList articlePriceList = article.getElementsByTagName("ARTICLE_PRICE");
		
		for (int i = 0; i < articlePriceList.getLength(); i++) {
			
			 Element articlePriceElement = (Element) articlePriceList.item(i);
			 NodeList territoryList = articlePriceElement.getElementsByTagName("TERRITORY");

			 if(territoryList.getLength() > 0) {
			 for (int j = 0; j < territoryList.getLength(); j++) {
				BOPurchasePrice boPrice = new BOPurchasePrice();
				BOCountry country = CountryBOA.getInstance().findCountry(territoryList.item(j).getTextContent());
				boPrice.setCountry(country);
				boPrice.setProduct(bp);
				
				NodeList articlePriceAmountList = articlePriceElement.getElementsByTagName("PRICE_AMOUNT");
				Double priceAmountDouble = Double.valueOf(articlePriceAmountList.item(0).getFirstChild().getNodeValue());
				BigDecimal priceAmount = BigDecimal.valueOf(priceAmountDouble);
				boPrice.setAmount(priceAmount);
				boPrice.setLowerboundScaledprice(1);

				BigDecimal tax = null;
				if(articlePriceElement.getElementsByTagName("TAX").getLength() > 0) {
					System.out.println("tax vorhaden");
					NodeList taxes = articlePriceElement.getElementsByTagName("TAX");

					Double taxDouble = Double.valueOf(taxes.item(0).getFirstChild().getNodeValue());
					tax = BigDecimal.valueOf(taxDouble);
				    
					boPrice.setTaxrate(tax);
				} else {
					System.out.println("tax nicht vorhaden");
					tax = BigDecimal.valueOf(Double.valueOf("0.1900"));
				}
				
				String priceType = articlePriceElement.getAttribute("price_type");
				boPrice.setPricetype(priceType);

				// save the purchase price
				PriceBOA.getInstance().saveOrUpdate(boPrice);

				// save sales price with 100% gain
				BOSalesPrice salesPreis = new BOSalesPrice();
				salesPreis.setAmount(priceAmount.multiply(new BigDecimal(2)));
				salesPreis.setCountry(country);
				salesPreis.setProduct(bp);
				salesPreis.setLowerboundScaledprice(1);
				salesPreis.setTaxrate(tax);
				salesPreis.setPricetype(priceType);
				PriceBOA.getInstance().saveOrUpdate(salesPreis);
			 }
			 } else {
				 BOPurchasePrice boPrice = new BOPurchasePrice();
				 boPrice.setCountry(new BOCountry(new Country("DE")));
				 boPrice.setProduct(bp);
				 NodeList articlePriceAmountList = articlePriceElement.getElementsByTagName("PRICE_AMOUNT");
				 Double priceAmountDouble = Double.valueOf(articlePriceAmountList.item(0).getFirstChild().getNodeValue());
				 BigDecimal priceAmount = BigDecimal.valueOf(priceAmountDouble);
				 boPrice.setAmount(priceAmount);
				 boPrice.setLowerboundScaledprice(1);
				 BigDecimal tax = null;
					if(articlePriceElement.getElementsByTagName("TAX").getLength() > 0) {
						System.out.println("tax vorhaden");
						NodeList taxes = articlePriceElement.getElementsByTagName("TAX");

						Double taxDouble = Double.valueOf(taxes.item(0).getFirstChild().getNodeValue());
						tax = BigDecimal.valueOf(taxDouble);
					    
						boPrice.setTaxrate(tax);
					} else {
						System.out.println("tax nicht vorhaden");
						tax = BigDecimal.valueOf(Double.valueOf(0.1900));
						System.out.println(tax.toString());
					}
					String priceType = articlePriceElement.getAttribute("price_type");
					boPrice.setPricetype(priceType);
					System.out.println("Mhh... 1");
					// save the purchase price
					//PriceBOA.getInstance().saveOrUpdate(boPrice);
					System.out.println("Mhh... 2");
					// save sales price with 100% gain
					BOSalesPrice salesPreis = new BOSalesPrice();
					salesPreis.setAmount(priceAmount.multiply(new BigDecimal(2)));
					salesPreis.setCountry(new BOCountry(new Country("DE")));
					salesPreis.setProduct(bp);
					salesPreis.setLowerboundScaledprice(1);
					System.out.println(tax.toString());
					salesPreis.setTaxrate(tax);
					salesPreis.setPricetype(priceType);
					PriceBOA.getInstance().saveOrUpdate(salesPreis);
			 }
		}
	}
	
	/**
	 * initializes the resulting map
	 * @return initialized map
	 */
	private HashMap<String, List<String>> initResultMap() {
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		result.put(Errors.ERRORS, new ArrayList<String>());
        result.put(Errors.NOT_VALID, new ArrayList<String>());
        result.put(Errors.NOT_WELLFORMED, new ArrayList<String>());
		result.put(Errors.UPDATED_PRODUCTS, new ArrayList<String>());
		result.put(Errors.NEW_PRODUCTS, new ArrayList<String>());
		return result;
	}
}
