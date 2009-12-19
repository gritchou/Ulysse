/**
 *	File: mailmanList.js
 *	Author: Maria Alejandra Trujillo Valencia
 *	E-Mail: alejandra.trujillo@atosorigin.com
 *	Creation date: 16-04-2009
 *	Last modification: 22-05-2009 by Maria Alejandra Trujillo Valencia
*/

/************************ FUNCIONES ************************/

var project_id = EzWebAPI.createRGadgetVariable("project_id", projectUpdate);
var list_id = EzWebAPI.createRWGadgetVariable("list_id");
var archive_id = EzWebAPI.createRWGadgetVariable("archive_id");
var last_show = 0;
var list_name = 0;
var url_head = "http://ezwebdev.eurodyn.com/repository/MailingGadgets/Services";

var MONTHS = ['', 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

/** funcion encargada de la consulta ajax
*
* @return void
* @param int numMiembro indica el numero de identificacion de la empresa a consultar
* @param int id indica el id del tr que se desea desplegar

*/
function projectUpdate(value)
{
    var project_body = document.getElementById('project_body');
    project_body.innerHTML = value;
    var lists_body = document.getElementById('lists_body');
    lists_body.innerHTML = "";
    last_show = 0;
    EzWebAPI.send_get(url_head+'clientMailmanLists.php', this, successListsHandler, errorListsHandler);
}

function successListsHandler(response)
{
     project = project_id.get();
     if (project != null)
     {
          var project_body = document.getElementById('project_body');
          project_body.innerHTML = "";
          var lists_body = document.getElementById('lists_body');
          lists_body.innerHTML = "";
          
		  var myObject = eval('(' + response + ')');
		  var lists = myObject.lists;
		  
          for(var i=0; i<lists.length; i++)
          {
               var name = lists[i].name;
               var desc = lists[i].description;
               write_list(name ,desc);
          }
     }
}

function errorListsHandler(response)
{
    var lists_body = document.getElementById('lists_body');
    lists_body.innerHTML = "Lists associated to project can't be loaded. Reason:" + response.statusText;
}

function write_list(name)
{
     var lists_body = document.getElementById('lists_body');
     lists_body.innerHTML += "<div id='list_title_"+name+"'><a href=\"javascript:write_archives('"+name+"');\">"+name+"</a> ( <a href=\"javascript:setList('"+name+"');\">sent</a> )</div>";
     lists_body.innerHTML += "<div id='list_desc_"+name+"'>"+desc;
     lists_body.innerHTML += "<div id='archives_"+name+"' Style='display=none'></div>";
     lists_body.innerHTML += "</div>";
}

function write_archives(id)
{
    EzWebAPI.send_get(url_head+'clientMailmanLists.php?'+id, this, successArchivesHandler, errorArchivesHandler);
    list_name = id;
    var archive_name = document.getElementById("archives_"+list_name);
    archive_name.innerHTML = "Loading archives....";
    toggle(list_name);
}

function successArchivesHandler(response)
{
     var myObject = eval('(' + response + ')');
	 var archives = myObject.archives;
	 var archive_name = document.getElementById("archives_"+id);
     archive_name.innerHTML = '';
     if (archives == null)
    {
       archive_name.innerHTML = "No archives have been found for this list";
    }
    	
    for(var i=0; i<archives.length; i++)
    {
         var mime = archives[i].mime;
         var name = archives[i].name;
         writeArchive(id, mime,name);
    }
}

function errorArchivesHandler(response)
{
    var archive_name = document.getElementById("archives_"+list_name);
    archive_name.innerHTML = "Archieves can't be loaded";

}

function writeArchive(list_name, mime, name)
{
  var div_id = document.getElementById("archives_"+list_name);
  div_id.innerHTML += "<div id='archive"+name+"'>";
  div_id.innerHTML += "<a href=\"javascript:setArchive('"+list_name+"', '"+name+"');\"> "+name+"</a>  ("+mime+")";
  div_id.innerHTML += "</div>";
}

function toggle(list_name)
{
    var div1 = document.getElementById("list_desc_"+list_name);
    var div2 = document.getElementById("archives_"+list_name);
    if (div1.style.display == "none" || div1.style.display =="")
    {
         if (last_show != 0 )
        {
            hide("list_desc_"+last_show);
            hide("archives_"+last_show);
        }
        show(div1.id);
        show(div2.id);
        last_show = list_name;

    }
    else
    {
        hide(div1.id);
        hide(div2.id);
    }
}

function setList(value)
{
    list_id.set(value.toLowerCase());
}

function setArchive(list_value, archive_value)
{
    list_id.set(list_value.toLowerCase());
    archive_id.set(alpha2num(archive_value));
}

function alpha2num(archive_alpha)
{
    splitted = archive_alpha.split(' ');
    num = month2num(splitted[0]);
    if (num > 12)
        num = num -12;
    if (num < 10)
        num = "0"+num;
    archive_num =  splitted[1]+'-'+num;
    return archive_num;
}

function month2num(month)
{
     return MONTHS.indexOf(month);
}

function show(id)
{
    document.getElementById(id).style.display = "block";
}

function hide(id)
{
    document.getElementById(id).style.display = "none";
}

