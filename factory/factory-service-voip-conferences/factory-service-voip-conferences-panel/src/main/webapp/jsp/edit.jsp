<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
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

<script src="panel/js/confPanel.js" type="text/javascript"></script>
<script src="panel/js/date-functions.js" type="text/javascript"></script>
<script src="panel/js/datechooser.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="panel/css/datechooser.css">

</head>

<body id="QUALIPSO"  >

<!-- TODO //-->
<div id="container">
	<div>

<form method="POST" action="EditConference" id="settingsForm" onsubmit="selectAllInvited();">
<input type="hidden" name="idConf" value="<c:out value="${param.id}"></c:out>">
<table border=0>
<tr><td>Owner</td>
<td colspan=2>
<select name="owner">
<c:forEach items="${ownerOptions}" var="option">
<option value="<c:out value="${option}"></c:out>" <c:if test="${option==owner}">selected</c:if> ><c:out value="${option}"></c:out>
</c:forEach>
</select>
</td></tr>

<tr><td>Choose conference time mode: </td><td colspan=2><input type="radio" name="confTime" value="true" <c:if test="${confTime=='true'}">checked=checked</c:if> onclick="setPermanent();"> Permanent Conference <c:out value="${confTime}"></c:out> </td></tr>
<tr><td></td><td colspan=2><input type="radio" name="confTime" value="false" <c:if test="${confTime=='false'}">checked=checked</c:if> onclick="setTimeSlot();"> Conference with time slot</td></tr>

<tr id="startRow"><td>Conference start date: </td><td colspan=2><input type="text" name="startDate" id="startDate" value="<c:out value="${startDate}"></c:out>" >
<img id="clickStart" src="images/calendar.gif" onclick="showChooser(this, 'startDate', 'chooserSpanStart', 2000, 2020, Date.patterns.ISO8601LongPattern, true);">
<div id="chooserSpanStart" class="dateChooser select-free" style="display: none; visibility: hidden; width: 160px;"></div>
</td></tr>

<tr id="endRow"><td>Conference end date: </td><td colspan=2><input type="text" name="endDate" id="endDate" value="<c:out value="${endDate}"></c:out>">
<img id="clickEnd" src="images/calendar.gif" onclick="showChooser(this, 'endDate', 'chooserSpanEnd', 2000, 2020, Date.patterns.ISO8601LongPattern, true);">
<div id="chooserSpanEnd" class="dateChooser select-free" style="display: none; visibility: hidden; width: 160px;"></div>
</td></tr>

<tr><td>Choose conference access policy:</td><td colspan=2><input type="radio" name="confAccess" value="0" <c:if test="${confAccess=='0'}">checked="checked"</c:if> onclick="setAccessType(this);">Public Conference</td></tr>
<tr><td></td><td colspan=2><input type="radio" name="confAccess" value="1" <c:if test="${confAccess=='1'}">checked=checked</c:if> onclick="setAccessType(this);">Pin Number</td></tr>
<tr><td></td><td colspan=2><input type="radio" name="confAccess" value="2" <c:if test="${confAccess=='2'}">checked=checked</c:if> onclick="setAccessType(this);">Invited Users Only</td></tr>
<tr><td></td><td colspan=2><input type="radio" name="confAccess" value="3" <c:if test="${confAccess=='3'}">checked=checked</c:if> onclick="setAccessType(this);">Pin Number + Invited Users</td></tr>

<tr><td>Invited users list: </td><td>
<select size=7 multiple="multiple" name="invitedUsers" id="invitedUsers">
<c:forEach items="${invitedUsers}" var="option">
<option value="<c:out value="${option}"></c:out>" ><c:out value="${option}"></c:out>
</c:forEach>
</select>
</td><td><input type="submit" value="Remove Users" onclick="removeUsers();return false;" name="remUsers"></td></tr>

<tr><td>Add User: </td><td><input type="text" name="addUserName"></td><td><input type="submit" value="Add user" onclick="addUser('asd');return false;" name="addUserButton"></td></tr>
<!--
<tr><td>Add Group: </td><td><input type="text" name="addGroupName"></td><td><input type="submit" value="Add group" onclick="return false;" name="addUserButton" ></td></tr>
 -->
<tr><td>Maximum number of users: </td><td colspan=2><input type="text" name="maxUsers" value="<c:out value="${maxUsers}"></c:out>"></td></tr>
<tr id="pinRow"><td>Pin: </td><td colspan=2><input type="text" name="pin" value="<c:out value="${pin}"></c:out>"></td></tr>
<tr id="adminRow"><td>Admin Pin: </td><td colspan=2><input type="text" name="adminpin" value="<c:out value="${adminPin}"></c:out>"></td></tr>

<tr><td>Name: </td><td colspan=2><input type="text" id="confName" name="name" value="<c:out value="${name}"></c:out>"></td></tr>
<tr><td>Agenda: </td><td colspan=2><textarea rows="10" cols="40" name="agenda"><c:out value="${agenda}"></c:out></textarea></td></tr>
<tr id="recordRow"><td>Record Conference</td><td colspan=2><input type="checkbox" name="recorded" <c:if test="${confRecord=='true'}">checked=checked</c:if> ></td></tr>
<tr><td></td><td colspan=2><input type="submit" value="Save Changes"></td></tr>
</table>
</form>
<br>
<a href="index.jsp">Cancel</a>

<br><br>
	</div>
</div>
</body>
</html>