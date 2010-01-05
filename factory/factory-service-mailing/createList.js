/**
 * Create list
 */
function createList()
{
	var listname = document.formulario.listname.value;
	var owner = document.formulario.owner.value;
	var autogen = document.formulario.autogen.value;
	var moderate = document.formulario.moderate.value;
	var notify = document.formulario.notify.value;
	var auth = document.formulario.auth.value;
	
	var values = "?listname="+listname+"&owner="+owner+"&autogen="+autogen+"&moderate="+moderate+"&notify="+notify+"&auth="+auth;
	EzWebAPI.send_post(url_head+version+'/remote/'+hostname+'/lists.xml', values, this, successMessage, errorMessage);		
}

function successMessage()
{
	var message = document.getElementById('formMessage');
	message.innerHTML =  '<b>You have successfully created the mailing list</b><br>';
}
function errorMessage()
{
	var message = document.getElementById('formMessage');
	message.innerHTML =  '<b>Unable to create list</b><br>';
}