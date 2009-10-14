<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>QUALIPSO Voice Conference Configuration Panel</title>
		
		<link rel="stylesheet" type="text/css" href="panel/css/style.css" media="all" />
		<link rel="stylesheet" type="text/css" href="panel/css/qualipso/template_css.css" title="default" media="all" />
		<!--[if IE 6]>
			<link rel ="Stylesheet" tittle="Default"  href="panel/css/qualipso/ie6.css">
		<![endif]-->
		
		<script src="panel/js/confPanel.js" type="text/javascript"></script>
		<script src="panel/js/date-functions.js" type="text/javascript"></script>
		<script src="panel/js/datechooser.js" type="text/javascript"></script>
		<link href="panel/css/datechooser.css" rel="stylesheet" type="text/css" >
	</head>

	<body id="QUALIPSO"  >
		<div id="container">
			<div>
				<form method="POST" action="AddConference" id="settingsForm" onsubmit="selectAllInvited();">
					<table border=0>
						<tr><td>Owner</td>
						<td colspan=2>
						<select name="owner">
						<c:forEach items="${ownerOptions}" var="option">
						<option value="<c:out value="${option}"></c:out>"><c:out value="${option}"></c:out>
						</c:forEach>
						</select>
						</td></tr>
						
						<tr><td>Permanent Conference</td><td colspan=2>
						<input type="radio" name="confTime" value="permanent" onclick="setPermanent();">
						</td></tr>
						<tr><td>Conference with time slot</td><td colspan=2>
						<input type="radio" name="confTime" value="timeslot" checked="checked" onclick="setTimeSlot();">
						</td></tr>
						
						<tr id="startRow"><td>Conference start date: </td><td colspan=2>
						<input type="text" name="startDate" id="startDate" value="<c:out value="${startDate}"></c:out>">
						<img id="clickStart" src="images/calendar.gif" onclick="showChooser(this, 'startDate', 'chooserSpanStart', 2000, 2020, Date.patterns.ISO8601LongPattern, true);">
						<div id="chooserSpanStart" class="dateChooser select-free" style="display: none; visibility: hidden; width: 160px;"></div>
						</td></tr>
						
						<tr id="endRow"><td>Conference end date: </td><td colspan=2>
						<input type="text" name="endDate" id="endDate" value="<c:out value="${endDate}"></c:out>">
						<img id="clickEnd" src="images/calendar.gif" onclick="showChooser(this, 'endDate', 'chooserSpanEnd', 2000, 2020, Date.patterns.ISO8601LongPattern, true);">
						<div id="chooserSpanEnd" class="dateChooser select-free" style="display: none; visibility: hidden; width: 160px;"></div>
						</td></tr>
						
						<tr><td>Choose conference access policy:</td><td colspan=2>
						<input type="radio" name="confAccess" value="0" checked="checked" onclick="setAccessType(this);">Public Conference</td></tr>
						<tr><td></td><td colspan=2><input type="radio" name="confAccess" value="1" onclick="setAccessType(this);">Pin Number</td></tr>
						<tr><td></td><td colspan=2><input type="radio" name="confAccess" value="2" onclick="setAccessType(this);">Invited Users Only</td></tr>
						<tr><td></td><td colspan=2><input type="radio" name="confAccess" value="3" onclick="setAccessType(this);">Pin Number + Invited Users</td></tr>
						
						<tr><td>Invited users list: </td><td><select size=7 multiple="multiple" name="invitedUsers" id="invitedUsers">
						</select></td><td><input type="submit" value="Remove Users" onclick="removeUsers();return false;" name="remUsers"></td></tr>
						
						<tr><td>Add User: </td><td><input type="text" name="addUserName"></td><td><input type="submit" value="Add user" onclick="addUser('asd');return false;" name="addUserButton"></td></tr>
						<!--
						<tr><td>Add Group: </td><td><input type="text" name="addGroupName"></td><td><input type="submit" value="Add group" onclick="return false;" name="addGroupButton"></td></tr>
						-->
						<tr><td>Maximum number of users: </td><td colspan=2><input type="text" name="maxUsers" value="<c:out value="${defaultMaxUsers}"></c:out>"></td></tr>
						<tr id="pinRow"><td>Pin: </td><td colspan=2><input type="text" name="pin"></td></tr>
						<tr id="adminRow"><td>Admin Pin: </td><td colspan=2><input type="text" name="adminpin"></td></tr>
						<tr><td>Name: </td><td colspan=2><input id="confName" type="text" name="name"></td></tr>
						<tr><td>Agenda: </td><td colspan=2>
						<textarea rows="10" cols="40" name="agenda"></textarea></td></tr>
						<tr id="recordRow"><td>Record Conference</td><td colspan=2>
						<input type="checkbox" name="recorded"></td></tr>
						<tr><td></td><td colspan=2><input type="submit" value="Add conference"></td></tr>
					</table>
				</form>
				<br />
				<a href="index.jsp">Cancel</a>
				<br /><br />
			</div>
		</div>
	</body>
</html>