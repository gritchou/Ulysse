<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="${schema.target.namespace}" prefix="ast" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
		pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>QUALIPSO VoIP Conference Configuration Panel</title>
		<link rel="stylesheet" type="text/css" href="panel/css/style.css" media="all" />
		<link rel="stylesheet" type="text/css" title="default" media="all" href="panel/css/qualipso/template_csas.css" />
		<!--[if IE 6]>
			<link rel ="Stylesheet" tittle="Default"  href="panel/css/qualipso/iea6.css">
		<![endif]-->
	</head>

	<body id="QUALIPSO"  >
	<!-- TODO //-->
	<div id="container">
		<div>
			<table>
			<!--
			<tr><td colspan="9"><p style="text-align: right;"><a href="?act=logout">Logout</a></p></td></tr>
			-->
			<tr><td colspan="9"><p style="text-align: left;"><strong><u>Conferences I own:</u></strong></td></tr>
			
			<tr><th>conf. no</th><th>Name</th><th>Start Date</th><th>End Date</th><th>Owner</th><th colspan=4>Actions</th></tr>
			<c:forEach items="${myConferences}" var="conference">
			<tr>
				<td><c:out value="${conference.confNo}"></c:out></td>
				<td><c:out value="${conference.name}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.startDate)}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.endDate)}"></c:out></td>
				<td><c:out value="${conference.owner}"></c:out></td>
				<td><a href="?act=detail&id=<c:out value="${conference.confNo}"></c:out>">View</a></td>
				<td><a href="?act=edit&id=<c:out value="${conference.confNo}"></c:out>">Edit</a></td>
				<td><a href="?act=del&id=<c:out value="${conference.confNo}"></c:out>">Remove</a></td>
				<td><a href="?act=end&id=<c:out value="${conference.confNo}"></c:out>">End</a></td>
			</tr>
			</c:forEach>
			
			<tr><td colspan="9"><p style="text-align: left;"><strong><u>Conferences I was invited to:</u></strong></td></tr>
			<tr><th>conf. no</th><th>Name</th><th>Start Date</th><th>End Date</th><th>Owner</th><th colspan=4>Actions</th></tr>
			<c:forEach items="${invitedToConferences}" var="conference">	
			<tr>
				<td><c:out value="${conference.confNo}"></c:out></td>
				<td><c:out value="${conference.name}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.startDate)}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.endDate)}"></c:out></td>
				<td><c:out value="${conference.owner}"></c:out></td>
				<td><a href="?act=detail&id=<c:out value="${conference.confNo}"></c:out>">View</a></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			</c:forEach>
			
			<tr><td colspan="9"><p style="text-align: left;"><strong><u>Public Conferences:</u></strong></td></tr>
			<tr><th>conf. no</th><th>Name</th><th>Start Date</th><th>End Date</th><th>Owner</th><th colspan=4>Actions</th></tr>
			<c:forEach items="${publicConferences}" var="conference">
			<tr>
				<td><c:out value="${conference.confNo}"></c:out></td>
				<td><c:out value="${conference.name}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.startDate)}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.endDate)}"></c:out></td>
				<td><c:out value="${conference.owner}"></c:out></td>
				<td><a href="?act=detail&id=<c:out value="${conference.confNo}"></c:out>">View</a></td>
				<td>-</td>
				<td>-</td>
				<td>-</td>
			</tr>
			</c:forEach>
			
			<tr><td colspan="9"><p style="text-align: left;"><strong><u>Past Conferences:</u></strong></td></tr>
			<tr><th>conf. no</th><th>Name</th><th>Start Date</th><th>End Date</th><th>Owner</th><th colspan=4>Actions</th></tr>
			<c:forEach items="${pastConferences}" var="conference">
			<tr>
				<td><c:out value="${conference.confNo}"></c:out></td>
				<td><c:out value="${conference.name}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.startDate)}"></c:out></td>
				<td><c:out value="${ast:formatDate(conference.endDate)}"></c:out></td>
				<td><c:out value="${conference.owner}"></c:out></td>
				<td><a href="?act=pastDetail&id=<c:out value="${conference.confNo}"></c:out>">View</a></td>
				<td><a href="?act=pastDel&id=<c:out value="${conference.confNo}"></c:out>">Remove</a></td>
			</tr>
			</c:forEach>
			
			<tr><td colspan="9"><p style="text-align: left;"><strong><u>Links:</u></strong></td></tr>
			<tr style="text-align: center;">
				<td colspan="4"><a href="?act=add">add a conference</a></td>
				<td colspan="5"><a href="http://voip.syros.eurodyn.com/openmeetings/">go to Videoconference</a></td>
			</tr>
			
			</table>
		</div>
	</div>
	</body>
</html>
