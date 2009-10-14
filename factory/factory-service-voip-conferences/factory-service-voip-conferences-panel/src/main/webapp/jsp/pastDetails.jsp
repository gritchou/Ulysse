<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="${schema.target.namespace}" prefix="ast" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>QUALIPSO Voice Conference Configuration Panel</title>
		<link rel="stylesheet" type="text/css" href="panel/css/style.css" media="all" />
		<link rel="stylesheet" type="text/css" title="default" media="all" href="panel/css/qualipso/template_css.css" />
		<!--[if IE 6]>
			<link rel ="Stylesheet" tittle="Default" href="panel/css/qualipso/ie6.css">
		<![endif]-->
	</head>

<body id="QUALIPSO"  >

<!-- TODO //-->
<div id="container">
	<div>

<table>
<tr><td>Conf Number: </td><td colspan=2><c:out value="${conference.confNo}"></c:out></td></tr>
<tr><td>Owner:</td><td><c:out value="${conference.owner}"></c:out></td></tr>
<tr><td>Conference time mode: </td><td colspan=2>
	<c:choose>
		<c:when test="${conference.permanent=='true'}">Permanent Conference</c:when>
		<c:otherwise>Conference with time slot</c:otherwise>
	</c:choose>
</td></tr>

<tr><td>Conference start date: </td><td colspan=2><c:out value="${ast:formatDate(conference.startDate)}"></c:out></td></tr>
<tr><td>Conference end date: </td><td colspan=2><c:out value="${ast:formatDate(conference.endDate)}"></c:out></td></tr>

<tr><td>Conference access policy:</td><td colspan=2>
<c:choose>
<c:when test="${conference.accessType=='0'}">Public Conference</c:when>
<c:when test="${conference.accessType=='1'}">Pin Number</c:when>
<c:when test="${conference.accessType=='2'}">Invited Users Only</c:when>
<c:when test="${conference.accessType=='3'}">Pin Number + Invited Users</c:when>
</c:choose>
</td></tr>

<tr><td>Invited users list: </td><td>
	<c:forEach items="${invitedUsers}" var="option">
		<c:out value="${option}"></c:out><br>
	</c:forEach>
</td></tr>

<tr><td>Maximum number of users: </td><td colspan=2><c:out value="${conference.maxUsers}"></c:out></td></tr>

<tr><td>Pin: </td><td colspan=2><c:out value="${conference.pin}"></c:out></td></tr>
<tr><td>Admin Pin: </td><td colspan=2><c:out value="${conference.adminPin}"></c:out></td></tr>
<tr><td>Name: </td><td colspan=2><c:out value="${conference.name}"></c:out></td></tr>
<tr><td>Agenda: </td><td colspan=2><c:out value="${conference.agenda}"></c:out></td></tr>
<tr><td>Conference is recorded</td><td colspan=2>
	<c:choose>
		<c:when test="${conference.recorded=='true'}">yes</c:when>
		<c:otherwise>no</c:otherwise>
	</c:choose>
</td></tr>
</table>
<br>

<c:if test="${conference.recorded}">
Download conference recordings
<!--
here: <a href="<c:out value="${recordings}"></c:out>">here</a>
-->
<br>
<iframe style="width: 100%; height: 400px;" src="<c:out value="${recordings}"></c:out>" />
</c:if>
Go <a href="index.jsp">back</a>.

<br><br>
	</div>
</div>
</body>
</html>