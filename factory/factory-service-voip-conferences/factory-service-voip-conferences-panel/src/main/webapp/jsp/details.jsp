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
<link rel ="Stylesheet" tittle="Default"  href="panel/css/qualipso/ie6.css">
<![endif]-->

<script src="panel/js/prototype.js" type="text/javascript"></script>
<script src="panel/js/detailsAjax.js" type="text/javascript"></script>
<script>
	onload = initialize(<c:out value="${param.id}"></c:out>);
</script>

</head>

<body id="QUALIPSO"  >

<!-- TODO //-->
<div id="container">
	<div>

<div id="ajaxStatus"></div>
<a href="?act=edit&id=<c:out value="${param.id}"></c:out>">Edit Conference</a>
<br><br>

<table>
<tr><td>Conf Number: </td><td colspan=2 id="confnoField"><c:out value="${conference.confNo}"></c:out></td></tr>
<tr><td>Owner:</td><td id="ownerField"><c:out value="${conference.owner}"></c:out></td></tr>
<tr><td>Conference time mode: </td><td colspan=2 id="isPermanentField">
	<c:choose>
		<c:when test="${confTime=='true'}">Permanent Conference</c:when>
		<c:otherwise>Conference with a time slot</c:otherwise>
	</c:choose>
</td></tr>

<tr><td>Conference start date: </td><td colspan=2 id="startTimeField"><c:out value="${ast:formatDate(conference.startDate)}"></c:out></td></tr>
<tr><td>Conference end date: </td><td colspan=2 id="endTimeField"><c:out value="${ast:formatDate(conference.endDate)}"></c:out></td></tr>

<tr><td>Conference access policy:</td><td colspan=2 id="accessPolicyField">
<c:choose>
<c:when test="${conference.accessType=='0'}">Public Conference</c:when>
<c:when test="${conference.accessType=='1'}">Pin Number</c:when>
<c:when test="${conference.accessType=='2'}">Invited Users Only</c:when>
<c:when test="${conference.accessType=='3'}">Pin Number + Invited Users</c:when>
</c:choose>
</td></tr>
<tr><td>Maximum number of users: </td><td colspan=2 id="maxUsersField"><c:out value="${conference.maxUsers}"></c:out></td></tr>

<tr><td>Pin: </td><td colspan=2 id="pinField"><c:out value="${conference.pin}"></c:out></td></tr>
<tr><td>Admin Pin: </td><td colspan=2 id="adminPinField"><c:out value="${conference.adminPin}"></c:out></td></tr>

<tr><td>Name: </td><td colspan=2 id="nameField"><c:out value="${conference.name}"></c:out></td></tr>
<tr><td>Agenda: </td><td colspan=2 id="agendaField"><c:out value="${conference.agenda}"></c:out></td></tr>
<tr><td>Is confference recorded</td><td colspan=2 id="isRecordedField">
	<c:choose>
		<c:when test="${isRecorded=='true'}">true</c:when>
		<c:otherwise>false</c:otherwise>
	</c:choose>
</td></tr>
</table>
<br><br>

Conference participants
<table id="participantsTable">
<thead>
<tr><th>Id</th><th>Username</th><th>Join Time</th><th>Is Talking?</th><th>Is Muted?</th><th>Is Banned?</th><th colspan=3>Actions</th></tr>
</thead>
<tbody>
<c:forEach items="${participants}" var="participant">
<tr>
	${participant}
	<td><c:out value="${participant.id}"></c:out></td>
	<td><c:out value="${participant.username}"></c:out></td>
	<td><c:out value="${participant.time}"></c:out></td>
	<td id="isTalking_<c:out value="${param.id}"></c:out>_<c:out value="${participant.id}"></c:out>"><c:out value="${participant.talking}"></c:out></td>
	<td id="isMuted_<c:out value="${param.id}"></c:out>_<c:out value="${participant.id}"></c:out>"><c:out value="${participant.muted}"></c:out></td>
	<td id="isBanned_<c:out value="${param.id}"></c:out>_<c:out value="${participant.id}"></c:out>"><c:out value="${participant.banned}"></c:out></td>

	<c:choose>
		<c:when test="${participant.banned=='false'}">  <td><a href="?act=ban&id=<c:out value="${param.id}"></c:out>&user=<c:out value="${participant.id}"></c:out>">Ban</a></td></c:when>
		<c:otherwise>  <td><a href="?act=unban&id=<c:out value="${param.id}"></c:out>&user=<c:out value="${participant.username}"></c:out>">Unban</a></td></c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${participant.muted=='false'}">  <td><a href="?act=mute&id=<c:out value="${param.id}"></c:out>&user=<c:out value="${participant.id}"></c:out>">Mute</a></td></c:when>
		<c:otherwise>  <td><a href="?act=unmute&id=<c:out value="${param.id}"></c:out>&user=<c:out value="${participant.id}"></c:out>">Unmute</a></td></c:otherwise>
	</c:choose>

	<td><a href="?act=kick&id=<c:out value="${param.id}"></c:out>&user=<c:out value="${participant.id}"></c:out>">Kick</a></td>
</tr>
</c:forEach>
<c:if test="${empty participants}">
 <tr><td colspan=9>No users in this conference!</td></tr>
</c:if>
</tbody>
</table>
<br>

Banned users
<table id="bannedTable">
<thead>
<tr><th>Username</th><th>Unban?</th></tr>
</thead>
<tbody>
<c:forEach items="${bannedUsers}" var="banned">
<tr>
	<td><c:out value="${banned}"></c:out></td>
	<td><a href="?act=unban&id=<c:out value="${param.id}"></c:out>&user=<c:out value="${banned}"></c:out>">Unban</a></td>
</tr>
</c:forEach>
<c:if test="${empty bannedUsers}">
 <tr><td colspan=2>No banned users!</td></tr>
</c:if>
</tbody>
</table>
<br><br>

Invited users
<table id="invitedTable">
<thead>
<tr><th>Username</th></tr>
</thead>
<tbody>
<c:forEach items="${invitedUsers}" var="invited">
<tr>
	<td><c:out value="${invited}"></c:out></td>
</tr>
</c:forEach>
<c:if test="${empty invitedUsers}">
 <tr><td>No invited users!</td></tr>
</c:if>
</tbody>
</table>
<br><br>

Lock conference: <a href="?act=lock&id=<c:out value="${param.id}"></c:out>" onClick='return lock(<c:out value="${param.id}"></c:out>);'>Lock</a><br>
Unlock conference: <a href="?act=unlock&id=<c:out value="${param.id}"></c:out>" onClick='return unlock(<c:out value="${param.id}"></c:out>);'>Unlock</a><br>
<br>
End conference: <a href="?act=end&id=<c:out value="${param.id}"></c:out>">End</a><br>
<br>
Go <a href="index.jsp">back</a>.

<br><br>
	</div>
</div>
</body>
</html>