package de.htwg_konstanz.ebus.wholesaler.main.upload.database_inserter;

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
import de.htwg_konstanz.ebus.wholesaler.action.Errors;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;

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
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        dom = builder.parse(xmlfile);
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
		
		
		HashMap<String, List<String>> resultMap = initResultMap();

		ArrayList<String> updatedProducts = new ArrayList<String>();
		ArrayList<String> newProducts = new ArrayList<String>();

		Element root = dom.getDocumentElement();
		
		// Supplier
		BOSupplier supplier = getSupplier(root);
		
		if (supplier == null){
			resultMap.get(Errors.ERRORS).add("No Valid Supplier found!");
			return resultMap;
		}
		
		// find all articles
		NodeList articles = root.getElementsByTagName("ARTICLE");
		for (int i = 0; i < articles.getLength(); i++) {
			Element article = (Element) articles.item(i);
			
			NodeList aidList = article.getElementsByTagName("SUPPLIER_AID");
			NodeList descriptionShortList = article.getElementsByTagName("DESCRIPTION_SHORT");
			NodeList descriptionLongList = article.getElementsByTagName("DESCRIPTION_LONG");
			
			BOProduct product = new BOProduct();
			
			product.setShortDescription(descriptionShortList.item(0).getFirstChild().getNodeValue());
			product.setLongDescription(descriptionLongList.item(0).getFirstChild().getNodeValue());
			product.setOrderNumberCustomer(aidList.item(0).getFirstChild().getNodeValue());
			product.setSupplier(supplier);
			product.setOrderNumberSupplier(aidList.item(0).getFirstChild().getNodeValue());
			
			ProductBOA pb = ProductBOA.getInstance();

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
			if(neu) {
				pb.saveOrUpdate(product);
				newPrice(article, product);
				newProducts.add(product.getShortDescription());
			} else {
				ProductBOA.getInstance().delete(product);
				_BaseBOA.getInstance().commit();
				pb.saveOrUpdate(product);
				newPrice(article, product);
				updatedProducts.add(product.getShortDescription());
			}
		}
		resultMap.put(Errors.NEW_PRODUCTS, newProducts);
		resultMap.put(Errors.UPDATED_PRODUCTS, updatedProducts);
		return resultMap;
	}

	/**
	 * searches the specified supplier out of the database
	 * @param root node of the document
	 * @return supplier, or null if none was found
	 */
	private BOSupplier getSupplier(final Element root) {
		BOSupplier supplier = null;

		NodeList suppliers = root.getElementsByTagName("SUPPLIER_NAME");
	    Element supplierElement = (Element) suppliers.item(0);
		String supplierName = supplierElement.getFirstChild().getNodeValue();
		
		SupplierBOA sboa = SupplierBOA.getInstance();

		List<BOSupplier> listeBOSupplier = sboa.findByCompanyName(supplierName);
		if (!listeBOSupplier.isEmpty()) {
			   supplier = listeBOSupplier.get(0);
		}
		return supplier;
	}


	/**
	 * saves a prices for an article
	 * @param article element of the document
	 * @param bp the product of which prices will be saved
	 */
	private void newPrice(Element article, BOProduct bp) {
		NodeList articlePriceList = article.getElementsByTagName("ARTICLE_PRICE");
		
		for (int i = 0; i < articlePriceList.getLength(); i++) {
			
			 Element articlePriceElement = (Element) articlePriceList.item(i);
			 NodeList territoryList = articlePriceElement.getElementsByTagName("TERRITORY");

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

				NodeList taxes = articlePriceElement.getElementsByTagName("TAX");

				Double taxDouble = Double.valueOf(taxes.item(0).getFirstChild().getNodeValue());
				BigDecimal tax = BigDecimal.valueOf(taxDouble);
				    
				boPrice.setTaxrate(tax);
				String priceType = articlePriceElement.getAttribute("price_type");
				boPrice.setPricetype(priceType);

				// save the Purchase Price
				PriceBOA.getInstance().saveOrUpdate(boPrice);

				// save Sales Price with 100% gain
				BOSalesPrice salesPreis = new BOSalesPrice();
				salesPreis.setAmount(priceAmount.multiply(new BigDecimal(2)));
				salesPreis.setCountry(country);
				salesPreis.setProduct(bp);
				salesPreis.setLowerboundScaledprice(1);
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
