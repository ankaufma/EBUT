<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Import</title>
</head>
<body>
	<center>
		<form name = "importform" method="post" action="<%= response.encodeURL("ImportAction") %>" enctype="multipart/form-data">
			Select file to upload: <input type="file" name="importFile" /> <br />
			<input type="hidden" name="action" value="importDOMProducts"> <input
				type="submit" name="Submit" value="Upload File">
		</form>
	</center>
</body>
</html>