<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Import Result List</title>
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="error.jsp" %>
	<center>
		<form method ="post" name = "import" action="import.jsp"> 
			<input type="submit" name="Submit" value="Back to import"/>
		</form>
	</center>
	<center>
		<form method ="post" name = "import" action="welcome.jsp"> 
			<input type="submit" name="Submit" value="Back to welcome"/>
		</form>
	</center>
<c:if test="${ not empty sessionScope.errorList }">
	<c:remove var="errorList" scope="session"/>
</c:if>
</body>
</html>