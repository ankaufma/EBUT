package de.htwg_konstanz.ebus.wholesaler.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

public class DOMDatabaseInserter implements IDatabaseInserter {

	@Override
	public Map<String, List<String>> insertIntoDatabase(FileItem item, Document document) {
		ArrayList<String> updatedProducts = new ArrayList<String>();
		ArrayList<String> newProducts = new ArrayList<String>();

		Element root = document.getDocumentElement();

		// Supplier
		BOSupplier supplier = getSupplier(root);

		if (supplier == null) {
			//resultMap.get(Errors.ERRORS).add("No Valid Supplier found!");
			return null;
		}

		// find all articles
		NodeList articles = root.getElementsByTagName("ARTICLE");
		for (int i = 0; i < articles.getLength(); i++) {
			Element article = (Element) articles.item(i);

			NodeList aidList = article.getElementsByTagName("SUPPLIER_AID");
			NodeList descriptionShortList = article
					.getElementsByTagName("DESCRIPTION_SHORT");
			NodeList descriptionLongList = article
					.getElementsByTagName("DESCRIPTION_LONG");

			BOProduct product = new BOProduct();

			product.setShortDescription(descriptionShortList.item(0)
					.getFirstChild().getNodeValue());
			product.setLongDescription(descriptionLongList.item(0)
					.getFirstChild().getNodeValue());
			product.setOrderNumberCustomer(aidList.item(0).getFirstChild()
					.getNodeValue());
			product.setSupplier(supplier);
			product.setOrderNumberSupplier(aidList.item(0).getFirstChild()
					.getNodeValue());

			ProductBOA pb = ProductBOA.getInstance();

			List<BOProduct> listeBOProduct = pb.findAll();

			boolean neu = true;
			if (listeBOProduct != null) {
				for (BOProduct iterator : listeBOProduct) {
					if (iterator.getOrderNumberCustomer().equals(
							product.getOrderNumberCustomer())) {
						pb.delete(iterator);
						neu = false;
					}
				}
			}
			if (neu) {
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
		//resultMap.put(Errors.NEW_PRODUCTS, newProducts);
		//resultMap.put(Errors.UPDATED_PRODUCTS, updatedProducts);
		//return resultMap;
		return null;
	}

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
}
