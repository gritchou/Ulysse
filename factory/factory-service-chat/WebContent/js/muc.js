function inviteUsers() {	
	//location.reload(true);
    var curtain = MM_findObj('users');
	curtain.style.height = windowheight + "px";
	curtain.style.display = "block";
	if (animations) {
		new Rico.Effect.FadeTo('users', .85, 300, 5, {} );
	} else {
		new Rico.Effect.FadeTo('users', .85, 0, 1, {} );
	}	
}

//ESTA FUNCION ES XA CD PINCHO ENCIMA DE UN USUARIO -> ABRE LA VENTANA DE CONVERSACION
function usersHandler(checks, username) {
    alert("estoy entrando en el handler");
    alert(checks);
    closeCurtain('users');
    alert(username);
    //esto solo va a funcionar cd seleccione un nombre (o x lo menos solo se abrira la ventana xa un nombre)
    var i = 0;
    openChatGroup('',username, checks[i], '','');
}

function openChatGroup(obj, usr, name, initVal, inout) {
	var actWin = null;
	if (windows[hex_md5(usr)] != null) {
		actWin = MM_findObj('chatWin' + usr);
		actWin.style.display = 'block';
	} else {
		var position = usr.indexOf('@');
		var usrMe = usr.substring(0,position);
		var position2 = name.indexOf('&');
		var nameTo = name.substring(0,position2);
		var cw = new ChatWin(usr, userMe, nameTo, 'Group chat', 'online');
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