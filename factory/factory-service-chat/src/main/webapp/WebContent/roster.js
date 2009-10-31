function RosterGroup(name) {
  this.name = htmlEnc(name);
  this.users = new Array();
  this.onlUserCount = 0;
  this.messagesPending = 0;
}

function RosterUserAdd2Group(group) {
  this.groups = this.groups.concat(group);
}

function RosterUser(jid,subscription,groups,name) {

  this.fulljid = jid;
  this.jid = cutResource(jid) || 'unknown';
  this.jid = this.jid.toLowerCase(); // jids are case insensitive

  this.subscription = subscription || 'none';
  this.groups = groups || [''];

  if (name)
    this.name = name;
  else if (this.jid == JABBERSERVER)
    this.name = "System";
  else if ((this.jid.indexOf('@') != -1) && this.jid.substring(this.jid.indexOf('@')+1) == JABBERSERVER) // we found a local user
    this.name = this.jid.substring(0,jid.indexOf('@'));
  else
    this.name = this.jid;

  this.name = htmlEnc(this.name);

  // initialise defaults
  this.status = (this.subscription == 'from' || this.subscription == 'none') ? 'stalker' : 'unavailable';
  this.statusMsg = null;
  this.lastsrc = null;
  this.messages = new Array();
  this.chatmsgs = new Array();
  this.chatW = null; // chat window

  // methods
  this.add2Group = RosterUserAdd2Group;

}

function getElFromArrByProp(arr,prop,str) {
  for (var i=0; i<arr.length; i++) {
    if (arr[i][prop] == str)
      return arr[i];
  }
  return null;
}

function getRosterGroupByName(groupName) {
  return getElFromArrByProp(this.groups,"name",groupName);
}

function getRosterUserByJID(jid) {
  return getElFromArrByProp(this.users,"jid",jid.toLowerCase());
}

function RosterUpdateStyleIE() {
  if(!is.ie)
    return;
  this.rosterW.getElementById("roster").style.width = this.rosterW.body.clientWidth;
}

function RosterGetUserIcons(from) {
  var images = new Array();
  
  for (var i=0; i<this.groups.length; i++) {
    var img = this.rosterW.images[from+"/"+this.groups[i].name];
    if (img) {
      images = images.concat(img);
      continue; // skip this group
    }
  }
  return images;
}

function RosterToggleHide() {
  this.usersHidden = !this.usersHidden;
  this.print();
  return;
}
	
function RosterToggleGrp(name) {
  var el = this.rosterW.getElementById(name);
  if (el.className == 'hidden') {
    el.className = 'rosterGroup';
    this.hiddenGroups[name] = false;
    this.rosterW.images[name+"Img"].src = grp_open_img.src;
  } else {
    el.className = 'hidden';
    this.hiddenGroups[name] = true;
    this.rosterW.images[name+"Img"].src = grp_close_img.src;
  }
  this.updateStyleIE();
}

function RosterOpenMessage(jid) {
  var user = this.getUserByJID(jid);
  var wName = makeWindowName(user.jid); 

  if (user.messages.length > 0 && (!user.mW || user.mW.closed)) // display messages
    user.mW = open('message.html?jid='+escape(jid),"mw"+wName,'width=360,height=270,dependent=yes,resizable=yes');
  else if (!user.sW || user.sW.closed) // open send dialog
    user.sW = open("send.html?jid="+escape(jid),"sw"+wName,'width=320,height=200,dependent=yes,resizable=yes');
  return false;
}

function RosterOpenChat(jid) {

	var iframejid = "iframe" + jid;
parent.top.OpenConvTab(jid, true);

var user = this.getUserByJID(jid);

/*
if (window.document.getElementById(iframejid) == null)
{
	parent.top.OpenConvTab(jid);
}
*/    
/*  var user = this.getUserByJID(jid);

  if (!user)
    return;

  if (user.messages.length > 0 && (!user.mW || user.mW.closed)) // display messages
    this.openMessage(jid);
		
  if (!user.chatW || user.chatW.closed)
  {
    user.chatW = open("chat.html?jid="+escape(jid),"chatW"+makeWindowName(user.jid),"width=320,height=390,resizable=yes");    
  } 
  else if (user.chatW.popMsgs)
    user.chatW.popMsgs();*/
    
}

function RosterCleanUp() {
  for (var i=0; i<this.users.length; i++) {
    if (this.users[i].roster)
      this.users[i].roster.cleanUp();
    if (this.users[i].sW)
      this.users[i].sW.close();
    if (this.users[i].mW)
      this.users[i].mW.close();
    if (this.users[i].chatW)
      this.users[i].chatW.close();
    if (this.users[i].histW)
      this.users[i].histW.close();
  }
}

function RosterUpdateGroupForUser(user) {
  for (var j=0; j<user.groups.length; j++) {
    if (user.groups.length > 1 && user.groups[j] == '')
      continue;
    var groupName = (user.groups[j] == '') ? "Unfiled" : user.groups[j];
    var group = this.getGroupByName(groupName);
    if(group == null) {
      group = new RosterGroup(groupName);
      this.groups = this.groups.concat(group);
    }
    group.users = group.users.concat(user);
  }
}
	
function RosterUpdateGroups() {
  this.groups = new Array();
  for (var i=0; i<this.users.length; i++)
    this.updateGroupsForUser(this.users[i]);
}

function RosterUserAdd(user) {
  this.users = this.users.concat(user);
	
  // add to groups
  this.updateGroupsForUser(user);
  return user;
}

function RosterRemoveUser(user) {
  var uLen = this.users.length;
  for (var i=0; i<uLen; i++) {
    if (user == this.users[i]) {
      this.users = this.users.slice(0,i).concat(this.users.slice(i+1,uLen));
      break;
    }
  }
  this.updateGroups();
}

function RosterGetGroupchats() {
  var groupchats = new Array();
  for (var i=0; i<this.users.length; i++)
    if (this.users[i].roster)
      groupchats[groupchats.length] = this.users[i].jid+'/'+this.users[i].roster.nick;
  return groupchats;
}
	
function Roster(items,targetW) {
  this.users = new Array();
  this.groups = new Array();
  this.hiddenGroups = new Array();
  this.name = 'Roster';

  this.rosterW = targetW;
	
  /* object's methods */
  this.print = printRoster;
  this.getGroupByName = getRosterGroupByName;
  this.getUserByJID = getRosterUserByJID;
  this.addUser = RosterUserAdd;
  this.removeUser = RosterRemoveUser;
  this.updateGroupsForUser = RosterUpdateGroupForUser;
  this.updateGroups = RosterUpdateGroups;
  this.toggleGrp = RosterToggleGrp;
  this.updateStyleIE = RosterUpdateStyleIE;
  this.toggleHide = RosterToggleHide;
  this.getUserIcons = RosterGetUserIcons;
  this.openMessage = RosterOpenMessage;
  this.openChat = RosterOpenChat;
  this.cleanUp = RosterCleanUp;
  this.getGroupchats = RosterGetGroupchats;
 
  /* setup groups */
  if (!items)
    return;
  for (var i=0;i<items.length;i++) {
    /* if (items[i].jid.indexOf("@") == -1) */ // no user - must be a transport
    if (typeof(items.item(i).getAttribute('jid')) == 'undefined')
      continue;
    var name = items.item(i).getAttribute('name') || cutResource(items.item(i).getAttribute('jid'));
    var groups = new Array('');
    for (var j=0;j<items.item(i).childNodes.length;j++)
      if (items.item(i).childNodes.item(j).nodeName == 'group')
	groups = groups.concat(items.item(i).childNodes.item(j).firstChild.nodeValue);
    this.addUser(new RosterUser(items.item(i).getAttribute('jid'),items.item(i).getAttribute('subscription'),groups,name));
  }
}

function rosterSort(a,b) {
//   if (typeof(a.name) != 'string' || typeof(b.name) != 'string')
//     return 0;
  return (a.name.toLowerCase()<b.name.toLowerCase())?-1:1;
}

function printRoster() {
	
  /* update user count for groups */
  for (var i=0; i<this.groups.length; i++) {
    this.groups[i].onlUserCount = 0;
    this.groups[i].messagesPending = 0;
    for (var j=0; j<this.groups[i].users.length; j++) {
      if (this.groups[i].users[j].status != 'unavailable' && this.groups[i].users[j].status != 'stalker')
        this.groups[i].onlUserCount++;
      if (this.groups[i].users[j].lastsrc)
        this.groups[i].messagesPending++;
    }
  }

  this.groups = this.groups.sort(rosterSort);

  var A = new Array();

	/* ***
	 * loop rostergroups 
	 */
  for (var i=0; i<this.groups.length; i++) {

    var rosterGroupHeadClass = (this.usersHidden && 
				this.groups[i].onlUserCount == 0 && 
				this.groups[i].messagesPending == 0 && 
				this.groups[i].name != "Gateways") 
      ? 'rosterGroupHeaderHidden':'rosterGroupHeader';
    A[A.length] = "<div id='";
		A[A.length] = this.groups[i].name;
		A[A.length] = "Head' class='";
		A[A.length] = rosterGroupHeadClass;
		A[A.length] = "' onClick='toggleGrp(\"";
		A[A.length] = this.groups[i].name;
		A[A.length] = "\");'><nobr>";
    var toggleImg = (this.hiddenGroups[this.groups[i].name])?'images/group_close.gif':'images/group_open.gif';
    A[A.length] = "<img src='";
		A[A.length] = toggleImg;
		A[A.length] ="' name='";
		A[A.length] = this.groups[i].name;
		A[A.length] = "Img'> ";
    A[A.length] = this.groups[i].name;
		A[A.length] = " (<span id='";
		A[A.length] = this.groups[i].name;
		A[A.length] = "On'>";
		A[A.length] = this.groups[i].onlUserCount;
		A[A.length] = "</span>/";
		A[A.length] = this.groups[i].users.length;
		A[A.length] = ")";
    A[A.length] = "</nobr></div>";
    var rosterGroupClass = (
			    (this.usersHidden && this.groups[i].onlUserCount == 0 && 
			     this.groups[i].messagesPending == 0 && 
			     this.groups[i].name != "Gateways") 
			    || this.hiddenGroups[this.groups[i].name])
      ? 'hidden':'rosterGroup';

    A[A.length] =  "<div id='";
		A[A.length] = this.groups[i].name;
		A[A.length] = "' class='";
		A[A.length] = rosterGroupClass;
		A[A.length] = "'>";
    
    this.groups[i].users = this.groups[i].users.sort(rosterSort);

		/* ***
		 * loop users in rostergroup 
		 */
    for (var j=0; j<this.groups[i].users.length; j++) {
      var user = this.groups[i].users[j];

      var rosterUserClass = (this.usersHidden && 
			     (user.status == 'unavailable' || 
			      user.status == 'stalker') && 
			     !user.lastsrc && 
			     this.groups[i].name != "Gateways") 
	? "hidden":"rosterUser";

      A[A.length] = "<div id=\"";
			A[A.length] = htmlEnc(user.jid);
			A[A.length] = "/";
			A[A.length] = this.groups[i].name;
			A[A.length] = "Entry\" class=\"";
			A[A.length] = rosterUserClass;
			A[A.length] = "\" onClick=\"return userClicked(this,'";
			A[A.length] = htmlEnc(user.jid);
			A[A.length] = "');\" title=\"";
			A[A.length] = user.name;
			if (user.realjid) {
				A[A.length] = "&#10;JID: ";
				A[A.length] = htmlEnc(user.realjid);
			} else {
				A[A.length] = "&#10;JID: ";
				A[A.length] = htmlEnc(user.jid);
			}
			A[A.length] = "&#10;";
			A[A.length] = "Status";
			A[A.length] = ": ";
			A[A.length] = user.status;
      if (user.statusMsg) {
        A[A.length] = "&#10;";
				A[A.length] = "Message";
				A[A.length] = ": ";
				A[A.length] = htmlEnc(user.statusMsg);
			}
			if ((user.messages.length + user.chatmsgs.length) > 0) {
				A[A.length] = "&#10;";
				A[A.length] = ""+(user.messages.length + user.chatmsgs.length)+" message(s) pending";
			}
      A[A.length] = "\">";
      var userImg = (user.lastsrc) ? messageImg : eval(user.status + "Led");
      A[A.length] = "<nobr><img src=\"";
			A[A.length] = userImg.src;
			A[A.length] = "\" name=\"";
			A[A.length] = htmlEnc(user.jid);
			A[A.length] = "/";
			A[A.length] = this.groups[i].name;
			A[A.length] = "\" width='16' height='16' border='0' align='left'>";
      A[A.length] = "<div><span class=\"nickName\">";
			A[A.length] = user.name;
			A[A.length] = "</span>";

      if (user.statusMsg) {
        A[A.length] = "<br clear=all><nobr><span class=\"statusMsg\">";
				A[A.length] = htmlEnc(user.statusMsg);
				A[A.length] = "</span></nobr>";
			}
      A[A.length] =  "</div></nobr></div>";  
    } /* END inner loop */
    A[A.length] =  "</div>";
  }

  this.rosterW.getElementById("roster").innerHTML = A.join('');
  this.updateStyleIE();
    
}


/***********************************************************************
 * GROUPCHAT ROSTER
 *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */
function GCRosterSort(a,b) {
  return (a.name.toLowerCase()<b.name.toLowerCase())?-1:1;
}

function GroupchatRosterPrint() {

  var A = new Array();
  
  this.groups = this.groups.sort(GCRosterSort);

  /* ***
   * loop rostergroups 
   */
  for (var i=0; i<this.groups.length; i++) {
    var rosterGroupHeadClass = (this.groups[i].users.length == 0) ? 'rosterGroupHeaderHidden':'rosterGroupHeader';

    A[A.length] = "<div id='";
		A[A.length] = this.groups[i].name;
		A[A.length] = "Head' class='";
		A[A.length] = rosterGroupHeadClass;
		A[A.length] = "'><nobr>&nbsp;";
    A[A.length] = this.groups[i].users.length;
		A[A.length] = " ";
		A[A.length] = this.groups[i].name;
    A[A.length] = "</nobr></div>";
    A[A.length] = "<div id='";
		A[A.length] = this.groups[i].name;
		A[A.length] = "' class='rosterGroup'>";
    
    this.groups[i].users = this.groups[i].users.sort(rosterSort);

		/* ***
		 * loop users in rostergroup 
		 */
    for (var j=0; j<this.groups[i].users.length; j++) {
      var user = this.groups[i].users[j];
      var rosterUserClass = (this.usersHidden && 
			     (user.status == 'unavailable' || 
			      user.status == 'stalker') && 
			     !user.lastsrc) 
	? "hidden":"rosterUser";
      
      A[A.length] = "<div id=\"";
			A[A.length] = htmlEnc(user.jid);
			A[A.length] = "/";
			A[A.length] = this.groups[i].name;
			A[A.length] = "Entry\" class=\"";
			A[A.length] = rosterUserClass;
			A[A.length] = "\" onClick=\"return userClicked(this,'";
			A[A.length] = htmlEnc(user.jid).replace(/\'/g,"\\\'")+"');\" title=\"";
			A[A.length] = user.name;
			if (user.realjid) {
				A[A.length] = "&#10;JID: ";
				A[A.length] = htmlEnc(user.realjid);
			} else {
				A[A.length] = "&#10;JID: ";
				A[A.length] = htmlEnc(user.jid);
			}
			A[A.length] = "&#10;";
			A[A.length] = "Status";
			A[A.length] = ": ";
			A[A.length] = user.status;
      if (user.statusMsg) {
        A[A.length] = "&#10;";
				A[A.length] = "Message";
				A[A.length] = ": ";
				A[A.length] = htmlEnc(user.statusMsg);
			}
			if ((user.messages.length + user.chatmsgs.length) > 0) {
				A[A.length] = "&#10;";
				A[A.length] = ""+(user.messages.length + user.chatmsgs.length)+" message(s) pending";
			}
      A[A.length] = "\"><nobr>";
      var userImg = (user.lastsrc) ? messageImg : eval(user.status + "Led");
      A[A.length] = "<img src=\"";
			A[A.length] = userImg.src;
			A[A.length] = "\" name=\"";
			A[A.length] = htmlEnc(user.jid);
			A[A.length] = "/";
			A[A.length] = this.groups[i].name;
			A[A.length] = "\" width=16 height=16 border=0 align=\"left\">";
      A[A.length] = "<div><span class=\"nickName\">";
			A[A.length] = user.name;
			A[A.length] = "</span>";
      if (user.statusMsg) {
        A[A.length] = "<br clear=all><nobr><span class=\"statusMsg\">";
				A[A.length] = htmlEnc(user.statusMsg);
				A[A.length] = "</span></nobr>";
			}
      A[A.length] = "</div></nobr></div>";
    } /* END inner loop */
    A[A.length] = "</div>";
  }

  this.rosterW.getElementById("roster").innerHTML = A.join('');
  this.updateStyleIE();
}

function GroupchatRosterUserAdd2Group(group) {
  this.groups = [group];
}

function GroupchatRosterUser(jid,name) {
	

  this.base = RosterUser;
  this.base(jid,'',[''],name);
	this.jid = this.fulljid; // always use fulljid
  this.affiliation = 'none';
  this.role = 'none';

  this.add2Group = GroupchatRosterUserAdd2Group;
}

GroupchatRosterUser.prototype = new RosterUser;

function getRosterGetRealJIDByNick(nick) {
  for (var i=0; i<this.users.length; i++)
    if (this.users[i].name == nick)
      return this.users[i].realjid;
  return null;
}

function getRosterGetFullJIDByNick(nick) {
  for (var i=0; i<this.users.length; i++)
    if (this.users[i].name == nick)
      return this.users[i].fulljid;
  return null;
}
			
function getGroupchatRosterUserByJID(jid) {
  // need to search fulljid here
  return getElFromArrByProp(this.users,"fulljid",jid);
}

function GroupchatRoster(targetW) {

  this.base = Roster;
  this.base(null);
  this.usersHidden = true;

  this.targetW = targetW.frames.groupchatRoster;

  this.rosterW = this.targetW.groupchatIRoster.document;

  this.name = 'GroupchatRoster';

  this.print = GroupchatRosterPrint;
  this.getUserByJID = getGroupchatRosterUserByJID;
  this.getRealJIDByNick = getRosterGetRealJIDByNick;
  this.getFullJIDByNick = getRosterGetFullJIDByNick;
}

GroupchatRoster.prototype = new Roster();

// some images - no idea why they are defined here

var messageImg = new Image();
messageImg.src = "images/message.gif";
var grp_open_img = new Image();
grp_open_img.src = 'images/group_open.gif';
var grp_close_img = new Image();
grp_close_img.src = 'images/group_close.gif';
var arrow_right_blinking = new Image();
arrow_right_blinking.src = 'images/arrow_right_blinking.gif';

