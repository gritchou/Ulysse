// JavaScript Document
var windowwidth;
var windowheight;
var netscape = false;
var ie = false;
var infoHide = true;
var infoFor = null;
var posX = 0;
var posY = 0;
//var gtalkClicked = false;
var jabberClicked = false;
var userStatus = new Array();
var logged = false;

ie = document.all?true:false;
if (!ie) document.captureEvents(Event.MOUSEMOVE)
document.onmousemove = getMouseXY;


function disableLoading() {
	MM_findObj('loading').style.display = 'none';
}

//	if (!window.event) event=arguments.callee.caller.arguments[0];
//	var obj=event.srcElement || event.currentTarget || event.target;

function showInfoWin(obj) {
	if (infoFor == null || infoHide == true || (obj.getAttribute('user') != infoFor)) {
		infoHide = false;
		if (animations) {
			new Rico.Effect.FadeTo('infowin', .2, 0, 1, {} );
		}
		var infoWin = MM_findObj('infowin');

		infoWin.getElementsByTagName('img')[0].src = "img/avatar_load.gif";

		infoWin.style.visibility = 'visible';
		if (animations) {
			new Rico.Effect.FadeTo('infowin', 1, 400, 10, {} );
		}
		
		var user = obj.getAttribute('user');
		var username = obj.getAttribute('username');
		var presence = obj.getAttribute('presence');
		var msg = obj.getAttribute('msg');
		if (msg == null || msg == 'null' || msg == "") msg = "<i>No Status Text</i>";
		
		var nlcount = 0;
		var tmp = "";
		for (i=0; i<msg.length; i++) {
			var c = msg.charAt(i);
			if (c != '\n') {
				nlcount++;
			}
			if (nlcount == 32) {
				tmp = tmp + "<br />" + c;
				nlcount = 0;
			} else {
				tmp = tmp + c;
			}
		}
		msg = tmp;
		
		infoFor = user;
	
		var contactWin = MM_findObj('contacts');
		var contactWinLeft = contactWin.offsetLeft;
		var infoLeft = contactWinLeft + contactWin.offsetWidth - 20;
//		if (infoLeft + 199 > windowwidth) {
		if (infoLeft - 370 > 0) {
			infoLeft = contactWinLeft - 180;
		}
	
		var infoTop = posY - 20;
		if ((infoLeft + "").indexOf('px') < 0) {
			infoLeft += 'px';
		}
		infoTop += 'px';
		
		infoWin.style.left = infoLeft;
		
		infoWin.style.top = infoTop;
		infoWin.user = user;
		infoWin.username = username;
		var infoUserName = MM_findObj('infotexttitle');

		var presTxt;
		if (presence == 'available') {
			presTxt = "Online";
		} else if (presence == 'away') {
			presTxt = "Away";
		} else if (presence == 'chat') {
			presTxt = "Chatting";
		} else if (presence == 'disturb') {
			presTxt = "Do Not Disturb";
		} else if (presence == 'extended_away') {
			presTxt = "Extended Away";
		} else if (presence == 'invisible') {
			presTxt = "Invisible";
		} else {
			presTxt = "Offline";
		}
		
		infoUserName.innerHTML = username + " (" + user + ") - " + presTxt;
		
		var infoMsg = MM_findObj('infotextstatus');
		infoMsg.innerHTML = msg;
		
		infoWin.style.visibility = 'visible';
		infoWin.style.display = '';
		infoWin.style.zIndex = 4000;
	
		var divs = document.getElementsByTagName('div');
		for (i=0; i<divs.length; i++) {
			var div = divs[i];
			if (div.id == 'contact') {
				unhoverContact(div);
			}
		}

		if (presence == 'available' || presence == 'away' || presence == 'chat' || presence == 'disturb' || presence == 'extended_away') {
			infoWin.getElementsByTagName('img')[0].src = "avatar.cl?u=" + user;
		} else {
			infoWin.getElementsByTagName('img')[0].src = "img/spacer.gif";
		}
		overInfoWin();	
	}
	obj.className = "contactHover";
	infoHide = false;
}

function hideInfoWinFade() {
	if (!infoHide) {
		if (animations) {
			new Rico.Effect.FadeTo('infowin', 0, 400, 10, {complete:function() {MM_findObj('infowin').style.display = 'none'}});
		} else {
			MM_findObj('infowin').style.display = 'none';
		}
		infoHide = true;
		MM_findObj('contactsscroll').style.overflow = 'auto';
	}
}

function unhoverContact(obj) {
	obj.className = null;
}

function overInfoWin() {
		var infoWin = MM_findObj('infowin');
		infoWin.style.visibility = 'visible';
		var cont = MM_findObj('contacts');
		var divs = cont.getElementsByTagName('div');
		for (i=0; i<divs.length; i++) {
			var div = divs[i];
			if (div.id == 'contact' && div.getAttribute('user') != null && div.getAttribute('user') == infoWin.user) {
				div.className = 'contactHover';
			} else if (div.id.indexOf('chatWin') < 0 && div.className != 'curtain') {
				unhoverContact(div);
			}
		}
}

function outInfoWin() {
	hideInfoWinFade();
}

function getMouseXY(e) {
	if (!e) var e = window.event;
	if (e.pageX || e.pageY) {
		posX = e.pageX;
		posY = e.pageY;
	} else if (e.clientX || e.clientY) 	{
		try {
			posX = event.clientX + document.body.scrollLeft;
			posY = event.clientY + document.body.scrollTop;
		} catch (e) {}
	}
}

function initContacts() {
	var contacts = MM_findObj('contacts');
	contacts.style.left = (windowwidth - 204) + "px";
}

function initLogin() {
	initWinSize();
	var login = document.getElementById('login');
	login.style.height = windowheight + "px";
	
	var logcent = document.getElementById('logincenter');
	var lc = new YAHOO.util.DD("logincenter");
	lc.setHandleElId("loginhandle");

	var myTop = (windowheight / 2) - 200;
	if (myTop < 0) {
		myTop = 0;
	}
	var myLeft = (windowwidth / 2) - 214;
	if (myLeft < 0) {
		myLeft = 0;
	}
	logcent.style.top = myTop + "px";
	logcent.style.left = myLeft + "px";
	login.style.display = 'block';
}

function f_clientWidth() {
	return f_filterResults (
		window.innerWidth ? window.innerWidth : 0,
		document.documentElement ? document.documentElement.clientWidth : 0,
		document.body ? document.body.clientWidth : 0
	);
}
function f_clientHeight() {
	return f_filterResults (
		window.innerHeight ? window.innerHeight : 0,
		document.documentElement ? document.documentElement.clientHeight : 0,
		document.body ? document.body.clientHeight : 0
	);
}
function f_scrollLeft() {
	return f_filterResults (
		window.pageXOffset ? window.pageXOffset : 0,
		document.documentElement ? document.documentElement.scrollLeft : 0,
		document.body ? document.body.scrollLeft : 0
	);
}
function f_scrollTop() {
	return f_filterResults (
		window.pageYOffset ? window.pageYOffset : 0,
		document.documentElement ? document.documentElement.scrollTop : 0,
		document.body ? document.body.scrollTop : 0
	);
}
function f_filterResults(n_win, n_docel, n_body) {
	var n_result = n_win ? n_win : 0;
	if (n_docel && (!n_result || (n_result > n_docel)))
		n_result = n_docel;
	return n_body && (!n_result || (n_result > n_body)) ? n_body : n_result;
}

function initWinSize() {
	if (self.innerHeight) {
		windowwidth = self.innerWidth;
		windowheight = self.innerHeight;
		ie = false;
		netscape = true;
	} else if(document.layers || (document.getElementById&&!document.all)) {
		windowwidth=window.innerWidth;
		windowheight=window.innerHeight;
		ie = false;
		netscape = true;
	} else if (document.documentElement) {
		windowwidth = document.documentElement.clientWidth;
		windowheight = document.documentElement.clientHeight;
		ie = true;
		netscape = false;
	} else if(document.all) {
		windowwidth=document.body.clientWidth;
		windowheight=document.body.clientHeight;
		ie = true;
		netscape = false;
	}
}

function initToolbar() {
	initWinSize();
	var toolbar = MM_findObj('toolbar');
	toolbar.style.left = '15px';
	toolbar.style.width = (windowwidth - 30) + "px";

	if (windowheight - 73 < 480) {
		toolbar.style.top = "480px";
	} else {
		toolbar.style.top = (windowheight - 73) + "px";
	}
	if (logged) {
		toolbar.style.visibility = 'visible';
		toolbar.style.display = 'block';
		MM_findObj('avatarme').src = 'img/avatar.png';
		MM_findObj('avatarme').src = 'avatar.cl';
	} else {
		toolbar.style.visibility = 'hidden';
		toolbar.style.display = 'none';
	}
	var objDiv = MM_findObj('consoleText');
	objDiv.scrollTop = objDiv.scrollHeight;
}

function addConsoleText(txt) {
	var objDiv = MM_findObj('consoleText');
	objDiv.innerHTML += "- " + txt + "<br />";
	objDiv.scrollTop = objDiv.scrollHeight;
}

function scrollChatWinMsgBtm(obj) {
	obj.scrollTop = obj.scrollHeight;
}	

function openChat(obj, usr, name, initVal, inout) {
	if (usr == null) {
		if (obj != null) {
			usr = obj.getAttribute('user');
			name = obj.getAttribute('username');
		}
		if (usr == null) {
			var inf = MM_findObj('infowin');
			usr = inf.user;
			name = inf.username;
		}
		hideInfoWinFade();
	}
	
	var actWin = null;
	if (windows[hex_md5(usr)] != null) {
		actWin = MM_findObj('chatWin' + usr);
		actWin.style.display = 'block';
	} else {
		var cw = new ChatWin(usr, userMe, usr, name, 'online');
		actWin = cw.init();
	}
	if (initVal != null) {
		if (inout == 'in') {
			saySomething(actWin, name, initVal, inout);
		} else {
			saySomething(actWin, "Me", initVal, inout);
		}
		takeAttention(actWin);
	} else {
		moveOnTop(actWin);
	}
}

function contactsHandler(txt, xmlDoc) {
	try {
		var obj;
		if (txt != null && txt.length != 0) {
			obj = MM_findObj('contactsscroll');
			obj.innerHTML = txt;
		}
		// time to set statuses
		var divs = obj.getElementsByTagName('div');
		for (i=0; i<divs.length; i++) {
			var div = divs[i];
			if (div.id == 'contact') {
				var usr = div.getAttribute('user');
				var name = div.getAttribute('username');
				var status = div.getAttribute('presence');
				setStatusChatWin(usr, name, status);
			}
		}
	} catch (e) {
//		alert(e.message);
	}
}

function setStatusChatWin(usr, name, status) {
	var txtstatus;
	if (status == 'available') {
		txtstatus = "Online";
		imgstatus = "green";
	} else if (status == 'away') {
		txtstatus = "Away";
		imgstatus = "orange";
	} else if (status == 'chat') {
		txtstatus = "Chatting";
		imgstatus = "green";
	} else if (status == 'disturb') {
		txtstatus = "Do Not Disturb";
		imgstatus = "red";
	} else if (status == 'extended_away') {
		txtstatus = "Extended Away";
		imgstatus = "orange";
	} else if (status == 'invisible') {
		txtstatus = "Invisible";
		imgstatus = "gray";
	} else {
		txtstatus = "Offline";
		imgstatus = "gray";
	}

	var toSay = name + " changed status to: " + txtstatus;
	var chatwin = MM_findObj('chatWin' + usr);
	if (chatwin != null) {
		var doSay = false;
		if (userStatus[usr] == null) {
			doSay = true;
		} else {
			if (status != userStatus[usr]) {
				doSay = true;
			}
		}
		
		if (doSay) {
			var imgstatus;
			var tds = chatwin.getElementsByTagName('div');
			for (i=0;i<tds.length;i++) {
				if (tds[i].id == 'myText') {
					var div = tds[i];
					var txt = div.innerHTML;

					txt = "<p><i style=\"color:#666666\">" + toSay + "</i></p>";
					div.innerHTML += txt;
					div.parentNode.scrollTop = div.parentNode.scrollHeight;

					break;
				}
			}

			var imgs = chatwin.getElementsByTagName('img');
			for (i=0;i<imgs.length;i++) {
				if (imgs[i].id == 'statusindicator') {
					imgs[i].src = "img/indicators/" + imgstatus + ".gif";
					break;
				}
			}
		}
	}

	var doSay2 = false;
	if (userStatus[usr] == null) {
	} else {
		if (status != userStatus[usr]) {
			doSay2 = true;
		}
	}
	if (doSay2) {
		var console = MM_findObj('consoleText');
		console.innerHTML += " - " + toSay + " " + formatDate(new Date(), "hh:mm dd/MM/yy") + "<br/>";
		console.scrollTop = console.scrollHeight;
	}
	userStatus[usr] = status;
}

function showAskWin() {
	var ask = MM_findObj('askWin')
	var tool = MM_findObj('toolbar')

	var left = (windowwidth - 241) ;
	
	if (windowheight - 73 < 480) {
		ask.style.top = (480 - 135) + "px";
	} else {
		ask.style.top = (windowheight - 73 - 135) + "px";
	}
	
	ask.style.left = left + "px";
	ask.style.display  = "block";

	if (animations) {
		new Rico.Effect.FadeTo('askWin', 1, 700, 10, {complete:function() {}});
	}
}

//function selectGtalk() {
	//var gtalk = MM_findObj('gtalk');
	//var jabber = MM_findObj('jabber');
	//gtalk.src = 'img/buttons/gtalk_clicked.gif';
	//jabber.src = 'img/buttons/jabber.gif';
	//gtalkClicked = true;
	//jabberClicked = false;
	//document.forms['loginform'].server.value = 'Google Talk';
	//document.forms['loginform'].server.readOnly = true;
//}

function selectJabber() {
	//var gtalk = MM_findObj('gtalk');
	var jabber = MM_findObj('jabber');
	
	//gtalk.src = 'img/buttons/gtalk.gif';
	jabber.src = 'img/buttons/jabber_clicked.gif';
	//gtalkClicked = false;
	jabberClicked = true;
	document.forms['loginform'].server.value = '';
	document.forms['loginform'].server.readOnly = false;
}

function preloadImages() {
	MM_preloadImages('img/buttons/jabber_clicked.gif',
					 'img/bg/askwin_bg.gif', 
					 'img/bg/chatwin_bg.gif', 
					 'img/bg/chatwin_bottom.gif', 
					 'img/bg/chatwin_top.gif', 
					 'img/bg/chatwin_top_disactive.gif', 
					 'img/bg/chatwin_top_notice.gif', 
					 'img/bg/contact_item_click_bg.gif', 
					 'img/bg/contact_item_hover_bg.gif', 
					 'img/bg/contact_win_bg.gif', 
					 'img/bg/contact_win_bottom.gif', 
					 'img/bg/contact_win_top.gif', 
					 'img/bg/info_bg.gif', 
					 'img/bg/info_bg_bottom.gif', 
					 'img/bg/info_bg_top.gif', 
					 'img/bg/login_bg.gif', 
					 'img/bg/login_bottom.gif', 
					 'img/bg/login_bottom.png', 
					 'img/bg/login_top.gif', 
					 'img/bg/logo.gif', 
					 'img/bg/main.gif', 
					 'img/bg/toolbar_bg.gif', 
					 'img/bg/toolbar_left.gif', 
					 'img/bg/toolbar_right.gif', 
					 'img/bg/toolbar_seperator.gif', 
					 'img/buttons/add_contact.gif', 
					 'img/buttons/allow.gif', 
					 'img/buttons/arr_down.gif', 
					 'img/buttons/arr_up.gif', 
					 'img/buttons/chat.gif', 
					 'img/buttons/deny.gif', 
					 'img/buttons/exclamation.gif', 
					 'img/buttons/jabber.gif', 
					 'img/buttons/jabber_clicked.gif', 
					 'img/buttons/login.gif', 
					 'img/buttons/remove.gif', 
					 'img/buttons/save.gif',
					 'img/avatar_load.gif',
					 'img/ico/claros.gif',
					 'img/ico/logout.gif', 
					 'img/ico/preferences.gif',
					 'img/indicators/gray.gif', 
					 'img/indicators/green.gif', 
					 'img/indicators/orange.gif',
					 'img/emotions/angry_smile.gif', 
					 'img/emotions/confused_smile.gif',
					 'img/emotions/cry_smile.gif',
					 'img/emotions/loading_mini.gif',
					 'img/emotions/omg_smile.gif',
					 'img/emotions/regular_smile.gif',
					 'img/emotions/sad_smile.gif',
					 'img/emotions/teeth_smile.gif',
					 'img/emotions/tongue_smile.gif',
					 'img/emotions/what_smile.gif',
					 'img/emotions/wink_smile.gif',
					 'img/emotions/shades_smile.gif',
					 'img/emotions/angel_smile.gif',
					 'img/emotions/dont_tell_smile.gif',
					 'img/emotions/nerd_smile.gif',
					 'img/emotions/dont_know_smile.gif',
					 'img/emotions/bat.gif',
					 'img/emotions/heart.gif',
					 'img/emotions/rose.gif',
					 'img/emotions/red_smile.gif',
					 'img/indicators/red.gif');
}

function callListener() {
	var url = "listener.cl";
	var callback = {
	  success: 	function(o) {
		if(o.responseText !== undefined) {
			if (o.responseText != null && o.responseText != '') {
				try {
					eval(o.responseText);
				} catch (e) {
//					alert(e.message);
				}
			}
			window.setTimeout("callListener()", 4000);
	  	}
	  },
	  failure: 	function(o) {
		window.setTimeout("callListener()", 4000);
	  },
	  argument: [],
	  timeout: 3000
	}

	YAHOO.util.Connect.asyncRequest('GET', url, callback);
}

function trim(str) {
	if (str == null) return null;
	return str.replace(/^\s*|\s*$/g,"");
}

