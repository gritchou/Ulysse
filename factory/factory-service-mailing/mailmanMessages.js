/**
 *	File: mailmanMessages.js
 *	Author: Maria Alejandra Trujillo Valencia
 *	E-Mail: alejandra.trujillo@atosorigin.com
 *	Creation date:  27-04-2009
 *	Last modification: 22-05-2009 by Maria Alejandra Trujillo Valencia
*/


var list_id = EzWebAPI.createRGadgetVariable("list_id", listIdHandler);
var archive_id = EzWebAPI.createRGadgetVariable("archive_id", archiveIdHandler);
var message_id = EzWebAPI.createRWGadgetVariable("message_id");

var last_show = 0;
var list_name = 0;
var url_head = "http://ezwebdev.eurodyn.com/repository/MailingGadgets/Services";


function listIdHandler(value)
{
}

function archiveIdHandler(value)
{
    var archive_body = document.getElementById('archive_body');
    archive_body.innerHTML = value;
    
    var archive_info = document.getElementById('archive_info');
    archive_info.innerHTML = 'Info:';
    
    var archive_info_body = document.getElementById('archive_info_body');
    archive_info_body.innerHTML = "Loading information....";

    EzWebAPI.send_get(url_head+"clientMailmanMessages.php?type=1&list_id="+list_id.get()+"&archive_id="+archive_id.get(), this, successInfoHandler, errorInfoHandler);

    var messages = document.getElementById('messages');
    messages.innerHTML =  'Messages:';
    
    var messages_body = document.getElementById('messages_body');
    messages_body.innerHTML = "Loading messages....";

    EzWebAPI.send_get(url_head+"clientMailmanMessages.php?type=2&list_id="+list_id.get()+"&archive_id="+archive_id.get(), this, successMessagesHandler, errorMessagesHandler);
}

function successInfoHandler (response)
{
    var myObject = eval('(' + response + ')');
	
    var archive_info_body = document.getElementById('archive_info_body');
    archive_info_body.innerHTML = '';
    archive_info_body.innerHTML += 'First message date: '+myObject.starting_date+'<br>';
    archive_info_body.innerHTML += 'Last message date: '+myObject.ending_date+'<br>';
    archive_info_body.innerHTML += 'Number of messages: '+myObject.n_messages+'<br>';
}

function errorInfoHandler (response)
{
    var archive_info_body = document.getElementById('archive_info_body');
    archive_info_body.innerHTML = "Archive information can't be loaded";
}

function successMessagesHandler (response)
{
    var messages_body = document.getElementById('messages_body');
    messages_body.innerHTML = '';
    
    if (response == null)
    {
       messages_body.innerHTML = "No messages have been found for the selected archieve";
    }
    var myObject = eval('(' + response + ')');
   
    var messages = myObject.messages;
    for(var i=0; i<messages.length; i++)
    {
         var subject = messages[i].subject;
         var id = messages[i].id;
         var author = messages[i].author;
         writeMessage(subject ,id , author);
    }
}

function errorMessagesHandler (response)
{
    messages = 0;
    $("messages_body").innerHTML = "Messages can't be loaded";
}

function writeMessage(subject,id, author)
{
    var messages_body = document.getElementById('messages_body');
    messages_body .innerHTML += "<div id='message_body_reply_"+id+"' class='message_reply'>";
    var message_body_reply = document.getElementById("message_body_reply_"+id);
    message_body_reply.innerHTML += "<div id='message_title_"+subject+"'>";
    message_body_reply.innerHTML += "<a href=\"javascript:setMessage('"+id+"');\"> "+subject+"</a> ";
    message_body_reply.innerHTML += author;
    message_body_reply.innerHTML += "</div>";
    message_body_reply.innerHTML += "</div>";

}

function setMessage(message_value)
{
    message_id.set(message_value);
}
