
function cjAjaxEngine(uri,handlerFunction,errorFunction)
{
  if (handlerFunction==null)  handlerFunction = function() {};
  if (errorFunction==null)    errorFunction = function () {};

  var r = (window.ActiveXObject)?new ActiveXObject("Microsoft.XMLHTTP"):new XMLHttpRequest();
  if (r)
  {
    r.onreadystatechange = function()
    {
	if (r.readyState == 4)
         if (r.status == 200)
  	 {
	   xmlDoc = r.responseXML;				
           txt = r.responseText;
           handlerFunction(txt, xmlDoc);
	 }
         else
          errorFunction();
    }
    r.open("GET", uri);
    if (window.XMLHttpRequest) r.send(null);
    else                       r.send();
    return true;
  }
  else
  {
    errorFunction();
    return false;
  }
}

function cjAjaxEngineGet(uri,handlerFunction,errorFunction)
{
  return cjAjaxEngine(uri,handlerFunction,errorFunction);
}

function cjAjaxEnginePost(uri, query, handlerFunction, errorFunction)
{
    var contentType = "application/x-www-form-urlencoded; charset=UTF-8";

    if (handlerFunction==null)  handlerFunction = function() {};
    if (errorFunction==null)    errorFunction = function () {};
    
    if (query==null) query="";

    var r = (window.ActiveXObject)?new ActiveXObject("Microsoft.XMLHTTP"):new XMLHttpRequest();
    if (r)
    {
      r.onreadystatechange = function()
      {
	if (r.readyState == 4)
         if (r.status == 200)
  	 {
	   xmlDoc = r.responseXML;				
           txt = r.responseText;
           handlerFunction(txt, xmlDoc);
	 }
         else
          errorFunction();
      }
     r.open("POST", uri, true);
     r.setRequestHeader("Content-Type", contentType);
     r.send(query);
     return true;
  }
  else
  {
    errorFunction();
    return false;
  }

}


function loadScript(uri)
{
  var scr = document.createElement('script'); 
  scr.setAttribute('type', 'text/javascript');
  scr.setAttribute('language','JavaScript');
  scr.setAttribute('src',uri);
  document.body.appendChild(scr); 
}

function createScript(s)
{
  var scr = document.createElement('script'); 
  scr.setAttribute('type', 'text/javascript');
  scr.setAttribute('language','JavaScript');
  scr.text=s;
  document.body.appendChild(scr); 
}

function alertHandler(txt, xmlDoc) 
{ 
  s=txt; step=1;
  while (s.length>0 && step==1)
  {
    if (s.substring(0,1)==' ') s=s.substring(1);
    else                       step=0;
  }

  if (s!='') alert(s);
} 

function emptyHandler(txt, xmlDoc) 
{ 
  
} 

function bodyHandler(txt, xmlDoc) 
{ 
  document.body.innerHTML=txt;
} 

function trimResponse(s0)
{
  if (s0==null) return s0;
  s=s0;

  while (s.length>0)
  {
    s1=s.substring(0,1);
    if (s1==' ' || s1=='\r' || s1=='\n' || s1=='\t') s=s.substring(1);
    else  return s;
  }

  return '';
}

function getPositionX(o) { var x=0; if(document.layers) x=o.pageX; else { while(eval(o)) { x+=o.offsetLeft; o=o.offsetParent; } } return x; };
function getPositionY(o) { var y=0; if(document.layers) y=o.pageY; else { while(eval(o)) { y+=o.offsetTop; o=o.offsetParent; } } return y; };

function Browser() {

  this.isIE=true;
  this.isNS=false;

  ua = navigator.userAgent;

  if (ua.indexOf("MSIE")>=0) {
    this.isIE = true;
    return;
  }

  if (ua.indexOf("Netscape")>=0)
  {
    this.isNS=true;
    this.isIE=false;
    return;
  }

  if (ua.indexOf("Mozilla")>=0) {
    this.isNS = true;
    this.isIE=false;
    return;
  }

  // Treat any other "Opera" browser as NS 6.1.
  if (ua.indexOf("Opera")>= 0) {
    this.isNS=true;
    this.isIE=false;
    return;
  }
}

var browser = new Browser();

var draggedObject = new Object();
draggedObject.zIndex = 0;

function startDrag(event, id) 
 {
  draggedObject.elem = document.getElementById(id);

  if (browser.isIE) {
    x = window.event.clientX + document.documentElement.scrollLeft
      + document.body.scrollLeft;
    y = window.event.clientY + document.documentElement.scrollTop
      + document.body.scrollTop;
  }
  else
   if (browser.isNS) {
     x = event.clientX + window.scrollX;
     y = event.clientY + window.scrollY;
  }

  draggedObject.cursorStartX = x;
  draggedObject.cursorStartY = y;
  draggedObject.elemStartLeft  = parseInt(draggedObject.elem.style.left, 10);
  draggedObject.elemStartTop   = parseInt(draggedObject.elem.style.top,  10);

  if (isNaN(draggedObject.elemStartLeft)) draggedObject.elemStartLeft = 0;
  if (isNaN(draggedObject.elemStartTop))  draggedObject.elemStartTop  = 0;

  draggedObject.elem.style.zIndex = ++draggedObject.zIndex;

  if (browser.isIE) {
    document.attachEvent("onmousemove", proceedDrag);
    document.attachEvent("onmouseup",   stopDrag);
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  }
  else
   if (browser.isNS) {
     document.addEventListener("mousemove", proceedDrag,   true);
     document.addEventListener("mouseup",   stopDrag, true);
     event.preventDefault();
   }
}

function proceedDrag(event) 
 {

  if (browser.isIE) {
    x = window.event.clientX + document.documentElement.scrollLeft
      + document.body.scrollLeft;
    y = window.event.clientY + document.documentElement.scrollTop
      + document.body.scrollTop;
  }
  else
  if (browser.isNS) {
    x = event.clientX + window.scrollX;
    y = event.clientY + window.scrollY;
  }

  draggedObject.elem.style.left = (draggedObject.elemStartLeft + x - draggedObject.cursorStartX) + "px";
  draggedObject.elem.style.top  = (draggedObject.elemStartTop  + y - draggedObject.cursorStartY) + "px";

  if (browser.isIE) {
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  }
  else
   if (browser.isNS)
     event.preventDefault();
}

function stopDrag(event) 
 {

  if (document.detachEvent)
   {
    document.detachEvent("onmousemove", proceedDrag);
    document.detachEvent("onmouseup",   stopDrag);
  }
  else
   if (document.removeEventListener)
   {
    document.removeEventListener("mousemove", proceedDrag,   true);
    document.removeEventListener("mouseup",   stopDrag, true);
   }
}

