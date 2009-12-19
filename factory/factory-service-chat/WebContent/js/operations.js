// JavaScript Document

var userMe;
var fullName;
var awayTimeout = 15;
var animations = true;
var soundAlert = true;
var statusTimer = 0;
var currStatus = "available";
var autosetAway = false;

function loginCatchEnter(e) {
	var code;
	if (!e) {
		code = e.keyCode;
	} else {
		code = e.which;
		if (code == null) {
			code = e.keyCode;
		}
	}
	if (code == 13) { // ENTER
		claroslogin();
		return false;
	}
	return true;
}

function claroslogin() {
	var username = document.forms['loginform'].username.value;
	var password = document.forms['loginform'].password.value;
	var server = document.forms['loginform'].server.value;

	if (username == null || username.length == 0 || password == null || password.length == 0) {
		var res = MM_findObj('loginResult');
		res.style.color = 'red';
		res.innerHTML = 'invalid username/password, try again';
		return;
	}

	MM_findObj('loginprogress').style.display = '';
	userMe = "";
	var paramData = "username=" + username + "&password=" + password + "&server=" + server;
	var url = "authenticate.cl";
	var pText = MM_findObj('logintext');

	var callback = {
	  success: 	function(o) {
					MM_findObj('loginprogress').style.display = 'none';
					if(o.responseText !== undefined){
						if (o.responseText != 'ok') {
							//var res = MM_findObj('loginResult');
							//res.style.color = 'red';
							//res.innerHTML = 'login failed, try again';
							//pText = "&nbsp;";
							//return;
							//ESTO ES NUEVO XA VER SI SE PUEDE DAR DE ALTA UN USUARIO
							JSJaCReg(username, password);
						}
						logged = true;
						pText.innerHTML = 'login successfull.';
						var userMe = username;
						// login is successful go on.
						if (animations) {
							new Rico.Effect.SizeAndPosition('logincenter', -500, null, 0 , 0, 500, 10, {} );
						}
						var lgn = MM_findObj('login');
						if (animations) {
							new Rico.Effect.FadeTo('login', 0, 300, 5, {complete:function() {lgn.style.display = "none";}} );
							new Rico.Effect.FadeTo('contacts', 1, 300, 5, {} );
						} else {
							lgn.style.display = 'none';
							new Rico.Effect.FadeTo('contacts', 1, 0, 1, {} );
						}
						pText.innerHTML = 'loading user details...';
						loadPreferences();
						loadStatus();
						initToolbar();
						pText.innerHTML = 'loading buddies, please wait.';
						initContacts();

						var console = MM_findObj('consoleText');
						console.innerHTML = "Succesfully logged in " + formatDate(new Date(), "hh:mm dd/MM/yy") + "<br/>";
						console.scrollTop = console.scrollHeight;

						var lc = new YAHOO.util.DD("contacts");
						lc.setHandleElId("contactshandle");

						MM_findObj('contactsscroll').style.overflow = 'auto';
						
						MM_findObj('avatarme').src = 'img/avatar.png';
						MM_findObj('avatarme').src = 'avatar.cl';
						pText.innerHTML = 'successfully logged in.';
					}
				},
	  failure: 	function(o) {
					if (o.responseText !== undefined){
						res.style.color = 'red';
						MM_findObj('loginResult').innerHTML = 'a system error occured. contact administrator.';
					}
	  			},
	  argument: []
	}

	MM_findObj('logintext').innerHTML = 'logging in please wait';
	var request = YAHOO.util.Connect.asyncRequest('POST', url, callback, paramData);
}

//ESTO ES NUEVO Y ES DE PRUEBA XA VER SI DA DE ALTA LOS USUARIOS
function JSJaCReg(username, password) {
	var iq = new JSJaCIQ();
	iq.setType('set');
	iq.setID('reg1');
	var query = iq.setQuery('jabber:iq:register');
	query.appendChild(iq.getDoc().createElement('username')).appendChild(iq.getDoc().createTextNode(this.username));
	query.appendChild(iq.getDoc().createElement('password')).appendChild(iq.getDoc().createTextNode(this.pass));
		
	this.send(iq,this._doAuth);
}

function JSJaCIQ() {
	this.base = JSJaCPacket;
	this.base('iq');

	this.setIQ = function(to,from,type,id) {
		if (to)
			this.setTo(to);
		if (type)
			this.setType(type);
		if (from)
			this.setFrom(from);
		if (id)
			this.setID(id);
		return this; 
	};
	this.setQuery = function(xmlns) {
		var query;
 		try {
 			query = this.getDoc().createElementNS(xmlns,'query');
 		} catch (e) {
			// fallback
			query = this.getDoc().createElement('query');
		}
		if (query && query.getAttribute('xmlns') != xmlns) // fix opera 8.5x
			query.setAttribute('xmlns',xmlns);
		this.getNode().appendChild(query);
		return query;
	};

	this.getQuery = function() {
		return this.getNode().getElementsByTagName('query').item(0);
	};
	this.getQueryXMLNS = function() {
		if (this.getQuery())
			return this.getQuery().namespaceURI;
		else
			return null;
	};
}
//FIN DE LO NUEVO DE PRUEBA

function changeStatus() {
	loadStatus();
	var curtain = MM_findObj('changeStatus');
	curtain.style.height = windowheight + "px";
	curtain.style.display = "block";
	if (animations) {
		new Rico.Effect.FadeTo('changeStatus', .85, 300, 5, {} );
	} else {
		new Rico.Effect.FadeTo('changeStatus', .85, 0, 1, {} );
	}
}

function loadStatus() {
	/*
	var url = "status.cl";
	var params = "act=load";
	var callback = {
	  success: 	function(o) {
		if(o.responseText !== undefined) {
			if (o.responseXML != null) {
				var xml = o.responseXML;
				currStatus = xml.getElementsByTagName('status')[0].firstChild.nodeValue;
				var message = xml.getElementsByTagName('message')[0].firstChild.nodeValue;
				
				var txtstatus;
				var status = currStatus;
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
				MM_findObj('userStatus').innerHTML = txtstatus;
				MM_findObj('statusindic').src = "img/indicators/" + imgstatus + ".gif";
				MM_findObj('statusnow').innerHTML = txtstatus;
				MM_findObj('newstatusmsg').value = message;
			}
	  	}
	  },
	  failure: 	function(o) {},
	  argument: [],
	  timeout: 5000
	}

	YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	*/

	var status = currStatus;
	var imgstatus;
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

	MM_findObj('userStatus').innerHTML = txtstatus;
	MM_findObj('statusindic').src = "img/indicators/" + imgstatus + ".gif";
	MM_findObj('statusnow').innerHTML = txtstatus;
	MM_findObj('avatarme').src = 'img/avatar.png';
	MM_findObj('avatarme').src = 'avatar.cl';
	
	statusTimer = 0;
}

function statusTimerIncrement() {
	if (logged == true) {
		statusTimer++;
	}
	window.setTimeout("statusTimerIncrement()", 1000);
}

function arrangeStatus() {
	if (currStatus == "away" && autosetAway) {
		saveStatus("available");
		autosetAway = false;
	} else {
		statusTimer = 0;
	}
}

function statusListener() {
	if (logged == true) {
		if (currStatus == "available" && statusTimer > awayTimeout * 60) {
			saveStatus("away");
			autosetAway = true;
		}
	}
	window.setTimeout("statusListener()", 10000);
}

function saveStatus(newStatus, msg) {
	if (newStatus == null) {
		var msg = MM_findObj('newstatusmsg').value;
		var newStatus = MM_findObj('newstatus').value;
	}

	var url = "status.cl";
	var params = "act=save&newstat=" + newStatus;
	if (msg != null) {
		params += "&newstatmsg=" + msg;
	}
	var callback = {
	  success: 	function(o) {
		if(o.responseText !== undefined) {
			statusTimer = 0;
			currStatus = newStatus;
			loadStatus();
		}
	  },
	  failure: 	function(o) {},
	  argument: [],
	  timeout: 20000
	}
	YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	closeCurtain('changeStatus');
}


function logout() {
	var curtain = MM_findObj('logout');
	curtain.style.height = windowheight + "px";
	curtain.style.display = "block";
	if (animations) {
		new Rico.Effect.FadeTo('logout', .85, 300, 5, {} );
	} else {
		new Rico.Effect.FadeTo('logout', .85, 0, 1, {} );
	}
}

function doLogout() {
	unloadHandler();
	window.location.reload(true);
}

function doAddContact() {
	var url = "roaster.cl";
	var buddy = document.forms['addBuddyForm'].newBuddy.value;
	var params = "act=save&newBuddy=" + buddy;
	
	var callback = {
	  success: 	function(o) {},
	  failure: 	function(o) {},
	  argument: [],
	  timeout: 20000
	}
	YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	closeCurtain('addContact');
}

function addContact() {
	var curtain = MM_findObj('addContact');
	curtain.style.height = windowheight + "px";
	curtain.style.display = "block";
	if (animations) {
		new Rico.Effect.FadeTo('addContact', .85, 300, 5, {} );
	} else {
		new Rico.Effect.FadeTo('addContact', .85, 0, 1, {} );
	}
}

function loadPreferences() {
	var url = "preferences.cl?act=load";
	var callback = {
	  success: 	function(o) {
		if(o.responseText !== undefined) {
		
			if (o.responseXML != null) {
				var fullname = o.responseXML.getElementsByTagName('fullname');
				var txtfullname = fullname[0].firstChild.nodeValue;

				fullName = txtfullname;
//				alert(MM_findObj('fullName').innerHTML);
				MM_findObj('fullName').innerHTML = txtfullname;
				MM_findObj('preffullname').value = txtfullname;
				
				var preferences = o.responseXML.getElementsByTagName('preference');
				if (preferences != null) {
					for (i=0; i<preferences.length;i++) {
						var key = preferences[i].getElementsByTagName("key")[0].firstChild.nodeValue;
						var val = preferences[i].getElementsByTagName("value")[0].firstChild.nodeValue;
						if (key == 'awayTimeout') {
							awayTimeout = parseInt(val);
							MM_findObj('mins').innerHTML = awayTimeout;
						}
						if (key == 'animations') {
							if (val == 'true') {
								document.forms['preferencesForm'].anims[0].checked = true;
								animations = true;
							} else {
								document.forms['preferencesForm'].anims[1].checked = true;
								animations = false;
							}
						}
						if (key == 'soundAlert') {
							if (val == 'true') {
								document.forms['preferencesForm'].sndalert[0].checked = true;
								soundAlert = true;
							} else {
								document.forms['preferencesForm'].sndalert[1].checked = true;
								soundAlert = false;
							}
						}
					}
				} else {
					MM_findObj('mins').innerHTML = awayTimeout;
					document.forms['preferencesForm'].anims[0].checked = true;
					animations = true;
					document.forms['preferencesForm'].sndalert[0].checked = true;
					soundAlert = true;
				}
			}
	  	}
	  },
	  failure: 	function(o) {
		MM_findObj('mins').innerHTML = awayTimeout;
		document.forms['preferencesForm'].anims[0].checked = true;
		animations = true;
		document.forms['preferencesForm'].sndalert[0].checked = true;
		soundAlert = true;
	  },
	  argument: [],
	  timeout: 3000
	}

	YAHOO.util.Connect.asyncRequest('POST', url, callback);
}

function savePreferences() {
	var url = "preferences.cl";
	var a1 = (document.forms['preferencesForm'].anims[0].checked == true) ? true : false;
	var a2 = (document.forms['preferencesForm'].sndalert[0].checked == true) ? true : false;
	var s = MM_findObj('mins').innerHTML;
	var n = MM_findObj('preffullname').value;
	
	var params = "act=save&fullName=" + n + "&awayTimeout=" + s + "&animations=" + a1 + "&soundAlert=" + a2;
	
	var callback = {
	  success: 	function(o) {
		if(o.responseText !== undefined) {
			loadPreferences();
		}
	  },
	  failure: 	function(o) {},
	  argument: [],
	  timeout: 20000
	}
	YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	closeCurtain('preferences');
}

function preferences() {
	loadPreferences();
	var curtain = MM_findObj('preferences');
	curtain.style.height = windowheight + "px";
	curtain.style.display = "block";
	if (animations) {
		new Rico.Effect.FadeTo('preferences', .85, 300, 5, {} );
	} else {
		new Rico.Effect.FadeTo('preferences', .85, 0, 1, {} );
	}
}

function closeCurtain(id) {
	var curtain = MM_findObj(id);
	if (animations) {
		new Rico.Effect.FadeTo(id, 0, 300, 5, {complete:function() {curtain.style.display = "none";}} );
	} else {
		curtain.style.display = "none";
	}
}

function prefArrUp() {
	var mins = MM_findObj('mins');
	var iMin = parseInt(mins.innerHTML);
	if (iMin < 180) {
		iMin++;
		mins.innerHTML = iMin + "";
	}
}

function prefArrDown() {
	var mins = MM_findObj('mins');
	var iMin = parseInt(mins.innerHTML);
	if (iMin > 1) {
		iMin--;
		mins.innerHTML = iMin + "";
	}
}
