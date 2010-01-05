/**
 *  Add user to list
 */
// Ezweb related
var list_id = EzWebAPI.createRGadgetVariable("list_id", listIdHandler);
var list_name = list_id.get();

function listIdHandler()
{
	var list = document.getElementById('list');
	list.innerHTML =  'List: '+list_name+'<br>';
}

function addUser()
{
	var mail = document.form.email.value;
	var pw = document.form.pw.value;
	var conf_pw = document.form.conf.value;
	
	var values = "email="+mail+"&pw="+pw+"&pw-conf="+conf_pw;
	EzWebAPI.send_post(url_head+version+'/remote/'+hostname+'/lists/'+list_name+'users.xml', values, this, successMessage, errorMessage);
}

function successMessage()
{
	var message = document.getElementById('formMessage');
	message.innerHTML =  '<b>User subscribed successfully</b><br>';
}

function errorMessage()
{
	var message = document.getElementById('formMessage');
	message.innerHTML =  '<b>Unable to subscribe user</b><br>';
}