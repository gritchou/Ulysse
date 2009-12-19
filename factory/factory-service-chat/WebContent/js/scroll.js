/*
fleXcroll v1.6.1 Public Version
This license text has to stay intact at all times:
Cross Browser Custom Scroll Bar Script by Hesido.
Public version - Free for non-commercial uses.

This script cannot be used in any commercially built
web sites, or in sites that relates to commercial
activities. 

Derivative works are only allowed for personal uses,
and they cannot be redistributed.
For licensing options:
Contact Emrah BASKAYA @ www.hesido.com

FleXcroll Public Key Code: 20050907122003339
MD5 hash for this license: 9ada3be4d7496200ab2665160807745d

End of license text---
*/
function CSBfleXcroll(targetId){
if(!document.getElementById||document.getElementById(targetId)==null||!document.createElement||navigator.userAgent.indexOf('Safari')!=-1||navigator.vendor=='KDE')return;
var dDiv=document.getElementById(targetId);
var cDiv=createDiv('contentwrapper',true);var mDiv=createDiv('mcontentwrapper',true);
var tDiv=createDiv('scrollwrapper',true);var pDiv=createDiv('copyholder',true);
pDiv.style.border='1px solid blue';
pDiv.style.visibility='hidden';
copyStyles(dDiv,pDiv,'0px',['border-left-width','border-right-width','border-top-width','border-bottom-width']);
var intlHeight=dDiv.offsetHeight,intlWidth=dDiv.offsetWidth,movedContent;
var oScrollY=(dDiv.scrollTop)?dDiv.scrollTop:0,oScrollX=(dDiv.scrollLeft)?dDiv.scrollLeft:0;
var kAct={_37:['-1s',0],_38:[0,'-1s'],_39:['1s',0],_40:[0,'1s'],_33:[0,'-1p'],_34:[0,'1p']};
dDiv.scrollPos=[0,0,0,0];dDiv.pageScroll=[0,0];dDiv.stepScroll=[0,0];
cDiv.containerSize=[0,0];cDiv.contentSize=[0,0];
copyStyles(dDiv,mDiv,'0px',['padding-left','padding-right','padding-top','padding-bottom']);

var postWidth=dDiv.offsetWidth,postHeight=dDiv.offsetHeight,heightLoss=intlHeight-postHeight,widthLoss=intlWidth-postWidth;
dDiv.style.width=intlWidth+'px';dDiv.style.height=intlHeight+'px';
tDiv.style.width=dDiv.offsetWidth+'px';tDiv.style.height=dDiv.offsetHeight+'px';
mDiv.style.width=postWidth+'px';mDiv.style.height=postHeight+'px';
tDiv.style.position='absolute';tDiv.style.top='0px';tDiv.style.left='0px';
tDiv.style.visibility='hidden';
while (dDiv.firstChild) {cDiv.appendChild(dDiv.firstChild)};
dDiv.appendChild(mDiv);dDiv.appendChild(pDiv);mDiv.appendChild(cDiv);
dDiv.appendChild(tDiv);
cDiv.style.position='relative';mDiv.style.position='relative';cDiv.style.padding='1px';
cDiv.style.width="100%";//fix IE7Beta2Bug
dDiv.style.overflow='hidden';mDiv.style.overflow='hidden';
mDiv.style.top="0";cDiv.style.top="0";
tDiv.hVspace=tDiv.vHspace=0;

cDiv.getContentWidth=function(){
	var cChilds=cDiv.childNodes,maxCWidth=compPad=0;
	for(var i=0;i<cChilds.length;i++){if(cChilds[i].offsetWidth){maxCWidth=Math.max(cChilds[i].offsetWidth,maxCWidth)}}
	cDiv.containerSize[0]=(cDiv.reqV)?dDiv.offsetWidth-tDiv.hVspace:dDiv.offsetWidth;
	cDiv.contentSize[0]=maxCWidth+widthLoss;
	return cDiv.contentSize[0];
	};

cDiv.getContentHeight=function(){
	cDiv.containerSize[1]=(cDiv.reqH)?dDiv.offsetHeight-tDiv.vHspace:dDiv.offsetHeight;
	cDiv.contentSize[1]=cDiv.offsetHeight+heightLoss;
	return cDiv.contentSize[1];
	};
	
cDiv.fixIEDispBug=function(){this.style.display='none';this.style.display='block'};

tDiv.createVScroll=function(){
if(!dDiv.vScroll){
	tDiv.vrt=new Array();var vrT=tDiv.vrt;
	createScrollBars(vrT,'vscroller');
	vrT.barPadding=[parseInt(getActiveStyle(vrT.sBar,'padding-top')),parseInt(getActiveStyle(vrT.sBar,'padding-bottom'))];
	vrT.sBar.style.padding='0px';vrT.sBar.curPos=0;vrT.sBar.vertical=true;
	vrT.sBar.indx=1; cDiv.vBar=vrT.sBar;
	cDiv.getContentWidth();cDiv.getContentHeight();
	prepareScroll(vrT,tDiv.vHspace);tDiv.hVspace=vrT.sDiv.offsetWidth;
	mDiv.style.width=postWidth-tDiv.hVspace+'px';
	cDiv.getContentWidth();cDiv.getContentHeight();
	prepareScroll(vrT,tDiv.vHspace);
	cDiv.fixIEDispBug();
	return true;
	}
};

tDiv.createHScroll=function(){
if(!dDiv.hScroll){
	tDiv.hrz=new Array();var hrZ=tDiv.hrz;
	createScrollBars(hrZ,'hscroller');
	hrZ.barPadding=[parseInt(getActiveStyle(hrZ.sBar,'padding-left')),parseInt(getActiveStyle(hrZ.sBar,'padding-right'))];
	hrZ.sBar.style.padding='0px';hrZ.sBar.curPos=0;hrZ.sBar.vertical=false;
	hrZ.sBar.indx=0; cDiv.hBar=hrZ.sBar;
	if(window.opera) hrZ.sBar.style.position='relative';
	cDiv.getContentWidth();cDiv.getContentHeight();
	prepareScroll(hrZ,(dDiv.vScroll)?tDiv.hVspace:0);
	tDiv.vHspace=hrZ.sDiv.offsetHeight;
	mDiv.style.height=postHeight-tDiv.vHspace+'px';
	hrZ.jBox=createDiv('scrollerjogbox');
	hrZ.jBox.style.display='none';hrZ.jBox.prnt=tDiv;
	tDiv.appendChild(hrZ.jBox);
	hrZ.jBox.onmousedown=function(){
		hrZ.sBar.scrollBoth=true;document.goScroll=hrZ.sBar;hrZ.sBar.clicked=true;
		hrZ.sBar.moved=false;tDiv.vrt.sBar.moved=false;
		dDiv.scrollUpdate();
		addTrigger(document,'selectstart',retFalse);
		addTrigger(document,'mousemove',mMoveBar);
		addTrigger(document,'mouseup',mMouseUp);
		return false;
		};
	cDiv.fixIEDispBug();
	return true;
	};
};

document.goScroll=null;
tDiv.vScroll=tDiv.createVScroll();
tDiv.hScroll=tDiv.createHScroll();
if(!addCheckTrigger(dDiv,'mousewheel',mWheelProc)||!addCheckTrigger(dDiv,'DOMMouseScroll',mWheelProc)){dDiv.onmousewheel=mWheelProc;}
dDiv.setAttribute('tabIndex','0');

addTrigger(dDiv,'keydown',function(e){
	if(!e){var e=window.event;};var pK=e.keyCode;
	if(kAct['_'+pK]){dDiv.commitScroll(kAct['_'+pK][0],kAct['_'+pK][1],true);if(e.preventDefault) e.preventDefault();return false;}
	});
addTrigger(dDiv,'keypress',function(e){//make Opera Happy
	if(!e){var e=window.event;};var pK=e.keyCode;
	if(kAct['_'+pK]){e.preventDefault();return false;}
});

dDiv.scrollUpdate=function(){
cDiv.style.padding='1px';
cDiv.reqH=cDiv.getContentWidth()>cDiv.containerSize[0];
cDiv.reqV=cDiv.getContentHeight()>cDiv.containerSize[1];

if(tDiv.vScroll&&cDiv.reqV){
	tDiv.vrt.sDiv.style.display='block';
	mDiv.style.width=postWidth-tDiv.hVspace+'px';
	dDiv.vScroll=true;
	} else if(tDiv.vScroll){
	tDiv.vrt.sDiv.style.display='none';mDiv.style.width=postWidth+'px';dDiv.vScroll=false;
	tDiv.vrt.sBar.curPos=0;cDiv.style.top='0px';
	if(tDiv.hScroll)tDiv.hrz.jBox.style.display='none';
	}
if(tDiv.hScroll&&cDiv.reqH){
	tDiv.hrz.sDiv.style.display='block';
	mDiv.style.height=postHeight-tDiv.vHspace+'px';
	dDiv.hScroll=true;
	if(tDiv.vScroll) tDiv.hrz.jBox.style.display='block';
	} else if(tDiv.hScroll){
	tDiv.hrz.sDiv.style.display='none';mDiv.style.height=postHeight+'px';dDiv.hScroll=false;
	tDiv.hrz.sBar.curPos=0;cDiv.style.left='0px';
	}
if((!cDiv.reqH&&tDiv.hScroll)||(!cDiv.reqV&&tDiv.hScroll)) tDiv.hrz.jBox.style.display='none';
if(cDiv.reqV) updateScroll(tDiv.vrt,(cDiv.reqH)?tDiv.vHspace:0);
if(cDiv.reqH) updateScroll(tDiv.hrz,(cDiv.reqV)?tDiv.hVspace:0);
if(cDiv.reqH&&cDiv.reqV) updateScroll(tDiv.vrt,(cDiv.reqH)?tDiv.vHspace:0);
cDiv.style.padding='0px';
};


dDiv.commitScroll=function(xScr,yScr,relative){
	var reT=[false,false],Bar;
	if(xScr&&dDiv.hScroll){xScr=calcScrollVal(xScr,0);Bar=tDiv.hrz.sBar;Bar.curPos=(relative)?Bar.curPos+xScr:xScr;Bar.doScrollPos();reT[0]=[Bar.curPos,Bar.sRange];}
	if(yScr&&dDiv.vScroll){yScr=calcScrollVal(yScr,1);Bar=tDiv.vrt.sBar;Bar.curPos=(relative)?Bar.curPos+yScr:yScr;Bar.doScrollPos();reT[1]=[Bar.curPos,Bar.sRange];}
	return reT;
};

dDiv.contentScroll=function(xPos,yPos,relative){
	var reT=[false,false],Bar;
	if(xPos&&dDiv.hScroll){Bar=tDiv.hrz.sBar;Bar.targetScroll=(relative)?Math.min(Math.max(Bar.mxScroll,Bar.targetScroll-xPos),0):-xPos;Bar.contentScrollPos();reT[0]=[Bar.targetScroll,Bar.mxScroll]}
	if(yPos&&dDiv.vScroll){Bar=tDiv.vrt.sBar;Bar.targetScroll=(relative)?Math.min(Math.max(Bar.mxScroll,Bar.targetScroll-yPos),0):-yPos;Bar.contentScrollPos();reT[1]=[Bar.targetScroll,Bar.mxScroll]}
	return reT;
}

copyStyles(pDiv,dDiv,'0px',['border-left-width','border-right-width','border-top-width','border-bottom-width']);

cDiv.style.padding='0px';dDiv.removeChild(pDiv);dDiv.fleXcroll=true;
dDiv.scrollUpdate();
dDiv.contentScroll(oScrollX,oScrollY,true);
//dDiv.scrollUpdate();
tDiv.style.visibility='visible';


function calcScrollVal(v,i){
var stR=v.toString(); v=parseInt(stR);
return (stR.match(/p$/))?v*dDiv.pageScroll[i]:(stR.match(/s$/))?v*dDiv.stepScroll[i]:v;
}

function camelConv(spL){
var spL=spL.split('-'),reT=spL[0],i;
for(i=1;parT=spL[i];i++) {reT +=parT.charAt(0).toUpperCase()+parT.substr(1);}
return reT;
}

function getActiveStyle(elem,style){
if(window.getComputedStyle) return window.getComputedStyle(elem,null).getPropertyValue(style);
if(elem.currentStyle) return elem.currentStyle[camelConv(style)];
return '';
};
function copyStyles(src,dest,replaceStr,sList){
var camelList = new Array();
for (var i=0;i<sList.length;i++){
	camelList[i]=camelConv(sList[i]);
	dest.style[camelList[i]] = getActiveStyle(src,sList[i],camelList[i]);
	if(replaceStr) src.style[camelList[i]] = replaceStr;
}
};
function createDiv(typeName,noGenericClass){
var nDiv=document.createElement('div');
nDiv.id=targetId+'_'+typeName;
nDiv.className=(noGenericClass)?typeName:typeName+' scrollgeneric';
nDiv.getSize = [function(){return nDiv.offsetWidth},function(){return nDiv.offsetHeight}]
nDiv.setSize = [function(sVal){nDiv.style.width=sVal},function(sVal){nDiv.style.height=sVal}]
nDiv.setPos = [function(sVal){nDiv.style.left=sVal},function(sVal){nDiv.style.top=sVal}]
return nDiv;
};
function createScrollBars(ary,bse){
ary.sDiv=createDiv(bse+'base');ary.sFDiv=createDiv(bse+'basebeg');
ary.sSDiv=createDiv(bse+'baseend');ary.sBar=createDiv(bse+'bar');
ary.sFBar=createDiv(bse+'barbeg');ary.sSBar=createDiv(bse+'barend');
tDiv.appendChild(ary.sDiv);ary.sDiv.appendChild(ary.sBar);
ary.sDiv.appendChild(ary.sFDiv);ary.sDiv.appendChild(ary.sSDiv);
ary.sBar.appendChild(ary.sFBar);ary.sBar.appendChild(ary.sSBar);
};
function prepareScroll(bAr,reqSpace){
var sDiv=bAr.sDiv,sBar=bAr.sBar,i=sBar.indx;
sBar.minPos=bAr.barPadding[0];
sBar.ofstParent=sDiv;
sBar.mDiv=mDiv;
sBar.scrlTrgt=cDiv;
sBar.targetSkew=0
updateScroll(bAr,reqSpace,true);

sBar.doScrollPos=function(){
sBar.curPos=(Math.min(Math.max(sBar.curPos,0),sBar.maxPos));
sBar.targetScroll=parseInt((sBar.curPos/sBar.sRange)*sBar.mxScroll);
sBar.targetSkew=(sBar.curPos==0)?0:(sBar.curPos==sBar.maxPos)?0:sBar.targetSkew;
sBar.setPos[i](sBar.curPos+sBar.minPos+"px");
cDiv.setPos[i](sBar.targetScroll+sBar.targetSkew+"px");
dDiv.scrollPos[i]=sBar.curPos;dDiv.scrollPos[i+2]=sBar.targetScroll;
};

sBar.contentScrollPos=function(){
sBar.curPos=parseInt((sBar.targetScroll*sBar.sRange)/sBar.mxScroll);
sBar.targetSkew=sBar.targetScroll-parseInt((sBar.curPos/sBar.sRange)*sBar.mxScroll);
sBar.curPos=(Math.min(Math.max(sBar.curPos,0),sBar.maxPos));
sBar.setPos[i](sBar.curPos+sBar.minPos+"px");
sBar.setPos[i](sBar.curPos+sBar.minPos+"px");
cDiv.setPos[i](sBar.targetScroll+"px");
}

mDiv.style.zIndex=getActiveStyle(sBar,'z-index');
sBar.onmousedown=function(){
	this.clicked=true;document.goScroll=this;this.scrollBoth=false;this.moved=false;
	dDiv.scrollUpdate();
	addTrigger(document,'selectstart',retFalse);
	addTrigger(document,'mousemove',mMoveBar);
	addTrigger(document,'mouseup',mMouseUp);
	return false;
	};

sDiv.onclick=function(e){
if(!e){var e=window.event;}
if(e.target&&(e.target==bAr.sFBar||e.target==bAr.sSBar)) return;
if(e.srcElement&&(e.srcElement==bAr.sFBar||e.srcElement==bAr.sSBar)) return;
var relPos;
var xScrolled=(window.pageXOffset)?window.pageXOffset:(document.documentElement&&document.documentElement.scrollLeft)?document.documentElement.scrollLeft:0;
var yScrolled=(window.pageYOffset)?window.pageYOffset:(document.documentElement&&document.documentElement.scrollTop)?document.documentElement.scrollTop:0;
sBar.mDiv.scrollTop=sBar.mDiv.scrollLeft=0;
CSBFindPos(sBar);
relPos=(sBar.vertical)?e.clientY+yScrolled-sBar.yPos:e.clientX+xScrolled-sBar.xPos;
sBar.curPos=(relPos>0)?sBar.curPos+sBar.pageScroll:sBar.curPos-sBar.pageScroll;
sBar.doScrollPos();
return false;
}
sDiv.onmousedown = retFalse;
};

function updateScroll(bAr,reqSpace,firstRun){
var sDiv=bAr.sDiv,sBar=bAr.sBar,sFDiv=bAr.sFDiv,sFBar=bAr.sFBar,sSDiv=bAr.sSDiv,sSBar=bAr.sSBar,i=sBar.indx;
	sDiv.setSize[i](tDiv.getSize[i]()-reqSpace+'px');sDiv.setPos[1-i](tDiv.getSize[1-i]()-sDiv.getSize[1-i]()+'px');
	sBar.aSize=Math.max(Math.min(parseInt(cDiv.containerSize[i]/cDiv.contentSize[i]*sDiv.getSize[i]()),parseInt(sDiv.getSize[i]()*0.85)),45);
	sBar.setSize[i](sBar.aSize+'px');sBar.maxPos=sDiv.getSize[i]()-sBar.getSize[i]()-bAr.barPadding[0]-bAr.barPadding[1];
	sBar.curPos=Math.min(Math.max(0,sBar.curPos),sBar.maxPos);
	sBar.setPos[i](sBar.curPos+sBar.minPos+'px');sBar.mxScroll=mDiv.getSize[i]()-cDiv.contentSize[i];
	sBar.sRange=sBar.maxPos;
	dDiv.pageScroll[i]=sBar.pageScroll=parseInt(sBar.getSize[i]()*0.96);
	dDiv.stepScroll[i]=sBar.stepScroll=Math.min(Math.abs(parseInt((sBar.sRange/sBar.mxScroll)*80)),parseInt(sBar.sRange*0.25));
	sFDiv.setSize[i](sDiv.getSize[i]()-sSDiv.getSize[i]()+'px');
	sFBar.setSize[i](sBar.getSize[i]()-sSBar.getSize[i]()+'px');
	sSBar.setPos[i](sBar.getSize[i]()-sSBar.getSize[i]()+'px');
	sSDiv.setPos[i](sFDiv.getSize[i]()+'px');
	if(!firstRun) sBar.doScrollPos();
	cDiv.fixIEDispBug();
};

addTrigger(window,'load',function(){if(dDiv.fleXcroll) dDiv.scrollUpdate();});
addTrigger(window,'resize',function(){
if(dDiv.refreshTimeout) window.clearTimeout(dDiv.refreshTimeout);
dDiv.refreshTimeout=window.setTimeout(function(){if(dDiv.fleXcroll) dDiv.scrollUpdate();},80);
});

function retFalse(){return false;};
function mMoveBar(e){
if(!e){var e=window.event;}
var FCBar=document.goScroll,moveBar,maxx,xScroll,yScroll;
if(FCBar==null) return;
maxx=(FCBar.scrollBoth)?2:1;
for (var i=0;i<maxx;i++){
	moveBar=(i==1)?FCBar.scrlTrgt.vBar:FCBar;
	if(FCBar.clicked){
		if(!moveBar.moved){
		moveBar.mDiv.scrollTop=0;moveBar.mDiv.scrollLeft=0;
		CSBFindPos(moveBar);CSBFindPos(moveBar.ofstParent);moveBar.pointerOffsetY=e.clientY-moveBar.yPos;
		moveBar.pointerOffsetX=e.clientX-moveBar.xPos;moveBar.inCurPos=moveBar.curPos;moveBar.moved=true;
		}
		moveBar.curPos=(moveBar.vertical)?e.clientY-moveBar.pointerOffsetY-moveBar.ofstParent.yPos-moveBar.minPos:e.clientX-moveBar.pointerOffsetX-moveBar.ofstParent.xPos-moveBar.minPos;
		if(FCBar.scrollBoth) moveBar.curPos=moveBar.curPos+(moveBar.curPos-moveBar.inCurPos);
		moveBar.doScrollPos();
		} else moveBar.moved=false;
	}
};

function mMouseUp(){
if(document.goScroll!=null){document.goScroll.clicked=false;}
document.goScroll=null;
removeTrigger(document,'selectstart',retFalse);
removeTrigger(document,'mousemove',mMoveBar);
removeTrigger(document,'mouseup',mMouseUp);
};

function mWheelProc(e){
if(!e) e=window.event;
if(!this.fleXcroll) return;
var scrDv=this,vEdge,hEdge,hoverH=false,delta=0;
hElem=(e.target)?e.target:(e.srcElement)?e.srcElement:this;
if(hElem.id&&hElem.id.match(/_hscroller/)) hoverH=true;
if(e.wheelDelta) delta=-e.wheelDelta;if(e.detail) delta=e.detail;delta=delta<0?-1:+1;
if(scrDv.vScroll&&!hoverH) scrollState=scrDv.commitScroll(false,delta*scrDv.stepScroll[1],true);
vEdge=!scrDv.vScroll||hoverH||(scrDv.vScroll&&((scrollState[1][0]==scrollState[1][1]&&delta>0)||(scrollState[1][0]==0&&delta<0)));
if(scrDv.hScroll&&(!scrDv.vScroll||hoverH)) scrollState=scrDv.commitScroll(delta*scrDv.stepScroll[0],false,true);
hEdge=!scrDv.hScroll||(scrDv.hScroll&&scrDv.vScroll&&vEdge&&!hoverH)||(scrDv.hScroll&&((scrollState[0][0]==scrollState[0][1]&&delta>0)||(scrollState[0][0]==0&&delta<0)));
if(vEdge&&hEdge&&!hoverH) {if(window.opera) {window.scrollBy(0, e.wheelDelta);if(e.preventDefault) e.preventDefault();return false} //keep O9 happy;
	return;}
if(e.preventDefault) e.preventDefault();
return false;
};

function addTrigger(elm,eventname,func){if(!addCheckTrigger(elm,eventname,func)&&elm.attachEvent) {elm.attachEvent('on'+eventname,func);}};
function addCheckTrigger(elm,eventname,func){if(elm.addEventListener){elm.addEventListener(eventname,func,false);window.addEventListener("unload",function(){removeTrigger(elm,eventname,func)},false);return true;} else return false;};
function removeTrigger(elm,eventname,func){if(!removeCheckTrigger(elm,eventname,func)&&elm.detachEvent) elm.detachEvent('on'+eventname,func);};
function removeCheckTrigger(elm,eventname,func){if(elm.removeEventListener){elm.removeEventListener(eventname,func,false);return true;} else return false;};

function CSBFindPos(elem){ 
//function modified from firetree.net
var obj=elem,curleft=curtop=0;
if(obj.offsetParent){while(obj){curleft+=obj.offsetLeft;curtop+=obj.offsetTop;obj=obj.offsetParent;}}
else if(obj.x){curleft+=obj.x;curtop+=obj.y;}
elem.xPos=curleft;elem.yPos=curtop;
};

};



