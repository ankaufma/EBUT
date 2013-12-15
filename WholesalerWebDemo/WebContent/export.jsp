<%@ page import = "java.util.*" %>
<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>eBusiness Framework Demo - Login</title>
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="pragma" content="no-cache">
<link rel="stylesheet" type="text/css" href="default.css">
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="error.jsp" %>

<h1>
Two Files with <%= request.getParameter("countOfArticles") %> Articles have been Generated
and are available for you for <b>two Minutes</b>...
</h1>
<p>
	You can Download the XML-File
	<a href="http://localhost:8080/WholesalerWebDemo/<%= request.getParameter("filename") %>">heer!</a>
</p>
<p>
	Or go directly to the Web-View
	<a href="http://localhost:8080/WholesalerWebDemo/<%= request.getParameter("filename") %>.html">there!</a>
</p>
<p>
	Or you can Validate this file via W3C-Validator-Api here
	<a target="_blank" href="http://validator.w3.org/check?uri=http://www.vfl-fluorn-fussball.de/BMECat1387133107339.xml.html">here!</a>
</p>
</body>
</html>