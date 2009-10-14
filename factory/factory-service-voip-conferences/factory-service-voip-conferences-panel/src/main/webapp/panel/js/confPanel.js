
function addUser() {
	var username=document.getElementById('settingsForm')['addUserName'].value;
	if (username!='') {
		document.getElementById('settingsForm')['addUserName'].value='';
		// xmlHttpRequest - check if the user exists
	}		
	else
		return;

	var sel=document.getElementById('settingsForm')['invitedUsers'];
	
	var newUser = document.createElement('option');
    newUser.text = username;
    newUser.value = username;
      
    try {
      sel.add(newUser, null);
    }
    catch(ex) {
      sel.add(newUser);
    }
}

function addGroup(groupname) {
	// xmlHttpRequest for users in group and add users
}

function removeUsers() {
  var elSel = document.getElementById('settingsForm')['invitedUsers'];
  var i;
  for (i = elSel.length - 1; i>=0; i--) {
    if (elSel.options[i].selected) {
      elSel.remove(i);
    }
  }
}

function setAccessType(element) {
	var val=element.value;
	switch(val) {
		case '0':
			document.getElementById('pinRow').style.display='none';
			document.getElementById('adminRow').style.display='none';
			break;
		case '1':
			document.getElementById('pinRow').style.display='table-row';
			document.getElementById('adminRow').style.display='table-row';			
			break;
		case '2':
			document.getElementById('pinRow').style.display='none';
			document.getElementById('adminRow').style.display='none';
			break;
		case '3':
			document.getElementById('pinRow').style.display='table-row';
			document.getElementById('adminRow').style.display='table-row';
			break;
	}
}

function setPermanent() {
	document.getElementById('clickStart').style.visibility="hidden";
	document.getElementById('clickEnd').style.visibility="hidden";
	document.getElementById('settingsForm')['startDate'].disabled=true;
	document.getElementById('settingsForm')['endDate'].disabled=true;
	
	document.getElementById('startRow').style.display='none';
	document.getElementById('endRow').style.display='none';
	document.getElementById('recordRow').style.display='none';
}

function setTimeSlot() {
	document.getElementById('clickStart').style.visibility="visible";
	document.getElementById('clickEnd').style.visibility="visible";
	document.getElementById('settingsForm')['startDate'].disabled=false;
	document.getElementById('settingsForm')['endDate'].disabled=false;
	
	document.getElementById('startRow').style.display='table-row';
	document.getElementById('endRow').style.display='table-row';
	document.getElementById('recordRow').style.display='table-row';
}

function selectAllInvited() {
	var elSel = document.getElementById('settingsForm')['invitedUsers'];
  	var i;
  	for (i = elSel.length - 1; i>=0; i--) {
	    elSel.options[i].selected=true;
  	}
  	return true;
}