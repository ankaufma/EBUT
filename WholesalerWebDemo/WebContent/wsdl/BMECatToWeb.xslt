<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="5.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<html>
			<head>
				<title>eBusiness Framework Demo - Artcile List</title>
				<meta http-equiv="cache-control" content="no-cache"/>
				<meta http-equiv="pragma" content="no-cache"/>
				<link rel="stylesheet" type="text/css" href="default.css"/>
				</head>
			<body>
				<h1>Product Catalog</h1>
				<table class="dataTable">
					<thead>
						<tr>
							<th><b>Order No.</b></th>
							<th><b>Title</b></th>
							<th><b>Description</b></th>
							<th><b>Price Net</b></th>
							<th><b>Price gros</b></th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="/BMECAT/T_NEW_CATALOG/ARTICLE">
						<tr>
							<td><xsl:value-of select="SUPPLIER_AID"/></td>
							<td><xsl:value-of select="ARTICLE_DETAILS/DESCRIPTION_SHORT"/></td>
							<td><xsl:value-of select="ARTICLE_DETAILS/DESCRIPTION_LONG"/></td>
							<td><xsl:value-of select="ARTICLE_PRICE_DETAILS/ARTICLE_PRICE/PRICE_AMOUNT"/></td>
							<td><xsl:value-of select="ARTICLE_PRICE_DETAILS/ARTICLE_PRICE/PRICE_AMOUNT * (number(ARTICLE_PRICE_DETAILS/ARTICLE_PRICE/TAX ) + 1)"/></td>							
						</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
