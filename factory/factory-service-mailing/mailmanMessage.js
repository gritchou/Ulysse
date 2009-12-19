/**
 *	Nombre: mailmanMessage.js
 *	Author: Maria Alejandra Trujillo Valencia
 *	E-Mail: alejandra.trujillo@atosorigin.com
 *	Creation date: 27-04-2009
 *	Last modification: 22-05-2009 by Maria Alejandra Trujillo Valencia
*/

var list_id = EzWebAPI.createRGadgetVariable("list_id", listIdHandler);
var archive_id = EzWebAPI.createRGadgetVariable("archive_id", archiveIdHandler);
var message_id = EzWebAPI.createRGadgetVariable("message_id", messageIdHandler);

var last_show = 0;
var list_name = 0;
var url_head = "http://ezwebdev.eurodyn.com/repository/MailingGadgets/Services";

function listIdHandler(value) {}

function archiveIdHandler(value) {}

function messageIdHandler(value)
{
    var message = document.getElementById('message');
    message.innerHTML =  '<b>Message: </b>'+value+"<br>";

    var message_body = document.getElementById('message_body');
    message_body.innerHTML = "Loading message....";

    var message_next = document.getElementById('message_next');
    message_next.innerHTML = "";

    var message_previous = document.getElementById('message_previous');
    message_previous.innerHTML = "";
	
    EzWebAPI.send_get(url_head+'clientMailmanMessage.php?list_id='+list_id.get()+'&archive_id='+archive_id.get()+'message_id='+message_id.get(), this, successMessageHandler, errorMessageHandler);
}

function successMessageHandler(response)
{
    var message_body = document.getElementById('message_body');
    message_body.innerHTML = '';
    writeMessage(response);
}

function errorMessageHandler(response)
{
    var message_body = document.getElementById('message_body');
    message_body.innerHTML = "Message can't be loaded";
}

function writeMessage(Object)
{
    var myObject = eval('(' + response + ')');

    var message_body = document.getElementById('message_body');
    message_body.innerHTML += "<b>Subject:</b> "+myObject.subject+"<br> ";
    message_body.innerHTML += "<b>Author:</b> "+myObject.author+"<br>";
    message_body.innerHTML += "<b>Date:</b> "+myObject.date+"<br>";
    message_body.innerHTML += "<b>Body:</b>";
    message_body.innerHTML += "<div id='message_data' class='message_data'><pre>"+myObject.data+"</pre></div>";
    if (myObject.next_message_id != null)
    {
        var message_next = document.getElementById('message_next');
        message_next.innerHTML += "<b>Next Message:</b> <a href=\"javascript:messageIdHandler('"+myObject.next_message_id +"');\"> "+next_message_subject+"</a>";
    }
    if (myObject.previous_message_id != null)
    {
        var message_previous = document.getElementById('message_previous');
        message_previous.innerHTML +=  "<b>Previous Message:</b> <a href=\"javascript:messageIdHandler('"+previous_message_id+"');\"> "+next_message_subject+"</a>";
    }

}




