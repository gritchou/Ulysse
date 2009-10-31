var tabs;
var cont = 1;
var evtDate = new Array(10);
var userA = new Array(10);
var eventA = new Array(10);
var infoA = new Array(10);
var start = 0;

Ext.onReady(function() {

	// Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
		buggin = opener.document.login.bugger.value;

		if (buggin == "Mantis") {
			var viewport = new Ext.Viewport( {

				layout : 'border',
				items : [ {
					region : 'west',
					contentEl : 'roster',
					split : true,
					width : 300,
					minSize : 300,
					maxSize : 400,
					collapsible : false
				}, tabs = new Ext.TabPanel( {
					region : 'center',
					enableTabScroll : true,
					resizeTabs : true,
					deferredRender : false,
					activeTab : 0,
					items : [{

						// id:'Welcome',
						contentEl : 'Welcome',
						title : 'Welcome',
						closable : false,
						autoScroll : true
					}]
				}), {
					region : 'east',
					contentEl : 'bugPanel',
					split : true,
					width : 300,
					minSize : 300,
					maxSize : 300,
					collapsed : true,
					collapsible : true
				}

				]
			})
		} else {
			var viewport = new Ext.Viewport( {

				layout : 'border',
				items : [ {
					region : 'west',
					contentEl : 'roster',
					split : true,
					width : 300,
					minSize : 300,
					maxSize : 400,
					collapsible : false
				}, tabs = new Ext.TabPanel( {
					region : 'center',
					enableTabScroll : true,
					resizeTabs : true,
					deferredRender : false,
					activeTab : 0,
					items : [{

						// id:'Welcome',
						contentEl : 'Welcome',
						title : 'Welcome',
						closable : false,
						autoScroll : true
					}]
				}), {
					region : 'east',
					contentEl : 'bugPanel2',
					split : true,
					width : 300,
					minSize : 300,
					maxSize : 300,
					collapsed : true,
					collapsible : true
				}

				]
			})
		}
		OpenCalendar();
	});


function openConvGroupTab(jid, nickparam, pass) {
	// nick param is the name of the person that is speaking in first person
	var roomName = jid.toString().substring(0, jid.toString().indexOf("@"));

	// example:
	// http://localhost:8080/ChatOS/groupchat.html?jid=talks@conference.es-2t93v2j&nick=diego5&pass=
	
	var tab;
	// alert("++++ jid : " +jid);
	var iframesrc = 'groupchat.html?jid=' + jid;
	// alert("iframesrc = "+iframesrc+"++++");
	var iframejid = "iframe" + jid;
	// alert("iframejid = "+iframejid+"++++");

	if (document.getElementById(iframejid) == null) {
		// alert("=null");
		var iframe = Ext.DomHelper.append(document.body, {
			tag : 'iframe',
			id : iframejid,
			frameBorder : '0',
			src : iframesrc,
			name : iframejid,
			width : '100%',
			height : '100%'
		});
		alert("creando tab");
		alert("creando tab a lo hardcore");
		tab = tabs.add(new Ext.Panel( {
			id : 'MUC' + roomName,
			title : roomName,
			border : 0,
			closable : true,
			contentEl : iframe
		}))
		alert("mostrando y poniendo activo");
		tab.show();
		tab.setActiveTab(iframejid);
		// tabs.hideTabStripItem(tab.getId());
		// tab.hide();

	} else {
		// IF CONVERSATION HAD BEEN OPENED SHOWS THE HIDDEN PANEL(EXIST)
		if (tabs.findById(iframejid) != null) {
			alert("diferente de null ++++");
			if (showpanel == true) {
				// /alert("viene de handle message++++");
				// If comes from handlemessage just unhide the panel
				tabs.setActiveTab(findById(iframejid));
				tabs.unhideTabStripItem(iframejid)
				tabs.findById(iframejid).show();
				// tabs.findById(iframejid).setTitle(nick);
				tabs.setActiveTab(iframejid);
				// LINE CREATES BUG WHEN FOCUSING to bring back focus to
				// NEWboxview
			} else {
				// alert("no viene de handlemessage y ademas no esta
				// activo++++");
				if (tabs.findById(iframejid).isVisible()) {

					// alert("no viene de handle message y esta activo y
					// visible++++");
					// tabs.findById(iframejid).setTitle(nick);
					tabs.setActiveTab(iframejid);
					// tabs.findById(iframejid).fireEvent('activate',iframejid,false);
				} else {
					// alert("no es visible y no viene de handlemessage++++");
					tabs.unhideTabStripItem(iframejid);
					tabs.findById(iframejid).setTitle(roomName + "   NEW");
					// tabs.findById(iframejid).fireEvent('activate',tabs.findById(iframejid),true);
				}
			}
		}
	}
}

function LoadCalendar(evtDate, userA, eventA, infoA) {
	// loading data for calendar. It is just a PofC
	// next iteraction : connection with calendar/agenda tool from european
	// dinamics
	// Date array
	for (var x = 0; x < evtDate.length; x++) {
		evtDate[x] = new Date(2008, 9, 1).add('d', x * 5);
	}
	// Users array
	for (var x = 0; x < userA.length; x++) {
		userA[x] = "diego" + x;
	}
	// Events array
	for (var x = 0; x < eventA.length; x++) {
		eventA[x] = "event" + x;
	}
	// Information array
	for (var x = 0; x < infoA.length; x++) {
		infoA[x] = "INFO" + x;
	}
	start = 1;
}

function OpenCalendar() {
	// function that starts the Calendar in a new tab
	if (start == 0) {
		LoadCalendar(evtDate, userA, eventA, infoA);
	} else {
		tabs.getActiveTab().destroy();
		tabs.hideTabStripItem(tabs.getActiveTab().getId());

	}
	var tab = tabs.add(new Ext.FormPanel( {
		labelWidth : 200, // label settings here cascade unless overridden
			frame : true,
			title : 'Calendar Event',
			bodyStyle : 'padding:5px 5px 0',
			width : 350,
			height : 'auto',
			defaultType : 'textfield',
			bodyStyle : 'padding:5px 5px 0',
			layoutConfig : {
				columns : 3
			},
			defaults : {
				width : 230,
				style : {
					margin : '10px'
				}
			},

			items : [ {
				id : 'username',
				fieldLabel : 'User Name *',
				name : 'User',
				width : 300
			// allowBlank:false
					}, {
						id : 'event',
						fieldLabel : 'Event *',
						name : 'Event',
						width : 300
					}, new Ext.form.TextArea( {
						id : 'information',
						fieldLabel : 'Information *',
						name : 'Info',
						height : 200,
						width : 300
					}), new Ext.form.DateField( {
						id : 'dateS',
						fieldLabel : 'Day Start(MM/DD/YY)*',
						name : 'dayS',
						hideTrigger : 'true'

					}), new Ext.form.DateField( {
						id : 'dateE',
						fieldLabel : 'Day End(MM/DD/YY)*',
						name : 'dayE',
						hideTrigger : 'true'

					}), {
						id : 'mmcalendar',
						xtype : 'mmcalendar',
						value : new Date(2008, 9, 1),
						noOfMonth : 3,
						eventDates : evtDate

					}],

			buttons : [
					{
						text : 'Delete',
						id : 'delete',
						handler : function() {
							if (document.getElementById("username").value == ""
									|| document.getElementById("event").value == ""
									|| document.getElementById("dateE").value == ""
									|| document.getElementById("information").value == ""
									|| document.getElementById("dateS").value == "") {
								alert(" Your selection is not correct. Please select it againg to delete");
							} else {
								var aux = document.getElementById("dateS").value;
								for (var j = 0; evtDate[j] != aux; j++) {}
								evtDate.splice(j, 1);
								userA.splice(j, 1);
								eventA.splice(j, 1);
								infoA.splice(j, 1);
								OpenCalendar();
							}
						}

					}, {
						text : 'Add',
						id : 'add', 
						// disabled:true,
					handler : function() {
						userA.push(document.getElementById("username").value);
						var aux = document.getElementById("dateS").value;
						var auxD = new Date("20" + aux.substring(6), aux.substring(3, 5)- 1, aux.substring(0, 2))
						evtDate.push(auxD);
						eventA.push(document.getElementById("event").value);
						infoA.push(document.getElementById("information").value);
						OpenCalendar();
					}
				}, {
					text : 'New Event',
					id : 'new',
					handler : function() {
						document.getElementById("username").value = "";
						document.getElementById("event").value = "";
						document.getElementById("dateS").value = "";
						document.getElementById("information").value = "";
						document.getElementById("dateE").value = "";
					}
				}, {
					text : 'Cancel',
					id : 'cancel',
					handler : function() {
						document.getElementById("username").value = "";
						document.getElementById("event").value = "";
						document.getElementById("dateS").value = "";
						document.getElementById("information").value = "";
						document.getElementById("dateE").value = "";
					}
				}]
		}));
	tab.show(); 

}

function OpenConvTab(jid, showpanel) {
	// alert("si pone esto es que pasa por aqui cuando abro la multi++++");
	var nick = jid.toString().substring(0, jid.toString().indexOf("@"));
	var iframesrc = 'chat.html?jid=' + jid;
	var iframejid = "iframe" + jid;
	var tab;

	// OPEN THE CONVERSATION IF IT IS NOT OPENED YET (NOT EXIST)
	if (document.getElementById(iframejid) == null) {

		var iframe = Ext.DomHelper.append(document.body, {
			tag : 'iframe',
			id : iframejid,
			frameBorder : '0',
			src : iframesrc,
			name : iframejid,
			width : '100%',
			height : '100%'
		});

		tab = tabs.add(new Ext.Panel( {
			id : iframejid,
			title : nick,
			border : 0,
			closable : true,
			contentEl : iframe
		}))
		tab.show();
		tabs.hideTabStripItem(tab.getId());
		tab.hide();
		var panelaux = tabs.findByType('panel');
		tabs.setActiveTab(panelaux[0]);

		// listener to put focus into textarea when it becomes active
		tab.on('activate', function(iframejid, active, jid) {
			var element = window.frames[iframejid.getId()].document
					.getElementById('msgboxview');
			var len = element.value.length;
			if (element.setSelectionRange) {
				element.setSelectionRange(len, len)
				element.focus();
			} else if (element.createTextRange) {
				var rnaux = element.createTextRange();
				rnaux.moveStart('character', len);
				rnaux.select();
			}
			var nickaux = iframejid.getId().substring(6,
					iframejid.getId().indexOf("@"));
			Ext.getCmp(iframejid.getId()).setTitle(nickaux);
			tabs.unhideTabStripItem(iframejid);
		});// end listener

		// request coming from roster or handlemessage
		if (showpanel == true) {
			tabs.unhideTabStripItem(iframejid);
			tabs.findById(iframejid).show();
			tabs.findById(iframejid).setTitle(nick);
			// tabs.setActiveTab(iframejid);
		} else {
			tabs.findById(iframejid).setTitle(nick + "   NEW");
			tabs.unhideTabStripItem(iframejid);
		}
	} else {
		// IF CONVERSATION HAD BEEN OPENED SHOWS THE HIDDEN PANEL(EXIST)
		if (tabs.findById(iframejid) != null) {
			if (showpanel == true) {
				// If comes from handlemessage just unhide the panel
				// tabs.setActiveTab(findById(iframejid));
				tabs.unhideTabStripItem(iframejid)
				tabs.findById(iframejid).show();
				tabs.findById(iframejid).setTitle(nick);
				// tabs.setActiveTab(iframejid);
				// LINE CREATES BUG WHEN FOCUSING to bring back focus to
				// NEWboxview
			} else {
				// coming form handlemessage
				if (tabs.findById(iframejid).isVisible()) {
					tabs.findById(iframejid).setTitle(nick);
					tabs.setActiveTab(iframejid);
					// tabs.findById(iframejid).fireEvent('activate',iframejid,false);
				} else {
					tabs.unhideTabStripItem(iframejid);
					tabs.findById(iframejid).setTitle(nick + "   NEW");
					// tabs.findById(iframejid).fireEvent('activate',tabs.findById(iframejid),true);
				}
			}
		}
	}
}

Ext.override(Ext.TabPanel,{
					remove : function(tab, auto) {
						auto = false;
						var panels = this.findByType('panel');
						if (panels != null) {
							// It becomes active the first tab. we will solve itlater
						
							var idaux = tab.getId();
							var cmp = Ext.getCmp(idaux);
							var element = window.frames[idaux].document.getElementById('msgboxview');

							if (element.value != "") {
								var r = confirm("You will lost all infor written in this conversation bar. Do you want to continue?");
								if (r == true) {
									element.value = "";
									this.setActiveTab(panels[0]);
									this.hideTabStripItem(tab);
									tab.hide();
								} else {
									auxid = tab.getId();
									this.unhideTabStripItem(tab);
									tab.show()
								}
							} else {
								this.setActiveTab(panels[0]);
								this.hideTabStripItem(tab);
								tab.hide();
							}
						}
					}
				});