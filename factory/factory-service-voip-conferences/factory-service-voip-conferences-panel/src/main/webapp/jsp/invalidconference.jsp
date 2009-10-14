<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>QUALIPSO Voice Conference Configuration Panel</title>

<link rel="stylesheet" type="text/css" href="panel/css/style.css" media="all" />

<link rel="stylesheet" type="text/css" title="default" media="all" href="panel/css/qualipso/template_css.css" />
<!--[if IE 6]>
<link rel ="Stylesheet" tittle="Default"  href="panel/css/qualipso/ie6.css">
<![endif]-->
</head>

<body id="QUALIPSO"  >

<!-- TODO //-->
<div id="container">  
<!-- CABECERA //--> 
	<div id="intro"> 
		<div id="logo" class="left">
			<a href="" title="QUALIPSO Logo"><img src="panel/images/qualipso/null.gif" border="0" alt="QUALIPSO Logo" width="360" height="103" /></a>
		</div>
		
		<div id="pageHeader" class="left"> 
			
		</div>

	</div>
<!-- TODO CONTENIDO //-->
	<div id="contenido"><br><br>

Invalid conference number. Conference <c:out value="${param.id}"></c:out> does not exist.

<br><br>
Go <a href="index.jsp">back</a>.

<br><br>
	</div>
</div>
</body>
</html>