<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.Iterator,
                 org.claros.chat.models.Contact,
                 org.jivesoftware.smack.Roster,
                 org.jivesoftware.smack.RosterEntry,
                 org.jivesoftware.smack.XMPPConnection,
                 javax.servlet.http.HttpServletRequest,
                 javax.servlet.http.HttpSession,
                 org.jivesoftware.smack.XMPPException" %>

<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="org.claros.chat.controllers.PreferencesController"/>
<%  
	response.setHeader("Expires","-1");
	response.setHeader("Pragma","no-cache");
	response.setHeader("Cache-control","no-cache");
%>
<%@ taglib uri="/WEB-INF/tld/ajax.tld" prefix="ajax" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<span id="loading" style="font-family: Arial, Helvetica, sans-serif; font-size: 14px;color: black;">Loading... Please wait.</span>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>ChatOS 1.0</title>
<script type="text/javascript" src="js/mm_functions.js"></script>
<script type="text/javascript" src="js/chatwin.js"></script>
<script type="text/javascript" src="js/chat.js"></script>
<script type="text/javascript" src="js/scroll.js"></script>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/rico.js"></script>
<script type="text/javascript" src="js/operations.js"></script>
<script type="text/javascript" src="js/cjajax.js"></script> 
<script type="text/javascript" src="js/md5.js"></script>
<script type="text/javascript" src="js/date.js"></script>
<script type="text/javascript" src="js/playa.js"></script>
<script type="text/javascript" src="js/muc.js"></script>
<script type="text/javascript" src="yui/yahoo/yahoo.js"></script>
<script type="text/javascript" src="yui/event/event.js"></script>
<script type="text/javascript" src="yui/connection/connection.js"></script>
<script type="text/javascript" src="yui/dom/dom.js" ></script>
<script type="text/javascript" src="yui/dragdrop/dragdrop.js" ></script>
<link href="css/chat.css" rel="stylesheet" type="text/css" />
<link href="css/scroll.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
function unloadHandler() {
	var url = "logout.cl";
	var callback = {
	  success: 	function(o) {},
	  failure: 	function(o) {},
	  argument: []
	}
	var request = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function loadUsers() {
	var checks = [];
	var j = 0;
	for(var i=0; i<document.userform.newUsers.length; i++)
	{
		if(document.userform.newUsers[i].checked)
		{
			checks[j] = document.userform.newUsers[i].value;
			j = j+1;
		}
	}
	//aqi es dd hay q llamar a la funcion usersHandler y pasarle el array de usuarios
	//hay q modificar la funcion, dependiendo de si recibe o no el array
	var username = document.forms['loginform'].username.value;
	usersHandler(checks, username);
}

var user;

function getArgs()
{
  passedArgs=new Array();
  search = self.location.href;
  search = search.split('?');
  if(search.length>1){
    argList = search[1];
    argList = argList.split('&');
    for(var i=0; i<argList.length; i++){
      newArg = argList[i];
      newArg = argList[i].split('=');
      passedArgs[unescape(newArg[0])] = unescape(newArg[1]);
    }
  }
}

function login()
{
	getArgs();
	var user = (passedArgs['jid'])?passedArgs['jid']:'';  
	if (user)
	{
		selectJabber();
		var jid = document.getElementById('jid');
		var pass = document.getElementById('pass');
		jid.value = user;
		pass.value = user;
		claroslogin();
	}
}

</script>
</head>

<body onload="preloadImages();disableLoading();initLogin();callListener();statusTimerIncrement();statusListener();login();" onresize="initToolbar()" onclick="outInfoWin()" onunload="unloadHandler()" onmousemove="arrangeStatus();">

<div id="contacts">
<table width="189" height="430" border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td width="189" height="34" background="img/bg/contact_win_top.gif" id="contactshandle">&nbsp;</td>
  </tr>
  <tr>
	<td valign="top" background="img/bg/contact_win_bg.gif" height="337">
		<table width="189" border="0" cellspacing="0" cellpadding="0" height="337">
		  <tr>
			<td width="19">&nbsp;</td>
			<td width="154">
				<ajax:AjaxDiv id="contactsscroll" url="contacts.cl" timeout="8" handler="contactsHandler">
					<br /><br /><br /><div align="center"><img src="img/loading.gif" width="32" height="32"></div>
				</ajax:AjaxDiv>
			</td>			
		  </tr>
		</table>
	</td>
  </tr>
  <tr>
	<td width="189" height="34" background="img/bg/contact_win_bottom.gif">&nbsp;</td>
  </tr>
</table>
</div>

<div id="infowin" style="visibility:hidden" onmouseover="overInfoWin()" >
 <table width="199" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td height="16" background="img/bg/info_bg_top.gif">&nbsp;</td>
	</tr>
	<tr>
	  <td height="60" valign="middle" background="img/bg/info_bg.gif">
		<table width="179" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
		  	<td align="center">
		  		<img src="img/avatar_load.gif" id="avatar" /><br/>
		  	</td>
		  </tr>
		  <tr>
			<td>
				<span id="infotexttitle" style="overflow: none;margin-top: 4px;">&nbsp;</span>
				<p id="infotextstatus">&nbsp;</p>
			</td>
		  </tr>
		  <tr>
			<td><img src="img/spacer.gif" width="10" height="8"/></td>
		  </tr>
		  <tr>
			<td align="center"><a href="javascript:openChat()"></a></td>
		  </tr>
		</table>
	  </td>
	</tr>
	<tr>
	  <td height="16" background="img/bg/info_bg_bottom.gif">&nbsp;</td>
	</tr>
 </table>
</div>



 <div id="toolbar" style="height:58px;display:none;">
   <table width="100%" height="58" border="0" cellpadding="0" cellspacing="0" align="center">
     <tr>
       <td width="1%" height="58"><img src="img/bg/toolbar_left.gif" width="23" height="58"/></td>
       <td width="33%" background="img/bg/toolbar_bg.gif">
		  <div id="consolescroll">
			<div id="consoleText" style="overflow:hidden; height:50px; width:95%">&nbsp;</div>
		  </div>
	   </td>
       <td width="1%" height="58" bgcolor="green"><img src="img/bg/toolbar_seperator.gif" width="17" height="58"/></td>
       <td width="33%" align="center" valign="middle" background="img/bg/toolbar_bg.gif" nowrap="nowrap" bgcolor="blue">

		<table border="0" cellpadding="2" cellspacing="0" id="statusTable">
		  <tr>
		    <td rowspan="2" align="left" nowrap>
		    	<img src="img/avatar.png" height="48" width="48" id="avatarme" class="avatarMe" onmouseover="this.className='avatarMeHover';" onmouseout="this.className='avatarMe'" style="margin-right: 10px"/>
		    </td>
			<td nowrap="nowrap" valign="bottom"><strong style="color:#272827">Welcome <span id="fullName">&nbsp;</span></strong></td>
		  </tr>
		  <tr>
			<td nowrap="nowrap" valign="top"><strong style="color:#272827">Status : </strong><span id="statusnow">Available</span> &nbsp;<img src="img/indicators/green.gif" border="0" id="statusindic"/>&nbsp; <a href="javascript:changeStatus()">Change</a></td>
		  </tr>
		</table>


	   </td>
       <td width="1%" height="58"><img src="img/bg/toolbar_seperator.gif" width="17" height="58" /></td>
       <td valign="top" background="img/bg/toolbar_bg.gif"><table border="0" width="260" align="right" cellpadding="0" cellspacing="0" style="margin-top:20px">
         <tr>
           <td width="13" nowrap="nowrap"><img src="img/ico/logout.gif" width="20" height="20" /></td>
           <td nowrap="nowrap" width="50"><a href="javascript:logout();" style="color:#0c4363;margin-left:7px;">logout</a></td>
           <td width="20" nowrap="nowrap"><img src="img/ico/preferences.gif" width="22" height="20" /></td>
           <td nowrap="nowrap" width="65"><a href="javascript:preferences()" style="color:#0c4363;margin-left:7px;">preferences</a></td>
           <td width="19" nowrap="nowrap"><img src="img/ico/claros.gif" width="21" height="20" /></td>
           <td nowrap="nowrap" width="50"><a href="http://www.claros.org" target="_blank" style="color:#0c4363;margin-left:7px;">claros.org</a></td>
         </tr>
       </table></td>
       <td width="1%" height="58"><img src="img/bg/toolbar_right.gif" width="23" height="58" /></td>
     </tr>
   </table>
 </div>
  


<div id="chatWin" class="chatWin" style="display:none;" onmousedown="moveOnTop(this);">
  <table width="234" height="264" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="26" class="handle" ondblclick="tidyDiv(this);"><div id="handle" style="outline:none; height:26px;cursor:move"><table width="234" border="0" cellspacing="0" cellpadding="0" height="26">
        <tr>
          <td width="12">&nbsp;</td>
          <td width="14"><img src="img/indicators/green.gif" width="10" height="10" id="statusindicator"/></td>
          <td width="187"><div id="chatWinTitleBar">&nbsp;</div></td>
          <td width="21"><img src="img/spacer.gif" width="17" height="17" onclick="hideChatWin(this.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode);" style="cursor: pointer;"></td>
        </tr>
      </table></div></td>
    </tr>    
    <tr>
      <td height="140" valign="top" background="img/bg/chatwin_bg.gif"><table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr>
          <td height="140" valign="top" id="chatWinText">
		  	<div id="chatScroll">
				<div id="myText" style="outline:hidden;">&nbsp;</div>
			</div>
		  </td>			  
        </tr>        
      </table></td>
    </tr>    
    <tr>
      <td height="60" align="left" valign="top" background="img/bg/chatwin_bottom.gif"><label>
       	<textarea name="chatWinMsg" wrap="physical" id="chatWinMsg" onkeyDown="return(handleKeys(event, this));"></textarea>
      </label></td>
    </tr>
    <tr>
      <td height="26" align="left" valign="top" background="img/bg/chatwin_bottom_link.GIF">
        <a href="javascript:inviteUsers();">Invite</a>        
      </td>
    </tr>
  </table>
</div>


<div id="users" class="curtain">
	&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br /><form id="userform" name="userform">
	<table width="400" border="0" cellspacing="0" cellpadding="4" align="center"  height="315">
	  <tr>
		<td colspan="2">
			<strong style="font-size:18px;color:#999999">Invite user/s:</strong><hr size="1" />
		</td>
	  </tr>
	  
    <tr>
    <td>
   
    <%
    	XMPPConnection conn = (XMPPConnection)request.getSession().getAttribute("conn");
    	    	
    	System.out.println("ESTA ES LA CONEXION Q RECIBE EL JSP ========> " + conn);
    	
    	if (conn != null) { %>
		 <table>
		<%	Roster roster = conn.getRoster();
			roster.setSubscriptionMode(Roster.SUBSCRIPTION_ACCEPT_ALL);
			ArrayList contacts = new ArrayList();
			RosterEntry en = null;
			Contact cn = null;
			for (Iterator i=roster.getEntries(); i.hasNext(); ) {
				en = (RosterEntry)i.next();
				cn = new Contact();
				cn.setName(en.getName()); 
				String user = en.getUser() + "&" + cn.getName();%>
				<tr><td>
				  <input type="checkbox" id="newUsers" class="checkbox" value="<%= user%>"><%= cn.getName()%>
				</td></tr>				
		<%	} %>
		  </table>			
	<%	}%>	  
	  </td>
	  </tr>
	  <tr>
		<td colspan="2" align="center" style="font-size:16px; font-weight:bold">
			<hr size="1"/>
			<br />
	
			[ <a href="javascript:loadUsers();" style="color:#EBEBEB">Save</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="javascript:closeCurtain('users');" style="color:#EBEBEB">Cancel</a> ]
		</td>
	  </tr>
	</table>
	</form>
</div>

<div id="login" style="display:none">
	<div id="logincenter" style="width:428px; height:281px; outline:none; position:absolute; left: 153px; top: 76px;">
		<table width="428" height="281" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
			<td height="29" background="img/bg/login_top.gif" id="loginhandle">&nbsp;</td>
		  </tr>
		  <tr>
			<td height="234" valign="top" background="img/bg/login_bg.gif"><form id="loginform" name="loginform" method="post" action="" onsubmit="return false">
			    <table width="427" border="0" align="center" cellpadding="5" cellspacing="0">
		          <tr>
		          	<td>&nbsp;</td>
		            <td style="color:red;font-weight: bold" id="loginResult">&nbsp;</td>
		          </tr>
		          <tr>
		            <td width="122"><div align="right">User : </div></td>
		            <td width="285"><label>
		              <input type="text" id='jid' name="username" tabindex="1" onkeydown="return(loginCatchEnter(event))" style="width: 200px;border: 1px solid #A3C6E8;height: 14px;font-family: Arial, Helvetica, sans-serif;font-size:11px;"/>
		            </label></td>
		          </tr>
		          <tr>
		            <td><div align="right">Password : </div></td>
		            <td><label>
		              <input type="password" id='pass' name="password" tabindex="2" onkeydown="return(loginCatchEnter(event))" style="width: 200px;border: 1px solid #A3C6E8;height: 14px;font-family: Arial, Helvetica, sans-serif;font-size:11px;"/>
		            </label></td>
		          </tr>
		          <tr>
		            <td><div align="right">Server : </div></td>
		            <td><label>
		              <input type="text" name="server" tabindex="3" onkeydown="return(loginCatchEnter(event))" value="Jabber" readonly="readonly" style="width: 200px;border: 1px solid #A3C6E8;height: 14px;font-family: Arial, Helvetica, sans-serif;font-size:11px;"/>
		            </label></td>
		          </tr>
		          <tr>
		            <td>&nbsp;</td>
		            <td><a href="javascript:claroslogin();"><img src="img/buttons/login.gif" border="0" /></a>&nbsp;&nbsp;
		            <img src="img/loading_mini.gif" width="16" height="16" id="loginprogress" style="display: none"/>
		            <span id="logintext" style="color: #333333; font-style: italic"></span>
		            </td>
		          </tr>
		          
		          <tr>
		            <td colspan="2"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		              <tr>
		                <td width="10">&nbsp;</td>
		                <td width="203" align="left"><a href="#" onmouseout="if (!jabberClicked) MM_swapImgRestore()" onmouseover="MM_swapImage('jabber','','img/buttons/jabber_clicked.gif',1)" onclick="selectJabber();"><img src="img/buttons/jabber.gif" name="jabber" width="188" height="83" border="0" id="jabber" /></a></td>
		              </tr>
		            </table></td>
		          </tr>
		        </table>
		          </form>
			  </td>
		  </tr>
		  <tr>
			<td height="18" background="img/bg/login_bottom.png">&nbsp;</td>
		  </tr>
		</table>
	</div>
</div>


<div id="changeStatus" class="curtain">
	&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br /><form id="statusform">
	<table width="400 border="0" cellspacing="0" cellpadding="4" align="center"  height="315">
	  <tr>
		<td colspan="2">
			<strong style="font-size:18px;color:#999999">Change Status</strong><hr size="1" />
		</td>
	  </tr>
	  <tr>
		<td>Current Status : </td>
		<td id="userStatus"></td>
	  </tr>
	  <tr>
		<td>New Status : </td>
		<td>
			<select name="newstatus">
			  <option value="available">Online</option>
			  <option value="away">Away</option>
			  <option value="disturb">Do Not Disturb</option>
			</select>
		</td>
	  </tr>
	  <tr>
		<td>Status Message : </td>
		<td>
			<textarea name="newstatusmsg" cols="30" rows="4" style="width:200px; height:40px;"></textarea>
		</td>
	  </tr>
	  <tr>
		<td colspan="2" align="center" style="font-size:16px; font-weight:bold">
			<hr size="1"/>
			<br />
	
			[ <a href="javascript:saveStatus();" style="color:#EBEBEB">Save</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="javascript:closeCurtain('changeStatus');" style="color:#EBEBEB">Cancel</a> ]
		</td>
	  </tr>
	</table>
	</form>
</div>


<div id="logout" class="curtain">
	&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />
	<table width="400 border="0" cellspacing="0" cellpadding="4" align="center"  height="315">
	  <tr>
		<td colspan="2">
			<strong style="font-size:18px;color:#999999">Logout Confirmation!</strong><hr size="1" />
		</td>
	  </tr>
	  <tr>
		<td width="135"><img src="img/buttons/exclamation.gif" /></td>
		<td>Are you sure you want to end your session?</td>
	  </tr>
	  <tr>
		<td colspan="2" align="center" style="font-size:16px; font-weight:bold">
			<hr size="1"/>
			<br />
			[ <a href="javascript:doLogout();" style="color:#EBEBEB">Yes</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="javascript:closeCurtain('logout');" style="color:#EBEBEB">No</a> ]
		</td>
	  </tr>
	</table>
</div>

<div id="preferences" class="curtain">
	&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />&nbsp;<br /><form id="preferencesForm">
	<table width="400 border="0" cellspacing="0" cellpadding="4" align="center"  height="315">
	  <tr>
		<td colspan="2">
			<strong style="font-size:18px;color:#999999">Preferences</strong><hr size="1" />
		</td>
	  </tr>
	  <tr>
		<td>Name Surname : </td>
		<td><input name="preffullname" id="preffullname" type="text" style="border:solid 1px #666666; width: 150px;"/></td>
	  </tr>
	  <tr>
		<td>Set Status Away After : </td>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td rowspan="2" width="70" nowrap="nowrap">
						<span id="mins">15</span> mins
					</td>
					<td>
						<a href="javascript:prefArrUp();"><img src="img/buttons/arr_up.gif" border="0" width="17" height="17"/></a>
					</td>
				</tr>
				<tr>
					<td>
						<a href="javascript:prefArrDown();"><img src="img/buttons/arr_down.gif" border="0" width="17" height="17"/></a>
					</td>
				</tr>
			</table>
		</td>
	  </tr>
	  <tr>
		<td>Effects : </td>
		<td><input name="anims" id="anims" type="radio" value="on" checked="checked"/> On <input name="anims" id="anims" type="radio" value="off" /> Off</td>
	  </tr>
	  <tr>
		<td>Sound Alert : </td>
		<td><input name="sndalert" id="sndalert" type="radio" value="on" checked="checked"/> On <input name="sndalert" id="sndalert" type="radio" value="off" /> Off</td>
	  </tr>
	  <tr>
		<td colspan="2" align="center" style="font-size:16px; font-weight:bold">
			<hr size="1"/>
			<br />
	 
			[ <a href="javascript:savePreferences();" style="color:#EBEBEB">Save</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="javascript:closeCurtain('preferences');" style="color:#EBEBEB">Cancel</a> ]					
		</td>
	  </tr>
	</table>
  </form>
</div>

<div id="askWin">
	<table width="200" border="0" align="center" cellpadding="5" cellspacing="0">
	  <tr>
	    <td>User <strong>Satılmış Salakova</strong> wants to add you to his/her buddies list. What would you like to do? </td>
	  </tr>
	  <tr>
	    <td>&nbsp;</td>
	  </tr>
	  <tr>
	    <td align="center"><a href="javascript:allowAskOp();"><img src="img/buttons/allow.gif" width="72" height="21" border="0" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:denyAskOp();"><img src="img/buttons/deny.gif" border="0" /></a></td>
	  </tr>
	</table>
</div>

<div id="player" style="visibility: hidden;width: 1px;height: 1px;">
<object
	classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
	width="1"
	height="1"
	codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab"
	id="playa"
	style="position:absolute">
	<param name="movie" value="resources/playa2.swf">
	<param name="quality" value="high">
	<param name="flashvars" value="autoplay=false&playlist=resources/appelsap.xml">
	<param name="loop" value="false">
	<param name="loopsong" value="false">
	<embed
		name="playa"
		src="resources/playa2.swf"
		width="1"
		height="1"
		quality="high"
		swLiveConnect="true"
		flashvars="autoplay=false&playlist=resources/appelsap.xml"
		pluginspage="http://www.macromedia.com/go/flashplayer/"
		loop="false" 
		style="position:absolute">
	</embed>
</object>
</div>
</body>
</html>
