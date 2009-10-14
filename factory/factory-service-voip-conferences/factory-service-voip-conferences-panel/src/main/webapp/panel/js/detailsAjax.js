var participants;
var bannedUsers;
var details;

var conf;
var period=5000;

function initialize(idConf) {
	conf=idConf;
	askForInfo(idConf);
}

function askForInfo(idConf) {
	var url='Ajax/Info';
	var pars='idConf='+idConf;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
				onSuccess: setUsers,
    			onFailure: failure
			});
}

function failure(request) {
	//alert(request.status);
	$('ajaxStatus').innerHTML=request.status;
	setTimeout(function(){askForInfo(conf);},period);
}

function setUsers(request) {
	//pobieramy liste userow jesli success
	//jesli nie success to pobieramy jeszcze raz?

	var jsonExpression = "(" + request.responseText + ")";
	if (participants == null) {
		participants = eval(jsonExpression).participants;
	}
	else {
		var evalText=eval(jsonExpression);
		var newParticipants = evalText.participants;
		var newBannedUsers = evalText.bannedUsers;
		var newInvitedUsers = evalText.invitedUsers;
		var newDetails = evalText.details;
		
		//porownanie tablic
		
		var tb = document.createElement("tbody");
		var tr;
		var td;
		if (newParticipants.length==0) {
			tr = document.createElement('tr');
			td = document.createElement('td');
			td.setAttribute("colspan",8);
	    	td.innerHTML="No users in this conference!";
	    	tr.appendChild(td);
	    	tb.appendChild(tr);
		}
		else {
			newParticipants.each(function(s) {
				tr = document.createElement('tr');
				td = document.createElement('td');
		    	td.innerHTML=s.id;
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
		    	td.innerHTML=s.username;
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
		    	td.innerHTML=s.time;
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
		    	td.innerHTML=s.isTalking;
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
		    	td.innerHTML=s.isMuted;
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
		    	td.innerHTML=s.isBanned;
		    	tr.appendChild(td);
		
				td = document.createElement('td');
				if (s.isBanned) {
					td.innerHTML="<a href='?act=unban&id="+conf+"&user="+s.username+"' onClick='return unban("+conf+",\""+s.username+"\");'>Unban</a>";
				}
				else {
					td.innerHTML="<a href='?act=ban&id="+conf+"&user="+s.id+"' onClick='return ban("+conf+",\""+s.id+"\");'>Ban</a>";
				}
		    	tr.appendChild(td);  
		    	
		    	td = document.createElement('td');
				if (s.isMuted) {
					td.innerHTML="<a href='?act=unmute&id="+conf+"&user="+s.id+"' onClick='return unmute("+conf+",\""+s.id+"\");'>Unmute</a>";
				}
				else {
					td.innerHTML="<a href='?act=mute&id="+conf+"&user="+s.id+"' onClick='return mute("+conf+",\""+s.id+"\");'>Mute</a>";
				}
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
				td.innerHTML="<a href='?act=kick&id="+conf+"&user="+s.id+"' onClick='return kick("+conf+",\""+s.id+"\");'>Kick</a>";
		    	tr.appendChild(td);
		    	
		    	tb.appendChild(tr);
			});
		}
		$('participantsTable').tBodies[0].innerHTML=tb.innerHTML;
		
		tb = document.createElement("tbody");
		if (newBannedUsers.length==0) {
			tr = document.createElement('tr');
			td = document.createElement('td');
			td.setAttribute("colspan",2);
	    	td.innerHTML="No banned users!";
	    	tr.appendChild(td);
	    	tb.appendChild(tr);
		}
		else {
			newBannedUsers.each(function(s) {
				tr = document.createElement('tr');
				td = document.createElement('td');
		    	td.innerHTML=s;
		    	tr.appendChild(td);
		    	
		    	td = document.createElement('td');
				td.innerHTML="<a href='?act=unban&id="+conf+"&user="+s+"' onClick='return unban("+conf+",\""+s+"\");'>Unban</a>";
		    	tr.appendChild(td);
		    		    	
		    	tb.appendChild(tr);
			});
		}
		$('bannedTable').tBodies[0].innerHTML=tb.innerHTML;
		
		tb = document.createElement("tbody");
		if (newInvitedUsers.length==0) {
			tr = document.createElement('tr');
			td = document.createElement('td');
	    	td.innerHTML="No invited users!";
	    	tr.appendChild(td);
	    	tb.appendChild(tr);
		}
		else {
			newInvitedUsers.each(function(s) {
				tr = document.createElement('tr');
				td = document.createElement('td');
		    	td.innerHTML=s;
		    	tr.appendChild(td);	    	
		    	tb.appendChild(tr);
			});
		}
		$('invitedTable').tBodies[0].innerHTML=tb.innerHTML;
		
		$('confnoField').innerHTML=newDetails.number;
		$('ownerField').innerHTML=newDetails.owner;
		
		if (newDetails.permanent == true)
			$('isPermanentField').innerHTML="Permanent Conference";
		else
			$('isPermanentField').innerHTML="Conference with a time slot";
		
		$('startTimeField').innerHTML=newDetails.startDate;
		$('endTimeField').innerHTML=newDetails.endDate;
		
		switch(newDetails.accessType) {
			case '0':
				$('accessPolicyField').innerHTML="Public Conference";
				break;
			case '1':
				$('accessPolicyField').innerHTML="Pin Number";
				break;
			case '2':
				$('accessPolicyField').innerHTML="Invited Users Only";
				break;
			case '3':
				$('accessPolicyField').innerHTML="Pin Number + Invited Users";
			break;
		}
		
		$('maxUsersField').innerHTML=newDetails.maxUsers;
		$('pinField').innerHTML=newDetails.pin;
		$('adminPinField').innerHTML=newDetails.adminPin;
		$('nameField').innerHTML=newDetails.name;
		$('agendaField').innerHTML=newDetails.agenda;
		$('isRecordedField').innerHTML=newDetails.recorded;
	}
	//var lastOrder = customer.orders[customer.orders.length-1];
	//var name = lastOrder.items[0].name;
	setTimeout(function(){askForInfo(conf);},period);
}

function unban(idConf,username)
{
	var url='Ajax/Unban';
	var pars='idConf='+idConf+'&username='+username;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;			
}

function ban(idConf,userConfId)
{	
	var url='Ajax/Ban';
	var pars='idConf='+idConf+'&userConfId='+userConfId;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;
}

function unmute(idConf,userConfId)
{
	var url='Ajax/Unmute';
	var pars='idConf='+idConf+'&userConfId='+userConfId;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;			
}

function mute(idConf,userConfId)
{
	var url='Ajax/Mute';
	var pars='idConf='+idConf+'&userConfId='+userConfId;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;			
}

function kick(idConf,userConfId)
{
	var url='Ajax/Kick';
	var pars='idConf='+idConf+'&userConfId='+userConfId;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;			
}

function unlock(idConf)
{
	var url='Ajax/Unlock';
	var pars='idConf='+idConf;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;			
}

function lock(idConf)
{
	var url='Ajax/Lock';
	var pars='idConf='+idConf;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;
}

function end(idConf)
{
	var url='Ajax/End';
	var pars='idConf='+idConf;
	var myAjax = new Ajax.Request(
			url,
			{
				method: 'get', 
				parameters: pars,
    			onFailure: failure
			});
	return false;
}
